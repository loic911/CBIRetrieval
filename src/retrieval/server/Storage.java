package retrieval.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.dist.RequestPictureVisualWord;
import retrieval.exception.CBIRException;
import retrieval.multiserver.globaldatabase.GlobalDatabase;
import retrieval.multiserver.globaldatabase.KyotoCabinetDatabase;
import retrieval.server.exception.*;
import retrieval.server.index.Index;
import retrieval.server.index.IndexMultiThread;
import retrieval.server.index.NoValidPictureException;
import retrieval.server.index.PictureIndex;
import retrieval.server.index.ResultSim;
import retrieval.server.net.ServerFromCentralServerXML;
import retrieval.server.net.ServerFromIndexerInfoXML;
import retrieval.server.net.ServerFromIndexerPictureXML;
import retrieval.testvector.TestVectorListServer;
import retrieval.testvector.generator.TestVectorReading;
import retrieval.utils.FileUtils;

/**
 * This class implement a server which index pictures and response to
 * serach request from central server
 * @author Rollus Loic
 */
public final class Storage {

    private String idServer;
    private GlobalDatabase globalDatabase; //may be null if database is not shared between all subserver
    /**
     * Configuration object for server
     */
    private ConfigServer config;
    /**
     * Hight level Index (pictures information index, visual words index,...)
     */
    private Index index;
    /**
     * Indexer Thread
     */
    private StorageIndexThread threadIndex;
//    /**
//     * Communicator for a central server request (search)
//     */
//    private StorageNetworkInterface serverNetworkSearch;
//    /**
//     * Communicator for an indexer picture request
//     */
//    private StorageNetworkInterface serverNetworkPicture;
//    /**
//     * Communicator for an indexer request (which want information)
//     */
//    private StorageNetworkInterface serverNetworkInfo;
    /**
     * All thread request
     */
//    WaitRequestThread searchThread;
//    WaitRequestThread indexPictureThread;
//    WaitRequestThread infoThread;    
    /**
     * Last picture that was taken from queue but not already
     * mark as indexed (between waited queue and index process)
     */
    private Long currentIndexedId;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(Storage.class);

    /**
     * Constructor for a server
     * @param config Configuration object
     * @param configStore Configuration object for store
     * @param indexPath Path to index at the start of server
     * @throws InternalServerException Internal error during server start
     * @throws TestsVectorsNotFoundServerException Tests vectors not found
     * @throws StartIndexException Error during the start of index
     * @throws ReadIndexException Error during the read of index
     */
    public Storage(
            String idServer,
            ConfigServer config,
            GlobalDatabase globalDatabase)
            throws
            InternalServerException,
            TestsVectorsNotFoundServerException,
            StartIndexException,
            ReadIndexException,
            Exception{

        logger.info("Server: start");
        this.idServer = idServer;
        this.globalDatabase = globalDatabase;
        try {
        this.config = config;
        this.threadIndex = StorageIndexThread.getThreadIndex(this, config.getSizeOfIndexQueue());
        logger.info("Store name:" + config.getStoreName());
        logger.info("Server: read tests vectors in " + config.getVectorPath());
        
        TestVectorListServer testVectors = TestVectorReading.readServer(this.idServer,config.getVectorPath(),config,initDatabase());

        logger.info("Server: " + testVectors.size() + " tests vectors");

        logger.info("Server: get Picture Index");
        PictureIndex pi = PictureIndex.getPictureIndex(idServer,config,globalDatabase);
        index = new IndexMultiThread(idServer,config, testVectors, pi);
        logger.info("Purge size = " +index.getPurgeSize());
        index.sync();
        logger.info("There are " + index.getSize() + " images");
        if (logger.isDebugEnabled()){
            index.printStat();
        }
        } catch(Exception e) {
            logger.error(e);
            throw e;
        }
    }
    
