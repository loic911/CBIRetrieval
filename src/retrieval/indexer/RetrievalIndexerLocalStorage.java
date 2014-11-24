/*
 * Copyright 2009-2014 the original author or authors.
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import retrieval.dist.NotValidMessageXMLException;
import retrieval.exception.CBIRException;
import retrieval.storage.Storage;
import retrieval.storage.index.NoValidPictureException;

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
    private static final Logger logger = Logger.getLogger(RetrievalIndexerLocalStorage.class);
    
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
     * @throws NoValidPictureException Picture is not a valid image
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */  
    protected Long indexToStorage(BufferedImage image, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        logger.info("index: Connexion to server in local object:"+storage);
        Long responseId;
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
     * You need to run purge (VERY HEAVY OP!) when server will not be used (during night,...) to clean all index data. 
     * @param ids List of files to delete
     * @return Map with all deleted files
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */
    public Map<Long, CBIRException> delete(List<Long> ids) throws IOException, NotValidMessageXMLException, CBIRException {     
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
    public Map<Long, CBIRException> checkPictures(Map<Long, CBIRException> ids) 
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
     * @throws Exception Error during the purge
     */
    public void purge() throws Exception{
        logger.info("purge: Connexion to server in local object");
        storage.purgeIndex();       
    }

    @Override
    /**
     * This function get all storages from a server
     * Only available for distant server.
     * @return Map with each storage and its size
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */     
    public Map<String, Long> listStorages() throws IOException, NotValidMessageXMLException, CBIRException {
        throw new UnsupportedOperationException("Not supported. Use RetrievalServer.getServerList()"); //To change body of generated methods, choose Tools | Templates.
    }
   
}
