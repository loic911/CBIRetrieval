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
import retrieval.multiindexer.exception.ContainerNotExistException;
import retrieval.multiserver.MultiServer;
import retrieval.server.Storage;
import retrieval.server.exception.NoException;
import retrieval.server.exception.PictureNotFoundException;
import retrieval.server.exception.PictureTooHomogeneous;
import retrieval.test.TestMultiServerUtils;
import retrieval.utils.PictureAuthorization;

/**
 *
 * @author lrollus
 */
public class RetrievalIndexerLocalTest extends TestUtils {
    
    MultiServer multiServer;
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
    public void testMultiIndexerIndexAsync() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncNoAuth");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(multiServer.getServer(container),false);
        index.index(new File(picture));

        waitToSizeEquals(multiServer.getServer(container),1); 
        System.out.println("XYZ="+multiServer.getServer(container).getAllPicturesMap());
        System.out.println(results);
        assertEquals(true,multiServer.getServer(container).isPictureInIndex(picture));
        assertEquals(1,results.size());
        assertEquals(NoException.CODE,results.get(LOCALPICTURE1).getCode());
    }
    
    @Test
    public void testMultiIndexerIndexAsyncWithID() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncNoAuth");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(container),false);
        Long id = index.index(new File(picture),123l);

        waitToSizeEquals(multiServer.getServer(container),1); 
        System.out.println("XYZ="+multiServer.getServer(container).getAllPicturesMap());
        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
    }    
       
    @Test
    public void testMultiIndexerIndexSync() throws Exception {
        System.out.println("testMultiIndexerIndexSyncNoAuth");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(container),true);
        Long id = index.index(new File(picture));
        assertEquals(1,multiServer.getServer(container).getNumberOfItem()); 
        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));        
    }
    
    @Test
    public void testMultiIndexerIndexSyncWithID() throws Exception {
        System.out.println("testMultiIndexerIndexSyncNoAuth");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        multiServer.createServer(container);
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(container),true);
        index.index(new File(picture),123l);
        assertEquals(1,multiServer.getServer(container).getNumberOfItem()); 
        assertEquals(true,multiServer.getServer(container).isPictureInIndex(123l));       
    }    
    
    @Test
    public void testMultiIndexerIndexContainerNotExistSyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncNoAuth");
        String picture = LOCALPICTURE1;
        String container = "containerNotExist";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(container),true);
        Long id = index.index(new File(picture));
        assertEquals(1,multiServer.getServer(container).getNumberOfItem()); 
        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));     
    }
    
    @Test
    public void testMultiIndexerIndexContainerNotExistAsyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncNoAuth");
        String picture = LOCALPICTURE1;
        String container = "containerNotExist";
        multiServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(container),true);
        Long id = index.index(new File(picture));
        waitToSizeEquals(multiServer.getServer(container),1); 
        assertEquals(1,multiServer.getServer(container).getNumberOfItem()); 
        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));  
    }  
    
    @Test
    public void testMultiIndexerIndexEquitablyContainerSynchrone() throws Exception {
        System.out.println("method="+getMethodName());
        
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(LOCALPICTURE1, RetrievalIndexer.EQUITABLY);
        pictures.put(LOCALPICTURE2, RetrievalIndexer.EQUITABLY);  
        pictures.put(LOCALPICTURE3, RetrievalIndexer.EQUITABLY);    
        pictures.put(LOCALPICTURE4, RetrievalIndexer.EQUITABLY);

        for(Entry<String,String> entry : pictures.entrySet()) {
            RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(entry.getValue()),true);
            index.index(new File(entry.getKey()));
        }        
        
        for(Storage server : multiServer.getServerList()) {
            assertEquals(true,server.getNumberOfItem()==1);
        }
    }
    
    @Test
    public void testMultiIndexerIndexEquitablyContainerASynchrone() throws Exception {
        System.out.println("method="+getMethodName());
        
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(LOCALPICTURE1, MultiIndexer.EQUITABLY);
        pictures.put(LOCALPICTURE2, MultiIndexer.EQUITABLY);  
        pictures.put(LOCALPICTURE3, MultiIndexer.EQUITABLY);    
        pictures.put(LOCALPICTURE4, MultiIndexer.EQUITABLY);
        
        for(Entry<String,String> entry : pictures.entrySet()) {
            RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(entry.getValue()),true);
            index.index(new File(entry.getKey()));
        }        
        
        for(Storage server : multiServer.getServerList()) {
            waitToSizeEquals(server, 1);
        }
    }    
        
    @Test(expected=PictureNotFoundException.class)
    public void testMultiIndexerIndexBadPictureSyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexBadPictureSyncrhone");
        
        String picture1 = BADPICTURE1;
        String container1 = "myContainer"; 
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        multiServer.createServer(container1);  
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(container1),true);
        Long id = index.index(new File(picture1),123l);
    }  
    
    @Test
    public void testMultiIndexerIndexBadPictureAsyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexBadPictureAsyncrhone");
        
        String picture1 = BADPICTURE1;
        String container1 = "myContainer"; 
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        multiServer.createServer(container1);  
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(container1),false);
        Long id = index.index(new File(picture1),123l);
        Thread.sleep(1000);
        assertEquals(false,multiServer.getServer(container1).isPictureInIndex(id));
    }  
    
    @Test
    public void testMultiIndexerDeletePictures() throws Exception {
        System.out.println("testMultiIndexerDeletePictures");
        String container1 = "myContainer";
        String picture1 = LOCALPICTURE1;
        String picture2 = LOCALPICTURE2; 
        multiServer.createServer(container1);     
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(multiServer.getServer(container1),true);
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
        RetrievalIndexer index1 = new RetrievalIndexerDistantStorage(multiServer.getServer(container1),true);
        Long id1 = index1.index(new File(LOCALPICTURE1));       
        RetrievalIndexer index2 = new RetrievalIndexerDistantStorage(multiServer.getServer(container2),true);
        Long id2 = index2.index(new File(LOCALPICTURE2));  
        
        index1.purge(); 
        
        assertEquals(1,multiServer.getServer(container1).getNumberOfPicturesToPurge());
        assertEquals(0,multiServer.getServer(container2).getNumberOfPicturesToPurge());
        
        index2.purge();    
        
        assertEquals(0,multiServer.getServer(container1).getNumberOfPicturesToPurge());
        assertEquals(0,multiServer.getServer(container2).getNumberOfPicturesToPurge());        
    }
    
    @Test
    public void testMultiIndexerInfos() throws Exception {
        System.out.println("testMultiIndexerPurge");
        String picture1 = LOCALPICTURE1;
        String container1 = "myContainer";
        String picture2 = LOCALPICTURE2;

        Map<String,String> properties = new HashMap<String,String>();
        properties.put("key","value");
        properties.put("path",LOCALPICTURE1);
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        pictures.put(picture2, container1);
        RetrievalIndexer index1 = new RetrievalIndexerDistantStorage(multiServer.getServer(container1),true);
        Long id1 = index1.index(new File(LOCALPICTURE1),123l,properties);       
        Long id2 = index1.index(new File(LOCALPICTURE2),456l);
        
        assertEquals(2,index1.listPictures().size());
        assertEquals("value",index1.listPictures().get(id1).get("key"));
        assertEquals(LOCALPICTURE1,index1.listPictures().get(id1).get("path"));
    } 
}