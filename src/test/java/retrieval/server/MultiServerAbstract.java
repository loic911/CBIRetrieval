/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import retrieval.RedisInstance;
import retrieval.TestUtils;
import static retrieval.TestUtils.LOCALPICTURE1;
import static retrieval.TestUtils.LOCALPICTURE1MAP;
import static retrieval.TestUtils.LOCALPICTURE2;
import retrieval.config.ConfigServer;
import retrieval.server.globaldatabase.RedisDatabase;
import retrieval.storage.Storage;
import retrieval.utils.FileUtils;
import retrieval.utils.NetworkUtils;

/**
 *
 * @author lrollus
 */
public abstract class MultiServerAbstract extends TestUtils{
    
    protected RetrievalServer multiServer;
    protected String CONTAINER1 = "myContainer1";
    protected String CONTAINER2 = "myContainer2";
    protected ConfigServer config;

    /**
     * Test of createServer method, of class MultiServer.
     */
    @Test
    public void testCreateServer() throws Exception {
        System.out.println("createServer");
        String container = "TEST";
        multiServer.createStorage(container);
        assertEquals(3,multiServer.getStorageList().size());
        Storage server = multiServer.getStorage(container);
        assertEquals(server,multiServer.getStorageMap().get(container));
    }
    
    @Test(expected=Exception.class)
    public void testCreateServerAlreadyExist() throws Exception {
        System.out.println("testCreateServerAlreadyExist");
        String container = CONTAINER1;
        multiServer.createStorage(container);
    }    

    /**
     * Test of deleteServer method, of class MultiServer.
     */
    @Test
    public void testDeleteServer() throws Exception {
        System.out.println("deleteServer");
        multiServer.deleteStorage(CONTAINER1);
        assertEquals(1,multiServer.getStorageList().size());
        assertEquals(null,multiServer.getStorage(CONTAINER1));
        assertEquals(null,multiServer.getStorageMap().get(CONTAINER1));   
        assertEquals(0,multiServer.getSize().intValue());
    }
    
    @Test(expected=Exception.class)
    public void testDeleteServerAlreadyExist() throws Exception {
        System.out.println("deleteServer");
        multiServer.deleteStorage("not exist!");
    }    

    /**
     * Test of getNextServer method, of class MultiServer.
     */
    @Test
    public void testGetNextServer() {
        System.out.println("getNextServer");
        assertEquals(true,multiServer.getNextStorage()!=multiServer.getNextStorage());
    }

    /**
     * Test of getServer method, of class MultiServer.
     */
    @Test
    public void testGetServer() throws Exception{
        System.out.println("getServer");
        assertEquals(true,multiServer.getStorage(CONTAINER1)!=null);
        assertEquals(true,multiServer.getStorage("not exist")==null);
    }

    /**
     * Test of getServerMap method, of class MultiServer.
     */
    @Test
    public void testGetServerMap() {
        System.out.println("getServerMap");
        assertEquals(true,multiServer.getStorageMap().get(CONTAINER1)!=null);
        assertEquals(true,multiServer.getStorageMap().get("not exist")==null);
    }

    /**
     * Test of getServerList method, of class MultiServer.
     */
    @Test
    public void testGetServerList() {
        assertEquals(2,multiServer.getStorageList().size());
    }

    /**
     * Test of getServers method, of class MultiServer.
     */
    @Test
    public void testGetServers() {
        System.out.println("getServers");
        List<String> keys = new ArrayList<String>();
        assertEquals(0,multiServer.getStorageMapByName(keys).size());
        keys.add(CONTAINER1);
        assertEquals(1,multiServer.getStorageMapByName(keys).size()); 
        keys.add(CONTAINER2);
        assertEquals(2,multiServer.getStorageMapByName(keys).size());  
        keys.clear();
        keys.add("not exist!");
        assertEquals(0,multiServer.getStorageMapByName(keys).size());  
    }

    /**
     * Test of getServersId method, of class MultiServer.
     */
    @Test
    public void testGetServersId() {
        System.out.println("getServersId");
        List<String> result = multiServer.getStoragesName();
        assertEquals(2, result.size());
        for(String id : result) {
            assertEquals(true, (id.equals(CONTAINER1) || id.equals(CONTAINER2)));
        }
    }

    /**
     * Test of closeSocket method, of class MultiServer.
     */
    @Test
    public void testCloseSocket() {
        System.out.println("closeSocket");
        multiServer.closeSocket();
        assertEquals(true, NetworkUtils.isPortAvailable(MULTISERVERPORT1));
    }

    /**
     * Test of getSize method, of class MultiServer.
     */
    @Test
    public void testGetSize() {
        assertEquals(2, multiServer.getSize().intValue());
    }

    /**
     * Test of getServersSize method, of class MultiServer.
     */
    @Test
    public void testGetServersSize() {
        assertEquals(2, multiServer.getStoragesSize().get(CONTAINER1).intValue());
        assertEquals(0, multiServer.getStoragesSize().get(CONTAINER2).intValue());
    }

    /**
     * Test of indexPictureAsynchrone method, of class MultiServer.
     */
    @Test
    public void testIndexPictureAsynchrone() throws Exception {
        System.out.println("indexPictureAsynchrone");
        multiServer.indexPictureSynchrone(FileUtils.readPicture(LOCALPICTURE2),3l,null);
        assertEquals(3, multiServer.getSize().intValue());        
    }

    /**
     * Test of indexPictureSynchrone method, of class MultiServer.
     */
    @Test
    public void testIndexPictureSynchrone() throws Exception {
        System.out.println("indexPictureSynchrone");
        multiServer.indexPictureSynchrone(FileUtils.readPicture(LOCALPICTURE2),3l,null);  
    }


