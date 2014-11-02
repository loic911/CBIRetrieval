/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressNBT;

import java.io.File;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.BasicConfigurator;
import retrieval.storage.exception.StartIndexException;
import retrieval.config.ConfigServer;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import retrieval.utils.FileUtils;
import static org.junit.Assert.*;

/**
 *
 * @author lrollus
 */
public class KyotoMultipleFileCompressIndexTest {

    static ConfigServer config;

    public KyotoMultipleFileCompressIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        BasicConfigurator.configure();
        PropertyConfigurator.configure("log4j.props");
        config = ConfigServer.getConfigServerForTest();
        config.setStoreName("KYOTOSINGLEFILE");
        config.setIndexPath(config.getIndexPath() + config.getStoreName() + "/");
        config.setNumberOfTestVector(1);
        FileUtils.deleteAllFilesRecursively(new File(config.getIndexPath()));

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        FileUtils.deleteAllFilesRecursively(new File(config.getIndexPath()));
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testBlacklistVW() {
        System.out.println("blacklistVW");
        String blacklisted = "blacklisted";
        String notblacklisted = "notblacklisted";
        CompressIndexNBT instance;
        try {
            instance = CompressIndexNBT.getCompressIndexFactory(config,"s0", "tv0","toto");
            instance.blacklistVW(blacklisted);
            assertTrue(instance.isBlackListed(blacklisted));
            assertFalse(instance.isBlackListed(notblacklisted));
        } catch (StartIndexException ex) {
            fail("cannot open compress:"+ex);
        }
    }

    /**
     * Test of getBlacklistedVW method, of class HashMapCompressIndex.
     */
    @Test
    public void testGetBlacklistedVW() {
        System.out.println("blacklistVW");
        String blacklisted1 = "blacklisted";
        String blacklisted2 = "notblacklisted";
        CompressIndexNBT instance;
        try {
            instance = CompressIndexNBT.getCompressIndexFactory(config,"s0", "tv0","toto");
            instance.blacklistVW(blacklisted1);
            instance.blacklistVW(blacklisted2);
            assertEquals(2,instance.getBlacklistedVW().size());
        } catch (StartIndexException ex) {
            fail("cannot open compress:"+ex);
        }
    }


}