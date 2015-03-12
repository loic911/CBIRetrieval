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
package retrieval.storage.index;

import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.AlreadyIndexedException;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;
import retrieval.storage.exception.StartIndexException;
import retrieval.storage.index.patchs.KyotoCabinetPatchsIndex;
import retrieval.storage.index.patchs.PicturePatchsIndex;
import retrieval.storage.index.patchs.RedisPatchsIndex;
import retrieval.storage.index.patchs.SimpleHashMapPatchsIndex;
import retrieval.storage.index.properties.KyotoCabinetPropertiesIndexSingleFile;
import retrieval.storage.index.properties.PicturePropertiesIndex;
import retrieval.storage.index.properties.RedisPropertiesIndex;
import retrieval.storage.index.properties.SimpleHashMapPropertiesIndex;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private PicturePropertiesIndex picturePathIndex;
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

    private ConfigServer configStore;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(PictureIndex.class);

    /**
     * Only way to get Picture Index (which is a singleton)
     * @param idServer
     * @param cs Configuration object
     * @param globalDatabase
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
        this.configStore = configStore;
        if (configStore.getStoreName().equals("MEMORY")) {
            picturePathIndex = new SimpleHashMapPropertiesIndex(globalDatabase,idServer);
            picturePatchsIndex =  new SimpleHashMapPatchsIndex(false);
        } else if (configStore.getStoreName().equals("KYOTOSINGLEFILE")){
            picturePathIndex = new KyotoCabinetPropertiesIndexSingleFile(globalDatabase,idServer);
            picturePatchsIndex = new KyotoCabinetPatchsIndex(globalDatabase,idServer);
       }else if (configStore.getStoreName().equals("REDIS")){
            picturePathIndex = new RedisPropertiesIndex(globalDatabase,idServer);
            picturePatchsIndex = new RedisPatchsIndex(globalDatabase,idServer);
        }else
            throw new StartIndexException(configStore.getStoreName() + " is not implemented for metadata index");
    }

    /**
     * Delete pictures files from picture index
     * @param ids Pictures files
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
     * @param id Image id
     * @param properties Image properties
     * @param fullPathName Picture
     * @param numberOfPatch Number of patch (N)
     * @return Picture id
     * @throws AlreadyIndexedException Picture path is already in index
     */
    public synchronized Long putPictureAsIndexed(Long id,Map<String,String> properties, int numberOfPatch, String storage)
            throws AlreadyIndexedException {
             //logger.info("putPictureAsIndexed="+fullPathName);
        if (picturePathIndex.containsPicture(id)) {
            throw new AlreadyIndexedException("Key already exist in picture index");
        }
        
        if(properties==null) {
            properties = new HashMap<String,String>();
        }
        properties.put("id",id+"");
        properties.put("storage",storage);
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
        return configStore.getNumberOfPatch();//picturePatchsIndex.get(id);
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

