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


/**
 *
 * @author lrollus
 */
public class KyotoCabinetHashTableMultipleFile extends KyotoCabinetHashTableAbstract {

    /**
     * Name of Hashtable
     */
    public static String NAME = "KYOTOMULTIPLEFILE";

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(KyotoCabinetHashTableMultipleFile.class);

    /**
     * Constructor for a memory Hash Table
     * @param file Name of file which will contain store
     * @param configStore Configuration object
     * @param read If true, read index (if already exist), else create new index
     * @throws ReadIndexException Error during the read of index
     */
    public KyotoCabinetHashTableMultipleFile(DB map,String idTestVector, ConfigServer config) throws ReadIndexException {
        logger.info("KyotoCabinetMultipleFile: start");
        this.config = config;
        this.hashmap = map;
        this.prefix = idTestVector+"#";
        this.visuwalWordPosition = 1;
    }
}
