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
package retrieval.indexer.main;

import java.util.Map;
import retrieval.indexer.RetrievalIndexerDistantStorage;

/**
 * This class implement a Indexer to manage picture on simple server with socket/xml
 * @author Rollus Loic
 */
public class RetrievalStoragesMain {


    /**
     * Main methode for info
     * Param0: Server HOST
     * param1: Server port
     * Param2: Storage name 
     * @param args Params arays
     */
    public static void main(String[] args) throws Exception {
        info(args);
    }
     
    private static void info(String[] args) throws Exception {
       String host = args[0];
        int port = Integer.parseInt(args[1]);
        
        RetrievalIndexerDistantStorage index = new RetrievalIndexerDistantStorage(host,port,null,true);
  
        Map<String,Long> maps = index.listStorages();
        
        System.out.println("There are " + maps.size() + " storages");
        for(Map.Entry<String,Long> entry : maps.entrySet()) {
            System.out.println("Storage '" + entry.getKey() + "' has "+entry.getValue() + " images");          
        }
    }
}
