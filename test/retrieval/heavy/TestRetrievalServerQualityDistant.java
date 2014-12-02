/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.heavy;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import retrieval.client.ListServerInformationSocket;
import retrieval.client.RetrievalClient;
import retrieval.client.ServerInformationSocket;
import retrieval.config.ConfigClient;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerDistantStorage;
import retrieval.server.RetrievalServer;
import retrieval.storage.index.ResultSim;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class TestRetrievalServerQualityDistant extends TestMultiServerUtils{
    
    public static void main(String[] args) {
        
        try {
            enableLog();
            File learning = new File("/media/DATA_/image/cbir/test");
            List<String> indexFiles = new ArrayList<String>();
            FileUtils.listFiles(learning, indexFiles);

            RetrievalServer server = createMultiServer(new ConfigServer("config/ConfigServer.prop"),PORT1, 8, "MEMORY");
            
            Long id = 0l;
            for(String file : indexFiles) {
                System.out.println("Index for "+id + " => " + file);
                RetrievalIndexer index = new RetrievalIndexerDistantStorage("localhost",PORT1,RetrievalServer.EQUITABLY,true); 
                Map<String,String> properties = new HashMap<String,String>();
                properties.put("path", file);
                properties.put("date", new Date().toLocaleString());
                index.index(ImageIO.read(new File(file)),id, properties);
                id++;
            }
            
            System.out.println("Total size: " + server.getStoragesSize());
            
            File test = new File("/media/DATA_/image/cbir/test");
            List<String> searchFiles = new ArrayList<String>();
            FileUtils.listFiles(test, searchFiles);            
            
            id = 0l;
            ListServerInformationSocket sockets = new ListServerInformationSocket();
            sockets.add(new ServerInformationSocket("localhost", PORT1), 10000);
            RetrievalClient client = new RetrievalClient(new ConfigClient("config/ConfigClient.prop"), sockets);
            for(String file : indexFiles) {
                System.out.println("Search for "+file);
                ResultsSimilarities result = client.search(ImageIO.read(new File(file)), 30);
                for(ResultSim res : result.getResults()) {
                    System.out.println("*** result "+res.getId() + " => " + res.getProperties());
                }
            }            
            
            
            
               
        }catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
