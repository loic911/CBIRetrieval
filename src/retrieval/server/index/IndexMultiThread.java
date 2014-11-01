package retrieval.server.index;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.dist.RequestPictureVisualWord;
import retrieval.server.exception.AlreadyIndexedException;
import retrieval.server.exception.PictureTooHomogeneous;
import retrieval.server.exception.ReadIndexException;
import retrieval.server.index.compress.compressPictureHomogeneous.CompressIndexPicture;
import retrieval.testvector.TestVectorListServer;
import retrieval.testvector.TestVectorServer;
import retrieval.utils.FileUtils;

/**
 * This class is a class for multithreaded index
 * This implements a hight level index, it means that you can
 * add pictures, delete pictures, get similar pictures...
 * @author Rollus Loic
 */
public class IndexMultiThread extends Index {

    private static Logger logger = Logger.getLogger(IndexMultiThread.class);

    private CompressIndexPicture compress;
    /**
     * Constructor for a multithread index
     * @param testVectors List of test vectors
     * @param pictureIndex Picture index
     */
    public IndexMultiThread(String idStorage,ConfigServer config, TestVectorListServer testVectors, PictureIndex pictureIndex) throws ReadIndexException {
        this.idStorage = idStorage;
        this.testVectors = testVectors;
        this.pictureIndex = pictureIndex;
        this.testVectors.setPictureIndexForAllTestVectors(pictureIndex);
        this.compress= new CompressIndexPicture(config.getMaxPercentageSimilarWord());
        this.picturesToPurge = PicturesToPurge.readPurgeIndex(idStorage,config);
    }

    /**
     * Add a picture to index
     * @param uri Picture path
     * @param N Number of patchs
     * @param resizeMethod Method for resizing patchs
     * @param sizeOfPatchW Size of patch (w)
     * @param sizeOfPatchH Size of patch (h)
     * @throws AlreadyIndexedException Picture is already indexed
     * @throws NoValidPictureException Picture is not valid
     */
    public Long addPicture(BufferedImage image, Long id, Map<String,String> properties, int N, int resizeMethod, int sizeOfPatchW, int sizeOfPatchH, boolean sync) throws AlreadyIndexedException, PictureTooHomogeneous, NoValidPictureException {
                
        if (pictureIndex.IsAlreadyIndex(id)) {
            throw new AlreadyIndexedException(id + " is already indexed");
        }

        try {
            if (image == null){
                throw new NoValidPictureException(id + " is not a valid picture: img = null!");
            }

            logger.debug("putPictureAsIndexed " + id + " N=" + N);
            pictureIndex.putPictureAsIndexed(id,properties, N);

            //Extract visualword from patch
            logger.debug("generateVisualWordFromPicture " + id + " N=" + N + " resizeMethod=" + resizeMethod + " sizeOfPatchW=" + sizeOfPatchW + " sizeOfPatchH=" + sizeOfPatchH);
            List<ConcurrentHashMap<String, Long>> visualWords = testVectors.generateVisualWordFromPicture(image, id, N, resizeMethod, sizeOfPatchW, sizeOfPatchH);

            //Check if picture is not too homogennous
            boolean isPictureTooHomogeneous = compress.isPictureTooHomogeneous(visualWords, N);
            logger.debug("isPictureTooHomogeneous="+isPictureTooHomogeneous);
            if(isPictureTooHomogeneous) {
                throw new PictureTooHomogeneous();
            }
                
            //add visual word on tests vectors index
            logger.debug("add visualwords into index for imageID=" + id);
            testVectors.addVisualWords(visualWords, id);
            image = null;

            //synchronize memory and disk
            if (sync) {
                sync();
            }
            return id;
        }  catch (NoValidPictureException e) {
            logger.error(e.toString());
            throw new NoValidPictureException(e.getMessage());
        } catch (AlreadyIndexedException e) {
            logger.error(e.toString());
            throw new AlreadyIndexedException(id + " cannot be indexed:" + e);
        } catch (PictureTooHomogeneous e) {
            throw new PictureTooHomogeneous();
        }catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }

