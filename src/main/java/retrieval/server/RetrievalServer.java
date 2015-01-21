/*
 * Copyright 2009-2014 the original author or authors.
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
package retrieval.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.dist.MultiServerMessageNBT;
import retrieval.dist.RequestPictureVisualWord;
import retrieval.exception.CBIRException;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.server.globaldatabase.KyotoCabinetDatabase;
import retrieval.server.globaldatabase.MemoryDatabase;
import retrieval.server.globaldatabase.RedisDatabase;
import retrieval.storage.Storage;
import retrieval.storage.exception.InternalServerException;
import retrieval.storage.index.ResultSim;
import retrieval.utils.CollectionUtils;
import retrieval.utils.FileUtils;
/**
 * A retrieval server manage n local storage.
 * @author lrollus
 */
public final class RetrievalServer {

    /**
     * Keyword to store a picture in "random" storage.
     * If you index multiple images, they will be store in different storage
     */
    public static String EQUITABLY = "###EQUITABLY###";
    
    /**
     * Map with storage name and the storage object
     */
    private Map<String, Storage> storageMap;
    
    /**
     * List with all storage
     */
    private List<Storage> storageList;
    
    /**
     * Config object of the last created storage
     */
    private ConfigServer configServer;
    
    /**
     * Config object to init server
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
     * Index of server that will carry next index request
     */
    private int currentStorageIndex;
    
    GlobalDatabase globalDatabase = null;
    
    private static Logger logger = Logger.getLogger(RetrievalServer.class);

    /**
     * Create Server
     * @param configMain Config server object
     * @param environment Name of environment (you can use something like 'test','prod',...)
     * @param totalStorage Number of server to create
     * @param deleteIndex Delete index path or not
     */
    public RetrievalServer(ConfigServer configMain, String environment, int totalStorage, boolean deleteIndex) {
        this(configMain, environment, deleteIndex, CollectionUtils.createNewContainers(totalStorage));
    }
    
     /**
     * Create Server
     * @param configMain Config server object
     * @param environment Name of environment (you can use something like 'test','prod',...)
     * @param deleteIndex Delete index path or not
     */   
    public RetrievalServer(ConfigServer configMain, String environment,boolean deleteIndex) {
        this(configMain, environment,deleteIndex,null);
    } 
     
    /**
     * Create MultiServer
     * @param configMain Config server object
     * @param environment Name of environment (you can use something like 'test','prod',...)
     * @param deleteIndex Delete index path or not
     * @param storageKeys Storage name, if not null create storage, if null try to read from database
     */   
    public RetrievalServer(ConfigServer configMain, String environment, boolean deleteIndex, List<String> storageKeys) {
        try {
            
            logger.info("Init retrieval super server withwith  env=" + environment + " deleteIndex=" + deleteIndex);
            this.configMain = configMain;
            configServer = (ConfigServer) configMain.clone();
            logger.info("Init config and path...");
            configServer.setIndexPath(configServer.getIndexPath() + environment + "/"); 
            if(!new File(configServer.getIndexPath()).exists()) {
               new File(configServer.getIndexPath()).mkdirs(); 
            } 
            
            if (deleteIndex) {
                logger.info("Delete old index data");
                clearIndexDirectory();
            }            
            
            if(configMain.getStoreName().equals("MEMORY")) {
                logger.info("Init global memory database");
               globalDatabase = new MemoryDatabase(configServer); 
            } else if(configMain.getStoreName().equals("KYOTOSINGLEFILE")) {
                logger.info("Init global kyoto database");
               globalDatabase = new KyotoCabinetDatabase(configServer); 
            } else if(configMain.getStoreName().equals("REDIS")) {
                logger.info("Init global redis database");
                globalDatabase = new RedisDatabase(configServer);
            }  else throw new CBIRException("Index name "+configMain.getStoreName() +" not supported!");
                       
            logger.info("Read container...");
            List<String> containers;
            if(storageKeys!=null && !storageKeys.isEmpty()) {
               containers = storageKeys; 
            } else {
                containers = globalDatabase.getStorages();
            } 
            logger.info("getStorages:"+containers);


            storageMap = new HashMap<String, Storage>();
            storageList = new ArrayList<Storage>();

            for (int i = 0; i < containers.size(); i++) {
                logger.info("create server:"+containers.get(i));
                createStorage(containers.get(i));

            }
            logger.info("MultiServer started...");
            currentStorageIndex = 0;           
        } catch(Exception e) {
            logger.fatal(e.toString());
        }
    }
      
    
    public String getIndexPath() {
        return configServer.getIndexPath();
    }

