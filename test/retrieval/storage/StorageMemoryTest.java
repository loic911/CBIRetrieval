package retrieval.storage;

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
import retrieval.storage.exception.NoException;
import retrieval.storage.exception.PictureNotFoundException;
import retrieval.storage.exception.PictureTooHomogeneous;
import retrieval.testvector.TestVectorListCentralServer;
import retrieval.testvector.generator.TestVectorReading;
import retrieval.utils.FileUtils;
import retrieval.utils.NetworkUtils;
import static retrieval.utils.TestUtils.LOCALPICTURE1;
import static retrieval.utils.TestUtils.LOCALPICTURE1MAP;

/**
 *
 * @author lrollus
 */
public class StorageMemoryTest extends StorageTestAbstract {  
    
    public StorageMemoryTest() {
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
            storage = createServer("0",config);           
        } catch (Exception e) {
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try { storage.stop();}catch(Exception e) {}
        storage=null;
    }
    
    @Test(expected=PictureTooHomogeneous.class)
    public void testServerIndexPictureTooHomogenous() throws Exception {
       System.out.println("testServerIndexPictureTooHomogenous");
        config.setMaxPercentageSimilarWord(0.50d);
       storage.stop();
       storage = createServer("1",config);
       storage.indexPicture(FileUtils.readPicture(PICTURETOOHOMOGENOUS),5l,LOCALPICTURE1MAP);
    }    
    
    @Test
    public void testServerIndexPictureTooHomogenousWithoutLimit() throws Exception {
       System.out.println("testServerIndexPictureTooHomogenous");
       storage.indexPicture(FileUtils.readPicture(PICTURETOOHOMOGENOUS),5l,LOCALPICTURE1MAP);
    }    
}
