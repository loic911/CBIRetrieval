package retrieval.storage;

import org.junit.*;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.MemoryDatabase;
import retrieval.storage.exception.PictureTooHomogeneous;
import retrieval.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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

    @Test
    public void testPropertiesShouldBeClone() throws Exception {
        System.out.println("testPropertiesShouldBeClone");
        Map<String,String> map = new HashMap<>();
        storage.indexPicture(FileUtils.readPicture(PICTURETOOHOMOGENOUS),5l,map);
        map.put("hello","world");
        Map<String,String> imageMap = storage.getProperties(5l);
        //the image properties should not be modify, even if we change the object
        assertNull(imageMap.get("hello"));
    }
}
