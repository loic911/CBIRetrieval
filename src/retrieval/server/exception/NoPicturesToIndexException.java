package retrieval.server.exception;

import retrieval.exception.*;

/**
 * Index is empty
 * @author Rollus Loic
 */
public class NoPicturesToIndexException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1100";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public NoPicturesToIndexException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoPicturesToIndexException(String msg) {
        super(CODE,msg);
    }
}
