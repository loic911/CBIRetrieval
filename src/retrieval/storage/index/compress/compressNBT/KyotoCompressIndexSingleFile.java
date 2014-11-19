/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressNBT;

import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;

public class KyotoCompressIndexSingleFile extends KyotoCompressIndexAbstract {

    private static Logger logger = Logger.getLogger(KyotoCompressIndexSingleFile.class);

    public KyotoCompressIndexSingleFile(GlobalDatabase global,ConfigServer config,String idServer, String idTV) {
        super(config.getIndexCompressThreshold());
        logger.info("KyotoCompressIndexSingleFile: start");
        this.prefix =idServer+"#"+idTV+"#";
        this.kyoto = (DB)global.getDatabaseCompress();
       
    }
}
