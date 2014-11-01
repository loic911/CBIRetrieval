package retrieval.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jdom.Document;
import retrieval.dist.MessageError;
import retrieval.dist.MessageIndexOrDeletePicture;
import retrieval.dist.MessageIndexResults;
import retrieval.dist.NotValidMessageXMLException;
import retrieval.exception.CBIRException;
import retrieval.server.Storage;
import retrieval.server.StorageNetworkInterface;
import retrieval.server.exception.InternalServerException;
import retrieval.utils.NetworkUtils;

/**
 * Server side Communication class between server and light indexer that
 * can index many files or directory (recursively or not)
 * with XML message and TCP/IP Socket.
 * @author Rollus Loic
 */
public class ServerFromIndexerPictureXML { //implements StorageNetworkInterface {

//    /**
//     * Server which will carry request
//     */
//    private Storage server;
//    /**
//     * Socket server
//     */
//    private ServerSocket serverSocket = null;
//    /**
//     * Logger
//     */
//    private static Logger logger = Logger.getLogger(ServerFromIndexerPictureXML.class);
//
//    /**
//     * Constructor for a picture indexer
//     * @param server Server which will carry request
//     * @param port Port on which communicator wait
//     * @param newPicturePath Path which will keep new indexed picture file
//     * @throws InternalServerException Exception during start of communicator
//     */
//    public ServerFromIndexerPictureXML(Storage server, int port)
//            throws InternalServerException {
//        this.server = server;
//        try {
//            logger.info("ServerFromIndexerPictureNetwork: port=" + port);
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
//     * Wait for an information request from an indexer picture
//     */
//    public void waitForRequest() {
//        Socket clientSocket = null;
//        while (true) {
//            try {
//                logger.info("waitForRequest: wait on socket " + serverSocket.getInetAddress() + "close=" + serverSocket.isClosed());
//                if (serverSocket.isClosed()) {
//                    break;
//                }
//                clientSocket = serverSocket.accept();
//
//                //receive request from idnexer
//                logger.info("waitForRequest: connexion on " + clientSocket.getInetAddress().getHostAddress());
//                Document doc = NetworkUtils.readXmlFromSocket(clientSocket);
//                MessageIndexOrDeletePicture msg = new MessageIndexOrDeletePicture(doc);
//
//                logger.info("waitForRequest: add to index queue");
//                MessageIndexResults msgIndex;
//                if(!msg.isDelete() && !msg.isPurge()) {
//                    //indexing
//                    String path = msg.getSingleFile();
//                    if(!msg.isSynchrone()) {
//                        server.addToIndexQueue(path,msg.getAuthorization());
//                    }
//                    else {
//                        server.indexPicture(path, msg.getAuthorization());
//                    }
//                    msgIndex = new MessageIndexResults(path);
//                }else if(msg.isPurge()) {
//                    //purge
//                    server.purgeIndex();
//                    msgIndex = new MessageIndexResults(new HashMap<String, CBIRException>());
//                } else {
//                    //delete
//                     Map<String,CBIRException> map = server.deletePictures(msg.getPaths());     
//                     msgIndex = new MessageIndexResults(map);
//                }
//
//                //response to indexer
//                logger.info("waitForRequest: sends results");
//                NetworkUtils.writeXmlToSocket(clientSocket, msgIndex.toXML());
//
//            } catch (CBIRException e) {
//                logger.error(e);
//                MessageError msg = new MessageError(e);
//                NetworkUtils.writeXmlToSocketWithoutException(clientSocket, msg.toXML());
//            } catch (NotValidMessageXMLException e) {
//                logger.error(e);
//                try{NetworkUtils.writeXmlToSocketWithoutException(clientSocket, new MessageError("0",e.toString()).toXML());}catch(Exception ex) {logger.error(e);}
//            } catch (IOException e) {
//                logger.error(e);
//                try{NetworkUtils.writeXmlToSocketWithoutException(clientSocket, new MessageError("0",e.toString()).toXML());}catch(Exception ex) {logger.error(e);}
//            } catch (Exception e) {
//                logger.error(e);
//                try{NetworkUtils.writeXmlToSocketWithoutException(clientSocket, new MessageError("0",e.toString()).toXML());}catch(Exception ex) {logger.error(e);}
//            }
//        }
//
//
//    }
}
