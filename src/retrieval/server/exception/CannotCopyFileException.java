package retrieval.server.exception;

import retrieval.exception.*;

/**
 * Cannot copy file
 * @author Rollus Loic
 */
public class CannotCopyFileException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1122";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public CannotCopyFileException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CannotCopyFileException(String msg) {
        super(CODE,msg);
    }
}