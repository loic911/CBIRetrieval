 package retrieval.multicentralserver;

import java.net.Socket;
import org.apache.log4j.Logger;

/**
 * Server Information class
 * The main utility of this class is to get
 * the server information (url, port,...) and
 * the server state (ok, error, timeout, ...)
 * @author Rollus Loic
 */
public class ServerInformationSocket extends ServerInformation implements Cloneable {

    private Socket socket;
    private String address;
    private int port;
    
    static Logger logger = Logger.getLogger(ServerInformationSocket.class);

    /**
     * Constructor for a server information
     * @param address Server Address
     * @param port Server Port
     */
    public ServerInformationSocket(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Constructor for a server information
     * @param address Server Address
     * @param port Server Port
     * @param timeout Timeout
     */
    public ServerInformationSocket(String address, int port, int timeout) {
        this.address = address;
        this.port = port;
        super.timeout = timeout;
    }

    /**
     * Make connection to the server
     * @throws Exception Error during connexion
     */
    public void connect() throws Exception {
        socket = new Socket(getAddress(), getPort());
        socket.setSoTimeout(timeout);
        connectionState = ServerInformationSocket.NOERROR;
    }

    /**
     * Close server connection
     */
    public void close() {
        try {
            socket.close();
        } catch(Exception e) {
            logger.error("Cannot close connection:"+e);
        }
    }

    /**
     * Synchronized because each search request from client (multithreaded)
     * will copy this object
     * @return Clone object
     */
    @Override
    public synchronized Object clone() {
        ServerInformationSocket server = new ServerInformationSocket(this.address, this.port);
        server.message = this.message;
        server.connectionState = this.connectionState;
        server.timeout = this.timeout;
        return server;
    }

    /**
     * String of Object
     * @return String of object
     */
    @Override
    public String toString() {
        return address + ":" + port + " " + getStateMessage() + " " + message + " size=" + sizeOfIndex;
    }

    /**
     * Get Address of server
     * @return Address of server
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set Server address
     * @param address Server address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get Address and port of server
     * @return Address of server concat with its port
     */
    public String getAddressAndPort() {
        return address + ":" + port;
    }

    /**
     * Get port of server
     * @return the server port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set port of server
     * @param port New port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get the socket of the server
     * @return Socket of server
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Set the timeout for the socket
     * @param timeout New Timeout
     * @throws Exception Error during this change
     */
    public void setSocketTimeOut(int timeout) throws Exception {
        socket.setSoTimeout(timeout);
    }
}
