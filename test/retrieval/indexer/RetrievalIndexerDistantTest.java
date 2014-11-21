/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.indexer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import retrieval.config.ConfigServer;
import retrieval.server.RetrievalServer;
import retrieval.storage.Storage;
import retrieval.storage.exception.PictureNotFoundException;
import retrieval.utils.TestUtils;
import static retrieval.utils.TestUtils.BADPICTURE1;
import static retrieval.utils.TestUtils.LOCALPICTURE1;
import static retrieval.utils.TestUtils.LOCALPICTURE1MAP;
import static retrieval.utils.TestUtils.LOCALPICTURE2;

/**
 *
 * @author lrollus
 */
public class RetrievalIndexerDistantTest extends TestUtils {
    
    RetrievalServer retrievalServer;
    ConfigServer config;
    
    private static Logger logger = Logger.getLogger(RetrievalIndexerDistantTest.class);
    
    public RetrievalIndexerDistantTest() {
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
            config = new ConfigServer("testdata/ConfigServer.prop");
            config.setStoreName("MEMORY");
            System.out.println("server");
            retrievalServer = createMultiServer(config,MULTISERVERPORT1,4,"MEMORY");            
        } catch (Exception e) {
            logger.error(e);
            fail();
        }
    }
    
    @After
    public void tearDown() {
        try{retrievalServer.stop();}catch(Exception e) {}
    }
    
    
    @Test
    public void testMultiIndexerIndexSync() throws Exception {
        System.out.println("testMultiIndexerIndexSync");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,true);
        Long id = index.index(new File(picture));
        System.out.println("id="+id);
        System.out.println("id="+retrievalServer.getServer(container).getAllPictures());
        assertEquals(new Long(1l),retrievalServer.getSize());
        assertEquals(true,retrievalServer.getServer(container).isPictureInIndex(id));
        
    }
    
    @Test
    public void testMultiIndexerIndexSyncWithId() throws Exception {
        System.out.println("testMultiIndexerIndexSyncWithId");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,true);
        Long id = index.index(new File(picture),5l);
        
        assertEquals(new Long(5),id);

        assertEquals(true,retrievalServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }   
    
    @Test
    public void testMultiIndexerIndexSyncWithIdAnProperties() throws Exception {
        System.out.println("testMultiIndexerIndexSyncWithIdAnProperties");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,true);
        Long id = index.index(new File(picture),5l,LOCALPICTURE1MAP);

        assertEquals(true,retrievalServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
        
        Map<String,String> map = retrievalServer.getServer(container).getProperties(id);
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
        retrievalServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,false);
        Long id = index.index(new File(picture));

        waitToSizeEquals(retrievalServer.getServer(container),1); 

        assertEquals(true,retrievalServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }
    
    @Test
    public void testMultiIndexerIndexAsyncWithId() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncWithId");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,false);
        Long id = index.index(new File(picture),5l);
        
        assertEquals(new Long(5),id);

        waitToSizeEquals(retrievalServer.getServer(container),1); 

        assertEquals(true,retrievalServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }   
    
    @Test
    public void testMultiIndexerIndexAsyncWithIdAnProperties() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncNoAuth");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,false);
        Long id = index.index(new File(picture),5l,LOCALPICTURE1MAP);

        waitToSizeEquals(retrievalServer.getServer(container),1); 

        assertEquals(true,retrievalServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
        
        Map<String,String> map = retrievalServer.getServer(container).getProperties(id);
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
        retrievalServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,true);
        Long id = index.index(new File(picture));
        assertEquals(true,retrievalServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }   
    
    
    @Test
    public void testMultiIndexerIndexAsyncWithContainerNotExist() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncWithContainerNotExist");
        String picture = LOCALPICTURE1;
        String container = "containerNotExist";
        retrievalServer.createServer(container);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,false);
        Long id = index.index(new File(picture));

        waitToSizeEquals(retrievalServer.getServer(container),1); 

        assertEquals(true,retrievalServer.getServer(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }    
    
    @Test(expected=PictureNotFoundException.class)
    public void testMultiIndexerIndexBadPictureSyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexBadPictureSyncrhone");
        
        String picture1 = BADPICTURE1;
        String container1 = "myContainer"; 
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        retrievalServer.createServer(container1);  
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,false);
        Long id = index.index(new File(picture1),123l);
    }  
    
    @Test(expected=PictureNotFoundException.class)
    public void testMultiIndexerIndexBadPictureASyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexBadPictureSyncrhone");
        
        String picture1 = BADPICTURE1;
        String container1 = "myContainer"; 
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        retrievalServer.createServer(container1);  
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,false);
        Long id = index.index(new File(picture1),123l);
    }      

    @Test
    public void testMultiIndexerDeletePictures() throws Exception {
        System.out.println("testMultiIndexerDeletePictures");
        String container1 = "myContainer";
        String picture1 = LOCALPICTURE1;
        String picture2 = LOCALPICTURE2; 
        retrievalServer.createServer(container1);     
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,true);
        Long id1 = index.index(new File(LOCALPICTURE1));
        Long id2 = index.index(new File(LOCALPICTURE2));        
        assertEquals(2l,(long)retrievalServer.getSize());
        
        List<Long> ids = new ArrayList<Long>();
        ids.add(id1);
        ids.add(id2);
        
        index.delete(ids);
        assertEquals(0l,(long)retrievalServer.getSize());  
        assertEquals(false,retrievalServer.getServer(container1).isPictureInIndex(id1));        
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
        retrievalServer.createServer(container1);    
        retrievalServer.createServer(container2);
        RetrievalIndexer index1 = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,true);
        Long id1 = index1.index(new File(LOCALPICTURE1));       
        RetrievalIndexer index2 = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container2,true);
        Long id2 = index2.index(new File(LOCALPICTURE2));
        
        index1.delete(id1);
        
        assertEquals(1,retrievalServer.getServer(container1).getNumberOfPicturesToPurge());
        assertEquals(0,retrievalServer.getServer(container2).getNumberOfPicturesToPurge());
        
        index1.purge();
        index2.purge();    
        
        assertEquals(0,retrievalServer.getServer(container1).getNumberOfPicturesToPurge());
        assertEquals(0,retrievalServer.getServer(container2).getNumberOfPicturesToPurge());        
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
        RetrievalIndexer index1 = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,true);
        Long id1 = index1.index(new File(LOCALPICTURE1),123l,properties);       
        Long id2 = index1.index(new File(LOCALPICTURE2),456l);
        
        assertEquals(2,index1.listPictures().size());
        assertEquals("value",index1.listPictures().get(id1).get("key"));
        assertEquals(LOCALPICTURE1,index1.listPictures().get(id1).get("path"));
    }     
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

