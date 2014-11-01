/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server;

import org.junit.*;
import static org.junit.Assert.*;
import retrieval.utils.TestUtils;

/**
 *
 * @author lrollus
 */
public class ServerMainTest extends TestUtils{
    
    public ServerMainTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
        
    }

    /**
     * Test of main method, of class ServerMain.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = {"config/ConfigServer.prop",LOCALPICTUREPATH,"MEMORY","test"};
        StorageMain.main(args);
    }
}
