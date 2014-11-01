package retrieval.server;

import org.junit.*;
import static org.junit.Assert.fail;
import retrieval.config.ConfigServer;
import retrieval.RedisInstance;
import retrieval.server.exception.PictureTooHomogeneous;

/**
 *
 * @author lrollus
 */
public class ServerRedisTest  {  
    
    RedisInstance redis;
    
    public ServerRedisTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Test
    public void testNotYetImpl() {
        
    }
    
//    @Before
//    public void setUp() {
//        try {
//            config = new ConfigServer("config/ConfigServer.prop");
//            config.STORENAME = "REDIS";
//            try {
//                redis = new RedisInstance(config);
//                redis.initRedis();
//            } catch(Exception e) {
//                e.printStackTrace();
//                redis.killRedis();
//                throw new Exception();
//            }            
//            System.out.println("server");
//            server = createServer(config);  
//            Thread.sleep(1000);
//            System.out.println("server stop");
//        } catch (Exception e) {
//            fail();
//        }        
//    }
//    
//    @After
//    public void tearDown() {
//        redis.killRedis();
//        try { server.stop();}catch(Exception e) {}
//        server=null;
//    }       
}
