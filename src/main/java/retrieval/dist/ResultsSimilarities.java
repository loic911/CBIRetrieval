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
package retrieval.dist;

import java.util.ArrayList;
import java.util.List;
import retrieval.client.ListServerInformationSocket;
import retrieval.server.RetrievalServer;
import retrieval.storage.index.ResultSim;

/**
 * A result object which contains:
 * -A list of ordered similar pictures
 * -A list of server state
 * -Total number of pictures indexed on all server (available)
 * @author Rollus Loic
 */
public class ResultsSimilarities {

    /**
     * List of ordered similar pictures
     */
    private List<ResultSim> results;
    /**
     * List of server and their state
     */
    private ListServerInformationSocket servers;
    private List<RetrievalServer> listsServer;
    /**
     * Total number of pictures indexed on all server (available)
     */
    private int totalSize;

    /**
     * Constructor far a results object
     * @param results List of ordered similar pictures
     * @param servers Total number of pictures indexed on all server (available)
     */
    public ResultsSimilarities(List<ResultSim> results, ListServerInformationSocket servers)
    {
        this.results = results;
        this.servers = servers;
        this.totalSize = 0;
    }
    public ResultsSimilarities(List<ResultSim> results, List<RetrievalServer> listsServer)
    {
        this.results = results;
        this.listsServer = listsServer;
        this.totalSize = 0;
    }

    /**
     * Add the size (number of picture indexed) of a server to the total size
     * @param size Size of server s
     */
    public void add(int size)
    {
        this.setTotalSize(this.getTotalSize() + size);
    }

    /**
     * Get only the k first similar pictures
     * Each server s (from S servers) will send (max) k similar pictures.
     * ResultSim structure will contains max S*k results.
     * This function just keep max k first items
     * @param k K
     */
    public void trimSimilarities(int k)
    {
        List<ResultSim> trimResults = new ArrayList<ResultSim>(k);
        for (int i = 0; i < getResults().size() && i < k; i++) {
           trimResults.add(getResults().get(i));
        }
        setResults(trimResults);
    }

    /**
     * Get results (most similar pictures)
     * @return Most similar pictures
     */
    public List<ResultSim> getResults() {
        return results;
    }

    /**
     * Set results (most similar pictures)
     * @param results Most similar pictures
     */
    public void setResults(List<ResultSim> results) {
        this.results = results;
    }

    /**
     * Get servers and their respective states
     * @return Servers list
     */
    public ListServerInformationSocket getServersSocket() {
        return servers;
    }
    public List<RetrievalServer> getServersObject() {
        return listsServer;
    }

    /**
     * Set servers and their respective states
     * @param servers Server list
     */
    public void setServers(ListServerInformationSocket servers) {
        this.servers = servers;
    }
    public void setServers(List<RetrievalServer> servers) {
        this.listsServer = servers;
    }

    /**
     * Total number of pictures on available server
     * @return Total number of pictures
     */
    public int getTotalSize() {
        return totalSize;
    }

    /**
     * Set total pictures indexed on available server
     * @param totalSize Total number of pictures
     */
    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    
    public void print() {

    }

    public String toString() {
        String result = "";

        for(int i=0;i<getResults().size();i++) {
            result = result + i + " => " + getResults().get(i).getId() + " (" + getResults().get(i).getSimilarities() + ")\n";
        }
        return result;
    }
 }
