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

import org.apache.log4j.Logger;
import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerDistantStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implement a Indexer to manage picture on simple server with socket/xml
 * @author Rollus Loic
 */
public class RetrievalDeleterMain {

private static Logger logger = Logger.getLogger(RetrievalDeleterMain.class);
    /**
     * Main method for deleter
     * Param0: Server URL
     * param1: Server port
     * Param3: Picture ids (comma sep)
     * @param args Params arays
     */
    public static void main(String[] args) throws Exception {
        delete(args);
    }
       
    private static void delete(String[] args) throws Exception{
        String host = args[0];
        int port = Integer.parseInt(args[1]); 
        String[] idsSplit = args[2].split(",");
        List<Long> ids = new ArrayList<Long>();
        for(int i=0;i<idsSplit.length;i++) {
            ids.add(Long.parseLong(idsSplit[i]));
        }
        
       logger.info("DELETE Host:"+host + " Port:"+port + " Image:"+ ids);
       
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(host,port,null,false);
        index.delete(ids);
    }    
}
