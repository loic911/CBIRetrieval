/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressNBT;

import org.junit.BeforeClass;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.MemoryDatabase;

/**
 *
 * @author lrollus
 */
public class HashMapCompressIndexTest extends CompressIndexAbstract{

    public HashMapCompressIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        config = ConfigServer.getConfigServerForTest();
        config.setStoreName("MEMORY");
        config.setIndexCompressThreshold(10);
        database = new MemoryDatabase(config);
    }

}