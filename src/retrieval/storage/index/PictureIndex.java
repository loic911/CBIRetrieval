package retrieval.storage.index;

import retrieval.storage.index.path.SimpleHashMapPathIndexTest;
import retrieval.storage.index.path.PicturePathIndex;
import retrieval.storage.index.path.KyotoCabinetPathIndexSingleFile;
import retrieval.storage.index.path.SimpleHashMapPathIndex;
import retrieval.storage.index.patchs.SimpleHashMapPatchsIndex;
import retrieval.storage.index.patchs.SimpleHashMapPatchsIndexTest;
import retrieval.storage.index.patchs.PicturePatchsIndex;
import retrieval.storage.index.patchs.KyotoCabinetPatchsIndexSingleFile;
import retrieval.storage.exception.StartIndexException;
import retrieval.storage.exception.AlreadyIndexedException;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;
import retrieval.config.*;
import retrieval.server.globaldatabase.GlobalDatabase;

/**
 * Picture index (not visual word index!)
 * Just store meta-data about indexed pictures: id, path and number of patchs.
 * @author Rollus Loic
 */
public final class PictureIndex implements Serializable {

    /**
     * Index which map image id with their path
     * and path with their image id (reverse)
     * rem: path are not use for id because string
     * are too heavy in visual word index
     */
    private PicturePathIndex picturePathIndex;
    /**
     * Index which map image id and number of patchs produce by image during
     * the indexage
     */
    private PicturePatchsIndex picturePatchsIndex;
    /**
     * Instance object for singleton pattern
     * Allow only one instanciation of PictureIndex
     */
    private static PictureIndex instance;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(PictureIndex.class);

    /**
     * Only way to get Picture Index (which is a singleton)
     * @param cs Configuration object
     * @param read Read index if already exist on disk, else delete them
     * @return Picture Index object
     * @throws StartIndexException Error during index start
     * @throws ReadIndexException Error during index read
     */
    public static PictureIndex getPictureIndex(String idServer,ConfigServer cs,GlobalDatabase globalDatabase) throws StartIndexException, ReadIndexException {
        return new PictureIndex(idServer,cs,globalDatabase);
    }
    /**
     * Constructor (private) for a picture index
     * @param configStore Configuration object
     * @param read Read index if already exist on disk, else delete them
     * @throws StartIndexException Error during index start
     * @throws ReadIndexException Error during index read
     */
    private PictureIndex(String idServer,ConfigServer configStore, GlobalDatabase globalDatabase) throws StartIndexException, ReadIndexException {
        if (configStore.getStoreName().equals("MEMORY") || configStore.getStoreName().equals("NESSDB")) {
            picturePathIndex = new SimpleHashMapPathIndex(false);
            picturePatchsIndex =  new SimpleHashMapPatchsIndex(false);
        } else if (configStore.getStoreName().equals("KYOTOSINGLEFILE")){
            picturePathIndex = new KyotoCabinetPathIndexSingleFile(globalDatabase,idServer);
            picturePatchsIndex = new KyotoCabinetPatchsIndexSingleFile(globalDatabase,idServer);
       }else if (configStore.getStoreName().equals("MEMORYTEST") || configStore.getStoreName().equals("KYOTOTEST")){
            picturePathIndex = new SimpleHashMapPathIndexTest(false);
            picturePatchsIndex =  new SimpleHashMapPatchsIndexTest(false);
        }else
            throw new StartIndexException(configStore.getStoreName() + " is not implemented for metadata index");
    }

    /**
     * Delete pictures files from picture index
     * @param files Pictures files
     * @return Map with id of pictures files
     */
    public Map<Long, Integer> delete(List<Long> ids) {
        logger.info("delete: size of list " + ids.size());
        Map<Long, Integer> picturesID = picturePathIndex.delete(ids);
        logger.info("delete: id found " + picturesID.size());
        picturePatchsIndex.delete(picturesID);
        return picturesID;
    }

    /**
     * Mark picture as indexed
     * @param fullPathName Picture
     * @param numberOfPatch Number of patch (N)
     * @return Picture id
     * @throws AlreadyIndexedException Picture path is already in index
     */
    public synchronized Long putPictureAsIndexed(Long id,Map<String,String> properties, int numberOfPatch)
            throws AlreadyIndexedException {
             //logger.info("putPictureAsIndexed="+fullPathName);
        if (picturePathIndex.containsPicture(id)) {
            throw new AlreadyIndexedException("Key already exist in picture index");
        }
        
        if(properties==null) {
            properties = new HashMap<String,String>();
        }
        picturePathIndex.addPicture(id,properties);
        picturePatchsIndex.put(id, new Integer(numberOfPatch));
        return id;
    }

    /**
     * Check if image is already indexed
     * @param fullPathName Image
     * @return True if already indexed, else False
     */
    public synchronized boolean IsAlreadyIndex(Long id) {
       return picturePathIndex.containsPicture(id);
    }
    /**
     * Get the File name of a picture id
     * @param id Picture id
     * @return Picture Name
     */
 /*   public synchronized String getPictureName(Long id) {
        return picturePathIndex.getPicturePath(id);
    }
    public synchronized int getPictureId(String uri) {
        return picturePathIndex.getPictureId(uri);
    }*/

    /**
     * Get the File path of a picture id
     * @param id Picture id
     * @return Picture path
     */
    public synchronized int getPicturePatchs(Long id) {
        return picturePatchsIndex.get(id);
    }

    /**
     * Get the size of index
     * @return Number of pictures indexed
     */
    public synchronized int getSize() {
        return picturePathIndex.getSize();
    }

    /**
     * Get all pictures indexed
     * @return Map with pictures and their id
     */
    public Map<Long, Map<String,String>> getAllPicturesMap() {
        return picturePathIndex.getMap();

    }
    
    /**
     * Get all pictures indexed
     * @return Map with pictures and their id
     */
    public List<Long> getAllPicturesList() {
        return picturePathIndex.getIdsList();

    }    
    
    public Map<String,String> getProperties(Long id) {
        return picturePathIndex.getPictureProperties(id);
    }

    /**
     * Close index
     * @throws CloseIndexException Exception during index close
     */
    public void close() throws CloseIndexException {
        picturePathIndex.close();
        picturePatchsIndex.close();
    }
    
    /**
     * Sync database memory and files
     */
    public void sync() {
        picturePathIndex.sync();
        picturePatchsIndex.sync();
    }

}

