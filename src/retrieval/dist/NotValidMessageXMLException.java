package retrieval.dist;

/**
 * A xml message exception
 * @author Rollus Loic
 */
public class NotValidMessageXMLException extends Exception {

    /**
     * Creates a new instance of 
     * <code>NotValidMessageXMLException</code> without detail message.
     */
    public NotValidMessageXMLException() {
    }

    /**
     * Constructs an instance of <code>NotValidMessageXMLException</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public NotValidMessageXMLException(String msg) {
        super("NotValidMessageException:"+msg);
    }
}
