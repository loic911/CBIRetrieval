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

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Rollus Loic
 */
public class MultiServerMessageDelete implements Message, Cloneable {
    private static Logger logger = Logger.getLogger(MultiServerMessageDelete.class);

    private List<Long> ids;
    private List<String> storages;

    public MultiServerMessageDelete(List<Long> ids,List<String> storages) {
        this.ids = ids;
        this.storages = storages;     
    }

    public MultiServerMessageDelete(Document document) throws NotValidMessageXMLException {
        try {
            Element root = document.getRootElement();
            ids = new ArrayList<Long>(); 
            List listPicture = root.getChildren("picture");
            Iterator itPicture = listPicture.iterator();
            while(itPicture.hasNext()) {
                ids.add(Long.parseLong(((Element)itPicture.next()).getAttributeValue("id")));
            }            
            
            storages = new ArrayList<String>(); 
            List listStorage = root.getChildren("storage");
            Iterator itStorage = listStorage.iterator();
            while(itStorage.hasNext()) {
                storages.add(((Element)itStorage.next()).getAttributeValue("id"));
            } 
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     */
    public Document toXML() {
        Document document = null;
        try {
            Element racine = new Element("MultiServerMessage");
            racine.setAttribute("type","DELETE");
            
            document = new Document(racine);
 
            for(int i=0;i<getIds().size();i++) {
                Element picture = new Element("picture");
                picture.setAttribute("id", getIds().get(i)+"");
                racine.addContent(picture);
            }
            for(int i=0;i<getStorages().size();i++) {
                Element storage = new Element("storage");
                storage.setAttribute("id", getStorages().get(i)+"");
                racine.addContent(storage);
            }            
        } catch(Exception e) {
            logger.error(e.toString());
        }
        return document;
    }

    /**
     * @return the ids
     */
    public List<Long> getIds() {
        return ids;
    }  

    /**
     * @return the storages
     */
    public List<String> getStorages() {
        return storages;
    }

}
