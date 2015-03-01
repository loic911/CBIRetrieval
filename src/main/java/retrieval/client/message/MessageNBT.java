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
package retrieval.client.message;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import retrieval.dist.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Message for XML request NBT message
 * Central server will send this message to all server (only visual word).
 * Each server will response with visual word and their NBT on server.
 * NBT is the number of patchs map with visual word B for a server
 * @author Rollus Loic
 */
public class MessageNBT implements Message, Cloneable {

    /**
     * A list of map which contains for each tests vector a map with
     * visual word and nbt (0 if message from central server).
     * Ex:
     * VT1
     * 654843 - 45 ; 89974 - 36 ; 13214 - 32
     * VT2
     * ...
     */
    private List<ConcurrentHashMap<String, Long>> tvList;

    public MessageNBT(List<ConcurrentHashMap<String, Long>> tvList) {
        this.tvList = tvList;
    }


    public MessageNBT copyWithoutValue() throws CloneNotSupportedException {

        List<ConcurrentHashMap<String, Long>> tvListNewObject = new ArrayList<ConcurrentHashMap<String, Long>>(tvList.size());
        for (int j = 0; j < tvList.size(); j++) {
            ConcurrentHashMap<String, Long> tvm = new ConcurrentHashMap<String, Long>(tvList.get(j).size());
            for (Map.Entry<String, Long> entry : tvList.get(j).entrySet()) {
                tvm.put(entry.getKey(), 0L);
            }
            tvListNewObject.add(tvm);
        }
        MessageNBT clone = new MessageNBT(tvListNewObject);
        return clone;
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     */
    public Document toXML() {
        Element racine = new Element("MessageNBT");
        Document document = new Document(racine);

        for (int i = 0; i < tvList.size(); i++) {

            ConcurrentHashMap<String, Long> tv = tvList.get(i);
            Element tvxml = new Element("tv");
            tvxml.setAttribute(new Attribute("id", i + ""));
            racine.addContent(tvxml);

            for (Map.Entry<String, Long> entree : tv.entrySet()) {
                Element vw = new Element("vw");
                vw.setAttribute(new Attribute("b", entree.getKey()));
                vw.setAttribute(new Attribute("nbit", entree.getValue().intValue() + ""));
                tvxml.addContent(vw);
            }
        }
        return document;
    }

    /**
     * Get the list with each tests vectors visual words
     * ex: Item 2 is a map with each vw and nbt for test vector 2
     * @return Vector with each tests vector Visual words
     */
    public List<ConcurrentHashMap<String, Long>> getVisualWordsByTestVector() {
        return tvList;
    }
}
