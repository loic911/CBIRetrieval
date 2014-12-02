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
package retrieval.storage.index.main;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import retrieval.storage.index.ValueStructure;

/**
 * Interface which force to implements method for HashTable
 * @author Rollus Loic
 */
public abstract class HashTableIndex {

    /**
     * Add a key value on the store
     * @param key Key
     * @param value Value
     */
    public abstract void put(String key, ValueStructure value);
    /**
     * Delete a key and it's value in the store
     * @param key Key
     */
    public abstract void delete(String key);
    /**
     * Get the value for a key
     * @param key Key
     * @return Value
     */
    public abstract ValueStructure get(String key);
    /**
     * Get each value map with each key from keys list
     * @param keys Key list
     * @return Map with key-value
     */
     public abstract Map<String,ValueStructure> getAll(List<String> keys);

    /**
     * Delete all key from mapID on the store
     * @param mapID Key that mus be removed
     */
    public abstract void deleteAll(Map<Long, Integer> mapID);
    
    /**
     * Check if the image is at least in a value from the index
     * @param id Image id
     * @return 
     */
    public abstract boolean isRessourcePresent(Long id);
    
    /**
     * Close index (write on disk, close connection,...)
     * @throws Exception Error during the index close
     */
    public abstract void closeIndex() throws Exception;

    /**
     * Synchronize store cache with disk files
     * Not needed for Memory and some Database engine
     */
    public abstract void sync();
    /**
     * Fill each entry from entries map with entries from the store
     * @param entires that must be looked on the store
     * @return A map with each value from the store
     */
    public abstract ConcurrentHashMap<String, Long> fillAllEntry(ConcurrentHashMap<String, Long> entires);
    /**
     * Print some information over HashTable
     */
    public abstract void printStat();





}
