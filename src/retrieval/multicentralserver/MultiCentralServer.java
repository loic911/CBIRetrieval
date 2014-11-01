/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.multicentralserver;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.config.ConfigCentralServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.exception.CBIRException;
import retrieval.multiserver.MultiServer;
import retrieval.testvector.TestVectorListCentralServer;
import retrieval.testvector.generator.TestVectorReading;

/**
 * This class define a central server for multiserver.
 * We can search similar pictures on multiple multiserver
 * @author lrollus
 */
public class MultiCentralServer implements CentralServerInterface{

    /**
     * Config object
     */
    private ConfigCentralServer configCentralServer;
    
    /**
     * Tests vectors to build request pictures data
     */
    private TestVectorListCentralServer testVectors;
    
    /**
     * All multiserver info (port, url,...)
     */
    private ListServerInformationSocket listsServerSocket;
    
    private ListServerInformationObject listsServerObject;

    /**
     * Logger
     */
    static Logger logger = Logger.getLogger(MultiCentralServer.class);


    /**
     * Build a multicentralserver and read servers infos from serversFile
     * @param configCentralServer Config object
     * @param serversFile Server.xml files
     * @throws Exception Error during creation
     */
    public MultiCentralServer(ConfigCentralServer configCentralServer, String serversFile) throws CBIRException {
        this.configCentralServer = configCentralServer;
        readTestsVectors(configCentralServer);
        readServerList(configCentralServer,serversFile);
    }

    /**
     * Build a multicentralserver and read servers infos from serversFile
     * @param configCentralServer Config object
     * @param listsServerSocket MultiServer infos
     * @throws Exception Error during creation
     */
    public MultiCentralServer(ConfigCentralServer configCentralServer,ListServerInformationSocket listsServerSocket) throws CBIRException {
        this.configCentralServer = configCentralServer;
        this.listsServerSocket = listsServerSocket;
        readTestsVectors(configCentralServer);
    }

    public MultiCentralServer(ConfigCentralServer configCentralServer,List<MultiServer> servers) throws CBIRException {
        this.configCentralServer = configCentralServer;    
        this.listsServerObject = new ListServerInformationObject(servers,this.configCentralServer.getTimeout());
        readTestsVectors(configCentralServer);
    } 
    
    public MultiCentralServer(ConfigCentralServer configCentralServer,MultiServer server) throws CBIRException {
        List<MultiServer> servers = new ArrayList<MultiServer>();
        servers.add(server);
        this.configCentralServer = configCentralServer;    
        this.listsServerObject = new ListServerInformationObject(servers,this.configCentralServer.getTimeout());
        readTestsVectors(configCentralServer);
    }      
    
    /**
     * Search max k similar pictures as img
     * @param img Image request
     * @param k Max result
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(BufferedImage img, int k) throws CBIRException {
        logger.debug("search (BufferedImage img, int k)");
        return this.search(img, configCentralServer.getNumberOfPatch(), k,new String[0]);
    }
    
    /**
     * Search max k similar pictures as img, search only on servers in servers array
     * @param img Image request
     * @param k Max result
     * @param servers Servers limitation (empty = all servers)
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(BufferedImage img, int k,String[] servers) throws CBIRException {
        logger.debug("search(BufferedImage img, int k,String[] servers)");
        return this.search(img, configCentralServer.getNumberOfPatch(), k,servers);
    }
    
    /**
     * Search max k similar pictures as img, search only on servers in servers array
     * @param img Image request
     * @param k Max result
     * @param servers Servers limitation (empty = all servers)
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(BufferedImage img, int k,List<String> servers) throws CBIRException {
        logger.debug("search(BufferedImage img, int k,String[] servers)");
        return this.search(img, configCentralServer.getNumberOfPatch(), k,(String[])servers.toArray(new String[servers.size()]));
    }    
    
    /**
     * Search max k similar pictures as img, search only on servers in servers array
     * @param img Image request
     * @param N Number of patchs to build
     * @param k Max result
     * @param servers Servers limitation (empty = all servers)
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(BufferedImage img, int N, int k,String[] servers) throws CBIRException {
        logger.debug("search(BufferedImage img, int N, int k,String[] servers)");
        try {
            //extract Visual word for img
            List<ConcurrentHashMap<String, Long>> visualWords = testVectors.generateVisualWordFromPicture(
                    img,
                    null, 
                    configCentralServer.getNumberOfPatch(), 
                    configCentralServer.getResizeMethod(), 
                    configCentralServer.getSizeOfPatchResizeWidth(), 
                    configCentralServer.getSizeOfPatchResizeHeight());

            return this.search(visualWords, N, k,servers);
        } catch (Exception e) {
            throw new CBIRException("Internal error: InterruptedException");
        }
    }
    
    /**
     * Search similar pictures thanks to visualWords
     * @param visualWords Visual word for request
     * @param N Number of patchs to build
     * @param k Max result
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    public ResultsSimilarities search(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k) throws CBIRException{
        logger.debug("search(Vector<ConcurrentHashMap<String, Long>> visualWords, int N, int k) ");
        return search(visualWords,N,k, new String[0]);
    }
    
    /**
     * Search similar pictures thanks to visualWords, search only on servers in servers array
     * @param visualWords Visual word for request
     * @param N Number of patchs to build
     * @param k Max result
     * @param servers Servers filters
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */    
    public ResultsSimilarities search(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k, String[] servers)
            throws CBIRException {
        logger.debug("search(Vector<ConcurrentHashMap<String, Long>> visualWords, int N, int k, String[] servers)");
        //TODO: Refactor me!
        try {
            /**
             * isDistribued = true => connect with xml/socket
             * isDistribued = false => direct access to object
             */
            ResultsSimilarities result = null;
            if(listsServerSocket!=null) {
                MultiCentralServerToServersXML serverNetwork = new MultiCentralServerToServersXML((ListServerInformationSocket) this.getListsServerSocket().getServers());
                logger.info("search: " + k + " similar pictures on "+ serverNetwork.getNumberOfServer() + " servers");
                result = serverNetwork.searchMultiThread(visualWords, N, k,servers);                
            } else {
                 MultiCentralServerToServersObject serverNetwork = new MultiCentralServerToServersObject((ListServerInformationObject) this.getListsServerSocket().getServers());
                logger.info("search: " + k + " similar pictures on "+ serverNetwork.getNumberOfServer() + " servers");
                result = serverNetwork.searchMultiThread(visualWords, N, k,servers);                  
            }

            //select only k first picture
            result.trimSimilarities(k);
            return result;
        } catch (InterruptedException e) {
            throw new CBIRException("Internal error: InterruptedException");
        }
    }

    /**
     * Read tests vectors on a path
     * @param configCentralServer Configuration object of central sever
     * @throws Exception Files not found/Not valids
     */
    private void readTestsVectors(ConfigCentralServer configCentralServer) throws CBIRException {
        //Lire les vecteur de test triés
        testVectors = TestVectorReading.readClient(configCentralServer.getVectorPath());
        logger.info(testVectors.size() + " tests vectors read...");
    }

    /**
     * Read server list on parh
     * @param configCentralServer Configuration object of central sever
     * @param serversFile Server file path
     * @throws Exception Files not found/Not valids
     */
    private void readServerList(ConfigCentralServer configCentralServer, String serversFile)  throws CBIRException{
        setListsServerSocket(new ListServerInformationSocket(serversFile, configCentralServer.getTimeout()));
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
