/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.server.index.compress.compressPictureHomogeneous;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lrollus
 */
public class CompressIndexPicture {

    //if picture.getMaxOccurencyForAVisualWord > thresholdSimilarWordPicture => don't index picture. Good to forget monochrome picture, ...
    double thresholdSimilarWordPicture;

    public CompressIndexPicture(double threshold) {
        this.thresholdSimilarWordPicture = threshold;
    }

    public boolean isPictureTooHomogeneous(List<ConcurrentHashMap<String, Long>> visualwords, int N) {
        if(!isCompessEnabled()) {
            return false;
        }
        int T = visualwords.size();
        return hasPictureToMuchSimilarWord(getOccurrenceOfBiggestVisualWordForEachVector(visualwords),N,T);
    }

    public boolean hasPictureToMuchSimilarWord(long[] occVisualWordByVector, int N, int T) {
        
        int numberOfVectorTooSimilar=0;
        double max = (double)((double)T/2);
        for(int i=0;i<occVisualWordByVector.length;i++) {
            if(isToMuchSimilarWordForAVector(occVisualWordByVector[i],N)){
                numberOfVectorTooSimilar++;
            }
        }
        return (numberOfVectorTooSimilar>max);
    }
    private boolean isToMuchSimilarWordForAVector(long occurenceForBiggestVisualWord, int N) {
        return thresholdSimilarWordPicture>0? (double)(occurenceForBiggestVisualWord/(double)N) > thresholdSimilarWordPicture: false;
    }
    public long[] getOccurrenceOfBiggestVisualWordForEachVector(List<ConcurrentHashMap<String, Long>> visualWords) {
        long[] result = new long[visualWords.size()];
        for(int i=0;i<visualWords.size();i++) {
            result[i]=getOccurrenceOfBiggestVisualWord(visualWords.get(i));
        }
        return result;
    }
    private long getOccurrenceOfBiggestVisualWord(ConcurrentHashMap<String, Long> visualWords) {
        long max=Long.MIN_VALUE;
        Iterator<Map.Entry<String,Long>> it = visualWords.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String,Long> entry = it.next();
            if(entry.getValue()>max){
                max = entry.getValue();
            }
        }
        return max;
    }

    public boolean isCompessEnabled() {
        return thresholdSimilarWordPicture>0;
    }
}
