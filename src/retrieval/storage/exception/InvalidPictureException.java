package retrieval.storage.exception;

import retrieval.exception.*;

/**
 * Picture is invalid
 * @author Rollus Loic
 */
public class InvalidPictureException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1121";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public InvalidPictureException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidPictureException(String msg) {
        super(CODE,msg);
    }
}