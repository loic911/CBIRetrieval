/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.dist;

import org.junit.*;
import retrieval.exception.CBIRException;
import retrieval.storage.exception.AlreadyIndexedException;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author lrollus
 */
public class MessageErrorTest {
    
    public MessageErrorTest() {
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
     * Test of toXML method, of class MessageError.
     */
    @Test
    public void testMessageErrorBuildFromException() throws Exception {
        System.out.println("testMessageErrorBuildFromException");
        CBIRException exception = new AlreadyIndexedException("Picture already indexed test!");
        MessageError messageError = new MessageError(exception);       
        MessageError check = new MessageError(messageError.toXML());
        assertEquals(MessageError.getException(check.toXML()).getCode(),exception.getCode());
        assertEquals(MessageError.getException(check.toXML()).getMessage(),exception.getMessage());
        assertEquals(true,MessageError.isErrorMessage(check.toXML()));
    }
    
    @Test
    public void testMessageErrorBuildFromExceptionWithNullMsg() throws Exception {
        System.out.println("testMessageErrorBuildFromException");
        CBIRException exception = new AlreadyIndexedException(null);
        MessageError messageError = new MessageError(exception);       
        MessageError check = new MessageError(messageError.toXML());
        assertEquals(MessageError.getException(check.toXML()).getCode(),exception.getCode());
    }    
    
    @Test
    public void testMessageErrorBuildFromParams() throws Exception {
        System.out.println("testMessageErrorBuildFromParams");
        MessageError messageError = new MessageError("404","Not found");
        MessageError check = new MessageError(messageError.toXML());
        assertEquals(MessageError.getException(check.toXML()).getCode(),"404");
        assertEquals(MessageError.getException(check.toXML()).getMessage(),"Not found");
        assertEquals(true,MessageError.isErrorMessage(check.toXML()));        
    }    
}
