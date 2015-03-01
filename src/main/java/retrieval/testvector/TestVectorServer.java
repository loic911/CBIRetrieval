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
package retrieval.testvector;

import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.dist.RequestPictureVisualWord;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;
import retrieval.storage.exception.StartIndexException;
import retrieval.storage.index.*;
import retrieval.storage.index.main.RedisHashTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A tests vector or Server used so it has an index
 * @author Rollus Loic
 */
public class TestVectorServer extends TestVector {

    /**
     * Picture index (map path with picture id and picture id with N)
     */
    private PictureIndex pictureIndex;
    /**
     * Index
     */
    private IndexStructAbs index;

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(TestVectorServer.class);

    /**
     * Construct a Test Vector
     * @param name Name of the test vector
     * @param pts List of Points to compare
     * @param val List of Value for the comparaison
     * @param pos List of Position (r,g,b,h,s,v) for comparaison
     * @param readIndex If True, read index files if already exists
     * @param configStore Cofiguration object
     * @throws StartIndexException Exception during the start of index
     * @throws ReadIndexException Exception during the end of index
     */
    public TestVectorServer(
            String idServer,
            String idTestVector,
            List<String> pts,
            List<String> val,
            List<String> pos,
            ConfigServer configServer, 
            Object database)
            throws StartIndexException, ReadIndexException {

        logger.debug("TestVectorServer: start");
        this.name = idTestVector;
        this.randomGenerator = new Random();
        this.tests = new ArrayList<TestPoint>(pts.size());

        logger.debug("TestVectorServer: init of index " + this.name);
        if(configServer.getStoreName().equals(RedisHashTable.NAME)) {
            this.index = new IndexStructOptim(idServer,idTestVector, configServer, Integer.parseInt(this.name),database);
        } else {
            this.index = new IndexStructClassic(idServer,idTestVector, configServer, Integer.parseInt(this.name),database);
        }

        //Create the new list of test with the pts and the val list
        logger.debug("TestVectorServer: build tests lists");
        for (int i = 0; i < pts.size(); i++) {
            tests.add(new TestPoint(
                    new PatchPoint(pts.get(i)),
                    Double.parseDouble(val.get(i)),
                    Integer.parseInt(pos.get(i))));
        }

    }

    /**
     * Set picture index for this tests vectors
     * NOT visual words index, just simple index with picture path, id and N.
     * @param pi Picture index
     */
    public void setPictureIndex(PictureIndex pi) {
        this.pictureIndex = pi;
    }

    /**
     * Setter of the Index
     * @param index Index of the test vector
     **/
    public void setIndex(IndexStructClassic index) {
        this.index = index;
    }

    /**
     * CLose index
     * @throws CloseIndexException Exception during index close
     **/
    public void closeIndex() throws CloseIndexException {
        this.index.closeIndex();
    }

    /**
     * Print stat over the index of this testVector on the system.out.println
     **/
    public void printStatIndex() {
        this.index.printStat();
    }

    public boolean isPicturePresent(Long id) {
        return index.isRessourcePresent(id);
    }
    /**
     * During the search, take a list of visualWord from request image Iq 
     * as parameter and compute similarities between Iq and each picturex in index
     * @param visualWords Visual words from Iq
     * @param NIQ Number of patchs extracted from Iq
     * @return A map with id pictures and entry value (contains similarity)
     **/
    public  ConcurrentHashMap<Long, Entry> completeSimilarityPictures(
            ConcurrentHashMap<String, RequestPictureVisualWord> visualWords, int NIQ) {

        List<String> keys = new ArrayList<String>(NIQ);

        for (Map.Entry<String, RequestPictureVisualWord> visualword : visualWords.entrySet()) {
//            if(visualword.getValue().nbtSum>0) {
                keys.add(visualword.getKey());
//            }
        }

        return completeSimilarityPictures(this.index.getAll(keys),visualWords,NIQ);
    }


