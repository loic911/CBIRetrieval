/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.centralserver;

import static org.junit.Assert.fail;
import org.junit.*;
import retrieval.utils.TestUtils;
import retrieval.client.ClientMain;

/**
 *
 * @author lrollus
 */
public class CentralServerMainTest {
    
    public CentralServerMainTest() {
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
     * Test of main method, of class CentralServerMain.
     */
    @Test
    public void testCentralServerMainTest() {
        System.out.println("testCentralServerMainTest");
        //heavy config path k servers publickey privatekey host
        String[] args = {"config/ConfigCentralServer.prop","config/servers.xml",TestUtils.MULTISERVERPORT1+"","test"};
        CentralServerMain.main(args);     
    }
}
