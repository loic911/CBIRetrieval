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

import retrieval.dist.NotValidMessageXMLException;
import retrieval.exception.CBIRException;
import retrieval.storage.exception.NoValidPictureException;
import retrieval.utils.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for a retrieval indexer.
 * A retrieval indexer can index, delete, get data on a RetrievalServer.
 * The retrieval server may be local (use retrievalServer.getMainStorage()) or distant (host and port)
 * @author Rollus Loic
 */
public abstract class RetrievalIndexer {
    
    /**
     * Asynchronous/synchronous mode.
     * SYNCHRONOUS MODE IS NOT SAFE IF YOU MAKE MULTIPLE INDEX
     * When using asynchronous mode (default), picture are added on a queue
     */
    private final Boolean synchronous;
    
    
    public RetrievalIndexer(boolean synchronous) {
        this.synchronous = synchronous;
    }
    
    public boolean isSynchronous() {
        return synchronous;
    }
       
  /**
     * This function insert a picture on a CBIR storage
     * @param file Local file to index
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NoValidPictureException Image cannot be read
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(File file) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(file,null,null);
    }
    
  /**
     * This function insert a picture on a CBIR storage
     * @param file Local file to index
     * @param properties Properties to store for the image
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NoValidPictureException Image cannot be read
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(File file, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(file,null,properties);
    }      
    
  /**
     * This function insert a picture on a CBIR storage
     * @param file Local file to index
     * @param id Resource id in server
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NoValidPictureException Image cannot be read
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(File file, Long id) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(file,id,null);
    } 
      
  /**
     * This function insert a picture on a CBIR storage
     * @param file Local file to index
     * @param id Resource id in server
     * @param properties Properties to store for the image
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */    
    public Long index(File file, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(FileUtils.readPictureFromPath(file),id,properties);
    }      
    
  /**
     * This function insert a picture on a CBIR storage
     * @param url Image url
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */    
    public Long index(URL url) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(url,null,null);
    }
    
  /**
     * This function insert a picture on a CBIR storage
     * @param url Image url
     * @param properties Properties to store for the image
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */    
    public Long index(URL url, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(url,null,properties);
    }  
    
  /**
     * This function insert a picture on a CBIR storage
     * @param url Image url
     * @param id Resource id in server
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */    
    public Long index(URL url, Long id) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(url,id,null);
    }    
    
  /**
     * This function insert a picture on a CBIR storage
     * @param url Image url
     * @param id Resource id in server
     * @param properties Properties to store for the image
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */    
    public Long index(URL url, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(FileUtils.readPictureFromUrl(url),id,properties);
    }    
    
   /**
     * This function insert a picture on a CBIR storage
     * @param image Image to index
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */    
    public Long index(BufferedImage image) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException{
        return indexToStorage(image,null,null);
    }     
    
  /**
     * This function insert a picture on a CBIR storage
     * @param image Image to index
     * @param properties Properties to store for the image
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */    
    public Long index(BufferedImage image, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException{
        return indexToStorage(image,null,properties);
    }          
    
  /**
     * This function insert a picture on a CBIR storage
     * @param image Image to index
     * @param id Resource id in server
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */    
    public Long index(BufferedImage image, Long id) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException{
        return indexToStorage(image,id,null);
    }         
      
  /**
     * This function insert a picture on a CBIR storage
     * @param image Image to index
     * @param id Resource id in server
     * @param properties Properties to store for the image
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */    
    public Long index(BufferedImage image, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return indexToStorage(image,id,properties);
    }     
    
    /**
     * This function insert a picture on a CBIR storage
     * @param image Image to index
     * @param id Resource id in server
     * @param properties Properties to store for the image
     * @return Image id
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws NoValidPictureException Image cannot be read
     * @throws CBIRException Error from server
     */
    protected abstract Long indexToStorage(BufferedImage image, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException;
    
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
    public abstract Map<Long, CBIRException> delete(List<Long> ids) throws IOException, NotValidMessageXMLException, CBIRException;
    
     /**
     * This function delete a single image on a storage. 
     * Image are just removed from results, but they are still in index. 
     * You need to run purge (VERY HEAVY OP!) when server will not be used (during night,...) to clean all index data. 
     * @param id Image id
     * @return Map with all deleted files
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */     
    public Map<Long, CBIRException> delete(Long id) throws IOException, NotValidMessageXMLException, CBIRException {
        List<Long> list = new ArrayList<Long>();
        list.add(id);       
        return delete(list);       
    } 
      
    /**
     * This function ask to a server information about indexed pictures on the storage
     * @param ids List of image id to delete
     * @return A map with picture list and their exception (NoException if picture is well indexed)
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */
    public abstract Map<Long, CBIRException> checkPictures(Map<Long, CBIRException> ids) throws IOException, NotValidMessageXMLException, CBIRException;   


    /**
     * This function get all indexed pictures info from a a storage
     * @return Map of indexed pictures (path and value are metadata)
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */
    public abstract Map<Long, Map<String,String>> listPictures() throws IOException, NotValidMessageXMLException, CBIRException;
    
    /**
     * This function get all storages from a server
     * Only available for distant server.
     * @return Map with each storage and its size
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */
    public abstract Map<String,Long> listStorages() throws IOException, NotValidMessageXMLException, CBIRException;
    
    /**
     * Clean index from server with deleted pictures data
     * @throws Exception Error during purge
     */
    public abstract void purge() throws Exception;
          
}