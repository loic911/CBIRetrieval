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
public abstract class RetrievalCentralServerAbstract extends TestUtils{
    
    RetrievalServer multiServer1;
    RetrievalServer multiServer2;
    String CONTAINER1 = "myContainer1";
    String CONTAINER2 = "myContainer2";
    ConfigServer config;
    ConfigCentralServer configCentralServer;
    MultiCentralServer multiCentralWithServer1;
    MultiCentralServer multiCentralWithAllServer;
    
    private static Logger logger = Logger.getLogger(RetrievalCentralServerAbstract.class);
    

    @BeforeClass
    public static void setUpClass() throws Exception {
        enableLog();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
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
