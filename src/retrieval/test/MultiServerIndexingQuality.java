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
import retrieval.multicentralserver.MultiCentralServer;
import retrieval.multiserver.MultiServer;
import retrieval.multiserver.globaldatabase.KyotoCabinetDatabase;
import retrieval.storage.index.ResultSim;
import retrieval.utils.CollectionUtils;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class MultiServerIndexingQuality extends TestMultiServerUtils {

    private static Logger logger = Logger.getLogger(MultiServerIndexingQuality.class);

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
//  //          indexFiles = indexFiles.subList(0, 100);
//            logger.info("Index "+indexFiles.size() + " files");
//
//            List<MultiServer> serverList = new ArrayList<MultiServer>();
//            //Create 1 multiserver with 2 servers
//            MultiServer server1 = createMultiServer(new ConfigServer("config/ConfigServer.prop"),PORT1,4,"MEMORY");
//            serverList.add(server1);
//            
////            MultiServer server2 = createMultiServer(new ConfigServer("config/ConfigServer.prop"),PORT2,2,"KYOTOSINGLEFILE");
////            serverList.add(server2);
////            List<String>[] indexFilesSplit = CollectionUtils.splitList(indexFiles, 1);
//
//
//            IndexMultiServerThread[] threads = new IndexMultiServerThread[1];
//            threads[0] = new IndexMultiServerThread("localhost",server1.getPort(),indexFiles);
//            
////            threads[0] = new IndexMultiServerThread("localhost",server1.getPort(),indexFilesSplit[0]);
////            threads[1] = new IndexMultiServerThread("localhost",server2.getPort(),indexFilesSplit[1]);
//
//            for(int i=0;i<threads.length;i++) {
//                threads[i].start();
//            }
//            for(int i=0;i<threads.length;i++) {
//                threads[i].join();
//            }
//            
//            List<String> searchFiles = new ArrayList<String>();
//            logger.info(new File(args[2]));
//            FileUtils.listFiles(new File(args[2]), searchFiles);
//           // searchFiles = searchFiles.subList(0, 100);
//            logger.info("Sreach "+searchFiles.size() + " files");
//
//
//
//            //start SuperCentralServer
////            CentralServer superCentralServer = createMultiCentralServer(new ConfigCentralServer("config/ConfigCentralServer.prop"), serverList);
//           MultiCentralServer superCentralServer = createCentralServer(new ConfigCentralServer("config/ConfigCentralServer.prop"), server1.getServerMap());
//            //start client
//            Client client = createClient(superCentralServer);
//
//
//            double[] avg = new double[searchFiles.size()];
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
//                int limit = ae.size();
//                //int limit = 1;
//                for (int j = 0; j < limit; j++) {
//                    System.out.print(ae.get(j).getPicturePath() + "#" + ae.get(j).getSimilarities() + "|");
//                    if (isSameClass(ae.get(j).getPicturePath(), file)) {
//                        sum = sum + 1; 
//                    }
//                }
//                avg[i] = (double) (sum / (double)limit);
//                logger.info(avg[i]);
//                logger.info(i + " AVERAGE SUCESS=" + computeAvg(avg,i) * 100);
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

//class IndexMultiServerThread extends Thread {
//
//    private List<String> files;
//    private String host;
//    private int port;
//    
//    private static Logger logger = Logger.getLogger(IndexMultiServerThread.class);
//
//    public IndexMultiServerThread(String host, int port, List<String> files) {
//        this.host = host;
//        this.port = port;
//        this.files = files;
//    }
//
//    public void run() {
//        try {
//            Map<String,String> toIndex = new HashMap<String,String>();
//            for(String image : files) {
//                toIndex.put(image,"###EQUITABLY###");
//            }
//            Map<String,CBIRException> indexPicture = MultiIndexer.indexSynchrone(host,port,toIndex);
//            logger.info("indexPicture="+indexPicture);
//        } catch (Exception ex) {
//             logger.info(ex);
//        }
//    }
//}
