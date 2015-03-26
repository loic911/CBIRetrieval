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
package retrieval.testvector;

import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.index.PictureIndex;
import retrieval.storage.index.ValueStructure;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tests Vectors List from central server which extends Abstract class for
 * Test vector list.
 * Implements add/get and special method for index
 * @author Rollus Loic
 */
public class TestVectorListServer extends TestVectorList {

    /**
     * Get the Tests vector server at index i
     * @param i Index
     * @return Test vector server i
     */
    @Override public TestVectorServer get(int i) {
        return (TestVectorServer) super.get(i);
    }

    /**
     * Add Tests vector server at list
     * @param tvs Test vector server
     */
    public void add(TestVectorServer tvs) {
        super.add(tvs);
    }

    /**
     * Set picture index for all tests vectors index
     * @param pi Picture index
     */
    public void setPictureIndexForAllTestVectors(PictureIndex pi) {
        for (int i = 0; i < this.size(); i++) {
            this.get(i).setPictureIndex(pi);
        }
    }

    /**
     * Add visual words from visualWords and map them with imageID in all
     * tests vectors index
     * @param visualWords Visual words
     * @param imageID Image ID
     */
    public void addVisualWords(List<ConcurrentHashMap<String, Long>> visualWords, Long imageID) {

        for (int i = 0; i < this.size(); i++) {
            this.get(i).addVisualWordsToIndex(visualWords.get(i), imageID);
            //TODO: For Redis call, hmset with all data
        }
    }

    /**
     * Delete all pictures ID in all tests vector index
     * @param picturesID Picture id
     */
    public void delete(Map<Long,Integer> picturesID)
    {
        for (int i = 0; i < this.size(); i++) {
            this.get(i).deletePictures(picturesID);
        }
    }



    public boolean isPicturePresentInIndex(Long id) {
        boolean isPresent = false;
          for (int i = 0; i < this.size(); i++) {
              if(this.get(i).isPicturePresent(id)) {
                   isPresent=true;
              }
        }
        return isPresent;
    }

    /**
     * Close index
     * @throws CloseIndexException Error during index close
     */
    public void closeIndex() throws CloseIndexException
    {
        for (int i = 0; i < this.size(); i++) {
            this.get(i).closeIndex();
        }
    }

    public void sync() {
        for (int i = 0; i < this.size(); i++) {
            this.get(i).sync();
        }
    }

    public void printStat() {
        for (int i = 0; i < this.size(); i++) {
            this.get(i).printStatIndex();
        }
    }

    public Map<String,Map<String,ValueStructure>> getAll(Map<String,List<String>> keysForTV) {
        return this.get(0).getAll(keysForTV);
    }

}
