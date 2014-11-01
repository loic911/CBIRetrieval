package retrieval.dist;

import org.jdom.Document;

/**
 * Force to implement method for XML message
 * In practise, each class MessageX which implements Message
 * should implement a constructor:
 * MessageX(Document xml) so that client and server can read/write
 * MessageX from/to XML.
 * @author Rollus Loic
 */
public interface Message {

    /**
     * Generate a XML document from the error message
     * @return XML document
     * @throws Exception Error during XML generation
     */
    Document toXML() throws Exception;
}
