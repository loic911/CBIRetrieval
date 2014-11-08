package retrieval.centralserver;

import retrieval.centralserver.MultiCentralServer;
import retrieval.centralserver.ListServerInformationSocket;
import retrieval.centralserver.ServerInformationSocket;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;
import retrieval.utils.TestUtils;
import retrieval.config.ConfigCentralServer;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.server.RetrievalServer;
import retrieval.storage.index.ResultSim;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class RetrievalCentralServerLocalTest extends TestUtils{
    
    RetrievalServer multiServer1;
    RetrievalServer multiServer2;
    String CONTAINER1 = "myContainer1";
    String CONTAINER2 = "myContainer2";
    ConfigServer config;
    ConfigCentralServer configCentralServer;
    MultiCentralServer multiCentralWithServer1;
    MultiCentralServer multiCentralWithAllServer;
    
    private static Logger logger = Logger.getLogger(RetrievalCentralServerLocalTest.class);
    

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
            
            multiServer1.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
            multiServer1.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE2),2l,null);
            multiServer1.getServer(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE3),3l,null);
            multiServer1.getServer(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE4),4l,null);
            multiServer2.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE5),5l,null);
            multiServer2.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE6),6l,null);
            multiServer2.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE7),7l,null);
            /*
             * multiServer1
             * container 1 = LOCALPICTURE1, LOCALPICTURE2
             * container 2 = LOCALPICTURE3, LOCALPICTURE4
             * 
             * multiserver2
             * container 1 = LOCALPICTURE5, LOCALPICTURE6 , LOCALPICTURE7
             */
            multiCentralWithServer1 = new MultiCentralServer(configCentralServer, multiServer1);
            
            List<RetrievalServer> servers = new ArrayList<RetrievalServer>();
            servers.add(multiServer1);
            servers.add(multiServer2);
            multiCentralWithAllServer = new MultiCentralServer(configCentralServer, servers);
            
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
        ResultsSimilarities result = multiCentralWithServer1.search(img, 30);
        assertEquals(true,containsPictures(result, 1l));
        
        result = multiCentralWithAllServer.search(img, 30);
        assertEquals(true,containsPictures(result, 1l));
        Map<String,String> properties = null;
        for(ResultSim sim : result.getResults()) {
            if(sim.getId()==1l) {
                properties = sim.getProperties();
            }
        }
        assertEquals(2,properties.size());
        for(String key : LOCALPICTURE1MAP.keySet()) {
            assertEquals(true, properties.containsKey(key)); 
            assertEquals(LOCALPICTURE1MAP.get(key), properties.get(key)); 
        }        
    }

    @Test
    public void testMultiCentralServerSearchFilter() throws Exception {
        System.out.println("testMultiCentralServerSearchFilter");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE3));
        ResultsSimilarities result = multiCentralWithServer1.search(img, 30, new String[]{"1"});
        assertEquals(false,containsPictures(result, 2l));
        
        result = multiCentralWithAllServer.search(img, 30, new String[]{"1"});
        assertEquals(false,containsPictures(result, 2l));
    }

    @Test
    public void testMultiCentralServerSearchBasicWithN() throws Exception {
        System.out.println("testMultiCentralServerSearchBasic");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE1));
        ResultsSimilarities result = multiCentralWithAllServer.search(img, 1000,30,new String[]{});
        assertEquals(true,containsPictures(result, 1l));
    }
}
