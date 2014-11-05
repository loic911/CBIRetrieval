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
import retrieval.utils.FileUtils;
import retrieval.utils.NetworkUtils;
import static retrieval.utils.TestUtils.LOCALPICTURE1;

/**
 *
 * @author lrollus
 */
public class MultiServerMemoryTest extends MultiServerAbstract{
    
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
            multiServer = createMultiServer(config,MULTISERVERPORT1,0,"MEMORY");      
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
