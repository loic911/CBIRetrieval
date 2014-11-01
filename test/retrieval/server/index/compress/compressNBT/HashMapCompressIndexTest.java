/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.server.index.compress.compressNBT;

import java.util.logging.Level;
import java.util.logging.Logger;
import retrieval.config.ConfigServer;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import retrieval.server.exception.StartIndexException;
import retrieval.server.index.compress.compressNBT.CompressIndexNBT;
import retrieval.server.index.compress.compressNBT.CompressIndexNBT;

/**
 *
 * @author lrollus
 */
public class HashMapCompressIndexTest {

    static ConfigServer config;

    public HashMapCompressIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        config = ConfigServer.getConfigServerForTest();
        config.setStoreName("MEMORY");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of blacklistVW method, of class HashMapCompressIndex.
     */
    @Test
    public void testBlacklistVW() {
        System.out.println("blacklistVW");
        String blacklisted = "blacklisted";
        String notblacklisted = "notblacklisted";
        CompressIndexNBT instance;
        try {
            instance = CompressIndexNBT.getCompressIndexFactory(config,"s0","tv0", "toto");
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
            instance = CompressIndexNBT.getCompressIndexFactory(config,"s0","tv0", "toto");
            instance.blacklistVW(blacklisted1);
            instance.blacklistVW(blacklisted2);
            assertEquals(2,instance.getBlacklistedVW().size());
        } catch (StartIndexException ex) {
            fail("cannot open compress:"+ex);
        }
    }

}