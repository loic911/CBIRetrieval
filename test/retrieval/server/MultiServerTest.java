/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server;

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
import retrieval.storage.Storage;
import retrieval.utils.FileUtils;
import retrieval.utils.TestUtils;
import static retrieval.utils.TestUtils.LOCALPICTURE1;

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
        String[] result = RetrievalServer.createNewContainers(0);
        assert result.length==0;
        result = RetrievalServer.createNewContainers(2);
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
        RetrievalServer.writeContainersOnDisk(config, servers);
        
        
        String[] containers = RetrievalServer.readContainersFromDisk(config);
        
        assert containers.length==2;
        assert containers[0].equals("test1") || containers[0].equals("test2");
        assert containers[1].equals("test2") || containers[1].equals("test1");
        
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
        RetrievalServer server = new RetrievalServer(config, "testNetbeans", 0, true);
        server.createServer("serverName");
        Long id1 = server.getServer("serverName").indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
        Long id2 = server.getServer("serverName").indexPicture(FileUtils.readPicture(LOCALPICTURE2),null,null);
        server.stop();
        server = null;
        System.out.println("### READ OLD SERVER DATA");
        RetrievalServer server2 = new RetrievalServer(config, "testNetbeans",false);
        assert server2.getServerList().size()==1;
        assert server2.getServerMap().keySet().iterator().next().equals("serverName");        
        assert server2.getServerList().get(0).isPictureInIndex(id1);       
        assert server2.getServerList().get(0).isPictureInIndex(id2);
        assertNotNull(server2.getServerList().get(0).getProperties(id1));
        assertEquals(LOCALPICTURE1MAP.size(),server2.getServerList().get(0).getProperties(id1).size());
        assertNotNull(server2.getServerList().get(0).getProperties(id2));
        assertEquals(0,server2.getServerList().get(0).getProperties(id2).size());
    }
       
}