//    @Test
//    public void testMultiIndexerIndexAsync() throws Exception {
//        System.out.println("testMultiIndexerIndexAsyncNoAuth");
//        String picture = LOCALPICTURE1;
//        String container = "myContainer";
//        multiServer.createServer(container);
//        
//        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,false);
//        index.index(new File(picture));
//
//        waitToSizeEquals(multiServer.getServer(container),1); 
//        System.out.println("XYZ="+multiServer.getServer(container).getAllPicturesMap());
//        System.out.println(results);
//        assertEquals(true,multiServer.getServer(container).isPictureInIndex(picture));
//        assertEquals(1,results.size());
//        assertEquals(NoException.CODE,results.get(LOCALPICTURE1).getCode());
//    }
//    
//    @Test
//    public void testMultiIndexerIndexAsyncWithID() throws Exception {
//        System.out.println("testMultiIndexerIndexAsyncNoAuth");
//        String picture = LOCALPICTURE1;
//        String container = "myContainer";
//        multiServer.createServer(container);
//        
//        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,false);
//        Long id = index.index(new File(picture),123l);
//
//        waitToSizeEquals(multiServer.getServer(container),1); 
//        System.out.println("XYZ="+multiServer.getServer(container).getAllPicturesMap());
//        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));
//    }    
//       
//    @Test
//    public void testMultiIndexerIndexSync() throws Exception {
//        System.out.println("testMultiIndexerIndexSyncNoAuth");
//        String picture = LOCALPICTURE1;
//        String container = "myContainer";
//        multiServer.createServer(container);
//        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,true);
//        Long id = index.index(new File(picture));
//        assertEquals(1,multiServer.getServer(container).getNumberOfItem()); 
//        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));        
//    }
//    
//    @Test
//    public void testMultiIndexerIndexSyncWithID() throws Exception {
//        System.out.println("testMultiIndexerIndexSyncNoAuth");
//        String picture = LOCALPICTURE1;
//        String container = "myContainer";
//        multiServer.createServer(container);
//        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,true);
//        index.index(new File(picture),123l);
//        assertEquals(1,multiServer.getServer(container).getNumberOfItem()); 
//        assertEquals(true,multiServer.getServer(container).isPictureInIndex(123l));       
//    }    
//    
//    @Test
//    public void testMultiIndexerIndexContainerNotExistSyncrhone() throws Exception {
//        System.out.println("testMultiIndexerIndexAsyncNoAuth");
//        String picture = LOCALPICTURE1;
//        String container = "containerNotExist";
//        multiServer.createServer(container);
//        
//        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,true);
//        Long id = index.index(new File(picture));
//        assertEquals(1,multiServer.getServer(container).getNumberOfItem()); 
//        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));     
//    }
//    
//    @Test
//    public void testMultiIndexerIndexContainerNotExistAsyncrhone() throws Exception {
//        System.out.println("testMultiIndexerIndexAsyncNoAuth");
//        String picture = LOCALPICTURE1;
//        String container = "containerNotExist";
//        multiServer.createServer(container);
//        
//        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container,true);
//        Long id = index.index(new File(picture));
//        waitToSizeEquals(multiServer.getServer(container),1); 
//        assertEquals(1,multiServer.getServer(container).getNumberOfItem()); 
//        assertEquals(true,multiServer.getServer(container).isPictureInIndex(id));  
//    }  
//    
//    @Test
//    public void testMultiIndexerIndexEquitablyContainerSynchrone() throws Exception {
//        System.out.println("method="+getMethodName());
//        
//        Map<String,String> pictures = new HashMap<String,String>();
//        pictures.put(LOCALPICTURE1, RetrievalIndexer.EQUITABLY);
//        pictures.put(LOCALPICTURE2, RetrievalIndexer.EQUITABLY);  
//        pictures.put(LOCALPICTURE3, RetrievalIndexer.EQUITABLY);    
//        pictures.put(LOCALPICTURE4, RetrievalIndexer.EQUITABLY);
//
//        for(Entry<String,String> entry : pictures.entrySet()) {
//            RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,entry.getValue(),true);
//            index.index(new File(entry.getKey()));
//        }        
//        
//        for(Storage server : multiServer.getServerList()) {
//            assertEquals(true,server.getNumberOfItem()==1);
//        }
//    }
//    
    @Test
    public void testMultiIndexerIndexEquitablyContainerASynchrone() throws Exception {
        System.out.println("method="+getMethodName());
        
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(LOCALPICTURE1, RetrievalServer.EQUITABLY);
        pictures.put(LOCALPICTURE2, RetrievalServer.EQUITABLY);  
        pictures.put(LOCALPICTURE3, RetrievalServer.EQUITABLY);    
        pictures.put(LOCALPICTURE4, RetrievalServer.EQUITABLY);
        
        for(Entry<String,String> entry : pictures.entrySet()) {
            RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,entry.getValue(),true);
            index.index(new File(entry.getKey()));
        }        
        
        for(Storage server : retrievalServer.getServerList()) {
            waitToSizeEquals(server, 1);
        }
    }    
