/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.config;

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
public class ConfigClientTest {
    
    public ConfigClientTest() {
    }
    
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
     * Test of getNumberOfPatch method, of class ConfigClient.
     */
    @Test
    public void testGetSet() throws Exception {
        System.out.println("testGetSet");
        ConfigClient instance = new ConfigClient("testdata/ConfigClient.prop");
        instance.setNumberOfPatch(1000);
        instance.setNumberOfTV(10);
        instance.setSizeOfPatchResizeWidth(16);
        instance.setSizeOfPatchResizeHeight(16);
        instance.setTimeout(0);
        instance.setResizeMethod(3);
        instance.setVectorPath("/test");
        assertEquals(1000, instance.getNumberOfPatch());
        assertEquals(10, instance.getNumberOfTV());
        assertEquals(16, instance.getSizeOfPatchResizeWidth());
        assertEquals(16, instance.getSizeOfPatchResizeHeight());
        assertEquals(0, instance.getTimeout());
        assertEquals(3, instance.getResizeMethod());
        assertEquals("/test", instance.getVectorPath());
    }
    
}
