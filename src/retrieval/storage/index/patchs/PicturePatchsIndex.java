package retrieval.storage.index.patchs;

import java.util.Map;
import retrieval.storage.exception.CloseIndexException;
/**
 * This interface allow to implement methods for a Picture Patchs Index
 * This index must map image id with their number of patchs
 * ex: [48-500;49-1000] image 48 has 500 patchs and 49 has 1000 patchs in index
 * @author Loic Rollus
 **/
public interface PicturePatchsIndex {

    /**
     * Add a new image id and its N value (number of patch extracted to index)
     * @param imageID Image I
     * @param N NI (Number of patch extracted from I to index it)
     */
    void put(Long id, Integer N);

    /**
     * Get the NI value of image I
     * @param imageID I
     * @return Number of patch extracted from I to index it
     */
    Integer get(Long id);

    /**
     * Check if index contains key
     * @param imagePath Image I
     * @return True if index contains I, else false
     */
    boolean containsKey(Long imagePath);

    /**
     * Close index
     * @throws CloseIndexException Error during index close
     */
    void close() throws CloseIndexException;

    /**
     * Delete all image ID key in index
     * @param imageID Image ID to delete (just look the key)
     */
    void delete(Map<Long,Integer> imageID);

    void sync();

}
