/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.server.index.compress.compressNBT;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import kyotocabinet.*;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;

public class KyotoCompressIndexMultipleFile extends KyotoCompressIndexAbstract {

    private static Logger logger = Logger.getLogger(KyotoCompressIndexMultipleFile.class);

    public KyotoCompressIndexMultipleFile(ConfigServer config, String name, String idTV) {
        super(config.getIndexCompressThreshold());
        logger.info("KyotoCabinetHashMap: start");
        this.prefix ="#"+idTV+"#";
        try {
            kyoto = new DB();
            String filename = config.getIndexPath() + "compressNBT"+name+".kch";
            String APOX = "apox=" + config.getKyotoApox();
            String BNUM = "bnum=" + config.getKyotoBNum();
            String MSIZ = "msiz=" + config.getKyotoCacheSizeForMainIndex();
            String DFUNIT = "dfunit=" + config.getKyotoFUnit();
            String openConfigPath = filename + "#" + APOX + "#" + BNUM + "#" + MSIZ + "#" + DFUNIT;
            logger.info("KyotoCabinetHashMap: open database with: "+ openConfigPath);

            File indexPath = new File(config.getIndexPath());
            if(!indexPath.exists()) {
                indexPath.mkdirs();
            }

            if (!kyoto.open(openConfigPath, DB.OWRITER | DB.OCREATE)) {
                logger.error("open error: " + kyoto.error());
                System.exit(0);
            }
        } catch (Exception e) {
            logger.fatal(e.toString());
        }
    }
}
