package retrieval.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import retrieval.config.ConfigServer;
import retrieval.dist.MultiServerMessageNBT;
import retrieval.dist.RequestPictureVisualWord;
import retrieval.exception.CBIRException;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.server.globaldatabase.KyotoCabinetDatabase;
import retrieval.storage.Storage;
import retrieval.storage.exception.InternalServerException;
import retrieval.storage.index.ResultSim;
import retrieval.utils.FileUtils;
/**
 * A retrieval super server can carry n local object servers.
 * @author lrollus
 */
public final class RetrievalServer {

    public static String EQUITABLY = "###EQUITABLY###";
    /**
     * Map with id server and the server
     */
    private Map<String, Storage> serverMap;
    
    /**
     * List with all server
     */
    private List<Storage> serverList;
    
    /**
     * Config object of the last created server
     */
    private ConfigServer configServer;
    
    /**
     * Config object to init multiserver
     */
    private ConfigServer configMain;
    
    /**
     * Socket interface to wait request (index, search, infos,...)
     */
    private RetrievalServerSocketXML socketInterface;
    private WaitRequestThread threadRequest;
    
    /**
     * Port of the current MultiServer
     */
    private int port = -1;
    
    /**
     * Indice of server that will carry next index request
     */
    private int currentServerIndice;
    
    GlobalDatabase globalDatabase = null;
    
    
    private static Logger logger = Logger.getLogger(RetrievalServer.class);


    /**
     * Create MultiServer
     * @param configMain Config server object
     * @param environment Name of environment (you can use smth like 'test','prod',...)
     * @param totalServer Number of server to create
     * @param deleteIndex Delete index path or not
     */
    public RetrievalServer(ConfigServer configMain, String environment, int totalServer, boolean deleteIndex) {
        this(configMain, environment, deleteIndex, RetrievalServer.createNewContainers(totalServer));
    }
    
    public RetrievalServer(ConfigServer configMain, String environment,boolean deleteIndex) {
        this(configMain, environment,deleteIndex,null);
    } 
    
 
    /**
     * Create MultiServer
     * @param configMain Config server object
     * @param environment Name of environment (you can use smth like 'test','prod',...)
     * @param totalServer Number of server to create
     * @param deleteIndex Delete index path or not
     * @param serverKeys Servers name (size must be equals to totalServer)
     */   
    public RetrievalServer(ConfigServer configMain, String environment, boolean deleteIndex, String[] serverKeys) {
        try {
            
            logger.info("Init retrieval super server withwith  env=" + environment + " deleteIndex=" + deleteIndex);
            this.configMain = configMain;
            configServer = (ConfigServer) configMain.clone();
            logger.info("Init config and path...");
            configServer.setIndexPath(configServer.getIndexPath() + environment + "/"); 
            if(!new File(configServer.getIndexPath()).exists()) {
               new File(configServer.getIndexPath()).mkdirs(); 
            } 
           
            logger.info("Read container...");
            String[] containers;
            if(serverKeys!=null) {
               containers = serverKeys; 
            } else if(configServer.getStoreName().equals("MEMORY")) {
               containers = new String[0];
            } else if(configServer.isGolbalDatabase()) {
               containers = createGlobalDatabaseContainers(configServer);
            } else {
               containers = createLocalDatabaseContainers(configServer);
            }
            logger.info("serverKeys:"+containers);
            configServer.setNumberOfSubserver(containers.length); 
            logger.info("Create container:"+Arrays.toString(containers));

            if (deleteIndex) {
                logger.info("Delete old index data");
                clearIndexDirectory();
            }            
            
            if(configMain.getStoreName().equals("KYOTOSINGLEFILE")) {
                logger.info("Init global database");
               globalDatabase = new KyotoCabinetDatabase(configServer); 
            }             
            
            serverMap = new HashMap<String, Storage>();
            serverList = new ArrayList<Storage>();

            for (int i = 0; i < containers.length; i++) {
                logger.info("create server:"+containers[i]);
                createServer(containers[i]);

            }
            logger.info("MultiServer started...");
            currentServerIndice = 0;           
        } catch(Exception e) {
            logger.fatal(e.toString());
        }
    }