    public  ConcurrentHashMap<Long, Entry> completeSimilarityPictures(
            Map<String,ValueStructure> map,
            ConcurrentHashMap<String, RequestPictureVisualWord> visualWords, int NIQ) {
        ConcurrentHashMap<Long, Entry> listPicture = new ConcurrentHashMap<Long, Entry>(2 * pictureIndex.getSize());

        for (Map.Entry<String,ValueStructure> vw : map.entrySet()) {

            /** ValueStructure list = List of pictures and occurences for the
             *  visualword visualWords.get(i)
             *  ex. [img01.jpg; 2 , img02.jpg; 5 , ...]
             **/
            RequestPictureVisualWord visualWordRequest = visualWords.get(vw.getKey());
            ValueStructure list = vw.getValue();

            if (list != null && list.getEntries().size() > 0) {
                /** merge the two list and addition the occurence number
                 * [img01.jpg; 3 , img02.jpg; 4 , ...]
                 * +
                 * [img01.jpg; 2 , img03.jpg; 1 , ...]
                 * =
                 * [img01.jpg; 5 , img02.jpg; 4 ,  img03.jpg; 1...]
                 **/
                listPicture = merge(listPicture, list.getEntries());
                /**
                 * For each picture in index which has visual word B
                 * (which was generated by Iq),
                 * compute similarities with equation 3.1 rom
                 * "Incremental Indexing and Distributed Image Search
                 * using Shared Randomized Vocabularies" (R. Marée & al)
                 */
                double NBT = visualWordRequest.nbtSum;
                double ONEOverNBT = (double) (1d / NBT);
                double NIQBT = visualWordRequest.nbiq;
                double NIQBTOverNIQ = (double) (NIQBT / (double) NIQ);
                for (Map.Entry<Long, Entry> entry : listPicture.entrySet()) {
                    Entry e = entry.getValue();
                    double NIRBT = list.getNBIT(e.getI());
                    double NIRBTOverNIR = (double) (NIRBT / e.getNumberOfPatch());
                    //Compute similarities with weighting
                    if (NIRBTOverNIR != 0) {
                        entry.getValue().addSimilarityComputation(
                                ONEOverNBT, NIQBTOverNIQ, NIRBTOverNIR);
                    }
                }
            }
        }

        return listPicture;
    }

    /**
     * Merge the two hashtable (can be view like a list)
     * and addition the occurence number
     * [img01.jpg = 3 , img02.jpg = 4 , ...]
     * +
     * [img01.jpg = 2 , img03.jpg = 1 , ...]
     * =
     * [img01.jpg = 5 , img02.jpg = 4 ,  img03.jpg = 1...]
     * @param   list1   List which will be update by list2
     * @param   list2   List where new item (not in list1) will be added to
     * list1 and existing item (already in list1) will increment correspondant
     * item in list 1
     * @return the new list
     *
     **/
    private ConcurrentHashMap<Long, Entry> merge(
            ConcurrentHashMap<Long, Entry> list1,
            Map<Long, Integer> list2) {

        for (Map.Entry<Long, Integer> entree : list2.entrySet()) {
            Entry e2 = new Entry(entree.getKey(), entree.getValue());
            boolean find = addElement(list1, e2);
            if (!find) {
                e2.setNumberOfPatch(pictureIndex.getPicturePatchs(e2.getI()));
                //if numberOfPatch=-1, picture has been delete in pictureIndex
                //but not in this index
                if (e2.getNumberOfPatch() != -1) {
                    list1.put(e2.getI(), e2);
                }
            }
        }
        return list1;
    }

    /**
     * If list1 contains the value of e2, increment e2 in list1, return true
     * If list1 does'nt contains the value of e2, do nothing, return false
     * @param   list1   A list
     * @param   e2   Element which will be
     * @return   true if element was found
     *
     **/
    private boolean addElement(ConcurrentHashMap<Long, Entry> list1, Entry e2) {
        Entry e1 = list1.get(e2.getI());
        if (e1 != null) {
            e1.incrementNIBT(e2.getNIBT());
            return true;
        }
        return false;
    }

   /**
    * Add visual words for image ID to Index
    * @param visualWords Visual words
    * @param imageID Image ID
    */
    public synchronized void addVisualWordsToIndex(ConcurrentHashMap<String, Long> visualWords, Long imageID) {
        this.index.put(visualWords,imageID);
    }

    /**
     * Delete visual words for image ID to Index
     * @param imagesID List of image ID
     */
    public synchronized void deletePictures(Map<Long, Integer> imagesID) {
        logger.debug("deletePictures:" + imagesID.size() + " item to delete");
        index.delete(imagesID);
    }

    /**
     * Fill NBT for visual words from visualWords
     * NBT is number of patch map with a visual word B for a tests
     * vector t in this server
     * @param visualWords Visual words
     * @return Visual words and their NBT
     */
    public synchronized ConcurrentHashMap<String, Long> fillNBT(
            ConcurrentHashMap<String, Long> visualWords) {
        ConcurrentHashMap<String, Long> chm = index.getNBT(visualWords);
        return chm;

    }

    public Map<String,Map<String,ValueStructure>> getAll(Map<String,List<String>> keysForTV) {
        return this.index.getAll(keysForTV);
    }

    public void sync()
    {
        index.sync();
    }
}

