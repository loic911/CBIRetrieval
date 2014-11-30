/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.compress.compressNBT;

import java.util.Map;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.StartIndexException;

/**
 *
 * @author lrollus
 */
public abstract class CompressIndexNBT {
    //if visualword.net > thresholdNBT => delete visual word, black list visualword and remove it from all servers
    long thresholdNBT;

    public static CompressIndexNBT getCompressIndexFactory(ConfigServer config, String idServer, String idTV,Object globalDatabase) throws StartIndexException {
        if(config.getStoreName().equals("MEMORY")) {
            return new HashMapCompressIndex(config);
        }  else if(config.getStoreName().equals("KYOTOSINGLEFILE")) {
            return new KyotoCompressIndexSingleFile((GlobalDatabase)globalDatabase,config,idServer,idTV);
        }
        throw new StartIndexException(config.getStoreName() + " is not implemented for compress index");
    }

    protected CompressIndexNBT(long thresholdNBT) {
        this.thresholdNBT = thresholdNBT;
    }

    public boolean isNBTTooBig(Long nbt) {
        return thresholdNBT>0? nbt > thresholdNBT: false;
    }

    public abstract void blacklistVW(String b);
    public abstract Map<String,Integer> getBlacklistedVW();
    public abstract boolean isBlackListed(String b);
    public boolean isCompessEnabled() {
        return thresholdNBT>0;
    }
}
