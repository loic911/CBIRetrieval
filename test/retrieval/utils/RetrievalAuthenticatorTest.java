/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import retrieval.utils.RetrievalAuthenticator;
import static org.junit.Assert.*;

/**
 *
 * @author lrollus
 */
public class RetrievalAuthenticatorTest extends TestUtils{

    public RetrievalAuthenticatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        enableLog();
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
     * Test of getPasswordAuthentication method, of class RetrievalAuthenticator.
     
    @Test
    public void testGetPasswordAuthentication() {
        System.out.println("getPasswordAuthentication");
        RetrievalAuthenticator instance = new RetrievalAuthenticator("login","pass");
        PasswordAuthentication expResult = instance.getPasswordAuthentication();
        assertEquals("login", expResult.getUserName());
    }

    @Test
    public void testAccessCytomineAnnotationWithKey() throws Exception{
        try {
            System.out.println("getPasswordAuthentication");
            String publickey = "29f51819-3dc6-468c-8aa7-9c81b9bc236b";
            String privatekey = "db214699-0384-498c-823f-801654238a67";
            BufferedImage img = FileUtils.readBufferedImageFromURLWithKeysAuth("http://localhost:8080/api/userannotation/92842375/crop.jpg", publickey, privatekey,"http://localhost:8080");
            assert img !=null;
        }  catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

    }

    @Test
    public void testAccessCytomineAnnotationWithKeyComplexURL() throws Exception{
        try {
            System.out.println("getPasswordAuthentication");
            String publickey = "29f51819-3dc6-468c-8aa7-9c81b9bc236b";
            String privatekey = "db214699-0384-498c-823f-801654238a67";
            BufferedImage img = FileUtils.readBufferedImageFromURLWithKeysAuth("http://localhost:8080/api/userannotation/92842375/crop.jpg?max_size=256", publickey, privatekey,"http://localhost:8080");
            assert img !=null;
        }  catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

    }*/
}