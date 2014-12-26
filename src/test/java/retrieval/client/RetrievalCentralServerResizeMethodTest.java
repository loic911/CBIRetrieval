package retrieval.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import retrieval.TestUtils;
import static retrieval.TestUtils.LOCALPICTURE1;
import static retrieval.TestUtils.containsPictures;
import retrieval.config.ConfigClient;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.server.RetrievalServer;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class RetrievalCentralServerResizeMethodTest extends TestUtils{
        RetrievalServer multiServer1;
    RetrievalServer multiServer2;
    String CONTAINER1 = "myContainer1";
    String CONTAINER2 = "myContainer2";
    ConfigServer config;
    ConfigClient configCentralServer;
    RetrievalClient multiCentralWithServer1;
    RetrievalClient multiCentralWithAllServer;
    private static Logger logger = Logger.getLogger(RetrievalCentralServerResizeMethodTest.class);
    

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
            multiServer1.createStorage(CONTAINER1);
            multiServer1.createStorage(CONTAINER2);
            multiServer2.createStorage(CONTAINER1);
            
            multiServer1.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
            multiServer1.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE2),2l,null);
            multiServer1.getStorage(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE3),3l,null);
            multiServer1.getStorage(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE4),4l,null);
            multiServer2.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE5),5l,null);
            multiServer2.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE6),6l,null);
            multiServer2.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE7),7l,null);


            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try{multiServer1.stop();}catch(Exception e) {}
        try{multiServer2.stop();}catch(Exception e) {}        
    }
    
    @Test
    public void testMultiCentralServerSearchBasicWithResizeMethod1() throws Exception {
        configCentralServer.setResizeMethod(1);
        List<RetrievalServer> servers = new ArrayList<RetrievalServer>();
        servers.add(multiServer1);
        servers.add(multiServer2);
        multiCentralWithAllServer = new RetrievalClient(configCentralServer, servers);
            
        System.out.println("testMultiCentralServerSearchBasic");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE1));
        ResultsSimilarities result = multiCentralWithAllServer.search(img, 1000,30,new String[]{});
        assertEquals(true,containsPictures(result, 1l));
    }    
    @Test
    public void testMultiCentralServerSearchBasicWithResizeMethod2() throws Exception {
        configCentralServer.setResizeMethod(2);
        List<RetrievalServer> servers = new ArrayList<RetrievalServer>();
        servers.add(multiServer1);
        servers.add(multiServer2);
        multiCentralWithAllServer = new RetrievalClient(configCentralServer, servers);
            
        System.out.println("testMultiCentralServerSearchBasic");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE1));
        ResultsSimilarities result = multiCentralWithAllServer.search(img, 1000,30,new String[]{});
        assertEquals(true,containsPictures(result, 1l));
    }      
    @Test
    public void testMultiCentralServerSearchBasicWithResizeMethod3() throws Exception {
        configCentralServer.setResizeMethod(3);
        List<RetrievalServer> servers = new ArrayList<RetrievalServer>();
        servers.add(multiServer1);
        servers.add(multiServer2);
        multiCentralWithAllServer = new RetrievalClient(configCentralServer, servers);
            
        System.out.println("testMultiCentralServerSearchBasic");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE1));
        ResultsSimilarities result = multiCentralWithAllServer.search(img, 1000,30,new String[]{});
        assertEquals(true,containsPictures(result, 1l));
    }
    @Test
    public void testMultiCentralServerSearchBasicWithResizeMethod4() throws Exception {
        configCentralServer.setResizeMethod(4);
        List<RetrievalServer> servers = new ArrayList<RetrievalServer>();
        servers.add(multiServer1);
        servers.add(multiServer2);
        multiCentralWithAllServer = new RetrievalClient(configCentralServer, servers);
            
        System.out.println("testMultiCentralServerSearchBasic");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE1));
        ResultsSimilarities result = multiCentralWithAllServer.search(img, 1000,30,new String[]{});
        assertEquals(true,containsPictures(result, 1l));
    }      
}
