package retrieval.client;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import retrieval.server.RetrievalServer;
import retrieval.storage.Storage;

/**
 * This class is a list of server information
 * @author Rollus Loic
 */
public class ListServerInformationObject {

    /**
     * Timout for each server
     */
    private int timeout;
    
    /**
     * Map of server information object
     */
    private TreeMap<String,ServerInformationObject> serversMap;

    /**
     * Constructor of empty server list
     */
    public ListServerInformationObject() {
        this.serversMap = new TreeMap();
    }

    /**
     * Constructor of server list
     * @param servers Servers based list
     * @param timeout Max timeout for each server 
     */
    public ListServerInformationObject(List<RetrievalServer> servers, int timeout)  {
        this.serversMap = new TreeMap();
        this.timeout = timeout;
        
        int i = 0;
        for(RetrievalServer server : servers) {
            serversMap.put(i + "",new ServerInformationObject(server, timeout));
            i++;
        }
        
    }
    /**
     * Return the list with all servers
     * @return All server list
     */
    public synchronized Object getServers() {
        ListServerInformationObject listServers = new ListServerInformationObject();
        TreeMap<String,Storage> ser = new TreeMap<String,Storage>();

         for (Map.Entry<String, ServerInformationObject> entry : serversMap.entrySet()) {
             ServerInformationObject sio = (ServerInformationObject)entry.getValue();
             RetrievalServer multi = sio.getServer();
             List<Storage> storages = multi.getServerList();
             
             for(Storage storage : storages) {
                 ser.put(entry.getKey()+"#"+storage.getStorageName(),storage);       
             }
             
        }        
       // listServers.serversMap = ser;
        return ser;
    }

    /**
     * Return Server list with only servers that has keys contains in argument
     * @param keys Server keys to filter (if empty or null, return all server)
     * @return Server list filter by keys
     */
    public synchronized Object getServers(String[] keys) {
        if(keys==null) {
            return getServers();
        }
        if(keys.length==0) {
            return getServers();
        }
        ListServerInformationObject listServers = new ListServerInformationObject();
        TreeMap<String,Storage> ser = new TreeMap<String,Storage>();

        for(int i=0;i<keys.length;i++) {
            for (Map.Entry<String, ServerInformationObject> entry : serversMap.entrySet()) {
                ServerInformationObject sio = (ServerInformationObject)entry.getValue();
                RetrievalServer multi = sio.getServer();
                Storage storage = multi.getServer(keys[i]);
                if(storage!=null) {
                    ser.put(entry.getKey()+"#"+storage.getStorageName(),storage);       
                }
           }  
        }
           //  listServers.serversMap = ser;
        return ser;
    }

    /**
     * Get a String with a list of server information
     * @return String of this object
     */
    @Override
    public String toString() {
        String server = "************ SERVER LIST **************\n";
         for (Map.Entry<String, ServerInformationObject> entry : serversMap.entrySet()) {
            String key = entry.getKey();
            ServerInformationObject value = entry.getValue();
            server = server + key + "=" + value + "\n";
        }
        server = server + "*************************************\n";
        return server;
    }

    /**
     * Get the server i
     * @param i Index of server
     * @return Server i
     */
    public ServerInformationObject get(int i) {
        return serversMap.get(i+"");
    }

    public Map<String,ServerInformationObject> getMap(){
        return serversMap;
    }

    /**
     * Add a new server information object at index i
     * @param server New Server information object
     * @param i Index
     */
    public void add(ServerInformationObject server, int i) {
        serversMap.put(i+"",server);
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

    /**
     * Retrieve a server as a generic server (object or socket)
     * @param i Index/Key of server
     * @return Server (generic)
     */
    public ServerInformation getGenericServer(int i) {
        return get(i);
    }
}
