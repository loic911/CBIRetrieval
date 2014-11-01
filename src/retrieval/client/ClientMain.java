/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import retrieval.config.ConfigCentralServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.exception.CBIRException;
import retrieval.multicentralserver.MultiCentralServer;
import retrieval.utils.CollectionUtils;

/**
 *
 * @author lrollus
 */
public class ClientMain {
    
    /**
     * Main methode for client
     * Param1: multiheavy / multilight
     * if heavy:
     * Param2: central server config file
     * Param3: servers xml file
     * Param4: search picture path
     * Param5: max number of similar pictures
     * Param6: servers to request (delimited by ','; 'all' to search on all server)
     * if light:
     * Param2: central server url
     * Param3: central server port
     * Param4: search picture path (local)
     * Param5: max number of similar pictures
     * Param6: servers to request (delimited by ','; 'all' to search on all server)
     * @param args Params arays
     */
    public static void main(String[] args) throws Exception{
        try {
        if(args[0].equals("multiheavy")) { 
                startHeavyClient(args);
            } 
        else if(args[0].equals("multilight")) {
                startLightClient(args);
            } 
        else {
                throw new IllegalArgumentException("");
            }
        }catch(Exception e) {
            throw e;
        }
    }
    
    public static void startHeavyClient(String[] args) throws CBIRException, IOException {
        String configFile = args[1];
        String serversListFile = args[2];
        
        Client client;
        
        MultiCentralServer centralServer = new MultiCentralServer(new ConfigCentralServer(configFile),serversListFile);
        client = new Client(centralServer);
             
        String path = args[3];
        int k = Integer.parseInt(args[4]);
        
        String[] servers;
        if(args[5].equals("all")) {
            servers = new  String[0];
        } else {
            servers = CollectionUtils.splitArray(args[5], ",");
        }
                
        ResultsSimilarities result = null;
        BufferedImage image = ImageIO.read(new File(path));
        result = client.search(image, k,servers);
        result.print();
    }
    
    public static void startLightClient(String[] args) throws CBIRException, IOException{
        
        Client client = new Client(args[1],Integer.parseInt(args[2]));
             
        String path = args[3];
        int k = Integer.parseInt(args[4]);
        
        String[] servers;
        if(args[5].equals("all")) {
            servers = new String[0];
        } else {
            servers = CollectionUtils.splitArray(args[5], ",");
        }
                
        ResultsSimilarities result = null;
        BufferedImage image = ImageIO.read(new File(path));
        result = client.search(image, k,servers);
        result.print();        
    }
}
