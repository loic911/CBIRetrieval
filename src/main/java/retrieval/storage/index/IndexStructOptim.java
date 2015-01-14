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
package retrieval.storage.index;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;
import retrieval.storage.exception.StartIndexException;
import retrieval.storage.index.compress.compressNBT.CompressIndexNBT;
import retrieval.storage.index.main.*;

/**
 * Index struct for an opt. engine like REDIS.
 * The main index arch is: | KEY | VALUE where VALUE is a Map (KEY | VALUE)
 * Sometimes we need to incr values from a map inside the main value map (with other engine:
 * value = map.get(key)
 * int subvalue = value.get(key2)
 * value.set(key2,subvalue+n)
 * ...
 * Redis provide method to do this without retrieving. So we dont need to retrieve the main value
 * jedis.hincr(key,key2,n)
 * @author Rollus Loic
 */
public class IndexStructOptim extends IndexStructAbs {
    private static Logger logger = Logger.getLogger(IndexStructClassic.class);
    private HashTableIndexOptim map;
    /**
     * Constructor for an index structure.
     * @throws StartIndexException Exception during the index start
     * @throws ReadIndexException Exception during the index read (if already
     * exists
     */
    public IndexStructOptim(String idStorage,String idTestVector, ConfigServer configStore, int testVector,Object database)
            throws StartIndexException, ReadIndexException {

        logger.info("IndexStruct:" +configStore.getStoreName() + " name=" + idTestVector);
        this.configStore = configStore;
        this.compressIndex = CompressIndexNBT.getCompressIndexFactory(configStore,idStorage,idTestVector,database);

        /*
         * Instanciate correct class to build index object
         */
        if (configStore.getStoreName().equals(RedisHashTable.NAME)) {
            map = new RedisHashTable(database,idStorage,idTestVector,configStore);
        } else {
            throw new StartIndexException(configStore.getStoreName() + " is not implemented");
        }
    }

    /**
     * Delete all image id in map
     * @param mapID Image ID to delete as key (don't care of value)
     */
    public void delete(Map<Long, Integer> mapID) {
        try {map.deleteAll(mapID);} catch(Exception e) { logger.error(e.toString());}
    }

    /**
     * Add a new entry for a key in index
     * @param B Visual word
     * @param I Request picture
     * @param NIBT
     */
    public void put(ConcurrentHashMap<String, Long> visualWords, Long I) {
        map.incrementHashValue(visualWords, I,compressIndex);
    }

    /**
     * Get NBT value map with B
     * @param visualWord Visual word B
     * @return Number of patchs map with B in index
     */
    public ConcurrentHashMap<String, Long> getNBT(ConcurrentHashMap<String, Long> visualWord) {
        return map.getAllValues(visualWord);
    }

    /**
     * Get all entry from index for these visualwords B
     * @param visualWord Visualwords to retrieve
     * @return Map with each VW as key and its value as value
     */
    @Override
    public Map<String, ValueStructure> getAll(List<String> visualWord) {
        return map.getAll(visualWord);
    }

    /**
     * Close index, write information in disk if necessary, ...
     * @throws CloseIndexException Exception during the index close
     */
    public void closeIndex() throws CloseIndexException {
        try {
            logger.info("closeIndex");
            this.map.closeIndex();
        } catch (Exception e) {
            logger.error(e);
            throw new CloseIndexException(e.toString());
        }
    }

    /**
     * Sync memory and disc database
     */
    public void sync() {
        try {
            this.map.sync();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Print statistic of index
     */
    public void printStat() {
        map.printStat();
    }

    /**
     * Check if a picture is still index
     * @param id Picture id
     * @return True if picture is still in index
     */
    public boolean isRessourcePresent(Long id) {
        return map.isRessourcePresent(id);
    }
}
