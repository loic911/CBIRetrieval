/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.Storage;
import retrieval.storage.index.ResultSim;
import retrieval.utils.CollectionUtils;
import retrieval.utils.FileUtils;
/**
/**
 *
 * @author lrollus
 */
public class TestUtils {
    private static Logger logger = Logger.getLogger(TestUtils.class);
    public static void enableLog() {
            BasicConfigurator.configure();
            PropertyConfigurator.configure("log4j.properties");
            logger.fatal("LOG FATAL enable");
            logger.error("LOG ERROR enable");
            logger.warn("LOG WARN enable");
            logger.debug("LOG DEBUG enable");
            logger.info("LOG INFO enable");
            
            Runtime runtime = Runtime.getRuntime();

            long maxMemory = runtime.maxMemory();
            long allocatedMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();

            System.out.println("free memory: " + freeMemory / (1024*1024));
            System.out.println("allocated memory: " + allocatedMemory / (1024*1024));
            System.out.println("max memory: " + maxMemory /(1024*1024));
            System.out.println("total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / (1024*1024));             
    }

    public static void printResult(ResultsSimilarities res) {
        String header = "\n####################################################" +
                "\n####################################################" +
                "\n####################################################";

        String footer = "####################################################" +
                "\n####################################################" +
                "\n####################################################";

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
    
    public static void executeCommand(String command, String[] args) throws Exception {
      String line;
      String fullCommand = command + CollectionUtils.join(args, " ");
      System.out.println("Execute: " + fullCommand);
      Process p = Runtime.getRuntime().exec(fullCommand);
      BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      
      while ((line = bri.readLine()) != null) {
        System.out.println(line);
      }
      bri.close();
      while ((line = bre.readLine()) != null) {
        System.out.println(line);
      }
      bre.close();
      p.waitFor();
      System.out.println("Done.");    
    }    
    
    public static String DEFAULTJARLOCATION = "dist/CBIRetrievalFull.jar";
    
    public static String LOCALPICTUREPATH = "testdata/pictures/cyto/";
    public static String LOCALPICTURE1 = LOCALPICTUREPATH+"crop1.jpg";
    
    public static final Map<String, String> LOCALPICTURE1MAP = ImmutableMap.of(
        "name", "CROP1",
        "date", new Date().toString()
    );
    
    public static String LOCALPICTURE2 = LOCALPICTUREPATH+"crop2.jpg";
    public static String LOCALPICTURE3 = LOCALPICTUREPATH+"crop3.jpg";
    public static String LOCALPICTURE4 = LOCALPICTUREPATH+"crop4.jpg";
    public static String LOCALPICTURE5 = LOCALPICTUREPATH+"crop5.jpg";
    public static String LOCALPICTURE6 = LOCALPICTUREPATH+"crop6.jpg";
    public static String LOCALPICTURE7 = LOCALPICTUREPATH+"crop7.jpg";
    public static String LOCALPICTURE8 = LOCALPICTUREPATH+"crop8.jpg";  
    
  
    
    public static String URLPICTURENOAUTH = "http://www.google.be/images/srpr/logo3w.png";
//    
//    public static String BASICAUTHPICTURE1 = "https://confluence.atlassian.com/images/icons/profilepics/Avatar-1.png";
//    public static String BASICAUTHPICTURE2 = "https://confluence.atlassian.com/images/icons/profilepics/Avatar-2.png";
//    public static String BASICAUTHPICTURE3 = "https://confluence.atlassian.com/images/icons/profilepics/Avatar-3.png";
//    public static String BASICAUTHPICTURE4 = "https://confluence.atlassian.com/images/icons/profilepics/Avatar-4.png";
//    
//    public static String KEYSAUTHPICTURE1 = "http://beta.cytomine.be/api/userannotation/404941/crop.jpg";
//    public static String KEYSAUTHPICTURE2 = "http://beta.cytomine.be/api/userannotation/649140/crop.jpg";
//    public static String KEYSAUTHPICTURE3 = "http://beta.cytomine.be/api/userannotation/593954/crop.jpg";
//    public static String KEYSAUTHPICTURE4 = "http://beta.cytomine.be/api/userannotation/593791/crop.jpg";
    
    public static String BADPICTURE1 = "testdata/badpicture1.jpg"; 
    public static String BADPICTURE2 = "testdata/badpicture2.jpg"; 
    
    public static String PICTURETOOHOMOGENOUS = "testdata/pictures/homogenous.jpg"; 
    
//    public static String PUBLIC = "xxxxx";
//    public static String PRIVATE = "xxxxxxx";
//    public static String HOST = "http://beta.cytomine.be";    
 
    public static String LOGIN = "retrievaltest";
    public static String PASSWORD = "test123";
    
    static public int PORT1 = 6001;//6001
    static public int PORT2 = 6002;//6002

    static public int PORTCENTRALSERVER = 6020;//7001
    
    
    static public int MULTISERVERPORT1 = 6101;//8001
    static public int MULTISERVERPORT2 = 6102;//8002  
    
    static public String MULTISERVERURL = "localhost";
    
    
    
    static public String[] format = {"JPG","JPEG","PNG","TIFF"};

    static public int TIMEWAIT = 2000;
    
    public boolean waitForPictureIndexed(Storage storage, Long id) {
        long startTime = System.currentTimeMillis();
        long maxTime = 10000;
        while((System.currentTimeMillis()-startTime)<maxTime) {
            if(storage.isPictureInIndex(id)) {
                return true;
            }
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
            }
        }
        return false;                
    }
    
    public boolean waitToSizeEquals(Storage server, int size) {
        long startTime = System.currentTimeMillis();
        long maxTime = 10000;
        while((System.currentTimeMillis()-startTime)<maxTime) {
            if(server.getNumberOfItem()==size) {
                return true;
            }
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
            }
        }
        return false;
    }
    
