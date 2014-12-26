/*
 * Copyright 2009-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrieval.dist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import retrieval.client.ListServerInformationSocket;
import retrieval.storage.index.ResultSim;

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
                     List propertiesElem = pict.getChildren("property");
                    for (int j = 0; j < propertiesElem.size(); j++) {
                        Element prop = (Element) propertiesElem.get(j);
                        properties.put(prop.getAttributeValue("key"),prop.getAttributeValue("value"));
                    }
                    ResultSim r = new ResultSim(id,properties,similarity);
                    lists.add(r);
                }
                 serverlists.put(idServer, lists);
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
