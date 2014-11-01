package retrieval.multicentralserver;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import retrieval.dist.ResultsSimilarities;
import retrieval.exception.CBIRException;

/**
 * Centrak Server interface
 * @author lrollus
 */
public interface CentralServerInterface {
    ResultsSimilarities search(BufferedImage img, int k) throws CBIRException;
    ResultsSimilarities search(BufferedImage img, int k,String[] servers) throws CBIRException;
    ResultsSimilarities search(BufferedImage img, int k,List<String> servers) throws CBIRException;
    ResultsSimilarities search(BufferedImage img, int N, int k,String[] servers) throws CBIRException;
    ResultsSimilarities search(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k) throws CBIRException;
    ResultsSimilarities search(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k, String[] servers) throws CBIRException;
}
