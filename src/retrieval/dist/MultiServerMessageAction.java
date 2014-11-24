package retrieval.dist;

import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Rollus Loic
 */
public class MultiServerMessageAction implements Message, Cloneable {
    
    /**
     * Purge command
     */
    public static final int PURGE = 0;
    
    /**
     * Size command
     */
    public static final int SIZE = 1;
    
    /**
     * Info command
     */
    public static final int INFOS = 2;
    
    public static final int STORAGES = 3;

    public int action;
    public String storage;

    public MultiServerMessageAction(int action) {
        this.action = action;
    }  
    
    public MultiServerMessageAction(int action, String storage) {
        this.action = action;
        this.storage = storage;
    }      

    public MultiServerMessageAction(Document document) throws NotValidMessageXMLException {
        try {
            Element root = document.getRootElement();
            storage = root.getAttributeValue("storage");

            if(root.getAttributeValue("type").equals("PURGE")) {
                action = PURGE;
            } else if(root.getAttributeValue("type").equals("SIZE")) {
                action = SIZE;
            } else if(root.getAttributeValue("type").equals("INFOS")) {
                action = INFOS;
            } else if(root.getAttributeValue("type").equals("STORAGES")) {
                action = STORAGES;
            }else {
                throw new NotValidMessageXMLException(root.getAttributeValue("type") + " is not a valid command!");
            }
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    public String getActionStr() {
        if(action==PURGE) {
            return "PURGE";
        }
        if(action==SIZE) {
            return "SIZE";
        }
        if(action==INFOS) {
            return "INFOS";
        }
        if(action==STORAGES) {
            return "STORAGES";
        }        
        return "ERROR";
    }

    @Override
    public String toString() {
        String s = "MESSAGE SUPER SERVER "+getActionStr()+"\n";
        return s;
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     */
    public Document toXML() {
        Element racine = new Element("MultiServerMessage");
        racine.setAttribute("type",getActionStr());
        if(storage!=null) {
            racine.setAttribute("storage",storage);
        }
        return new Document(racine);
    }
}
