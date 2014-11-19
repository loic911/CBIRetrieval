package retrieval.dist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Message for XML request to index/delete pictures/paths
 * An indexer will send this message to a server
 * @author Rollus Loic
 */
public class MessageIndexOrDeletePicture implements Message{

    /**
     * List of path/pictures to index
     */
    private List<Long> ids;
    
    private List<Map<String,String>> properties;
    /**
     * Browse path recursively or not
     */
    private boolean recursive;
    /**
     * Delete path/pictures or index it
     */
    private boolean delete;
    
    private boolean synchrone;
    
    private boolean purge;


    /**
     * Constructor for this message
     * Delete and recursive are by default False
     * @param paths Path to index
     */
    public MessageIndexOrDeletePicture(List<Long> ids, List<Map<String,String>> properties, boolean synchrone) {
        this.ids = ids;
        this.properties = properties;
        this.recursive = false;
        this.delete = false;
        this.synchrone = synchrone;
        this.purge = false;
    }
    
    /**
     * Constructor for this message 
     * Simply purge index
     * @param path Path to delete
     * @param recursive Recursive browse of path
     */
    public MessageIndexOrDeletePicture(boolean purge) {
        this.recursive = false;
        this.delete = false;
        this.synchrone = false;
        this.purge = purge;
    } 
    
    
    /**
     * Constructor for this message 
     * Simply purge index
     * @param path Path to delete
     * @param recursive Recursive browse of path
     */
    public MessageIndexOrDeletePicture(List<Long> ids, boolean delete) {
        this.ids = ids;
        this.recursive = false;
        this.delete = true;
        this.synchrone = false;
        this.purge = purge;
    }     

    /**
     * Constructor for this message
     * @param document XML document to build message
     * @throws NotValidMessageXMLException Message is not valid
     */
    public MessageIndexOrDeletePicture(Document document) throws NotValidMessageXMLException {
        try {           
            Element root = document.getRootElement();
            recursive = Boolean.parseBoolean(root.getAttributeValue("recursive"));
            delete = Boolean.parseBoolean(root.getAttributeValue("delete"));
            synchrone = Boolean.parseBoolean(root.getAttributeValue("synchrone"));
            purge = Boolean.parseBoolean(root.getAttributeValue("purge"));
            List pictures = root.getChildren("pict");
            ids = new ArrayList<Long>(pictures.size());
            properties = new ArrayList<Map<String,String>>();
            
            //    private List<Long> ids;
    
           // private List<Map<String,String>> properties;

            for (int i = 0; i < pictures.size(); i++) {
                Map<String,String> prop = new HashMap<String,String>();
                Element pict = (Element) pictures.get(i);
                ids.add(Long.parseLong(pict.getAttributeValue("id")));
                List propertiesXML = pict.getChildren("property");
                
                for (int j = 0; j < propertiesXML.size(); j++) {
                    Element property = (Element) propertiesXML.get(i);
                    String key = property.getAttributeValue("key");
                    String value = property.getAttributeValue("value");
                    prop.put(key,value);
                }
                properties.add(prop);
            }
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.getMessage());
        }
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     * @throws NotValidMessageXMLException Bad xml message
     */
    public Document toXML() throws NotValidMessageXMLException {
        Document document = null;
        try {
            Element racine = new Element("addpictures");
            racine.setAttribute(new Attribute("recursive", isRecursive() + ""));
            racine.setAttribute(new Attribute("delete", isDelete() + ""));
            racine.setAttribute(new Attribute("synchrone", isSynchrone() + ""));
            racine.setAttribute(new Attribute("purge", isPurge() + ""));

            document = new Document(racine);
            for (int i = 0; i <getIds().size(); i++) {
                Long id = getIds().get(i);
                Element pathElement = new Element("pict");
                pathElement.setAttribute(new Attribute("id", id+""));
                Map<String,String> prop = properties.get(i);
                Iterator it = prop.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    Element proElement = new Element("property");
                    proElement.setAttribute(new Attribute("key", (String)pairs.getKey()));         
                    proElement.setAttribute(new Attribute("value", (String)pairs.getValue())); 
                }
                
                racine.addContent(pathElement);
            }
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.getMessage());
        }
        return document;
    }

    /**
     * Get the paths/files lists
     * @return the paths/files list
     */
    public List<Long> getIds() {
        return ids;
    }

    /**
     * Paths must be browse recursively
     * @return True if paths are browser recursively, otherwise false
     */
    public boolean isRecursive() {
        return recursive;
    }

    /**
     * Delete or index paths
     * @return True is paths must be delete, otheriwse false
     */
    public boolean isDelete() {
        return delete;
    }

    /**
     * Change delete argument
     * @param delete Paths must be delete
     */
    public void setDelete(boolean delete) {
        this.delete = delete;
    }



    /**
     * @return the synchrone
     */
    public boolean isSynchrone() {
        return synchrone;
    }

    /**
     * @param synchrone the synchrone to set
     */
    public void setSynchrone(boolean synchrone) {
        this.synchrone = synchrone;
    }

    /**
     * @return the purge
     */
    public boolean isPurge() {
        return purge;
    }

    /**
     * @param purge the purge to set
     */
    public void setPurge(boolean purge) {
        this.purge = purge;
    }
}
