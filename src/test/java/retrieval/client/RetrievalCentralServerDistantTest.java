package retrieval.client;

import org.apache.log4j.Logger;
import org.junit.*;
import retrieval.config.ConfigClient;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.utils.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author lrollus
 */
public class RetrievalCentralServerDistantTest extends RetrievalCentralServerAbstract{
    
    private static Logger logger = Logger.getLogger(RetrievalCentralServerDistantTest.class);
    

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
            /*
             * multiServer1
             * container 1 = LOCALPICTURE1, LOCALPICTURE2
             * container 2 = LOCALPICTURE3, LOCALPICTURE4
             * 
             * multiserver2
             * container 1 = LOCALPICTURE5, LOCALPICTURE6 , LOCALPICTURE7
             */
            multiCentralWithServer1 = new RetrievalClient(configCentralServer, multiServer1);
            
            List<ServerInformationSocket> servers = new ArrayList<ServerInformationSocket>();
            servers.add(new ServerInformationSocket("localhost", multiServer1.getPort()));
            servers.add(new ServerInformationSocket("localhost", multiServer2.getPort()));
            multiCentralWithAllServer = new RetrievalClient(configCentralServer, new ListServerInformationSocket(servers));
            
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
    public void testMultiCentralServerSearchBasicFromServerFile() throws Exception {
        
            multiCentralWithAllServer = new RetrievalClient(configCentralServer, "testdata/servers.xml");       
        
        System.out.println("testMultiCentralServerSearchBasic");
        BufferedImage img = ImageIO.read(new File(LOCALPICTURE1));
        ResultsSimilarities result = multiCentralWithServer1.search(img, 30);
        assertEquals(true,containsPictures(result, 1l));
        
        result = multiCentralWithAllServer.search(img, 30);
        assertEquals(true,containsPictures(result, 1l));

        //from server 2
        img = ImageIO.read(new File(LOCALPICTURE5));      
        result = multiCentralWithAllServer.search(img, 30);
        assertEquals(true,containsPictures(result, 5l));           
    }

}
