/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server.index.path;

import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import retrieval.config.ConfigServer;
import retrieval.server.ServerTestAbstract;
import retrieval.utils.TestUtils;

/**
 *
 * @author lrollus
 */
public abstract class KyotoCabinetPathIndexTestAbstract extends TestUtils{
    
    KyotoCabinetPathIndexAbstract mainIndex;
    ConfigServer config;    
    
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCountValue method, of class KyotoCabinetPathIndexAbstract.
     */
    @Test
    public void testGetCountValue() {
        System.out.println("getCountValue");
        assertEquals(0,mainIndex.getCountValue());
        mainIndex.addPicture("toto");
        assertEquals(1,mainIndex.getCountValue());
    }

    /**
     * Test of setCountValue method, of class KyotoCabinetPathIndexAbstract.
     */
    @Test
    public void testSetCountValue() {
        System.out.println("setCountValue");
        mainIndex.setCountValue(99);
        assertEquals(99,mainIndex.getCountValue());
    }

    /**
     * Test of incrCountSize method, of class KyotoCabinetPathIndexAbstract.
     */
    @Test
    public void testIncrCountSize() {
        System.out.println("incrCountSize");
        mainIndex.setCountValue(2);
        mainIndex.incrCountSize();
        assertEquals(3,mainIndex.getCountValue());
    }

    /**
     * Test of decrCountSize method, of class KyotoCabinetPathIndexAbstract.
     */
    @Test
    public void testDecrCountSize() {
        System.out.println("decrCountSize");
        mainIndex.setCountValue(3);
        mainIndex.decrCountSize();
        assertEquals(2,mainIndex.getCountValue());
    }

    /**
     * Test of getSize method, of class KyotoCabinetPathIndexAbstract.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        assertEquals(0,mainIndex.getCountValue());
        mainIndex.addPicture("toto");
        assertEquals(1,mainIndex.getCountValue());
    }

//    /**
//     * Test of getPathMap method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testGetPathMap() {
//        System.out.println("getPathMap");
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        Map expResult = null;
//        Map result = instance.getPathMap();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPathList method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testGetPathList() {
//        System.out.println("getPathList");
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        List expResult = null;
//        List result = instance.getPathList();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addPicture method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testAddPicture() {
//        System.out.println("addPicture");
//        String path = "";
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        Integer expResult = null;
//        Integer result = instance.addPicture(path);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPicturePath method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testGetPicturePath() {
//        System.out.println("getPicturePath");
//        Integer imageID = null;
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        String expResult = "";
//        String result = instance.getPicturePath(imageID);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPictureId method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testGetPictureId() {
//        System.out.println("getPictureId");
//        String imagePath = "";
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        Integer expResult = null;
//        Integer result = instance.getPictureId(imagePath);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of containsPicture method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testContainsPicture_String() {
//        System.out.println("containsPicture");
//        String path = "";
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        boolean expResult = false;
//        boolean result = instance.containsPicture(path);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of containsPicture method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testContainsPicture_Integer() {
//        System.out.println("containsPicture");
//        Integer id = null;
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        boolean expResult = false;
//        boolean result = instance.containsPicture(id);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of print method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testPrint() {
//        System.out.println("print");
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        instance.print();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of delete method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testDelete() {
//        System.out.println("delete");
//        List<String> picturesPath = null;
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        Map expResult = null;
//        Map result = instance.delete(picturesPath);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of close method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testClose() throws Exception {
//        System.out.println("close");
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        instance.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of sync method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testSync() {
//        System.out.println("sync");
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        instance.sync();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLastElement method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testGetLastElement() {
//        System.out.println("getLastElement");
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        int expResult = 0;
//        int result = instance.getLastElement();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setLastElement method, of class KyotoCabinetPathIndexAbstract.
//     */
//    @Test
//    public void testSetLastElement() {
//        System.out.println("setLastElement");
//        int i = 0;
//        KyotoCabinetPathIndexTestAbstract instance = new KyotoCabinetPathIndexAbstractImpl();
//        instance.setLastElement(i);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public class KyotoCabinetPathIndexAbstractImpl extends KyotoCabinetPathIndexTestAbstract {
//    }
}
