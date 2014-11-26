/*
 * Copyright 2009-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package retrieval.client;

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
    private final String address;
    private final int port;
    
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
     * Get port of server
     * @return the server port
     */
    public int getPort() {
        return port;
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
