package retrieval.indexer.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerDistantStorage;
import retrieval.indexer.RetrievalIndexerLocalStorage;

/**
 * This class implement a Indexer to manage picture on simple server with socket/xml
 * @author Rollus Loic
 */
public class RetrievalDeleteMain {


    /**
     * Main methode for deleter
     * Param0: Server URL
     * param1: Server port
     * Param2: Storage name 
     * Param3: Picture id (commat sep)
     * @param args Params arays
     */
    public static void main(String[] args) throws Exception {
        delete(args);
    }
       
    private static void delete(String[] args) throws Exception{
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String storage = args[2]; 
        List<String> list = new ArrayList<String>();
        String[] idsSplit = args[3].split(",");
        List<Long> ids = new ArrayList<Long>();
        for(int i=0;i<idsSplit.length;i++) {
            ids.add(Long.parseLong(idsSplit[i]));
        }
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(host,port,storage,false);
        index.delete(ids);
    }    
}
