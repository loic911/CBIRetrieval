/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.config;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author lrollus
 */
public class ConfigServerTest {
    
    public ConfigServerTest() {
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
     * Test of clone method, of class ConfigServer.
     */
    @Test
    public void testGetSet() throws Exception {
        System.out.println("testGetSet");
        ConfigServer instance = new ConfigServer("testdata/ConfigServer.prop");

        instance.setNumberOfPatch(3);
        assertEquals(3, instance.getNumberOfPatch());  
        
        instance.setResizeMethod(3);
        assertEquals(3, instance.getResizeMethod());         
        
        instance.setVectorPath("path/");
        assertEquals("path/", instance.getVectorPath());          
        
        //generic
        instance.setSizeOfPatchResizeHeight(100);
        assertEquals(100, instance.getSizeOfPatchResizeHeight());      
        instance.setSizeOfPatchResizeWidth(100);
        assertEquals(100, instance.getSizeOfPatchResizeWidth());              
        
        
        instance.setkMax(100);
        assertEquals(100, instance.getkMax());         
        instance.setSearchMax(100);
        assertEquals(100, instance.getSearchMax());         
        
        instance.setSizeOfIndexQueue(100);
        assertEquals(100, instance.getSizeOfIndexQueue());              
        
        
        instance.setStrucType(1);
        assertEquals(1, instance.getStrucType());         
        
        instance.setHashMapStartSize(50);
        assertEquals(50, instance.getHashMapStartSize());          
        
        instance.setSyncAfterImage(true);
        assertEquals(true, instance.isSyncAfterImage());        
        
        
        //memory
        instance.setMemoryStartSize(4096);
        assertEquals(4096, instance.getMemoryStartSize());
    }

   
    
}
