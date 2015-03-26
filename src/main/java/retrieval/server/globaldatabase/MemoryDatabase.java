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
package retrieval.server.globaldatabase;

import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;
import retrieval.config.ConfigServer;
import retrieval.storage.Storage;
import retrieval.storage.exception.ReadIndexException;
import retrieval.storage.index.ValueStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lrollus
 */
public class MemoryDatabase implements GlobalDatabase{
    private static Logger logger = Logger.getLogger(Storage.class);

    private Map<String,ValueStructure> database;
    private Map<String,String> databasePatchs;
    private Map<Long, Map<String,String>> databasePath;
    private Map<String,Integer> databaseCompress;
    private Map<String,String> databaseStorage;
    private Map<String,Map<Long,Integer>> databasePurge;

    
    public MemoryDatabase(ConfigServer config) throws ReadIndexException {
        logger.info("MemoryDatabase: start");
        database = new HashMap<String,ValueStructure>();
        databasePatchs = new HashMap<String,String>();
        databasePath = new HashMap<Long, Map<String,String>>();
        databaseCompress = new HashMap<String,Integer>();
        databaseStorage = new HashMap<String,String>(); 
        databasePurge = new HashMap<String,Map<Long,Integer>>(); 
    }    

    public Object getDatabase() {
        logger.info("getDatabase on memory!");
        return database;
    }  
    public Object getDatabasePatchs() {
        logger.info("getDatabase on memory!");
       return databasePatchs;
    }  
    public Object getDatabaseProperties() {
        logger.info("getDatabase on memory!");
       return databasePath;
    } 
    public Object getDatabasePathInverse() {
        logger.info("getDatabase on memory!");
       return databaseCompress;
    } 
    
    public Object getDatabaseCompress() {
        logger.info("getDatabase on memory!");
        return databaseStorage;
    }
    
    public Object getDatabaseStorage() {
        logger.info("getDatabase on memory!");
        return databasePurge;
    }   
    
    public List<String> getStorages() {
        List<String> storages = new ArrayList<String>();
        for(Map.Entry<String,String> entries : databaseStorage.entrySet()) {
            storages.add(entries.getKey());
        }
        return storages;
    }
    
    public void addStorage(String name) {
        databaseStorage.put(name,"");
    }
    
    public void deleteStorage(String name) {
        databaseStorage.remove(name);
    }

    public void putToPurge(String storage, Map<Long, Integer> toPurge) {
        Map<Long,Integer> map = databasePurge.get(storage);
        if(map==null) {
            map = new HashMap<Long,Integer>();
        }
        map.putAll(toPurge);
        databasePurge.put(storage,map);
    }

    public Map<Long, Integer> getPicturesToPurge(String storage) {
        Map<Long,Integer> map = databasePurge.get(storage);
        if(map==null) {
            map = new HashMap<Long,Integer>();
        }    
        return map;
    }

    public void clearPurge(String storage) {
        databasePurge.put(storage,new HashMap<Long,Integer>());        
    }
    
}
