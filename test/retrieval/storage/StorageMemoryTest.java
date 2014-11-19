package retrieval.storage;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import retrieval.config.ConfigServer;
import retrieval.storage.exception.PictureTooHomogeneous;
import retrieval.utils.FileUtils;
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
            config = new ConfigServer("testdata/ConfigServer.prop");
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
