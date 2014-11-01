package retrieval.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import static org.junit.Assert.*;
import org.junit.*;
import retrieval.utils.TestUtils;
import retrieval.config.ConfigServer;
import retrieval.exception.CBIRException;
import retrieval.server.exception.NoException;
import retrieval.server.exception.PictureNotFoundException;
import retrieval.testvector.TestVectorListCentralServer;
import retrieval.testvector.generator.TestVectorReading;
import retrieval.utils.FileUtils;
import retrieval.utils.NetworkUtils;
import retrieval.utils.PictureAuthorization;

/**
 *
 * @author lrollus
 */
public abstract class ServerTestAbstract extends TestUtils {
 
    Storage server = null;
    ConfigServer config = null;    
    
    public ServerTestAbstract() {
    }

    /**
     * Test of start method, of class Server.
     */
    @Test
    public void testServerStart() throws Exception {
        System.out.println("testServerStart");
        assertEquals(false, NetworkUtils.isPortAvailable(config.getPortIndexPicture()));
        assertEquals(false, NetworkUtils.isPortAvailable(config.getPortInfo()));
        assertEquals(false, NetworkUtils.isPortAvailable(config.getPortSearch()));
    }

    /**
     * Test of stop method, of class Server.
     */
    @Test
    public void testServerStop() throws Exception {
        System.out.println("testServerStop");  
        server.stop();
        assertEquals(true, NetworkUtils.isPortAvailable(config.getPortIndexPicture()));
        assertEquals(true, NetworkUtils.isPortAvailable(config.getPortInfo()));
        assertEquals(true, NetworkUtils.isPortAvailable(config.getPortSearch()));         
    }

    /**
     * Test of getNumberOfItem method, of class Server.
     */
    @Test
    public void testServerGetNumberOfItem() throws Exception {
        System.out.println("testServerGetNumberOfItem");
        assertEquals(0, server.getNumberOfItem());  
        server.indexPicture(LOCALPICTURE1);
        assertEquals(1, server.getNumberOfItem()); 
        server.deletePicture(LOCALPICTURE1);
        assertEquals(0, server.getNumberOfItem()); 
    }

    /**
     * Test of indexPicture method, of class Server.
     */
    @Test
    public void testServerIndexPicture() throws Exception {
        System.out.println("testServerIndexPicture");
       server.indexPicture(LOCALPICTURE1);
       assertEquals(1, server.getNumberOfItem()); 
       assertEquals(true, server.isPictureInIndex(LOCALPICTURE1)); 
    }
    
     

    /**
     * Test of indexPictures method, of class Server.
     */
    @Test
    public void testServerIndexPictures() {
        System.out.println("testServerIndexPictures");
        List<String> lists = new ArrayList<String>();
        lists.add(LOCALPICTURE1);
        lists.add(LOCALPICTURE2);
        lists.add(BADPICTURE1);
        lists.add(LOCALPICTURE3);
        Map<String,CBIRException> result = server.indexPictures(lists, new PictureAuthorization());
        assertEquals(3,server.getNumberOfItem());
        assertEquals(true,server.isPictureInIndex(LOCALPICTURE1));
        assertEquals(true,server.isPictureInIndex(LOCALPICTURE2));
        assertEquals(false,server.isPictureInIndex(BADPICTURE1));
        assertEquals(true,server.isPictureInIndex(LOCALPICTURE3));
        
        assertEquals(NoException.CODE,result.get(LOCALPICTURE1).getCode());
        assertEquals(NoException.CODE,result.get(LOCALPICTURE2).getCode());
        assertEquals(PictureNotFoundException.CODE,result.get(BADPICTURE1).getCode());
        assertEquals(NoException.CODE,result.get(LOCALPICTURE3).getCode());
    }

    /**
     * Test of indexPicturePath method, of class Server.
     */
    @Test
    public void testServerIndexPicturePath() throws Exception {
        System.out.println("testServerIndexPicturePath");
        List<Integer> result = server.indexPicturePath(LOCALPICTUREPATH, new PictureAuthorization());
    }

    /**
     * Test of indexPictureFiles method, of class Server.
     */
    @Test
    public void testServerIndexPictureFiles() throws Exception {
        System.out.println("testServerIndexPictureFiles");
        List<String> lists = new ArrayList<String>();
        lists.add(LOCALPICTURE1);
        lists.add(LOCALPICTURE2);
        lists.add(LOCALPICTURE3);        
        List<Integer> result = server.indexPictureFiles(lists, new PictureAuthorization());
        assertEquals(3,result.size());
    }

    /**
     * Test of addToIndexQueue method, of class Server.
     */
    @Test
    public void testServerAddToIndexQueue() throws Exception {
        System.out.println("testServerAddToIndexQueue");
        server.addToIndexQueue(KEYSAUTHPICTURE1, new PictureAuthorization(PUBLIC,PRIVATE,HOST));
        server.addToIndexQueue(KEYSAUTHPICTURE2, new PictureAuthorization(PUBLIC,PRIVATE,HOST));
        server.addToIndexQueue(KEYSAUTHPICTURE3, new PictureAuthorization(PUBLIC,PRIVATE,HOST));
        server.addToIndexQueue(KEYSAUTHPICTURE4, new PictureAuthorization(PUBLIC,PRIVATE,HOST));
        //may fail if all pictures are indexed before going in this code (http access so hopefully)
        assertEquals(true,server.getIndexQueueSize()>0);
        assertEquals(false,server.isIndexQueueEmpty());
    }

