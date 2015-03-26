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
package retrieval.storage.index.main;

import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.server.globaldatabase.MemoryDatabase;
import retrieval.server.globaldatabase.RedisDatabase;
import retrieval.storage.exception.ReadIndexException;
import retrieval.storage.index.ValueStructure;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple hash table in memory
 * @author Rollus Loic
 */
public class MemoryHashTable extends HashTableIndex {

    protected String prefix = "";
    protected String subPrefix = "";
    /**
     * Name of Hashtable
     */
    public static String NAME = "MEMORY";
    /**
     * HashTable
     */
    protected Map<String, ValueStructure> hashmap;
    /**
     * Configuration object
     */
    private ConfigServer configStore;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(MemoryHashTable.class);

    /**
     * Constructor for a memory Hash Table
     * @param file Name of file which will contain store
     * @param configStore Configuration object
     * @param read If true, read index (if already exist), else create new index
     * @throws ReadIndexException Error during the read of index
     */
    public MemoryHashTable(MemoryDatabase database,String idServer, String idTestVector,ConfigServer configStore, boolean read) throws ReadIndexException {
        logger.debug("SimpleHashMap: start");
        this.configStore = configStore;
        try {
            logger.debug("SimpleHashMap: read old index");
             if(!read) {
                 hashmap = (Map<String, ValueStructure>)(database.getDatabase());
                 this.prefix = GlobalDatabase.KEY_INDEX_STORE + "#"+idServer+"#"+idTestVector+"#";
                 this.subPrefix = GlobalDatabase.KEY_INDEX_STORE + "#"+idServer+ "#";
             } 
                 
        } catch (Exception e) {
            throw new ReadIndexException(e.toString());
        }
    }

    /**
     * Put a key and its value on the store
     * @param key Key
     * @param Value Value
     */
    public void put(String key, ValueStructure Value) {
        hashmap.put(this.prefix+key, Value);
    }

    /**
     * Get a value of the key
     * @param key Key
     * @return Value
     */
    public ValueStructure get(String key) {
        return hashmap.get(this.prefix+key);
    }

    public Map<String,ValueStructure> getAll(List<String> key) {
        Map<String,ValueStructure> list = new HashMap<String,ValueStructure> ();
        Iterator<String> it = key.iterator();
        while(it.hasNext()) {
            String strkey = it.next();
            ValueStructure v = get(strkey);
            if(v!=null) {
                list.put(strkey, v);
            }
        }
        return list;
    }

    @Override
    public ConcurrentHashMap<String, Long> fillAllEntry(ConcurrentHashMap<String, Long> visualWord) {
        Iterator<String> it = visualWord.keySet().iterator();
        while(it.hasNext()) {
            String key = it.next();
            ValueStructure value = this.get(key);
            visualWord.put(key, value!=null?value.getNBT():0);
        }
        return visualWord;
    }

    /**
     * Delete a map of key on the concurrent store
     * @param mapID Key that must be deleted
     */
    public void deleteAll(Map<Long, Integer> mapID) {
        logger.info("deleteAll:" + mapID.size());
        List<String> emptyKeys = new ArrayList<String>();
        //for each element in the main map
        for (Map.Entry<String, ValueStructure> entry : hashmap.entrySet()) {
            //take the value
            ValueStructure value = entry.getValue();
            //and erase all item of mapID in the value
            value.deleteValue(mapID);

            if(value.getEntries().isEmpty()) {
                emptyKeys.add(entry.getKey());
            }
        }

        for(int i=0;i<emptyKeys.size();i++) {
             hashmap.remove(this.prefix+emptyKeys.get(i));
        }

        
    }

    public boolean isRessourcePresent(Long id) {
        for (Map.Entry<String, ValueStructure> entry : hashmap.entrySet()) {
            //take the value
            ValueStructure value = entry.getValue();
            //and erase all item of mapID in the value
            if(value.isPicturePresent(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Print map stat
     */
    public void printStat() {
        logger.info("INDEX TOTAL SIZE:"+hashmap.size());

    }

    /**
     * Close Voldemort index
     * @throws Exception Error during close
     */
    public void closeIndex() throws Exception {
        //just in memory
    }

    public void sync() {
        //memory-only so no sync between memory and disk
    }

    @Override
    public void delete(String key) {
        hashmap.remove(this.prefix+key);
    }
}