    private Object initDatabase() {
        logger.info("Create multiple database");
        Object database = null;
        if(config.getStoreName().equals("KYOTOSINGLEFILE")) {
            //if database is shared with multiple server, return it
            database=this.globalDatabase; 
        } else if(config.getStoreName().equals("KYOTOMULTIPLEFILE")) {
            //if database is juste for this server, create it
                     //String file, String indexPath, String apox, String bnum, String cache, String funit
            database=KyotoCabinetDatabase.openDatabase(
                    "main.kch",
                    config.getIndexPath(),
                    config.getKyotoApox(),
                    config.getKyotoBNum(),
                    config.getKyotoCacheSizeForMainIndexWith1T(), 
                    config.getKyotoFUnit());
        }
        return database;
    } 

    
    /**
     * Constructor for a server
     * @param config Configuration object
     * @throws InternalServerException Internal error during server start
     * @throws TestsVectorsNotFoundServerException Tests vectors not found
     * @throws StartIndexException Error during the start of index
     * @throws ReadIndexException Error during the read of index
     */
    public Storage(String idServer,ConfigServer config) throws InternalServerException, TestsVectorsNotFoundServerException, StartIndexException, ReadIndexException,Exception {
        this(idServer,config,null);
    }    
    
//    /**
//     * Start server and initiailise all socket
//     * @throws InternalServerException Error during server start
//     */
//    public void start() throws InternalServerException {
//        this.start(true);
//    }
//
//    /**
//     * Start server without initialise socket (for direct access server)
//     * @throws InternalServerException Error during server start
//     */
//    public void startWithoutInitSocket() throws InternalServerException {
//        this.start(false);
//    }
  
    /**
     * Start server
     * @param initSocket Init socket for client/indexer or not
     * @throws InternalServerException Error during the server start
     */
    public void start() throws InternalServerException {
        logger.info("start: config.PORTSEARCH="+config.getPortSearch()+ " config.PORTINDEXPICTURE="+config.getPortIndexPicture() + " config.PORTINFO="+config.getPortInfo());
//
//        if (initSocket) {
//            //start all thread that will wait on a specific port
//            serverNetworkSearch = new ServerFromCentralServerXML(this, config.getPortSearch(), config.getSearchMax(), config.getkMax());
//            serverNetworkPicture = new ServerFromIndexerPictureXML(this, config.getPortIndexPicture());
//            serverNetworkInfo = new ServerFromIndexerInfoXML(this, config.getPortInfo());
//
//            searchThread = new WaitRequestThread(serverNetworkSearch);
//            searchThread.start();
//            indexPictureThread = new WaitRequestThread(serverNetworkPicture);
//            indexPictureThread.start();
//            infoThread = new WaitRequestThread(serverNetworkInfo);
//            infoThread.start();
//        }
        logger.info("start: launch index thread");
        this.threadIndex.start();
        logger.info("start: launch index thread OK");
    }
    
    /**
     * Stop server
     * @throws CloseIndexException Error during the index close
     */
    public void stop() throws CloseIndexException {
        try {index.close();}catch(Exception e){ logger.debug("stop index thread:"+e);}
//        try {serverNetworkSearch.close();}catch(Exception e){ logger.debug("stop serverNetworkSearch thread:"+e);}
//        try {serverNetworkPicture.close();}catch(Exception e){ logger.debug("stop serverNetworkPicture thread:"+e);}
//        try {serverNetworkInfo.close();}catch(Exception e){ logger.debug("stop serverNetworkInfo thread:"+e);}

//        logger.debug("stop search thread...");
//        try {searchThread.stop();}catch(Exception e) { logger.debug("stop search thread:"+e);}
//        logger.debug("stop index path thread...");
//        try {indexPictureThread.stop();}catch(Exception e) { logger.debug("stop index picture thread:"+e);}
//        logger.debug("stop info thread...");
//        try {infoThread.stop();}catch(Exception e) { logger.debug("stop info thread:"+e);}
//        logger.debug("stop threadindex thread...");
        try {this.threadIndex.stop();}catch(Exception e) { logger.debug("stop index thread:"+e);}
        logger.debug("all thread stopped...");
    }
    
