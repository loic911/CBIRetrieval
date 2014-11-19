package retrieval.storage.exception;


import retrieval.exception.CBIRException;

/**
 * Picture is not found
 * @author Rollus Loic
 */
public class PictureNotFoundException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1111";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public PictureNotFoundException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PictureNotFoundException(String msg) {
        super(CODE,msg);
    }
}