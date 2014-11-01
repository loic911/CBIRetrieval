/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import retrieval.utils.CollectionUtils;
import static org.junit.Assert.*;

/**
 *
 * @author lrollus
 */
public class CollectionUtilsTest {

    public CollectionUtilsTest() {
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
     * Test of join method, of class CollectionUtils.
     */
    @Test
    public void testJoin() {
        System.out.println("join");
        Collection<String> words = new ArrayList<String>();
        words.add("a");
        words.add("bc");
        String character = ",";
        String expResult = "a,bc";
        String result = CollectionUtils.join(words, character);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testJoinArray() {
        System.out.println("join");
        String[] words = new String[]{"a", "bc"};
        String character = ",";
        String expResult = "a,bc";
        String result = CollectionUtils.join(words, character);
        assertEquals(expResult, result);
    }    

    /**
     * Test of split method, of class CollectionUtils.
     */
    @Test
    public void testSplit() {
        System.out.println("split");
        String words = "a,bc";
        String character = ",";
        List expResult = new ArrayList<String>();
        expResult.add("a");
        expResult.add("bc");
        List result = CollectionUtils.split(words, character);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSplitArray() {
        System.out.println("split");
        String words = "a,bc";
        String character = ",";
        String[] expResult = new String[]{"a", "bc"};
        String[] result = CollectionUtils.splitArray(words, character);
        assertArrayEquals(expResult, result);
    }    

    /**
     * Test of splitList method, of class CollectionUtils.
     */
    @Test
    public void testSplitList() {
        System.out.println("splitList");
        List<String> list = new ArrayList<String>();
        list.add("shouldBeIn1");
        list.add("shouldBeIn1 too");
        list.add("shouldBeIn2");
        list.add("shouldBeIn2 too");
        list.add("shouldBeIn3");

        int number = 3;

        ArrayList[] result = CollectionUtils.splitList(list, number);
        result[0].contains("shouldBeIn1");
        result[0].contains("shouldBeIn1 too");
        result[1].contains("shouldBeIn2");
        result[1].contains("shouldBeIn2 too");
        result[2].contains("shouldBeIn3");

    }

}