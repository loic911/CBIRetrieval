package retrieval.storage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import retrieval.RedisInstance;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.server.globaldatabase.KyotoCabinetDatabase;
import retrieval.server.globaldatabase.RedisDatabase;
import retrieval.storage.StorageTestAbstract;
import retrieval.utils.FileUtils;

import java.io.File;

import static org.junit.Assert.fail;

/**
 *
 * @author lrollus
 */
public class StorageRedisTest extends StorageTestAbstract {

    static RedisInstance redis = new RedisInstance();

    public StorageRedisTest() {
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
            enableLog();
            config = ConfigServer.getConfigServerForTest();
            config.setStoreName("REDIS");
            config.setIndexCompressThreshold(10);
            config.setRedisPort(RedisInstance.PORT+"");
            redis.deleteRedisData();
            redis.initRedis();
            GlobalDatabase database = new RedisDatabase(config);
            storage = createServer("0",config,database);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try { Thread.sleep(1000);storage.stop();}catch(Exception e) {}
        storage=null;
        try {redis.killRedisAll();}catch(Exception e) {}


    }
       
}