    /**
     * Test of indexDirectory method, of class MultiServer.
     */
//    @Test
//    public void testIndexDirectory() throws Exception{
//        System.out.println("indexDirectory");
//        List<Long> toDelete = new ArrayList<Long>();
//        toDelete.add(1l);
//        toDelete.add(2l);
//        multiServer.delete(toDelete);
//        
//        File allPictures = new File(LOCALPICTUREPATH);
//        int numberOfPictures = allPictures.listFiles().length;
//        
//        multiServer.indexDirectory(LOCALPICTUREPATH);
//        
//        multiServer.getServersSize().get(CONTAINER1).intValue();
//         multiServer.getServersSize().get(CONTAINER2).intValue();
//    }

    /**
     * Test of delete method, of class MultiServer.
     */
    @Test
    public void testDelete() throws Exception {
        System.out.println("delete");
        List<Long> toDelete = new ArrayList<Long>();
        toDelete.add(1l);
        toDelete.add(2l);
        multiServer.delete(toDelete);
        assertEquals(0, multiServer.getSize().intValue());
    }

    /**
     * Test of purge method, of class MultiServer.
     */
    @Test
    public void testPurge() throws Exception {
        System.out.println("purge");
        List<Long> toDelete = new ArrayList<Long>();
        toDelete.add(1l);
        toDelete.add(2l);
        multiServer.delete(toDelete);
        assertEquals(toDelete.size(),multiServer.getStorage(CONTAINER1).getNumberOfPicturesToPurge());
        multiServer.purge();
        assertEquals(0,multiServer.getStorage(CONTAINER1).getNumberOfPicturesToPurge());
    }

    /**
     * Test of printStat method, of class MultiServer.
     */
    @Test
    public void testPrintStat() {
        System.out.println("printStat");
        multiServer.printStat();
    }

    /**
     * Test of isIndexQueueEmpty method, of class MultiServer.
     */
    @Test
    public void testIsIndexQueueEmpty() {
        System.out.println("isIndexQueueEmpty");
        assertEquals(true, multiServer.isIndexQueueEmpty());
    }

    /**
     * Test of getIndexQueueSize method, of class MultiServer.
     */
    @Test
    public void testGetIndexQueueSize() {
        System.out.println("getIndexQueueSize");
        assertEquals(0, multiServer.getIndexQueueSize());
    }

    /**
     * Test of getInfos method, of class MultiServer.
     */
    @Test
    public void testGetInfos() throws Exception {
        System.out.println("getInfos");
        assertEquals(2, multiServer.getInfos(CONTAINER1).size());
        assertEquals(0, multiServer.getInfos(CONTAINER2).size());
    }
    
    
    @Test
    public void testReadAlreadyExistImageDataGlobal() throws Exception {
        System.out.println("testReadAlreadyExistData");

        //redis data because the retrievalserver is local to the method
        Jedis jedis = new Jedis("localhost",RedisInstance.PORT, 20000);
        jedis.flushAll();
        
        System.out.println("StoreName="+config.getStoreName());
        RetrievalServer server = new RetrievalServer(config, "testNetbeans", 0, true);
        server.createStorage("serverName");
        Long id1 = server.getStorage("serverName").indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
        Long id2 = server.getStorage("serverName").indexPicture(FileUtils.readPicture(LOCALPICTURE2),null,null);
        server.stop();
        server = null;
        RetrievalServer server2 = new RetrievalServer(config, "testNetbeans",false);
        assert server2.getStorageList().size()==1;
        assert server2.getStorageMap().keySet().iterator().next().equals("serverName");        
        assert server2.getStorageList().get(0).isPictureInIndex(id1);       
        assert server2.getStorageList().get(0).isPictureInIndex(id2);
        assertNotNull(server2.getStorageList().get(0).getProperties(id1));
        assertEquals(LOCALPICTURE1MAP.size(),server2.getStorageList().get(0).getProperties(id1).size());
        assertNotNull(server2.getStorageList().get(0).getProperties(id2));
        assertEquals(0,server2.getStorageList().get(0).getProperties(id2).size());
    }   
    
     @Test
    public void testReadAlreadyExistDeleteStorageDataGlobal() throws Exception {
        System.out.println("testReadAlreadyExistData");
         //redis data because the retrievalserver is local to the method
         Jedis jedis = new Jedis("localhost",RedisInstance.PORT, 20000);
         jedis.flushAll();
        System.out.println("StoreName="+config.getStoreName());
        RetrievalServer server = new RetrievalServer(config, "testNetbeans", 0, true);
        server.createStorage("serverName");
        server.createStorage("will_be_deleted");
        Long id1 = server.getStorage("serverName").indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
        Long id2 = server.getStorage("serverName").indexPicture(FileUtils.readPicture(LOCALPICTURE2),null,null);
        Long id3 = server.getStorage("will_be_deleted").indexPicture(FileUtils.readPicture(LOCALPICTURE2),null,null);
        
        server.deleteStorage("will_be_deleted");
        
        server.stop();
        server = null;
        System.out.println("### READ OLD SERVER DATA");
        RetrievalServer server2 = new RetrievalServer(config, "testNetbeans",false);
        assert server2.getStorageList().size()==1;
        assert server2.getStorageMap().keySet().iterator().next().equals("serverName");        
        assert server2.getStorageList().get(0).isPictureInIndex(id1);       
        assert server2.getStorageList().get(0).isPictureInIndex(id2);
        assertNotNull(server2.getStorageList().get(0).getProperties(id1));
        assertEquals(LOCALPICTURE1MAP.size(),server2.getStorageList().get(0).getProperties(id1).size());
        assertNotNull(server2.getStorageList().get(0).getProperties(id2));
        assertEquals(0,server2.getStorageList().get(0).getProperties(id2).size());
    }    
}
