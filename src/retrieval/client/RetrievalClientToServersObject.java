/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.dist.MessageNBT;
import retrieval.dist.MessageResults;
import retrieval.dist.MessageSimilarities;
import retrieval.dist.ResultsSimilarities;
import retrieval.server.RetrievalServer;
import retrieval.storage.Storage;
import retrieval.storage.exception.WrongNumberOfTestsVectorsException;
import retrieval.storage.index.ResultSim;

/**
 * Central server side of Communication class between Central Server and servers
 * with XML message and TCP/IP Socket
 * @author Rollus Loic
 */
public class RetrievalClientToServersObject  {

    /**
     * Server list
     */
    private List<RetrievalServer> listsServer;
    private Map<String,Storage> storages;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(RetrievalClientToServersObject.class);

    /**
     * Launch communication class on server
     * @param listsServer List of server
     */
    public RetrievalClientToServersObject(List<RetrievalServer> listsServer) {
        this.listsServer = listsServer;
    }

    
    
    /**
     * Thanks to all Result messages in msgSimilar[], this function will compute the best k similar pictures from all servers
     * @param msgSimilar Result messages
     * @param k Max similar pictures
     * @return Max k similar pictures
     */
    protected ResultsSimilarities sortAndTrimBestResults(MessageResults msgSimilar[], int k) {
        return new ResultsSimilarities(sortSimilarities(msgSimilar, k),listsServer);
    }
    
    public ResultsSimilarities searchMultiThread(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k, String[] servers) throws InterruptedException {

        this.storages = new HashMap<String,Storage>();
        for(RetrievalServer server : listsServer) {
            for(Storage storage : server.getServerList()) {
                if(servers==null || servers.length==0) {
                    this.storages.put(server.getIndexPath()+"#"+storage.getStorageName(),storage);
                } else {
                    for(String serverName : servers) {
                        if(storage.getStorageName().equals(serverName)) {
                            this.storages.put(server.getIndexPath()+"#"+storage.getStorageName(),storage);
                        }
                    }                    
                } 
            }
            
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("searchMultiThread: N=" + N + " T=" + visualWords.size() + " k=" + k);
            logger.debug("searchMultiThread: build first message...");
        }
        //ask for nbt on each server
        logger.debug("getNBTFromServers");
        MessageSimilarities msgNBT = getNBTFromServers(visualWords, N, k);
        //ask for similar pictures on each server thanks to nbt
        logger.debug("getMostSimilarPictureFromServers");
        MessageResults msgSimilar[] = getMostSimilarPictureFromServers(msgNBT);
        //filter most similar picture (sort + filter k first)
        logger.debug("sortAndTrimBestResults");
        ResultsSimilarities results = sortAndTrimBestResults(msgSimilar, k);
        //fill the size of each server index (number of picture
        logger.debug("fillSizeOfServer");
//        fillSizeOfServer(msgSimilar, results);
        logger.debug("results");
        return results;
    }    
    
    /**
     * TODO: move tu super class!!!!
     * Sort similarities for all server reponse
     * @param msg3s All servers reponses
     * @param k Max similar picture for each server (optimisation parameter)
     * @return All servers reponses sorted
     */
    protected List<ResultSim> sortSimilarities(MessageResults[] msg3s, int k) {
        //results will have the max size of number of similar picture (k) * number of server
        List<ResultSim> results = new ArrayList<ResultSim>(k * msg3s.length);
        for (int i = 0; i < msg3s.length; i++) {
            if(msg3s[i]!=null) {
               results.addAll(msg3s[i].getResults()); 
            }
        }
        Collections.sort(results);
        return results;
    } 
    
//    /**
//     * Fill the size of each server and compute total size
//     * @param listsServer Server list
//     * @param msgSimilar Message with similarities result from server
//     * @param results Result object with similar picture and server info
//     */
//    protected void fillSizeOfServer(MessageResults[] msgSimilar, ResultsSimilarities results) {
//
//        int i = 0;
//        TreeSet<String> keys = new TreeSet<String>(listsServer.getMap().keySet());
//        for (String key : keys) {
//           ServerInformationObject value = listsServer.getMap().get(key);
//            if (msgSimilar[i] != null) {
//                int sizeOfIndex = msgSimilar[i].getNumberOfPicturesInIndex();
//                results.add(sizeOfIndex);
//                value.setSizeOfIndex(sizeOfIndex);
//            }
//           i++;
//        }
//    }

