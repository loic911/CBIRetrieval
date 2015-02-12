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

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.server.globaldatabase.RedisDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Compress index for Kyoto db
 * @author Loïc Rollus
 */
public class RedisCompressIndex extends CompressIndexNBT {

   private JedisPool redis;
   protected String prefix;


    private static Logger logger = Logger.getLogger(RedisCompressIndex.class);

    /**
     * Create a compress NBT index for Kyoto
     * @param global The main kyoto database
     * @param config Config server
     * @param idStorage Storage name
     * @param idTV Test vector id
     */
    public RedisCompressIndex(GlobalDatabase global, ConfigServer config, String idStorage, String idTV) {
        super(config.getIndexCompressThreshold());
        logger.info("KyotoCompressIndexSingleFile: start");
        this.prefix = RedisDatabase.REDIS_COMPRESS_STORE + "#"+idStorage+"#"+idTV+"#";
        this.redis = (JedisPool)global.getDatabaseCompress();
       
    }
    
    /**
     * Blacklist a visualword
     * @param b visualword
     */    
    public void blacklistVW(String b) {
        try (Jedis redis = this.redis.getResource()) {
            redis.sadd(prefix, b);
        }
    }

    /**
     * Retrieve all blacklisted VW
     */    
    public Map<String,Integer> getBlacklistedVW() {

        Map<String, Integer> blacklistedVW = new HashMap<String, Integer>();
        try (Jedis redis = this.redis.getResource()) {
            Set<String> keys = redis.smembers(this.prefix);
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                blacklistedVW.put(key, -1);

            }
        }

        return blacklistedVW;
    }

   /**
     * Check if a visualword is blacklisted in the storage for this test vector
     * @param b visualword
     * @return true if b is blacklisted
     */    
    public boolean isBlackListed(String b) {
        try (Jedis redis = this.redis.getResource()) {
            return redis.sismember(prefix, b);
        }
    }

}
