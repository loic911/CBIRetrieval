/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressNBT;

import retrieval.utils.ProcessUtils;
import retrieval.storage.exception.StartIndexException;
import retrieval.utils.FileUtils;
import java.io.File;
import retrieval.config.ConfigServer;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.BasicConfigurator;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lrollus
 */
public class JedisCompressIndexTest {

    static ConfigServer config;
    static Process processRedis;

    public JedisCompressIndexTest() {
    }
    
   @Test
    public void testNotYetImpl() {
        
    }
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//        BasicConfigurator.configure();
//        PropertyConfigurator.configure("log4j.props");
//        config = ConfigServer.getConfigServerForTest();
//        config.STORENAME = "REDIS";
//        config.INDEXPATH = config.INDEXPATH + config.STORENAME + "/";
//        config.NUMBEROFTESTVECTOR = 1;
//        FileUtils.deleteAllFilesRecursively(new File(config.INDEXPATH));
//
//        //TODO: load redis
//        Runtime runtime = Runtime.getRuntime();
//        processRedis = runtime.exec("testdata/redis-2.2.13/src/redis-server testdata/redis-2.2.13/redis1.conf");
//
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//        FileUtils.deleteAllFilesRecursively(new File(config.INDEXPATH));
//
//        //TODO: close redis
//         ProcessUtils.killUnixProcess(processRedis);
//        try {processRedis.destroy();}catch(Exception e) {}
//    }
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    @Test
//    public void testBlacklistVW() {
//        System.out.println("blacklistVW");
//        String blacklisted = "blacklisted";
//        String notblacklisted = "notblacklisted";
//        CompressIndexNBT instance;
//        try {
//            instance = CompressIndexNBT.getCompressIndexFactory(config,"toto");
//            instance.blacklistVW(blacklisted);
//            assertTrue(instance.isBlackListed(blacklisted));
//            assertFalse(instance.isBlackListed(notblacklisted));
//        } catch (StartIndexException ex) {
//            fail("cannot open compress:"+ex);
//        }
//    }
//
//    /**
//     * Test of getBlacklistedVW method, of class HashMapCompressIndex.
//     */
//    @Test
//    public void testGetBlacklistedVW() {
//        System.out.println("blacklistVW");
//        String blacklisted1 = "blacklisted";
//        String blacklisted2 = "notblacklisted";
//        CompressIndexNBT instance;
//        try {
//            instance = CompressIndexNBT.getCompressIndexFactory(config,"toto");
//            instance.blacklistVW(blacklisted1);
//            instance.blacklistVW(blacklisted2);
//            assertEquals(2,instance.getBlacklistedVW().size());
//        } catch (StartIndexException ex) {
//            fail("cannot open compress:"+ex);
//        }
//    }


}