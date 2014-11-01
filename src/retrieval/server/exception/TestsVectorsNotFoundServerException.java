package retrieval.server.exception;

import retrieval.exception.*;

/**
 * Tests vectors cannot be found
 * @author Rollus Loic
 */
public class TestsVectorsNotFoundServerException extends CBIRException {

    /**
     * Error Code
     */
    public static final String CODE = "1004";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public TestsVectorsNotFoundServerException() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TestsVectorsNotFoundServerException(String msg) {
        super(CODE,msg);
    }
}