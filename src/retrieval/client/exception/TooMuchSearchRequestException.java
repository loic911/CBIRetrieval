package retrieval.client.exception;

import retrieval.exception.CBIRException;

/**
 * Exception if too much client ask search at the same time
 * @author Rollus Loic
 */
public class TooMuchSearchRequestException extends CBIRException {

    /**
     * Code for this error
     */
    public static final String CODE = "2100";

    /**
     * Constructs an instance of 
     * <code>TooMuchSearchRequestException</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public TooMuchSearchRequestException(String msg) {
        super(CODE, msg);
    }
}