    /**
     * Get the size of index
     * @return Size of index
     */
    public long getNumberOfItem() {
        return index.getSize();
    }
       
    
    /**
     * Index a picture directly in index (without waiting on queue)
     * THIS METHOD IS NOT SAFE IF YOU MAKE MULTIPLE INDEX
     * @param path Picture path
     * @param authorization Authorization to access picture
     * @return ID of picture
     * @throws PictureTooHomogeneous Picture is too homogeneous to be indexed (ex: only one color,...)
     * @throws Exception Error during indexing
     */
    public Long indexPicture(BufferedImage image, Long id, Map<String,String> properties) throws AlreadyIndexedException, NoValidPictureException, PictureTooHomogeneous {

        return index.addPicture(
                image,
                id,
                properties, 
                config.getNumberOfPatch(), 
                config.getResizeMethod(), 
                config.getSizeOfPatchResizeWidth(), 
                config.getSizeOfPatchResizeHeight(), 
                config.isSyncAfterImage());
    }
    
//    /**
//     * Index a pictures lists directly in index (without waiting on queue)
//     * THIS METHOD IS NOT SAFE IF YOU MAKE MULTIPLE INDEX
//     * @param lists List of pictures to index
//     * @param authorization Authorization to access picture
//     * @return Map with picture path and exception (NoException if OK)
//     */
//    public Map<String,CBIRException> indexPictures(List<String> lists,PictureAuthorization authorization)  {
//
//        logger.info("indexPictures: " + lists.size() + " to index");
//
//        Map<String, CBIRException> picturesState = new HashMap<String, CBIRException>();
//
//        for (int i = 0; i < lists.size(); i++) {
//
//            String path = lists.get(i);
//
//            if (index.isPictureAlreadyIndexed(path)) {
//                picturesState.put(path, new AlreadyIndexedException());
//            }
//            else if (!FileUtils.isValidResource(path)) {
//                picturesState.put(path, new PictureNotFoundException());
//            }
//            else {
//                //otherwise, index
//                try {
//                   indexPicture(path,authorization);
//                   picturesState.put(path, new NoException());
//                } catch(Exception e) {
//                    logger.error(e);
//                    picturesState.put(path, new InternalServerException(e.toString()));
//                    
//                }
//                
//            }
//        }
//        return picturesState;
//    }       
    
//    /**
//     * Index a path of pictures (admin use)
//     * @param indexPath Pictures path or a picture
//     * @param authorization Authorization to access pictures
//     * @return List of id of pictures
//     * @throws InternalServerException Error during indexing
//     */
//    public List<Integer> indexPicturePath(String indexPath, PictureAuthorization authorization) throws InternalServerException {
//        List<Integer> picturesId = null;
//        if (indexPath != null) {
//            //a index path was give as argument, so index it
//            List<String> lists = new ArrayList<String>();
//            //list all pictures
//            try {
//                File f = new File(indexPath);
//                boolean isURL = FileUtils.isValidURL(indexPath);
//                boolean isDirectory = f.isDirectory();
//                boolean isFile = f.isFile();
//                logger.debug(indexPath + " isURL= " + isURL + " | isDirectory= " + isDirectory + " | isFile= " + isFile);
//                //if directory, get all images (and images from subdirectory)
//                if (isURL || isFile) {
//                    lists.add(indexPath);
//                }
//                else {
//                    FileUtils.listFiles(f, lists);
//                }
//                picturesId = this.indexPictureFiles(lists,authorization);
//            } catch (Exception e) {
//                logger.error("Server:" + e);
//                throw new InternalServerException("path " + indexPath + " " + e.toString());
//            }
//        }
//        return picturesId;
//    }

//    /**
//     * Index a set of pictures
//     * @param lists List of pictures to index
//     * @param authorization Authorization to access pictures
//     * @return LIst of id of indexed pictures
//     * @throws InternalServerException Error during indexing
//     */
//    public List<Integer> indexPictureFiles(List<String> lists, PictureAuthorization authorization) throws InternalServerException {
//        List<Integer> picturesId = new ArrayList<Integer>(lists.size());
//        if (lists != null) {
//            //indexall pictures
//            logger.info("Server: index " + lists.size());
//            List<String> tooHomogenousPicture = new ArrayList<String>();
//            for (int i = 0; i < lists.size(); i++) {
//                try {
//                    Integer id = indexPicture(lists.get(i),authorization);
//                    picturesId.add(id);
//                } catch (PictureTooHomogeneous e) {
//                    tooHomogenousPicture.add(lists.get(i));
//                } catch (Exception e) {
//                    logger.error("index picture: " + lists.get(i) + " " + e);
//                }
//            }
//            logger.info("There are " + tooHomogenousPicture.size() + " too Homogenous Picture");
//            for (int i = 0; i < tooHomogenousPicture.size(); i++) {
//                logger.info(tooHomogenousPicture.get(i) + "; is too homogenous");
//            }
//        }
//        return picturesId;
//    }    
    
