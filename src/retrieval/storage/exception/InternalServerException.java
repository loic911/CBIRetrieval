package retrieval.storage.exception;

import retrieval.exception.CBIRException;

/**
 * Server Internal error
 * @author Rollus Loic
 */
public class InternalServerException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1001";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public InternalServerException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InternalServerException(String msg) {
        super(CODE,msg);
    }
}