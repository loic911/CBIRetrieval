/*
 * Copyright 2009-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval.storage.index.compress.compressPictureHomogeneous;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Don't index an image if the image is too homogenous (black picture,...)
 * @author lrollus
 */
public class CompressIndexPicture {

    /**
     * if picture.getMaxOccurencyForAVisualWord > thresholdSimilarWordPicture => don't index picture. Good to forget monochrome picture, ...
     * **/
    double thresholdSimilarWordPicture;

    /**
     * Create index to remove homogenous pictures
     * @param threshold 
     */
    public CompressIndexPicture(double threshold) {
        this.thresholdSimilarWordPicture = threshold;
    }

    /**
     * Check if picture is too homogenous by checking its visualwords
     * @param visualwords
     * @param N
     * @return 
     */
    public boolean isPictureTooHomogeneous(List<ConcurrentHashMap<String, Long>> visualwords, int N) {
        if(!isCompessEnabled()) {
            return false;
        }
        int T = visualwords.size();
        return hasPictureToMuchSimilarWord(getOccurrenceOfBiggestVisualWordForEachVector(visualwords),N,T);
    }

    /**
     * Check if picture has visualwords with too much occurrence (too homogenous)
     * @param occVisualWordByVector Visualwords occurrence
     * @param N Number of visualwords
     * @param T Number of Test vector
     * @return 
     */
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
    
    /**
     * 
     * @param occurenceForBiggestVisualWord
     * @param N
     * @return 
     */
    private boolean isToMuchSimilarWordForAVector(long occurenceForBiggestVisualWord, int N) {
        return thresholdSimilarWordPicture>0? (double)(occurenceForBiggestVisualWord/(double)N) > thresholdSimilarWordPicture: false;
    }
    
    /**
     * Retrieve the occurrence for the biggest visualwords
     * @param visualWords
     * @return 
     */
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
