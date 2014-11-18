package retrieval.client.exception;

import retrieval.exception.CBIRException;

/**
 * Exception if client ask too much similar pictures (k>threshold)
 * @author Rollus Loic
 */
public class TooMuchSimilaritiesAskException extends CBIRException {

    /**
     * Code for this error
     */
    public static final String CODE = "2101";

    /**
     * Constructs an instance of <code>TooMuchSimilaritiesAskException</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public TooMuchSimilaritiesAskException(String msg) {
        super(CODE, msg);
    }
}
