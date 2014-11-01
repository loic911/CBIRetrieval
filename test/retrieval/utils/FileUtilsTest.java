/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lrollus
 */
public class FileUtilsTest extends TestUtils{
    
    public FileUtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        enableLog();
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
     * Test of listFiles method, of class FileUtils.
     */
    @Test
    public void testListFiles_File_List() {
        System.out.println("listFiles");
        File f = new File(TestUtils.LOCALPICTUREPATH);
        List<String> v = new ArrayList<String>();
        FileUtils.listFiles(f, v);
        assert v.size() >= 8; //may hidden files...
    }

    /**
     * Test of listFiles method, of class FileUtils.
     */
    @Test
    public void testListFiles_3args() {
        System.out.println("listFiles");
        
        File f = new File(TestUtils.LOCALPICTUREPATH);
        List<String> v = new ArrayList<String>();
        FileUtils.listFiles(f, v,TestUtils.format);
        System.out.println("v.size():"+v.size());
        
        for(String item : v) {
            System.out.println(item);
        }
        
        
        assertEquals(8,v.size()); //may no be hidden files...
    }
}
