package retrieval.storage.exception;
import retrieval.exception.CBIRException;

/**
 * Server is full: too much index request in waited queue
 * @author Rollus Loic
 */
public class TooMuchIndexRequestException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1120";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public TooMuchIndexRequestException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TooMuchIndexRequestException(String msg) {
        super(CODE,msg);
    }
}