package retrieval.indexer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.jdom.Document;
import retrieval.dist.*;
import retrieval.exception.CBIRException;
import retrieval.server.Storage;
import retrieval.server.exception.InvalidPictureException;
import retrieval.server.exception.PictureTooHomogeneous;
import retrieval.server.exception.TooMuchIndexRequestException;
import retrieval.server.index.NoValidPictureException;
import retrieval.utils.NetworkUtils;

/**
 * Retrieval indexer for a local RetrievalServer (a java object).
 * A retrieval indexer can index, delete, get data on a RetrievalServer.
 * The retrieval server must be local (use retrievalServer.getMainStorage())
 * @author Rollus Loic
 */
public class RetrievalIndexerLocalStorage extends RetrievalIndexer {
    
    /**
     * Local storage for op
     */
    private Storage storage;
   
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(RetrievalIndexerLocalStorage.class);
    
    /**
     * Build a retrieval index with a local storage
     * The asynchronous mode will be used
     * @param storage Storage
     */
    public RetrievalIndexerLocalStorage(Storage storage) {
        super(false);
        this.storage = storage;
    }
    
    /**
     * Build a retrieval index with a local storage
     * The asynchronous mode will be used
     * @param storage Storage
     * @param synchronous Asynchronous/synchronous mode.
     */    
    public RetrievalIndexerLocalStorage(Storage storage, boolean synchronous) {
        super(synchronous);
        this.storage = storage;
    } 
    
    /**
     * Change the current storage for a new one
     * @param newStorage New Storage
     */
    public void changeCurrentStorage(Storage newStorage) {
        this.storage = newStorage;
    }
    
  /**
     * This function insert a picture on a CBIR storage
     * @param image Image to index
     * @param id Resource id in server
     * @param properties Properties to store for the image
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */  
    protected Long indexToStorage(BufferedImage image, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        logger.info("index: Connexion to server in local object:"+storage);
        Long responseId = -1l;
        if(!isSynchronous()) {
            responseId = storage.addToIndexQueue(image,id,properties);
        } else {
            responseId = storage.indexPicture(image,id,properties);
        }
        return responseId;
    }
   
   /**
     * This function delete a list of image on a storage
     * Images are just removed from results, but they are still in index. 
     * You need to run purge (VERY HEAVY OP!) when server will ne be used (during night,...) to clean all index data. 
     * @param ids List of files to delete
     * @return Map with all deleted files
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */
    public Map<Long, CBIRException> delete(List<Long> ids) throws IOException, NotValidMessageXMLException, CBIRException {
        if (ids == null || ids.isEmpty()) {
            logger.error("indexDeletePictures: Picture list is empty");
            throw new CBIRException("1111", "Picture list is empty");
        }        
        return storage.deletePictures(ids);       
    }    
    
    /**
     * This function ask to a server information about indexed pictures on the server
     * @param ids Picture ids
     * @return A map with picture list and their exception (NoException if picture is well indexed)
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */
    public Map<Long, CBIRException> checkPictures(
            Map<Long, CBIRException> ids)
            throws IOException, NotValidMessageXMLException, CBIRException {
        logger.info("checkPictures: Connexion to " + storage);      
        Map<Long, CBIRException> map = storage.getInfo(ids);
        return map;
    }        
    
     /**
     * This function get all pictures indexed from server
     * @return A map with Key = picture path, value = properties
     */
    public Map<Long,Map<String,String>> listPictures(){
        logger.info("listPictures: Connexion to server in local object");
        return storage.getAllPicturesMap();
    }   
    
    /**
     * Clean index from server with deleted pictures data
     * @param server Server that will be clean
     */
    public void purge() throws Exception{
        logger.info("purge: Connexion to server in local object");
        storage.purgeIndex();       
    }
   
}
