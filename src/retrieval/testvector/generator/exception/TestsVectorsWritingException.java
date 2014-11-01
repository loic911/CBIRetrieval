package retrieval.testvector.generator.exception;

import retrieval.exception.*;

/**
 * IO exception during the tests vectors files creation
 * @author Rollus Loic
 */
public class TestsVectorsWritingException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "5002";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public TestsVectorsWritingException() {
        super(CODE,"");
    }

    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TestsVectorsWritingException(String msg) {
        super(CODE, msg);
    }
}
