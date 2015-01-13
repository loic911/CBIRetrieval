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
package retrieval.config;

import java.util.Properties;
import org.apache.log4j.Logger;


/**
 * Configuration file for a server
 * @author Rollus Loic
 */
public class ConfigServer extends Config implements Cloneable {   


    /**************************************************************************
     *****
     *****                 Information about quality / speedup  
     ***** 
     *************************************************************************/
    /**
     * Number of patch (N) for request picture
     */
    private int numberOfPatch;
    
    private int numberOfTV;
    /**
     * Resize method use (influence performance)
     * 1=BILL/Graphics2D, 2=PPV/Graphics2D, 3=BILL/AffineTransformOp et 4=PPV/AffineTransformOp
     */
    private int resizeMethod;    
    
    /**
     * Maximum NBT for a visual word (0 = no max)
     * Blacklist visualword with too much entry (reduce index size)
     */
    private int indexCompressThreshold;
    
    /**
     * Maximum percentage of a word from a picture (0 = no max)
     * If a picture that will be indexed has a word that is built up to MAXPERCTENTAGESIMILARWORD%, don't index picture (too homogenous)
     */
    private double maxPercentageSimilarWord;    
    
    
    /**************************************************************************
     *****
     *****       Information about tests vectors and patch build
     ***** Note: Must be the same as central server and other servers config
     ***** 
     *************************************************************************/ 
    /**
     * Path of test vectors
     */
    private String vectorPath;    
    /**
     * Size of patch generation (width)
     */
    private int sizeOfPatchResizeWidth;
    /**
     * Size of patch generation (height)
     */
    private int sizeOfPatchResizeHeight;
    
    
    /**************************************************************************
     *****
     *****                 Information about security 
     ***** 
     *************************************************************************/
    /**
     * Max similar pictures for a request
     */
    private int kMax;
    /**
     * Max client for search (central server) at the same time
     */
    private int searchMax;
    /**
     * Size of server queue
     */
    private int sizeOfIndexQueue;


    /**************************************************************************
     *****
     *****                 Information about database
     ***** 
     *************************************************************************/        
    /**
     * Store name: MEMORY, KYOTO, KYOTOSINGLEFILE, REDIS, NESSDB, BDB, ...
     */
    private String storeName;
    /**
     * Path of index files
     */
    private String indexPath;
    /**
     * Structure type of value (hashmap=0,treemap=1)
     */
    private int strucType;
    /**
     * Default start size for hashmap map with a visual word 
     * (only effect if STRUCTYPE=0)
     * Too low is not good: hashmap will make too much rehash during index process
     * Too hight is not good: hashmap will be too big in memory
     * HASHMAPSTARTSIZE = 0 is no compression
     */
    private int hashMapStartSize;
    
    /**
     * Force sync database on disk avec each index images (only effect with some DB: Redis, BDB,...)
     */
    private boolean syncAfterImage;  
    
    /**
     * Start size for only-memory index
     * Too low is not good: hashmap will make too much rehash during index process
     * Too hight is not good: hashmap will be too big in memory
     */
    private int memoryStartSize;
    
    /**
     * Apox -> tune_alignment: default 3 (8 = 1 cc 3). 
     * DB is build and not updated (0), else more
     */
    private String kyotoApox;
    
    /**
     * Bnum -> tune_buckets: number of bucket (number eof entry * 2)
     */
    private String kyotoBNum;
    
    /**
     * dfunit -> tune_defrag: defrag after x update (default 8): more = quick, less = heavy space
     */
    private String kyotoFUnit;

    /**
     * Cache DB for main index
     */
    private String kyotoCacheMainIndex;
    
    /**
     * Cache DB for metadata index
     */    
    private String kyotoCacheMetadata;
    
    /**
     * Cache DB for compress index
     */
    private String kyotoCacheCompress;

    /**
     * Redis host adress
     */
    private String redisHost;
    
    /**
     * Redis host port
     */
    private String redisPort;

    private static Logger logger = Logger.getLogger(ConfigServer.class);