    public static String[] createNewContainers(int number) {
        String[] containers = new String[number];
        for (int i = 0; i < number; i++) {
            containers[i] = i + "";
        }
        return containers;
    }
    
    public String[] createGlobalDatabaseContainers(ConfigServer config) throws Exception {
        return readContainersFromDisk(config);
    }
    
    public static void writeContainersOnDisk(ConfigServer config,Map<String,Storage> servers) throws Exception{
        JSONArray attr = new JSONArray();
        Set<String> keys = servers.keySet();
        for(String name : keys) {
           attr.add(name); 
        }
        String jsonString = attr.toString();
        File newTextFile = new File(config.getIndexPath()+"containers.json");
        FileWriter fileWriter = new FileWriter(newTextFile);
        fileWriter.write(jsonString);
        fileWriter.close();               
    }
    
    public static String[] readContainersFromDisk(ConfigServer config) throws Exception {
        File file = new File(config.getIndexPath()+"containers.json");
        logger.info("Read container list from "+file.getAbsolutePath());
        if(file.exists()) {
            logger.info("File found, read data");
            FileInputStream fstream = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            String jsonString = "";
            while ((strLine = br.readLine()) != null)   {
                jsonString = jsonString + strLine;
            }
            in.close();
            
            List obj = (List)JSONValue.parse(jsonString);
            
            int nbreNull = 0;
            for(int i=0;i<obj.size();i++) {
                if(obj.get(i)==null) {
                   nbreNull++; 
                }
            }            
            
            String[] containers = new String[obj.size()-nbreNull];
            int j = 0;
            for(int i=0;i<obj.size();i++) {
                if(obj.get(i)!=null) {
                    containers[j]=obj.get(i).toString();
                    j++;
                }
                
            }
            return containers;
        } else {
            logger.info("File not found, return empty array:"+new String[0]);
            return new String[0];
        } 
    }

    public String[] createLocalDatabaseContainers(ConfigServer config) {
        File basePath = new File(config.getIndexPath());
        return FileUtils.listDirectories(basePath);
    }    
    
    public String getIndexPath() {
        return configServer.getIndexPath();
    }

    /**
     * Add a new Server on MultiServer
     * @param key Server name
     * @throws Exception Error during server creation
     */
    public synchronized void createServer(String key) throws Exception {
        //init config for each server
        if(getServerMap().containsKey(key)) {
            throw new CBIRException("Server "+key + " already exist!");
        }
        ConfigServer configLocalServer = configMain.clone();
        
        int i = getServerList().size();
        //MAYBE THE redisDatabase computation must be check
        
        if(globalDatabase!=null) {
            logger.info("Global database is set");
        } else {
            logger.info("Global database is not set, init a database for each server");
            int redisDatabase = i * ((configServer.getNumberOfTestVector() * 2) + (3));
            configLocalServer.setRedisStoreId(redisDatabase - 1);
            configServer.setNumberOfSubserver(configServer.getNumberOfSubserver() + 1);
            configLocalServer.setIndexPath(configServer.getIndexPath() + key + "/");           
        }

        logger.info("Create server " + key + " path:" + configLocalServer.getIndexPath());
        Storage server = new Storage(key,configLocalServer,globalDatabase);
        serverMap.put(key, server);
        getServerList().add(server);
        logger.info("Server list:"+getServerList());
        if(!configMain.getStoreName().equals("MEMORY")) 
            writeContainersOnDisk(configServer,serverMap);
        //server.startWithoutInitSocket();       
        server.start();
        Thread.sleep(100);
        logger.info("Server started...");
    }
    
    

