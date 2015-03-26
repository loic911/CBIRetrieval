/*
 * Copyright 2015 ROLLUS Loïc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval.storage;

import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.dist.RequestPictureVisualWord;
import retrieval.exception.CBIRException;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.*;
import retrieval.storage.index.Index;
import retrieval.storage.index.IndexMultiThread;
import retrieval.storage.index.PictureIndex;
import retrieval.storage.index.ResultSim;
import retrieval.testvector.TestVectorListServer;
import retrieval.testvector.generator.TestVectorReading;
import retrieval.utils.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implement a storage which index pictures and response to
 * search request from client
 * @author Rollus Loic
 */
public final class Storage {

    /**
     * Storage name
     */
    private String idServer;
    
    /**
     * Storage database (memory, redis,...)
     */
    private GlobalDatabase globalDatabase;
    
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

    /**
     * Last picture that was taken from queue but not already
     * mark as indexed (between waited queue and index process)
     */
    private Long currentIndexedId;
    
    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(Storage.class);

    /**
     * Constructor for a server
     * @param idServer Storage name
     * @param config Configuration object
     * @param globalDatabase Storage databse
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
        
        TestVectorListServer testVectors = TestVectorReading.readServer(this.idServer,config.getVectorPath(),config,this.globalDatabase);

        logger.info("Server: " + testVectors.size() + " tests vectors");

        logger.info("Server: get Picture Index");
        PictureIndex pi = PictureIndex.getPictureIndex(idServer,config,globalDatabase);
        index = new IndexMultiThread(idServer,globalDatabase,config, testVectors, pi);
        logger.info("Purge size = " +index.getPurgeSize());
        index.sync();
        logger.info("There are " + index.getSize() + " images");
        } catch(Exception e) {
            logger.error(e);
            throw e;
        }
    } 
  
    /**
     * Start server
     * @throws InternalServerException Error during the server start
     */
    public void start() throws InternalServerException {
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
     * @param image Picture
     * @param id Image id
     * @param properties Picture properties 
     * @return ID of picture
     * @throws retrieval.storage.exception.AlreadyIndexedException
     * @throws retrieval.storage.exception.NoValidPictureException
     * @throws PictureTooHomogeneous Picture is too homogeneous to be indexed (ex: only one color,...)
     */
    public Long indexPicture(BufferedImage image, Long id, Map<String,String> properties) throws AlreadyIndexedException, NoValidPictureException, PictureTooHomogeneous {

        return index.addPicture(
                image,
                id,
                (properties!=null? new HashMap<>(properties) : new HashMap<>()),
                config.getNumberOfPatch(), 
                config.getResizeMethod(), 
                config.getSizeOfPatchResizeWidth(), 
                config.getSizeOfPatchResizeHeight(), 
                config.isSyncAfterImage());
    }
    
     /**
     * Add a single picture file to index queue
     * @param image Picture
     * @param id Image id
     * @param properties Picture properties 
     * @return Indexed Picture file
     * @throws TooMuchIndexRequestException Waited queue are full
     */
    public Long addToIndexQueue(BufferedImage image, Long id, Map<String,String> properties) throws TooMuchIndexRequestException {
        logger.debug("addToIndexQueue="+id);
        while(id==null) {
            id = System.currentTimeMillis() + new Random().nextLong();
            if(index.isPictureAlreadyIndexed(id)) {
                id = null;
            }
        }
        threadIndex.addInIndexPicture(image,id,(properties!=null? new HashMap<>(properties) : new HashMap<>()));
        return id;
    }  
    
    
    /**
     * Delete pictures in ids list from storage
     * THIS METHOD DOES NOT REMOVED PICTURES DATA FROM INDEX, just removed pictures from result.
     * If this method is used many times, call purge method sometimes
     * @param ids Pictures to delete
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
            e.printStackTrace();
            throw new InternalServerException(e.toString());
        }
    }
    
    /**
     * Delete picture id from storage
     * THIS METHOD DOES NOT REMOVED PICTURES DATA FROM INDEX, just removed pictures from result.
     * If this method is used many times, call purge method sometimes
     * @param id Picture to delete
     * @return Map with pictures path and exception (NoException if ok)
     * @throws InternalServerException Error during delete
     */    
    public Map<Long, CBIRException> deletePicture(Long id) throws InternalServerException {
        List<Long> ids = new ArrayList<Long>();
        ids.add(id);
        return deletePictures(ids);
    }
       
    /**
     * Get a picture properties
     * @param id Picture id
     * @return Properties
     */
    public Map<String,String> getProperties(Long id) {
        return index.getProperties(id);
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
     * Print index and stats (not available with all index type
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
     * @return True if picture is indexed, otherwise false
     */
    public boolean isPictureInIndex(Long id) {
        return index.isPictureAlreadyIndexed(id);
    }
    
    /**
     * Check if index queue is empty (Pictures are still waiting to be indexed)
     * @return True if index queue is empty, otherwise false
     */
    public boolean isIndexQueueEmpty() {
       return threadIndex.isIndexQueueEmpty();
    }
    
    /**
     * Get the image index queue size (= number of image to index)
     * @return Image index queue size
     */
    public int getIndexQueueSize() {
       return threadIndex.getIndexQueueSize();
    }    

    /**
     * Fill NBT (number of patches map with visual word B in index) for each
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
     * @param Niq Number of patches extract from Iq
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
            sim.setProperties(index.getProperties(sim.getId()));
            firstResults.add(sim);
        }
        return firstResults;
    }
//
//    public List<ResultSim> getPicturesSimilarities(List<ConcurrentHashMap<String, RequestPictureVisualWord>> vw, int Niq, int k) {
//        List<ResultSim> allResults = null; //index.computeSimilarity(vw,null, Niq); //TODO!!!!!!
//        List<ResultSim> firstResults = new ArrayList<ResultSim>(k);
//        if(index.getSize()<1) {
//            return firstResults;
//        }
//
//        for (int i = 0; i < allResults.size() && i < k; i++) {
//            ResultSim sim = allResults.get(i);
//            sim.setId(sim.getId());
//            firstResults.add(sim);
//        }
//        return firstResults;
//    }

    
    /**
     * Change current indexed Picture
     * Rem: If picture is not yet indexed and not in queue, we can say that it
     * is in index process
     * @param currentIndexedId Current indexed Picture
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

    public TestVectorListServer getTestVectors() {
        return index.getTestVectors();
    }
}