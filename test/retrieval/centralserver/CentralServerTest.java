/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.centralserver;

import retrieval.multicentralserver.ServerInformation;
import retrieval.multicentralserver.ServerInformationSocket;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;
import retrieval.utils.TestUtils;
import retrieval.client.Client;
import retrieval.config.ConfigCentralServer;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.indexer.RetrievalIndexerLocalStorage;
import retrieval.server.Storage;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class CentralServerTest extends TestUtils {
    
    Storage server0 = null;
    Storage server1 = null;
    ConfigServer config0 = null;  
    ConfigServer config1 = null;
    ConfigCentralServer configCentralServer = null;    
    
    public CentralServerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        enableLog();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        try {
            configCentralServer = new ConfigCentralServer("config/ConfigCentralServer.prop");
            config0 = new ConfigServer("config/ConfigServer.prop");
            config0.setStoreName("MEMORY");
            System.out.println("server");
            server0 = createServer("0",config0);  
            //index 4 pictures on this server        
            RetrievalIndexerLocalStorage.indexSynchrone(server0, LOCALPICTURE1);
            RetrievalIndexerLocalStorage.indexSynchrone(server0, LOCALPICTURE2);
            RetrievalIndexerLocalStorage.indexSynchrone(server0, LOCALPICTURE3);
            RetrievalIndexerLocalStorage.indexSynchrone(server0, LOCALPICTURE4);  
            
            config1 = new ConfigServer("config/ConfigServer.prop");
            config1.setStoreName("MEMORY");
            config1.setPortIndexPicture(config1.getPortIndexPicture() + 11);
            config1.setPortInfo(config1.getPortInfo() + 11);
            config1.setPortSearch(config1.getPortSearch() + 11);
            server1 = createServer("1",config1);  
            //index 4 pictures on this server        
            RetrievalIndexerLocalStorage.indexSynchrone(server1, BASICAUTHPICTURE1,LOGIN,PASSWORD);
//            IndexerPictureToServer.indexSynchrone(server1, BASICAUTHPICTURE2,LOGIN,PASSWORD);
//            IndexerPictureToServer.indexSynchrone(server1, BASICAUTHPICTURE3,LOGIN,PASSWORD);
//            IndexerPictureToServer.indexSynchrone(server1, BASICAUTHPICTURE4,LOGIN,PASSWORD);               
        } catch (Exception e) {
            System.out.println("ERROR IN INIT TEST:"+e);
            e.printStackTrace();
            fail();
        }          
    }
    
    @After
    public void tearDown() {
         try { server0.stop();}catch(Exception e) {}
         try { server1.stop();}catch(Exception e) {}
    }

    /**
     * Test of search method, of class CentralServer.
     */
    @Test
    public void testCentralServer1ServerObject() throws Exception {
        System.out.println("testCentralServer1ServerObject");
        //init centralserver
        CentralServer centralServer = new CentralServer(configCentralServer, server0);
        assertEquals(1,centralServer.getNumberOfServer());
        
        //search picture 1 and check it in result
        ResultsSimilarities rs = centralServer.search(ImageIO.read(new File(LOCALPICTURE1)), 30);
        assertEquals(true,(rs.getResults().size()>=1));
        assertEquals(true,containsPictures(rs,LOCALPICTURE1));
    }
    
    @Test
    public void testCentralServerNServerObject() throws Exception {
        System.out.println("testCentralServerNServerObject");
        Map<String, Storage> serversMap = new HashMap<String,Storage>();
        serversMap.put("0", server0);
        serversMap.put("1", server1);
        //init centralserver
        CentralServer centralServer = new CentralServer(configCentralServer, serversMap);
        assertEquals(2,centralServer.getNumberOfServer());
        
        //search on all picture from server 1 and check it in result
        ResultsSimilarities rs = centralServer.search(ImageIO.read(new File(LOCALPICTURE1)), 30);
        assertEquals(true,(rs.getResults().size()>=1));
        assertEquals(true,containsPictures(rs,LOCALPICTURE1));
        
        //search on all picture from server 2 and check it in result
        rs = centralServer.search(FileUtils.readBufferedImageFromURLWithBasicAuth(BASICAUTHPICTURE1, LOGIN, PASSWORD), 30);
        assertEquals(true,(rs.getResults().size()>=1));
        assertEquals(true,containsPictures(rs,BASICAUTHPICTURE1));  
        
        //search on server 1 only with a image from server 2 (image shouldn't be in result!)
        rs = centralServer.search(FileUtils.readBufferedImageFromURLWithBasicAuth(BASICAUTHPICTURE1, LOGIN, PASSWORD), 30,new String[]{"0"});
        assertEquals(false,containsPictures(rs,BASICAUTHPICTURE1));        
        
        //search on server 2 only with a image from server 1 (image shouldn't be in result!)
        rs = centralServer.search(ImageIO.read(new File(LOCALPICTURE1)), 30,new String[]{"1"});
        assertEquals(false,containsPictures(rs,LOCALPICTURE1));        
    }    
    
    @Test
    public void testCentralServerNServerXMLSocket() throws Exception {
        System.out.println("testCentralServerNServerXMLSocket");
        List<ServerInformationSocket> serverList = new ArrayList<ServerInformationSocket>();
        serverList.add(new ServerInformationSocket("localhost", config0.getPortSearch()));
        serverList.add(new ServerInformationSocket("localhost", config1.getPortSearch()));
        
        CentralServer centralServer = new CentralServer(configCentralServer, serverList);
        assertEquals(2,centralServer.getNumberOfServer());
        
        //search on all picture from server 1 and check it in result
        ResultsSimilarities rs = centralServer.search(ImageIO.read(new File(LOCALPICTURE1)), 30);
        assertEquals(true,(rs.getResults().size()>=1));
        assertEquals(true,containsPictures(rs,LOCALPICTURE1));
        
        //search on all picture from server 2 and check it in result
        rs = centralServer.search(FileUtils.readBufferedImageFromURLWithBasicAuth(BASICAUTHPICTURE1, LOGIN, PASSWORD), 30);
        printResult(rs);
        assertEquals(true,(rs.getResults().size()>=1));
        assertEquals(true,containsPictures(rs,BASICAUTHPICTURE1));  
        
        //search on server 1 only with a image from server 2 (image shouldn't be in result!)
        rs = centralServer.search(FileUtils.readBufferedImageFromURLWithBasicAuth(BASICAUTHPICTURE1, LOGIN, PASSWORD), 30,new String[]{"0"});
        assertEquals(false,containsPictures(rs,BASICAUTHPICTURE1));        
        
        //search on server 2 only with a image from server 1 (image shouldn't be in result!)
        rs = centralServer.search(ImageIO.read(new File(LOCALPICTURE1)), 30,new String[]{"1"});
        assertEquals(false,containsPictures(rs,LOCALPICTURE1));  
        
        //first server will be crash!
        try { server0.stop();}catch(Exception e) {}
        
        rs = centralServer.search(ImageIO.read(new File(LOCALPICTURE1)), 30);
        assert((ServerInformation.CLOSE==rs.getServersSocket().get(0).getState() || ServerInformation.LOST==rs.getServersSocket().get(0).getState()));
        assertEquals(ServerInformation.NOERROR,rs.getServersSocket().get(1).getState());
        
        rs = centralServer.search(ImageIO.read(new File(LOCALPICTURE1)), 30,new String[]{"0"});
        assertEquals(0,rs.getResults().size());  
    }
    
    
    @Test
    public void testCentralServerNServerXMLSocketClientXMLSocket() throws Exception {
        System.out.println("testCentralServerNServerXMLSocket");
        List<ServerInformationSocket> serverList = new ArrayList<ServerInformationSocket>();
        serverList.add(new ServerInformationSocket("localhost", config0.getPortSearch()));
        serverList.add(new ServerInformationSocket("localhost", config1.getPortSearch()));

        //TODO: plug a client and open central server thread
//  
//        CentralServer centralServer = new CentralServer(configCentralServer, serverList);
//        assertEquals(2,centralServer.getNumberOfServer());
//        
//        //search on all picture from server 1 and check it in result
//        ResultsSimilarities rs = centralServer.search(ImageIO.read(new File(LOCALPICTURE1)), 30);
//        assertEquals(true,(rs.getResults().size()>=1));
//        assertEquals(true,containsPictures(rs,LOCALPICTURE1));
    }    

}