    /**
     * Delete this server on MultiServer
     * @param key Server name
     * @throws Exception Error during server creation 
     */
    public void deleteServer(String key) throws Exception {
        //init config for each server
        Storage server = serverMap.get(key);
        logger.info("Stop server " + key + "...");
        if (server == null) {
            throw new Exception("Server don't exist with key=" + key);
        }
        server.stop();
        server.deleteIndex();
        serverMap.remove(key);
        getServerList().remove(server);
    }

    /**
     * Get the current server and move cursor to the next one
     * @return Current server
     */
    public synchronized Storage getNextServer() {
        if (currentServerIndice == getServerList().size() - 1) {
            currentServerIndice = 0;
        }
        else {
            currentServerIndice++;
        }
        return getServerList().get(currentServerIndice);
    }

    /**
     * Get the server with this key
     * @param key Server key
     * @return Server
     */
    public synchronized Storage getServer(String key)  {
        return getServer(key,false);
    }

    public synchronized Storage getServer(String key, boolean createIfNotExist) {
        try {
            Storage storage = serverMap.get(key);
            if(createIfNotExist && storage==null) {
               createServer(key);
               storage = getServer(key);    
            } 
            return storage;
        } catch(Exception e) {
            logger.error("Cannot get storage: "+key + ": " +e.toString());
            return null;
        }
        
    }    
    
    /**
     * Get a map with server
     * @return Map with entry id-server
     */
    public Map<String, Storage> getServerMap() {
        return serverMap;
    }  
    
    /**
     * @return the serverList
     */
    public List<Storage> getServerList() {
        return serverList;
    }  

    /**
     * Retrieve all keys from list and get ther corresponding server
     * @param keys Servers keys to retrieve
     * @return Map with serever id as key and server as value
     */
    public synchronized Map<String, Storage> getServers(List<String> keys) {
        Iterator<String> itr = keys.iterator();
        Map<String, Storage> servers = new TreeMap<String, Storage>();
        while (itr.hasNext()) {
            String key = itr.next();
            if(serverMap.get(key)!=null) {
                servers.put(key, serverMap.get(key));
            }
        }
        return servers;
    }

    /**
     * Retrieve all keys id
     * @return Servers id
     */
    public synchronized List<String> getServersId() {
        Iterator<String> itr = serverMap.keySet().iterator();
        List<String> serversId = new ArrayList<String>();
        while (itr.hasNext()) {
            String key = itr.next();
            serversId.add(key);
        }
        return serversId;
    }

