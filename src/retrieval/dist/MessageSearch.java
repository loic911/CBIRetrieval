package retrieval.dist;

import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import retrieval.utils.CollectionUtils;

/**
 * Message XML for search.
 * A light Client will send it to a central server to get the k most similar
 * pictures on each servers.
 * @author Rollus Loic
 */
public class MessageSearch implements Message {

    /**
     * Top-k similar pictures
     * If too big, may generate an exception from central server
     */
    private int k;
    
    /**
     * Server where to request similar pictures
     */
    private List<String> servers;

    /**
     * Constructor for a search message
     * @param k Max similar pictures
     * @param pathPictureSearch Path of request picture
     */
    public MessageSearch(int k,List<String> servers) {
        this.k = k;
        this.servers = servers;
    }

    /**
     * Constructor for a search message
     * @param document XML document
     */
    public MessageSearch(Document document) throws Exception {
        Element root = document.getRootElement();
        k = Integer.parseInt(root.getAttributeValue("k"));
        servers = CollectionUtils.split(root.getAttributeValue("servers"),",");
    }

    /**
     * Generate a XML document from the error message
     * @return XML document
     */
    public Document toXML()  {
        try {
            Element racine = new Element("SEARCHPICTURE");
            racine.setAttribute(new Attribute("k", k + ""));
            racine.setAttribute(new Attribute("servers", CollectionUtils.join(getServers(), ",")));
            return new Document(racine);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Get the max number of similar pictures
     * @return Top-k
     */
    public int getK() {
        return k;
    }

    /**
     * @return the servers
     */
    public List<String> getServers() {
        return servers;
    }

    /**
     * @param servers the servers to set
     */
    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}
