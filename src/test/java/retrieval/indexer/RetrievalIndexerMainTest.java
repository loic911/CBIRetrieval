/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.indexer;

import org.apache.log4j.Logger;
import org.junit.*;
import retrieval.TestUtils;
import retrieval.config.ConfigServer;
import retrieval.indexer.main.RetrievalDeleterMain;
import retrieval.indexer.main.RetrievalIndexerMain;
import retrieval.indexer.main.RetrievalPurgerMain;
import retrieval.server.RetrievalServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

    /**
     * Main methode for indexer
     * Param0: Server host
     * param1: Server port
     * Param2: Picture URI
     * Param3: (Optional) 'async' or 'sync' string
     * Param4: (Optional) Storage name 
     * Param5: (Optional) Picture id
     * Param6: (Optional) Picture properties keys (comma sep) (e.g. id,name,date)
     * Param7: (Optional) Picture properties values (comma sep) (e.g. 123,test,2014/10/31)
     * @param args Params arays
     */
public class RetrievalIndexerMainTest extends TestUtils {
    
    RetrievalServer multiServer;
    ConfigServer config;
    
    private static Logger logger = Logger.getLogger(RetrievalIndexerMainTest.class);
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        enableLog();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        try {
            config = new ConfigServer("testdata/ConfigServer.prop");
            config.setStoreName("MEMORY");
            multiServer = createMultiServer(config,MULTISERVERPORT1,4,"MEMORY");            
        } catch (Exception e) {
            logger.error(e);
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try{multiServer.stop();}catch(Exception e) {}
    }

    /**
     * Test of main method, of class MultiIndexerMain.
     */
    @Test
    public void testMultiIndexerMainIndexLocalPicture() throws Exception {
        String container = "test";
        multiServer.createStorage(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",LOCALPICTURE1,"sync",container};
        RetrievalIndexerMain.main(args);
        assertEquals(1, multiServer.getStorage(container).getNumberOfItem());      
    } 
    
    @Test
    public void testMultiIndexerMainIndexLocalPictureWithID() throws Exception {
        String container = "test";
        multiServer.createStorage(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",LOCALPICTURE1,"sync",container,"123"};
        RetrievalIndexerMain.main(args);
        assertEquals(1, multiServer.getStorage(container).getNumberOfItem()); 
        assertEquals(true,multiServer.getStorage(container).isPictureInIndex(123l));   
    }    
    
    @Test
    public void testMultiIndexerMainIndexLocalPictureWithIDAndProperties() throws Exception {
        String container = "test";
        String key1 = "key";
        String key2 = "hel";
        String value1 = "value";
        String value2 = "lo";
        multiServer.createStorage(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",LOCALPICTURE1,"sync",container,"123","key,hel","value,lo"};
        RetrievalIndexerMain.main(args);
        assertEquals(1, multiServer.getStorage(container).getNumberOfItem()); 
        assertEquals(true,multiServer.getStorage(container).isPictureInIndex(123l));
        assertEquals(TestUtils.BASE_NUMBER_OF_PROPERTIES+2, multiServer.getStorage(container).getProperties(123l).size());
        assertEquals(value1,multiServer.getStorage(container).getProperties(123l).get(key1));
        assertEquals(value2,multiServer.getStorage(container).getProperties(123l).get(key2));
    }       
    
    
    @Test
    public void testMultiIndexerMainIndexURLWithID() throws Exception {
        String container = "test";
        multiServer.createStorage(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",URLPICTURENOAUTH,"sync",container,"123"};
        RetrievalIndexerMain.main(args);
        assertEquals(1, multiServer.getStorage(container).getNumberOfItem()); 
        assertEquals(true,multiServer.getStorage(container).isPictureInIndex(123l));  
    }       
    
    @Test
    public void testMultiIndexerMainDelete() throws Exception {
        
        String container = "test";
        multiServer.createStorage(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",URLPICTURENOAUTH,"sync",container,"123"};
        RetrievalIndexerMain.main(args);
         assertEquals(1, multiServer.getStorage(container).getNumberOfItem());                
        String[] args2 = {MULTISERVERURL,MULTISERVERPORT1+"","123,456"};
        RetrievalDeleterMain.main(args2);       
        assertEquals(0, multiServer.getStorage(container).getNumberOfItem()); 
    }    
        
    @Test
    public void testMultiIndexerMainPurge() throws Exception {
        String container = "test";
        multiServer.createStorage(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",URLPICTURENOAUTH,"sync",container,"123"};
        RetrievalIndexerMain.main(args);
         assertEquals(1, multiServer.getStorage(container).getNumberOfItem());                
        String[] args2 = {MULTISERVERURL,MULTISERVERPORT1+"","123,456"};
        RetrievalDeleterMain.main(args2);       
        assertEquals(1, multiServer.getStorage(container).getNumberOfPicturesToPurge());   
        String[] args3 = {MULTISERVERURL,MULTISERVERPORT1+""};
        RetrievalPurgerMain.main(args3); 
        waitEquals(0, multiServer.getStorage(container).getNumberOfPicturesToPurge());
    }    
//    
//        
//    @Test
//    public void testMultiIndexerMainInfos() throws Exception {
//        String container = "test";
//        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",container};
//        RetrievalInfoMain.main(args);        
//    }  
}
