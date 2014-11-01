/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.server.index.compress.compressNBT;

import java.util.HashMap;
import java.util.Map;
import retrieval.config.ConfigServer;

/**
 *
 * @author lrollus
 */
public class HashMapCompressIndex extends CompressIndexNBT {
    Map<String,Integer> blacklistedVW;

    public HashMapCompressIndex(ConfigServer config) {
        super(config.getIndexCompressThreshold());
        blacklistedVW = new HashMap<String,Integer>();
    }

    public void blacklistVW(String b) {
        blacklistedVW.put(b, 1);
    }

    public Map<String,Integer> getBlacklistedVW() {
        return blacklistedVW;
    }

    public boolean isBlackListed(String b) {
        return blacklistedVW.containsKey(b);
    }


}