    /**
     * Load each server and wait on port
     * @param port Port to wait request
     */
    public void loadWithSocket(int port) {
        try {
            this.port = port;
            socketInterface = new RetrievalServerSocketXML(this, port);
            threadRequest = new WaitRequestThread(socketInterface);
            threadRequest.start();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    /**
     * Close request socket
     */
    public void closeSocket() {
        try {
            threadRequest.close();
        } catch (Exception e) {
            logger.error("Cannot close connection:"+e);
        }
    }

    /**
     * Stop multiserver (and all servers)
     */
    public void stop() {
        logger.info("Stop all server");
        for (int i = 0; i < getServerList().size(); i++) {
            try {
                getServerList().get(i).stop();
            } catch (Exception e) {
                logger.error(e);
            }
            logger.info("server " + i + " stoped...");
        }
        try {closeSocket();}catch(Exception e) {logger.error("Cannot close connection:"+e);}
        try {threadRequest.stop();}catch(Exception e) {logger.error("Cannot close connection:"+e);}
        try {socketInterface.close();}catch(Exception e) {logger.error("Cannot close connection:"+e);}
        logger.info("All servers are closed!");
    }   
    
    
    /**
     * Get the size of multiserver (number of picture on all server)
     * @return Multiserver size
     */
    public Long getSize() {
        Long total = 0L;
        for (int i = 0; i < getServerList().size(); i++) {
            total = total + getServerList().get(i).getNumberOfItem();
        }
        return total;
    }

    /**
     * Get the size of each server
     * @return Map with key=server id and value=server size
     */
    public Map<String, Long> getServersSize() {
        Map<String, Long> serversSizeMap = new TreeMap();

        for (Map.Entry<String, Storage> entry : serverMap.entrySet()) {
            serversSizeMap.put(entry.getKey(), entry.getValue().getNumberOfItem());
        }
        return serversSizeMap;
    } 
    
    /**
     * Index picture asynchrone (on queue) on next server
     * @param picture Picture path
     * @param authorization Authorization to acess pictures
     * @throws Exception Error during indexing
     */
     public void indexPictureAsynchrone(BufferedImage picture,Long id,Map<String,String> properties) throws Exception {
        Storage server = getNextServer();
        indexPictureAsynchrone(picture,id,properties);
    }
     
      /**
      * Index picture asynchrone (on queue) on server idServer if idServer exist
      * @param picture Picture path
      * @param idServer Server id
      * @return False if server not exist, otherwise true
      * @throws Exception Error during indexing
      */
     public void indexPictureAsynchrone(BufferedImage picture,Long id,Map<String,String> properties, Storage storage) throws Exception {
         storage.addToIndexQueue(picture,id,properties);
    } 
     
     public void indexPictureAsynchrone(BufferedImage picture,Long id,Map<String,String> properties, String storageName) throws Exception {
         Storage storage = getServer(storageName);
         if(storage==null) {
             createServer(storageName);
             storage = getServer(storageName);
         }
         storage.addToIndexQueue(picture,id,properties);
    }      
         
    /**
     * Index picture synchrone on next server
     * @param picture Picture path
     * @param authorization Authorization to acess pictures
     * @throws Exception Error during indexing
     */
    public void indexPictureSynchrone(BufferedImage picture,Long id,Map<String,String> properties, Storage storage) throws Exception {
        storage.indexPicture(picture,id,properties);
    }    
    
    public void indexPictureSynchrone(BufferedImage picture,Long id,Map<String,String> properties, String storageName) throws Exception {
         Storage storage = getServer(storageName);
         if(storage==null) {
             createServer(storageName);
             storage = getServer(storageName);
         }
         storage.indexPicture(picture,id,properties);
    } 

    /**
     * Index picture synchrone on next server
     * @param picture Picture path
     * @param authorization Authorization to acess pictures
     * @throws Exception Error during indexing
     */
    public void indexPictureSynchrone(BufferedImage picture,Long id,Map<String,String> properties) throws Exception {
        Storage storage = getNextServer();
        storage.indexPicture(picture,id,properties);
    }    
    
    /**
     * Delete all picture path from all server
     * @param path Picture to delete
     * @throws Exception Error during deleting
     */
    public void delete(List<Long> ids) throws Exception {
        Thread[] threads = new Thread[getServerList().size()];
        for (int i = 0; i < getServerList().size(); i++) {
            threads[i] = new DeleteThread(getServerList().get(i), ids);
            threads[i].start();
        }
        for (int i = 0; i < getServerList().size(); i++) {
            threads[i].join();
        }
    }

    /**
     * Purge all server
     * @throws Exception Error during purge
     */
    public void purge() throws Exception {
        logger.info("Purge all server");
        Thread[] threads = new Thread[getServerList().size()];
        for (int i = 0; i < getServerList().size(); i++) {
            threads[i] = new PurgeThread(getServerList().get(i));
            threads[i].start();
        }
        for (int i = 0; i < getServerList().size(); i++) {
            threads[i].join();
        }
    }   
        
     /**
     * Print index info
     */
    public void printStat() {
        for (int i = 0; i < getServerList().size(); i++) {
            getServerList().get(i).printIndex();
        }
    }   
    
    /**
     * Check if all queues are empty
     * @return False if at least 1 queue is not empty
     */
    public boolean isIndexQueueEmpty() {
        for (int i = 0; i < getServerList().size(); i++) {
            if (!serverList.get(i).isIndexQueueEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get number of pictures in all index queue
     * @return 
     */
    public int getIndexQueueSize() {
        int size = 0;
        for (int i = 0; i < getServerList().size(); i++) {
            size = size + getServerList().get(i).getIndexQueueSize();
        }
        return size;
    }
    

    /**
     * Get NBT for each visual words on servers from servers list (search part 1)
     * @param visualWords Visual words
     * @param servers Servers list (empty = all servers)
     * @return All Visual words NBT for each servers
     * @throws Exception Error during search
     */
    Map<String, List<ConcurrentHashMap<String, Long>>> getNBT(List<ConcurrentHashMap<String, Long>> visualWords, List<String> servers) throws Exception {
        Map<String, List<ConcurrentHashMap<String, Long>>> allNBT = new TreeMap<String, List<ConcurrentHashMap<String, Long>>>();

        Map<String,Storage> serversInstance;
        if(!servers.isEmpty()) serversInstance = getServers(servers);
        else serversInstance = serverMap;
        logger.debug("Search on " + serversInstance);
        logger.debug("Servers available " + getServerMap());
        Iterator<Entry<String, Storage>> it = serversInstance.entrySet().iterator();

        NBTRequestThread[] threads = new NBTRequestThread[serversInstance.size()];
        int i=0;
        while (it.hasNext()) {
            Entry<String, Storage> entry = it.next();
            String idServer = entry.getKey();
            Storage server = entry.getValue();
            logger.debug("Search on container "+ idServer);
            logger.debug("Search on "+ idServer + " with size " + server.getNumberOfItem());
            threads[i]=new NBTRequestThread(server,idServer,allNBT,visualWords);
            threads[i].start();
            i++;
        }

        for(i=0;i<threads.length;i++) {
            threads[i].join();
        }
        return allNBT;
    }

    /**
     * Thanks to visual words info from vw, retrieve all similar pictures from each servers (search part 2)
     * @param vw Visual word info
     * @param Niq Number of patchs produced on search picture
     * @param k Max number of similar pictures for each server
     * @param servers Servers list (empty = all servers)
     * @return Similar pictures for each server (key=id server, value=server)
     * @throws Exception Error during search
     */
    public Map<String, List<ResultSim>> getPicturesSimilarities(Map<String, List<ConcurrentHashMap<String, RequestPictureVisualWord>>> vw, int Niq, int k,List<String> servers) throws Exception {
        Map<String, List<ResultSim>> allPictures = new TreeMap<String, List<ResultSim>>();
        
        Map<String,Storage> serversInstance;
        if(!servers.isEmpty()) {
            serversInstance = getServers(servers);
        }
        else {
            serversInstance = serverMap;
        }
        Iterator<Entry<String, Storage>> it = serversInstance.entrySet().iterator();

        SimRequestThread[] threads = new SimRequestThread[serversInstance.size()];
        int i = 0;
        while (it.hasNext()) {
            Entry<String, Storage> entry = it.next();
            String idServer = entry.getKey();
            Storage server = entry.getValue();
            logger.debug("Search on "+ idServer + " with size " + server.getNumberOfItem());
            threads[i]=new SimRequestThread(server,idServer,vw,Niq,k,allPictures);
            threads[i].start();
            i++;
        }

        for(i=0;i<threads.length;i++) {
            threads[i].join();
        }
        return allPictures;
    }    
    
    /**
     * Get all pictures from each servers
     * @return Map with key = server id and value = all pictures from server
     */
    public Map<Long,Map<String,String>> getInfos(String storageName) throws CBIRException {
        Map<String,List<Long>> results = new HashMap<String,List<Long>>();
        Storage storage = getServer(storageName);
        if(storage==null) {
            return null;
        }
        return storage.getAllPicturesMap();
    }      
    

    /**
     * Get request socket port
     * @return Port
     */
    public int getPort() {
        return port;
    }

    /**
     * Delete all index directory
     */
    public void clearIndexDirectory() {

        try {
            logger.info("clearIndexDirectory");
            File indexPath = new File(configServer.getIndexPath());
            if (indexPath.exists()) {
                FileUtils.deleteAllFilesRecursively(indexPath);
            }
        } catch (Exception e) {
            logger.fatal(e.toString());
        }
    }
}

class NBTRequestThread extends Thread {

    private Storage server;
    private String idServer;
    private Map<String, List<ConcurrentHashMap<String, Long>>> allNBT;
    private List<ConcurrentHashMap<String, Long>> vw;
    
    private static Logger logger = Logger.getLogger(NBTRequestThread.class);

    public NBTRequestThread(Storage server, String idServer, Map<String, List<ConcurrentHashMap<String, Long>>> allNBT,List<ConcurrentHashMap<String, Long>> vw) {
        this.server = server;
        this.idServer = idServer;
        this.allNBT = allNBT;
        this.vw = vw;
    }

    @Override
    public void run() {
        try {
        List<ConcurrentHashMap<String, Long>> nbtFromServer = server.getNBT(MultiServerMessageNBT.copyVWList(vw));
        allNBT.put(idServer, nbtFromServer);
        } catch(Exception e) {
            logger.error("ERROR:"+e);
        }
    }
}

class SimRequestThread extends Thread {

    private Storage server;
    private String idServer;
    private Map<String, List<ConcurrentHashMap<String, RequestPictureVisualWord>>> vw;
    private int Niq;
    private int k;
    private Map<String, List<ResultSim>> allPictures;
    
    private static Logger logger = Logger.getLogger(SimRequestThread.class);

    public SimRequestThread(Storage server, String idServer, Map<String, List<ConcurrentHashMap<String, RequestPictureVisualWord>>> vw, int Niq, int k,Map<String, List<ResultSim>> allPictures) {
        this.server = server;
        this.idServer = idServer;
        this.vw = vw;
        this.Niq = Niq;
        this.k = k;
        this.allPictures=allPictures;
    }

    @Override
    public void run() {
        try {
            allPictures.put(idServer, server.getPicturesSimilarities(vw.get(idServer), Niq, k));
        } catch(Exception e) {
            logger.error("ERROR:"+e);
        }
    }
}

class WaitRequestThread extends Thread {

    private RetrievalServerSocketXML server;
    
    private static Logger logger = Logger.getLogger(WaitRequestThread.class);

    public WaitRequestThread(RetrievalServerSocketXML server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            server.waitForRequest();
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }

    public void close() {
        try {
            server.close();
        } catch (Exception ex) {
            logger.error("Cannot close connection:"+ex);
        }
    }
}
//
//class IndexerThread extends Thread {
//
//    private List<String> lists;
//    private Storage server;
//    
//    private static Logger logger = Logger.getLogger(IndexerThread.class);
//
//    public IndexerThread(Storage server, List<String> lists) {
//        this.server = server;
//        this.lists = lists;
//    }
//
//    @Override
//    public void run() {
//        try {
//            server.indexPictureFiles(lists,authorization);
//        } catch (InternalServerException ex) {
//            logger.error("Cannot close connection:"+ex);
//        }
//    }
//}

class DeleteThread extends Thread {

    private List<Long> lists;
    private Storage server;
    
    private static Logger logger = Logger.getLogger(DeleteThread.class);

    public DeleteThread(Storage server, List<Long> lists) {
        this.server = server;
        this.lists = lists;
    }

    @Override
    public void run() {
        try {
            server.deletePictures(lists);
        } catch (InternalServerException ex) {
            logger.error("Cannot close connection:"+ex);
        }
    }
}

class PurgeThread extends Thread {

    private Storage server;
    
    private static Logger logger = Logger.getLogger(PurgeThread.class);

    public PurgeThread(Storage server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            server.purgeIndex();
        } catch (Exception ex) {
            logger.error("Cannot close connection:"+ex);
        }
    }
}
