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
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Rollus Loic
 */
public class MultiServerMessageIndex implements Message, Cloneable {
    private static Logger logger = Logger.getLogger(MultiServerMessageIndex.class);

    private Long id;
    private Map<String,String> properties;
    private String storage;
    public boolean async;

    public MultiServerMessageIndex(Long id,Map<String,String> properties, String storage,boolean async) {
        this.id = id;
        this.properties = properties;
        this.storage = storage;
        this.async = async;     
    }

    public MultiServerMessageIndex(Document document) throws NotValidMessageXMLException {
        try {
            Element root = document.getRootElement();
            async = root.getAttributeValue("async").equals("FALSE") ? false: true;
            storage = root.getAttributeValue("storage");
            if(root.getAttributeValue("id")!=null && !root.getAttributeValue("id").equals("null")) {
                id = Long.parseLong(root.getAttributeValue("id"));
            } else id = null;
            
           
            
            List listProperties = root.getChildren("property");
            Iterator itProperty = listProperties.iterator();
            properties = new HashMap<String,String>();
            
            while(itProperty.hasNext()) {
                Element propElement = (Element)itProperty.next();
                properties.put(propElement.getAttributeValue("key"), propElement.getAttributeValue("value"));
            }
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    @Override
    public String toString() {
        String s = "MESSAGE SUPER SERVER INDEX\n";
        s = s + "PICTURE " + id +"\n";
        if(properties!=null) {
            for(Map.Entry<String,String> entry : properties.entrySet()) {
               s = s + "PROP " + entry.getKey() + "=" + entry.getValue() + "\n";
           }           
        }
        return s;
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     */
    public Document toXML() {
        Document document = null;
        try {
            Element racine = new Element("MultiServerMessage");
            racine.setAttribute("type","INDEX");
            if(async) {
                racine.setAttribute("async","TRUE");
            }
            else {
                racine.setAttribute("async","FALSE");
            }

            document = new Document(racine);
            
            racine.setAttribute("id", id+"");
            racine.setAttribute("storage", getStorage());

            if(properties!=null) {
                for(Map.Entry<String,String> entry : properties.entrySet()) {
                    Element prop = new Element("property");
                    prop.setAttribute("key", entry.getKey()+"");
                    prop.setAttribute("value", entry.getValue()+"");
                    racine.addContent(prop);
                }                
            }

        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        return document;
    }

    /**
     * @return the ids
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the properties
     */
    public Map<String,String> getProperties() {
        return properties;
    }


    /**
     * @return the storage
     */
    public String getStorage() {
        return storage;
    }

}
