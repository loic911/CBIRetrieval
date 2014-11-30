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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.jdom.Document;
import org.jdom.Element;
import retrieval.exception.CBIRException;

/**
 * Message XML which contains pictur path and their state (index,in queue,...)
 * A server will send this message as a response to an indexer
 * @author Rollus Loic
 */
public class MultiServerMessageIndexResults implements Message {
   /**
     * A map which map a picture path with an exception
     * (which can be null or NoException)
     */
    private Map<String,Map<Long, CBIRException>> allPictures;

    /**
     * Constructor
     * @param allPictures A map with pictures and their state (opt)
     */
    public MultiServerMessageIndexResults(Map<String,Map<Long, CBIRException>> allPictures) {
        this.allPictures = allPictures;
    }

    /**
     * Constructor
     * @param document XML message
     * @throws NotValidMessageXMLException XML message was not valid
     */
    public MultiServerMessageIndexResults(Document document)
            throws NotValidMessageXMLException {

        try {
            allPictures = new HashMap<String,Map<Long, CBIRException>>();
            Element root = document.getRootElement();

            List listServer = root.getChildren("server");

            Iterator itServer = listServer.iterator();

           while (itServer.hasNext()) {
                Element serverSim = (Element) itServer.next();
                String idServer = serverSim.getAttributeValue("id");
                List listPict = serverSim.getChildren("pict");
                Map<Long, CBIRException> subAllPictures = new HashMap<Long, CBIRException>(listPict.size() * 2);
                Iterator it = listPict.iterator();

                while (it.hasNext()) {
                    Element result = (Element) it.next();
                    String key = result.getAttributeValue("id");
                    String valueCode = result.getAttributeValue("code");
                    String valueMsg = result.getAttributeValue("msg");

                    CBIRException value = new CBIRException(valueCode, valueMsg);
                    subAllPictures.put(Long.parseLong(key), value);
                }

                allPictures.put(idServer, subAllPictures);
            }
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    /**
     * Generate a XML document from the error message
     * @return XML document
     * @throws Exception Error during XML generation
     */
    public Document toXML() throws Exception {
        Element root = new Element("MultiServerMessage");
        root.setAttribute("type","INDEX");
        Document document = new Document(root);

        Iterator<Entry<String,Map<Long, CBIRException>>> it = allPictures.entrySet().iterator();
        while(it.hasNext()) {
            Entry<String,Map<Long, CBIRException>> entry = it.next();
            String idServer = entry.getKey();
            Map<Long, CBIRException> result = entry.getValue();
            Element serverRoot = new Element("server");
            serverRoot.setAttribute("id", idServer);

            for (Map.Entry<Long, CBIRException> subentry : result.entrySet()) {
                Element subresult = new Element("pict");
                subresult.setAttribute("id", subentry.getKey()+"");
                subresult.setAttribute("code", subentry.getValue().getCode());
                subresult.setAttribute("msg", subentry.getValue().toString());
                serverRoot.addContent(subresult);
            }
            root.addContent(serverRoot);
        }
        return document;
    }

    /**
     * Get the map with pictures path and their state
     * @return All pictures and state
     */
    public Map<String,Map<Long, CBIRException>> getAllPictures() {
        return allPictures;
    }

    public TreeMap<Long, CBIRException> getAllPicturesFlat() {
        
        TreeMap<Long, CBIRException> flatten = new TreeMap<Long, CBIRException>();

        Iterator<Entry<String,Map<Long, CBIRException>>> it = allPictures.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,Map<Long, CBIRException>> entry = it.next();
            flatten.putAll(entry.getValue());
        }
        return flatten;
    }
}