    /**
     * Test of deletePictures method, of class Server.
     */
    @Test
    public void testServerDeletePictures() throws Exception {
        System.out.println("testServerDeletePictures");
        server.indexPicture(LOCALPICTURE1);
        server.deletePicture(LOCALPICTURE1);
        assertEquals(0, server.getNumberOfItem()); 
    }

    /**
     * Test of indexPurge method, of class Server.
     */
    @Test
    public void testServerIndexPurge() throws Exception {
        System.out.println("testServerIndexPurge");
        server.indexPicture(LOCALPICTURE1);
        server.deletePicture(LOCALPICTURE1);
        //check if picture is in this file
        assertEquals(1,server.getNumberOfPicturesToPurge());
        server.purgeIndex();
        assertEquals(0,server.getNumberOfPicturesToPurge());
    }
       
    /**
     * Test of isPictureCorrectlyRemovedFromIndex method, of class Server.
     */
    @Test
    public void testServerIsPictureCorrectlyRemovedFromIndex() throws Exception {
        System.out.println("testServerIsPictureCorrectlyRemovedFromIndex");
        int id = server.indexPicture(LOCALPICTURE1);
        server.deletePicture(LOCALPICTURE1);
        //check if picture is in this file
        assertEquals(false,server.isPictureCorrectlyRemovedFromIndex(id));
        server.purgeIndex();
        assertEquals(true,server.isPictureCorrectlyRemovedFromIndex(id));
    }

    /**
     * Test of deleteIndex method, of class Server.
     */
    @Test
    public void testServerDeleteIndex() throws Exception {
        System.out.println("testServerDeleteIndex");
        server.deleteIndex();
    }

    /**
     * Test of getIndexPath method, of class Server.
     */
    @Test
    public void testServerGetIndexPath() {
        System.out.println("testServerGetIndexPath");
        assertEquals(config.getIndexPath(), server.getIndexPath());
    }

    /**
     * Test of printIndex method, of class Server.
     */
    @Test
    public void testServerPrintIndex() {
        System.out.println("testServerPrintIndex");
        server.printIndex();
    }

    /**
     * Test of getAllPicturesMap method, of class Server.
     */
    @Test
    public void testServerGetAllPicturesMap() throws Exception{
        System.out.println("testServerGetAllPicturesMap");
        Integer id = server.indexPicture(LOCALPICTURE1);
        assertEquals(1,server.getAllPicturesMap().size());
        assertEquals(id,server.getAllPicturesMap().get(LOCALPICTURE1));
    }

    /**
     * Test of getInfo method, of class Server.
     */
    @Test
    public void testServerGetInfo() throws Exception{
        System.out.println("testServerGetInfo");
        Integer id = server.indexPicture(LOCALPICTURE1);
        HashMap<String,CBIRException> toCheck = new HashMap<String,CBIRException>();
        toCheck.put(LOCALPICTURE1, null);
        assertEquals(NoException.CODE,server.getInfo(toCheck).get(LOCALPICTURE1).getCode());
    }

    /**
     * Test of isPictureInIndex method, of class Server.
     */
    @Test
    public void testServerIsPictureInIndex() throws Exception{
        System.out.println("testServerIsPictureInIndex");
        Integer id = server.indexPicture(LOCALPICTURE1);
        assertEquals(true, server.isPictureInIndex(id));
        assertEquals(true, server.isPictureInIndex(LOCALPICTURE1));
        
        assertEquals(false, server.isPictureInIndex(99));
        assertEquals(false, server.isPictureInIndex(LOCALPICTURE2));        
    }


    /**
     * Test of getNBT method, of class Server.
     */
    @Test
    public void testServerSearch() throws Exception {
        System.out.println("testServerSearch");        
        server.indexPicture(LOCALPICTURE1);      
        TestVectorListCentralServer buildVW = TestVectorReading.readClient(config.getVectorPath());
        List<ConcurrentHashMap<String,Long>> vw = buildVW.generateVisualWordFromPicture(ImageIO.read(new File(LOCALPICTURE1)), LOCALPICTURE1, config.getNumberOfPatch(), config.getResizeMethod(), config.getSizeOfPatchResizeWidth(), config.getSizeOfPatchResizeHeight());
        List<ConcurrentHashMap<String,Long>> result = server.getNBT(vw);
    }
    
    /**
     * Test of getInfo method, of class Server.
     */
    @Test
    public void testServerGetList() throws Exception{
        System.out.println("testServerGetList");
        server.indexPicture(LOCALPICTURE1);
        System.out.println("error#"+server.getAllPictures());
        
        assertEquals(true,server.getAllPictures().contains(LOCALPICTURE1));
    }    
}
