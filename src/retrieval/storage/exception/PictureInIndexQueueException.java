package retrieval.storage.exception;

import retrieval.exception.CBIRException;
/**
 * Picture is in indexed queue
 * @author Rollus Loic
 */
public class PictureInIndexQueueException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1113";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public  PictureInIndexQueueException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public  PictureInIndexQueueException(String msg) {
        super(CODE,msg);
    }
}
