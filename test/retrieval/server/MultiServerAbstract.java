/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.*;
import retrieval.utils.TestUtils;
import retrieval.config.ConfigServer;
import retrieval.storage.Storage;
import retrieval.utils.FileUtils;
import retrieval.utils.NetworkUtils;

/**
 *
 * @author lrollus
 */
public class MultiServerAbstract extends TestUtils{
    
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
        multiServer.createServer(container);
        assertEquals(3,multiServer.getServerList().size());
        Storage server = multiServer.getServer(container);
        assertEquals(server,multiServer.getServerMap().get(container));
    }
    
    @Test(expected=Exception.class)
    public void testCreateServerAlreadyExist() throws Exception {
        System.out.println("testCreateServerAlreadyExist");
        String container = CONTAINER1;
        multiServer.createServer(container);
    }    

    /**
     * Test of deleteServer method, of class MultiServer.
     */
    @Test
    public void testDeleteServer() throws Exception {
        System.out.println("deleteServer");
        multiServer.deleteServer(CONTAINER1);
        assertEquals(1,multiServer.getServerList().size());
        assertEquals(null,multiServer.getServer(CONTAINER1));
        assertEquals(null,multiServer.getServerMap().get(CONTAINER1));   
        assertEquals(0,multiServer.getSize().intValue());
    }
    
    @Test(expected=Exception.class)
    public void testDeleteServerAlreadyExist() throws Exception {
        System.out.println("deleteServer");
        multiServer.deleteServer("not exist!");
    }    

    /**
     * Test of getNextServer method, of class MultiServer.
     */
    @Test
    public void testGetNextServer() {
        System.out.println("getNextServer");
        assertEquals(true,multiServer.getNextServer()!=multiServer.getNextServer());
    }

    /**
     * Test of getServer method, of class MultiServer.
     */
    @Test
    public void testGetServer() {
        System.out.println("getServer");
        assertEquals(true,multiServer.getServer(CONTAINER1)!=null);
        assertEquals(true,multiServer.getServer("not exist")==null);
    }

    /**
     * Test of getServerMap method, of class MultiServer.
     */
    @Test
    public void testGetServerMap() {
        System.out.println("getServerMap");
        assertEquals(true,multiServer.getServerMap().get(CONTAINER1)!=null);
        assertEquals(true,multiServer.getServerMap().get("not exist")==null);
    }

    /**
     * Test of getServerList method, of class MultiServer.
     */
    @Test
    public void testGetServerList() {
        assertEquals(2,multiServer.getServerList().size());
    }

    /**
     * Test of getServers method, of class MultiServer.
     */
    @Test
    public void testGetServers() {
        System.out.println("getServers");
        List<String> keys = new ArrayList<String>();
        assertEquals(0,multiServer.getServers(keys).size());
        keys.add(CONTAINER1);
        assertEquals(1,multiServer.getServers(keys).size()); 
        keys.add(CONTAINER2);
        assertEquals(2,multiServer.getServers(keys).size());  
        keys.clear();
        keys.add("not exist!");
        assertEquals(0,multiServer.getServers(keys).size());  
    }

    /**
     * Test of getServersId method, of class MultiServer.
     */
    @Test
    public void testGetServersId() {
        System.out.println("getServersId");
        List<String> result = multiServer.getServersId();
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
        assertEquals(2, multiServer.getServersSize().get(CONTAINER1).intValue());
        assertEquals(0, multiServer.getServersSize().get(CONTAINER2).intValue());
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
     * Test of indexPictureAsynchrone method, of class MultiServer.
     */
    @Test
    public void testIndexPictureAsynchroneServer() throws Exception {
        System.out.println("indexPictureAsynchroneServer");
        multiServer.indexPictureAsynchrone(FileUtils.readPicture(LOCALPICTURE3),3l,null,CONTAINER1);    
    }

    /**
     * Test of indexPictureSynchrone method, of class MultiServer.
     */
    @Test
    public void testIndexPictureSynchroneServer() throws Exception {
        System.out.println("indexPictureSynchroneServer");
        multiServer.indexPictureSynchrone(FileUtils.readPicture(LOCALPICTURE3),3l,null,CONTAINER1);
        assertEquals(3, multiServer.getServer(CONTAINER1).getNumberOfItem());  
        assertEquals(3, multiServer.getSize().intValue());  
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
        assertEquals(toDelete.size(),multiServer.getServer(CONTAINER1).getNumberOfPicturesToPurge());
        multiServer.purge();
        assertEquals(0,multiServer.getServer(CONTAINER1).getNumberOfPicturesToPurge());
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
    public void testGetInfos() {
        System.out.println("getInfos");
        assertEquals(2, multiServer.getInfos(CONTAINER1).size());
        assertEquals(0, multiServer.getInfos(CONTAINER2).size());
    }
}
