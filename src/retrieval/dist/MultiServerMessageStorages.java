package retrieval.dist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Message XML which contains pictur path and their state (index,in queue,...)
 * A server will send this message as a response to an indexer
 * @author Rollus Loic
 */
public class MultiServerMessageStorages implements Message {

    /**
     * A map which map a picture path with an exception
     * (which can be null or NoException)
     */
    private Map<String, Long> allStorages;

    /**
     * Constructor
     * @param allPictures A map with pictures and their state (opt)
     */
    public MultiServerMessageStorages(Map<String, Long> allStorages) {
        this.allStorages = allStorages;
        
    }

    /**
     * Constructor
     * @param document XML message
     * @throws NotValidMessageXMLException XML message was not valid
     */
    public MultiServerMessageStorages(Document document)  throws NotValidMessageXMLException {

        try {
            allStorages = new TreeMap<String, Long>();
            Element root = document.getRootElement();
            
            List listStorage = root.getChildren("storage");
            Iterator itStorage = listStorage.iterator();

           while (itStorage.hasNext()) {
                Element storage = (Element) itStorage.next();
                String name = storage.getAttributeValue("name");
                Long size = Long.parseLong(storage.getAttributeValue("size"));
                allStorages.put(name, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    /**
     * Generate a XML document from the error message
     * @return XML document
     * @throws Exception Error during XML generation
     */
    public Document toXML() throws Exception {
        
        Element root = new Element("MultiServerMessage");
        root.setAttribute("type","STORAGES");
        Document document = new Document(root);
      
        Iterator<Entry<String,Long>> it = getAllStorages().entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,Long> entry = it.next();
            Element pictElem = new Element("storage");
            pictElem.setAttribute("name", entry.getKey());
            pictElem.setAttribute("size", entry.getValue()+"");
            root.addContent(pictElem);
        }
        return document;
    }

    /**
     * @return the allStorages
     */
    public Map<String, Long> getAllStorages() {
        return allStorages;
    }

    /**
     * @param allStorages the allStorages to set
     */
    public void setAllStorages(Map<String, Long> allStorages) {
        this.allStorages = allStorages;
    }

  

}
