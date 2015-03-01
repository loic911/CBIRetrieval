/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.dist;

import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author lrollus
 */
public class MultiServerMessageDeleteTest {
    
    public MultiServerMessageDeleteTest() {
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
    
    @Test
    public void testMessageErrorBuildFromException() throws Exception {
        System.out.println("testMessageErrorBuildFromException");
        List<Long> ids = new ArrayList<Long>();
        ids.add(1l);
        ids.add(2l);
        
        List<String> storages = new ArrayList<String>();
        storages.add("test1");
        storages.add("test2");        
        
        MultiServerMessageDelete msg = new MultiServerMessageDelete(ids,storages);
        msg = new MultiServerMessageDelete(msg.toXML());
        assertEquals(2,msg.getIds().size());
        assertEquals(true,msg.getIds().contains(1l));
        assertEquals(true,msg.getIds().contains(2l));
         assertEquals(2,msg.getStorages().size());
        assertEquals(true,msg.getStorages().contains("test1"));
        assertEquals(true,msg.getStorages().contains("test2")); 
        
        msg = new MultiServerMessageDelete(ids,new ArrayList<String>());
        msg = new MultiServerMessageDelete(msg.toXML());
         assertEquals(0,msg.getStorages().size());     
        
    }    

    
}
