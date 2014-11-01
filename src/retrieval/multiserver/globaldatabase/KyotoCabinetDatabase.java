/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.multiserver.globaldatabase;

import java.io.File;
import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.server.Storage;
import retrieval.server.exception.ReadIndexException;

/**
 *
 * @author lrollus
 */
public class KyotoCabinetDatabase implements GlobalDatabase{
    private static Logger logger = Logger.getLogger(Storage.class);
    private DB database;
    private DB databasePatchs;
    private DB databasePath;
    private DB databasePathInverse;
    private DB databaseCompress;
    
    public KyotoCabinetDatabase(ConfigServer config) throws ReadIndexException {
        logger.info("KyotoCabinetDatabase: start");
        try {
            logger.info("KyotoCabinetDatabase: Creating database...");
            database = openDatabase("main.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForAll(),config.getKyotoFUnit());
            databasePatchs = openDatabase("patchs.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForMetaData(),config.getKyotoFUnit());
            databasePath = openDatabase("path.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForMetaData(),config.getKyotoFUnit());
            databasePathInverse = openDatabase("pathInverse.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForMetaData(),config.getKyotoFUnit());
            databaseCompress = openDatabase("compress.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForMetaData(),config.getKyotoFUnit());
            logger.info("KyotoCabinetDatabase: Database ready!");
        } catch (Exception e) {
            throw new ReadIndexException(e.toString());
        }        
    }    
    
    public static DB openDatabase(String file, String indexPath, String apox, String bnum, String cache, String funit) {
            logger.info("KyotoCabinetMultipleFile: read old index");
            DB kyotoDB = new DB();
            logger.info("Open: " + indexPath + file);
            /**
             * apox -> tune_alignment : default 3 (8 = 1 << 3). DB is build and not updated (0), else more
             * fpow -> tune_fbp : default 10 (1024 = 1 << 10). Most case, don't modify
             * opts -> tune_options : s (small, max 16GB) c (compress), l (linear) => linear
             * bnum -> tune_buckets : number of bucket (number eof entry * 2)
             * msiz -> tune_map : size in memory
             * dfunit -> tune_defrag : defrag after x update (default 8): more = quick, less = heavy space
             */
            String filename = indexPath + file;
            String APOX = "apox=" + apox;
            String BNUM = "bnum=" + bnum;
            String MSIZ = "msiz=" + cache;
            String DFUNIT = "dfunit=" + funit;
            String openConfigPath = filename + "#" + APOX + "#" + BNUM + "#" + MSIZ + "#" + DFUNIT;
            logger.info("KyotoCabinetMultipleFile: open database with: " + openConfigPath);

            File indexPathDir = new File(indexPath);
            logger.info("Index path="+indexPathDir.getAbsolutePath());
            if (!indexPathDir.exists()) {
                indexPathDir.mkdirs();
            }

            if (!kyotoDB.open(openConfigPath, DB.OWRITER | DB.OCREATE)) {
                logger.error("open error: " + kyotoDB.error());
            } 
            return kyotoDB;
    }

    
    public Object getDatabase() {
       return (Object)database; 
    }  
    public Object getDatabasePatchs() {
       return (Object)databasePatchs; 
    }  
    public Object getDatabasePath() {
       return (Object)databasePath; 
    } 
    public Object getDatabasePathInverse() {
       return (Object)databasePathInverse; 
    } 
    
    public Object getDatabaseCompress() {
        return (Object)databaseCompress;
    }
    
}
