package retrieval.storage.exception;

import retrieval.exception.*;

/**
 * Picture format is not supported
 * @author Rollus Loic
 */
public class PictureFormatNotSupportedException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1008";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public PictureFormatNotSupportedException() {
        super(CODE,"");
    }

    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PictureFormatNotSupportedException(String msg) {
        super(CODE, msg);
    }
}
