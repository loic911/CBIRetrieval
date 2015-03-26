/*
 * Copyright 2015 ROLLUS Loïc
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
package retrieval.server.globaldatabase;

import kyotocabinet.Cursor;
import kyotocabinet.DB;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.storage.Storage;
import retrieval.storage.exception.ReadIndexException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lrollus
 */
public class KyotoCabinetDatabase implements GlobalDatabase{
    private static Logger logger = Logger.getLogger(Storage.class);
    private DB database;
    private DB databasePatchs;
    private DB databasePath;
    private DB databaseCompress;
    private DB databaseStorage;
    private DB databasePurge;
    
    public KyotoCabinetDatabase(ConfigServer config) throws ReadIndexException {
        logger.info("KyotoCabinetDatabase: start");
        try {
            logger.info("KyotoCabinetDatabase: Creating database...");
            database = openDatabase("main.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForAll(),config.getKyotoFUnit());
            databasePatchs = openDatabase("patchs.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForMetaData(),config.getKyotoFUnit());
            databasePath = openDatabase("path.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForMetaData(),config.getKyotoFUnit());
            databaseCompress = openDatabase("compress.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForMetaData(),config.getKyotoFUnit());
            databaseStorage = openDatabase("storage.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForMetaData(),config.getKyotoFUnit());
            databasePurge = openDatabase("purge.kch",config.getIndexPath(),config.getKyotoApox(),config.getKyotoBNum(),config.getKyotoCacheSizeForMetaData(),config.getKyotoFUnit());
            logger.info("KyotoCabinetDatabase: Database ready!");
        } catch (Exception e) {
            throw new ReadIndexException(e.toString());
        }        
    }    
    
    public static DB openDatabase(String file, String indexPath, String apox, String bnum, String cache, String funit) {
            logger.info("KyotoCabinetMultipleFile: read old index");
            DB kyotoDB = new DB();
            logger.info("Open: " + indexPath + file);
            /**
             * apox -> tune_alignment : default 3 (8 = 1 << 3). DB is build and not updated (0), else more
             * fpow -> tune_fbp : default 10 (1024 = 1 << 10). Most case, don't modify
             * opts -> tune_options : s (small, max 16GB) c (compress), l (linear) => linear
             * bnum -> tune_buckets : number of bucket (number eof entry * 2)
             * msiz -> tune_map : size in memory
             * dfunit -> tune_defrag : defrag after x update (default 8): more = quick, less = heavy space
             */
            String filename = indexPath + file;
            String APOX = "apox=" + apox;
            String BNUM = "bnum=" + bnum;
            String MSIZ = "msiz=" + cache;
            String DFUNIT = "dfunit=" + funit;
            String openConfigPath = filename + "#" + APOX + "#" + BNUM + "#" + MSIZ + "#" + DFUNIT;
            logger.info("KyotoCabinetMultipleFile: open database with: " + openConfigPath);

            File indexPathDir = new File(indexPath);
            logger.info("Index path="+indexPathDir.getAbsolutePath());
            if (!indexPathDir.exists()) {
                indexPathDir.mkdirs();
            }

            if (!kyotoDB.open(openConfigPath, DB.OWRITER | DB.OCREATE)) {
                logger.error("open error: " + kyotoDB.error());
            } 
            return kyotoDB;
    }

    
    public Object getDatabase() {
       return (Object)database; 
    }  
    public Object getDatabasePatchs() {
       return (Object)databasePatchs; 
    }  
    public Object getDatabaseProperties() {
       return (Object)databasePath; 
    } 
    
    public Object getDatabaseCompress() {
        return (Object)databaseCompress;
    }
    
    public Object getDatabaseStorage() {
        return (Object)databaseStorage;
    }   
    
    public List<String> getStorages() {
        List<String> storages = new ArrayList<String>();
        Cursor cur = databaseStorage.cursor();
        cur.jump();
        String[] rec;
        while ((rec = cur.get_str(true)) != null) {
            storages.add(rec[0]);
        }
        cur.disable(); 
        return storages;
    }
    
    public void addStorage(String name) {
        databaseStorage.set(name, "");
    }
    
    public void deleteStorage(String name) {
        databaseStorage.remove(name);
    }    

    public void putToPurge(String storage, Map<Long, Integer> toPurge) {
//        byte[] data = SerializationUtils.serialize(yourObject);
//        YourObject yourObject = (YourObject) SerializationUtils.deserialize(byte[] data)
        HashMap<Long,Integer> map;
        byte[] data = databasePurge.get(SerializationUtils.serialize(storage));
        if(data!=null) {
            map = (HashMap<Long,Integer>) SerializationUtils.deserialize(data);
        } else {
            map = new HashMap<Long,Integer>();
        }
        map.putAll(toPurge);
        databasePurge.set(SerializationUtils.serialize(storage),SerializationUtils.serialize(map));       
    }

    public Map<Long, Integer> getPicturesToPurge(String storage) {
         HashMap<Long,Integer> map;
        byte[] data = databasePurge.get(SerializationUtils.serialize(storage));
        if(data!=null) {
            map = (HashMap<Long,Integer>) SerializationUtils.deserialize(data);
        } else {
            map = new HashMap<Long,Integer>();
        }
        return map;       
    }

    public void clearPurge(String storage) {
        databasePurge.set(SerializationUtils.serialize(storage),SerializationUtils.serialize(new HashMap<Long,Integer>()));  
    }
    
}
