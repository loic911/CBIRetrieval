/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.storage.index.patchs;

import java.io.File;
import java.util.Map;
import kyotocabinet.Cursor;
import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.multiserver.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.ReadIndexException;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class KyotoCabinetPatchsIndexSingleFile extends KyotoCabinetPatchsIndexAbstract {

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(KyotoCabinetPatchsIndexSingleFile.class);

    /**
     * Constructor to build a Patchs Map in Memory
     * @param read No effect (just memory, don't read anything)
     * @throws ReadIndexException Error during the Read
     */
    public KyotoCabinetPatchsIndexSingleFile(GlobalDatabase global,String idServer)
            throws ReadIndexException {
        logger.info("KyotoCabinetPatchsIndexSingleFile: start");
        this.map = (DB)global.getDatabasePatchs();
        this.prefix = idServer+"#";
        
    }
}
