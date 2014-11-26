/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import retrieval.indexer.*;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import retrieval.client.main.RetrievalClientMain;
import retrieval.config.ConfigClient;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.indexer.main.RetrievalDeleterMain;
import retrieval.indexer.main.RetrievalIndexerMain;
import retrieval.indexer.main.RetrievalPurgerMain;
import retrieval.server.RetrievalServer;
import retrieval.storage.index.ResultSim;
import retrieval.utils.FileUtils;
import retrieval.utils.TestUtils;
import static retrieval.utils.TestUtils.LOCALPICTURE1;
import static retrieval.utils.TestUtils.LOCALPICTURE1MAP;
import static retrieval.utils.TestUtils.LOCALPICTURE2;
import static retrieval.utils.TestUtils.LOCALPICTURE3;
import static retrieval.utils.TestUtils.LOCALPICTURE4;
import static retrieval.utils.TestUtils.LOCALPICTURE5;
import static retrieval.utils.TestUtils.LOCALPICTURE6;
import static retrieval.utils.TestUtils.LOCALPICTURE7;
import static retrieval.utils.TestUtils.MULTISERVERPORT1;
import static retrieval.utils.TestUtils.MULTISERVERPORT2;
import static retrieval.utils.TestUtils.containsPictures;
import static retrieval.utils.TestUtils.createMultiServer;


public class RetrievalClientMainTest extends TestUtils {
    
    RetrievalServer multiServer1;
    RetrievalServer multiServer2;
    ConfigServer config;
    ConfigClient configCentralServer;
     String CONTAINER1 = "myContainer1";
    String CONTAINER2 = "myContainer2";   
    private static final Logger logger = Logger.getLogger(RetrievalClientMainTest.class);
    
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
            configCentralServer = new ConfigClient("testdata/ConfigClient.prop");
            config.setStoreName("MEMORY");
            System.out.println("server");
            multiServer1 = createMultiServer(config,MULTISERVERPORT1,0,"MEMORY");      
            multiServer2 = createMultiServer(config,MULTISERVERPORT2,0,"MEMORY");
            multiServer1.createServer(CONTAINER1);
            multiServer1.createServer(CONTAINER2);
            multiServer2.createServer(CONTAINER1);
            
            multiServer1.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
            multiServer1.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE2),2l,null);
            multiServer1.getServer(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE3),3l,null);
            multiServer1.getServer(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE4),4l,null);
            multiServer2.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE5),5l,null);
            multiServer2.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE6),6l,null);
            multiServer2.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE7),7l,null);       
        } catch (Exception e) {
            logger.error(e);
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try{multiServer1.stop();}catch(Exception e) {}
        try{multiServer2.stop();}catch(Exception e) {}
    }
    
        /**
     * Main method for client
     * Param0: Config client file
     * Param1: Servers (host:port) list (commat sep list: host1:port1,host2:port2,...)
     * Param2: Image path/url
     * Param3: Maximum similar pictures 
     * Param4: (Optional) Storages name (commat sep: test,mystorage,...)
     * @param args Params arrays
     */
    
     @Test
    public void testMultiCentralServerSearchBasic() throws Exception {
        System.out.println("testMultiCentralServerSearchBasic");
        
        String[] args = {
            "testdata/ConfigClient.prop",
            MULTISERVERURL+":"+MULTISERVERPORT1+","+MULTISERVERURL+":"+MULTISERVERPORT1,
            LOCALPICTURE1,
            "10"};
        RetrievalClientMain.main(args);     
        
        String[] args2 = {
            "testdata/ConfigClient.prop",
            MULTISERVERURL+":"+MULTISERVERPORT1+","+MULTISERVERURL+":"+MULTISERVERPORT1,
            LOCALPICTURE1,
            "10",
            CONTAINER1};
        RetrievalClientMain.main(args);      
        
        String[] args3 = {
            "testdata/ConfigClient.prop",
            "testdata/servers.xml",
            LOCALPICTURE1,
            "10",
            CONTAINER1};
        RetrievalClientMain.main(args);            
        
           
    }


  
    
    
