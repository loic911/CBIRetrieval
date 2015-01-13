/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressNBT;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import retrieval.RedisInstance;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.KyotoCabinetDatabase;
import retrieval.server.globaldatabase.RedisDatabase;
import retrieval.utils.FileUtils;

import java.io.File;

/**
 *
 * @author lrollus
 */
public class RedisCompressIndexTest extends CompressIndexAbstract{

    static RedisInstance redis = new RedisInstance();
    public RedisCompressIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        enableLog();
        config = ConfigServer.getConfigServerForTest();
        FileUtils.deleteAllSubFilesRecursively(new File(config.getIndexPath()));
        config.setStoreName("REDIS");
        config.setIndexCompressThreshold(10);
        config.setRedisPort(RedisInstance.PORT+"");

        redis.deleteRedisData();
        redis.initRedis();

        database = new RedisDatabase(config);

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        redis.killRedisAll();
    }
}