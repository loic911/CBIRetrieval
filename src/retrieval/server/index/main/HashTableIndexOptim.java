package retrieval.server.index.main;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import retrieval.server.index.ValueStructure;
import retrieval.server.index.compress.compressNBT.CompressIndexNBT;

/**
 * Interface which force to implements method for HashTable
 * @author Rollus Loic
 */
public abstract class HashTableIndexOptim {

    /**
     * Add a key value on the store
     * @param key Key
     * @param Value Value
     */
    //public abstract void put(String key, ValueStructure value);
    /**
     * Delete a key and i'ts value in the store
     * @param key Key
     */
    public abstract void delete(String key);
    /**
     * Get the value for a key
     * @param key Key
     * @return Value
     */
    public abstract void incrementHashValue(String mainkey, String haskey, long value);
    public abstract void incrementHashValue(ConcurrentHashMap<String, Long> visualWords, int I, CompressIndexNBT compress);
    public abstract String getHashValue(String mainkey, String haskey);
    public abstract Map<String,String> getValue(String mainkey);
   // public abstract ConcurrentHashMap<String, Long> getAllValues(ConcurrentHashMap<String, Long> result);

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
    public abstract ConcurrentHashMap<String, Long> getAllValues(ConcurrentHashMap<String, Long> result);
    /**
     * Print some information over HashTable
     */
    public abstract void printStat();





}
