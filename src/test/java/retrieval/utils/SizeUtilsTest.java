/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.utils;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author lrollus
 */
public class SizeUtilsTest {

    public SizeUtilsTest() {
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
     * Test of computeThumbSize method, of class SizeUtils.
     */
    @Test
    public void testComputeThumbSize() {
        System.out.println("computeThumbSize");
        SizeUtils instance = new SizeUtils(50, 50, 100, 75);
        SizeUtils result = instance.computeThumbSize();
        assertEquals(75, result.getWidth());
        assertEquals(75, result.getHeight());

        instance = new SizeUtils(100, 50, 50, 50);
        result = instance.computeThumbSize();
        assertEquals(50, result.getWidth());
        assertEquals(25, result.getHeight());

        instance = new SizeUtils(50, 100, 50, 50);
        result = instance.computeThumbSize();
        assertEquals(25, result.getWidth());
        assertEquals(50, result.getHeight());
    }

}