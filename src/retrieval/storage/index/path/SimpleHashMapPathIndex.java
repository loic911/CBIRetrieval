package retrieval.storage.index.path;

import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;
import java.util.*;
import org.apache.log4j.*;

/**
 * Picture index path in memory
 * This index must map image id with their path and their path with their id
 * ex: /home/user/mypict.jpg - 15 | 15 - /home/user/mypict.jpg
 *     /home/user/otherPict.jpg - 17 | 17 - /home/user/mypict.jpg
 * @author Rollus Loic
 */
public class SimpleHashMapPathIndex implements PicturePathIndex {

    /**
     * Map: id picture - path
     */
    protected Map<Long, Map<String,String>> map;

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(SimpleHashMapPathIndex.class);

    /**
     * Constructor for a path map
     * @param read Not used
     * @throws ReadIndexException Error during the index read
     */
    public SimpleHashMapPathIndex(
            boolean read)
            throws ReadIndexException {
        logger.info("SimpleHashMapPatchsIndex: start");
        if (!read) {
            logger.info("SimpleHashMapPatchsIndex: start");
            map = new HashMap<Long, Map<String,String>>();
        } else {
            throw new ReadIndexException("SimpleHashMapPatchsIndex: bot implemented");
        }
    }
    
    public Map<Long, Map<String,String>> getMap() {
        return map;
    }
    
    public Map<String,String> getPictureProperties(Long id) {
        return map.get(id);
    }

    /**
     * Delete all path from picturesPath list and get their id
     * @param picturesPath Pictures paths that mus be delete
     * @return Pictures paths deleted id
     */
    public Map<Long, Integer> delete(List<Long> ids) {
        Map<Long, Integer> picturesID = new HashMap<Long, Integer>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            //logger.info("delete: " + ids.get(i));
            Object o = map.get(ids.get(i));
           // logger.info("delete: id=" + id);
            if(o!=null) {
                logger.info("delete: " + ids.get(i));
                picturesID.put(ids.get(i), 0);
                map.remove(ids.get(i));
            }
        }
        return picturesID;
    }

    /**
     * Get the size of the map
     * @return Size of the map
     */
    public int getSize() {
        return this.map.size();
    }

    /**
     * Add a new picture path, generate a new id and get the id
     * @param path Picture path
     * @return Picture ID
     */
    public Long addPicture(Long id, Map<String,String> properties) {
        map.put(id, properties);
        Date date = Calendar.getInstance().getTime();
        logger.info(";"+date.getTime() + ";" +"" + id + ";" + properties);
        return id;
    }
//
//    /**
//     * Get a picture path from the image ID
//     * @param imageID Image id
//     * @return Picture path
//     */
//    public String getPicturePath(Integer imageID) {
//        return map.get(imageID);
//    }

//    /**
//     * Get a picture id from a picture path
//     * @param imagePath Picture path
//     * @return Picture id
//     */
//    public Integer getPictureId(String imagePath) {
//        return mapReverse.get(imagePath);
//    }

    /**
     * Check if map contains picture path
     * @param path Picture path
     * @return True if map contains picture path, else false
     */
//    public boolean containsPicture(String path) {
//        return mapReverse.containsKey(path);
//    }
    public boolean containsPicture(Long id) {
        return map.containsKey(id);
    }
//    
//    /**
//     * Get a map with all pictures
//     * @return All pictures map
//     */
//    public Map<String, Integer> getPathMap() {
//        return mapReverse;
//    }
//    
    /**
     * Get a list with all pictures
     * @return All pictures list
     */
    public List<Long> getIdsList() {
        List<Long> pictures = new ArrayList<Long>(map.size());
        for(Long key : map.keySet()) {
            pictures.add(key);
        }
        return pictures;
    }    

    /**
     * Close index
     * @throws CloseIndexException Exception during the close
     */
    public void close() throws CloseIndexException {
        //only in memory, not storage needed
    }

    public void sync()
    {
        
    }
}
