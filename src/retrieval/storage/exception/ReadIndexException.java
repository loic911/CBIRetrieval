package retrieval.storage.exception;
import retrieval.exception.CBIRException;

/**
 * Cannot read index
 * @author Rollus Loic
 */
public class ReadIndexException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1006";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public ReadIndexException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ReadIndexException(String msg) {
        super(CODE,msg);
    }
}