//    /**
//     * Test of main method, of class MultiIndexerMain.
//     */
//    @Test
//    public void testMultiIndexerMainIndexLocalPicture() throws Exception {
//        String container = "test";
//        multiServer.createServer(container);
//        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",LOCALPICTURE1,"sync",container};
//        RetrievalIndexerMain.main(args);
//        assertEquals(1, multiServer.getServer(container).getNumberOfItem());      
//    } 
//    
//    @Test
//    public void testMultiIndexerMainIndexLocalPictureWithID() throws Exception {
//        String container = "test";
//        multiServer.createServer(container);
//        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",LOCALPICTURE1,"sync",container,"123"};
//        RetrievalIndexerMain.main(args);
//        assertEquals(1, multiServer.getServer(container).getNumberOfItem()); 
//        assertEquals(true,multiServer.getServer(container).isPictureInIndex(123l));   
//    }    
//    
//    @Test
//    public void testMultiIndexerMainIndexLocalPictureWithIDAndProperties() throws Exception {
//        String container = "test";
//        String key1 = "key";
//        String key2 = "hel";
//        String value1 = "value";
//        String value2 = "lo";
//        multiServer.createServer(container);
//        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",LOCALPICTURE1,"sync",container,"123","key,hel","value,lo"};
//        RetrievalIndexerMain.main(args);
//        assertEquals(1, multiServer.getServer(container).getNumberOfItem()); 
//        assertEquals(true,multiServer.getServer(container).isPictureInIndex(123l));
//        assertEquals(2, multiServer.getServer(container).getProperties(123l).size()); 
//        assertEquals(value1,multiServer.getServer(container).getProperties(123l).get(key1));
//        assertEquals(value2,multiServer.getServer(container).getProperties(123l).get(key2));
//    }       
//    
//    
//    @Test
//    public void testMultiIndexerMainIndexURLWithID() throws Exception {
//        String container = "test";
//        multiServer.createServer(container);
//        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",URLPICTURENOAUTH,"sync",container,"123"};
//        RetrievalIndexerMain.main(args);
//        assertEquals(1, multiServer.getServer(container).getNumberOfItem()); 
//        assertEquals(true,multiServer.getServer(container).isPictureInIndex(123l));  
//    }       
//    
//    @Test
//    public void testMultiIndexerMainDelete() throws Exception {
//        
//        String container = "test";
//        multiServer.createServer(container);
//        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",URLPICTURENOAUTH,"sync",container,"123"};
//        RetrievalIndexerMain.main(args);
//         assertEquals(1, multiServer.getServer(container).getNumberOfItem());                
//        String[] args2 = {MULTISERVERURL,MULTISERVERPORT1+"","123,456"};
//        RetrievalDeleterMain.main(args2);       
//        assertEquals(0, multiServer.getServer(container).getNumberOfItem()); 
//    }    
//        
//    @Test
//    public void testMultiIndexerMainPurge() throws Exception {
//        String container = "test";
//        multiServer.createServer(container);
//        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",URLPICTURENOAUTH,"sync",container,"123"};
//        RetrievalIndexerMain.main(args);
//         assertEquals(1, multiServer.getServer(container).getNumberOfItem());                
//        String[] args2 = {MULTISERVERURL,MULTISERVERPORT1+"","123,456"};
//        RetrievalDeleterMain.main(args2);       
//        assertEquals(1, multiServer.getServer(container).getNumberOfPicturesToPurge());   
//        String[] args3 = {MULTISERVERURL,MULTISERVERPORT1+""};
//        RetrievalPurgerMain.main(args3); 
//        waitEquals(0, multiServer.getServer(container).getNumberOfPicturesToPurge());
//    }    
//    
//        
//    @Test
//    public void testMultiIndexerMainInfos() throws Exception {
//        String container = "test";
//        String[] args = {MULTISERVERURL,MULTISERVERPORT1+"",container};
//        RetrievalInfoMain.main(args);        
//    }  
}