    public ConfigServer(String configFileServer) throws Exception {
        logger.info("Read config file:"+configFileServer);
        Properties p = null;

        p = read(configFileServer);
        logger.info("Config init");
        
        numberOfPatch = Integer.parseInt(p.getProperty("NUMBEROFPATCH", propertiesError));
        numberOfTV = Integer.parseInt(p.getProperty("NUMBEROFTV", propertiesError));
        
        resizeMethod = Integer.parseInt(p.getProperty("RESIZEMETHOD", propertiesError));
        indexCompressThreshold = Integer.parseInt(p.getProperty("INDEXCOMPRESSTHRESHOLD", propertiesError)); 
        maxPercentageSimilarWord = Double.parseDouble(p.getProperty("MAXPERCTENTAGESIMILARWORD", propertiesError));      
        
        vectorPath = p.getProperty("VECTORPATH", "ERRORPROPERTIES");
        sizeOfPatchResizeWidth = Integer.parseInt(p.getProperty("SIZEOFPATCHRESIZEWIDTH", propertiesError));
        sizeOfPatchResizeHeight = Integer.parseInt(p.getProperty("SIZEOFPATCHRESIZEHEIGHT", propertiesError));
        
        
        kMax = Integer.parseInt(p.getProperty("KMAX", propertiesError));
        searchMax = Integer.parseInt(p.getProperty("SEARCHMAX", propertiesError));
        sizeOfIndexQueue = Integer.parseInt(p.getProperty("SIZEOFINDEXQUEUE", propertiesError));
       

        indexPath = p.getProperty("INDEXPATH", propertiesError);
        storeName = p.getProperty("STORENAME", propertiesError);
        strucType = Integer.parseInt(p.getProperty("STRUCTYPE", propertiesError));
        hashMapStartSize = Integer.parseInt(p.getProperty("HASHMAPSTARTSIZE", propertiesError));
        syncAfterImage = Boolean.parseBoolean(p.getProperty("SYNCAFTERIMAGE", propertiesError));
        
        
        memoryStartSize = Integer.parseInt(p.getProperty("MEMORYSTARTSIZE", propertiesError));

        kyotoApox = p.getProperty("KYOTOAPOX", propertiesError);
        kyotoBNum = p.getProperty("KYOTOBNUM", propertiesError);
        kyotoFUnit = p.getProperty("KYOTODFUNIT", propertiesError);
        kyotoCacheMainIndex = p.getProperty("KYOTOCACHEMAININDEX", propertiesError);
        kyotoCacheMetadata = p.getProperty("KYOTOCACHEMETADATA", propertiesError);
        kyotoCacheCompress = p.getProperty("KYOTOCACHECOMPRESS", propertiesError);

        redisHost = p.getProperty("REDISHOST", propertiesError);
        redisPort = p.getProperty("REDISPORT", propertiesError);
    }

    @Override
        public ConfigServer clone() throws CloneNotSupportedException {
                ConfigServer newC = (ConfigServer) super.clone();
                return newC;
        }

    
    /**
     * Compute Kyoto cache size of one database share accross all index
     * @return MB value (format XXXMB)
     */    
    public String getKyotoCacheSizeForAll() {
        int s = 1; //int s = NUMBEROFSUBSERVER;
        int t = 1;
        String sizeMain = getKyotoCacheMainIndex();
        Long size = convertLongSize(sizeMain);
        Long sizeForIndex =  (size/s)/t;
        String sizeFull = sizeForIndex + "MB"; 
        logger.info("KYOTOCACHE="+sizeFull);       
        return sizeFull;
    }    

    /**
     * Compute Kyoto cache size of one metadata index
     * @return MB value (format XXXMB)
     */
    public String getKyotoCacheSizeForMetaData() {
        int s = 1; //NUMBEROFSUBSERVER;
        String sizeMeta = getKyotoCacheMetadata();
        logger.info("NUMBEROFSUBSERVER="+s + "KYOTOCACHEMETADATA="+sizeMeta);
        Long size = convertLongSize(sizeMeta);
        Long sizeForIndex =  (size/s)/4; //3 because 1 path, 1 reverse path and 1 patchs (4 for optim)
        return sizeForIndex + "MB";
    }
    
