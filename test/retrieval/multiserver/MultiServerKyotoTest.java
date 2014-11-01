/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.multiserver;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.*;
import retrieval.config.ConfigServer;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class MultiServerKyotoTest extends MultiServerAbstract{
    
    MultiServer multiServer2;
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
            config.setStoreName("KYOTOSINGLEFILE");
            FileUtils.deleteAllSubFilesRecursively(new File(config.getIndexPath()));
            System.out.println("server");
            
            multiServer = createMultiServer(config,MULTISERVERPORT1,0,"KYOTOSINGLEFILE");  
            
            multiServer.createServer(CONTAINER1);
            multiServer.createServer(CONTAINER2);
            
            multiServer.getServer(CONTAINER1).indexPicture(LOCALPICTURE1);
            multiServer.getServer(CONTAINER1).indexPicture(LOCALPICTURE2);
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