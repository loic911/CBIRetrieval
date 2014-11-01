/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.server.index;

/**
 *
 * @author finch
 */
public class NoValidPictureException extends Exception {

    /**
     * Creates a new instance of <code>NoValidPicturException</code> without detail message.
     */
    public NoValidPictureException() {
    }


    /**
     * Constructs an instance of <code>NoValidPicturException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoValidPictureException(String msg) {
        super(msg);
    }
}
