/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server.index.compress.compressNBT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import retrieval.config.ConfigServer;
import retrieval.server.exception.StartIndexException;

public class JedisCompressIndex extends CompressIndexNBT {

    private Jedis redis;
    private ConfigServer config;
    private static Logger logger = Logger.getLogger(JedisCompressIndex.class);

    public JedisCompressIndex(ConfigServer config) throws StartIndexException {
        super(config.getIndexCompressThreshold());
        try {
            this.config = config;
            String host = config.getRedisHost();
            Integer port = Integer.parseInt(config.getRedisPort());
            int store = config.getNextRedisStoreId();
            logger.info("Jedis client will be launch for host=" + host + " port=" + port + " db=" + store);
            redis = new Jedis(host, port, Integer.MAX_VALUE);
            redis.select(store);
        } catch (Exception e) {
            logger.fatal(e.toString());
            throw new StartIndexException(e.toString());
        }
    }

    public void blacklistVW(String b) {
        redis.set(b, "1");
    }

    public Map<String, Integer> getBlacklistedVW() {
        Map<String, Integer> blacklistedVW = new HashMap<String, Integer>(redis.dbSize().intValue());
        Set<String> keys = redis.keys("*");
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = redis.get(key);
            blacklistedVW.put(key, Integer.parseInt(value));

        }
        return blacklistedVW;
    }

    public boolean isBlackListed(String b) {
        return redis.exists(b);
    }
}
