package retrieval.dist;

import org.jdom.Document;
import org.jdom.Element;
import retrieval.exception.CBIRException;

/**
 * Message for XML error by a central server or a server
 * @author Rollus Loic
 */
public class MessageError implements Message {

    /**
     * Code for the exception
     */
    private String code;
    /**
     * Message map with exception
     */
    private String message;

    /**
     * Constructor
     * @param ex A CBIRException which occur in a server or a central server
     */
    public MessageError(CBIRException ex) {
        this.code = ex.getCode();
        if (ex.getMessage() != null) {
            this.message = ex.getMessage();
        } else {
            this.message = "";
        }
    }

    /**
     * Constructor
     * @param code Error code
     * @param message Error message
     */
    public MessageError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor
     * @param msg XML error message
     * @throws NotValidMessageXMLException Not valid XML error message
     */
    public MessageError(Document msg) throws NotValidMessageXMLException {
        try {
            Element root = msg.getRootElement();
            this.code = root.getAttributeValue("code");
            this.message = root.getAttributeValue("message");
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.getMessage());
        }
    }

    /**
     * Generate a XML document from the error message
     * @return XML document
     */
    public Document toXML() {
        Element racine = new Element("error");
        racine.setAttribute("code", code);
        racine.setAttribute("message", message);
        Document document = new Document(racine);
        return document;

    }

    /**
     * Check if xml message is an error message
     * @param msg XML message
     * @return True if error message, else false;
     */
    public static boolean isErrorMessage(Document msg) {
        Element root = msg.getRootElement();
        if (root.getName().equals("error")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generate the exception from the xml message
     * @param msg XML message
     * @return CBIRException build with message
     */
    public static CBIRException getException(Document msg) {
        Element root = msg.getRootElement();
        String code = root.getAttributeValue("code");
        String message = root.getAttributeValue("message");
        return new CBIRException(code, message);
    }
}
