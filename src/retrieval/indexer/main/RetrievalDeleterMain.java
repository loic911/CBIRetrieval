package retrieval.indexer.main;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerDistantStorage;

/**
 * This class implement a Indexer to manage picture on simple server with socket/xml
 * @author Rollus Loic
 */
public class RetrievalDeleterMain {

private static Logger logger = Logger.getLogger(RetrievalDeleterMain.class);
    /**
     * Main methode for deleter
     * Param0: Server URL
     * param1: Server port
     * Param3: Picture ids (commat sep)
     * @param args Params arays
     */
    public static void main(String[] args) throws Exception {
        delete(args);
    }
       
    private static void delete(String[] args) throws Exception{
        String host = args[0];
        int port = Integer.parseInt(args[1]); 
        String[] idsSplit = args[2].split(",");
        List<Long> ids = new ArrayList<Long>();
        for(int i=0;i<idsSplit.length;i++) {
            ids.add(Long.parseLong(idsSplit[i]));
        }
        
       logger.info("DELETE Host:"+host + " Port:"+port + " Image:"+ ids);
       
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(host,port,null,false);
        index.delete(ids);
    }    
}
