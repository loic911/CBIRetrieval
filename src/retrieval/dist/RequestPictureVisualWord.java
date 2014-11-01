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

    /**
     * Return a string with two values
     * @return String with value
     */
    @Override public String toString() {
        return nbiq + "#" + nbtSum;
    }

}
