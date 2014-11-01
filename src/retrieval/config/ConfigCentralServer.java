package retrieval.config;

import java.util.Properties;
import retrieval.exception.CBIRException;

/**
 * Configuration file for a central server
 * @author Rollus Loic
 */
public class ConfigCentralServer extends Config {

    /**
     * Number of patch (N) for request picture
     */
    private int numberOfPatch;
    /**
     * Size of patch generation (width)
     */
    private int sizeOfPatchResizeWidth;
    /**
     * Size of patch generation (height)
     */
    private int sizeOfPatchResizeHeight;
    /**
     * Resize method use (influence performance)
     */
    private int resizeMethod;
    /**
     * Path of test vectors
     */
    private String vectorPath;
    /**
     * Max similar picture for a request
     */
    private int kMax;
    /**
     * Max number of client connected
     */
    private int searchMax;
    /**
     * Timeout for server communication
     */
    private int timeout;

    /**
     * Constructor for a central server configuration object
     * @param file Configuration file
     */
    public ConfigCentralServer(String file) throws CBIRException {
        try {
            Properties p = read(file);
            numberOfPatch = Integer.parseInt(p.getProperty("NUMBEROFPATCH",propertiesError));
            sizeOfPatchResizeWidth = Integer.parseInt(p.getProperty("SIZEOFPATCHRESIZEWIDTH", propertiesError));
            sizeOfPatchResizeHeight = Integer.parseInt(p.getProperty("SIZEOFPATCHRESIZEHEIGHT", propertiesError));
            resizeMethod = Integer.parseInt(p.getProperty("RESIZEMETHOD", propertiesError));
            vectorPath = p.getProperty("VECTORPATH", propertiesError);
            kMax = Integer.parseInt(p.getProperty("KMAX", propertiesError));
            searchMax = Integer.parseInt(p.getProperty("SEARCHMAX", propertiesError));
            timeout = Integer.parseInt(p.getProperty("TIMEOUT", propertiesError));            
        } catch(Exception e) {
            throw new CBIRException(e.toString());
        }

    }

    /**
     * @return the numberOfPatch
     */
    public int getNumberOfPatch() {
        return numberOfPatch;
    }

    /**
     * @param numberOfPatch the numberOfPatch to set
     */
    public void setNumberOfPatch(int numberOfPatch) {
        this.numberOfPatch = numberOfPatch;
    }

    /**
     * @return the sizeOfPatchResizeWidth
     */
    public int getSizeOfPatchResizeWidth() {
        return sizeOfPatchResizeWidth;
    }

    /**
     * @param sizeOfPatchResizeWidth the sizeOfPatchResizeWidth to set
     */
    public void setSizeOfPatchResizeWidth(int sizeOfPatchResizeWidth) {
        this.sizeOfPatchResizeWidth = sizeOfPatchResizeWidth;
    }

    /**
     * @return the sizeOfPatchResizeHeight
     */
    public int getSizeOfPatchResizeHeight() {
        return sizeOfPatchResizeHeight;
    }

    /**
     * @param sizeOfPatchResizeHeight the sizeOfPatchResizeHeight to set
     */
    public void setSizeOfPatchResizeHeight(int sizeOfPatchResizeHeight) {
        this.sizeOfPatchResizeHeight = sizeOfPatchResizeHeight;
    }

    /**
     * @return the resizeMethod
     */
    public int getResizeMethod() {
        return resizeMethod;
    }

    /**
     * @param resizeMethod the resizeMethod to set
     */
    public void setResizeMethod(int resizeMethod) {
        this.resizeMethod = resizeMethod;
    }

    /**
     * @return the vectorPath
     */
    public String getVectorPath() {
        return vectorPath;
    }

    /**
     * @param vectorPath the vectorPath to set
     */
    public void setVectorPath(String vectorPath) {
        this.vectorPath = vectorPath;
    }

    /**
     * @return the kMax
     */
    public int getkMax() {
        return kMax;
    }

    /**
     * @param kMax the kMax to set
     */
    public void setkMax(int kMax) {
        this.kMax = kMax;
    }

    /**
     * @return the searchMax
     */
    public int getSearchMax() {
        return searchMax;
    }

    /**
     * @param searchMax the searchMax to set
     */
    public void setSearchMax(int searchMax) {
        this.searchMax = searchMax;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
