package retrieval.storage;

import org.junit.*;
import static org.junit.Assert.fail;
import retrieval.config.ConfigServer;
import retrieval.RedisInstance;
import retrieval.storage.exception.PictureTooHomogeneous;

/**
 *
 * @author lrollus
 */
public class StorageRedisTest  {  
    
    RedisInstance redis;
    
    public StorageRedisTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    } 
}
