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
package retrieval.storage.index;

import java.util.Map;
import org.apache.log4j.Logger;
import retrieval.server.globaldatabase.GlobalDatabase;

/**
 * This class implement a set of pictures that has been deleted from server but
 * that already exists on server index. Server must be purge to delete all these data.
 * @author lrollus
 */
public class PicturesToPurge {
    
    private static Logger logger = Logger.getLogger(PicturesToPurge.class);
    
    private String idServer;
    
    private GlobalDatabase database;
    
    
    public PicturesToPurge(String idServer,GlobalDatabase database) {
        logger.info("PicturesToPurge "+database);
        this.idServer = idServer;
        this.database = database;
    }

    public int size() {
        logger.info("PicturesToPurge "+database);
        logger.info("PicturesToPurge "+database.getPicturesToPurge(idServer));
        return database.getPicturesToPurge(idServer).size();
    }
    
    public void putToPurge(Map<Long, Integer> toPurge) {
        database.putToPurge(idServer, toPurge);
    }
    
    public Map<Long, Integer> getPicturesToPurge() {
        return database.getPicturesToPurge(idServer);
    }
    
    public void clear() {
        database.clearPurge(idServer);       
    }
    
}
