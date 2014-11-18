package retrieval.client.exception;

import retrieval.exception.CBIRException;

/**
 * Exception if an error occur in the central server
 * @author Rollus Loic
 */
public class InternalServerException extends CBIRException {

    /**
     * Code for this error
     */
    public static final String CODE = "2001";


    /**
     * Constructs an instance of 
     * <code>InternalServerException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InternalServerException(String msg) {
        super(CODE,msg);
    }
}