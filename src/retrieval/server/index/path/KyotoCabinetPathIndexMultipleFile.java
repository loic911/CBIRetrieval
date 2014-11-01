/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.server.index.path;

import java.io.File;
import java.util.*;
import kyotocabinet.Cursor;
import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.multiserver.globaldatabase.KyotoCabinetDatabase;
import retrieval.server.exception.CloseIndexException;
import retrieval.server.exception.StartIndexException;
import retrieval.utils.FileUtils;
/**
 *
 * @author lrollus
 */
public class KyotoCabinetPathIndexMultipleFile extends KyotoCabinetPathIndexAbstract{


    private String filename = "path.kch";
    private String filenameReverse = "pathreverse.kch";
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(KyotoCabinetPathIndexMultipleFile.class);

    /**
     * Constructor for a BDB path index
     * @param idStore Id of store
     * @param configStore Configuration object
     * @param read If true, read index (if already exist) else create new index
     * @throws StartIndexException Error during the start of index
     */
    public KyotoCabinetPathIndexMultipleFile(ConfigServer configStore, String idServer)
            throws StartIndexException {

        try {
            this.prefix = "#";
            this.idServer = idServer;
            logger.info("KyotoCabinetPathIndex: start");

            map = KyotoCabinetDatabase.openDatabase(
                    filename,
                    configStore.getIndexPath(),
                    configStore.getKyotoApox(),
                    configStore.getKyotoBNum(),
                    configStore.getKyotoCacheSizeForMetaData(),
                    configStore.getKyotoFUnit()
                    ); 
                         

            if (getSize() == 0) {
                setCountValue(0); 
            }           
            
        } catch (Exception e) {
            throw new StartIndexException(e.getMessage());
        }
    }
 
}
