package retrieval.multicentralserver;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;
import retrieval.utils.TestUtils;
import retrieval.config.ConfigCentralServer;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.multiserver.MultiServer;

/**
 *
 * @author lrollus
 */
public class MultiCentralServerTest extends TestUtils{
    
    MultiServer multiServer1;
    MultiServer multiServer2;
    String CONTAINER1 = "myContainer1";
    String CONTAINER2 = "myContainer2";
    ConfigServer config;
    ConfigCentralServer configCentralServer;
    MultiCentralServer multiCentral;
    
    private static Logger logger = Logger.getLogger(MultiCentralServerTest.class);
    

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
            configCentralServer = new ConfigCentralServer("config/ConfigCentralServer.prop");
            config.setStoreName("MEMORY");
            System.out.println("server");
            multiServer1 = createMultiServer(config,MULTISERVERPORT1,0,"MEMORY");      
            multiServer2 = createMultiServer(config,MULTISERVERPORT2,0,"MEMORY");
            multiServer1.createServer(CONTAINER1);
            multiServer1.createServer(CONTAINER2);
            multiServer2.createServer(CONTAINER1);
            
            multiServer1.getServer(CONTAINER1).indexPicture(LOCALPICTURE1);
            multiServer1.getServer(CONTAINER1).indexPicture(LOCALPICTURE2);
            multiServer1.getServer(CONTAINER2).indexPicture(LOCALPICTURE3);
            multiServer1.getServer(CONTAINER2).indexPicture(LOCALPICTURE4);
            multiServer2.getServer(CONTAINER1).indexPicture(LOCALPICTURE5);
            multiServer2.getServer(CONTAINER1).indexPicture(LOCALPICTURE6);
            multiServer2.getServer(CONTAINER1).indexPicture(LOCALPICTURE7);
            /*
             * multiServer1
             * container 1 = LOCALPICTURE1, LOCALPICTURE2
             * container 2 = LOCALPICTURE3, LOCALPICTURE4
             * 
             * multiserver2
             * container 1 = LOCALPICTURE5, LOCALPICTURE6 , LOCALPICTURE7
             */
            
            ListServerInformationSocket servers = new ListServerInformationSocket();
            servers.add(new ServerInformationSocket(MULTISERVERURL, MULTISERVERPORT1), 1);
            servers.add(new ServerInformationSocket(MULTISERVERURL, MULTISERVERPORT2), 2);
            multiCentral = new MultiCentralServer(configCentralServer, servers);
            
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

    /**
     * Test of search method, of class MultiCentralServer.
     */
    @Test
    public void testMultiCentralServerSearchBasic() throws Exception {
        System.out.println("testMultiCentralServerSearchBasic");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE1));
        ResultsSimilarities result = multiCentral.search(img, 30);
        assertEquals(true,containsPictures(result, LOCALPICTURE1));
    }

    @Test
    public void testMultiCentralServerSearchFilter() throws Exception {
        System.out.println("testMultiCentralServerSearchFilter");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE3));
        ResultsSimilarities result = multiCentral.search(img, 30, new String[]{"1"});
        assertEquals(false,containsPictures(result, LOCALPICTURE3));
    }

    @Test
    public void testMultiCentralServerSearchBasicWithN() throws Exception {
        System.out.println("testMultiCentralServerSearchBasic");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE1));
        ResultsSimilarities result = multiCentral.search(img, 1000,30,new String[]{});
        assertEquals(true,containsPictures(result, LOCALPICTURE1));
    }
}
