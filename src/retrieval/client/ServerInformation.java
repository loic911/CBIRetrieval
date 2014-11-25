/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.client;

/**
 *
 * @author lrollus
 */
public abstract class ServerInformation  implements Cloneable{
    /**
     * Server connexion without error
     */
    public static final int NOERROR = 0;
    /**
     * Server timeout during search
     */
    public static final int TIMEOUT = -1;
    /**
     * Server doesn't answer during communication
     */
    public static final int LOST = -2;
    /**
     * Server unreachable
     */
    public static final int CLOSE = -3;
    /**
     * Server internal error
     */
    public static final int SERVERERROR = -4;
    /**
     * Server error not defined
     */
    public static final int UNDEF = -9;
    /**
     * Number of picture on the server
     */
    protected int sizeOfIndex;

    protected String message;
    protected int connectionState;
    protected int timeout;

    /**
     * Get the state message
     * @return State Message
     */
    public String getStateMessage() {
        if (connectionState == NOERROR) {
            return "NOERROR";
        } else if (connectionState == TIMEOUT) {
            return "TIMEOUT";
        } else if (connectionState == LOST) {
            return "LOST";
        } else if (connectionState == CLOSE) {
            return "CLOSE";
        } else if (connectionState == SERVERERROR) {
            return "SERVERERROR";
        } else if (connectionState == UNDEF) {
            return "UNDEF";
        }
        return "UNDEF";
    }

    /**
     * Change the state of a server
     * @param state New state
     * @param reason Error Message
     */
    public void changeState(int state, String reason) {
        this.connectionState = state;
        this.message = reason;
    }
    /**
     * Get the error message
     * @return Error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the state of server
     * @return state
     */
    public int getState() {
        return connectionState;
    }

    /**
     * Set the new size of index in server (number of pictures)
     * @param sizeOfIndex New size of index
     */
    public void setSizeOfIndex(int sizeOfIndex) {
        this.sizeOfIndex = sizeOfIndex;
    }

    /**
     * Get the number of pictures on server
     * @return Size of index
     */
    public int getSizeOfIndex() {
        return sizeOfIndex;
    }

    @Override
    public abstract Object clone();

}
