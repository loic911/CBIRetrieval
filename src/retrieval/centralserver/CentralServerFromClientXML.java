/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.centralserver;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;
import org.jdom.Document;
import retrieval.dist.*;
import retrieval.centralserver.exception.ImageNotValidException;
import retrieval.centralserver.exception.TooMuchSimilaritiesAskException;
import retrieval.storage.exception.InternalServerException;
import retrieval.storage.exception.TooMuchSearchRequestException;
import retrieval.utils.FileUtils;
import retrieval.utils.NetworkUtils;

/**
 * Central server side of Communication class between Central Server and client
 * with XML message and TCP/IP Socket
 * @author Rollus Loic
 */
public class CentralServerFromClientXML implements CentralServerFromClient {

    /**
     * Central Server object (to carry search request)
     */
    private MultiCentralServer centralServer;
    /**
     * Max search at the same client (=max client)
     */
    private int maxSearch;
    /**
     * Max similar picture for a request
     */
    private int maxK;
    /**
     * Group of request Thread
     */
    private ThreadGroup searchThread = new ThreadGroup("SearchThread");
    /**
     * Server Socket of central server
     */
    private ServerSocket serverSocket;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(CentralServerFromClientXML.class);

    /**
     * Constructor for a communication object (central server side)
     * @param centralServer Central Server
     * @param port Port for client request
     * @param maxSearch Max search at the same client (=max client)
     * @param maxK Max similar picture for a request
     * @throws InternalServerException Internal error during start
     */
    public CentralServerFromClientXML(MultiCentralServer centralServer, int port, int maxSearch, int maxK) throws InternalServerException {

        this.maxSearch = maxSearch;
        this.maxK = maxK;

        this.centralServer = centralServer;
        try {
            logger.info("CentralServerFromClientXML: wait on port " + port);
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error(e);
            throw new InternalServerException("Cannot make connexion:" + e.getMessage());
        }

    }
    
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            logger.error("Cannot close connection:"+ex);
        }
    }

    /**
     * This function will always wait on request from client
     */
    public void waitForRequest() {

        while (true) {
            try {
                Socket clientSocket = null;
                try {
                    logger.debug("waitForRequest: wait on socket");
                    clientSocket = serverSocket.accept();
                    logger.debug("waitForRequest: Search in progress: " + searchThread.activeCount());
                    //check if central server is not saturated
                    if (maxSearch > 0 && searchThread.activeCount() > maxSearch) {
                        logger.error("waitForRequest: Too much thread:" + searchThread.activeCount());
                        throw new TooMuchSearchRequestException("Too much search in progres");
                    }
                    //launch new request client thread
                    logger.info("waitForRequest: connexion on " + clientSocket.getInetAddress().getHostAddress());
                    NewClientSearchThread ncw = new NewClientSearchThread(centralServer, clientSocket, searchThread, maxK);
                    ncw.start();
                } catch (TooMuchSearchRequestException e) {
                    logger.error(e);
                    MessageError msg = new MessageError(e);
                    NetworkUtils.writeXmlToSocketWithoutException(clientSocket, msg.toXML());
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }
}

/**
 * Thread which talk with client
 * @author Rollus Loic
 */
class NewClientSearchThread extends Thread {

    private Socket client;
    private MultiCentralServer server;
    private int maxK;
    private static Logger logger = Logger.getLogger(NewClientSearchThread.class);

    NewClientSearchThread(MultiCentralServer server, Socket client, ThreadGroup tg, int maxK) {
        super(tg, "search");
        this.client = client;
        this.server = server;
        this.maxK = maxK;
    }

    @Override
    public void run() {

        try {
            //Read request message
            logger.info("run client: Read request message");
            Document doc = NetworkUtils.readXmlFromSocket(client);
            MessageSearch msgSearch = new MessageSearch(doc);

            //check k (=max similar picture)
            int k = msgSearch.getK();
            if (k > maxK) {
                throw new TooMuchSimilaritiesAskException("Max k=" + maxK + " similar pictures");
            }

            logger.info("run client:");

            //Read pictures
            BufferedImage img = null;
            try {
                //TODO: allow using this with publickey/privatekey/...
                img = NetworkUtils.readBufferedImageFromSocket(client);//FileUtils.readPictureFromPath(msgSearch.getPathPictureSearch(), msgSearch.getAuthorization());
            } catch (Exception e) {
                throw new ImageNotValidException(e.getMessage());
            }

            //Search similarities on every sever
            logger.info("run client: Search similarities on every sever");
            ResultsSimilarities rs = server.search(img, k,msgSearch.getServers());
            MessageResults msgResults = new MessageResults(rs.getResults(), rs.getServersSocket(), rs.getTotalSize());

            //Send results
            logger.info("run client: Send results");
            NetworkUtils.writeXmlToSocket(client, msgResults.toXML());

        } catch (TooMuchSimilaritiesAskException e) {
            logger.error(e);
            MessageError msg = new MessageError(e);
            NetworkUtils.writeXmlToSocketWithoutException(client, msg.toXML());
        } catch (NotValidMessageXMLException e) {
            logger.error("waitForRequest: NotValidMessageException" + e);
        } catch (ImageNotValidException e) {
            logger.error("waitForRequest: NotValidMessageException" + e);
            MessageError msg = new MessageError(e);
            NetworkUtils.writeXmlToSocketWithoutException(client, msg.toXML());
        } catch (IOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}