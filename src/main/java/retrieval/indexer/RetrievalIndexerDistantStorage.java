/*
 * Copyright 2015 ROLLUS Loïc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval.indexer;

import org.apache.log4j.Logger;
import org.jdom.Document;
import retrieval.dist.*;
import retrieval.exception.CBIRException;
import retrieval.utils.NetworkUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.Map.Entry;

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
    private final String host;
    
    /**
     * RetrievalServer port
     */    
    private final int port;
    
    /**
     * RetrievalServer storage
     */        
    public String storage;
    
    /**
     * Logger
     */
    private final static Logger logger = Logger.getLogger(RetrievalIndexerDistantStorage.class);
 
    /**
     * Build an indexer for a distant server
     * @param host RetrievalServer host
     * @param port RetrievalServer port
     * @param storage Storage from server
     * @param synchronous Async/Sync mode (only for index)
     */
    public RetrievalIndexerDistantStorage(String host, int port, String storage, boolean synchronous) {
        super(synchronous);
        this.storage = storage;
        this.host = host;
        this.port = port;
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
        
        MultiServerMessageIndex message = new MultiServerMessageIndex(id,properties, storage,!isSynchronous());
        logger.debug(message);
        Document doc = message.toXML();

        NetworkUtils.writeXmlToSocket(server, doc);
        NetworkUtils.writeXmlToSocket(server, image);
        
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
    
     /**
     * This function delete a list of images on a storage. 
     * Images are just removed from results, but they are still in index. 
     * You need to run purge (VERY HEAVY OP!) when server will not be used (during night,...) to clean all index data. 
     * @param ids List of image id to delete
     * @return Map with all deleted files
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */      
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

        return null; 
        } catch(Exception e) {
            throw new CBIRException(e.toString());
        }
    }

    /**
     * This function get all indexed pictures info from a a storage
     * @return Map of indexed pictures (path and value are metadata)
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */      
      public Map<Long, Map<String,String>> listPictures() throws IOException, NotValidMessageXMLException, CBIRException{
        try {
            Socket server = new Socket(host, port);
            MultiServerMessageAction message = new MultiServerMessageAction(MultiServerMessageAction.INFOS,storage);
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
     * This function ask to a server information about indexed pictures on the storage
     * @param ids List of image id to delete
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

    /**
     * This function get all storages from a server
     * Only available for distant server.
     * @return Map with each storage and its size
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Map<String, Long> listStorages() throws IOException, NotValidMessageXMLException, CBIRException {
         try {
            Socket server = new Socket(host, port);
            MultiServerMessageAction message = new MultiServerMessageAction(MultiServerMessageAction.STORAGES,storage);
            Document doc = message.toXML();
            NetworkUtils.writeXmlToSocket(server, doc);

            //read reponse
            Document responsexml = NetworkUtils.readXmlFromSocket(server);
            //check if error message, if true, throw exception
            if (MessageError.isErrorMessage(responsexml)) {
                throw MessageError.getException(responsexml);
            }

            //create result message and return pictures lists
            MultiServerMessageStorages msgIndex = new MultiServerMessageStorages(responsexml);
            server.close();
            return msgIndex.getAllStorages();               
        } catch(Exception e) {
            throw new CBIRException(e.toString());
        }       
    }
    
    /**
     * Clean index from server with deleted pictures data
     * @throws Exception Error during purge
     */    
    public void purge() throws Exception {
        Socket server = new Socket(host, port);
        MultiServerMessageAction message = new MultiServerMessageAction(MultiServerMessageAction.PURGE);
        NetworkUtils.writeXmlToSocket(server, message.toXML());
    }
        
}
