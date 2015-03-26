/*
 * Copyright 2015 ROLLUS Loïc
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
package retrieval.dist;

/**
 * A map between a NBIQ and a NBTSUM for a visual word
 * @author Rollus Loic
 */
public class RequestPictureVisualWord implements Cloneable {

    /**
     * Number of patch of image Iq that map with visual word B
     */
    public int nbiq;
    /**
     * Number of patch map with visual word B in all server
     */
    public int nbtSum;

    /**
     * Constructor for a visual word information
     * @param nbiq Number of patch of image Iq that map with visual word B
     */
    public RequestPictureVisualWord(int nbiq) {
        this.nbiq = nbiq;
        this.nbtSum = 0;
    }

    /**
     * Constructor for a visual word information
     * @param nbiq Number of patch of image Iq that map with visual word B
     * @param nbtSum Number of patch map with visual word B in all server
     */
    public RequestPictureVisualWord(int nbiq,int nbtSum) {
        this.nbiq = nbiq;
        this.nbtSum = nbtSum;
    }

    /**
     * Add nbt to the total NBT sum already compute
     * @param nbt NBT
     */
    public void addNbtSum(int nbt) {
        this.nbtSum = this.nbtSum + nbt;
    }

}
