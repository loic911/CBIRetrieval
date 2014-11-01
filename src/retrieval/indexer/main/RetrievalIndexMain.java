package retrieval.indexer.main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerDistantStorage;
import retrieval.indexer.RetrievalIndexerLocalStorage;

/**
 * This class implement a Indexer to manage picture on simple server with socket/xml
 * @author Rollus Loic
 */
public class RetrievalIndexMain {


    /**
     * Main methode for indexer
     * Param0: Server host
     * param1: Server port
     * Param2: Storage name 
     * Param3: Picture URI
     * Param4: 'async' or 'sync' string
     * Param5: Picture id
     * Param6: Picture properties keys (comma sep) (e.g. id,name,date)
     * Param7: Picture properties values (comma sep) (e.g. 123,test,2014/10/31)
     * @param args Params arays
     */
    public static void main(String[] args) throws Exception {
        index(args);
    }
    
    private static void index(String[] args) throws Exception{
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String storage = args[2]; 
        String picture = args[3];        
        boolean asynchrone = !args[4].equals("sync");        
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(host,port,storage,asynchrone);
        
        Long id = null;
        
        if(args.length>5) {
            try {
               id = Long.parseLong(args[5]); 
            } catch(Exception e) {
                System.out.println("Image id must be a number (long)! "+args[5]+ " is not valid!");
            }
            
        }
        
        Map<String,String> properties = null;
        
        if(args.length>6) {
            try {
               String[] keys = args[6].split(",");
               String[] values = args[7].split(",");
               
               properties = new HashMap<String,String>();
               
               for(int i=0;i<keys.length;i++) {
                   properties.put(keys[i], values[i]);
               }
               
            } catch(Exception e) {
                System.out.println("Keys or values are not valid:"+e.getMessage());
            }
            
        }    
        
        if(new File(picture).exists()) {
            index.index(new File(picture),id,properties);
        } else {
            index.index(new URL(picture),id,properties);
        }
                   
    }

}