    /**
     * Convert a G/MB string value to a long value of MB
     * @param sizeMain String value format X...XG or X...XMB]
     * @return Number value
     */
    private Long convertLongSize(String sizeMain) {
        sizeMain = sizeMain.replace("G", "000M"); //replace GB size by MB
        sizeMain = sizeMain.replaceAll("[a-zA-Z]", ""); //Just keep long value in MB
        logger.info("KYOTOCACHEMAININDEX in MB="+sizeMain);
        return Long.parseLong(sizeMain);
    }  
    
   public static ConfigServer getConfigServerForTest() {
       try {
           return new ConfigServer("config/ConfigServer.prop");
       }
       catch(Exception e) {
           return null;
       }
   }    

    /**
     * @return the numberOfTestVector
     */
    public int getNumberOfTestVector() {
        return numberOfTV;
    }

    /**
     * @return the numberOfPatch
     */
    public int getNumberOfPatch() {
        return numberOfPatch;
    }

    /**
     * @param numberOfPatch the numberOfPatch to set
     */
    public void setNumberOfPatch(int numberOfPatch) {
        this.numberOfPatch = numberOfPatch;
    }

    /**
     * @return the resizeMethod
     */
    public int getResizeMethod() {
        return resizeMethod;
    }

    /**
     * @param resizeMethod the resizeMethod to set
     */
    public void setResizeMethod(int resizeMethod) {
        this.resizeMethod = resizeMethod;
    }

    /**
     * @return the indexCompressThreshold
     */
    public int getIndexCompressThreshold() {
        return indexCompressThreshold;
    }

    /**
     * @param indexCompressThreshold the indexCompressThreshold to set
     */
    public void setIndexCompressThreshold(int indexCompressThreshold) {
        this.indexCompressThreshold = indexCompressThreshold;
    }

    /**
     * @return the maxPercentageSimilarWord
     */
    public double getMaxPercentageSimilarWord() {
        return maxPercentageSimilarWord;
    }

    /**
     * @param maxPercentageSimilarWord the maxPercentageSimilarWord to set
     */
    public void setMaxPercentageSimilarWord(double maxPercentageSimilarWord) {
        this.maxPercentageSimilarWord = maxPercentageSimilarWord;
    }

    /**
     * @return the vectorPath
     */
    public String getVectorPath() {
        return vectorPath;
    }

    /**
     * @param vectorPath the vectorPath to set
     */
    public void setVectorPath(String vectorPath) {
        this.vectorPath = vectorPath;
    }

    /**
     * @return the sizeOfPatchResizeWidth
     */
    public int getSizeOfPatchResizeWidth() {
        return sizeOfPatchResizeWidth;
    }

    /**
     * @param sizeOfPatchResizeWidth the sizeOfPatchResizeWidth to set
     */
    public void setSizeOfPatchResizeWidth(int sizeOfPatchResizeWidth) {
        this.sizeOfPatchResizeWidth = sizeOfPatchResizeWidth;
    }

    /**
     * @return the sizeOfPatchResizeHeight
     */
    public int getSizeOfPatchResizeHeight() {
        return sizeOfPatchResizeHeight;
    }

    /**
     * @param sizeOfPatchResizeHeight the sizeOfPatchResizeHeight to set
     */
    public void setSizeOfPatchResizeHeight(int sizeOfPatchResizeHeight) {
        this.sizeOfPatchResizeHeight = sizeOfPatchResizeHeight;
    }

    /**
     * @return the kMax
     */
    public int getkMax() {
        return kMax;
    }

    /**
     * @param kMax the kMax to set
     */
    public void setkMax(int kMax) {
        this.kMax = kMax;
    }

    /**
     * @return the searchMax
     */
    public int getSearchMax() {
        return searchMax;
    }

    /**
     * @param searchMax the searchMax to set
     */
    public void setSearchMax(int searchMax) {
        this.searchMax = searchMax;
    }

