package retrieval.client;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import retrieval.RedisInstance;
import retrieval.config.ConfigClient;
import retrieval.config.ConfigServer;
import retrieval.server.RetrievalServer;
import retrieval.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

/**
 *
 * @author lrollus
 */
public class RetrievalCentralServerLocalRedisTest extends RetrievalCentralServerAbstract{
    
    private static Logger logger = Logger.getLogger(RetrievalCentralServerLocalRedisTest.class);

    static RedisInstance redis = new RedisInstance(2);
    

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
            configCentralServer = new ConfigClient("testdata/ConfigClient.prop");
            config.setStoreName("REDIS");
            config.setRedisPort(RedisInstance.PORT + "");
            redis.deleteRedisData();
            redis.initRedis();
            System.out.println("server");
            config.setRedisPort(redis.getPort(0)+"");
            multiServer1 = createMultiServer(config,MULTISERVERPORT1,0,"REDIS");
            config.setRedisPort(redis.getPort(1)+"");
            multiServer2 = createMultiServer(config,MULTISERVERPORT2,0,"REDIS");
            multiServer1.createStorage(CONTAINER1);
            multiServer1.createStorage(CONTAINER2);
            multiServer2.createStorage(CONTAINER1);
            
            multiServer1.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
            multiServer1.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE2),2l,null);
            multiServer1.getStorage(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE3),3l,null);
            multiServer1.getStorage(CONTAINER2).indexPicture(FileUtils.readPicture(LOCALPICTURE4),4l,null);
            multiServer2.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE5),5l,null);
            multiServer2.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE6),6l,null);
            multiServer2.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE7),7l,null);
            /*
             * multiServer1
             * container 1 = LOCALPICTURE1, LOCALPICTURE2
             * container 2 = LOCALPICTURE3, LOCALPICTURE4
             * 
             * multiserver2
             * container 1 = LOCALPICTURE5, LOCALPICTURE6 , LOCALPICTURE7
             */
            multiCentralWithServer1 = new RetrievalClient(configCentralServer, multiServer1);
            
            List<RetrievalServer> servers = new ArrayList<RetrievalServer>();
            servers.add(multiServer1);
            servers.add(multiServer2);
            multiCentralWithAllServer = new RetrievalClient(configCentralServer, servers);
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try{multiServer1.stop();}catch(Exception e) {}
        try{multiServer2.stop();}catch(Exception e) {}
        try {redis.killRedisAll();}catch(Exception e) {}
    }

}
