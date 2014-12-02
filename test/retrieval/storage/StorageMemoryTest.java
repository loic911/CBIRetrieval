package retrieval.storage;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static retrieval.TestUtils.LOCALPICTURE1MAP;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.MemoryDatabase;
import retrieval.storage.exception.PictureTooHomogeneous;
import retrieval.utils.FileUtils;

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
            storage = createServer("0",config,new MemoryDatabase(config));           
        } catch (Exception e) {
            e.printStackTrace();
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
       storage = createServer("1",config,new MemoryDatabase(config));
       storage.indexPicture(FileUtils.readPicture(PICTURETOOHOMOGENOUS),5l,LOCALPICTURE1MAP);
    }    
    
    @Test
    public void testServerIndexPictureTooHomogenousWithoutLimit() throws Exception {
       System.out.println("testServerIndexPictureTooHomogenous");
       storage.indexPicture(FileUtils.readPicture(PICTURETOOHOMOGENOUS),5l,LOCALPICTURE1MAP);
    }    
}
