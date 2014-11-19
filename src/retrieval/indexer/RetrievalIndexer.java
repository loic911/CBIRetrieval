/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.indexer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrieval.dist.NotValidMessageXMLException;
import retrieval.exception.CBIRException;
import retrieval.storage.index.NoValidPictureException;
import retrieval.utils.FileUtils;

/**
 * Interface for a retrieval indexer.
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
    private Boolean synchronous;
    
    
    public RetrievalIndexer(boolean synchronous) {
        this.synchronous = synchronous;
    }
    
    public boolean isSynchronous() {
        return synchronous;
    }
    
    
  /**
     * This function insert a picture on a CBIR storage
     * @param file Local file to index
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
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
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
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
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
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
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(File file, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(FileUtils.readPictureFromPath(file),id,properties);
    }      
    
  /**
     * This function insert a picture on a CBIR storage
     * @param url image url
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(URL url) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(url,null,null);
    }
    
  /**
     * This function insert a picture on a CBIR storage
     * @param url image url
     * @param properties Properties to store for the image
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(URL url, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(url,null,properties);
    }  
    
  /**
     * This function insert a picture on a CBIR storage
     * @param url image url
     * @param id Resource id in server
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(URL url, Long id) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(url,id,null);
    }    
    
  /**
     * This function insert a picture on a CBIR storage
     * @param url image url
     * @param id Resource id in server
     * @param properties Properties to store for the image
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(URL url, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return index(FileUtils.readPictureFromUrl(url),id,properties);
    }    
    
   /**
     * This function insert a picture on a CBIR storage
     * @param image Image to index
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(BufferedImage image) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException{
        return indexToStorage(image,null,null);
    }     
    
  /**
     * This function insert a picture on a CBIR storage
     * @param image Image to index
     * @param properties Properties to store for the image
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(BufferedImage image, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException{
        return indexToStorage(image,null,properties);
    }          
    
  /**
     * This function insert a picture on a CBIR storage
     * @param fimage Image to index
     * @param id Resource id in server
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
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
     * @return Absolute file Path
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */    
    public Long index(BufferedImage image, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException {
        return indexToStorage(image,id,properties);
    }     
    
    protected abstract Long indexToStorage(BufferedImage image, Long id, Map<String,String> properties) throws IOException, NoValidPictureException, NotValidMessageXMLException, CBIRException;
    
     /**
     * This function delete a list of image on a storage. 
     * Images are just removed from results, but they are still in index. 
     * You need to run purge (VERY HEAVY OP!) when server will ne be used (during night,...) to clean all index data. 
     * @param server Server where to delete pictures
     * @param lists List of files to delete
     * @return Map with all deleted files
     * @throws IOException Cannot make a correct connection with server
     * @throws NotValidMessageXMLException Bad message format
     * @throws CBIRException Error from server
     */   
    public abstract Map<Long, CBIRException> delete(List<Long> ids) throws IOException, NotValidMessageXMLException, CBIRException;
     public Map<Long, CBIRException> delete(Long id) throws IOException, NotValidMessageXMLException, CBIRException {
        List<Long> list = new ArrayList<Long>();
         if (id != null) {
             list.add(id);
        }        
        return delete(list);       
    } 
      
    /**
     * This function ask to a server information about indexed pictures on the storage
     * @param server server
     * @param filesPaths
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
     * Clean index from server with deleted pictures data
     */
    public abstract void purge() throws Exception;
          
}