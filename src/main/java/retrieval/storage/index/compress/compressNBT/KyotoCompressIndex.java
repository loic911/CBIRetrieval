/*
 * Copyright 2009-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval.storage.index.compress.compressNBT;

import kyotocabinet.Cursor;
import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Compress index for Kyoto db
 * @author Loïc Rollus
 */
public class KyotoCompressIndex extends CompressIndexNBT {

   protected DB kyoto;
   protected String prefix;    
    
    private static Logger logger = Logger.getLogger(KyotoCompressIndex.class);

    /**
     * Create a compress NBT index for Kyoto
     * @param global The main kyoto database
     * @param config Config server
     * @param idStorage Storage name
     * @param idTV Test vector id
     */
    public KyotoCompressIndex(GlobalDatabase global, ConfigServer config, String idStorage, String idTV) {
        super(config.getIndexCompressThreshold());
        logger.info("KyotoCompressIndexSingleFile: start");
        this.prefix =idStorage+"#"+idTV+"#";
        this.kyoto = (DB)global.getDatabaseCompress();
       
    }
    
    /**
     * Blacklist a visualword
     * @param b visualword
     */    
    public void blacklistVW(String b) {
        kyoto.set(prefix+b, "1");
    }

    /**
     * Retrieve all blacklisted VW
     */    
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

   /**
     * Check if a visualword is blacklisted in the storage for this test vector
     * @param b visualword
     * @return true if b is blacklisted
     */    
    public boolean isBlackListed(String b) {
        return kyoto.get(prefix+b)!=null;
    }    
}
