package retrieval.client.net;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.jdom.Document;
import retrieval.client.ClientToCentralServer;
import retrieval.dist.*;
import retrieval.exception.*;
import retrieval.utils.NetworkUtils;

/**
 * Communication class between a client and a central server with
 * XML message and tcp/ip socket
 * @author Rollus Loic
 */
public class ClientToCentralServerXML implements ClientToCentralServer {

    /**
     * The socket of client
     */
    private Socket client;
    /**
     * Central Server url
     */
    private String url;
    /**
     * Central Server port
     */
    private int port;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(ClientToCentralServerXML.class);

    /**
     * Constructor
     * @param url Central Server url
     * @param port Central Server port
     */
    public ClientToCentralServerXML(String url, int port) {
        this.url = url;
        this.port = port;
    }

//    /**
//     * Search max k similar pictures of picture img in central server
//     * This is made with XML message and socket.
//     * @param path Request image
//     * @param k Max similar pictures
//     * @return Max k similar pictures of img
//     * @throws CBIRException Error during communication with central server
//     */
//    public ResultsSimilarities searchInCentralServer(String path, int k) throws CBIRException {
//        return this.searchInCentralServer(path, k, new PictureAuthorization(),null);
//    }
//
//    /**
//     * Search picture in central server
//     * @param path Picture path
//     * @param k Max number of similar pictures
//     * @param authorization Authorization to access search picture
//     * @param servers Servers that central server must request (if empty, all server)
//     * @return Most similar pictures and server states
//     * @throws CBIRException Exception during search
//     */     
//    public ResultsSimilarities searchInCentralServer(String path, int k, PictureAuthorization authorization,List<String> servers) throws CBIRException {
//        logger.info("searchInCentralServer: connexion " + url + ":" + port);
//
//        try {
//            client = new Socket(url, port);
//
//            logger.info("searchInCentralServer: connexion ok");
//            //send request
//            MessageSearch msg = new MessageSearch(k, path,authorization,servers);
//            NetworkUtils.writeXmlToSocket(client, msg.toXML());
//            logger.info("searchInCentralServer: request send");
//
//            //receive result
//            Document msgResultsXML = NetworkUtils.readXmlFromSocket(client);
//
//            //if error message, throw it
//            if (MessageError.isErrorMessage(msgResultsXML)) {
//                throw MessageError.getException(msgResultsXML);
//            }
//
//            MessageResults msgResults = new MessageResults(msgResultsXML,0);
//            logger.info("searchInCentralServer: response receive");
//
//            ResultsSimilarities rs = new ResultsSimilarities(msgResults.getResults(),msgResults.getServers());
//
//            logger.info("searchInCentralServer: results " + rs.getResults().size());
//            client.close();
//            return rs;
//        } catch (CBIRException e) {
//            logger.error(e.getMessage());
//            throw e;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            throw new CBIRException( "3000","Cannot connect to central server: " + e.getMessage());
//        } catch (NotValidMessageXMLException e) {
//            logger.error(e.getMessage());
//            throw new CBIRException("3000", "Bad message: " + e.getMessage());
//        }
//
//    }
    
   /**
     * Search picture in central server
     * @param img Picture
     * @param k Max number of similar pictures
     * @param authorization Authorization to access search picture
     * @param servers Servers that central server must request (if empty, all server)
     * @return Most similar pictures and server states
     * @throws CBIRException Exception during search
     */     
    public ResultsSimilarities searchInCentralServer(BufferedImage img, int k, List<String> servers) throws CBIRException {
        logger.info("searchInCentralServer: connexion " + url + ":" + port);
        try {
            client = new Socket(url, port);

            logger.info("searchInCentralServer: connexion ok");
            //send request
            MessageSearch msg = new MessageSearch(k,servers);
            NetworkUtils.writeXmlToSocket(client, msg.toXML());
            NetworkUtils.writeXmlToSocket(client, img);
            logger.info("searchInCentralServer: request send");

            //receive result
            Document msgResultsXML = NetworkUtils.readXmlFromSocket(client);

            //if error message, throw it
            if (MessageError.isErrorMessage(msgResultsXML)) {
                throw MessageError.getException(msgResultsXML);
            }

            MessageResults msgResults = new MessageResults(msgResultsXML,0);
            logger.info("searchInCentralServer: response receive");

            ResultsSimilarities rs = new ResultsSimilarities(msgResults.getResults(),msgResults.getServers());

            logger.info("searchInCentralServer: results " + rs.getResults().size());
            client.close();
            return rs;
        } catch (CBIRException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new CBIRException( "3000","Cannot connect to central server: " + e.getMessage());
        } catch (NotValidMessageXMLException e) {
            logger.error(e.getMessage());
            throw new CBIRException("3000", "Bad message: " + e.getMessage());
        }

    }    

    /**
     * Search picture that has generated visual words in central server
     * @param visualWords Visual words generated from pictures
     * @param N Number of patch
     * @param k Max number of similar pictures
     * @return Most similar pictures and server states
     * @throws CBIRException Exception during search
     */      
    public ResultsSimilarities searchInCentralServer(List<ConcurrentHashMap<String, Long>> visualWords,  int N, int k) throws CBIRException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