    /**
     * Add a new storage
     * @param key Storage name
     * @throws Exception Error during storage creation
     */
    public synchronized void createStorage(String key) throws Exception {
        //init config for each server
        if(getStorageMap().containsKey(key)) {
            throw new CBIRException("Storage "+key + " already exist!");
        }
        ConfigServer configLocalServer = configMain.clone();
        logger.info("Create storage " + key + " path:" + configLocalServer.getIndexPath());
        Storage storage = new Storage(key,configLocalServer,globalDatabase);
        storageMap.put(key, storage);
        getStorageList().add(storage);
        logger.info("Server list:"+getStorageList());
        globalDatabase.addStorage(key);      
        storage.start();
        Thread.sleep(100);
        logger.info("Server started...");
    }
    
    /**
     * Delete this storage
     * @param key Storage name
     * @throws Exception Error during server creation 
     */
    public void deleteStorage(String key) throws Exception {
        //init config for each server
        Storage server = storageMap.get(key);
        logger.info("Stop server " + key + "...");
        if (server == null) {
            throw new Exception("Server doesn't exist with key=" + key);
        }
        server.stop();
        server.deleteIndex();
        storageMap.remove(key);
        getStorageList().remove(server);
        globalDatabase.deleteStorage(key);
    }

    /**
     * Get the current server and move cursor to the next one
     * @return Current server
     */
    public synchronized Storage getNextStorage() {
        if (currentStorageIndex == getStorageList().size() - 1) {
            currentStorageIndex = 0;
        }
        else {
            currentStorageIndex++;
        }
        return getStorageList().get(currentStorageIndex);
    }

    /**
     * Get the server with this key
     * @param key Server key
     * @return Server
     */
    public synchronized Storage getStorage(String key)  {
        return getStorage(key,false);
    }

