/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server.index.main.kyoto;

import com.google.protobuf.InvalidProtocolBufferException;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import kyotocabinet.Cursor;
import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.server.exception.ReadIndexException;
import retrieval.server.index.ValueStructure;
import retrieval.server.index.main.HashTableIndex;
import retrieval.utils.ConvertUtils;
import retrieval.utils.Value.ValueInfo;

/**
 *
 * @author lrollus
 */
public abstract class KyotoCabinetHashTableAbstract extends HashTableIndex {

    /**
     * HashTable
     */
    protected DB hashmap;
    protected String prefix = ""; //prefix that indicate the testvector in eahc value
    protected int visuwalWordPosition = -1; //(0:testvector id, 1: vw)
    /**
     * Configuration object
     */
    protected ConfigServer config;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(KyotoCabinetHashTableAbstract.class);

    /**
     * Put a key and its value on the store
     * @param key Key
     * @param Value Value
     */
    public synchronized void put(String key, ValueStructure Value) {
        String keyPrefixed = prefix+key;
        hashmap.set(keyPrefixed.getBytes(), ConvertUtils.convertObjectToProtoBuf(Value).toByteArray());
    }

    /**
     * Get a value of the key
     * @param key Key
     * @return Value
     */
    public synchronized ValueStructure get(String key) {

        String keyPrefixed = prefix+key;
        byte[] data = hashmap.get(keyPrefixed.getBytes());
                
        if (data != null) {
            try {
                return ConvertUtils.convertProtoBufToValueStructure(ValueInfo.parseFrom(data), config);
            } catch (Exception e) {
                logger.error(e);
                return null;
            }
        } else {
            return null;
        }
    }

    public Map<String, ValueStructure> getAll(List<String> key) {
        Map<String, ValueStructure> map = new HashMap<String, ValueStructure>(key.size() * 2);
        byte[][] keyBytes = new byte[key.size()][];
        for (int i = 0; i < key.size(); i++) {
            String keyPrefixed = prefix+key.get(i);
            keyBytes[i] = keyPrefixed.getBytes();
        }

        byte[][] result = hashmap.get_bulk(keyBytes, true);
        if (result != null) {
            for (int i = 0; i < result.length; i = i + 2) {
                //i=key, i+1=value
                byte[] keyData = result[i];
                byte[] valueData = result[i + 1];
                if (valueData != null) {
                    try {
                        ValueStructure value = ConvertUtils.convertProtoBufToValueStructure(ValueInfo.parseFrom(valueData), config);
                        String fullKey = new String(keyData);
                        String correctKey = fullKey.split("#")[visuwalWordPosition];
                        map.put(correctKey, value);
                    } catch (InvalidProtocolBufferException ex) {
                        logger.error(ex);
                    }
                }
            }
        }
        return map;
    }
    
    public ConcurrentHashMap<String, Long> fillAllEntry(ConcurrentHashMap<String, Long> visualWord) {
        Iterator<String> searchKey = visualWord.keySet().iterator();
        byte[][] keyBytes = new byte[visualWord.size()][];
        int k = 0;
        while (searchKey.hasNext()) {
            String key = searchKey.next();
            String keyPrefixed = prefix+key;
            keyBytes[k] = keyPrefixed.getBytes();
            k++;
        }

        byte[][] result = hashmap.get_bulk(keyBytes, true);

        if (result != null) {
            for (int i = 0; i < result.length; i = i + 2) {
                byte[] keyData = result[i];
                byte[] valueData = result[i + 1];
                String fullKey = new String(keyData);
                String correctKey = fullKey.split("#")[visuwalWordPosition];
                if (valueData != null) {
                    try {
                        ValueStructure value = ConvertUtils.convertProtoBufToValueStructure(ValueInfo.parseFrom(valueData), config);

                        visualWord.put(correctKey, value != null ? value.getNBT() : 0);
                    } catch (InvalidProtocolBufferException ex) {
                        logger.error(ex);
                    }
                } else {
                    visualWord.put(correctKey, 0L);
                }
            }
        }
        return visualWord;
    }

    /**
     * Delete a map of key on the concurrent store
     * @param mapID Key that must be deleted
     */
    public void deleteAll(Map<Long, Integer> mapID) {
        logger.info("deleteAll:" + mapID.size());
        List<String> emptyKeys = new ArrayList<String>();
        //for each element in the main map
        Cursor cur = hashmap.cursor();
        cur.jump();
        byte[][] rec;
        while ((rec = cur.get(true)) != null) {
            try {
                String key = new String(rec[0]);
                ValueStructure value = ConvertUtils.convertProtoBufToValueStructure(ValueInfo.parseFrom(rec[1]), config);
                value.deleteValue(mapID);
                if (value.getEntries().isEmpty()) {
                    emptyKeys.add(key);
                } else {
                    hashmap.replace(key.getBytes(), ConvertUtils.convertObjectToProtoBuf(value).toByteArray());
                }
            } catch (InvalidProtocolBufferException ex) {
                java.util.logging.Logger.getLogger(KyotoCabinetHashTableAbstract.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        for(int i=0;i<emptyKeys.size();i++) {

             hashmap.remove(emptyKeys.get(i).getBytes());
        }
    }

    public boolean isRessourcePresent(Long id) {
        Cursor cur = hashmap.cursor();
        cur.jump();
        byte[] rec;
        while ((rec = cur.get_value(true)) != null) {
            try {
                ValueStructure value = ConvertUtils.convertProtoBufToValueStructure(ValueInfo.parseFrom(rec), config);
                if (value.isPicturePresent(id)){
                    return true;
                }
            } catch (InvalidProtocolBufferException ex) {
                java.util.logging.Logger.getLogger(KyotoCabinetHashTableAbstract.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     * Print map stat
     */
    public void printStat() {
        logger.info("INDEX TOTAL SIZE:"+hashmap.count());
        logger.info("STATUS:"+hashmap.status());
    }

    /**
     * Close index
     * @throws Exception Error during close
     */
    public void closeIndex() throws Exception {
        logger.info("Close database");
        hashmap.close();

    }

    public void sync() {
        //memory-only so no sync between memory and disk
    }

    @Override
    public void delete(String key) {
        String keyPrefixed = prefix+key;
        hashmap.remove(keyPrefixed);
    }
}
