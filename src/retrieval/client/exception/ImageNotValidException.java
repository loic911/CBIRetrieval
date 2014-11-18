package retrieval.client.exception;

import retrieval.exception.CBIRException;

/**
 * Exception is the picture send by client is not valid
 * @author Rollus Loic
 */
public class ImageNotValidException extends CBIRException {

    /**
     * Code for this error
     */
    public static final String CODE = "2102";


    /**
     * Constructs an instance of 
     * <code>ImageNotValidException</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public ImageNotValidException(String msg) {
        super(CODE,msg);
    }
}