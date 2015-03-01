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

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.RedisDatabase;
import retrieval.storage.exception.StartIndexException;
import retrieval.storage.index.ValueStructure;
import retrieval.storage.index.compress.compressNBT.CompressIndexNBT;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lrollus on 14/01/15.
 */
public class RedisHashTable extends HashTableIndexOptim{
    private JedisPool redis;
    protected String prefix = "";
    protected String subPrefix = "";
    ConfigServer config;
    public static String NAME = "REDIS";



    private static Logger logger = Logger.getLogger(RedisHashTable.class);

    public RedisHashTable(Object database,String idServer, String idTestVector, ConfigServer config) throws StartIndexException {
        try {
            this.config = config;
            redis = (JedisPool)((RedisDatabase)database).getDatabase();
            this.prefix = RedisDatabase.REDIS_INDEX_STORE + "#"+idServer+"#"+idTestVector+"#";
            this.subPrefix = RedisDatabase.REDIS_INDEX_STORE + "#"+idServer+ "#";
        }
        catch(Exception e){
            logger.fatal(e.toString());
            throw new StartIndexException();
        }
    }
    public void clear() {
        try (Jedis redis = this.redis.getResource()) {
            redis.flushDB();
        }
    }

    public void incrementHashValue(String mainkey, String haskey, long value) {
        try (Jedis redis = this.redis.getResource()) {
            redis.hincrBy(this.prefix + mainkey, haskey, value);
        }

    }

