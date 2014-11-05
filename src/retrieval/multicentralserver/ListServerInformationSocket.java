package retrieval.multicentralserver;

import java.io.File;
import java.net.URL;
import java.util.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import retrieval.exception.CBIRException;
import retrieval.server.RetrievalServer;

/**
 * This class is a list of server information
 * @author Rollus Loic
 */
public class ListServerInformationSocket implements Cloneable {

    /**
     * Timout for each server
     */
    private int timeout;
    
    /**
     * Array of server information object
     */
    private TreeMap<Integer,ServerInformationSocket> serversMap;

    /**
     * Constructor of a server socket list
     */
    public ListServerInformationSocket() {
        this.serversMap = new TreeMap<Integer,ServerInformationSocket>();
    }
    
    /**
     * Constructor of a server socket list
     */
    public ListServerInformationSocket(List<ServerInformationSocket> servers) {
        this.serversMap = new TreeMap<Integer,ServerInformationSocket>();
        for(int i=0;i<servers.size();i++) {
            serversMap.put(i, servers.get(i));
        }
    }   
    

    /**
     * Constructor of a server socket list
     * @param path Path of xml file
     * @param timeout Timeout for this server
     * @throws Exception Not valid XML file
     */
    public ListServerInformationSocket(String path, int timeout) throws CBIRException {
        this.timeout = timeout;
        SAXBuilder sxb = new SAXBuilder();
        Document document = null;
        try {
            //try if xml file
            document = sxb.build(new File(path));
        } catch (Exception e) {
            try {
                //if error, try if xml url file
                document = sxb.build(new URL(path));
            } catch (Exception ex) {
                //if error, throw...
                throw new CBIRException(ex.toString());
            }
        }

        //build server list information with xml document
        Element root = document.getRootElement();
        List listTV = root.getChildren("server");
        serversMap = new TreeMap<Integer,ServerInformationSocket>();
        int i = 0;
        Iterator it = listTV.iterator();
        while (it.hasNext()) {
            Element server = (Element) it.next();

            String host = server.getAttributeValue("host");
            int port = Integer.parseInt(server.getAttributeValue("port"));

            ServerInformationSocket si = new ServerInformationSocket(host, port, timeout);
            serversMap.put(i, si);
            i++;
        }
    }

    /**
     * Clone function
     * @return Clone object (same object, not same reference)
     */
    public synchronized Object getServers() {
        ListServerInformationSocket listServers = new ListServerInformationSocket();
        TreeMap<Integer,ServerInformationSocket> ser = new TreeMap<Integer,ServerInformationSocket>();

         for (Map.Entry<Integer, ServerInformationSocket> entry : serversMap.entrySet()) {
            Integer key = entry.getKey();
            ServerInformationSocket value = entry.getValue();
            ser.put(key,(ServerInformationSocket)value.clone());
        }

        listServers.serversMap = ser;
        return listServers;
    }

    /**
     * Return Server list with only servers that has keys contains in argument
     * @param keys Server keys to filter (if empty or null, return all server)
     * @return Server list filter by keys
     */  
    public synchronized Object getServers(Integer[] keys) {
        if(keys==null) {
            return getServers();
        }
        if(keys.length==0) {
            return getServers();
        }
        ListServerInformationSocket listServers = new ListServerInformationSocket();
        TreeMap<Integer,ServerInformationSocket> ser = new TreeMap<Integer,ServerInformationSocket>();

        for(int i=0;i<keys.length;i++) {
            ServerInformationSocket server = serversMap.get(keys[i]);
            if(server!=null) {
                ser.put(keys[i],(ServerInformationSocket)server.clone());
            }
        }
        listServers.serversMap = ser;
        return listServers;
    }
    
    public synchronized Object getServers(String[] keys) {
        if(keys==null) {
            return getServers();
        }
        if(keys.length==0) {
            return getServers();
        }
        Integer[] keysInt = new Integer[keys.length];
        for(int i=0;i<keys.length;i++) {
            keysInt[i]=Integer.parseInt(keys[i]);
        }
        return getServers(keysInt);
    }
    
    /**
     * Return Server list with only servers that has keys contains in argument
     * @param keys Server keys to filter (if empty or null, return all server)
     * @return Server list filter by keys
     */  
    public synchronized Object getServers(List<Integer> keys) {
        return getServers((Integer[])keys.toArray(new Integer[keys.size()]));
    }
  

    /**
     * Get a String with a list of server information
     * @return String of this object
     */
    @Override
    public String toString() {
        String server = "************ SERVER LIST **************\n";
         for (Map.Entry<Integer, ServerInformationSocket> entry : serversMap.entrySet()) {
            Integer key = entry.getKey();
            ServerInformationSocket value = entry.getValue();
            server = server + key + "=" + value + " timeout=" + value.getTimeout() +"\n";
        }
        server = server + "*************************************\n";
        return server;
    }

    /**
     * Close all servers socket connection
     */
    public void closeAll() {
         for (Map.Entry<Integer, ServerInformationSocket> entry : serversMap.entrySet()) {
            ServerInformationSocket value = entry.getValue();
            value.close();
        }
    }

    /**
     * Get the server i
     * @param i Index of server
     * @return Server i
     */
    public ServerInformationSocket get(int i) {
        return serversMap.get(i);
    }   
    
    public List<Integer> getKeys() {
        List<Integer> keys = new ArrayList<Integer>();
        for(Integer key : serversMap.keySet()) {
            keys.add(key);
        }
        return keys;
        
    }

    /**
     * Add a new server information object at index i
     * @param server New Server information object
     * @param i Index
     */
    public void add(ServerInformationSocket server, int i) {
        serversMap.put(i,server);
    }

    /**
     * Get the number of servers
     * @return Number of servers
     */
    public int size() {
        return serversMap.size();
    }

    /**
     * Get the timeout for each server
     * @return Timeout for each server
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Update the timeout value for server communication
     * @param timeout New timeout value
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
