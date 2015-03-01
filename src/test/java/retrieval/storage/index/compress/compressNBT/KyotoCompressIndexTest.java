/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressNBT;

import org.junit.BeforeClass;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.KyotoCabinetDatabase;
import retrieval.utils.FileUtils;

import java.io.File;

/**
 *
 * @author lrollus
 */
public class KyotoCompressIndexTest extends CompressIndexAbstract{

    public KyotoCompressIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        config = ConfigServer.getConfigServerForTest();
        FileUtils.deleteAllSubFilesRecursively(new File(config.getIndexPath()));
        config.setStoreName("KYOTOSINGLEFILE");
        config.setIndexCompressThreshold(10);
        database = new KyotoCabinetDatabase(config);
    }

}