    public synchronized Storage getStorage(String key, boolean createIfNotExist) {
        try {
            Storage storage = storageMap.get(key);
            if(createIfNotExist && storage==null) {
               createStorage(key);
               storage = RetrievalServer.this.getStorage(key);    
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
    public Map<String, Storage> getStorageMap() {
        return storageMap;
    }  
    
    /**
     * @return the serverList
     */
    public List<Storage> getStorageList() {
        return storageList;
    }  

    /**
     * Retrieve all keys from list and get their corresponding server
     * @param keys Servers keys to retrieve
     * @return Map with storage id as key and storage as value
     */
    public synchronized Map<String, Storage> getStorageMapByName(List<String> keys) {
        Iterator<String> itr = keys.iterator();
        Map<String, Storage> servers = new TreeMap<String, Storage>();
        while (itr.hasNext()) {
            String key = itr.next();
            if(storageMap.get(key)!=null) {
                servers.put(key, storageMap.get(key));
            }
        }
        return servers;
    }

    /**
     * Retrieve all keys id
     * @return Storages id
     */
    public synchronized List<String> getStoragesName() {
        Iterator<String> itr = storageMap.keySet().iterator();
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
     * Stop server (and all storages)
     */
    public void stop() {
        logger.info("Stop all server");
        for (int i = 0; i < getStorageList().size(); i++) {
            try {
                getStorageList().get(i).stop();
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
     * Get the size of server (number of picture on all storage)
     * @return Server size
     */
    public Long getSize() {
        Long total = 0L;
        for (int i = 0; i < getStorageList().size(); i++) {
            total = total + getStorageList().get(i).getNumberOfItem();
        }
        return total;
    }

    /**
     * Get the size of each server
     * @return Map with key=server id and value=server size
     */
    public Map<String, Long> getStoragesSize() {
        Map<String, Long> serversSizeMap = new TreeMap();

        for (Map.Entry<String, Storage> entry : storageMap.entrySet()) {
            serversSizeMap.put(entry.getKey(), entry.getValue().getNumberOfItem());
        }
        return serversSizeMap;
    } 


    /**
     * Index a picture with a sync method on a random storage
     * @param picture Picture to index
     * @param id Picture id
     * @param properties Picture properties
     * @throws Exception Error during indexing
     */
    public void indexPictureSynchrone(BufferedImage picture,Long id,Map<String,String> properties) throws Exception {
        Storage storage = getNextStorage();
        storage.indexPicture(picture,id,properties);
    }    
    
    /**
     * Delete all picture path from all server
     * @param ids Pictures to delete
     * @throws Exception Error during deleting
     */
    public void delete(List<Long> ids) throws Exception {
        Thread[] threads = new Thread[getStorageList().size()];
        for (int i = 0; i < getStorageList().size(); i++) {
            threads[i] = new DeleteThread(getStorageList().get(i), ids);
            threads[i].start();
        }
        for (int i = 0; i < getStorageList().size(); i++) {
            threads[i].join();
        }
    }

    /**
     * Purge all server
     * @throws Exception Error during purge
     */
    public void purge() throws Exception {
        logger.info("Purge all server");
        Thread[] threads = new Thread[getStorageList().size()];
        for (int i = 0; i < getStorageList().size(); i++) {
            threads[i] = new PurgeThread(getStorageList().get(i));
            threads[i].start();
        }
        for (int i = 0; i < getStorageList().size(); i++) {
            threads[i].join();
        }
    }   
        
     /**
     * Print index info
     */
    public void printStat() {
        for (int i = 0; i < getStorageList().size(); i++) {
            getStorageList().get(i).printIndex();
        }
    }   
    
    /**
     * Check if all queues are empty
     * @return False if at least 1 queue is not empty
     */
    public boolean isIndexQueueEmpty() {
        for (int i = 0; i < getStorageList().size(); i++) {
            if (!storageList.get(i).isIndexQueueEmpty()) {
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
        for (int i = 0; i < getStorageList().size(); i++) {
            size = size + getStorageList().get(i).getIndexQueueSize();
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
        if(!servers.isEmpty()) serversInstance = getStorageMapByName(servers);
        else serversInstance = storageMap;
        logger.debug("Search on " + serversInstance);
        logger.debug("Servers available " + getStorageMap());
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
     * @param Niq Number of patches produced on search picture
     * @param k Max number of similar pictures for each server
     * @param servers Servers list (empty = all servers)
     * @return Similar pictures for each server (key=id server, value=server)
     * @throws Exception Error during search
     */
    public Map<String, List<ResultSim>> getPicturesSimilarities(Map<String, List<ConcurrentHashMap<String, RequestPictureVisualWord>>> vw, int Niq, int k,List<String> servers) throws Exception {
        Map<String, List<ResultSim>> allPictures = new TreeMap<String, List<ResultSim>>();
        
        Map<String,Storage> serversInstance;
        if(!servers.isEmpty()) {
            serversInstance = getStorageMapByName(servers);
        }
        else {
            serversInstance = storageMap;
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
     * Get all pictures from a storage
     * @param storageName Storage name
     * @return Map with key = image id and value = image properties
     * @throws CBIRException
     */
    public Map<Long,Map<String,String>> getInfos(String storageName) throws CBIRException {
        Storage storage = RetrievalServer.this.getStorage(storageName);
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
            logger.error("Close error:"+ex);
        }
    }
}

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
            logger.error("Delete error:"+ex);
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
            logger.info("Purge server "+server.getStorageName());
            server.purgeIndex();
        } catch (Exception ex) {
            logger.error("Purge error:"+ex);
        }
    }
}
