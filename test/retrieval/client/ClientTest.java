/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import retrieval.utils.TestUtils;
import retrieval.centralserver.CentralServer;
import retrieval.multicentralserver.ServerInformationSocket;
import retrieval.client.gui.ResultHtmlBuilder;
import retrieval.config.ConfigCentralServer;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.indexer.RetrievalIndexerLocalStorage;
import retrieval.multiserver.MultiServer;
import retrieval.server.Storage;
import retrieval.utils.PictureAuthorization;

/**
 *
 * @author lrollus
 */
public class ClientTest extends TestUtils {
 
   Storager server0 = null;
  Storageer server1 = null;
    ConfigServer config0 = null;  
    ConfigServer config1 = null;
    ConfigCentralServer configCentralServer = null; 
    CentralServer centralServerNetwork;
    MultiServer multiServer0;
    
        
    
    public ClientTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        enableLog();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
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
            
            RetrievalIndexerLocalStorage.indexSynchrone(server1, KEYSAUTHPICTURE1,PUBLIC,PRIVATE,HOST);
//            IndexerPictureToServer.indexSynchrone(server1, KEYSAUTHPICTURE2,PUBLIC,PRIVATE,HOST);           
//            IndexerPictureToServer.indexSynchrone(server1, KEYSAUTHPICTURE3,PUBLIC,PRIVATE,HOST);
//            IndexerPictureToServer.indexSynchrone(server1, KEYSAUTHPICTURE4,PUBLIC,PRIVATE,HOST);  
//            
            
