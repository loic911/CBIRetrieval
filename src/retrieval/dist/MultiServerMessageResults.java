package retrieval.dist;

import java.util.*;
import java.util.Map.Entry;
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
public class MultiServerMessageResults implements Message {

    /**
     * Lists of ordered similar pictures
     */
    Map<String,List<ResultSim>> serverlists;
    Map<String,Long> serverSizelists;
    /**
     * Lists of server
     */
    private ListServerInformationSocket servers;
    /**
     * Total number of pictures of all servers
     */
    private long numberOfPicturesInIndex;
    
    /**
     * Constructor for a result message
     * @param document XML document
     * @throws NotValidMessageXMLException Bad xml document
     */
    public MultiServerMessageResults(Document document) throws NotValidMessageXMLException {
        try {
            serverlists = new TreeMap<String,List<ResultSim>>();
            serverSizelists=new TreeMap<String,Long>();
            numberOfPicturesInIndex = 0;

            Element root = document.getRootElement();

            List listServer = root.getChildren("server");

            Iterator itServer = listServer.iterator();

           while (itServer.hasNext()) {
                Element serverSim = (Element) itServer.next();
                String idServer = serverSim.getAttributeValue("id");
                Long size = Long.parseLong(serverSim.getAttributeValue("size"));
                numberOfPicturesInIndex = numberOfPicturesInIndex + size;
                serverSizelists.put(idServer, size);
                 List pictures = serverSim.getChildren("pict");
                 List<ResultSim> lists = new ArrayList<ResultSim>();

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
                 serverlists.put(idServer, lists);
            }

            List listServers = root.getChildren("serverinfo");

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
                    ServerInformationSocket server = new ServerInformationSocket(urlServer, portServer);
                    server.changeState(state, msg);
                    server.setSizeOfIndex(size);
                    servers.add(server, i);
                }
            } else {
                listServers = null;
            }
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    /**
     * Constructor for a result message
     * @param serverlists List of results
     * @param numberOfPictures Size of index
     */
    public MultiServerMessageResults(Map<String,List<ResultSim>> lists, Map<String,Long> listsSize) {
        this.serverlists = lists;
        this.serverSizelists = listsSize;
        this.numberOfPicturesInIndex = 0;
        for (Long serverSize : listsSize.values()) {
            numberOfPicturesInIndex=numberOfPicturesInIndex+serverSize;
        }
        this.servers = null;
    }

    /**
     * Constructor for a result message
     * @param serverlists List of results
     * @param servers List of server information
     * @param numberOfPictures Size of index
     */
    public MultiServerMessageResults(
            Map<String,List<ResultSim>> lists,
            ListServerInformationSocket servers,
            int numberOfPictures) {
        this.serverlists = lists;
        this.servers = servers;
        this.numberOfPicturesInIndex = numberOfPictures;
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     */
    @Override
    public String toString() {
        String s = "MESSAGE SUPER SERVER RESULTS\n";

        Iterator<Entry<String,List<ResultSim>>> it = serverlists.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,List<ResultSim>> entry = it.next();
            String idServer = entry.getKey();
            List<ResultSim> result = entry.getValue();
            s = s + "SERVER " + idServer +"\n";
            s+="NumberOfPicture="+ this.serverSizelists.get(idServer) + "\n";
            for (int i = 0; i < result.size(); i++) {
                s+=result.get(i).toString()+"\n";
            }
        }

        if (servers != null) {
            for (int i = 0; i < servers.size(); i++) {
                s+=servers.get(i).toString()+"\n";
            }
        }
        return s;
    }

    public Document toXML() throws Exception {
        Element root = new Element("MultiServerMessage");
        root.setAttribute("type","SEARCH3");
        root.setAttribute("size", numberOfPicturesInIndex + "");
        Document document = new Document(root);

        Iterator<Entry<String,List<ResultSim>>> it = serverlists.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,List<ResultSim>> entry = it.next();
            String idServer = entry.getKey();
            List<ResultSim> result = entry.getValue();
            Element serverRoot = new Element("server");
            serverRoot.setAttribute("id", idServer);
            serverRoot.setAttribute("size", this.serverSizelists.get(idServer)+"");
            for (int i = 0; i < result.size(); i++) {
                Element pict = new Element("pict");
                pict.setAttribute(
                        new Attribute("id", result.get(i).getId()+""));
                pict.setAttribute(
                        new Attribute("sim", result.get(i).getSimilarities() + ""));
                
                for(Map.Entry<String,String> entryProp : result.get(i).getProperties().entrySet()) {
                    Element prop = new Element("property");
                    prop.setAttribute("key", entryProp.getKey());
                    prop.setAttribute("value", entryProp.getValue());
                    pict.addContent(prop);
                }                
                
                serverRoot.addContent(pict);
            }
            root.addContent(serverRoot);
        }

        if (servers != null) {
            for (int i = 0; i < servers.size(); i++) {

                Element pict = new Element("server");
                pict.setAttribute(
                        new Attribute("url", servers.get(i).getAddress()));
                pict.setAttribute(
                        new Attribute("port", servers.get(i).getPort() + ""));
                pict.setAttribute(
                        new Attribute("state", servers.get(i).getState() + ""));
                pict.setAttribute(
                        new Attribute("msg", servers.get(i).getMessage() + ""));
                pict.setAttribute(
                        new Attribute("size", servers.get(i).getSizeOfIndex() + ""));
                root.addContent(pict);
            }
        }


        return document;
    }

    /**
     * Get most similar pictures
     * @return Most similar pictures
     */
    public Map<String,List<ResultSim>> getResults() {
        return serverlists;
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
    public long getNumberOfPicturesInIndex() {
        return numberOfPicturesInIndex;
    }
}
