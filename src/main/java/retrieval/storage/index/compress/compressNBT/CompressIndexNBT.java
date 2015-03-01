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

import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.StartIndexException;

import java.util.Map;

/**
 * The main Retrieval map store a lot of key value
 * This compress index remove key-value if the value is too big.
 * If a visualword (key) has too much data, these data will not be interesting (low value) but the visualword will take memory and CPU
 * @author lrollus
 */
public abstract class CompressIndexNBT {
    
    /**
     * if visualword.nbt > thresholdNBT => delete visual word, black list visualword and remove it from all servers
     **/
    long thresholdNBT;

    public static CompressIndexNBT getCompressIndexFactory(ConfigServer config, String idStorage, String idTV,Object globalDatabase) throws StartIndexException {
        if(config.getStoreName().equals("MEMORY")) {
            return new HashMapCompressIndex(config);
        }  else if(config.getStoreName().equals("KYOTOSINGLEFILE")) {
            return new KyotoCompressIndex((GlobalDatabase)globalDatabase,config,idStorage,idTV);
        }else if(config.getStoreName().equals("REDIS")) {
            return new RedisCompressIndex((GlobalDatabase)globalDatabase,config,idStorage,idTV);
        }
        throw new StartIndexException(config.getStoreName() + " is not implemented for compress index");
    }

    /**
     * Create a compress NBT index
     * @param thresholdNBT 
     */
    protected CompressIndexNBT(long thresholdNBT) {
        this.thresholdNBT = thresholdNBT;
    }

    /**
     * Check if NBT is too big
     * @param nbt
     */
    public boolean isNBTTooBig(Long nbt) {
        return thresholdNBT>0? nbt > thresholdNBT: false;
    }

    /**
     * Blacklist a visualword
     * @param b visualword
     */
    public abstract void blacklistVW(String b);
    
    /**
     * Retrieve all blacklisted VW
     */
    public abstract Map<String,Integer> getBlacklistedVW();
    
    /**
     * Check if a visualword is blacklisted in the storage for this test vector
     * @param b visualword
     * @return true if b is blacklisted
     */
    public abstract boolean isBlackListed(String b);
    
    /**
     * Check if the NBT compression is enabled
     * @return 
     */
    public boolean isCompessEnabled() {
        return thresholdNBT>0;
    }
}
