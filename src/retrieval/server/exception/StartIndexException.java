package retrieval.server.exception;
import retrieval.exception.*;

/**
 * Cannot start index
 * @author Rollus Loic
 */
public class StartIndexException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1005";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public StartIndexException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public StartIndexException(String msg) {
        super(CODE,msg);
    }
}