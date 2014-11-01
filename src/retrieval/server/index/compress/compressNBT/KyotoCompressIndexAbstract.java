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

public abstract class KyotoCompressIndexAbstract extends CompressIndexNBT {

    protected DB kyoto;
    protected String prefix;
    private static Logger logger = Logger.getLogger(KyotoCompressIndexAbstract.class);
    
    protected KyotoCompressIndexAbstract(long thresholdNBT) {
        super(thresholdNBT);
    }    

    public void blacklistVW(String b) {
        kyoto.set(prefix+b, "1");
    }

    public Map<String,Integer> getBlacklistedVW() {
        Map<String, Integer> blacklistedVW = new HashMap<String, Integer>((int)kyoto.count());

        Cursor cur = kyoto.cursor();
        cur.jump();
        String[] rec;
        while ((rec = cur.get_str(true)) != null) {
                blacklistedVW.put(rec[0].split("#")[2], Integer.parseInt(rec[1]));
        }
        cur.disable();

        return blacklistedVW;
    }

    public boolean isBlackListed(String b) {
        return kyoto.get(prefix+b)!=null;
    }
}
