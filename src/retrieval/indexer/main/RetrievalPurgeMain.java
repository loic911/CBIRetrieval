package retrieval.indexer.main;

import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerDistantStorage;

/**
 * This class implement a Indexer to manage picture on simple server with socket/xml
 * @author Rollus Loic
 */
public class RetrievalPurgeMain {


    /**
     * Main methode for purge
     * Param0: Server Host
     * param1: Server port
     * Param2: Storage name 
     * @param args Params arays
     */
    public static void main(String[] args) throws Exception {
        purge(args);
    }
    
    private static void purge(String[] args) throws Exception{
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(host,port,args[2],true);
        index.purge();
    } 
   
}
