package retrieval.storage.exception;
import retrieval.exception.CBIRException;

/**
 * Index path is invalid
 * @author Rollus Loic
 */
public class InvalidIndexPathException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1100";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public InvalidIndexPathException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidIndexPathException(String msg) {
        super(CODE,msg);
    }
}