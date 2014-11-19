package retrieval.dist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import retrieval.exception.CBIRException;
import retrieval.storage.exception.NoException;

/**
 * Message XML which contains pictur path and their state (index,in queue,...)
 * A server will send this message as a response to an indexer
 * @author Rollus Loic
 */
public class MessageIndexResults implements Message {

    /**
     * A map which map a picture path with an exception
     * (which can be null or NoException)
     */
    private Map<String, CBIRException> allPictures;
    /**
     * A single picture (if only one to index)
     */
    private String singlePicture;

    /**
     * Constructor
     * @param allPictures A map with pictures and their state (opt)
     */
    public MessageIndexResults(Map<String, CBIRException> allPictures) {
        this.allPictures = allPictures;
    }

    /**
     * Constructor
     * @param singlePicture A single picture to index
     */
    public MessageIndexResults(String singlePicture) {
        this.allPictures = new HashMap<String, CBIRException>(2);
        this.allPictures.put(singlePicture, new NoException());
        this.singlePicture = singlePicture;
    }

    /**
     * Constructor
     * @param document XML message
     * @throws NotValidMessageXMLException XML message was not valid
     */
    public MessageIndexResults(Document document)
            throws NotValidMessageXMLException {

        try {
            Element root = document.getRootElement();
            List listPict = root.getChildren("pict");
            allPictures =
                    new HashMap<String, CBIRException>(listPict.size() * 2);
            Iterator it = listPict.iterator();

            while (it.hasNext()) {
                Element result = (Element) it.next();
                String key = result.getAttributeValue("path");
                String valueCode = result.getAttributeValue("code");
                String valueMsg = result.getAttributeValue("msg");

                CBIRException value = new CBIRException(valueCode, valueMsg);
                singlePicture = key;
                allPictures.put(key, value);
            }
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    /**
     * Generate a XML document from the error message
     * @return XML document
     * @throws Exception Error during XML generation
     */
    public Document toXML() throws Exception {
        Element racine = new Element("MessageIndex");
        Document document = new Document(racine);


        for (Map.Entry<String, CBIRException> entry : getAllPictures().entrySet()) {
            Element result = new Element("pict");
            result.setAttribute("path", entry.getKey());
            result.setAttribute("code", entry.getValue().getCode());
            result.setAttribute("msg", entry.getValue().toString());
            racine.addContent(result);

        }
        return document;
    }

    /**
     * Get the map with pictures path and their state
     * @return All pictures and state
     */
    public Map<String, CBIRException> getAllPictures() {
        return allPictures;
    }

    /**
     * Get a single picture
     * @return picture
     */
    public String getSinglePictures() {
        return singlePicture;
    }

    /**
     * Get the state map with a picture path
     * @param picture Key pictures
     * @return Value Exception (may be NoEXception (ok) or null)
     */
    public CBIRException getException(String picture) {
        return allPictures.get(picture);
    }
}
