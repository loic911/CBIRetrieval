package retrieval.dist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import retrieval.multicentralserver.ListServerInformationSocket;
import retrieval.multicentralserver.ServerInformationSocket;
import retrieval.server.index.ResultSim;

/**
 * Message for XML results
 * Many servers will send this message to a central server (similar pitcures).
 * A Central server will send this message to a client
 * (similar pictures + server state)
 * and a list of server state
 * @author Rollus Loic
 */
public class MessageResults implements Message {

    /**
     * Lists of ordered similar pictures
     */
    List<ResultSim> lists;
    /**
     * Lists of server
     */
    private ListServerInformationSocket servers;
    
    /**
     * Total number of pictures of all servers
     */
    private int numberOfPicturesInIndex;
    
    private Integer serverKey;

    /**
     * Constructor for a result message
     * @param document XML document
     * @throws NotValidMessageXMLException Bad xml document
     */
    public MessageResults(Document document, Integer serverKey) throws NotValidMessageXMLException {
        try {
            this.serverKey = serverKey;
            Element root = document.getRootElement();
            numberOfPicturesInIndex =
                    Integer.parseInt(root.getAttributeValue("size"));
            List pictures = root.getChildren("pict");
            lists = new ArrayList<ResultSim>(pictures.size());

            for (int i = 0; i < pictures.size(); i++) {
                Element pict = (Element) pictures.get(i);
                Long id = Long.parseLong(pict.getAttributeValue("id"));
                double similarity = Double.parseDouble(pict.getAttributeValue("sim"));
                
                Map<String,String> properties = new HashMap<String,String>();
                List propertiesElem = root.getChildren("property");
                for (int j = 0; j < propertiesElem.size(); j++) {
                    Element prop = (Element) propertiesElem.get(i);
                    properties.put(prop.getAttributeValue("key"),prop.getAttributeValue("value"));
                }
                ResultSim r = new ResultSim(id,properties,similarity);
                lists.add(r);
            }

            List listServers = root.getChildren("server");

            if (listServers.size() > 0) {
                servers = new ListServerInformationSocket();
                for (int i = 0; i < listServers.size(); i++) {
                    Element serv = (Element) listServers.get(i);
                    String urlServer = serv.getAttributeValue("url");
                    int portServer =
                            Integer.parseInt(serv.getAttributeValue("port"));
                    int state =
                            Integer.parseInt(serv.getAttributeValue("state"));
                    String msg = serv.getAttributeValue("msg");
                    int size = Integer.parseInt(serv.getAttributeValue("size"));
                    ServerInformationSocket server =
                            new ServerInformationSocket(urlServer, portServer);
                    server.changeState(state, msg);
                    server.setSizeOfIndex(size);
                    servers.add(server, i);
                }
            } 
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    /**
     * Constructor for a result message
     * @param lists List of results
     * @param numberOfPictures Size of index
     */
    public MessageResults(List<ResultSim> lists, int numberOfPictures) {
        this.lists = lists;
        this.numberOfPicturesInIndex = numberOfPictures;
        this.servers = null;
    }

    /**
     * Constructor for a result message
     * @param lists List of results
     * @param servers List of server information
     * @param numberOfPictures Size of index
     */
    public MessageResults(
            List<ResultSim> lists,
            ListServerInformationSocket servers,
            int numberOfPictures) {
        this.lists = lists;
        this.servers = servers;
        this.numberOfPicturesInIndex = numberOfPictures;
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     */
    public String toString() {

        String s = "";
        s+="NumberOfPicture="+ numberOfPicturesInIndex + "\n";
        for (int i = 0; i < lists.size(); i++) {
            s+=lists.get(i).toString()+"\n";
        }

        if (servers != null) {
            for (int i = 0; i < servers.size(); i++) {
                s+=servers.get(i).toString()+"\n";
            }
        }
        return s;
    }

    public Document toXML() throws Exception {
        Element root = new Element("Message3");
        root.setAttribute("size", numberOfPicturesInIndex + "");
        Document document = new Document(root);

        for (int i = 0; i < lists.size(); i++) {
            Element pict = new Element("pict");
            pict.setAttribute(new Attribute("id", lists.get(i).getId()+""));
            pict.setAttribute(new Attribute("sim", lists.get(i).getSimilarities() + ""));
            
           for(Map.Entry<String,String> entry : lists.get(i).getProperties().entrySet()) {
               Element prop = new Element("property");
               prop.setAttribute("key", entry.getKey());
               prop.setAttribute("value", entry.getValue());
               pict.addContent(prop);
           }
            
            root.addContent(pict);
            
        }

        if (servers != null) {
            List<Integer> serverKeys = servers.getKeys();
            for (int i = 0; i < serverKeys.size(); i++) {
                Element pict = new Element("server");
                pict.setAttribute(new Attribute("url", servers.get(serverKeys.get(i)).getAddress()));
                pict.setAttribute(new Attribute("port", servers.get(serverKeys.get(i)).getPort() + ""));
                pict.setAttribute(new Attribute("state", servers.get(serverKeys.get(i)).getState() + ""));
                pict.setAttribute(new Attribute("msg", servers.get(serverKeys.get(i)).getMessage() + ""));
                pict.setAttribute(new Attribute("size", servers.get(serverKeys.get(i)).getSizeOfIndex() + ""));
                root.addContent(pict);
            }
        }


        return document;
    }

    /**
     * Get most similar pictures
     * @return Most similar pictures
     */
    public List<ResultSim> getResults() {
        return lists;
    }

    /**
     * Get the servers informations
     * @return the servers Servers List
     */
    public ListServerInformationSocket getServers() {
        return servers;
    }

    /**
     * Get the number of indexed pictures
     * @return Number of indexed pictures
     */
    public int getNumberOfPicturesInIndex() {
        return numberOfPicturesInIndex;
    }

    /**
     * @return the serverKey
     */
    public Integer getServerKey() {
        return serverKey;
    }

    /**
     * @param serverKey the serverKey to set
     */
    public void setServerKey(Integer serverKey) {
        this.serverKey = serverKey;
    }
}