    /**
     * @return the sizeOfIndexQueue
     */
    public int getSizeOfIndexQueue() {
        return sizeOfIndexQueue;
    }

    /**
     * @param sizeOfIndexQueue the sizeOfIndexQueue to set
     */
    public void setSizeOfIndexQueue(int sizeOfIndexQueue) {
        this.sizeOfIndexQueue = sizeOfIndexQueue;
    }

    /**
     * @return the storeName
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * @param storeName the storeName to set
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /**
     * @return the indexPath
     */
    public String getIndexPath() {
        return indexPath;
    }

    /**
     * @param indexPath the indexPath to set
     */
    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    /**
     * @return the strucType
     */
    public int getStrucType() {
        return strucType;
    }

    /**
     * @param strucType the strucType to set
     */
    public void setStrucType(int strucType) {
        this.strucType = strucType;
    }

    /**
     * @return the hashMapStartSize
     */
    public int getHashMapStartSize() {
        return hashMapStartSize;
    }

    /**
     * @param hashMapStartSize the hashMapStartSize to set
     */
    public void setHashMapStartSize(int hashMapStartSize) {
        this.hashMapStartSize = hashMapStartSize;
    }

    /**
     * @return the syncAfterImage
     */
    public boolean isSyncAfterImage() {
        return syncAfterImage;
    }

    /**
     * @param syncAfterImage the syncAfterImage to set
     */
    public void setSyncAfterImage(boolean syncAfterImage) {
        this.syncAfterImage = syncAfterImage;
    }

    /**
     * @return the memoryStartSize
     */
    public int getMemoryStartSize() {
        return memoryStartSize;
    }

    /**
     * @param memoryStartSize the memoryStartSize to set
     */
    public void setMemoryStartSize(int memoryStartSize) {
        this.memoryStartSize = memoryStartSize;
    }

    /**
     * @return the kyotoApox
     */
    public String getKyotoApox() {
        return kyotoApox;
    }

    /**
     * @param kyotoApox the kyotoApox to set
     */
    public void setKyotoApox(String kyotoApox) {
        this.kyotoApox = kyotoApox;
    }

    /**
     * @return the kyotoBNum
     */
    public String getKyotoBNum() {
        return kyotoBNum;
    }

    /**
     * @param kyotoBNum the kyotoBNum to set
     */
    public void setKyotoBNum(String kyotoBNum) {
        this.kyotoBNum = kyotoBNum;
    }

    /**
     * @return the kyotoFUnit
     */
    public String getKyotoFUnit() {
        return kyotoFUnit;
    }

    /**
     * @param kyotoFUnit the kyotoFUnit to set
     */
    public void setKyotoFUnit(String kyotoFUnit) {
        this.kyotoFUnit = kyotoFUnit;
    }

    /**
     * @return the kyotoCacheMainIndex
     */
    public String getKyotoCacheMainIndex() {
        return kyotoCacheMainIndex;
    }

    /**
     * @param kyotoCacheMainIndex the kyotoCacheMainIndex to set
     */
    public void setKyotoCacheMainIndex(String kyotoCacheMainIndex) {
        this.kyotoCacheMainIndex = kyotoCacheMainIndex;
    }

    /**
     * @return the kyotoCacheMetadata
     */
    public String getKyotoCacheMetadata() {
        return kyotoCacheMetadata;
    }

    /**
     * @param kyotoCacheMetadata the kyotoCacheMetadata to set
     */
    public void setKyotoCacheMetadata(String kyotoCacheMetadata) {
        this.kyotoCacheMetadata = kyotoCacheMetadata;
    }

    /**
     * @return the kyotoCacheCompress
     */
    public String getKyotoCacheCompress() {
        return kyotoCacheCompress;
    }

    /**
     * @param kyotoCacheCompress the kyotoCacheCompress to set
     */
    public void setKyotoCacheCompress(String kyotoCacheCompress) {
        this.kyotoCacheCompress = kyotoCacheCompress;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public String getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(String redisPort) {
        this.redisPort = redisPort;
    }
}
