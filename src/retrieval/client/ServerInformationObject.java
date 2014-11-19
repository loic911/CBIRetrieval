package retrieval.client;

import retrieval.server.RetrievalServer;

/**
 * Server Information class
 * The main utility of this class is to get
 * the server information (url, port,...) and
 * the server state (ok, error, timeout, ...)
 * @author Rollus Loic
 */
public class ServerInformationObject extends ServerInformation implements Cloneable {


    private RetrievalServer server;
    /**
     * Constructor for a server information
     * @param address Server Address
     * @param port Server Port
     */
    public ServerInformationObject(RetrievalServer server) {
        this.server = server;
    }

    /**
     * Constructor for a server information
     * @param address Server Address
     * @param port Server Port
     * @param timeout Timeout
     */
    public ServerInformationObject(RetrievalServer server, int timeout) {
        this.server  = server;
        super.timeout = timeout;
    }

    /**
     * Synchronized because each search request from client (multithreaded)
     * will copy this object
     * @return Clone object
     */
    @Override public synchronized Object clone() {
        ServerInformationObject serverObj = new ServerInformationObject(this.server);
        serverObj.server = this.server;
        serverObj.message = this.message;
        serverObj.connectionState = this.connectionState;
        return serverObj;
    }

    /**
     * String of Object
     * @return String of object
     */
    @Override public String toString() {
        String state = getStateMessage();
        return "local object server: " + server + "stats=" + state + " " + message + " size=" + sizeOfIndex;
    }

    /**
     * Get Address of server
     * @return Address of server
     */
    public RetrievalServer getServer() {
        return server;
    }

    /**
     * Set Server address
     * @param address Server address
     */
    public void setServer(RetrievalServer server) {
        this.server = server;
    }
}