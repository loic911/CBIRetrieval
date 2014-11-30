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
package retrieval.client;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.config.ConfigClient;
import retrieval.dist.ResultsSimilarities;
import retrieval.exception.CBIRException;
import retrieval.server.RetrievalServer;
import retrieval.testvector.TestVectorListCentralServer;
import retrieval.testvector.generator.TestVectorReading;

/**
 * This class define a client for servers.
 * A client can search similar pictures on multiple server.
 * Server may be distant (host/port) or local (java object)
 * @author lrollus
 */
public class RetrievalClient implements RetrievalClientInterface{

    /**
     * Configuration object
     */
    private final ConfigClient configClient;
    
    /**
     * Tests vectors to build request pictures data
     */
    private TestVectorListCentralServer testVectors;
    
    /**
     * All server info (port, url,...)
     */
    private ListServerInformationSocket listsServerSocket;
    
    /**
     * All server object
     */
    private List<RetrievalServer> listServerObjects;

    /**
     * Logger
     */
    static Logger logger = Logger.getLogger(RetrievalClient.class);


    /**
     * Build a client and read servers host/port from serversFile
     * @param configClient Configuration object
     * @param serversFile Server.xml files
     * @throws CBIRException Error during creation
     */
    public RetrievalClient(ConfigClient configClient, String serversFile) throws CBIRException {
        this.configClient = configClient;
        readTestsVectors(configClient);
        readServerList(configClient,serversFile);
    }

    /**
     * Build a client and read servers from listsServerSocket param
     * @param configClient Configuration object
     * @param listsServerSocket Servers
     * @throws CBIRException Error during creation
     */
    public RetrievalClient(ConfigClient configClient,ListServerInformationSocket listsServerSocket) throws CBIRException {
        this.configClient = configClient;
        this.listsServerSocket = listsServerSocket;
        readTestsVectors(configClient);
    }

    /**
     * Build a client and read servers from listsServerSocket param
     * @param configClient Configuration object
     * @param servers Servers
     * @throws CBIRException Error during creation
     */    
    public RetrievalClient(ConfigClient configClient,List<RetrievalServer> servers) throws CBIRException {
        this.configClient = configClient;    
        this.listServerObjects = servers;
        readTestsVectors(configClient);
    } 
    
    /**
     * Build a client and read a single server from server param
     * @param configClient Configuration object
     * @param server Sever
     * @throws CBIRException Error during creation
     */     
    public RetrievalClient(ConfigClient configClient,RetrievalServer server) throws CBIRException {
        List<RetrievalServer> servers = new ArrayList<RetrievalServer>();
        servers.add(server);
        this.configClient = configClient;    
        this.listServerObjects = servers;
        readTestsVectors(configClient);
    }      
    
    /**
     * Search max k similar pictures as img
     * @param img Image request
     * @param k Max result
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(BufferedImage img, int k) throws CBIRException {
        return this.search(img, configClient.getNumberOfPatch(), k,new String[0]);
    }
    
    /**
     * Search max k similar pictures as img, search only on servers in servers array
     * @param img Image request
     * @param k Max result
     * @param storages Storages limitation (empty = all storages)
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(BufferedImage img, int k,String[] storages) throws CBIRException {
        return this.search(img, configClient.getNumberOfPatch(), k,storages);
    }
    
    /**
     * Search max k similar pictures as img, search only on servers in servers array
     * @param img Image request
     * @param k Max result
     * @param storages Storages limitation (empty = all storages)
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(BufferedImage img, int k,List<String> storages) throws CBIRException {
        return this.search(img, configClient.getNumberOfPatch(), k,(String[])storages.toArray(new String[storages.size()]));
    }    
    
    /**
     * Search max k similar pictures as img, search only on servers in servers array
     * @param img Image request
     * @param N Number of patches to build
     * @param k Max result
     * @param storages Storages limitation (empty = all storages)
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(BufferedImage img, int N, int k,String[] storages) throws CBIRException {
        try {
            //extract Visual word for img
            List<ConcurrentHashMap<String, Long>> visualWords = testVectors.generateVisualWordFromPicture(img,
                    null, 
                    configClient.getNumberOfPatch(), 
                    configClient.getResizeMethod(), 
                    configClient.getSizeOfPatchResizeWidth(), 
                    configClient.getSizeOfPatchResizeHeight());

            return this.search(visualWords, N, k,storages);
        } catch (InterruptedException e) {
            logger.error(e);
            throw new CBIRException(e.getMessage());
        }
    }
    
    /**
     * Search similar pictures thanks to visualWords
     * @param visualWords Visual word for request
     * @param N Number of patches to build
     * @param k Max result
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k) throws CBIRException{
        return search(visualWords,N,k, new String[0]);
    }
    
    /**
     * Search similar pictures thanks to visualWords, search only on servers in servers array
     * @param visualWords Visual word for request
     * @param N Number of patches to build
     * @param k Max result
     * @param storages Storages filters
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */    
    public ResultsSimilarities search(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k, String[] storages)
            throws CBIRException {
        try {
            /**
             * isDistribued = true => connect with xml/socket
             * isDistribued = false => direct access to object
             */
            ResultsSimilarities result = null;
            if(listsServerSocket!=null) {
                ListServerInformationSocket serversSocket = (ListServerInformationSocket) this.getListsServerSocket().getServers();
                logger.info("Search on "+serversSocket);
                RetrievalClientToServersXML serverNetwork = new RetrievalClientToServersXML(serversSocket);
                logger.info("search: " + k + " similar pictures on "+ serverNetwork.getNumberOfServer() + " servers");
                result = serverNetwork.searchMultiThread(visualWords, N, k,storages);                
            } else {
                 RetrievalClientToServersObject serverNetwork = new RetrievalClientToServersObject(listServerObjects);
                logger.info("search: " + k + " similar pictures on "+ serverNetwork.getNumberOfServer() + " servers");
                result = serverNetwork.searchMultiThread(visualWords, N, k,storages);                  
            }

            //select only k first picture
            result.trimSimilarities(k);
            return result;
        } catch (InterruptedException e) {
            logger.error(e);
            throw new CBIRException(e.getMessage());
        }
    }

    /**
     * Read tests vectors on a path
     * @param configClient Configuration object of client
     * @throws Exception Files not found/Not valid
     */
    private void readTestsVectors(ConfigClient configClient) throws CBIRException {
        testVectors = TestVectorReading.readClient(configClient.getVectorPath(),configClient);
        logger.info(testVectors.size() + " tests vectors read...");
    }

    /**
     * Read server list from path serversFile
     * @param configClient Configuration object of client
     * @param serversFile Server file path
     * @throws Exception Files not found/Not valid
     */
    private void readServerList(ConfigClient configClient, String serversFile)  throws CBIRException{
        setListsServerSocket(new ListServerInformationSocket(serversFile, configClient.getTimeout()));
    }

    /**
     * @return the listsServerSocket
     */
    public ListServerInformationSocket getListsServerSocket() {
        return listsServerSocket;
    }

    /**
     * @param listsServerSocket the listsServerSocket to set
     */
    public void setListsServerSocket(ListServerInformationSocket listsServerSocket) {
        this.listsServerSocket = listsServerSocket;
    }

}
