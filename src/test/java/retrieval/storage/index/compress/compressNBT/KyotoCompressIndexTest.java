/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressNBT;

import java.io.File;
import org.junit.BeforeClass;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.KyotoCabinetDatabase;
import static retrieval.storage.index.compress.compressNBT.CompressIndexAbstract.database;
import retrieval.utils.FileUtils;

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