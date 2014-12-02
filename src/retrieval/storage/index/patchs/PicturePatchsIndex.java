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
package retrieval.storage.index.patchs;

import java.util.Map;
import retrieval.storage.exception.CloseIndexException;
/**
 * This interface allow to implement methods for a Picture Patchs Index
 * This index must map image id with their number of patchs
 * ex: [48-500;49-1000] image 48 has 500 patchs and 49 has 1000 patchs in index
 * @author Loic Rollus
 **/
public interface PicturePatchsIndex {

    /**
     * Add a new image id and its N value (number of patch extracted to index)
     * @param id Image I
     * @param N NI (Number of patch extracted from I to index it)
     */
    void put(Long id, Integer N);

    /**
     * Get the NI value of image I
     * @param id I
     * @return Number of patch extracted from I to index it
     */
    Integer get(Long id);

    /**
     * Close index
     * @throws CloseIndexException Error during index close
     */
    void close() throws CloseIndexException;

    /**
     * Delete all image ID key in index
     * @param imageID Image ID to delete (just look the key)
     */
    void delete(Map<Long,Integer> imageID);

    void sync();

}
