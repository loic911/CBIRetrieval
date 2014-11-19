package retrieval.storage.exception;

/**
 * Bad request message
 * @author Rollus Loic
 */
import retrieval.exception.CBIRException;
/**
 *
 * @author finch
 */
public class BadRequestException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1002";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public BadRequestException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BadRequestException(String msg) {
        super(CODE,msg);
    }
}
