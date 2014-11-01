package retrieval.client;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import retrieval.dist.ResultsSimilarities;
import retrieval.exception.CBIRException;

/**
 * This interface force to implement a method to search a picture in
 * a central server.
 * The goal of this interface is to allow multiple implementation to
 * make communication with client and central server
 * @author Rollus Loic
 */
public interface ClientToCentralServer {

    /**
     * Search pictures in central server
     * @param img Picture
     * @param k Max number of similar pictures
     * @param authorization Authorization to access search picture
     * @param servers Servers that central server must request (if empty, all server)
     * @return Most similar pictures and server states
     * @throws CBIRException Exception during search
     */
    ResultsSimilarities searchInCentralServer(BufferedImage img, int k,List<String> containers) throws CBIRException;    
    
    /**
     * Search pictures that has generated visual words in central server
     * @param visualWords Visual words generated from pictures
     * @param N Number of patch
     * @param k Max number of similar pictures
     * @return Most similar pictures and server states
     * @throws CBIRException Exception during search
     */       
    ResultsSimilarities searchInCentralServer(List<ConcurrentHashMap<String, Long>> visualWords ,  int N, int  k) throws CBIRException;
}
