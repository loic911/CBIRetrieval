/*
 * Copyright 2015 ROLLUS Loïc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval.client.main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import retrieval.client.ListServerInformationSocket;
import retrieval.client.RetrievalClient;
import retrieval.client.ServerInformationSocket;
import retrieval.config.ConfigClient;
import retrieval.dist.ResultsSimilarities;
import retrieval.indexer.main.RetrievalIndexerMain;
import retrieval.storage.index.ResultSim;
import retrieval.utils.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Loïc Rollus
 */
public class RetrievalClientMain {
    private static Logger logger = Logger.getLogger(RetrievalIndexerMain.class);
    /**
     * Main method for client
     * Param0: Config client file
     * Param1: Servers (host:port) list (commat sep list: host1:port1,host2:port2,...)
     * Param2: Image path/url
     * Param3: Maximum similar pictures 
     * Param4: (Optional) Storages name (commat sep: test,mystorage,...)
     * @param args Params arrays
     */
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");
        try {
            search(args);
        } catch(IllegalArgumentException e) {
            logger.error(e);
        } 
    }
    
    private static void search(String[] args) throws Exception{
        ConfigClient configClient = new ConfigClient(args[0]);
        
        List<ServerInformationSocket> serverList = new ArrayList<ServerInformationSocket>();
        String xmlFile = null;
        String hosts = args[1];
        if(hosts==null || hosts.trim().equals("")) {
            throw new IllegalArgumentException("Hosts is not valid '"+hosts+"'");
        } else if(new File(hosts).exists()) {
            xmlFile = new File(hosts).getAbsolutePath();
        } else {
                    
            String[] hostsSplit = hosts.split(",");
            for(String host : hostsSplit) {
                String[] hostSplit = hosts.split(":");
                serverList.add(new ServerInformationSocket(hostSplit[0],Integer.parseInt(hostSplit[1])));
            }
        }       
        
        
        //read picture path
        String picture = args[2]; 
        if(picture==null || picture.trim().equals("")) {
            throw new IllegalArgumentException("Picture path/url is not valid '"+picture+"'");
        }    
        
        //read max simil
        int max=10;
        try {
            max = Integer.parseInt(args[3]);
        }catch(NumberFormatException e) {
            throw new IllegalArgumentException("Max k is not valid '" + max + "'");
        }        
        
            
        String[] storages = new String[0];
         if(args.length>4) {
            storages = args[4].split(",");  
        }     
         
        RetrievalClient client;
        if(xmlFile==null) {
            client = new RetrievalClient(configClient,new ListServerInformationSocket(serverList));
        } else {
            client = new RetrievalClient(configClient,xmlFile);
        }       
               
        BufferedImage image = FileUtils.readPicture(picture);
       
        ResultsSimilarities results = client.search(image, max, storages);
        
        for(ResultSim sim : results.getResults()) {
            System.out.println(sim.getId() + " " + sim.getProperties() + " ====> " + sim.getSimilarities());
        }
                   
    }
}
