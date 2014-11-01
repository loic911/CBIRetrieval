/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.multiserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import retrieval.config.ConfigServer;
import retrieval.dist.RequestPictureVisualWord;
import retrieval.server.Storage;
import retrieval.utils.PictureAuthorization;
import retrieval.utils.TestUtils;

/**
 *
 * @author lrollus
 */
public class MultiServerTest extends TestUtils{

    
    public MultiServerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        enableLog();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    

    /**
     * Test of createNewContainers method, of class MultiServer.
     */
    @Test
    public void testCreateNewContainers() {
        System.out.println("createNewContainers");
        String[] result = MultiServer.createNewContainers(0);
        assert result.length==0;
        result = MultiServer.createNewContainers(2);
        assert result.length==2;
        assert result[0].equals("0");
        assert result[1].equals("1");
    }
    /**
     * Test of writeContainersOnDisk method, of class MultiServer.
     */
    @Test
    public void testWriteContainersOnDisk() throws Exception {
        System.out.println("writeContainersOnDisk");
        ConfigServer config = ConfigServer.getConfigServerForTest();
        Map<String, Storage> servers = new HashMap<String,Storage>();
        servers.put("test1",null);
        servers.put("test2",null); 
        MultiServer.writeContainersOnDisk(config, servers);
        
        
        String[] containers = MultiServer.readContainersFromDisk(config);
        
        assert containers.length==2;
        assert containers[0].equals("test1");
        assert containers[1].equals("test2");
        
    }
    
    @Test
    public void testReadAlreadyExistDataGlobal() throws Exception {
        System.out.println("testReadAlreadyExistData");
        
        ConfigServer config = ConfigServer.getConfigServerForTest();
        config.setStoreName("KYOTOSINGLEFILE");
        config.setIndexPath(config.getIndexPath() + "testNetbeansMulti/testReadAlreadyExistData/");
        config.setIndexCompressThreshold(0);
        config.setMaxPercentageSimilarWord(0);
        
        System.out.println("StoreName="+config.getStoreName());
        MultiServer server = new MultiServer(config, "testNetbeans", 0, true);
        server.createServer("serverName");
        server.getServer("serverName").indexPicture(TestUtils.LOCALPICTURE1);
        
        server.stop();
        server = null;
        System.out.println("### READ OLD SERVER DATA");
        MultiServer server2 = new MultiServer(config, "testNetbeans",false);
        assert server2.getServerList().size()==1;
        assert server2.getServerMap().keySet().iterator().next().equals("serverName");        
        assert server2.getServerList().get(0).isPictureInIndex(TestUtils.LOCALPICTURE1);       
    }
    
    @Test
    public void testReadAlreadyExistDataLocal() throws Exception {
        System.out.println("testReadAlreadyExistDataLocal");
        
        ConfigServer config = ConfigServer.getConfigServerForTest();
        config.setStoreName("KYOTOMULTIPLEFILE");
        config.setIndexPath(config.getIndexPath() + "testNetbeansMulti/testReadAlreadyExistData/");
        config.setIndexCompressThreshold(0);
        config.setMaxPercentageSimilarWord(0);
        
        System.out.println("StoreName="+config.getStoreName());
        MultiServer server = new MultiServer(config, "testNetbeans", 0, true);
        server.createServer("serverName");
        server.getServer("serverName").indexPicture(TestUtils.LOCALPICTURE1);
        
        server.stop();
        server = null;
        System.out.println("### READ OLD SERVER DATA LOCAL");
        MultiServer server2 = new MultiServer(config, "testNetbeans",false);
        assert server2.getServerList().size()==1;
        assert server2.getServerMap().keySet().iterator().next().equals("serverName");        
        assert server2.getServerList().get(0).isPictureInIndex(TestUtils.LOCALPICTURE1);       
    }    
}
