package retrieval.exception;

/**
 * A CBIR exception
 * @author Rollus Loic
 */
public class CBIRException extends Exception {

    /**
     * Code for the CBIRException
     */
    String code;

    /**
     * Constructor of CBIRException
     * @param code Error code for exception
     * @param message Message for exception
     */
    public CBIRException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructor of CBIRException
     * @param msg Message for exception
     */
    public CBIRException(String msg) {
        super(msg);
    }

    /**
     * Get code of CBIRException
     * @return Code
     */
    public String getCode()
    {
        return code;
    }
    
    public boolean isNotAnException() {
        return "1000".equals(code);
    }

}
