/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.client;

import static org.junit.Assert.fail;
import org.junit.*;
import retrieval.utils.TestUtils;
import retrieval.centralserver.CentralServer;
import retrieval.config.ConfigCentralServer;
import retrieval.config.ConfigServer;
import retrieval.indexer.RetrievalIndexerLocalStorage;
import retrieval.server.Storage;

/**
 *
 * @author lrollus
 */
public class ClientMainTest extends TestUtils{
 
    Storage server = null;
    CentralServer centralServer = null;
    ConfigServer config = null;
    
    public ClientMainTest() {
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
            config = new ConfigServer("config/ConfigServer.prop");
            config.setStoreName("MEMORY");
            System.out.println("server");
            server = createServer("0",config);  
            RetrievalIndexerLocalStorage.index(server,LOCALPICTURE1);
            RetrievalIndexerLocalStorage.index(server,LOCALPICTURE2);
            
            centralServer = new CentralServer(new ConfigCentralServer("config/ConfigCentralServer.prop"), "config/servers.xml",PORTCENTRALSERVER);
        } catch (Exception e) {
            fail();
        }        
    }
    
    @After
    public void tearDown() {
        try { server.stop();}catch(Exception e) {}
        try { centralServer.stop();}catch(Exception e) {}     
    }

    /**
     * Test of main method, of class IndexerMain.
     */
    @Test
    public void testMainClientHeavyNoAuth() throws Exception {
        System.out.println("testMainClientHeavyBasic");
        //heavy config path k servers publickey privatekey host
        String[] args = {"heavy","config/ConfigCentralServer.prop","config/servers.xml",LOCALPICTURE1+"","30","all"};
        ClientMain.main(args);     
        String[] args2 = {"heavy","config/ConfigCentralServer.prop","config/servers.xml",LOCALPICTURE1+"","30","0,1"};
        ClientMain.main(args2);        
    }
       
    @Test
    public void testMainClientHeavyBasicAuth() throws Exception {
        System.out.println("testMainClientHeavyBasicAuth");
        //heavy config path k servers publickey privatekey host
        String[] args = {"heavy","config/ConfigCentralServer.prop","config/servers.xml",BASICAUTHPICTURE1,"30","all",LOGIN,PASSWORD};
        ClientMain.main(args);      
        String[] args2 = {"heavy","config/ConfigCentralServer.prop","config/servers.xml",BASICAUTHPICTURE1,"30","0,1",LOGIN,PASSWORD};
        ClientMain.main(args2);        
    }
    
    @Test
    public void testMainClientHeavyKeysAuth() throws Exception {
        System.out.println("testMainClientHeavyKeysAuth");
        //heavy config path k servers publickey privatekey host
        String[] args = {"heavy","config/ConfigCentralServer.prop","config/servers.xml",KEYSAUTHPICTURE1,"30","all",PUBLIC,PRIVATE,HOST};
        ClientMain.main(args);       
        String[] args2 = {"heavy","config/ConfigCentralServer.prop","config/servers.xml",KEYSAUTHPICTURE1,"30","0,1",PUBLIC,PRIVATE,HOST};
        ClientMain.main(args2);        
    }  
    
    @Test
    public void testMainClientLightNoAuth() throws Exception {
        System.out.println("testMainClientLightNoAuth");
        //heavy config path k servers publickey privatekey host
        String[] args = {"light","localhost",PORTCENTRALSERVER+"",LOCALPICTURE1+"","30","all"};
        ClientMain.startLightClient(args);     
        String[] args2 = {"light","localhost",PORTCENTRALSERVER+"",LOCALPICTURE1+"","30","0,1"};
        ClientMain.startLightClient(args2);          
    } 
    
    @Test
    public void testMainClientLightBasicAuth() throws Exception {
        System.out.println("testMainClientHeavyBasicAuth");
        //heavy config path k servers publickey privatekey host
        String[] args = {"light","localhost",PORTCENTRALSERVER+"",BASICAUTHPICTURE1,"30","all",LOGIN,PASSWORD};
        ClientMain.startLightClient(args);      
        String[] args2 = {"light","localhost",PORTCENTRALSERVER+"",BASICAUTHPICTURE1,"30","0,1",LOGIN,PASSWORD};
        ClientMain.startLightClient(args2);         
    }
    
    @Test
    public void testMainClientLightKeysAuth() throws Exception {
        System.out.println("testMainClientHeavyKeysAuth");
        //heavy config path k servers publickey privatekey host
        String[] args = {"light","localhost",PORTCENTRALSERVER+"",KEYSAUTHPICTURE1,"30","all",PUBLIC,PRIVATE,HOST};
        ClientMain.startLightClient(args);       
        String[] args2 = {"light","localhost",PORTCENTRALSERVER+"",KEYSAUTHPICTURE1,"30","0,1",PUBLIC,PRIVATE,HOST};
        ClientMain.startLightClient(args2);         
    }      
}
