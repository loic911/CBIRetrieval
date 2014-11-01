/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server.index.patchs;

import java.io.File;
import java.util.Map;
import kyotocabinet.Cursor;
import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.multiserver.globaldatabase.KyotoCabinetDatabase;
import retrieval.server.exception.CloseIndexException;
import retrieval.server.exception.ReadIndexException;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class KyotoCabinetPatchsIndexMultipleFile extends KyotoCabinetPatchsIndexAbstract {

    /**
     * Store map
     */
    private String filename = "patchs.kch";
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(KyotoCabinetPatchsIndexMultipleFile.class);

    /**
     * Constructor to build a Patchs Map in Memory
     * @param read No effect (just memory, don't read anything)
     * @throws ReadIndexException Error during the Read
     */
    public KyotoCabinetPatchsIndexMultipleFile(ConfigServer configStore)
            throws ReadIndexException {
        logger.info("KyotoCabinetPatchsIndex: start");
        try {
            this.prefix = "#";
            
            map = KyotoCabinetDatabase.openDatabase(
                    filename,
                    configStore.getIndexPath(),
                    configStore.getKyotoApox(),
                    configStore.getKyotoBNum(),
                    configStore.getKyotoCacheSizeForMetaData(),
                    configStore.getKyotoFUnit()
                    );             
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
