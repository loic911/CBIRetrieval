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
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;
import retrieval.storage.exception.StartIndexException;
import retrieval.storage.index.compress.compressNBT.CompressIndexNBT;
import retrieval.storage.index.main.HashTableIndex;
import retrieval.storage.index.main.KyotoCabinetHashTable;
import retrieval.storage.index.main.MemoryHashTable;

/**
 * A visual word index which is implemented by:
 * -simple hashmap for MEMORY config
 * -bdb for BDB config
 * -voldemort for VOLDEMORT config
 * -mysql for a MYSQL config
 * -kyoto for kyoto cabinet
 * -...
 * Only MEMORY and KYOTO were supported
 * @author Rollus Loic
 */
public final class IndexStructClassic extends IndexStructAbs {

    /**
     * Index
     */
    private HashTableIndex map;

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(IndexStructClassic.class);

    /**
     * Constructor for an index structure.
     * The implementation will depends on configStore information
     * @param name Name of index
     * @param configStore Config store information
     * @param readIndex Read index files if already exists or delete them
     * (no influence on voldemort and memory)
     * @param database Database if still open
     * @throws StartIndexException Exception during the index start
     * @throws ReadIndexException Exception during the index read (if already
     * exists
     */
    public IndexStructClassic(String idStorage,String idTestVector, ConfigServer configStore, int testVector,Object database)
            throws StartIndexException, ReadIndexException {

        logger.info("IndexStruct:" +configStore.getStoreName() + " name=" + idTestVector);
        this.configStore = configStore;
        if(isCompressIndexEnabled()) {
            this.compressIndex = CompressIndexNBT.getCompressIndexFactory(configStore,idStorage,idTestVector,database);
        }
           
        /*
         * Instanciate correct class to build index object
         */
        logger.debug(configStore.getStoreName());
        if (    configStore.getStoreName().equals(MemoryHashTable.NAME)) {
            //MEMORY
            map = new MemoryHashTable(configStore, false);
        }  else if (configStore.getStoreName().equals(KyotoCabinetHashTable.NAME)) {
            //KYOTO SINGLE FILE (1 file for N servers and T test vector)
            map = new KyotoCabinetHashTable(database,idStorage,idTestVector,configStore);
        }else {
            throw new StartIndexException(configStore.getStoreName() + " is not implemented");
        }
    }

    /**
     * Check if compress index is enabled
     * @return True if compress index is enabled
     */
    public boolean isCompressIndexEnabled() {
        return configStore.getIndexCompressThreshold()!=0 || configStore.getMaxPercentageSimilarWord()!=0;
    }

    /**
     * Delete all image id in map
     * @param mapID Image ID to delete as key (don't care of value)
     */
    public void delete(Map<Long, Integer> mapID) {
        map.deleteAll(mapID);
    }

    /**
     * Add a new entry for a key in index
     * @param B Visual word
     * @param I Request picture
     * @param NIBT
     */
    public void put(ConcurrentHashMap<String, Long> visualWords, Long I) {

        for (Map.Entry<String, Long> entry : visualWords.entrySet()) {

            ValueStructure valueStruct = map.get(entry.getKey());
            if(!isCompressIndexEnabled() || (isCompressIndexEnabled() && !compressIndex.isBlackListed(entry.getKey()))) {
                if (valueStruct == null) {
                    //if key does not exist, create it
                    valueStruct = new ValueStructure(configStore);
                }

                //add entry in the collection map with key
                if(addListIndex(valueStruct, I, entry.getValue().intValue()))
                {
                    //===>if(compressIndex.isTooBig(valueStruct.getNBT())) { }
                    //if value is modified,
                    //replace collection map with key
                    if(!isCompressIndexEnabled() || !compressIndex.isNBTTooBig(valueStruct.getNBT())) {
                        map.put(entry.getKey(), valueStruct);
                    }
                    else {
                        //logger.info("#############BLACKLIST:"+entry.getKey());
                        compressIndex.blacklistVW(entry.getKey());
                        map.delete(entry.getKey());
                    }

                }
            }
        }
   
    }

    /**
     * Get NBT value map with map visualWord
     * @param visualWord Visual Words
     * @return NBT for each Visual Words
     */
    public ConcurrentHashMap<String, Long> getNBT(ConcurrentHashMap<String, Long> visualWord) {
         return map.fillAllEntry(visualWord);
    }

    /**
     * Get all entry from index for these visualwords B
     * @param B Visualwords to retrieve
     * @return Map with each VW as key and its value as value
     */
    @Override
    public Map<String, ValueStructure> getAll(List<String> B) {
        return map.getAll(B);
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


    public Map<String,Map<String,ValueStructure>> getAll(Map<String,List<String>> keysForTV) {
        return null;
    }
}
