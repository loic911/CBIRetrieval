package retrieval.storage.exception;
import retrieval.exception.*;

/**
 * Client ask too much similar pictures; K is too hight
 * @author Rollus Loic
 */
public class TooMuchSimilarPicturesAskException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1201";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public TooMuchSimilarPicturesAskException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TooMuchSimilarPicturesAskException(String msg) {
        super(CODE,msg);
    }
}