            multiServer0 = createMultiServer(config0,PORT1,2);
            multiServer0.getServer("0").indexPicture(LOCALPICTURE1, new PictureAuthorization());
            multiServer0.getServer("0").indexPicture(LOCALPICTURE2, new PictureAuthorization());
            multiServer0.getServer("0").indexPicture(LOCALPICTURE3, new PictureAuthorization());
            multiServer0.getServer("0").indexPicture(LOCALPICTURE4, new PictureAuthorization());
            multiServer0.getServer("1").indexPicture(BASICAUTHPICTURE1, new PictureAuthorization(LOGIN,PASSWORD));
//            multiServer0.getServer("1").indexPicture(BASICAUTHPICTURE2, new PictureAuthorization(LOGIN,PASSWORD));
//            multiServer0.getServer("1").indexPicture(BASICAUTHPICTURE3, new PictureAuthorization(LOGIN,PASSWORD));
//            multiServer0.getServer("1").indexPicture(BASICAUTHPICTURE4, new PictureAuthorization(LOGIN,PASSWORD));
            multiServer0.getServer("1").indexPicture(KEYSAUTHPICTURE1, new PictureAuthorization(PUBLIC,PRIVATE,HOST));
//            multiServer0.getServer("1").indexPicture(KEYSAUTHPICTURE2, new PictureAuthorization(PUBLIC,PRIVATE,HOST));
//            multiServer0.getServer("1").indexPicture(KEYSAUTHPICTURE3, new PictureAuthorization(PUBLIC,PRIVATE,HOST));
//            multiServer0.getServer("1").indexPicture(KEYSAUTHPICTURE4, new PictureAuthorization(PUBLIC,PRIVATE,HOST));            
    }
    
    @After
    public void tearDown() {
         try { server0.stop();}catch(Exception e) {}
         try { server1.stop();}catch(Exception e) {}  
         try { centralServerNetwork.stop();}catch(Exception e) {} 
         try { multiServer0.stop();}catch(Exception e) {} 
    }

    /**
     * Test of search method, of class Client.
     */
    @Test
    public void testHeavyClientSearchNServers() throws Exception {
        System.out.println("testHeavyClientSearchNServers");            
        Map<StrinStoragever> serversMap = new HashMap<StrStoragerver>();
        serversMap.put("0", server0);
        serversMap.put("1", server1);         
        CentralServer centralServerLocal = new CentralServer(configCentralServer, serversMap);
        Client client = new Client(centralServerLocal);
        
        ResultsSimilarities rs = client.search(LOCALPICTURE1, 30);
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, LOCALPICTURE1));
        
        rs = client.search(BASICAUTHPICTURE1,30,LOGIN,PASSWORD);
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, BASICAUTHPICTURE1));        
        
        rs = client.search(KEYSAUTHPICTURE1,30,PUBLIC,PRIVATE,HOST);
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, KEYSAUTHPICTURE1)); 
    }
    
    @Test
    public void testHeavyClientSearchNServersFilterServer() throws Exception {
        System.out.println("testHeavyClientSearchNServersFilterServer");            
        Map<StrStorageerver> serversMap = new HashMap<SStorageServer>();
        serversMap.put("0", server0);
        serversMap.put("1", server1);         
        CentralServer centralServerLocal = new CentralServer(configCentralServer, serversMap);
        Client client = new Client(centralServerLocal);
        
        ResultsSimilarities rs = client.search(LOCALPICTURE1, 30, new String[]{"1"});
        assertEquals(false,containsPictures(rs, LOCALPICTURE1));
        
        rs = client.search(BASICAUTHPICTURE1,30,LOGIN,PASSWORD, new String[]{"1"});
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, BASICAUTHPICTURE1));        
        
        rs = client.search(KEYSAUTHPICTURE1,30,PUBLIC,PRIVATE,HOST, new String[]{"0"});
        assertEquals(false,containsPictures(rs, KEYSAUTHPICTURE1)); 
    }    
    
    @Test
    public void testLightClientSearchNServers() throws Exception {
        System.out.println("testLightClientSearchNServers");
        List<ServerInformationSocket> serverList = new ArrayList<ServerInformationSocket>();
        serverList.add(new ServerInformationSocket("localhost", config0.getPortSearch()));
        serverList.add(new ServerInformationSocket("localhost", config1.getPortSearch()));        
        centralServerNetwork = new CentralServer(configCentralServer,serverList,PORTCENTRALSERVER);  
        
        Client client = new Client("localhost", PORTCENTRALSERVER);
        
        ResultsSimilarities rs = client.search(LOCALPICTURE1, 30);
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, LOCALPICTURE1));
        
        rs = client.search(BASICAUTHPICTURE1,30,LOGIN,PASSWORD);
        printResult(rs);
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, BASICAUTHPICTURE1));        
        
        rs = client.search(KEYSAUTHPICTURE1,30,PUBLIC,PRIVATE,HOST);
        printResult(rs);
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, KEYSAUTHPICTURE1)); 
    }  
    
    @Test
    public void testLightClientSearchNServersFilterServer() throws Exception {
        System.out.println("testLightClientSearchNServersFilterServer");
        List<ServerInformationSocket> serverList = new ArrayList<ServerInformationSocket>();
        serverList.add(new ServerInformationSocket("localhost", config0.getPortSearch()));
        serverList.add(new ServerInformationSocket("localhost", config1.getPortSearch()));        
        centralServerNetwork = new CentralServer(configCentralServer,serverList,PORTCENTRALSERVER);  
        
        Client client = new Client("localhost", PORTCENTRALSERVER);
        
        ResultsSimilarities rs = client.search(LOCALPICTURE1, 30, new String[]{"1"});
        assertEquals(false,containsPictures(rs, LOCALPICTURE1));
        
        rs = client.search(BASICAUTHPICTURE1,30,LOGIN,PASSWORD, new String[]{"1"});
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, BASICAUTHPICTURE1));        
        
        rs = client.search(KEYSAUTHPICTURE1,30,PUBLIC,PRIVATE,HOST, new String[]{"0"});
        assertEquals(false,containsPictures(rs, KEYSAUTHPICTURE1)); 
    }    
    
    @Test
    public void testHeavyClientSearchMultiServer() throws Exception {
        System.out.println("testHeavyClientSearchMultiServer");            
       
        CentralServer centralServerLocal = new CentralServer(configCentralServer, multiServer0.getServerMap());
        Client client = new Client(centralServerLocal);
        
        ResultsSimilarities rs = client.search(LOCALPICTURE1, 30);
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, LOCALPICTURE1));
        
        rs = client.search(BASICAUTHPICTURE1,30,LOGIN,PASSWORD);
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, BASICAUTHPICTURE1));        
        
        rs = client.search(KEYSAUTHPICTURE1,30,PUBLIC,PRIVATE,HOST);
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, KEYSAUTHPICTURE1)); 
    }
    
    @Test
    public void testHeavyClientSearchMultiServerFilterServer() throws Exception {
        System.out.println("testHeavyClientSearchNServers");            
        CentralServer centralServerLocal = new CentralServer(configCentralServer, multiServer0.getServerMap());
        Client client = new Client(centralServerLocal);
        
        ResultsSimilarities rs = client.search(LOCALPICTURE1, 30, new String[]{"1"});
        assertEquals(false,containsPictures(rs, LOCALPICTURE1));
        
        rs = client.search(BASICAUTHPICTURE1,30,LOGIN,PASSWORD, new String[]{"1"});
        assertEquals(true, rs.getResults().size()>0);
        assertEquals(true,containsPictures(rs, BASICAUTHPICTURE1));        
        
        rs = client.search(KEYSAUTHPICTURE1,30,PUBLIC,PRIVATE,HOST, new String[]{"0"});
        assertEquals(false,containsPictures(rs, KEYSAUTHPICTURE1)); 
    } 
    
    @Test
    public void testBuildHTMLReport() throws Exception {
        System.out.println("testBuildHTMLReport");            
        CentralServer centralServerLocal = new CentralServer(configCentralServer, multiServer0.getServerMap());
        Client client = new Client(centralServerLocal);
        
        ResultsSimilarities rs = client.search(LOCALPICTURE1, 30);
        printResult(rs);
        ResultHtmlBuilder.write("test.html", rs.getResults(), LOCALPICTURE1);
               
    }
    
    
}
