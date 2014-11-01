package retrieval.server.exception;

import retrieval.exception.*;

/**
 * Picture is already in index
 * @author Rollus Loic
 */
public class AlreadyIndexedException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1112";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public AlreadyIndexedException() {
        super(CODE,"");
    }

    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public AlreadyIndexedException(String msg) {
        super(CODE, msg);
    }
}
