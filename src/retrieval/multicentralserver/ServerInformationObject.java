package retrieval.multicentralserver;

import retrieval.multiserver.MultiServer;
import retrieval.server.Storage;

/**
 * Server Information class
 * The main utility of this class is to get
 * the server information (url, port,...) and
 * the server state (ok, error, timeout, ...)
 * @author Rollus Loic
 */
public class ServerInformationObject extends ServerInformation implements Cloneable {


    private MultiServer server;
    /**
     * Constructor for a server information
     * @param address Server Address
     * @param port Server Port
     */
    public ServerInformationObject(MultiServer server) {
        this.server = server;
    }

    /**
     * Constructor for a server information
     * @param address Server Address
     * @param port Server Port
     * @param timeout Timeout
     */
    public ServerInformationObject(MultiServer server, int timeout) {
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
    public MultiServer getServer() {
        return server;
    }

    /**
     * Set Server address
     * @param address Server address
     */
    public void setServer(MultiServer server) {
        this.server = server;
    }
}