/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import retrieval.config.ConfigServer;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class MultiServerKyotoTest extends MultiServerAbstract{
    
    RetrievalServer multiServer2;
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
            config = new ConfigServer("testdata/ConfigServer.prop");
            config.setStoreName("KYOTOSINGLEFILE");
            FileUtils.deleteAllSubFilesRecursively(new File(config.getIndexPath()));
            System.out.println("server");
            
            multiServer = createMultiServer(config,MULTISERVERPORT1,0,"KYOTOSINGLEFILE");  
            
            multiServer.createServer(CONTAINER1);
            multiServer.createServer(CONTAINER2);
            
            multiServer.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE1),1l,LOCALPICTURE1MAP);
            multiServer.getServer(CONTAINER1).indexPicture(FileUtils.readPicture(LOCALPICTURE2),2l,null);
            /*
             * multiServer1
             * container 1 = LOCALPICTURE1, LOCALPICTURE2
             */
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }           
    }
    
    @After
    public void tearDown() {
        try{multiServer.stop();}catch(Exception e) {}
    }
}