//        
//    @Test(expected=PictureNotFoundException.class)
//    public void testMultiIndexerIndexBadPictureSyncrhone() throws Exception {
//        System.out.println("testMultiIndexerIndexBadPictureSyncrhone");
//        
//        String picture1 = BADPICTURE1;
//        String container1 = "myContainer"; 
//        Map<String,String> pictures = new HashMap<String,String>();
//        pictures.put(picture1, container1);
//        multiServer.createServer(container1);  
//        
//        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,true);
//        Long id = index.index(new File(picture1),123l);
//    }  
//    
//    @Test
//    public void testMultiIndexerIndexBadPictureAsyncrhone() throws Exception {
//        System.out.println("testMultiIndexerIndexBadPictureAsyncrhone");
//        
//        String picture1 = BADPICTURE1;
//        String container1 = "myContainer"; 
//        Map<String,String> pictures = new HashMap<String,String>();
//        pictures.put(picture1, container1);
//        multiServer.createServer(container1);  
//        
//        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,false);
//        Long id = index.index(new File(picture1),123l);
//        Thread.sleep(1000);
//        assertEquals(false,multiServer.getServer(container1).isPictureInIndex(id));
//    }  
//    
//    @Test
//    public void testMultiIndexerDeletePictures() throws Exception {
//        System.out.println("testMultiIndexerDeletePictures");
//        String container1 = "myContainer";
//        String picture1 = LOCALPICTURE1;
//        String picture2 = LOCALPICTURE2; 
//        multiServer.createServer(container1);     
//        RetrievalIndexer index = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,true);
//        Long id1 = index.index(new File(LOCALPICTURE1));
//        Long id2 = index.index(new File(LOCALPICTURE2));        
//        assertEquals(2l,(long)multiServer.getSize());
//        
//        List<Long> ids = new ArrayList<Long>();
//        ids.add(id1);
//        ids.add(id2);
//        
//        index.delete(ids);
//        assertEquals(0l,(long)multiServer.getSize());  
//        assertEquals(false,multiServer.getServer(container1).isPictureInIndex(id1));        
//    }
//        
//    @Test
//    public void testMultiIndexerPurge() throws Exception {
//        System.out.println("testMultiIndexerPurge");
//        String picture1 = LOCALPICTURE1;
//        String container1 = "myContainer";
//        String picture2 = LOCALPICTURE2;
//        String container2= "myContainer2"; 
//        Map<String,String> pictures = new HashMap<String,String>();
//        pictures.put(picture1, container1);
//        pictures.put(picture2, container2);
//        multiServer.createServer(container1);    
//        multiServer.createServer(container2);
//        RetrievalIndexer index1 = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,true);
//        Long id1 = index1.index(new File(LOCALPICTURE1));       
//        RetrievalIndexer index2 = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container2,true);
//        Long id2 = index2.index(new File(LOCALPICTURE2));  
//        
//        index1.purge(); 
//        
//        assertEquals(1,multiServer.getServer(container1).getNumberOfPicturesToPurge());
//        assertEquals(0,multiServer.getServer(container2).getNumberOfPicturesToPurge());
//        
//        index2.purge();    
//        
//        assertEquals(0,multiServer.getServer(container1).getNumberOfPicturesToPurge());
//        assertEquals(0,multiServer.getServer(container2).getNumberOfPicturesToPurge());        
//    }
//    
//    @Test
//    public void testMultiIndexerInfos() throws Exception {
//        System.out.println("testMultiIndexerPurge");
//        String picture1 = LOCALPICTURE1;
//        String container1 = "myContainer";
//        String picture2 = LOCALPICTURE2;
//
//        Map<String,String> properties = new HashMap<String,String>();
//        properties.put("key","value");
//        properties.put("path",LOCALPICTURE1);
//        Map<String,String> pictures = new HashMap<String,String>();
//        pictures.put(picture1, container1);
//        pictures.put(picture2, container1);
//        RetrievalIndexer index1 = new RetrievalIndexerDistantStorage(MULTISERVERURL,MULTISERVERPORT1,container1,true);
//        Long id1 = index1.index(new File(LOCALPICTURE1),123l,properties);       
//        Long id2 = index1.index(new File(LOCALPICTURE2),456l);
//        
//        assertEquals(2,index1.listPictures().size());
//        assertEquals("value",index1.listPictures().get(id1).get("key"));
//        assertEquals(LOCALPICTURE1,index1.listPictures().get(id1).get("path"));
//    } 
}
