package retrieval.server;

import java.io.File;
import org.junit.*;
import static org.junit.Assert.fail;
import retrieval.config.ConfigServer;
import retrieval.multiserver.globaldatabase.GlobalDatabase;
import retrieval.multiserver.globaldatabase.KyotoCabinetDatabase;
import retrieval.server.exception.PictureTooHomogeneous;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class ServerKyotoMultipleTest extends ServerTestAbstract {  
    
    public ServerKyotoMultipleTest() {
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
            config.setStoreName("KYOTOMULTIPLEFILE");
            FileUtils.deleteAllSubFilesRecursively(new File(config.getIndexPath()));
            System.out.println("server");
            server = createServer("0",config,null);           
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try { server.stop();}catch(Exception e) {}
        server=null;
    }
       
}
