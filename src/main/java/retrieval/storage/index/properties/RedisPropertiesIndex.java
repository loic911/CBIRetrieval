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

package retrieval.storage.index.properties;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.StartIndexException;

import java.util.*;

/**
 *
 * @author lrollus
 */
public class RedisPropertiesIndex implements PicturePropertiesIndex{
    private Jedis redis;
    protected String prefix;
    protected String idServer;

    public static int REDIS_PROPERTIES_STORE = 3;

    /**
     * Number of pictures indexed
     */
    public long numberOfItem;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(RedisPropertiesIndex.class);

    /**
     * Constructor for a BDB path index
     * @throws retrieval.storage.exception.StartIndexException Error during the start of index
     */
    public RedisPropertiesIndex(GlobalDatabase global, String idServer) throws StartIndexException {

        try {
            logger.info("KyotoCabinetPathIndexSingleFile: start");
            this.idServer = idServer;
            Jedis base = (Jedis)global.getDatabaseProperties();

            this.redis = new Jedis(base.getClient().getHost(),base.getClient().getPort(),20000);
            System.out.println("redis="+ getRedis().getDB());
            this.getRedis().select(3);
            System.out.println("redis="+ getRedis().getDB());
            this.prefix = idServer + "#";

            ///if empty insert first tuple
            logger.info("getSize="+getSize());
            if (getSize() == 0) {
                setCountValue(0);
            }
            logger.info("getSize="+getSize());

        } catch (Exception e) {
            throw new StartIndexException(e.getMessage());
        }
    }



    public int getCountValue() {
       String data = getRedis().hget("COUNT#" + idServer, "CBIR");
       if(data==null) {
           return 0;
       }
       int lValue = Integer.parseInt(data);
       return lValue;
    }

    public void setCountValue(long value) {
        getRedis().hset("COUNT#" + idServer, "CBIR", value + "");
    }

    public void incrCountSize() {
       int value = getCountValue();
       value++;
       setCountValue(value);
    }

    public void decrCountSize() {
       int value = getCountValue();
       value--;
       setCountValue(value);
    }

    /**
     * Get the size of the map
     * @return Size of the map
     */
    public int getSize() {
        int count = getCountValue();
        if(count==-1) {
            count=0;
        }
        return count;
    }

    /**
     * Add a new picture path, generate a new id and get the id
     * @param id Picture path
     * @return Picture ID
     */
    public Long addPicture(Long id, Map<String,String> properties) {
        try {
            getRedis().hset(this.prefix + id, "CBIRTRUE", "CBIRTRUE");
            for(Map.Entry<String,String> prop : properties.entrySet()) {
                getRedis().hset(this.prefix + id, prop.getKey(), prop.getValue());
            }
            incrCountSize();
            Date date = Calendar.getInstance().getTime();
            logger.info(";" + date.getTime() + ";" + "" + id + ";" + properties);
            return id;
        } catch (Exception ex) {
            logger.error(ex.toString());
            return -1l;
        }
    }

    /**
     * Get a map with all pictures
     * @return All pictures map
     */
    public List<Long> getIdsList() {
        List<Long> list = new ArrayList<Long>();

        Set<String> keys = getRedis().keys("*");
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            System.out.println("key="+key + " => " + getRedis().getDB() + " " + getRedis().getClient());
            getRedis().select(2);
            System.out.println("key="+key + " => " + getRedis().getDB() + " " + getRedis().getClient());
            if(key.startsWith(prefix)) {
                list.add(Long.parseLong(key.replaceFirst(prefix, "")));
            }
        }

        return list;
    }

    /**
     * Get a picture path from the image ID
     * @return Picture path
     */
    public Map<String,String> getPictureProperties(Long id) {
        Map<String,String> properties = getRedis().hgetAll(this.prefix + id);
        properties.remove("CBIRTRUE");
        return properties;
    }


    /**
     * Get a map with all pictures
     * @return All pictures map
     */
    public Map<Long, Map<String,String>> getMap() {
        Map<Long, Map<String,String>> hashmap = new HashMap<Long, Map<String,String>>(2048);
        List<Long> ids = getIdsList();

        for(Long id : ids) {
            hashmap.put(id,getPictureProperties(id));
        }

        return hashmap;
    }


    public boolean containsPicture(Long id) {
        return getRedis().hget(this.prefix + id, "CBIRTRUE") != null;
    }
    /**
     * Print index
     */
    public void print() {
        logger.info(getMap());
    }

    /**
     * Delete all path from picturesPath list and get their id
     * @param ids Pictures paths that mus be delete
     * @return Pictures paths deleted id
     */
    public Map<Long, Integer> delete(List<Long> ids) {
        Map<Long, Integer> picturesID = new HashMap<Long, Integer>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            //logger.info("delete: " + ids.get(i));
            String value = getRedis().hget(this.prefix + ids.get(i), "CBIRTRUE");
            if(value!=null) {
                logger.info("delete: id=" + ids.get(i));
                picturesID.put(ids.get(i), 0);
                getRedis().del(this.prefix + ids.get(i));
                decrCountSize();
            }

        }
        return picturesID;
    }

    /**
     * Close index
     * @throws retrieval.storage.exception.CloseIndexException Exception during the close
     */
    public void close() throws CloseIndexException {
        //closer bdb
        try {
            closeBDB();
        } catch (Exception e) {
            logger.error(e.toString());
            throw new CloseIndexException();
        }
    }

    /** Closes the database. */
    private void closeBDB() throws Exception {
        getRedis().close();
    }

    public void sync() {

    }

    /**
     * BDB database object for map and mapreverse
     */
    public Jedis getRedis() {
        redis.select(REDIS_PROPERTIES_STORE);
        return redis;
    }
}
