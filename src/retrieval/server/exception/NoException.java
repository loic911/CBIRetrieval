package retrieval.server.exception;

import retrieval.exception.*;

/**
 * No Exception
 * @author Rollus Loic
 */
public class NoException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1000";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public NoException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoException(String msg) {
        super(CODE,msg);
    }
}
