/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.properties;

import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.StartIndexException;
/**
 *
 * @author lrollus
 */
public class KyotoCabinetPropertiesIndexSingleFile extends KyotoCabinetPropertiesIndexAbstract{

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(KyotoCabinetPropertiesIndexSingleFile.class);

    /**
     * Constructor for a BDB path index
     * @param idStore Id of store
     * @param configStore Configuration object
     * @param read If true, read index (if already exist) else create new index
     * @throws StartIndexException Error during the start of index
     */
    public KyotoCabinetPropertiesIndexSingleFile(GlobalDatabase global,String idServer) throws StartIndexException {

        try {
            logger.info("KyotoCabinetPathIndexSingleFile: start");
            this.idServer = idServer;
            this.map = (DB)global.getDatabasePath();
            this.prefix = idServer + "#";

            ///if empty insert first tuple
            logger.info("getSize="+getSize());
            if (getSize() == 0) {
                setCountValue(0);
            }
            logger.info("getSize="+getSize());            
            
        } catch (Exception e) {
            throw new StartIndexException(e.getMessage());
        }
    }
}
