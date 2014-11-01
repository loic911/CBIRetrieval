package retrieval.testvector;
import java.util.StringTokenizer;

/**
 * This class implement a simple PatchPoint
 * @author Loic Rollus
 **/
public class PatchPoint {

    private int x;
    private int y;

    /**
     * Construct a PatchPoint
     * @param   x   X value of the point
     * @param   y   Y value of the point
     */
    public PatchPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Construct a PatchPoint
     * @param   value   X and Y value of the point in format x,y
     */
    public PatchPoint(String value) {
        StringTokenizer st = new StringTokenizer(value, ",");
        x = Integer.valueOf(st.nextToken());
        y = Integer.valueOf(st.nextToken());
    }

    /**
     * Getter of X
     * @return X value
     */
    public int getX() {
        return x;
    }

    /**
     * Getter of Y
     * @return Y value
     */
    public int getY() {
        return y;
    }

    /**
     * Return a String value of this point in x,y format
     * @return X and Y value (x,y)
     */
    @Override public String toString() {
        return x + "," + y;
    }
}
