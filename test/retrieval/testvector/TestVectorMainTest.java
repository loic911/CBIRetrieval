/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.testvector;

import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;
import retrieval.utils.TestUtils;
import retrieval.testvector.generator.TestVectorReading;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class TestVectorMainTest extends TestUtils{
    
    public TestVectorMainTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        enableLog();
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
     * Test of write method, of class TestVectorMain.
     */
    @Test
    public void testWriteVectorTest() throws Exception {
        System.out.println("testWriteVectorTest");
        int numberOfVector = 10;
        int numberOfTest = 30;
        int xMax = 16;
        int yMax = 16;
        int firstValue = 0;
        int lastValue = 255;
        int firstPosition = 0;
        int lastPosition = 2;
        String buildDirectory = "testdata/testsvectorsBUILD/";     
        
        FileUtils.deleteAllFilesRecursively(new File(buildDirectory));
        
        TestVectorMain.write(numberOfVector, numberOfTest, xMax, yMax, 0, 0, firstValue, lastValue, firstPosition, lastPosition, buildDirectory);
        
        TestVectorListCentralServer tests = TestVectorReading.readClient(buildDirectory);
        assertEquals(numberOfVector,tests.size());
    }
    
    @Test
    public void testMainVectorTest() throws Exception {
        System.out.println("testMainVectorTest");
        int numberOfVector = 11;
        int numberOfTest = 30;
        int xMax = 16;
        int yMax = 16;
        int firstValue = 0;
        int lastValue = 255;
        int firstPosition = 0;
        int lastPosition = 2;
        String buildDirectory = "testdata/testsvectorsBUILD/";     
        
        FileUtils.deleteAllFilesRecursively(new File(buildDirectory));
        
        String[] agrs = { numberOfVector+"",numberOfTest+"", xMax+"", yMax+"",  firstValue+"", lastValue+"", firstPosition+"", lastPosition+"", buildDirectory};
        TestVectorMain.main(agrs);
        
        TestVectorListCentralServer tests = TestVectorReading.readClient(buildDirectory);
        assertEquals(numberOfVector,tests.size());        
    }
}