     /**
     * Add a single picture file to index queue
     * @param file Picture File
     * @return Indexed Picture file
     * @throws InvalidPictureException Picture was not valid
     * @throws TooMuchIndexRequestException Waited queue are full
     */
    public Long addToIndexQueue(BufferedImage image, Long id, Map<String,String> properties) throws InvalidPictureException, TooMuchIndexRequestException {
        logger.debug("addToIndexQueue="+id);
        threadIndex.addInIndexPicture(image,id,properties);
        return id;
    }  
    
    
    /**
     * Delete pictures in paths list from server
     * THIS METHOD DOES NOT REMOVED PICTURES DATA FROM INDEX, just removed pictures from result.
     * If this method is used many times, invoque purge method sometimes
     * @param paths Pictures to delete
     * @return Map with pictures path and exception (NoException if ok)
     * @throws InternalServerException Error during delete
     */
    public Map<Long, CBIRException> deletePictures(List<Long> ids) throws InternalServerException {
        logger.info("deletePicture:" + ids);

        try {
            Map<Long, CBIRException> mapResults = new HashMap<Long, CBIRException>();
            for (int i = 0; i < ids.size(); i++){
                mapResults.put(ids.get(i), new PictureNotFoundException());
            }
            logger.info("deletePictures: lists size = " + ids.size());
            index.deletePicture(ids);
            return mapResults;
        } catch (Exception e) {
            logger.error("deletePictures:" + e);
            throw new InternalServerException(e.toString());
        }
    }

    /**
     * Delete from index, all data from pictures that has been removed
     * VERY HEAVY because you need to browse all index entry
     */
    public void purgeIndex() {
        logger.info("Purge server!");
        index.purge(config);
    }
    
    /**
     * Get the number of deleted pictures that are still in index
     * @return Number of deleted pictures
     */
    public int getNumberOfPicturesToPurge() {
        return index.getPurgeSize();
    }
    
    /**
     * Check if picture id data still exist in index
     * VERY HEAVY because you need to browse all index entry
     * @param id Picture id
     * @return True if index doesn't contains data with id
     */
    public boolean isPictureCorrectlyRemovedFromIndex(Long id) {
        return !index.isPicturePresentInIndex(id);
    }
        
    /**
     * Delete all index on disc from this server
     * @throws Exception Error during indexing
     */
    public void deleteIndex() throws Exception {
        logger.info("Drop index path:" + this.getIndexPath() + "...");
        //TODO: don't remove index if it's a share index between many server! => index share between many server is not yet implemented (but it is not a perfect thing)
        if(globalDatabase==null) {
            FileUtils.deleteAllFilesRecursively(new File(this.getIndexPath()));
        } else {
            deletePictures(getAllPictures()); 
        }  
    }
    
    /**
     * Get the index path
     * @return 
     */
    public String getIndexPath() {
        return config.getIndexPath();
    }    

    /**
     * Print index and stats (not avalaible with all index type
     */
    public void printIndex() {
        index.printStat();
    }

    /**
     * Get a map with all pictures and their ID
     * @return Pictures indexed (path) and their id
     */
    public Map<Long, Map<String,String>> getAllPicturesMap() {
        return index.getAllPicturesMap();
    }
    
    /**
     * Get a list with all pictures indexed
     * @return Pictures indexed path
     */
    public List<Long> getAllPictures() {
        return index.getAllPicturesList();
    }

