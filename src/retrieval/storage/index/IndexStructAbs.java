package retrieval.storage.index;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import retrieval.config.ConfigServer;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.index.compress.compressNBT.CompressIndexNBT;

/**
 * Abstract class for index structure
 * Two type of index structure:
 * Kyoto/Redis/Memory/...: collection = index.get(key) than value=collection.get(subkey)
 * Redis: index.get(key,subkey,value)
 * @author lrollus
 */
public abstract class IndexStructAbs {
    
    /**
     * Name of index (test vector name)
     */
    protected String name;
    /**
     * Configuration object for storage
     */
    protected ConfigServer configStore;

    /**
     * Index to save blacklisted NBT
     */
    protected CompressIndexNBT compressIndex;

    /**
     * Get the name of index
     * @return Name of index
     */
    public String getName() {
        return name;
    }

    /**
     * Add the image I and its NBIT to the visual word B collection valList
     * @param valList Visual word collection
     * @param I Image I id
     * @param NBIT Number of patch produced by I for visual word B
     */
    protected boolean addListIndex(ValueStructure valList, Long I, int NBIT) {
        return valList.addEntry(I, NBIT);
    }

    /**
     * Delete all images with id in mapID
     * @param mapID Pictures id to delete (just key are used)
     */
    public abstract void delete(Map<Long, Integer> mapID);
       
    /**
     * Add Visual words in index for image I
     * @param visualWords Visual words build with I
     * @param I Image id
     */
    public abstract void put(ConcurrentHashMap<String, Long> visualWords, Long I);
    
    /**
     * Get NBT from index for these visualWord
     * @param visualWord Visual words
     * @return NBT
     */
    public abstract ConcurrentHashMap<String, Long> getNBT(ConcurrentHashMap<String, Long> visualWord);

    /**
     * Get all entry from index for these visualwords B
     * @param visualWord Visualwords to retrieve
     * @return Map with each VW as key and its value as value
     */
    public abstract Map<String,ValueStructure> getAll(List<String> visualWord);
    
    /**
     * Close index database
     * @throws CloseIndexException 
     */
    public abstract void closeIndex() throws CloseIndexException;
    
    /**
     * Sync memory and disc database 
     */
    public abstract void sync();
    
    /**
     * Check if a picture is still index
     * @param id Picture id
     * @return True if picture is still in index
     */
    public abstract boolean isRessourcePresent(Long id);
    
    /**
     * Print index stat 
     */
    public abstract void printStat();    
}
