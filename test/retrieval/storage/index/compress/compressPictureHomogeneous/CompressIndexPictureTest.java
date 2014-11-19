/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressPictureHomogeneous;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author lrollus
 */
public class CompressIndexPictureTest {

    public CompressIndexPictureTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isCompessEnabled method, of class CompressIndexPicture.
     */

    @Test
    public void testIsCompessEnabled() {
        System.out.println("isCompessEnabled");
        CompressIndexPicture instance = new CompressIndexPicture(0.1d);
        assertEquals(true, instance.isCompessEnabled());
        instance = new CompressIndexPicture(0d);
        assertEquals(false, instance.isCompessEnabled());
    }

    @Test
    public void testGetOccurrenceOfBiggestVisualWordForEachVector() {
        System.out.println("getOccurrenceOfBiggestVisualWordForEachVector");
        List<ConcurrentHashMap<String, Long>> visualWords = new ArrayList<ConcurrentHashMap<String, Long>>();

        ConcurrentHashMap<String, Long> map1 = new ConcurrentHashMap<String, Long>();
        map1.put("a", 1L);
        map1.put("b", 2L);
        map1.put("c", 3L);
        visualWords.add(map1);
        ConcurrentHashMap<String, Long> map2 = new ConcurrentHashMap<String, Long>();
        map2.put("x", -1L);
        map2.put("y", -2L);
        map2.put("z", -3L);
        visualWords.add(map2);


        CompressIndexPicture instance = new CompressIndexPicture(0.1d);
        long[] expResult = {3L,-1L};
        long[] result = instance.getOccurrenceOfBiggestVisualWordForEachVector(visualWords);
        System.out.println(result[0] + " " + result[1]);
        assertEquals(expResult.length, result.length);
        for(int i = 0; i<expResult.length;i++) {
            assertEquals(expResult[i],result[i]);
        }
    }

    @Test
    public void testHasPictureToMuchSimilarWord() {
        System.out.println("hasPictureToMuchSimilarWord");
        long[] occVisualWordByVector = {75,50,60,15,5};
        int N = 100;
        int T = 5;
        CompressIndexPicture instance = new CompressIndexPicture(0.4d);
        boolean result = instance.hasPictureToMuchSimilarWord(occVisualWordByVector, N, T);
        assertEquals(true, result);
        
        instance = new CompressIndexPicture(0.8d);
        result = instance.hasPictureToMuchSimilarWord(occVisualWordByVector, N, T);
        assertEquals(false, result);

        instance = new CompressIndexPicture(0.70d);
        result = instance.hasPictureToMuchSimilarWord(occVisualWordByVector, N, T);
        assertEquals(false, result);

    }




}