package retrieval.dist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import retrieval.utils.CollectionUtils;

/**
 * Message XML for similarities
 * A central server will send information of request picture to each server.
 * @author Rollus Loic
 */
public class MultiServerMessageSimilarities implements Message,Cloneable {

    /**
     * A structure which contain for each test vectors a map with
     * visualword B - nBiq - nBtsum where nBiq is the number of patch of Iq
     * that produced visualword B and nBtsum is the total number of patchs
     * map on all server with visual word B (for this tests vector)
     */
    private Map<String,List<ConcurrentHashMap<String, RequestPictureVisualWord>>> tvLists;
    private List<String> containers;
    /**
     * Number of patch produced by request image Iq
     */
    private int N;
    /**
     * Max number of similar pictures to get
     */
    private int k;

    /**
     * Constructor for a similarities message
     * Use to build this message with information from a MessageNBT
     * @param lists A list of map which contains for each tests vector
     * a map with visual word and nbt (0 if message from central server).
     * @param N Number of patch produced by request image Iq
     * @param k Max number of similar pictures to get
     */
    public MultiServerMessageSimilarities(Map<String,List<ConcurrentHashMap<String, Long>>> lists, int N, int k,List<String> containers) {
        this.N = N;
        this.k = k;
        this.containers = containers;
        tvLists = new TreeMap<String,List<ConcurrentHashMap<String, RequestPictureVisualWord>>>();

        Iterator<Entry<String,List<ConcurrentHashMap<String, Long>>>> it = lists.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,List<ConcurrentHashMap<String, Long>>> entry = it.next();
            String server = entry.getKey();
            List<ConcurrentHashMap<String, Long>> tvListLong = entry.getValue();
            List<ConcurrentHashMap<String, RequestPictureVisualWord>> tvListRequestPictureVisualWord =
                    new ArrayList<ConcurrentHashMap<String, RequestPictureVisualWord>>();

            for (int i = 0; i < tvListLong.size(); i++) {
                tvListRequestPictureVisualWord.add(new ConcurrentHashMap<String, RequestPictureVisualWord>(N));
                for (Map.Entry<String, Long> entry2 : tvListLong.get(i).entrySet()) {
                    tvListRequestPictureVisualWord.get(i).put(entry2.getKey(), new RequestPictureVisualWord(entry2.getValue().intValue()));
                }
            }
            tvLists.put(server, tvListRequestPictureVisualWord);
        }
    }

    public MultiServerMessageSimilarities(Map<String,List<ConcurrentHashMap<String, Long>>> lists, int N, int k,String[] containers) {
        this(lists,N,k,Arrays.asList(containers));
    }

    /**
     * Constructor for a similarities message
     * @param doc XML message similarities
     * @throws NotValidMessageXMLException Not a valid message
     */
    public MultiServerMessageSimilarities(Document document) throws NotValidMessageXMLException {
        try {
            Element root = document.getRootElement();
            containers = CollectionUtils.split(root.getAttributeValue("container"), ",");
            N = Integer.parseInt(root.getAttributeValue("N"));
            k = Integer.parseInt(root.getAttributeValue("k"));            
            List listServer = root.getChildren("server");
            Iterator itServer = listServer.iterator();
            tvLists = new TreeMap<String,List<ConcurrentHashMap<String, RequestPictureVisualWord>>>();

            while (itServer.hasNext()) {
                Element serverSim = (Element) itServer.next();
                String idServer = serverSim.getAttributeValue("id");
                List listTV = serverSim.getChildren("tv");

                Iterator it = listTV.iterator();
                List<ConcurrentHashMap<String, RequestPictureVisualWord>> tvList =
                        new ArrayList<ConcurrentHashMap<String, RequestPictureVisualWord>>(listTV.size());

                while (it.hasNext()) {

                    ConcurrentHashMap<String, RequestPictureVisualWord> tvm = new ConcurrentHashMap<String, RequestPictureVisualWord>();
                    Element tvxml = (Element) it.next();
                    List listVW = tvxml.getChildren();
                    Iterator it2 = listVW.iterator();

                    while (it2.hasNext()) {
                        Element assoc = (Element) it2.next();
                        String vw = assoc.getAttributeValue("b");
                        int nbiq = Integer.parseInt(assoc.getAttributeValue("nbiq"));
                        int nbt = Integer.parseInt(assoc.getAttributeValue("nbt"));
                        tvm.put(vw, new RequestPictureVisualWord(nbiq, nbt));
                    }
                    tvList.add(tvm);
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
    public Document toXML() throws Exception {
        Element racine = new Element("MultiServerMessage");
        racine.setAttribute("container",CollectionUtils.join(getContainers(), ","));
        racine.setAttribute("type","SEARCH2");
        racine.setAttribute("N", N + "");
        racine.setAttribute("k", k + "");
        Document document = new Document(racine);

        Iterator<Entry<String,List<ConcurrentHashMap<String, RequestPictureVisualWord>>>> it = tvLists.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,List<ConcurrentHashMap<String, RequestPictureVisualWord>>> entry = it.next();
            String server = entry.getKey();
            List<ConcurrentHashMap<String, RequestPictureVisualWord>> tvList = entry.getValue();

            Element racineServer = new Element("server");
            racineServer.setAttribute("id", server);

            for (int i = 0; i < tvList.size(); i++) {

                ConcurrentHashMap<String, RequestPictureVisualWord> tv = tvList.get(i);
                Element tvxml = new Element("tv");
                tvxml.setAttribute(new Attribute("id", i + ""));
                racineServer.addContent(tvxml);

                for (Map.Entry<String, RequestPictureVisualWord> entry2 : tv.entrySet()) {

                    RequestPictureVisualWord tvm2 = entry2.getValue();
                    Element vw = new Element("vw");
                    vw.setAttribute(new Attribute("b", entry2.getKey()));
                    vw.setAttribute(new Attribute("nbiq", tvm2.nbiq + ""));
                    vw.setAttribute(new Attribute("nbt", tvm2.nbtSum + ""));
                    tvxml.addContent(vw);
                }
            }
            racine.addContent(racineServer);
        }
        return document;
    }


    @Override
    public String toString() {
        String s = "";
        s = s + "Search on server " + getContainers() + "\n";
        s+="N="+ N + "\n";
        s+="k="+ k + "\n";
        Iterator<Entry<String,List<ConcurrentHashMap<String, RequestPictureVisualWord>>>> it = tvLists.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String,List<ConcurrentHashMap<String, RequestPictureVisualWord>>> entry = it.next();
            String server = entry.getKey();
            List<ConcurrentHashMap<String, RequestPictureVisualWord>> tvList = entry.getValue();
            s+="SERVER  "+server+"\n";
            for (int i = 0; i < tvList.size(); i++) {
                s+="TEST VECTOR "+i+"\n";
                ConcurrentHashMap<String, RequestPictureVisualWord> tv = tvList.get(i);
                for (Map.Entry<String, RequestPictureVisualWord> entry2 : tv.entrySet()) {
                    RequestPictureVisualWord tvm2 = entry2.getValue();
                    Element vw = new Element("vw");
                    vw.setAttribute(new Attribute("b", entry2.getKey()));
                    vw.setAttribute(new Attribute("nbiq", tvm2.nbiq + ""));
                    vw.setAttribute(new Attribute("nbt", tvm2.nbtSum + ""));
                    s+="b=" + entry2.getKey() + "| nbiq="+ tvm2.nbiq + "| nbt=" + tvm2.nbtSum +"\n";
                }

            }
        }
        return s;
    }


    /**
     * Get the number of patch for Iq
     * @return N
     */
    public int getNiq() {
        return N;
    }

    /**
     * Get the top-k similar pictures asked
     * @return k
     */
    public int getK() {
        return k;
    }

    /**
     * Get the visual word map for each tests vector
     * @return Visual word map for each tests vector
     */
    public Map<String,List<ConcurrentHashMap<String, RequestPictureVisualWord>>> getVisualWord() {
        return tvLists;
    }

    /**
     * Set the visual word map for each tests vector
     * @param vw Visual word map for each tests vector
     */
    public void setVisualWords(Map<String,List<ConcurrentHashMap<String, RequestPictureVisualWord>>> vw) {
        this.tvLists = vw;
    }

    /**
     * Add a new message NBT (from server s) to produce this message.
     * This will add NBT of s for every visual word of each tests vector
     * Synchronized because central server talk to each server
     * on a specific thread
     * @param messageNBT Message NBT
     */
    public synchronized void addNBT(Document messageNBT) throws Exception {
        try {
        Element root = messageNBT.getRootElement();
        List listServer = root.getChildren("server");
        Iterator itServer = listServer.iterator();

        while (itServer.hasNext()) {
            Element serverxml = (Element) itServer.next();
            String server = serverxml.getAttributeValue("id");
            List listTV = serverxml.getChildren("tv");

            List<ConcurrentHashMap<String, RequestPictureVisualWord>> tvList = tvLists.get(server);


            //if not exist for this server, create a new list with empty value
            if(tvList==null) {
                tvList = new ArrayList<ConcurrentHashMap<String, RequestPictureVisualWord>> ();

                List<ConcurrentHashMap<String, RequestPictureVisualWord>> base = tvLists.get("#all#");

                for(int i=0;i<base.size();i++) {
                    ConcurrentHashMap<String, RequestPictureVisualWord> baseMap = base.get(i);
                    ConcurrentHashMap<String, RequestPictureVisualWord> newMap = new ConcurrentHashMap<String, RequestPictureVisualWord>();

                    Iterator<Entry<String,RequestPictureVisualWord>> it = baseMap.entrySet().iterator();

                    while(it.hasNext()) {
                        Entry<String,RequestPictureVisualWord> entry = it.next();
                        RequestPictureVisualWord value = entry.getValue();
                        newMap.put(entry.getKey(), new RequestPictureVisualWord(value.nbiq,value.nbtSum));
                    }
                    tvList.add(newMap);
                }
                tvLists.put(server,tvList);
            }

            int i = 0;
            Iterator it = listTV.iterator();

            while (it.hasNext()) {
                Element tvxml = (Element) it.next();
                List listVW = tvxml.getChildren();
                Iterator it2 = listVW.iterator();
                while (it2.hasNext()) {

                    Element assoc = (Element) it2.next();
                    String vw = assoc.getAttributeValue("b");
                    int nbit = Integer.parseInt(assoc.getAttributeValue("nbit"));
                    
                    

                    RequestPictureVisualWord item = tvList.get(i).get(vw);
                    //add the NBT of server s to the total NBT
                    item.addNbtSum(nbit);
                    tvList.get(i).put(vw, item);

                }
                i++;
            }
        }
        }catch(Exception e) {
            throw e;
        }
    }

    /**
     * Add a new message NBT (from server s) to produce this message.
     * This will add NBT of s for every visual word of each tests vector
     * Synchronized because central server talk to each server
     * on a specific thread
     * @param messageNBT Message NBT
     */
    public synchronized void addNBT(Map<String,List<ConcurrentHashMap<String,Long>>> lists) {

        Iterator<Entry<String,List<ConcurrentHashMap<String,Long>>>> it1 = lists.entrySet().iterator();

        while (it1.hasNext()) {
            Entry<String,List<ConcurrentHashMap<String,Long>>> entry1 = it1.next();
            String server = entry1.getKey();
            List<ConcurrentHashMap<String,Long>> tvList = entry1.getValue();

            Iterator<ConcurrentHashMap<String,Long>> it2 = tvList.iterator();
            while(it2.hasNext()) {
                ConcurrentHashMap<String,Long> testElem = it2.next();
                Enumeration<String> it3 = testElem.keys();
                int i = 0;
                while (it3.hasMoreElements()) {
                    String b = it3.nextElement();
                    long nbit = testElem.get(b);

                    RequestPictureVisualWord item = tvLists.get(server).get(i).get(b);
                    //add the NBT of server s to the total NBT
                    item.addNbtSum((int)nbit);
                    tvLists.get(server).get(i).put(b, item);
                }
                i++;

            }
        }
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
