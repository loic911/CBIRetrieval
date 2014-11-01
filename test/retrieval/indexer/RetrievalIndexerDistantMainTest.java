/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.indexer;

import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.*;
import retrieval.utils.TestUtils;
import retrieval.config.ConfigServer;
import retrieval.indexer.main.RetrievalDeleteMain;
import retrieval.indexer.main.RetrievalIndexMain;
import retrieval.indexer.main.RetrievalInfoMain;
import retrieval.indexer.main.RetrievalPurgeMain;
import retrieval.multiserver.MultiServer;

/**
 *
 * @author lrollus
 */
public class RetrievalIndexerDistantMainTest extends TestUtils {
    
    MultiServer multiServer;
    ConfigServer config;
    
    private static Logger logger = Logger.getLogger(RetrievalIndexerDistantMainTest.class);
    
    public RetrievalIndexerDistantMainTest() {
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
        try {
            config = new ConfigServer("config/ConfigServer.prop");
            config.setStoreName("MEMORY");
            System.out.println("server");
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
        multiServer.createServer(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",container,LOCALPICTURE1,"sync"};
        RetrievalIndexMain.main(args);
        assertEquals(1, multiServer.getServer(container).getNumberOfItem());      
    }
    
    @Test
    public void testMultiIndexerMainIndexLocalPictureWithID() throws Exception {
        String container = "test";
        multiServer.createServer(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",container,LOCALPICTURE1,"sync","123"};
        RetrievalIndexMain.main(args);
        assertEquals(1, multiServer.getServer(container).getNumberOfItem()); 
        assertNotNull(multiServer.getServer(container).getImage(123));   
    }    
    
    @Test
    public void testMultiIndexerMainIndexLocalPictureWithIDAndProperties() throws Exception {
        String container = "test";
        String key1 = "key";
        String key2 = "hel";
        String value1 = "value";
        String value2 = "lo";
        multiServer.createServer(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",container,LOCALPICTURE1,"sync","123","key,hel","value,lo"};
        RetrievalIndexMain.main(args);
        assertEquals(1, multiServer.getServer(container).getNumberOfItem()); 
        assertNotNull(multiServer.getServer(container).getImageProperties(123));
        assertEquals(2, multiServer.getServer(container).getImageProperties(123).size()); 
        assertEquals(value1,multiServer.getServer(container).getImageProperties(123).get(key1));
        assertEquals(value2,multiServer.getServer(container).getImageProperties(123).get(key2));
    }       
    
    
    @Test
    public void testMultiIndexerMainIndexURLWithID() throws Exception {
        String container = "test";
        multiServer.createServer(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",container,URLPICTURENOAUTH,"sync","123"};
        RetrievalIndexMain.main(args);
        assertEquals(1, multiServer.getServer(container).getNumberOfItem()); 
        assertNotNull(multiServer.getServer(container).getImage(123));   
    }       
    
    @Test
    public void testMultiIndexerMainDelete() throws Exception {
        String container = "test";
        multiServer.createServer(container);
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",container,LOCALPICTURE1,"sync","123"};
        assertEquals(1, multiServer.getServer(container).getNumberOfItem()); 
        String[] args2 = {MULTISERVERURL,MULTISERVERPORT1+"",LOCALPICTURE1};
        RetrievalDeleteMain.main(args2);       
        assertEquals(0, multiServer.getServer(container).getNumberOfItem()); 
    }    
        
    @Test
    public void testMultiIndexerMainPurge() throws Exception {
        String container = "test";
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",container};
        RetrievalPurgeMain.main(args);        
    }    
    
        
    @Test
    public void testMultiIndexerMainInfos() throws Exception {
        String container = "test";
        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",container};
        RetrievalInfoMain.main(args);        
    }     
}
