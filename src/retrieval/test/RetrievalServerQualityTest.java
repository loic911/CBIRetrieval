/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import retrieval.client.RetrievalClient;
import retrieval.config.ConfigClient;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerLocalStorage;
import retrieval.server.RetrievalServer;
import retrieval.storage.index.ResultSim;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class RetrievalServerQualityTest extends TestMultiServerUtils{
    
    public static void main(String[] args) {
        
        try {
            String maindir = "/media/DATA_/image/country";
            long maxIndex = 5000;
            long maxSearch = -100;
            File learning = new File(maindir+"/learning");
            List<String> indexFiles = new ArrayList<String>();
            FileUtils.listFiles(learning, indexFiles);
            Collections.shuffle(indexFiles);  
            RetrievalServer server = createMultiServer(new ConfigServer("config/ConfigServer.prop"),PORT1, 8, "MEMORY");
            
            Long id = 0l;
            for(String file : indexFiles) {
                try {
                System.out.println("Index for "+id + " => " + file);
                RetrievalIndexer index = new RetrievalIndexerLocalStorage(server.getNextServer(),false); 
                Map<String,String> properties = new HashMap<String,String>();
                properties.put("path", file);
                properties.put("date", new Date().toLocaleString());
                index.index(ImageIO.read(new File(file)),id, properties);
                id++;
                if(id==maxIndex) break;
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("Total size: " + server.getServersSize());
            
            while(server.getIndexQueueSize()>0) {
                System.out.println("Queue size ="+server.getIndexQueueSize()+"...");
                Thread.sleep(1000);
                
            }
            
            File test = new File(maindir+"/learning");
            File ouput = new File(maindir+"/ouput");
            ouput.delete();
            ouput.mkdirs();
            List<String> searchFiles = new ArrayList<String>();
            FileUtils.listFiles(test, searchFiles);            
            Collections.shuffle(searchFiles); 
            id = 0l;
            RetrievalClient client = new RetrievalClient(new ConfigClient("config/ConfigClient.prop"), server);
            for(String file : indexFiles) {
                try {
                System.out.println("Search for "+file);
                ResultsSimilarities result = client.search(ImageIO.read(new File(file)), 30);
                File dir = new File(ouput.getAbsolutePath()+"/"+id);
                dir.mkdirs();
                String ext = FilenameUtils.getExtension(file);
                FileUtils.copyFile(new File(file), new File(ouput.getAbsolutePath()+"/"+id+"/BASE."+ext));
                Double first=-1d;
                for(ResultSim res : result.getResults()) {
                    if(first==-1) {
                        first = res.getSimilarities();
                    }
                    int percentage = (int)((res.getSimilarities()/first)*100);
                    System.out.println(first);
                    System.out.println(res.getSimilarities());
                    System.out.println((res.getSimilarities()/first));
                    System.out.println((int)(res.getSimilarities()/first));
                    System.out.println((res.getSimilarities()/first)*100);
                    System.out.println("*** result "+res.getId() + " => " + res.getProperties() + " sim="+percentage);
                    String extRes = FilenameUtils.getExtension(res.getProperties().get("path"));
                   
                    String prefix = "_";
                    File output = new File(ouput.getAbsolutePath()+"/"+id+"/"+percentage+"."+extRes);
                    
                    while(output.exists()) {
                        prefix = prefix + "_";
                        output = new File(ouput.getAbsolutePath()+"/"+id+"/"+percentage+"_"+prefix+"."+extRes);
                    }
                    
                    FileUtils.copyFile(new File(res.getProperties().get("path")), output);
                }
                id++;
                if(id==maxSearch) break;
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }            
            
            
            
               
        }catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
