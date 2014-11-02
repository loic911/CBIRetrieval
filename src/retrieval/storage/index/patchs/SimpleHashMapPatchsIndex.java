package retrieval.storage.index.patchs;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;

/**
 * Patchs Index implemented with a simple HashMap for memory use only
 * (Use BDBPatchIndex to support persistant storage)
 * @author Rollus Loic
 */
public class SimpleHashMapPatchsIndex implements PicturePatchsIndex {

    /**
     * Store map
     */
    protected Map<Long, Integer> map;

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(SimpleHashMapPatchsIndex.class);

    /**
     * Constructor to build a Patchs Map in Memory
     * @param read No effect (just memory, don't read anything)
     * @throws ReadIndexException Error during the Read
     */
    public SimpleHashMapPatchsIndex(
            boolean read)
            throws ReadIndexException {
        logger.info("SimpleHashMapPatchsIndex: start");
        if (!read) {
            logger.info("SimpleHashMapPatchsIndex: start");
            map = new HashMap<Long, Integer>();
        } else {
            //no read/write on disk support, use bdb map
            map = new HashMap<Long, Integer>();
        }
    }

    /**
     * Delete all image ID key in index
     * @param picturesID Image ID to delete (just look the key)
     */
    public void delete(Map<Long,Integer> picturesID)
    {
        for (Map.Entry<Long, Integer> entry : picturesID.entrySet()) {
            map.remove(entry.getKey());
        }
    }

    /**
     * Add a new image id and its N value (number of patch extracted to index)
     * @param imageID Image I
     * @param N NI (Number of patch extracted from I to index it)
     */
    public void put(Long imageID, Integer N) {
        map.put(imageID, N);
    }

    /**
     * Get the NI value of image I
     * @param imageID I
     * @return Number of patch extracted from I to index it
     */
    public Integer get(Long imageID) {
        Integer numberOfPatch = map.get(imageID);
        if (numberOfPatch==null) {
            return -1;
        }
        else {
            return numberOfPatch;
        }
    }

    /**
     * Check if index contains key
     * @param imagePath Image I
     * @return True if index contains I, else false
     */
    public boolean containsKey(Long id) {
        return map.containsKey(id);
    }

    /**
     * Close index
     * @throws CloseIndexException Error during index close
     */
    public void close() throws CloseIndexException {
        //just in memory, no close, write...

    }

    public void sync() {
       
    }
}
