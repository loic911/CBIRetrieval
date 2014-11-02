package retrieval.storage.exception;
import retrieval.exception.*;

/**
 * Number of tests vectors (T) from central server and server are different
 * @author Rollus Loic
 */
public class WrongNumberOfTestsVectorsException extends CBIRException {

    /**
     * Error Code
     */ 
    public static final String CODE = "1202";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public WrongNumberOfTestsVectorsException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public WrongNumberOfTestsVectorsException(String msg) {
        super(CODE,msg);
    }
}
