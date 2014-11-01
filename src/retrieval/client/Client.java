package retrieval.client;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.client.net.ClientToCentralServerLocal;
import retrieval.client.net.ClientToCentralServerXML;
import retrieval.dist.ResultsSimilarities;
import retrieval.exception.CBIRException;
import retrieval.multicentralserver.MultiCentralServer;

/**
 * This class implemets a client which can make request.
 * It can be a Heavy client (with central server) or a light client (withouth)
 * @author Rollus Loic
 */
public class Client {

    /**
     * Communication object to a central server (local or distant)
     */
    private ClientToCentralServer centralServer;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(Client.class);

    /**
     * Constructor for a light client (without central server)
     * @param urlToCentralServer URL Central server URL
     * @param portToCentralServer Central server port
     */
    public Client(String urlToCentralServer, int portToCentralServer) {
        logger.info("Client: start");
        this.centralServer = new ClientToCentralServerXML(urlToCentralServer, portToCentralServer);
    }

//    /**
//     * Constructor for an heavy client (with central server)
//     * @param cs Central server
//     */
//    public Client(CentralServer cs) {
//        logger.info("Client: start");
//        this.centralServer = new ClientToCentralServerLocal(cs);
//    }

    /**
     * Constructor for an heavy client (with super central server)
     * @param cs Central server
     */
    public Client(MultiCentralServer cs) {
        logger.info("Client: start");
        this.centralServer = new ClientToCentralServerLocal(cs);
    }
//
//    /**
//     * Search similar pictures on central server
//     * @param path Path of request image Iq
//     * @param k Number of similar pictures
//     * @return Similar picture of picture from path
//     * @throws CBIRException Error from central server
//     */
//    public ResultsSimilarities search(String path, int k) throws CBIRException {
//        return this.search(path, k, new String[0]);
//    }

//    /**
//     * Search similar pictures on central server
//     * @param path Path of request image Iq
//     * @param k Number of similar pictures
//     * @param container Container to request
//     * @return Similar picture of picture from path
//     * @throws CBIRException Error from central server
//     */
//    public ResultsSimilarities search(String path, int k,String[] container) throws CBIRException {
//        return this.search(path, k, new PictureAuthorization(),container);
//    }
    
    /**
     * Search similar pictures on central server
     * @param path Path of request image Iq
     * @param k Number of similar pictures
     * @param container Container to request
     * @return Similar picture of picture from path
     * @throws CBIRException Error from central server
     */
    public ResultsSimilarities search(BufferedImage img, int k,String[] containers) throws CBIRException {
        return centralServer.searchInCentralServer(img, k, Arrays.asList(containers));
    }
    
    /**
     * Search similar pictures on central server
     * @param path Path of request image Iq
     * @param k Number of similar pictures
     * @param container Container to request
     * @return Similar picture of picture from path
     * @throws CBIRException Error from central server
     */
    public ResultsSimilarities search(BufferedImage img, int k,List<String> containers) throws CBIRException {
        return centralServer.searchInCentralServer(img, k, containers);
    }        

//    /**
//     * Search similar pictures on central server
//     * @param path Path of request image Iq
//     * @param k Number of similar pictures
//     * @param login Login for basic auth to access picture
//     * @param password Password for basic auth to access picture
//     * @return Similar picture of picture from path
//     * @throws CBIRException Error from central server
//     */
//    public ResultsSimilarities search(String path, int k,String login, String password) throws CBIRException {
//        return this.search(path, k, new PictureAuthorization(login,password),new String[0]);
//    }
//
//    /**
//     * Search similar pictures on central server
//     * @param path Path of request image Iq
//     * @param k Number of similar pictures
//     * @param login Login for basic auth to access picture
//     * @param password Password for basic auth to access picture
//     * @param container Container to request
//     * @return Similar picture of picture from path
//     * @throws CBIRException Error from central server
//     */
//    public ResultsSimilarities search(String path, int k,String login, String password,String[] container) throws CBIRException {
//        return this.search(path, k, new PictureAuthorization(login,password),container);
//    }
//
//    /**
//     * Search similar pictures on central server
//     * @param path Path of request image Iq
//     * @param k Number of similar pictures
//     * @param publicKey Public key for authentification to access picture
//     * @param privateKey Private key for authentification to access picture
//     * @param host Host for authentification to access picture
//     * @return Similar picture of picture from path
//     * @throws CBIRException Error from central server
//     */
//    public ResultsSimilarities search(String path, int k,String publicKey, String privateKey, String host) throws CBIRException {
//        return this.search(path, k, new PictureAuthorization(publicKey,privateKey,host),new String[0]);
//    }
//
//    /**
//     * Search similar pictures on central server
//     * @param path Path of request image Iq
//     * @param k Number of similar pictures
//     * @param publicKey Public key for authentification to access picture
//     * @param privateKey Private key for authentification to access picture
//     * @param host Host for authentification to access picture
//     * @param container Container to request
//     * @return Similar picture of picture from path
//     * @throws CBIRException Error from central server
//     */
//    public ResultsSimilarities search(String path, int k,String publicKey, String privateKey, String host,String[] container) throws CBIRException {
//        return this.search(path, k, new PictureAuthorization(publicKey,privateKey,host),container);
//    }

    /**
     * Search similar pictures on central server 
     * @param visualWords Visual words generated by picture
     * @param N Number of patchs
     * @param k Number of similar pictures
     * @return Similar picture of picture with visualwords
     * @throws CBIRException Error from central server
     */
    public ResultsSimilarities search(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k) throws CBIRException {
        return centralServer.searchInCentralServer(visualWords, N, k);
    }
//    
//    /**
//     * Search similar pictures on central server
//     * @param path Path of request image Iq
//     * @param k Number of similar pictures
//     * @param authorization Authorization to access picture
//     * @param container Container to request
//     * @return Similar picture of picture from path
//     * @throws CBIRException Error from central server
//     */
//    private ResultsSimilarities search(String path, int k, PictureAuthorization authorization,String[] container) throws CBIRException {
//        logger.info("search: " + path + " - " + k);
//        return centralServer.searchInCentralServer(path, k, authorization,Arrays.asList(container));
//    }
}