    public boolean waitEquals(int expected, int size) {
        long startTime = System.currentTimeMillis();
        long maxTime = 10000;
        while((System.currentTimeMillis()-startTime)<maxTime) {
            if(expected==size) {
                return true;
            }
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
            }
        }
        return false;
    }    
    
    public boolean waitServerCreation(RetrievalServer multi,String key) throws Exception {
        long startTime = System.currentTimeMillis();
        long maxTime = 10000;
        Storage server = null;
        while((System.currentTimeMillis()-startTime)<maxTime) {
            server = multi.getStorage(key);
            if(server!=null) return true;
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
            }
        }
        return false;
    }    
    
    public static RetrievalServer createMultiServer(ConfigServer cc, int port) {
           return createMultiServer(cc, port,2);
    }

    public static RetrievalServer createMultiServer(ConfigServer cc, int port, int serverNumber, String storeName) {
           logger.info("Start MultiServer...");
            cc.setStoreName(storeName);
            cc.setIndexPath(cc.getIndexPath() + "testNetbeansMulti/" + port + "/");
            cc.setIndexCompressThreshold(0);
            cc.setMaxPercentageSimilarWord(0);
            System.out.println("StoreName="+storeName + " N="+cc.getNumberOfPatch() + " T="+cc.getNumberOfTestVector());
            RetrievalServer server = new RetrievalServer(cc, "testNetbeans", serverNumber, true);
            server.loadWithSocket(port);
            return server;
    }

    public static RetrievalServer createMultiServer(ConfigServer cc, int port, int serverNumber) {
           return createMultiServer(cc,port,serverNumber,"MEMORY");
    }
     
     public Storage createServer(String id,ConfigServer cc, GlobalDatabase database) throws Exception {
         cc.setIndexPath(cc.getIndexPath() + "testNetbeansSingle/");
         FileUtils.deleteAllFilesRecursively(new File(cc.getIndexPath()));
         Storage server = new Storage(id,cc,database);
         server.start();    
         return server;
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

    public static RetrievalClient createCentralServer(ConfigClient ccs, HashMap<Storage,Storage> servers) throws Exception {
//             logger.info("Start SuperCentralServer...");
//              MultiCentralServer centralServer = new MultiCentralServer(ccs, servers);
//                return centralServer;
        throw new Exception();
    }

    
    public static boolean containsPictures(ResultsSimilarities rs, Long id) {
        List<ResultSim> list = rs.getResults();
        for(ResultSim res : list) {
            if(res.getId()==id) return true;
        }
        return false;
    }   
    
    public static String getMethodName() {
        return getMethodName(0);
    }
    
    public static String getMethodName(final int depth)
    {
    final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

    //System. out.println(ste[ste.length-depth].getClassName()+"#"+ste[ste.length-depth].getMethodName());
    // return ste[ste.length - depth].getMethodName();  //Wrong, fails for depth = 0
    return ste[ste.length - 1 - depth].getMethodName(); //Thank you Tom Tresansky
    }    
    
 
}
