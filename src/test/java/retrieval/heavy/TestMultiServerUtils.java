/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.heavy;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import retrieval.client.ListServerInformationSocket;
import retrieval.client.RetrievalClient;
import retrieval.client.ServerInformationSocket;
import retrieval.config.ConfigClient;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.server.RetrievalServer;
/**
 *
 * @author lrollus
 */
public class TestMultiServerUtils {
    protected static Logger logger = Logger.getLogger(TestMultiServerUtils.class);

    public static int PORT1 = 6001;
    public static int PORT2 = 6002;

    public static int TIMEWAIT = 1500;

    public static RetrievalServer createMultiServer(ConfigServer cc, int port) {
           return createMultiServer(cc, port,2);
    }

    public static RetrievalServer createMultiServer(ConfigServer cc, int port, int serverNumber, String storeName)  {
           logger.info("Start MultiServer...");
            cc.setStoreName(storeName);
            cc.setIndexPath(cc.getIndexPath() + "" + port + "/");
            cc.setIndexCompressThreshold(0);
            cc.setMaxPercentageSimilarWord(0);
            logger.info("StoreName="+storeName + " N="+cc.getNumberOfPatch() + " T="+cc.getNumberOfTestVector());
            System.out.println("StoreName="+storeName + " N="+cc.getNumberOfPatch() + " T="+cc.getNumberOfTestVector());
            RetrievalServer server = new RetrievalServer(cc, "testNetbeans", serverNumber, true);
            server.loadWithSocket(port);
            return server;
    }

    public static RetrievalServer createMultiServer(ConfigServer cc, int port, int serverNumber) {
           return createMultiServer(cc,port,serverNumber,"MEMORY");
    }

    public static RetrievalClient createMultiCentralServer(ConfigClient ccs, List<RetrievalServer> servers) throws Exception {
             logger.info("Start SuperCentralServer...");
                Iterator<RetrievalServer> it = servers.iterator();
                ListServerInformationSocket serverList = new ListServerInformationSocket();
                int i=0;
                while(it.hasNext()) {
                    RetrievalServer server = it.next();
                    logger.info("Add server...");
                    ServerInformationSocket serverInfo = new ServerInformationSocket("localhost", server.getPort());
                    serverInfo.setSocketTimeOut(2000);
                    serverList.add(serverInfo,i);
                    i++;
                }
                RetrievalClient centralServer = new RetrievalClient(ccs, serverList);
                logger.info(serverList);
                return centralServer;
    }
//
//    public static MultiCentralServer createCentralServer(ConfigCentralServer ccs, Map<String,Storage> servers) throws Exception {
//             logger.info("Start SuperCentralServer...");
//              MultiCentralServer centralServer = new MultiCentralServer(ccs, servers);
//              return centralServer;
//    }


//    public static Client createClient(CentralServer cs) throws Exception{
//        logger.info("Start Client...");
//        return new Client(cs);
//    }

    public static List<String> getPicturesPathList() {
        List<String> pictures = new ArrayList<String>();
        pictures.add(new File("image/cytomine567775.jpg").getAbsolutePath());
        pictures.add(new File("image/cytomine12010.jpg").getAbsolutePath());
        pictures.add(new File("image/cytomine74073.jpg").getAbsolutePath());
        pictures.add(new File("image/cytomine477570.jpg").getAbsolutePath());
        return pictures;
    }

    public static List<String> getPicturesPathBadList() {
        List<String> pictures = new ArrayList<String>();
        pictures.add(new File("image/badpicture1.jpg").getAbsolutePath());
        pictures.add(new File("image/badpicture2.jpg").getAbsolutePath());
        return pictures;
    }

    public static void enableLog() {
            BasicConfigurator.configure();
            PropertyConfigurator.configure("log4j.props");
            logger.fatal("LOG FATAL enable");
            logger.error("LOG ERROR enable");
            logger.warn("LOG WARN enable");
            logger.debug("LOG DEBUG enable");
            logger.info("LOG INFO enable");
    }

    public static void printResult(ResultsSimilarities res) {
        String header = "\n####################################################\n";
        String footer = header;

        String result = "";

        for(int i=0;i<res.getResults().size();i++) {
            result = result + i + " => " + res.getResults().get(i).getId() + " (" + res.getResults().get(i).getSimilarities() + ")\n";
        }

        String server = "";
        if(res.getServersSocket()!=null) {
            server = res.getServersSocket().toString();
        } else {
            server = res.getServersObject().toString();
        }

        logger.info(header+"\n"+result+server+footer);
    }
    
    public static boolean isSameClass(String file1, String file2) {
        logger.info("isSameClass:" + getClassLearning(file1) + "|" + getClassTest(file2));
        return getClassLearning(file1).equals(getClassTest(file2));
    }

    public static boolean isSameClassSport(String file1, String file2) {
        logger.info("isSameClass:" + getClassSportLearning(file1) + "|" + getClassSportTest(file2));
        return getClassSportLearning(file1).equals(getClassSportTest(file2));
    }

    public static String getClassLearning(String file) {
        return file.substring(40, 43);
    }

    public static String getClassTest(String file) {
        return file.substring(36, 39);
    }

    public static String getClassSportLearning(String file) {
        return file.split("/")[7];
    }

    public static String getClassSportTest(String file) {
        return file.split("/")[7];
    }

    public static double computeAvg(double[] item) {
        int i;
        double sum = 0d;
        for (i = 0; i < item.length; i++) {
            sum += item[i];
        }
        double average = sum / (double) item.length;
        return average;
    }

    public static double computeAvg(double[] item, int limit) {
        int i;
        double sum = 0d;
        for (i = 0; i < (limit+1); i++) {
            sum += item[i];
        }
        double average = sum / (double)(limit+1);
        return average;
    }    
}
