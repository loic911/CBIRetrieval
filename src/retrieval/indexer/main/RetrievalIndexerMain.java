package retrieval.indexer.main;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerDistantStorage;
import retrieval.server.RetrievalServer;

/**
 * This class implement a Indexer to manage picture on simple server with socket/xml
 * @author Rollus Loic
 */
public class RetrievalIndexerMain {

    private static Logger logger = Logger.getLogger(RetrievalIndexerMain.class);
    /**
     * Main methode for indexer
     * Param0: Server host
     * param1: Server port
     * Param2: Picture URI
     * Param3: (Optional) 'async' or 'sync' string
     * Param4: (Optional) Storage name 
     * Param5: (Optional) Picture id
     * Param6: (Optional) Picture properties keys (comma sep) (e.g. id,name,date)
     * Param7: (Optional) Picture properties values (comma sep) (e.g. 123,test,2014/10/31)
     * @param args Params arays
     */
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");
        try {
            index(args);
        } catch(IllegalArgumentException e) {
            logger.error(e);
        } 
    }
    
    private static void index(String[] args) throws Exception{
        //read host parameter
        String host = args[0];
        if(host==null || host.trim().equals("")) {
            throw new IllegalArgumentException("Host is not valid '"+host+"'");
        }
        
        //read port parameter
        int port=-1;
        try {
            port = Integer.parseInt(args[1]);
        }catch(NumberFormatException e) {
            throw new IllegalArgumentException("Port is not valid '" + port + "'");
        }
        
        //read picture path
        String picture = args[2]; 
        if(picture==null || picture.trim().equals("")) {
            throw new IllegalArgumentException("Picture path/url is not valid '"+picture+"'");
        }      
       
        boolean synchrone = false;
                
         if(args.length>3) {
            try {
               synchrone = !args[3].equals("async");
            } catch(Exception e) {
                throw new IllegalArgumentException("Async param must be 'sync' or 'async'");
            }
            
        }     
         
         String storage = RetrievalServer.EQUITABLY;
         if(args.length>4) {
            storage = args[4]; 
         } 
         if(storage==null || storage.trim().equals("")) {
            throw new IllegalArgumentException("Storage name is not valid '"+storage+"'");
        }           
                
        RetrievalIndexer index = new RetrievalIndexerDistantStorage(host,port,storage,synchrone);
        
        Long id = null;
        
         if(args.length>5) {
            try {
               id = Long.parseLong(args[5]); 
            } catch(Exception e) {
                throw new IllegalArgumentException("Image id must be a number (long)! "+args[5]+ " is not valid!");
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
                throw new IllegalArgumentException("Keys or values are not valid:"+e.getMessage());
            }
            
        }    
        
        logger.info("INDEX Host:"+host + " Port:"+port + " Image:"+ picture + " Async:"+synchrone + " Storage: "+storage + " Id: " + id + " properties:"+properties);
        if(new File(picture).exists()) {
            index.index(new File(picture),id,properties);
        } else if(picture.startsWith("http:") || picture.startsWith("https:")) {
            index.index(new URL(picture),id,properties);
        } else {
            throw new IllegalArgumentException("Image path must be a local file or a valid URL:"+picture);
        }
                   
    }

}
