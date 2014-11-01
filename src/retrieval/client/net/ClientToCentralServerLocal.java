package retrieval.client.net;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.multicentralserver.CentralServerInterface;
import retrieval.client.ClientToCentralServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.exception.CBIRException;
import retrieval.utils.FileUtils;

/**
 * Communication class between client and central server for a Heavy client. So
 * the central server is an object of client.
 *
 * @author Rollus Loic
 */
public class ClientToCentralServerLocal implements ClientToCentralServer {

    private static Logger logger = Logger.getLogger(ClientToCentralServerLocal.class);
    /**
     * Centrak server of the client
     */
    private CentralServerInterface centralServer;

    /**
     * Constructor
     *
     * @param centralServer Centrak server of the client
     */
    public ClientToCentralServerLocal(CentralServerInterface centralServer) {
        this.centralServer = centralServer;
    }
//
//    /**
//     * Search picture in central server
//     *
//     * @param path Picture path
//     * @param k Max number of similar pictures
//     * @param authorization Authorization to access search picture
//     * @param servers Servers that central server must request (if empty, all
//     * server)
//     * @return Most similar pictures and server states
//     * @throws CBIRException Exception during search
//     */
//    public ResultsSimilarities searchInCentralServer(String path, int k, List<String> servers) throws CBIRException {
//        try {
//            BufferedImage img = FileUtils.readPictureFromPath(path, authorization);
//            if (img == null) {
//                throw new CBIRException(path + " is null image! Authorization=" + authorization);
//            }
//            return centralServer.search(img, k, servers);
//        } catch (Exception e) {
//            logger.error("searchInCentralServer:" + e);
//            throw new CBIRException(e.getMessage());
//        }
//    }
    
    /**
     * Search picture in central server
     *
     * @param img Picture
     * @param k Max number of similar pictures
     * @param authorization Authorization to access search picture
     * @param servers Servers that central server must request (if empty, all
     * server)
     * @return Most similar pictures and server states
     * @throws CBIRException Exception during search
     */
    public ResultsSimilarities searchInCentralServer(BufferedImage img, int k, List<String> servers) throws CBIRException {
        try {
            return centralServer.search(img, k, servers);
        } catch (Exception e) {
            logger.error("searchInCentralServer:" + e);
            throw new CBIRException(e.getMessage());
        }
    }    

    /**
     * Search picture that has generated visual words in central server
     *
     * @param visualWords Visual words generated from pictures
     * @param N Number of patch
     * @param k Max number of similar pictures
     * @return Most similar pictures and server states
     * @throws CBIRException Exception during search
     */
    public ResultsSimilarities searchInCentralServer(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k) throws CBIRException {
        try {
            return centralServer.search(visualWords, N, k);
        } catch (Exception e) {
            throw new CBIRException(e.getMessage());
        }
    }
}
