package retrieval.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.jdom.Document;
import retrieval.dist.*;
import retrieval.server.Storage;
import retrieval.server.StorageNetworkInterface;
import retrieval.server.exception.InternalServerException;
import retrieval.server.exception.TooMuchSearchRequestException;
import retrieval.server.exception.TooMuchSimilarPicturesAskException;
import retrieval.server.exception.WrongNumberOfTestsVectorsException;
import retrieval.server.index.ResultSim;
import retrieval.utils.NetworkUtils;

/**
 * Server side Communication class between Central Server and server
 * with XML message and TCP/IP Socket.
 * This include NBT and similarities exchange
 * @author Rollus Loic
 */
public class ServerFromCentralServerXML  { //implements StorageNetworkInterface {
//
//    /**
//     * Server which will carry request
//     */
//    private Storage server;
//    /**
//     * Thread group for search
//     */
//    private ThreadGroup searchThread = new ThreadGroup("SearchThread");
//    /**
//     * Max search Thread allowed on this server at a time
//     */
//    private int maxSearch;
//    /**
//     * Max number of similar pictures server will answer
//     */
//    private int maxK;
//    /**
//     * Socket for this server
//     */
//    private ServerSocket serverSocket = null;
//    /**
//     * Logger
//     */
//    private static Logger logger = Logger.getLogger(ServerFromCentralServerXML.class);
//
//    /**
//     * Constructor for a central server vs server communication
//     * @param server Server which will carry request
//     * @param port Port for request
//     * @param maxSearch Max search Thread allowed on this server at a time
//     * @param maxK Max number of similar pictures server will answer
//     * @throws InternalServerException Exception during the start of the request thread
//     */
//    public ServerFromCentralServerXML(Storage server,int port, int maxSearch,int maxK) throws InternalServerException {
//        
//        logger.info("ServerFromCentralServerNetwork: start on " + port);
//        
//        this.maxSearch = maxSearch;
//        this.maxK = maxK;     
//        this.server = server;
//        try {
//            serverSocket = new ServerSocket(port);
//        } catch (IOException e) {
//            logger.error(e);
//            throw new InternalServerException(e.getMessage());
//        }
//
//    }
//
//    /**
//     * Close socket
//     */
//    public void close()
//    {
//        try {
//            serverSocket.close();
//        } catch(Exception e){
//            logger.error(e);
//        }
//    }
//
//    /**
//     * Wait for a search request from central server
//     */
//    public void waitForRequest() {
//
//        while (true) {
//            Socket clientSocket = null;
//            try {
//                
//                logger.info("waitForRequest: wait on socket");
//                if(serverSocket.isClosed()) {
//                    break;
//                }
//                clientSocket = serverSocket.accept();
//
//                logger.info("waitForRequest: number of search in progress: " + searchThread.activeCount());
//
//                if (maxSearch > 0 && searchThread.activeCount() > maxSearch) {
//                    //server is full
//                    logger.info("waitForRequest: Too much thread:" + searchThread.activeCount());
//                    throw new TooMuchSearchRequestException("Too much search in progres");
//                }
//
//                logger.info("waitForRequest: connexion on " + clientSocket.getInetAddress().getHostAddress());
//                NewClientSearchThread ncw = new NewClientSearchThread(server,clientSocket,searchThread,maxK);
//                ncw.start();
//
//            } catch (TooMuchSearchRequestException e) {
//                logger.error(e);
//                MessageError msg = new MessageError(e);
//                NetworkUtils.writeXmlToSocketWithoutException(clientSocket, msg.toXML());
//            } catch (Exception e) {
//                logger.error(e);
//            }
//        }
//    }
//}
//
///**
// * A thread which will carry A single request from central server
// * @author Rollus Loic
// */
//class NewClientSearchThread extends Thread {
//
//    /**
//     * Socket from client (central server)
//     */
//    private Socket client;
//    /**
//     * Server which will carry the request
//     */
//    private Storage server;
//    /**
//     * Max number of similar picture for a request
//     */
//    private int maxK;
//    /**
//     * Logger
//     */
//    private static Logger logger = Logger.getLogger(NewClientSearchThread.class);
//
//    /**
//     * Constructor for a search thread
//     * @param server Server which will carry the request
//     * @param client Socket from client (central server)
//     * @param tg Thread Group which will be group of this thread
//     * @param maxK Max number of similar picture for a request
//     */
//    NewClientSearchThread(Storage server, Socket client, ThreadGroup tg, int maxK) {
//        super(tg, "search");
//        this.client = client;
//        this.server = server;
//        this.maxK = maxK;
//    }
//
//    @Override public void run() {
//
//        try {
//
//            logger.info("run: read messageNBT");
//            //receive message that ask NBT from server
//            Document docAskNBT = NetworkUtils.readXmlFromSocket(client);
//            MessageNBT msgAskNBT = new MessageNBT(docAskNBT);
//            List<ConcurrentHashMap<String, Long>> visualWords = msgAskNBT.getVisualWordsByTestVector();
//
//            logger.info("run: get NBT");
//            //search all NBT and fill this message
//            visualWords = server.getNBT(visualWords);
//            msgAskNBT.setVisualWordsByTestVector(visualWords);
//
//            logger.info("run: write response NBT");
//            //response to central server
//            NetworkUtils.writeXmlToSocket(client, msgAskNBT.toXML());
//
//            logger.info("run: read similarities message");
//            //read second message that ask similarities from central server
//            MessageSimilarities msg2 = new MessageSimilarities(NetworkUtils.readXmlFromSocket(client));
//            List<ConcurrentHashMap<String, RequestPictureVisualWord>> vw = msg2.getVisualWord();
//
//            int Niq = msg2.getNiq();
//            int k = msg2.getK();
//            if (this.maxK!=0 && k > this.maxK) {
//                throw new TooMuchSimilarPicturesAskException("Ask max " + this.maxK + " similar pictures");
//            }
//
//            //send results messages
//            int sizeOfIndex = (int) server.getNumberOfItem();
//            logger.info("run: write response results");
//            
//            List<ResultSim> results = server.getPicturesSimilarities(vw, Niq, k);
//            MessageResults msg3 = new MessageResults(results, sizeOfIndex);
//            NetworkUtils.writeXmlToSocket(client, msg3.toXML());
//
//            client.close();
//
//        } catch (TooMuchSimilarPicturesAskException e) {
//            logger.error(e);
//            MessageError msg = new MessageError(e);
//            NetworkUtils.writeXmlToSocketWithoutException(client, msg.toXML());
//        } catch (WrongNumberOfTestsVectorsException e) {
//            logger.error(e);
//            MessageError msg = new MessageError(e);
//            NetworkUtils.writeXmlToSocketWithoutException(client, msg.toXML());
//        } catch (NotValidMessageXMLException e) {
//            logger.error("waitForRequest:NotValidMessageException" + e);
//        } catch (Exception e) {
//            logger.error("waitForRequest:" + e);
//        }
//    }
}