    public void incrementHashValue(ConcurrentHashMap<String, Long> visualWords, Long I, CompressIndexNBT compress) {
        try (Jedis redis = this.redis.getResource()) {
            Pipeline p = redis.pipelined();
            ConcurrentHashMap<String, Long> visualWordsWithNBT=null;
            if(compress.isCompessEnabled()) {
                visualWordsWithNBT = new ConcurrentHashMap<String, Long>(500);
                visualWordsWithNBT.putAll(visualWords);
                visualWordsWithNBT = getAllValues(visualWordsWithNBT);
            }

            for (Map.Entry<String, Long> entry : visualWords.entrySet()) {

                if(!compress.isCompessEnabled() || (compress.isCompessEnabled() && !compress.isBlackListed(entry.getKey()))) {
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
        }

        //try{Thread.sleep(1000000);}catch(Exception e){};
    }

    public String getHashValue(String mainkey, String haskey) {
        try (Jedis redis = this.redis.getResource()) {
            return redis.hget(this.prefix + mainkey, haskey);
        }
    }
    public Map<String,String> getValue(String mainkey) {
        try (Jedis redis = this.redis.getResource()) {
            return redis.hgetAll(this.prefix + mainkey);
        }

    }

    public ConcurrentHashMap<String, Long> getAllValues(ConcurrentHashMap<String, Long> result) {

        try (Jedis redis = this.redis.getResource()) {
            Pipeline p = redis.pipelined();
            List<Response<String>> hgetsR = new ArrayList<Response<String>>(500);
            List<String> keys = new ArrayList<String>(500);
            Iterator<String> searchKey = result.keySet().iterator();
            int j=0;
            while (searchKey.hasNext()) {
                String k = searchKey.next();
                k=prefix+k;
                keys.add(k);
                //System.out.println(j+" => getAllValues="+k);
                try {
                    hgetsR.add(p.hget(k, "-1"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                j++;
            }


            p.sync();
            for(int i=0;i<keys.size();i++) {
                Response<String> value = hgetsR.get(i);
                try {
                    if(value.get()!=null) { //????
                        String[] keyParts =  keys.get(i).split("#");
                        result.put(keyParts[3], Long.parseLong(value.get()));
                    }

                } catch(NullPointerException e) {
                    //TODO: very bad code => bug in jedis
                    result.put(keys.get(i),0L);
                }

            }
            return result;
        }





    }

    public Map<String,ValueStructure> getAll(List<String> key) {

        List<Response<Map<String, String>>> hgetAllsR = new  ArrayList<Response<Map<String, String>>> (key.size());

        try (Jedis redis = this.redis.getResource()) {
            Pipeline p = redis.pipelined();

            Iterator<String> searchKey = key.iterator();
            while (searchKey.hasNext()) {
                String k = searchKey.next();
                hgetAllsR.add(p.hgetAll(this.prefix +k));

            }
            p.sync();
        }

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

    public Map<String,Map<String,ValueStructure>> getAll(Map<String,List<String>> keysForTV) {
        Long start = System.currentTimeMillis();

        TreeMap<String,List<Response<Map<String, String>>>> hgetAllsR = new  TreeMap<String,List<Response<Map<String, String>>>>();
        try (Jedis redis = this.redis.getResource()) {
            Pipeline p = redis.pipelined();

            for(Map.Entry<String,List<String>> entry : keysForTV.entrySet()) {
                List<Response<Map<String, String>>> req = new ArrayList<>();
                String prefixForTV = this.subPrefix+entry.getKey()+"#";
                Iterator<String> searchKey = entry.getValue().iterator();
                while (searchKey.hasNext()) {
                    String k = searchKey.next();
                    req.add(p.hgetAll(prefixForTV+k));

                }
                hgetAllsR.put(entry.getKey(),req);
            }

            p.sync();
        }

        TreeMap<String,Map<String,ValueStructure>> map = new TreeMap<String,Map<String,ValueStructure>>();



        for(Map.Entry<String,List<Response<Map<String, String>>>> entry : hgetAllsR.entrySet()) {

            List<Response<Map<String, String>>> value = entry.getValue();
            List<String> visualwords = keysForTV.get(entry.getKey());
            Map<String,ValueStructure> subMap = new HashMap<String,ValueStructure>();
            int k=0;
            for(int i=0;i<value.size();i++) {
                Map<String, String> submap = value.get(i).get();
                if(submap!=null) {
                    String nbt = submap.get("-1");
                    if(nbt!=null)
                        subMap.put(visualwords.get(k), new ValueStructure(config, submap, Long.parseLong(nbt)));
                }
                k++;
            }
            map.put(entry.getKey(),subMap);

        }
//        System.out.println("PWET:"+(System.currentTimeMillis()-start));
        return map;


    }

//    @Override
//    public Map<String,Map<String,Map<String,ValueStructure>>> getAll(Map<String,Map<String,List<String>>> keysForTVAndForStorage) {
//        Long start = System.currentTimeMillis();
//        TreeMap<String,TreeMap<String,List<Response<Map<String, String>>>>> hgetAllsR = new  TreeMap<String,TreeMap<String,List<Response<Map<String, String>>>>>();
//        try (Jedis redis = this.redis.getResource()) {
//            Pipeline p = redis.pipelined();
//
//            for(Map.Entry<String,Map<String,List<String>>> entryStorage : keysForTVAndForStorage.entrySet()) {
//                TreeMap<String,List<Response<Map<String, String>>>> hgetAllsRForTV = new  TreeMap<String,List<Response<Map<String, String>>>>();
//                for (Map.Entry<String, List<String>> entryTV : entryStorage.getValue().entrySet()) {
//                    List<Response<Map<String, String>>> req = new ArrayList<>();
//                    String prefixForTV = this.subPrefix + entryStorage.getKey() + "#"+entryTV.getKey() + "#";
//                    Iterator<String> searchKey = entryTV.getValue().iterator();
//                    while (searchKey.hasNext()) {
//                        String k = searchKey.next();
//                        req.add(p.hgetAll(prefixForTV + k));
//
//                    }
//                    hgetAllsRForTV.put(entryTV.getKey(), req);
//                }
//
//
//                String storage = entryStorage.getKey();
//                hgetAllsR.put(storage,hgetAllsRForTV);
//            }
//            p.sync();
//        }
//
//        TreeMap<String,Map<String,Map<String,ValueStructure>>> map = new TreeMap<String,Map<String,Map<String,ValueStructure>>>();
//
//        for(Map.Entry<String,TreeMap<String,List<Response<Map<String, String>>>>> entry : hgetAllsR.entrySet()) {
//            String storage = entry.getKey();
//            TreeMap<String,Map<String,ValueStructure>> mapTV = new TreeMap<String,Map<String,ValueStructure>>();
//            for(Map.Entry<String,List<Response<Map<String, String>>>> entryTV : entry.getValue().entrySet()) {
//                List<Response<Map<String, String>>> value = entryTV.getValue();
//                List<String> visualwords = keysForTVAndForStorage.get(storage).get(entry.getKey());
//                Map<String,ValueStructure> subMap = new HashMap<String,ValueStructure>();
//                int k=0;
//                for(int i=0;i<value.size();i++) {
//                    Map<String, String> submap = value.get(i).get();
//                    if(submap!=null) {
//                        String nbt = submap.get("-1");
//                        if(nbt!=null)
//                            subMap.put(visualwords.get(k), new ValueStructure(config, submap, Long.parseLong(nbt)));
//                    }
//                    k++;
//                }
//                mapTV.put(entry.getKey(),subMap);
//            }
//            map.put(storage,mapTV);
//
//
//        }
//        System.out.println("PWET:"+(System.currentTimeMillis()-start));
//        return map;
//
//
//    }


    public void delete(String key) {
        try (Jedis redis = this.redis.getResource()) {
            redis.del(this.prefix + key);
        }
    }

    public void deleteAll(Map<Long, Integer> mapID)  {

        try (Jedis redis = this.redis.getResource()) {
            Set<String> keys = redis.keys("M*");
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
        }
        //try{Thread.sleep(1000000);}catch(Exception e){};
    }


    public boolean isRessourcePresent(Long id) {
        try (Jedis redis = this.redis.getResource()) {
            Set<String> keys = redis.keys("M*");
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
        }


        return false;
    }

    public void sync()  {
    }

    public void closeIndex() throws Exception {
        try (Jedis redis = this.redis.getResource()) {
            redis.disconnect();
        }

    }

    public void printStat() {

        try (Jedis redis = this.redis.getResource()) {
            System.out.println("INDEX TOTAL SIZE:" + redis.dbSize());
        }
    }
}
