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
package retrieval.server.globaldatabase;

import java.util.List;
import java.util.Map;

/**
 * This represent a GlobalDatabase.
 * Generic return type because a database may be a hashmap, jedis db object,...
 * @author lrollus
 */
public interface GlobalDatabase {
    
    Object getDatabase();
    
    Object getDatabasePatchs();
    
    Object getDatabaseProperties();
    
    Object getDatabaseCompress();
    
    Object getDatabaseStorage();
    
    List<String> getStorages();
    
    void addStorage(String name);
    
    void deleteStorage(String name);
    
    void putToPurge(String storage,Map<Long, Integer> toPurge);
    
    Map<Long, Integer> getPicturesToPurge(String storage);
    
    void clearPurge(String storage); 
    
}
