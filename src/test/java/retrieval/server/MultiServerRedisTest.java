/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import retrieval.RedisInstance;
import retrieval.config.ConfigServer;
import retrieval.utils.FileUtils;

import java.io.File;

import static org.junit.Assert.fail;

/**
 *
 * @author lrollus
 */
public class MultiServerRedisTest extends MultiServerAbstract{

    RetrievalServer multiServer2;
    static RedisInstance redis = new RedisInstance();

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
            config.setRedisPort(RedisInstance.PORT + "");
            redis.deleteRedisData();
            redis.initRedis();

            multiServer = createMultiServer(config,MULTISERVERPORT1,0,"REDIS");
            
            multiServer.createStorage(CONTAINER1);
            multiServer.createStorage(CONTAINER2);
            
            multiServer.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
            multiServer.getStorage(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE2),2l,null);
            /*
             * multiServer1
             * container 1 = LOCALPICTURE1, LOCALPICTURE2
             */
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }           
    }
    
    @After
    public void tearDown() {
        try{multiServer.stop();}catch(Exception e) {}
        try {redis.killRedisAll();}catch(Exception e) {}
    }

}
