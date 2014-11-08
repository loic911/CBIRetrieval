package retrieval.storage;

import java.io.File;
import org.junit.*;
import static org.junit.Assert.fail;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.server.globaldatabase.KyotoCabinetDatabase;
import retrieval.storage.exception.PictureTooHomogeneous;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class StorageKyotoSingleTest extends StorageTestAbstract {  
    
    public StorageKyotoSingleTest() {
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
            config.setStoreName("KYOTOSINGLEFILE");
            System.out.println("server");
            FileUtils.deleteAllSubFilesRecursively(new File(config.getIndexPath()));
            GlobalDatabase database = new KyotoCabinetDatabase(config);
            storage = createServer("0",config,database); 
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
       
}
