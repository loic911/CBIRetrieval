package retrieval.server.exception;
import retrieval.exception.*;

/**
 * No right to do this
 * NOT IMPLEMENTED! MUST BE IMPLEMENTED WITH SECURITY SUPPORT
 * @author Rollus Loic
 */
public class UnauthorizedException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1003";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public UnauthorizedException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnauthorizedException(String msg) {
        super(CODE,msg);
    }
}