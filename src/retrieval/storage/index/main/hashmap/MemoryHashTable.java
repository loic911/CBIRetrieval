package retrieval.storage.index.main.hashmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.storage.exception.ReadIndexException;
import retrieval.storage.index.ValueStructure;
import retrieval.storage.index.main.HashTableIndex;

/**
 * A simple hash table in memory
 * @author Rollus Loic
 */
public class MemoryHashTable extends HashTableIndex {

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
    public MemoryHashTable(ConfigServer configStore, boolean read) throws ReadIndexException {
        logger.debug("SimpleHashMap: start");
        this.configStore = configStore;
        try {
            logger.debug("SimpleHashMap: read old index");
             if(!read) {
                hashmap = new HashMap<String, ValueStructure>(configStore.getMemoryStartSize()); 
             } 
                 
        } catch (Exception e) {
            throw new ReadIndexException(e.toString());
        }
    }

    /**
     * Get the Hash Map
     * @return Hash Map
     */
    public synchronized Map<String, ValueStructure> getHashMap() {
        return hashmap;
    }

    /**
     * Put a key and its value on the store
     * @param key Key
     * @param Value Value
     */
    public synchronized void put(String key, ValueStructure Value) {
        hashmap.put(key, Value);
    }

    /**
     * Get a value of the key
     * @param key Key
     * @return Value
     */
    public synchronized ValueStructure get(String key) {
        return hashmap.get(key);
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
             hashmap.remove(emptyKeys.get(i));
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
        hashmap.remove(key);
    }
}
