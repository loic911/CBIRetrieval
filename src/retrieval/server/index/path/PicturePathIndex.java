package retrieval.server.index.path;
import java.util.*;
import retrieval.server.exception.*;

/**
 * This interface allow to implement methods for a Picture index
 * Format:
 * id ===> properties
 * @author Loic Rollus
 **/
public interface PicturePathIndex {

    /**
     * Add a new picture path, generate a new id and get the id
     * @param path Picture path
     * @return Picture ID
     */
    Long addPicture(Long id, Map<String,String> properties);

    /**
     * Get a picture path from the image ID
     * @param imageID Image id
     * @return Picture path
     */
    Map<String,String> getPictureProperties(Long id);

    /**
     * Check if map contains picture path
     * @param path Picture path
     * @return True if map contains picture path, else false
     */
    boolean containsPicture(Long id);

    /**
     * Close index
     * @throws CloseIndexException Exception during the close
     */
    void close() throws CloseIndexException;

    /**
     * Get the size of the map
     * @return Size of the map
     */
    int getSize();

    /**
     * Delete all path from picturesPath list and get their id
     * @param picturesPath Pictures paths that mus be delete
     * @return Pictures paths deleted id
     */
    Map<Long,Integer> delete(List<Long> ids);
 
    /**
     * Get a list with all pictures
     * @return All pictures list
     */    
    List<Long> getIdsList();
    
    
    Map<Long, Map<String,String>> getMap(); 

    void sync();
}
