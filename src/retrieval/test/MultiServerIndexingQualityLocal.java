/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.test;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import retrieval.client.Client;
import retrieval.config.*;
import retrieval.dist.ResultsSimilarities;
import retrieval.exception.CBIRException;
import retrieval.indexer.RetrievalIndexerLocalStorage;
import retrieval.multicentralserver.MultiCentralServer;
import retrieval.multiserver.MultiServer;
import retrieval.multiserver.globaldatabase.KyotoCabinetDatabase;
import retrieval.storage.Storage;
import retrieval.storage.index.ResultSim;
import retrieval.utils.CollectionUtils;
import retrieval.utils.FileUtils;


/**
 *
 * @author lrollus
 */
public class MultiServerIndexingQualityLocal extends TestMultiServerUtils {

//    private static Logger logger = Logger.getLogger(MultiServerIndexingQualityLocal.class);
//
//    /**
//     * @param args the command line arguments
//     */    
//    public static void main(String[] args) {
//        enableLog();
//        long start = System.currentTimeMillis();
//        try {
//            List<String> indexFiles = new ArrayList<String>();
//            logger.info(new File(args[1]));
//            FileUtils.listFiles(new File(args[1]), indexFiles);
//            //indexFiles = indexFiles.subList(0, 100);
//            logger.info("Index "+indexFiles.size() + " files");
//
//            List<MultiServer> serverList = new ArrayList<MultiServer>();
//            //Create 1 multiserver with 2 servers
//            ConfigServer config = new ConfigServer("config/ConfigServer.prop");
//            String storeName = "KYOTOSINGLEFILE";
//            if(storeName.equals("KYOTOSINGLEFILE")) config.setKyotoCacheMainIndex("3000M");
//            if(storeName.equals("KYOTOMULTIPLEFILE")) config.setKyotoCacheMainIndex("750M");
//            MultiServer server1 = createMultiServer(config,PORT1,4,storeName);
//            serverList.add(server1);
//            
////            for(int i=0;i<indexFiles.size();i++) {
////                try {server1.indexPictureSynchrone(indexFiles.get(i));}catch(Exception e) {e.printStackTrace();}
////            }
//            
//            List<Storage> servers = server1.getServerList();
//            IndexServerThread[] threads = new IndexServerThread[servers.size()];
//            
//            List<String>[] indexFilesSplit = CollectionUtils.splitList(indexFiles, 4);
//            for(int i=0;i<servers.size();i++) {
//               threads[i] = new IndexServerThread(servers.get(i),indexFilesSplit[i]);                
//            }
//            
//            for(int i=0;i<threads.length;i++) {
//                threads[i].start();
//            }
//            
//            for(int i=0;i<threads.length;i++) {
//                threads[i].join();
//            } 
//            
//            logger.info("!!!!!!!!!!!!!!!!!!!!!!!!! ALL ARE JOINING !!!!!!!!!!!!!!!!!!!!!!!!!");
//            
//            
//            List<String> searchFiles = new ArrayList<String>();
//            logger.info(new File(args[2]));
//            FileUtils.listFiles(new File(args[2]), searchFiles);
//           // searchFiles = searchFiles.subList(0, 100);
//            logger.info("Sreach "+searchFiles.size() + " files");
//
//           CentralServer superCentralServer = createCentralServer(new ConfigCentralServer("config/ConfigCentralServer.prop"), server1.getServerMap());
//            //start client
//            Client client = createClient(superCentralServer);
//
//            double[] avg = new double[searchFiles.size()];
//            double sumR1 = 0d;
//            for(int i=0;i<searchFiles.size();i++) {
//                String file = searchFiles.get(i);
//                
//
//                ResultsSimilarities rs = null;
//                try {
//                    rs = client.search(file, 10);
//                } catch (Exception e) {
//                    logger.info(e.toString());
//                }
//                List<ResultSim> ae = rs.getResults();
//
//                System.out.print("************** SERVER STATE ***********");
//                System.out.print(rs.getServersSocket());
//                System.out.print("******************************************");
//
//                System.out.print(ae + "|");
//                double sum = 0d;
//                
//
//                int limit = ae.size();
//                //int limit = 1; 
//                for (int j = 0; j < limit; j++) {
//                    System.out.print(ae.get(j).getPicturePath() + "#" + ae.get(j).getSimilarities() + "|");                    
//                    if (isSameClass(ae.get(j).getPicturePath(), file)) {
//                        if(j==0) {
//                           sumR1 = sumR1 +1; 
//                        }
//                        sum = sum + 1; 
//                    }
//                }
//                avg[i] = (double) (sum / (double)limit);
//                logger.info(avg[i]);
//                logger.info(i + " AVERAGE SUCESS R10=" + computeAvg(avg,i) * 100);
//                logger.info(i + " AVERAGE SUCESS R1=" + (sumR1/searchFiles.size()));
//                logger.info(i + " TIME=" + (System.currentTimeMillis()-start));
//
//            }
//
//            server1.stop();
//
//        //Search each picture
//        }catch(Exception e) {
//            logger.error(e.toString());
//
//        }
//    }
}

//class IndexServerThread extends Thread {
//
//    private List<String> files;
//    private Storage server;
//    
//    private static Logger logger = Logger.getLogger(IndexMultiServerThread.class);
//
//    public IndexServerThread(Storage server,List<String> files) {
//        this.server = server;
//        this.files = files;
//    }
//
//    public void run() {       
//        for(String image : files) {
//            try {
//                RetrievalIndexerLocalStorage.indexSynchrone(server,image);
//            } catch (Exception ex) {
//                 logger.info(ex);
//            }                
//        }
//    }
//}