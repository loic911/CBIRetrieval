package retrieval.dist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Rollus Loic
 */
public class MultiServerMessageDelete implements Message, Cloneable {
    private static Logger logger = Logger.getLogger(MultiServerMessageDelete.class);

    private List<Long> ids;
    private List<String> storages;

    public MultiServerMessageDelete(List<Long> ids,List<String> storages) {
        this.ids = ids;
        this.storages = storages;     
    }

    public MultiServerMessageDelete(Document document) throws NotValidMessageXMLException {
        try {
            Element root = document.getRootElement();
            ids = new ArrayList<Long>(); 
            List listPicture = root.getChildren("picture");
            Iterator itPicture = listPicture.iterator();
            while(itPicture.hasNext()) {
                ids.add(Long.parseLong(((Element)itPicture.next()).getAttributeValue("id")));
            }            
            
            storages = new ArrayList<String>(); 
            List listStorage = root.getChildren("storage");
            Iterator itStorage = listStorage.iterator();
            while(itStorage.hasNext()) {
                storages.add(((Element)itStorage.next()).getAttributeValue("id"));
            } 
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    @Override
    public String toString() {
        String s = "MESSAGE SUPER SERVER DELETE\n";
        for(int i=0;i<getIds().size();i++) {
            s = s + "PICTURE " + getIds().get(i) + "\n";
        }
        for(int i=0;i<getStorages().size();i++) {
            s = s + "STORAGE " + getStorages().get(i) + "\n";
        }        
        return s;
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     */
    public Document toXML() {
        Document document = null;
        try {
            Element racine = new Element("MultiServerMessage");
            racine.setAttribute("type","DELETE");
            
            document = new Document(racine);
 
            for(int i=0;i<getIds().size();i++) {
                Element picture = new Element("picture");
                picture.setAttribute("id", getIds().get(i)+"");
                racine.addContent(picture);
            }
            for(int i=0;i<getStorages().size();i++) {
                Element storage = new Element("storage");
                storage.setAttribute("id", getStorages().get(i)+"");
                racine.addContent(storage);
            }            
        } catch(Exception e) {
            logger.error(e.toString());
        }
        return document;
    }

    /**
     * @return the ids
     */
    public List<Long> getIds() {
        return ids;
    }

    /**
     * @param ids the ids to set
     */
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }    

    /**
     * @return the storages
     */
    public List<String> getStorages() {
        return storages;
    }

    /**
     * @param storages the storages to set
     */
    public void setStorages(List<String> storages) {
        this.storages = storages;
    }
   
}
