package retrieval.server.net;

import retrieval.server.*;
import retrieval.dist.*;
import java.util.*;
import java.io.*;
import java.net.*;
import org.jdom.*;
import org.apache.log4j.*;
import retrieval.server.exception.*;
import retrieval.exception.*;
import retrieval.utils.*;

/**
 * Server side Communication class between server and indexer that ask information)
 * with XML message and TCP/IP Socket.
 * It can list pictures indexed or see their state in waited queue
 * @author Rollus Loic
 */
public class ServerFromIndexerInfoXML {//implements StorageNetworkInterface {

//    /**
//     * Server which will carry request
//     */
//    private Storage server;
//    /**
//     * Port on which communicator wait
//     */
//    private int port;
//    /**
//     * Socket server
//     */
//    private ServerSocket serverSocket = null;
//    /**
//     * Logger
//     */
//    private static Logger logger = Logger.getLogger(ServerFromIndexerInfoXML.class);
//
//    /**
//     * Constructor for a communicator between a server and an indexer for
//     * information request
//     * @param server Server which will carry request
//     * @param port Port on which communicator wait
//     * @throws InternalServerException Exception during start of communicator
//     */
//    public ServerFromIndexerInfoXML(Storage server, int port) throws InternalServerException {
//        this.port = port;
//        this.server = server;
//        try {
//            serverSocket = new ServerSocket(port);
//        } catch (IOException e) {
//            logger.error(e);
//            throw new InternalServerException(e.toString());
//        }
//    }
//
//    /**
//     * Close socket
//     */    
//    public void close() {
//        try {
//            serverSocket.close();
//        } catch (Exception e) {
//            logger.error(e);
//        }
//    }
//
//    /**
//     * Wait for an information request from an indexer
//     */
//    public void waitForRequest() {
//
//        while (true) {
//            Socket clientSocket;
//            try {
//                logger.info("waitForRequest: wait on socket - " + port);
//                if (serverSocket.isClosed()) {
//                    break;
//                }
//                clientSocket = serverSocket.accept();
//
//                logger.info("waitForRequest: connection with "  + clientSocket.getInetAddress().getHostAddress());
//                //read message information
//                Document msgInfoXML = NetworkUtils.readXmlFromSocket(clientSocket);
//                MessageInfo msgInfo = new MessageInfo(msgInfoXML);
//
//                Map<String, CBIRException> filesPaths = msgInfo.getFiles();
//
//                if (filesPaths != null) {
//                    //if not null, check all files in filesPaths
//                    filesPaths = server.getInfo(filesPaths);
//                    long numberOfItem = server.getNumberOfItem();
//                    MessageInfo msg = new MessageInfo(filesPaths, numberOfItem);
//                    logger.info("waitForRequest: sends results");
//                    NetworkUtils.writeXmlToSocket(clientSocket, msg.toXML());
//                } else {
//                    //otherwise, lists all files in server
//                    Map<String, Integer> pictureList = server.getAllPicturesMap();
//                    MessageInfo msg = new MessageInfo();
//                    msg.setPictures(pictureList);
//                    logger.info("waitForRequest: sends results");
//                    NetworkUtils.writeXmlToSocket(clientSocket, msg.toXML());
//                }
//
//            } catch (IOException e) {
//                logger.error("waitForRequest:IOException" + e);
//            } catch (NotValidMessageXMLException e) {
//                logger.error("waitForRequest:NotValidMessageException" + e);
//            } catch (Exception e) {
//                logger.error(e);
//            }
//
//        }
//    }
}
