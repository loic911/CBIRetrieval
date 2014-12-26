/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressNBT;

import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.StartIndexException;

/**
 *
 * @author lrollus
 */
public abstract class CompressIndexAbstract {

    static ConfigServer config;
    static GlobalDatabase database;

    public CompressIndexAbstract() {
    }

    /**
     * Test of blacklistVW method, of class HashMapCompressIndex.
     */
    @Test
    public void testBlacklistVW() {
        System.out.println("blacklistVW");
        String blacklisted = "blacklisted"+new Date().getTime();
        String notblacklisted = "notblacklisted"+new Date().getTime();
        CompressIndexNBT instance;
        try {
            instance = CompressIndexNBT.getCompressIndexFactory(config,"s0","tv0",database);
            assertFalse(instance.isBlackListed(blacklisted));
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
        String blacklisted1 = "blacklisted"+new Date().getTime();
        String blacklisted2 = "notblacklisted"+new Date().getTime();
        CompressIndexNBT instance;
        try {
            instance = CompressIndexNBT.getCompressIndexFactory(config,"s0","tv0",database);
            instance.blacklistVW(blacklisted1);
            instance.blacklistVW(blacklisted2);
            assertEquals(true,instance.getBlacklistedVW().size()>=2);
        } catch (StartIndexException ex) {
            fail("cannot open compress:"+ex);
        }
    }
    
    
    /**
     * Test of getBlacklistedVW method, of class HashMapCompressIndex.
     */
    @Test
    public void testGetCompressEnable() {
        System.out.println("blacklistVW");
        CompressIndexNBT instance;
        try {
            instance = CompressIndexNBT.getCompressIndexFactory(config,"s0","tv0",database);
            assertEquals(true, instance.isCompessEnabled());
        } catch (StartIndexException ex) {
            fail("cannot open compress:"+ex);
        }
    } 
    
    /**
     * Test of getBlacklistedVW method, of class HashMapCompressIndex.
     */
    @Test
    public void testIsNBTTooBIG() {
        System.out.println("blacklistVW");
        CompressIndexNBT instance;
        try {
            instance = CompressIndexNBT.getCompressIndexFactory(config,"s0","tv0",database);
            assertEquals(true, instance.isNBTTooBig(config.getIndexCompressThreshold()+1l));
            assertEquals(false, instance.isNBTTooBig((long)config.getIndexCompressThreshold()));
        } catch (StartIndexException ex) {
            fail("cannot open compress:"+ex);
        }
    }    

}