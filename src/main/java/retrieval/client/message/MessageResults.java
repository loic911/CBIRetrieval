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
package retrieval.client.message;

import retrieval.client.ListServerInformationSocket;
import retrieval.storage.index.ResultSim;

import java.util.List;

/**
 * Message for XML results
 * Many servers will send this message to a central server (similar pitcures).
 * A Central server will send this message to a client
 * (similar pictures + server state)
 * and a list of server state
 * @author Rollus Loic
 */
public class MessageResults {

    /**
     * Lists of ordered similar pictures
     */
    List<ResultSim> lists;
    /**
     * Lists of server
     */
    private ListServerInformationSocket servers;
    
    /**
     * Total number of pictures of all servers
     */
    private int numberOfPicturesInIndex;
    
    private Integer serverKey;

    /**
     * Constructor for a result message
     * @param lists List of results
     * @param numberOfPictures Size of index
     */
    public MessageResults(List<ResultSim> lists, int numberOfPictures) {
        this.lists = lists;
        this.numberOfPicturesInIndex = numberOfPictures;
        this.servers = null;
    }

    /**
     * Constructor for a result message
     * @param lists List of results
     * @param servers List of server information
     * @param numberOfPictures Size of index
     */
    public MessageResults(
            List<ResultSim> lists,
            ListServerInformationSocket servers,
            int numberOfPictures) {
        this.lists = lists;
        this.servers = servers;
        this.numberOfPicturesInIndex = numberOfPictures;
    }

 

    /**
     * Get most similar pictures
     * @return Most similar pictures
     */
    public List<ResultSim> getResults() {
        return lists;
    }

}
