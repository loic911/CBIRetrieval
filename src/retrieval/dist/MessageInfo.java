package retrieval.dist;

import java.util.*;
import org.jdom.*;
import retrieval.exception.*;

/**
 * Message for XML request to get information from a server
 * An indexer (or a client) will send this message to ask to a server some
 * informations. A server will send this message to answer with information
 * @author Rollus Loic
 */
public class MessageInfo implements Message{

    /**
     * A map which contains files map with exceptions
     * (may be null or NoException)
     */
    private Map<String, CBIRException> files;
    /**
     * A map which contains pictures and their id on server
     */
    private Map<String, Integer> pictures;
    /**
     * Size of index (number of picture indexed)
     */
    private long numberOfIndexItems;
    /**
     * Flag which indicates that user want all pictures information or
     * only from files map
     */
    boolean listAll;

    /**
     * Constructor for an information message
     * @param document XML message
     * @throws NotValidMessageXMLException Error in XML message document
     */
    public MessageInfo(Document document) throws NotValidMessageXMLException {
        try {

            Element root = document.getRootElement();

            listAll = Boolean.parseBoolean(root.getAttributeValue("listAll"));
            List picts = root.getChildren("pict");
            List pictsLists = root.getChildren("pictList");
            numberOfIndexItems = Long.parseLong(root.getAttributeValue("nb"));

            //list only information from pictures in files map
            if (!listAll) {
                files = new HashMap<String, CBIRException>(picts.size());
                for (int i = 0; i < picts.size(); i++) {
                    Element pict = (Element) picts.get(i);
                    String picture = pict.getAttributeValue("path");
                    String code = pict.getAttributeValue("code");
                    String message = pict.getAttributeValue("msg");
                    files.put(picture, new CBIRException(code, message));
                }
            } else {
                //list all pictures from server
                pictures = new HashMap<String, Integer>(pictsLists.size());
                for (int i = 0; i < pictsLists.size(); i++) {
                    Element pict = (Element) pictsLists.get(i);
                    String picture = pict.getAttributeValue("path");
                    String code = pict.getAttributeValue("code");
                    pictures.put(picture, Integer.parseInt(code));
                }
            }
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    /**
     * Constructor for an information message (server side)
     * @param files All files mapped with an optionnal exception
     * @param numberOfIndexItems Size of pictures index
     */
    public MessageInfo(
            Map<String, CBIRException> files,
            long numberOfIndexItems) {
        this.files = files;
        this.numberOfIndexItems = numberOfIndexItems;
    }

    /**
     * Constructor for an information message (client side)
     * @param files All files mapped with an optionnal exception
     */
    public MessageInfo(Map<String, CBIRException> files) {
        this.files = files;
        this.numberOfIndexItems = -1;
    }

    /**
     * Constructor for an information message
     */
    public MessageInfo() {
        this.listAll = true;
        this.pictures = new HashMap<String, Integer>();
        this.files = null;
        this.numberOfIndexItems = -1;
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     */
    public Document toXML() {
        Element root = new Element("info");
        Document document = new Document(root);
        root.setAttribute("nb", "" + getNumberOfIndexItems());

        root.setAttribute("listAll", listAll + "");

        if (!listAll) {
            for (Map.Entry<String, CBIRException> entry : files.entrySet()) {
                Element pict = new Element("pict");
                pict.setAttribute(new Attribute("path", entry.getKey()));
                if(entry.getValue()!=null) {
                    pict.setAttribute(new Attribute("code", entry.getValue().getCode()));
                    pict.setAttribute(new Attribute("msg", entry.getValue().toString()));                    
                }
                root.addContent(pict);
            }
        } else {

            for (Map.Entry<String, Integer> entry : pictures.entrySet()) {

                Element pict = new Element("pictList");
                pict.setAttribute(new Attribute("path", entry.getKey()));
                pict.setAttribute(new Attribute("code", entry.getValue() + ""));

                root.addContent(pict);
            }
        }
        return document;
    }

    /**
     * Get the size of index (number of pictures) from server
     * @return Size of index
     */
    public long getNumberOfIndexItems() {
        return numberOfIndexItems;
    }

    /**
     * Get a set of pictures and their server id
     * @return Map of Pictures
     */
    public Map<String, Integer> getPictures() {
        return pictures;
    }

    /**
     * Set a set of pictures and their server id
     * @param pictures Map of Pictures
     */
    public void setPictures(Map<String, Integer> pictures) {
        this.pictures = pictures;
    }

    /**
     * Get a set of Files and their exception
     * @return Set of files
     */
    public Map<String, CBIRException> getFiles() {
        return files;
    }
}
