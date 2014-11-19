package retrieval.indexer.main;

import java.util.Map;
import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerDistantStorage;

/**
 * This class implement a Indexer to manage picture on simple server with socket/xml
 * @author Rollus Loic
 */
public class RetrievalInfoMain {


    /**
     * Main methode for info
     * Param0: Server HOST
     * param1: Server port
     * Param2: Storage name 
     * @param args Params arays
     */
    public static void main(String[] args) throws Exception {
        info(args);
    }
     
    private static void info(String[] args) throws Exception {
       String host = args[0];
        int port = Integer.parseInt(args[1]);
        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(host,port,args[2],true);
  
        Map<Long,Map<String,String>> maps = index.listPictures();
        
        System.out.println("Pictures indexed: " + maps.size() + " in storage "+args[2]);
        for(Map.Entry<Long,Map<String,String>> entry : maps.entrySet()) {
            System.out.println("Image " + entry.getKey());
            for(Map.Entry<String,String> entryProp : entry.getValue().entrySet()) {
                System.out.println("==>" + entryProp.getKey() + "="+entryProp.getValue());

            }            
        }
    }
}
