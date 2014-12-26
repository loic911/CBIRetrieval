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
package retrieval.server.globaldatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.storage.Storage;
import retrieval.storage.exception.ReadIndexException;

/**
 *
 * @author lrollus
 */
public class MemoryDatabase implements GlobalDatabase{
    private static Logger logger = Logger.getLogger(Storage.class);

    private Map<String,String> databaseStorage;
    
    private Map<String,Map<Long,Integer>> databasePurge;
    
    public MemoryDatabase(ConfigServer config) throws ReadIndexException {
        logger.info("MemoryDatabase: start");
        databaseStorage = new HashMap<String,String>(); 
        databasePurge = new HashMap<String,Map<Long,Integer>>(); 
    }    

    public Object getDatabase() {
        logger.error("getDatabase on memory!");
        return null;
    }  
    public Object getDatabasePatchs() {
        logger.error("getDatabase on memory!");
       return null;
    }  
    public Object getDatabaseProperties() {
        logger.error("getDatabase on memory!");
       return null;
    } 
    public Object getDatabasePathInverse() {
        logger.error("getDatabase on memory!");
       return null; 
    } 
    
    public Object getDatabaseCompress() {
        logger.error("getDatabase on memory!");
        return null;
    }
    
    public Object getDatabaseStorage() {
        logger.error("getDatabase on memory!");
        return null;
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
