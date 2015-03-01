/*
 * Copyright 2009-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval.indexer;

import org.apache.log4j.Logger;
import org.junit.*;
import retrieval.TestUtils;
import retrieval.config.ConfigServer;
import retrieval.server.RetrievalServer;
import retrieval.storage.exception.PictureNotFoundException;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author lrollus
 */
public class RetrievalIndexerLocalTest extends TestUtils {
    
    RetrievalServer retrievalServer;
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
        retrievalServer.createStorage(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container),true);
        Long id = index.index(new File(picture));
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }
    
    @Test
    public void testMultiIndexerIndexSyncWithId() throws Exception {
        System.out.println("testMultiIndexerIndexSyncWithId");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createStorage(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container),true);
        Long id = index.index(new File(picture),5l);
        
        assertEquals(new Long(5),id);

        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }   
    
    @Test
    public void testMultiIndexerIndexSyncWithIdAnProperties() throws Exception {
        System.out.println("testMultiIndexerIndexSyncWithIdAnProperties");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createStorage(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container),true);
        Long id = index.index(new File(picture),null,LOCALPICTURE1MAP);

        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
        
        Map<String,String> map = retrievalServer.getStorage(container).getProperties(id);
        assertEquals(TestUtils.BASE_NUMBER_OF_PROPERTIES+2,map.size());
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
        retrievalServer.createStorage(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container),false);
        Long id = index.index(new File(picture));

        waitToSizeEquals(retrievalServer.getStorage(container),1); 

        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }
    
    @Test
    public void testMultiIndexerIndexAsyncWithId() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncWithId");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createStorage(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container),false);
        Long id = index.index(new File(picture),5l);
        
        assertEquals(new Long(5),id);

        waitToSizeEquals(retrievalServer.getStorage(container),1); 

        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }   
    
    @Test
    public void testMultiIndexerIndexAsyncWithIdAnProperties() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncNoAuth");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createStorage(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container),false);
        Long id = index.index(new File(picture),5l,LOCALPICTURE1MAP);

        waitToSizeEquals(retrievalServer.getStorage(container),1); 

        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
        
        Map<String,String> map = retrievalServer.getStorage(container).getProperties(id);
        assertEquals(TestUtils.BASE_NUMBER_OF_PROPERTIES+2,map.size());
        for(Map.Entry<String,String> entry : LOCALPICTURE1MAP.entrySet()) {
            assertEquals(true,map.containsKey(entry.getKey()));
            assertEquals(entry.getValue(),map.get(entry.getKey()));
        }
        
    }    
    
    
    @Test
    public void testMultiIndexerIndexAllSync() throws Exception {
        System.out.println("testMultiIndexerIndexSync");
        String picture = LOCALPICTURE1;
        String container = "myContainer";
        retrievalServer.createStorage(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container),true);
        Long id;
        
        id = index.index(new File(picture));
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));

        id = index.index(new File(picture),LOCALPICTURE1MAP);
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        
        id = index.index(new File(picture),new Date().getTime(),LOCALPICTURE1MAP);
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        
        id = index.index(new URL(URLPICTURENOAUTH));
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        
        id = index.index(new URL(URLPICTURENOAUTH),LOCALPICTURE1MAP);
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        
        id = index.index(new URL(URLPICTURENOAUTH),new Date().getTime());
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        
         id = index.index(ImageIO.read(new File(picture)));
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        
         id = index.index(ImageIO.read(new File(picture)),LOCALPICTURE1MAP);
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        
        id = index.index(ImageIO.read(new File(picture)),new Date().getTime());
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
              
    }    
    
    
    
     @Test
    public void testMultiIndexerIndexSyncWithContainerNotExist() throws Exception {
        System.out.println("testMultiIndexerIndexSyncWithContainerNotExist");
        String picture = LOCALPICTURE1;
        String container = "containerNotExist";
        retrievalServer.createStorage(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container),true);
        Long id = index.index(new File(picture));
        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }   
    
    
    @Test
    public void testMultiIndexerIndexAsyncWithContainerNotExist() throws Exception {
        System.out.println("testMultiIndexerIndexAsyncWithContainerNotExist");
        String picture = LOCALPICTURE1;
        String container = "containerNotExist";
        retrievalServer.createStorage(container);
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container),false);
        Long id = index.index(new File(picture));

        waitToSizeEquals(retrievalServer.getStorage(container),1); 

        assertEquals(true,retrievalServer.getStorage(container).isPictureInIndex(id));
        assertEquals(new Long(1l),retrievalServer.getSize());
    }    
    
    @Test(expected=PictureNotFoundException.class)
    public void testMultiIndexerIndexBadPictureSyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexBadPictureSyncrhone");
        
        String picture1 = BADPICTURE1;
        String container1 = "myContainer"; 
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        retrievalServer.createStorage(container1);  
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container1),true);
        Long id = index.index(new File(picture1),123l);
    }  
    
    @Test(expected=PictureNotFoundException.class)
    public void testMultiIndexerIndexBadPictureASyncrhone() throws Exception {
        System.out.println("testMultiIndexerIndexBadPictureSyncrhone");
        
        String picture1 = BADPICTURE1;
        String container1 = "myContainer"; 
        Map<String,String> pictures = new HashMap<String,String>();
        pictures.put(picture1, container1);
        retrievalServer.createStorage(container1);  
        
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container1),false);
        Long id = index.index(new File(picture1),123l);
    }      

    @Test
    public void testMultiIndexerDeletePictures() throws Exception {
        System.out.println("testMultiIndexerDeletePictures");
        String container1 = "myContainer";
        String picture1 = LOCALPICTURE1;
        String picture2 = LOCALPICTURE2; 
        retrievalServer.createStorage(container1);     
        RetrievalIndexer index = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container1),true);
        Long id1 = index.index(new File(LOCALPICTURE1));
        Long id2 = index.index(new File(LOCALPICTURE2));        
        Long id3 = index.index(new File(LOCALPICTURE3)); 
        
        List<Long> ids = new ArrayList<Long>();
        ids.add(id1);
        ids.add(id2);
        
        index.delete(ids);
        assertEquals(1l,(long)retrievalServer.getSize());  
        assertEquals(false,retrievalServer.getStorage(container1).isPictureInIndex(id1)); 
        index.delete(id3);
        assertEquals(0l,(long)retrievalServer.getSize());        
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
        retrievalServer.createStorage(container1);    
        retrievalServer.createStorage(container2);
        RetrievalIndexer index1 = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container1),true);
        Long id1 = index1.index(new File(LOCALPICTURE1));       
        RetrievalIndexer index2 = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container2),true);
        Long id2 = index2.index(new File(LOCALPICTURE2));
        
        
        
        index1.delete(id1);
        
        assertEquals(1,retrievalServer.getStorage(container1).getNumberOfPicturesToPurge());
        assertEquals(0,retrievalServer.getStorage(container2).getNumberOfPicturesToPurge());
        
        index1.purge();
        index2.purge();    
        
        assertEquals(0,retrievalServer.getStorage(container1).getNumberOfPicturesToPurge());
        assertEquals(0,retrievalServer.getStorage(container2).getNumberOfPicturesToPurge());        
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
        RetrievalIndexer index1 = new RetrievalIndexerLocalStorage(retrievalServer.getStorage(container1,true),true);
        Long id1 = index1.index(new File(LOCALPICTURE1),123l,properties);       
        Long id2 = index1.index(new File(LOCALPICTURE2),456l);
        
        assertEquals(2,index1.listPictures().size());
        assertEquals("value",index1.listPictures().get(id1).get("key"));
        assertEquals(LOCALPICTURE1,index1.listPictures().get(id1).get("path"));
    } 
    
    @Test
    public void testChangeCurrentStorage() throws Exception {   
        RetrievalIndexerLocalStorage index1 = new RetrievalIndexerLocalStorage(retrievalServer.getStorage("0",true),true);
        index1.changeCurrentStorage(retrievalServer.getStorage("1"));
    }
}
