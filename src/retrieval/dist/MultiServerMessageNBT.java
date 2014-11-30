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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import retrieval.utils.CollectionUtils;

/**
 * @author Rollus Loic
 */
public class MultiServerMessageNBT implements Message, Cloneable {
    private static Logger logger = Logger.getLogger(MultiServerMessageNBT.class);
    /**
     * Each Server has a list of map which contains for each tests vector a map with
     * visual word and nbt (0 if message from central server).
     * Ex:
     * SERVER1
     *  VT1
     *   654843 - 45 ; 89974 - 36 ; 13214 - 32
     *  VT2
     *  ...
     * SERVER2
     *  VT1
     *   ...
     */
    private Map<String,List<ConcurrentHashMap<String, Long>>> tvLists;
    private List<String> containers;

    public MultiServerMessageNBT(Map<String,List<ConcurrentHashMap<String, Long>>> tvLists,String[] containers) {
        this.tvLists = tvLists;
        this.containers=Arrays.asList(containers);
    }


    /**
     * Constructor for a NBT message
     * @param document XML document
     * @throws NotValidMessageXMLException Bad xml document
     */
    public MultiServerMessageNBT(Document document) throws NotValidMessageXMLException {
        try {
            Element root = document.getRootElement();
            containers = CollectionUtils.split(root.getAttributeValue("container"), ",");

            List listServer = root.getChildren("server");
            Iterator itServer = listServer.iterator();
            tvLists = new TreeMap<String,List<ConcurrentHashMap<String, Long>>>();

            while (itServer.hasNext()) {
                Element serverNBT = (Element) itServer.next();
                String idServer = serverNBT.getAttributeValue("id");
                List listTV = serverNBT.getChildren("tv");
                Iterator it = listTV.iterator();
                List<ConcurrentHashMap<String, Long>> tvList = new ArrayList<ConcurrentHashMap<String, Long>>(listTV.size());

                while (it.hasNext()) {
                    Element tvxml = (Element) it.next();
                    List listVW = tvxml.getChildren();
                    Iterator it2 = listVW.iterator();
                    ConcurrentHashMap<String, Long> tv =
                            new ConcurrentHashMap<String, Long>();
                    while (it2.hasNext()) {
                        Element assoc = (Element) it2.next();
                        String vw = assoc.getAttributeValue("b");
                        Long nbit = Long.parseLong(assoc.getAttributeValue("nbit"));
                        tv.put(vw, nbit);
                    }
                    tvList.add(tv);
                }
                tvLists.put(idServer, tvList);
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
        racine.setAttribute("container",CollectionUtils.join(getContainers(), ","));
        racine.setAttribute("type","SEARCH1");
        document = new Document(racine);

       Iterator<Entry<String,List<ConcurrentHashMap<String, Long>>>> it = tvLists.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,List<ConcurrentHashMap<String, Long>>> entry = it.next();
            String server = entry.getKey();
            List<ConcurrentHashMap<String, Long>> tvList = entry.getValue();

            Element racineServer = new Element("server");
            racineServer.setAttribute("id", server);

            for (int i = 0; i < tvList.size(); i++) {
                ConcurrentHashMap<String, Long> tv = tvList.get(i);
                Element tvxml = new Element("tv");
                tvxml.setAttribute(new Attribute("id", i + ""));
                racineServer.addContent(tvxml);

                for (Map.Entry<String, Long> entree : tv.entrySet()) {
                    Element vw = new Element("vw");
                    vw.setAttribute(new Attribute("b", entree.getKey()));
                    vw.setAttribute(new Attribute("nbit", entree.getValue().intValue() + ""));
                    tvxml.addContent(vw);
                }
            }
            racine.addContent(racineServer);
        }
        } catch(Exception e) {
            logger.error(e.toString());
        }
        return document;
    }

    /**
     * Set the list with each tests vectors visual words
     * ex: Item 2 is a map with each vw and nbt for test vector 2
     * @param visualWords Vector with each tests vector Visual words
     */
    public void setVisualWordsByTestVectorServer(
            Map<String,List<ConcurrentHashMap<String, Long>>> visualWords) {
        tvLists = visualWords;
    }

    /**
     * Get the list with each tests vectors visual words
     * ex: Item 2 is a map with each vw and nbt for test vector 2
     * @return Vector with each tests vector Visual words
     */
    public Map<String,List<ConcurrentHashMap<String, Long>>> getVisualWordsByTestVectorServer() {
        return tvLists;
    }


    public static List<ConcurrentHashMap<String, Long>> copyVWList(List<ConcurrentHashMap<String, Long>> baseList) {
        List<ConcurrentHashMap<String, Long>> newList = new ArrayList<ConcurrentHashMap<String, Long>>();

        Iterator<ConcurrentHashMap<String, Long>> it = baseList.iterator();

        while (it.hasNext()) {
            ConcurrentHashMap<String, Long> map = it.next();
            ConcurrentHashMap<String, Long> newMap = copyVWMap(map);
            newList.add(newMap);
        }
        return newList;
    }

    public static ConcurrentHashMap<String, Long> copyVWMap(ConcurrentHashMap<String, Long> baseMap) {
        ConcurrentHashMap<String, Long> copyMap = new ConcurrentHashMap<String, Long>(baseMap.size());
        Iterator<Entry<String, Long>> it = baseMap.entrySet().iterator();

        while (it.hasNext()) {
            Entry<String, Long> entry = it.next();

            copyMap.put(entry.getKey(), new Long(entry.getValue().longValue()));
        }
        return copyMap;
    }

    /**
     * @return the containers
     */
    public List<String> getContainers() {
        return containers;
    }
}
