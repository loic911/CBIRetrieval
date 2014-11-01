package retrieval.server;

/**
 * A generic interface for server request class
 * @author Rollus Loic
 */
public interface StorageNetworkInterface {

    /**
     * A communicator will wait for a client from server
     * (indexer, central server...)
     */
    void waitForRequest();

    /**
     * Close interface and stop socket waiting
     */
    void close();
}
