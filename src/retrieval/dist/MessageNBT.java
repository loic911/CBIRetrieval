package retrieval.dist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import org.jdom.*;

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

    /**
     * Constructor for a NBT message
     * @param document XML document
     * @throws NotValidMessageXMLException Bad xml document
     */
    public MessageNBT(Document document) throws NotValidMessageXMLException {
        try {
            Element root = document.getRootElement();
            List listTV = root.getChildren("tv");
            Iterator it = listTV.iterator();
            tvList = new ArrayList<ConcurrentHashMap<String, Long>>(listTV.size());

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
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }

    @Override
    public String toString() {
        String s = "MESSAGE NBT\n";
        for (int i = 0; i < tvList.size(); i++) {
            s = s + "TEST VECTOR " + i;
            s = s + tvList.get(i);
        }
        return s;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        List<ConcurrentHashMap<String, Long>> tvListNewObject = new ArrayList<ConcurrentHashMap<String, Long>>(tvList.size());
        for (int j = 0; j < tvList.size(); j++) {
            ConcurrentHashMap<String, Long> tvm = new ConcurrentHashMap<String, Long>(tvList.get(j).size());
            for (Map.Entry<String, Long> entry : tvList.get(j).entrySet()) {
                tvm.put(entry.getKey(), entry.getValue());
            }
            tvListNewObject.add(tvm);
        }
        MessageNBT clone = new MessageNBT(tvListNewObject);
        return clone;
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
     * Add a entry [visualword;nbt] for test vector t
     * @param t Test vector index
     * @param vw Visual word
     * @param nbt NBT
     */
    public void add(int t, String vw, long nbt) {
        tvList.get(t).put(vw, nbt);
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
     * Set the list with each tests vectors visual words
     * ex: Item 2 is a map with each vw and nbt for test vector 2
     * @param visualWords Vector with each tests vector Visual words
     */
    public void setVisualWordsByTestVector(List<ConcurrentHashMap<String, Long>> visualWords) {
        tvList = visualWords;
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
