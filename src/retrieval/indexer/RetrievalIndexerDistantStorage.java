package retrieval.indexer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.jdom.Document;
import retrieval.dist.*;
import retrieval.exception.CBIRException;
import retrieval.storage.Storage;
import retrieval.storage.exception.InvalidPictureException;
import retrieval.storage.exception.PictureTooHomogeneous;
import retrieval.storage.exception.TooMuchIndexRequestException;
import retrieval.utils.NetworkUtils;

/**
 * Retrieval indexer for a distant RetrievalServer (a server).
 * A retrieval indexer can index, delete, get data on a RetrievalServer.
 * The retrieval server must be a server (use host/port)
 * @author Rollus Loic
 */
public class RetrievalIndexerDistantStorage extends RetrievalIndexer {
    
    /**
     * RetrievalServer host
     */
    private String host;
    
    /**
     * RetrievalServer port
     */    
    private int port;
    
    /**
     * RetrievalServer storage
     */        
    public String storage;
    
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(RetrievalIndexerDistantStorage.class);

    public RetrievalIndexerDistantStorage(String host, int port, String storage) {
        super(false);
        this.host = host;
        this.port = port;
    }
    
    public RetrievalIndexerDistantStorage(String host, int port, String storage, boolean synchronous) {
        super(synchronous);
        this.storage = storage;
        this.host = host;
        this.port = port;
    }    
    
    public void purge() throws Exception {
        Socket server = new Socket(host, port);
        MultiServerMessageAction message = new MultiServerMessageAction(MultiServerMessageAction.PURGE);
        NetworkUtils.writeXmlToSocket(server, message.toXML());
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
    public Long indexToStorage(BufferedImage image, Long id, Map<String,String> properties) throws IOException, NotValidMessageXMLException, CBIRException {
        
        try {
        Socket server = new Socket(host, port);     
        
        MultiServerMessageIndex message = new MultiServerMessageIndex(id,properties, storage,isSynchronous());
        Document doc = message.toXML();

        NetworkUtils.writeXmlToSocket(server, doc,image);
        
        //read reponse
        Document responsexml = NetworkUtils.readXmlFromSocket(server);

        //check if error message, if true, throw exception
        if (MessageError.isErrorMessage(responsexml)) {
            throw MessageError.getException(responsexml);
        }

        //create result message and return pictures lists
        MultiServerMessageIndexResults msgIndex = new MultiServerMessageIndexResults(responsexml);
        TreeMap<Long, CBIRException> map = msgIndex.getAllPicturesFlat();
        
        Long returnId = null;
        Iterator<Entry<Long,CBIRException>> it = map.entrySet().iterator();
        while(it.hasNext()) {
            Entry<Long,CBIRException> entry = it.next();
            if(entry.getValue().isNotAnException()) {
                returnId = entry.getKey();
            } else {
                throw entry.getValue();
            }
            
        }
        
        if(returnId==null) {
            throw new CBIRException("Undefined","Image cannot be indexed!");
        }
        
        server.close();
        
        return returnId; 
        } catch(Exception e) {
            throw new CBIRException(e.toString());
        }
    }    
    
    public Map<Long, CBIRException> delete(List<Long> ids) throws IOException, NotValidMessageXMLException, CBIRException {
        try {
        Socket server = new Socket(host, port);
        MultiServerMessageDelete message = new MultiServerMessageDelete(ids,new ArrayList<String>());
        Document doc = message.toXML();

        NetworkUtils.writeXmlToSocket(server, doc);
        
        //read reponse
        Document responsexml = NetworkUtils.readXmlFromSocket(server);

        //check if error message, if true, throw exception
        if (MessageError.isErrorMessage(responsexml)) {
            throw MessageError.getException(responsexml);
        }

        //create result message and return pictures lists
        MultiServerMessageIndexResults msgIndex = new MultiServerMessageIndexResults(responsexml);
        TreeMap<Long, CBIRException> map = msgIndex.getAllPicturesFlat();
        server.close();
        return map; 
        } catch(Exception e) {
            throw new CBIRException(e.toString());
        }
    }

      
      
      public Map<Long, Map<String,String>> listPictures() throws IOException, NotValidMessageXMLException, CBIRException{
        try {
            Socket server = new Socket(host, port);
            MultiServerMessageAction message = new MultiServerMessageAction(MultiServerMessageAction.INFOS);
            Document doc = message.toXML();
            NetworkUtils.writeXmlToSocket(server, doc);

            //read reponse
            Document responsexml = NetworkUtils.readXmlFromSocket(server);
            //check if error message, if true, throw exception
            if (MessageError.isErrorMessage(responsexml)) {
                throw MessageError.getException(responsexml);
            }

            //create result message and return pictures lists
            MultiServerMessageInfos msgIndex = new MultiServerMessageInfos(responsexml);
            server.close();
            return msgIndex.getAllPictures();               
        } catch(Exception e) {
            throw new CBIRException(e.toString());
        }
 
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
        return null;
    }  
}
