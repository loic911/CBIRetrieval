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
import org.jdom.Document;
import org.jdom.Element;

/**
 * Message XML which contains pictur path and their state (index,in queue,...)
 * A server will send this message as a response to an indexer
 * @author Rollus Loic
 */
public class MultiServerMessageInfos implements Message {

    /**
     * A map which map a picture path with an exception
     * (which can be null or NoException)
     */
    private Map<Long, Map<String,String>> allPictures;
    
    private String storage;

    /**
     * Constructor
     * @param allPictures A map with pictures and their state (opt)
     */
    public MultiServerMessageInfos(Map<Long, Map<String,String>> allPictures,String storage) {
        this.allPictures = allPictures;
        this.storage = storage;
        
    }

    /**
     * Constructor
     * @param document XML message
     * @throws NotValidMessageXMLException XML message was not valid
     */
    public MultiServerMessageInfos(Document document)  throws NotValidMessageXMLException {

        try {
            allPictures = new HashMap<Long, Map<String,String>>();
            Element root = document.getRootElement();
            if(root.getAttributeValue("storage")!=null && !root.getAttributeValue("storage").equals("null")) {
                storage = root.getAttributeValue("storage");
            }
            
            List listPictures = root.getChildren("picture");
            Iterator itPictures = listPictures.iterator();

           while (itPictures.hasNext()) {
                Element picture = (Element) itPictures.next();
                Long id = Long.parseLong(picture.getAttributeValue("id"));
                Map<String,String> prop = new HashMap<String,String>();
                List<String> list = new ArrayList<String>();
                
                List listProp = picture.getChildren("properties");
                Iterator itProp = listProp.iterator();
                while (itProp.hasNext()) {
                    Element propertyElem = (Element) itProp.next();
                    prop.put(propertyElem.getAttributeValue("key"), propertyElem.getAttributeValue("value"));                   
                }
                allPictures.put(id, prop);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        root.setAttribute("type","INFO");
        Document document = new Document(root);

            if(storage!=null) {
                root.setAttribute("storage", storage);
            } else {
                root.setAttribute("storage", "null");
            }        
        Iterator<Entry<Long,Map<String,String>>> it = getAllPictures().entrySet().iterator();

        while(it.hasNext()) {
            Entry<Long,Map<String,String>> entry = it.next();
            Long id = entry.getKey();
            Element pictElem = new Element("picture");
            pictElem.setAttribute("id", id+"");

            Iterator<Entry<String,String>> it2 = entry.getValue().entrySet().iterator();
            while(it2.hasNext()) {
                Element propElem = new Element("properties");
                Entry<String,String> pro = it2.next();
                propElem.setAttribute("key",pro.getKey());
                propElem.setAttribute("value",pro.getValue());
                pictElem.addContent(propElem);
            }
            root.addContent(pictElem);
        }
        return document;
    }

    /**
     * @return the allPictures
     */
    public Map<Long, Map<String,String>> getAllPictures() {
        return allPictures;
    }

    /**
     * @return the storage
     */
    public String getStorage() {
        return storage;
    }

}
