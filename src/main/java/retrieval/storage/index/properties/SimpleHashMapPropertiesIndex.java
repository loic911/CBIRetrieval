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
package retrieval.storage.index.properties;

import org.apache.log4j.Logger;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;

import java.util.*;

/**
 * Picture index path in memory
 * This index must map image id with their path and their path with their id
 * ex: /home/user/mypict.jpg - 15 | 15 - /home/user/mypict.jpg
 *     /home/user/otherPict.jpg - 17 | 17 - /home/user/mypict.jpg
 * @author Rollus Loic
 */
public class SimpleHashMapPropertiesIndex implements PicturePropertiesIndex {
    protected String prefix;
    protected String prefixIds;
    private String storage;
    /**
     * Map: id picture - path
     */
    protected Map<String, Map<String,String>> map;

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(SimpleHashMapPropertiesIndex.class);

    /**
     * Constructor for a path map
     * @param read Not used
     * @throws ReadIndexException Error during the index read
     */
    public SimpleHashMapPropertiesIndex(GlobalDatabase global, String idServer)
            throws ReadIndexException {
        logger.info("SimpleHashMapPatchsIndex: start");
        this.prefix = GlobalDatabase.KEY_PROPERTIES_STORE + "#" + idServer + "#";
        this.prefixIds = GlobalDatabase.KEY_LIST_ID + "#" + idServer + "#";
        this.storage = idServer;
            logger.info("SimpleHashMapPatchsIndex: start");
            map = (Map<String, Map<String,String>>)global.getDatabaseProperties();
    }

    private Long convertKeysToLong(String key) {
        return Long.parseLong(key.replaceFirst(this.prefix,""));
    }
    
    public Map<Long, Map<String,String>> getMap() {
        Map<Long, Map<String,String>> result = new TreeMap<Long, Map<String,String>>();
        for(String key : map.keySet()) {
            if(key.startsWith(this.prefix)) {
                result.put(convertKeysToLong(key),getPictureProperties(convertKeysToLong(key)));
            }
        }
        return result;
    }
    
    public Map<String,String> getPictureProperties(Long id) {
        if(map.get(this.prefix+id)==null) {
            return new HashMap<String,String>();
        }
        Map<String,String> properties = map.get(this.prefix+id);
        return properties;
    }

    /**
     * Delete all path from picturesPath list and get their id
     * @param ids Pictures ids that must be delete
     * @return Pictures paths deleted id
     */
    public Map<Long, Integer> delete(List<Long> ids) {
        Map<Long, Integer> picturesID = new HashMap<Long, Integer>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            //logger.info("delete: " + ids.get(i));
            Object o = map.get(this.prefix+ids.get(i));
           // logger.info("delete: id=" + id);
            if(o!=null) {
                logger.info("delete: " + ids.get(i));
                picturesID.put(ids.get(i), 0);
                map.remove(this.prefix+ids.get(i));
                decrCountSize();
            }
        }
        return picturesID;
    }

    /**
     * Get the size of the map
     * @return Size of the map
     */
    public int getSize() {
        return this.getCountValue();
    }

    /**
     * Add a new picture path, generate a new id and get the id
     * @param id Picture id
     * @return Picture ID
     */
    public Long addPicture(Long id, Map<String,String> properties) {
        if(properties==null) {
            properties = new HashMap<String,String>();
        }
        incrCountSize();
        map.put(this.prefix+id, properties);
        Date date = Calendar.getInstance().getTime();
        logger.info(";"+date.getTime() + ";" +"" + id + ";" + properties);
        return id;
    }

    /**
     * Check if map contains picture path
     * @param id Picture id
     * @return True if map contains picture path, else false
     */
//    public boolean containsPicture(String path) {
//        return mapReverse.containsKey(path);
//    }
    public boolean containsPicture(Long id) {
        return map.containsKey(this.prefix+id);
    }
   
    /**
     * Get a list with all pictures
     * @return All pictures list
     */
    public List<Long> getIdsList() {
        List<Long> pictures = new ArrayList<Long>(map.size());
        for(String key : map.keySet()) {
            if(key.startsWith(this.prefix)) {
                pictures.add(convertKeysToLong(key));
            }
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

    public int getCountValue() {
        Map data = map.get("COUNT#" + storage);
        if(data==null) {
            return 0;
        }
        return Integer.parseInt(data.get("CBIR").toString());
    }

    public void setCountValue(long value) {
        Map<String,String> data = new HashMap<>();
        data.put("CBIR",value+"");
        map.put("COUNT#" + storage, data);
    }

    public void incrCountSize() {
        int value = getCountValue();
        value++;
        setCountValue(value);
    }

    public void decrCountSize() {
        int value = getCountValue();
        value--;
        setCountValue(value);
    }
}
