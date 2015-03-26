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
package retrieval.storage.index;
/**
 * This class implements an entry for a visual word B in the index
 * of tests vector t
 * Entry are [I - nbit] where iq is the ID of request image I and nbi
 * is the number of times I produce B for t.
 * @author Rollus Loic
 **/
public class Entry implements java.lang.Comparable, java.lang.Cloneable {
    /**
     * Request pictures
     */
    private long I;
    /**
     * Number of patchs produced by Iq mapped with B
     */
    private int NIBT;
    /**
     * Similarity rates between Iq (request picture) and I
     */
    private double similarity = 0;
    /**
     * Number of patch generate from I
     */
    private int NI;

    /**
     * Constructor for an entry
     * @param I Image I ID
     * @param NIBT Number of patchs produced by Iq mapped with B
     */
    public Entry(long I, int NIBT) {
        this.I = I;
        this.NIBT = NIBT;
    }

    /**
     * Compute similarity with (1/nbt) * (niqbt / niq) * (nirbt / nir)
     * @param oneOvernbt (1/nbt)
     * @param NiqbtOverNiq (niqbt/niq)
     * @param NirbtOverNir (nirbt/nir)
     */
    public void addSimilarityComputation(double oneOvernbt,double NiqbtOverNiq,double NirbtOverNir) {
        similarity =similarity + (double) (oneOvernbt * NiqbtOverNiq * NirbtOverNir);
    }

    /**
     * Compare this with object other
     * @param   other   Object that must be compare width this
     * @return   0 if this is equal to other, 1 if other is bigger than this
     * else -1
     **/
    public int compareTo(Object other) {
        double nombre1 = ((Entry) other).getSimilarities();
        double nombre2 = this.similarity;
        if (nombre1 > nombre2) {
            return 1;
        } else if (nombre1 == nombre2) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Clone object
     * @return   Object clone
     **/
    @Override
    public Object clone() {
        try {
            return (Entry) super.clone();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Getter I
     * @return I id
     */
    public long getI() {
        return I;
    }

    /**
     * Get NIBT
     * @return NIBT
     */
    public long getNIBT() {
        return NIBT;
    }


    /**
     * Increment number NIBT
     * @param n Number to add to this number
     */
    public void incrementNIBT(long n) {
        this.setNIBT(this.NIBT + n);
    }

    /**
     * Return a String representation of this
     * @return String representation
     */
    @Override
    public String toString() {
        return this.I + "[" + this.NIBT + "] [" + this.similarity + "]";
    }

    /**
     * Return true if o is equal to this
     * @param o Compare object
     * @return true if o is equal to this
     */
    @Override
    public boolean equals(Object o) {
        Entry ei = (Entry) o;
        if (this.getI() == ei.getI()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (int)this.I;
        hash = 83 * hash + this.NIBT;
        return hash;
    }

    /**
     * Setter NIBT
     * @param number New NIBT
     */
    public void setNIBT(long number) {
        this.NIBT = (int) number;
    }

    /**
     * Getter Similarities
     * @return the similarities
     */
    public double getSimilarities() {
        return similarity;
    }

    /**
     * Increment similarities
     * @param similarity Similarity to add to this similarities number
     */
    public void incrementSimilarities(double similarity) {
        this.similarity = this.similarity + similarity;
    }

    /**
     * @return the numberOfPatch
     */
    public int getNumberOfPatch() {
        return NI;
    }

    /**
     * @param numberOfPatch the numberOfPatch to set
     */
    public void setNumberOfPatch(int numberOfPatch) {
        this.NI = numberOfPatch;
    }
}
