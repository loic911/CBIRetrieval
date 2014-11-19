package retrieval.testvector.generator.exception;

import retrieval.exception.CBIRException;

/**
 * Argument for tests vectors creation error (ex: negative value for m)
 * @author Rollus Loic
 */
public class TestsVectorsArgumentException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "5001";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public TestsVectorsArgumentException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TestsVectorsArgumentException(String msg) {
        super(CODE,msg);
    }
}