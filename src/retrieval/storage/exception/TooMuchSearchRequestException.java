package retrieval.storage.exception;
import retrieval.exception.*;

/**
 * Server is full: too much search request from client
 * @author Rollus Loic
 */
public class TooMuchSearchRequestException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1200";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public TooMuchSearchRequestException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TooMuchSearchRequestException(String msg) {
        super(CODE,msg);
    }
}