    /**
     * Get all NBT info on each server
     * @param visualWords Visualword to check NBT
     * @param N Number of patch
     * @param k Number of max result
     * @return Message with sum of NBT from each server
     * @throws InterruptedException
     */
    protected MessageSimilarities getNBTFromServers(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k) throws InterruptedException {
        logger.debug("getNBTFromServers");
        MessageNBT msgVW = new MessageNBT(visualWords);
        MessageSimilarities msgSimilar = new MessageSimilarities(visualWords, N, k);
       // Map<String,Storage> servers = (Map<String,Storage>)listsServer.getServers();
        AskNBTObjectThread[] threadsNBT = new AskNBTObjectThread[storages.size()];

        int i = 0;
        TreeSet<String> keys = new TreeSet<String>(storages.keySet());
        for (String key : keys) {
           Storage value = storages.get(key);
           // do something
            threadsNBT[i] = new AskNBTObjectThread(msgVW, msgSimilar, value);
            threadsNBT[i].start();
           i++;
        }

        //wait all server similar pictures
        for (int j = 0; j < threadsNBT.length; j++) {
            if (threadsNBT[j] != null) {
                threadsNBT[j].join();
            }
        }
        return msgSimilar;
    }

    /**
     * Get Most Similar Pictures
     * @param msgWithNBT Message with NBT
     * @return Result for each server
     * @throws InterruptedException
     */
    protected MessageResults[] getMostSimilarPictureFromServers(MessageSimilarities msgWithNBT) throws InterruptedException {
        /*
         * Ask Similarity between visual word from Iq and index of each server
         */
        ClientAskSimilaritiesObjectThread[] threadsSimilar = new ClientAskSimilaritiesObjectThread[storages.size()];
        MessageResults[] msg3s = new MessageResults[storages.size()];

        //Map<String,ServerInformationObject> servers = listsServer.getMap();
        int i = 0;
        TreeSet<String> keys = new TreeSet<String>(storages.keySet());
        for (String key : keys) {
           Storage value = storages.get(key);
            //if (value.getState() == ServerInformationSocket.NOERROR) {
                threadsSimilar[i] = new ClientAskSimilaritiesObjectThread(msgWithNBT, msg3s, i,value);
                threadsSimilar[i].start();
            //}
           i++;
        }

        //Wait each server response
        for (int j = 0; j < threadsSimilar.length; j++) {
            if (threadsSimilar[j] != null) {
                threadsSimilar[j].join();
            }
        }
        return msg3s;
    }
    
    /**
     * Number of server that will be requested
     * @return Server number
     */
    public int getNumberOfServer() {
        return listsServer.size();
    }    
}


/**
 * This Thread ask NBT on a specific server
 * @author Rollus Loic
 */
class AskNBTObjectThread extends Thread {

    private MessageNBT msgSource;
    private MessageSimilarities msgResult;
    private Storage storage;
    private static Logger logger = Logger.getLogger(AskNBTObjectThread.class);
    /**
     * Populate NBT info thanks to server response
     * @param msgSource Message with NBT values
     * @param msgResult Message that will save all NBT from all servers
     * @param server Server where to request NBT
     */
    AskNBTObjectThread(MessageNBT msgSource, MessageSimilarities msgResult, Storage storage) {
        this.msgSource = msgSource;
        this.msgResult = msgResult;
        this.storage = storage;
    }

    @Override
    public void run() {
        try {
            MessageNBT toSend = (MessageNBT) msgSource.copyWithoutValue();
            MessageNBT response = new MessageNBT(storage.getNBT(toSend.getVisualWordsByTestVector()));
            msgResult.addNBT(response.toXML());
        } catch (WrongNumberOfTestsVectorsException ex) {
            logger.error("run nbt: undef1:" + ex.toString() + " server="+storage.getStorageName());
            //storage.changeState(ServerInformationSocket.UNDEF, ex.getMessage());
        } catch (Exception ex) {
            logger.error("run nbt: undef2:" + ex.toString() + " server="+storage.getStorageName());
            //server.changeState(ServerInformationSocket.UNDEF, ex.getMessage());
        }
    }
}

/**
 * This Thread ask similar pictures on a server
 * @author Rollus Loic
 */
class ClientAskSimilaritiesObjectThread extends Thread {

    private MessageSimilarities msgToSend;
    private MessageResults[] msgToReceive;
    private Storage server;
    private int indexOfThread;
    private static Logger logger = Logger.getLogger(ClientAskSimilaritiesObjectThread.class);

    /**
     * Populate similar pictures info thanks to server response
     * @param msgToSend Message that save all NBT from servers
     * @param msgToReceive Message with all similar pictures from these server
     * @param indexOfThread Index of this thread (the index of this server)
     * @param server Server where to request similar pictures
     */
    ClientAskSimilaritiesObjectThread(MessageSimilarities msgToSend, MessageResults[] msgToReceive, int indexOfThread, Storage server) {
        this.msgToSend = msgToSend;
        this.msgToReceive = msgToReceive;
        this.server = server;
        this.indexOfThread = indexOfThread;
    }

    @Override
    public void run() {
        try {
            int sizeOfIndex = (int) server.getNumberOfItem();
            List<ResultSim> results = server.getPicturesSimilarities(msgToSend.getVisualWord(), msgToSend.getNiq(), msgToSend.getK());
            msgToReceive[indexOfThread] = new MessageResults(results, sizeOfIndex);
        } catch (Exception e) {
            logger.error("run sim: undef " + e);
           // server.changeState(ServerInformationSocket.UNDEF, e.getMessage());
        }
    }
}