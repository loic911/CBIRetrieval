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

    public MultiServerMessageNBT(Map<String,List<ConcurrentHashMap<String, Long>>> tvLists,List<String> containers) {
        this.tvLists = tvLists;
        this.containers=containers;
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

    @Override
    public String toString() {
        String s = "MESSAGE SUPER SERVER NBT\n";

        Iterator<Entry<String,List<ConcurrentHashMap<String, Long>>>> it = tvLists.entrySet().iterator();
        s = s + "Search on server " + getContainers() + "\n";
        while(it.hasNext()) {
            Entry<String,List<ConcurrentHashMap<String, Long>>> entry = it.next();
            String idServer = entry.getKey();
            s = s + "SERVER " + idServer +"\n";
            List<ConcurrentHashMap<String, Long>> tvList = entry.getValue();

            for (int i = 0; i < tvList.size(); i++) {
                s = s + "TEST VECTOR " + i;
                s = s + tvList.get(i);
            }
        }
        return s;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Map<String,List<ConcurrentHashMap<String, Long>>> map = new TreeMap<String,List<ConcurrentHashMap<String, Long>>>();

        Iterator<Entry<String,List<ConcurrentHashMap<String, Long>>>> it = tvLists.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,List<ConcurrentHashMap<String, Long>>> entry = it.next();
            String idServer = entry.getKey();
            List<ConcurrentHashMap<String, Long>> tvList = entry.getValue();
            List<ConcurrentHashMap<String, Long>> tvListNewObject = new ArrayList<ConcurrentHashMap<String, Long>>(tvList.size());

            for (int j = 0; j < tvList.size(); j++) {
                ConcurrentHashMap<String, Long> tvm = new ConcurrentHashMap<String, Long>(tvList.get(j).size());
                for (Map.Entry<String, Long> entrySub : tvList.get(j).entrySet()) {
                    tvm.put(entrySub.getKey(), entrySub.getValue());
                }
                tvListNewObject.add(tvm);
            }

            map.put(idServer, tvListNewObject);
        }
        MultiServerMessageNBT clone = new MultiServerMessageNBT(map,CollectionUtils.cloneStringList(getContainers()));
        return clone;
    }

    public MultiServerMessageNBT copyWithoutValue() throws CloneNotSupportedException {
        Map<String,List<ConcurrentHashMap<String, Long>>> map = new TreeMap<String,List<ConcurrentHashMap<String, Long>>>();

        Iterator<Entry<String,List<ConcurrentHashMap<String, Long>>>> it = tvLists.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,List<ConcurrentHashMap<String, Long>>> entry = it.next();
            String idServer = entry.getKey();
            List<ConcurrentHashMap<String, Long>> tvList = entry.getValue();
            List<ConcurrentHashMap<String, Long>> tvListNewObject = new ArrayList<ConcurrentHashMap<String, Long>>(tvList.size());

            for (int j = 0; j < tvList.size(); j++) {
                ConcurrentHashMap<String, Long> tvm = new ConcurrentHashMap<String, Long>(tvList.get(j).size());
                for (Map.Entry<String, Long> entrySub : tvList.get(j).entrySet()) {
                    tvm.put(entrySub.getKey(), 0L);
                }
                tvListNewObject.add(tvm);
            }

            map.put(idServer, tvListNewObject);

        }
        MultiServerMessageNBT clone = new MultiServerMessageNBT(map,CollectionUtils.cloneStringList(getContainers()));
        return clone;
    }

    /**
     * Add a entry [visualword;nbt] for test vector t
     * @param t Test vector index
     * @param vw Visual word
     * @param nbt NBT
     */
    public void add(String server,int t, String vw, long nbt) {
        List<ConcurrentHashMap<String, Long>> tvList = tvLists.get(server);
        tvList.get(t).put(vw, nbt);
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

    /**
     * @param containers the containers to set
     */
    public void setContainers(List<String> containers) {
        this.containers = containers;
    }
}
