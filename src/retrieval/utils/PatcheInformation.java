package retrieval.utils;

/**
 * This class implements a PatchInformation
 * @author Loic Rollus
 **/
public class PatcheInformation {

    private int x;
    private int y;
    private int w;
    private int h;

    /**
     * Construct a PatchInformation Object
     * @param   x   X
     * @param   y   Y
     * @param   w   Width
     * @param   h   Height
     **/
    public PatcheInformation(int x,int y,int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Return a String format of a PatchPoint
     * @return   String value
     **/
    @Override public String toString()
    {
        return "Point("+x+","+y+") with ("+w+","+h+")";
    }

    /**
     * Getter X
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Setter X
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Getter Y
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Setter Y
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Getter W
     * @return the w
     */
    public int getW() {
        return w;
    }

    /**
     * Setter W
     * @param w the w to set
     */
    public void setW(int w) {
        this.w = w;
    }

    /**
     * Getter H
     * @return the h
     */
    public int getH() {
        return h;
    }

    /**
     * Setter H
     * @param h the h to set
     */
    public void setH(int h) {
        this.h = h;
    }
}
