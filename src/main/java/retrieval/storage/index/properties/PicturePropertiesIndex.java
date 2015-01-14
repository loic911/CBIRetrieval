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
import java.util.List;
import java.util.Map;
import retrieval.storage.exception.CloseIndexException;

/**
 * This interface allow to implement methods for a Picture index
 * Format:
 * id ===> properties
 * @author Loic Rollus
 **/
public interface PicturePropertiesIndex {

    /**
     * Add a new picture path, generate a new id and get the id
     * @param id Picture id
     * @param properties Picture metadata
     * @return Picture ID
     */
    Long addPicture(Long id, Map<String,String> properties);

    /**
     * Get a picture path from the image ID
     * @param id Image id
     * @return Picture path
     */
    Map<String,String> getPictureProperties(Long id);

    /**
     * Check if map contains picture path
     * @param id Picture path
     * @return True if map contains picture path, else false
     */
    boolean containsPicture(Long id);

    /**
     * Close index
     * @throws CloseIndexException Exception during the close
     */
    void close() throws CloseIndexException;

    /**
     * Get the size of the map
     * @return Size of the map
     */
    int getSize();

    /**
     * Delete all path from picturesPath list and get their id
     * @param ids Pictures ids that mus be delete
     * @return Pictures paths deleted id
     */
    Map<Long,Integer> delete(List<Long> ids);
 
    /**
     * Get a list with all pictures
     * @return All pictures list
     */    
    List<Long> getIdsList();
    
    
    Map<Long, Map<String,String>> getMap(); 

    void sync();


}
