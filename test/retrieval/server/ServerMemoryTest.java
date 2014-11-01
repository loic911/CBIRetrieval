package retrieval.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import static org.junit.Assert.*;
import org.junit.*;
import retrieval.utils.TestUtils;
import retrieval.config.ConfigServer;
import retrieval.exception.CBIRException;
import retrieval.server.exception.NoException;
import retrieval.server.exception.PictureNotFoundException;
import retrieval.server.exception.PictureTooHomogeneous;
import retrieval.testvector.TestVectorListCentralServer;
import retrieval.testvector.generator.TestVectorReading;
import retrieval.utils.NetworkUtils;
import retrieval.utils.PictureAuthorization;

/**
 *
 * @author lrollus
 */
public class ServerMemoryTest extends ServerTestAbstract {  
    
    public ServerMemoryTest() {
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
            server = createServer("0",config);           
        } catch (Exception e) {
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try { server.stop();}catch(Exception e) {}
        server=null;
    }
    
    @Test(expected=PictureTooHomogeneous.class)
    public void testServerIndexPictureTooHomogenous() throws Exception {
       System.out.println("testServerIndexPictureTooHomogenous");
        config.setMaxPercentageSimilarWord(0.50d);
       server.stop();
       server = createServer("1",config);
       server.indexPicture(PICTURETOOHOMOGENOUS);
    }    
    
    @Test
    public void testServerIndexPictureTooHomogenousWithoutLimit() throws Exception {
       System.out.println("testServerIndexPictureTooHomogenous");
       server.indexPicture(PICTURETOOHOMOGENOUS);
    }    
}
