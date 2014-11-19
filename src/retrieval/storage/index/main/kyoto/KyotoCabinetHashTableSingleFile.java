/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.storage.index.main.kyoto;

import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.KyotoCabinetDatabase;
import retrieval.storage.exception.ReadIndexException;

/**
 *
 * @author lrollus
 */
public class KyotoCabinetHashTableSingleFile extends KyotoCabinetHashTableAbstract {

    /**
     * Name of Hashtable
     */
    public static String NAME = "KYOTOSINGLEFILE";
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(KyotoCabinetHashTableSingleFile.class);

    /**
     * Constructor for a memory Hash Table
     * @param file Name of file which will contain store
     * @param configStore Configuration object
     * @param read If true, read index (if already exist), else create new index
     * @throws ReadIndexException Error during the read of index
     */
    public KyotoCabinetHashTableSingleFile(Object database,String idServer, String idTestVector, ConfigServer config) throws ReadIndexException {
        logger.info("KyotoCabinetMultipleFile: start");
        this.visuwalWordPosition = 2;
        this.config = config;
        try {
           logger.debug("init database");
           hashmap = (DB)((KyotoCabinetDatabase)database).getDatabase(); 
           logger.debug("database OK");
        } catch(ClassCastException e) {
            throw new ReadIndexException("Cannot convert database to kyoto database!"+e);
        }
        this.prefix = idServer+"#"+idTestVector+"#";
    }
}