    /**
     * Fill structure in argument with nbt an return it
     * Central server will ask NBT for this server during the search process.
     * @param visualWordsByTestVector Map of visual words for each tests vector
     * @return Map of visual words and their NBT for each tests vector
     */
    public synchronized List<ConcurrentHashMap<String, Long>> fillNBT(List<ConcurrentHashMap<String, Long>> visualWordsByTestVector) {
        
        try {
            FillNBTThread[] threads = new FillNBTThread[visualWordsByTestVector.size()];

            for (int i = 0; i < visualWordsByTestVector.size(); i++) {
                threads[i] = new FillNBTThread(this.testVectors.get(i), visualWordsByTestVector.get(i));
                threads[i].start();
            }

            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }
            return visualWordsByTestVector;

        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }

    /**
     * Compute similarity thanks to structure in argument and Niq
     * @param visualWordsByTestVector Visual words for request picture IQ
     * @param Niq Number of patch generated by Iq
     * @return Ordered lists of similar pictures (ordered by similarities with Iq)
     */
    public synchronized List<ResultSim> computeSimilarity(
            List<ConcurrentHashMap<String, RequestPictureVisualWord>> visualWordsByTestVector,
            int Niq) {
        
        try {
            //map with all pictures with at least one visual word similar
            ConcurrentHashMap<Long, Entry> resultsForAllTV = new ConcurrentHashMap<Long, Entry>(this.getSize());

            ComputeSimilaritiesThread[] threads = new ComputeSimilaritiesThread[visualWordsByTestVector.size()];

            //compute similarities for each visual word
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new ComputeSimilaritiesThread(visualWordsByTestVector.get(i), resultsForAllTV, testVectors.get(i), Niq);
                threads[i].start();
            }
            //wait for all thread
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }
            //sort results
            List<ResultSim> resultsList = new ArrayList<ResultSim>(this.getSize());
            for (Map.Entry<Long, Entry> entry : resultsForAllTV.entrySet()) {
                Map<String,String> properties = pictureIndex.getProperties(entry.getKey());
                resultsList.add(new ResultSim(entry.getKey(), properties, entry.getValue().getSimilarity()));

            }

            for (int i = 0; i < resultsList.size(); i++) {
                resultsList.get(i).divideSimilarities(visualWordsByTestVector.size());
            }
            Collections.sort(resultsList);
            return resultsList;

        } catch (Exception e) {
            logger.error(e.toString());
        }
        return new ArrayList<ResultSim>();
    }
}

/**
 * Get all NBT on a test vector for each visual word in visualwords
 * @author Rollus Loic
 */
class FillNBTThread extends Thread {

    private TestVectorServer tv;
    private ConcurrentHashMap<String, Long> visualWords;

    FillNBTThread(TestVectorServer tv, ConcurrentHashMap<String, Long> visualWords) {
        this.tv = tv;
        this.visualWords = visualWords;
    }

    @Override
    public void run() {
        visualWords = tv.fillNBT(visualWords);
    }
}

/**
 * Compute similarities for ona test vector for each visual word visualwords
 * @author Rollus Loic
 */
class ComputeSimilaritiesThread extends Thread {

    private ConcurrentHashMap<Long, Entry> resultsForAllTV;
    private ConcurrentHashMap<String, RequestPictureVisualWord> visualWords;
    private TestVectorServer tv;
    private int Niq;
    
    private static Logger logger = Logger.getLogger(ComputeSimilaritiesThread.class);

    ComputeSimilaritiesThread(
            ConcurrentHashMap<String, RequestPictureVisualWord> visualWords,
            ConcurrentHashMap<Long, Entry> resultsForAllTV,
            TestVectorServer tv, int Niq) {
        this.resultsForAllTV = resultsForAllTV;
        this.tv = tv;
        this.visualWords = visualWords;
        this.Niq = Niq;
    }

    @Override
    public void run() {
        try {
        ConcurrentHashMap<Long, Entry> results = tv.completeSimilarityPictures(visualWords, Niq);
        Index.fusion(resultsForAllTV, results);
        } catch(Exception e) {logger.error(e.toString());}
    }
}