    /**
     * Get the state of each picture file (from structure files)
     * @param files Picture files
     * @return Pictures files and their state
     */
    public Map<Long, CBIRException> getInfo(Map<Long, CBIRException> files) {

        Map<Long, CBIRException> map = new HashMap<Long, CBIRException>(files.size() * 2);

        for (Map.Entry<Long, CBIRException> entry : files.entrySet()) {
            Long id = entry.getKey();
            if (index.isPictureAlreadyIndexed(id)) {
                //Picture is in index
                map.put(id, new NoException());
            } else if (threadIndex.isInIndexQueue(id)) {
                //Picture is in index queue
                map.put(id, new PictureInIndexQueueException());
            } else if (currentIndexedId!=null && currentIndexedId.equals(id)) {
                //Picture is in index process (not in queue, not in index)
                //Will be index very soon...
                map.put(id, new PictureInIndexQueueException());
            } else {
                //Picture not found in index, error during insert
                map.put(id, new PictureNotFoundException());
            }
        }
        return map;
    }


    /**
     * Check if a picture with this id is indexed 
     * @param id Picture id
     * @return True if picture is indexed, othrerwise false
     */
    public boolean isPictureInIndex(Long id) {
        return index.isPictureAlreadyIndexed(id);
    }
    
    /**
     * Check if index queue is empty (Pictures are still waiting to be indexed)
     * @return True if index queue is empty, othewise false
     */
    public boolean isIndexQueueEmpty() {
       return threadIndex.isIndexQueueEmpty();
    }
    public int getIndexQueueSize() {
       return threadIndex.getIndexQueueSize();
    }    

    /**
     * Fill NBT (number of patchs map with visual word B in index) for each
     * visual words from visualwords structures
     * FIRST PART OF SEARCH WITH CENTRAL SERVER
     * @param visualWords Tests vectors lists and their visual words
     * @return Visual words and their NBT
     * @throws WrongNumberOfTestsVectorsException Central server has a different
     * number of tests vectors than server.
     */
    public List<ConcurrentHashMap<String, Long>> getNBT(List<ConcurrentHashMap<String, Long>> visualWords) throws WrongNumberOfTestsVectorsException {
        int T = index.getNumberOfTestsVectors();
        if (T != visualWords.size()){
            throw new WrongNumberOfTestsVectorsException("Server has " + T + " tests vectors (T), central server has " + visualWords.size() + " tests vectors (T)");
        }
        return index.fillNBT(visualWords);
    }

    /**
     * Get (maximum) k most similar pictures from server with vw and niq information
     * SECOND PART OF SEARCH WITH CENTRAL SERVER
     * @param vw Visual words from Request image Iq
     * @param Niq Number of patchs extract from Iq
     * @param k Max similar pictures
     * @return Most similar pictures
     */
    public List<ResultSim> getPicturesSimilarities(List<ConcurrentHashMap<String, RequestPictureVisualWord>> vw, int Niq, int k) {
        List<ResultSim> allResults = index.computeSimilarity(vw, Niq);
        List<ResultSim> firstResults = new ArrayList<ResultSim>(k);
        if(index.getSize()<1) {
            return firstResults;
        }
        
        for (int i = 0; i < allResults.size() && i < k; i++) {
            ResultSim sim = allResults.get(i);
            sim.setId(sim.getId());
            firstResults.add(sim);
        }
        return firstResults;
    }
    
    /**
     * Change current indexed Picture
     * Rem: If picture is not yet indexed and not in queue, we can say that it
     * is in index process
     * @param currentIndexedPicture Current indexed Picture
     */
    public void setCurrentIndexedPicture(Long currentIndexedId) {
        this.currentIndexedId = currentIndexedId;
    }
    
    /**
     * Get config file user in server
     * @return Config file
     */
    public ConfigServer getConfig() {
        return config;
    }    

    /**
     * @return the idServer
     */
    public String getStorageName() {
        return idServer;
    }
}

///**
// * Utility class that take a interface (index, info, serach request) and launch a thread to wait request
// * @author lrollus
// */
//class WaitRequestThread extends Thread {
//
//    private StorageNetworkInterface server;
//    
//    private static Logger logger = Logger.getLogger(WaitRequestThread.class);
//
//    WaitRequestThread(StorageNetworkInterface server) {
//        this.server = server;
//    }
//
//    @Override
//    public void run() {
//        try {
//            this.server.waitForRequest();
//        } catch (Exception e) {
//           logger.error(e);
//        }
//    }
//}
