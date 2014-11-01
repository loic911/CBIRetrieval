package retrieval.testvector;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import retrieval.server.exception.CloseIndexException;
import retrieval.server.index.PictureIndex;

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

}
