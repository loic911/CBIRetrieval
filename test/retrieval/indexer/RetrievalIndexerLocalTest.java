/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.indexer;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.*;
import retrieval.utils.TestUtils;
import retrieval.multicentralserver.exception.ImageNotValidException;
import retrieval.config.ConfigServer;
import retrieval.exception.CBIRException;
import retrieval.indexer.main.RetrievalIndexMain;
import retrieval.server.RetrievalServer;
import retrieval.storage.exception.PictureNotFoundException;
import retrieval.test.TestMultiServerUtils;

/**
 *
 * @author lrollus
 */
public class RetrievalIndexerLocalTest extends TestUtils {
    
    RetrievalServer multiServer;
    ConfigServer config;
    
    private static Logger logger = Logger.getLogger(RetrievalIndexerLocalTest.class);
    
    public RetrievalIndexerLocalTest() {
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
    
    @Test
    public void testMultiIndexerIndexSync() throws Exception {
        System.out.println("testMultiIndexerIndexSync");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container),true);
        Long id = index.index(new File(picture));
        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),multiServer.getSize());
    }
    
    @Test
    public void testMultiIndexerIndexSyncWithId() throws Exception {
        System.out.println("testMultiIndexerIndexSyncWithId");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container),true);
        Long id = index.index(new File(picture),5l);
        
        assertEquals(new Long(5),id);

        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),multiServer.getSize());
    }   
    
    @Test
    public void testMultiIndexerIndexSyncWithIdAnProperties() throws Exception {
        System.out.println("testMultiIndexerIndexSyncWithIdAnProperties");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container),true);
        Long id = index.index(new File(picture),null,LOCALPICTURE1MAP);

        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),multiServer.getSize());
        
        Map<String,String> map = multiServer.getServer(container).getProperties(id);
        assertEquals(2,map.size());
        for(Map.Entry<String,String> entry : LOCALPICTURE1MAP.entrySet()) {
            assertEquals(true,map.containsKey(entry.getKey()));
            assertEquals(entry.getValue(),map.get(entry.getKey()));
        }
        
    }         

    @Test
    public void testMultiIndexerIndexAsync() throws Exception {
        System.out.println("testMultiIndexerIndexAsync");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container),false);
        Long id = index.index(new File(picture));

        waitToSizeEquals(multiServer.getServer(container),1); 

        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),multiServer.getSize());
    }
    
    @Test
    public void testMultiIndexerIndexAsyncWithId() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncWithId");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container),false);
        Long id = index.index(new File(picture),5l);
        
        assertEquals(new Long(5),id);

        waitToSizeEquals(multiServer.getServer(container),1); 

        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),multiServer.getSize());
    }   
    
    @Test
    public void testMultiIndexerIndexAsyncWithIdAnProperties() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncNoAuth");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container),false);
        Long id = index.index(new File(picture),5l,LOCALPICTURE1MAP);

        waitToSizeEquals(multiServer.getServer(container),1); 

        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),multiServer.getSize());
        
        Map<String,String> map = multiServer.getServer(container).getProperties(id);
        assertEquals(2,map.size());
        for(Map.Entry<String,String> entry : LOCALPICTURE1MAP.entrySet()) {
            assertEquals(true,map.containsKey(entry.getKey()));
            assertEquals(entry.getValue(),map.get(entry.getKey()));
        }
        
    }     
    
    
    
     @Test
    public void testMultiIndexerIndexSyncWithContainerNotExist() throws Exception {
        System.out.println("testMultiIndexerIndexSyncWithContainerNotExist");
        String picture = LOCALPICTURE1;
        String container = "containerNotExist";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container),true);
        Long id = index.index(new File(picture));
        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),multiServer.getSize());
    }   
    
    
    @Test
    public void testMultiIndexerIndexAsyncWithContainerNotExist() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncWithContainerNotExist");
        String picture = LOCALPICTURE1;
        String container = "containerNotExist";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container),false);
        Long id = index.index(new File(picture));

        waitToSizeEquals(multiServer.getServer(container),1); 

        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),multiServer.getSize());
    }    
    
    @Test(expected=PictureNotFoundException.class)
    public void testMultiIndexerIndexBadPictureSyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexBadPictureSyncrhone");
        
        String picture1 = BADPICTURE1;
        String container1 = "myContainer"; 
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        multiServer.createServer(container1);  
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container1),true);
        Long id = index.index(new File(picture1),123l);
    }  
    
    @Test(expected=PictureNotFoundException.class)
    public void testMultiIndexerIndexBadPictureASyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexBadPictureSyncrhone");
        
        String picture1 = BADPICTURE1;
        String container1 = "myContainer"; 
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        multiServer.createServer(container1);  
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container1),false);
        Long id = index.index(new File(picture1),123l);
    }      

    @Test
    public void testMultiIndexerDeletePictures() throws Exception {
        System.out.println("testMultiIndexerDeletePictures");
        String container1 = "myContainer";
        String picture1 = LOCALPICTURE1;
        String picture2 = LOCALPICTURE2; 
        multiServer.createServer(container1);     
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container1),true);
        Long id1 = index.index(new File(LOCALPICTURE1));
        Long id2 = index.index(new File(LOCALPICTURE2));        
        assertEquals(2l,(long)multiServer.getSize());
        
        List<Long> ids = new ArrayList<Long>();
        ids.add(id1);
        ids.add(id2);
        
        index.delete(ids);
        assertEquals(0l,(long)multiServer.getSize());  
        assertEquals(false,multiServer.getServer(container1).isPictureInIndex(id1));        
    }
        
    @Test
    public void testMultiIndexerPurge() throws Exception {
        System.out.println("testMultiIndexerPurge");
        String picture1 = LOCALPICTURE1;
        String container1 = "myContainer";
        String picture2 = LOCALPICTURE2;
        String container2= "myContainer2"; 
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        pictures.put(picture2, container2);
        multiServer.createServer(container1);    
        multiServer.createServer(container2);
        RetrievalIndexer index1 = new RetrievalIndexerLocalStorage(multiServer.getServer(container1),true);
        Long id1 = index1.index(new File(LOCALPICTURE1));       
        RetrievalIndexer index2 = new RetrievalIndexerLocalStorage(multiServer.getServer(container2),true);
        Long id2 = index2.index(new File(LOCALPICTURE2));
        
        
        
        index1.delete(id1);
        
        assertEquals(1,multiServer.getServer(container1).getNumberOfPicturesToPurge());
        assertEquals(0,multiServer.getServer(container2).getNumberOfPicturesToPurge());
        
        index1.purge();
        index2.purge();    
        
        assertEquals(0,multiServer.getServer(container1).getNumberOfPicturesToPurge());
        assertEquals(0,multiServer.getServer(container2).getNumberOfPicturesToPurge());        
    }
    
    @Test
    public void testMultiIndexerInfos() throws Exception {
        System.out.println("testMultiIndexerInfos");
        String picture1 = LOCALPICTURE1;
        String container1 = "myContainer";
        String picture2 = LOCALPICTURE2;

        Map<String,String> properties = new HashMap<String,String>();
        properties.put("key","value");
        properties.put("path",LOCALPICTURE1);
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        pictures.put(picture2, container1);
        RetrievalIndexer index1 = new RetrievalIndexerLocalStorage(multiServer.getServer(container1,true),true);
        Long id1 = index1.index(new File(LOCALPICTURE1),123l,properties);       
        Long id2 = index1.index(new File(LOCALPICTURE2),456l);
        
        assertEquals(2,index1.listPictures().size());
        assertEquals("value",index1.listPictures().get(id1).get("key"));
        assertEquals(LOCALPICTURE1,index1.listPictures().get(id1).get("path"));
    } 
}
