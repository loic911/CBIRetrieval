package retrieval.server.exception;
import retrieval.exception.*;

/**
 * Cannot close index
 * @author Rollus Loic
 */
public class CloseIndexException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1007";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public CloseIndexException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CloseIndexException(String msg) {
        super(CODE,msg);
    }
}