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
package retrieval.storage.index.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.RedisDatabase;
import retrieval.storage.exception.StartIndexException;
import retrieval.storage.index.ValueStructure;
import retrieval.storage.index.compress.compressNBT.CompressIndexNBT;

/**
 * Created by lrollus on 14/01/15.
 */
public class RedisHashTable extends HashTableIndexOptim{
    Jedis redis;
    protected String prefix = "";
    ConfigServer config;
    public static String NAME = "REDIS";

    private static Logger logger = Logger.getLogger(RedisHashTable.class);

    public RedisHashTable(Object database,String idServer, String idTestVector, ConfigServer config) throws StartIndexException {
        try {
            this.config = config;
            redis = (Jedis)((RedisDatabase)database).getDatabase();
            logger.info("Redis client will be launch for host=" + redis.getClient().getHost() + " port="+redis.getClient().getPort());
            this.prefix = idServer+"#"+idTestVector+"#";
        }
        catch(Exception e){
            logger.fatal(e.toString());
            throw new StartIndexException();
        }
    }
    public void clear() {
        redis.flushDB();
    }

    public void incrementHashValue(String mainkey, String haskey, long value) {
        redis.hincrBy(this.prefix +mainkey, haskey, value);
    }

    public void incrementHashValue(ConcurrentHashMap<String, Long> visualWords, Long I, CompressIndexNBT compress) {
        Pipeline p = redis.pipelined();
        ConcurrentHashMap<String, Long> visualWordsWithNBT=null;
        if(compress.isCompessEnabled()) {
            visualWordsWithNBT = new ConcurrentHashMap<String, Long>(500);
            visualWordsWithNBT.putAll(visualWords);
            visualWordsWithNBT = getAllValues(visualWordsWithNBT);
        }

        for (Map.Entry<String, Long> entry : visualWords.entrySet()) {

            if(!compress.isBlackListed(entry.getKey())) {
                Long oldNBTValue=null;
                if(compress.isCompessEnabled()) {

                    oldNBTValue = visualWordsWithNBT.get(entry.getKey());
                }
                if(oldNBTValue!=null && compress.isNBTTooBig(oldNBTValue+entry.getValue())) {
                    compress.blacklistVW(entry.getKey());
                    p.del(this.prefix +entry.getKey());
                } else {
                    //System.out.println(this.prefix +entry.getKey() + "=>"  +String.valueOf(I) + "=" + entry.getValue());
                    p.hincrBy(this.prefix + entry.getKey(), String.valueOf(I), entry.getValue());
                    p.hincrBy(this.prefix +entry.getKey(),"-1",entry.getValue());
                }
            }
        }
        p.sync();
        //try{Thread.sleep(1000000);}catch(Exception e){};
    }

    public String getHashValue(String mainkey, String haskey) {
        String str = redis.hget(this.prefix +mainkey, haskey);
        return str;
    }
    public Map<String,String> getValue(String mainkey) {
        return redis.hgetAll(this.prefix +mainkey);
    }

    public ConcurrentHashMap<String, Long> getAllValues(ConcurrentHashMap<String, Long> result) {
        Pipeline p = redis.pipelined();
        List<Response<String>> hgetsR = new ArrayList<Response<String>>(500);
        List<String> keys = new ArrayList<String>(500);
        Iterator<String> searchKey = result.keySet().iterator();
        while (searchKey.hasNext()) {
            String k = searchKey.next();
            k=prefix+k;
            keys.add(k);
            hgetsR.add(p.hget(k,"-1"));
        }
        p.sync();

        for(int i=0;i<keys.size();i++) {
            Response<String> value = hgetsR.get(i);
            try {
                if(value.get()!=null) { //????
                    result.put(keys.get(i), Long.parseLong(value.get()));
                }

            } catch(NullPointerException e) {
                //TODO: very bad code => bug in jedis
                result.put(keys.get(i),0L);
            }

        }
        return result;
    }

    public Map<String,ValueStructure> getAll(List<String> key) {
        List<Response<Map<String, String>>> hgetAllsR = new  ArrayList<Response<Map<String, String>>> (key.size());
        Pipeline p = redis.pipelined();

        Iterator<String> searchKey = key.iterator();
        while (searchKey.hasNext()) {
            String k = searchKey.next();
            hgetAllsR.add(p.hgetAll(this.prefix +k));

        }
        p.sync();

        Map<String,ValueStructure> map = new HashMap<String,ValueStructure>(key.size()*2);
        int k=0;
        for(int i=0;i<hgetAllsR.size();i++) {
            Map<String, String> submap = hgetAllsR.get(i).get();
            if(submap!=null) {
                String nbt = submap.get("-1");
                if(nbt!=null)
                    map.put(key.get(k), new ValueStructure(config, submap, Long.parseLong(nbt)));
            }
            k++;
        }
        return map;
    }

    public void delete(String key) {
        redis.del(this.prefix + key);
    }
    public void deleteAll(Map<Long, Integer> mapID)  {
        Set<String> keys = redis.keys("*");
        Iterator<String> it = keys.iterator();

        while(it.hasNext()) {
            String key = it.next();
            Map<String, String> submap = redis.hgetAll(key);
            Set<String> keys2 = submap.keySet();
            Iterator<String> it2 = keys2.iterator();

            while(it2.hasNext()) {
                String subkeys = it2.next();
                if(mapID.containsKey(Long.parseLong(subkeys))) {
                    Long value = redis.hdel(key, subkeys);
                    redis.hincrBy(key, "-1", -value);
                    if(redis.hlen(key)<=1) {
                        //1 because nbt is store there
                        redis.del(key);
                    }
                }
            }
        }
        //try{Thread.sleep(1000000);}catch(Exception e){};
    }


    public boolean isRessourcePresent(Long id) {
        Set<String> keys = redis.keys("*");
        Iterator<String> it = keys.iterator();

        while(it.hasNext()) {
            String key = it.next();
//            System.out.println("key="+key + " id="+id);
//            System.out.println();
            Map<String, String> submap = redis.hgetAll(key);
//            System.out.println("submap="+submap);
            if(submap.containsKey(id+"")) return true;
            //try{Thread.sleep(10000);}catch(Exception e){};
        }

        return false;
    }

    public void sync()  {
    }

    public void closeIndex() throws Exception {
        redis.disconnect();
    }

    public void printStat() {
        System.out.println("INDEX TOTAL SIZE:"+redis.dbSize());

    }

}
