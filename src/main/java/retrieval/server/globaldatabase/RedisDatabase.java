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

import kyotocabinet.Cursor;
import kyotocabinet.DB;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import retrieval.config.ConfigServer;
import retrieval.storage.Storage;
import retrieval.storage.exception.ReadIndexException;

import java.io.File;
import java.util.*;

/**
 *
 * @author lrollus
 */
public class RedisDatabase implements GlobalDatabase{
    private static Logger logger = Logger.getLogger(Storage.class);
    private Jedis database;
    private Jedis databasePatchs;
    private Jedis databasePath;
    private Jedis databaseCompress;
    private Jedis databaseStorage;
    private Jedis databasePurge;

    public RedisDatabase(ConfigServer config) throws ReadIndexException {
        logger.info("redis: start");
        try {
            logger.info("redis: Creating database...");
            database = openDatabase(config.getRedisHost(), config.getRedisPort(),1);
            databasePatchs = openDatabase(config.getRedisHost(), config.getRedisPort(),2);
            databasePath = openDatabase(config.getRedisHost(), config.getRedisPort(),3);
            databaseCompress = openDatabase(config.getRedisHost(), config.getRedisPort(),4);
            databaseStorage = openDatabase(config.getRedisHost(), config.getRedisPort(),5);
            databasePurge = openDatabase(config.getRedisHost(), config.getRedisPort(),6);
            logger.info("redis: Database ready!");
        } catch (Exception e) {
            throw new ReadIndexException(e.toString());
        }        
    }    
    
    public static Jedis openDatabase(String host, String port, int store) {
            logger.info("Redis: open database "+host +":" + port);
            Jedis jedis = new Jedis(host,Integer.parseInt(port),20000);
            logger.info("Open jedis, select store "+store);
            jedis.select(store);
            return jedis;
    }

    
    public Object getDatabase() {
       return (Object)database; 
    }  
    public Object getDatabasePatchs() {
       return (Object)databasePatchs; 
    }  
    public Object getDatabaseProperties() {
       return (Object)databasePath; 
    } 
    
    public Object getDatabaseCompress() {
        return (Object)databaseCompress;
    }
    
    public Object getDatabaseStorage() {
        return (Object)databaseStorage;
    }   
    
    public List<String> getStorages() {
        List<String> storages = new ArrayList<String>();

        Set<String> keys = databaseStorage.keys("*");
        Iterator<String> it = keys.iterator();

        while(it.hasNext()) {
            String key = it.next();
            storages.add(key);
        }
        return storages;
    }
    
    public void addStorage(String name) {
        databaseStorage.set(name, "");
    }
    
    public void deleteStorage(String name) {
        databaseStorage.del(name);
    }    

    public void putToPurge(String storage, Map<Long, Integer> toPurge) {
//        byte[] data = SerializationUtils.serialize(yourObject);
//        YourObject yourObject = (YourObject) SerializationUtils.deserialize(byte[] data)
        HashMap<Long,Integer> map;
        byte[] data = databasePurge.get(SerializationUtils.serialize(storage));
        if(data!=null) {
            map = (HashMap<Long,Integer>) SerializationUtils.deserialize(data);
        } else {
            map = new HashMap<Long,Integer>();
        }
        map.putAll(toPurge);
        databasePurge.set(SerializationUtils.serialize(storage),SerializationUtils.serialize(map));       
    }

    public Map<Long, Integer> getPicturesToPurge(String storage) {
         HashMap<Long,Integer> map;
        byte[] data = databasePurge.get(SerializationUtils.serialize(storage));
        if(data!=null) {
            map = (HashMap<Long,Integer>) SerializationUtils.deserialize(data);
        } else {
            map = new HashMap<Long,Integer>();
        }
        return map;       
    }

    public void clearPurge(String storage) {
        databasePurge.set(SerializationUtils.serialize(storage),SerializationUtils.serialize(new HashMap<Long,Integer>()));  
    }
    
}
