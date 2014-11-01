package retrieval.multicentralserver;

/**
 * Interface which force to implement a method to wait client
 * @author Rollus Loic
 */
public interface CentralServerFromClient {
     /**
     * Method which wait client request
     */
    void waitForRequest();
    void close();   
}
