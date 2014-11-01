package retrieval.dist;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Message XML for similarities
 * A central server will send information of request picture to each server.
 * @author Rollus Loic
 */
public class MessageSimilarities implements Message,Cloneable {

    /**
     * A structure which contain for each test vectors a map with
     * visualword B - nBiq - nBtsum where nBiq is the number of patch of Iq
     * that produced visualword B and nBtsum is the total number of patchs
     * map on all server with visual word B (for this tests vector)
     */
    private List<ConcurrentHashMap<String, RequestPictureVisualWord>> tvList;
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
    public MessageSimilarities(List<ConcurrentHashMap<String, Long>> lists, int N, int k) {
        this.N = N;
        this.k = k;
        tvList = new ArrayList<ConcurrentHashMap<String, RequestPictureVisualWord>>(lists.size());
        for (int i = 0; i < lists.size(); i++) {
            tvList.add(new ConcurrentHashMap<String, RequestPictureVisualWord>(N));
            for (Map.Entry<String, Long> entry : lists.get(i).entrySet()) {
                tvList.get(i).put(entry.getKey(), new RequestPictureVisualWord(entry.getValue().intValue()));
            }
        }
    }

    /**
     * Constructor for a similarities message
     * @param doc XML message similarities
     * @throws NotValidMessageXMLException Not a valid message
     */
    public MessageSimilarities(Document doc) throws NotValidMessageXMLException {
        try {
            Element root = doc.getRootElement();
            N = Integer.parseInt(root.getAttributeValue("N"));
            k = Integer.parseInt(root.getAttributeValue("k"));
            List listTV = root.getChildren("tv");
            Iterator it = listTV.iterator();
            tvList = new ArrayList<ConcurrentHashMap<String, RequestPictureVisualWord>>(listTV.size());
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
        } catch (Exception e) {
            throw new NotValidMessageXMLException(e.toString());
        }
    }


    /**
     * Method to build XML document from this message
     * @return XML document
     */
    public Document toXML() throws Exception {
        Document document = null;

        Element racine = new Element("MessageSim");
        racine.setAttribute("N", N + "");
        racine.setAttribute("k", k + "");
        document = new Document(racine);

        for (int i = 0; i < tvList.size(); i++) {

            ConcurrentHashMap<String, RequestPictureVisualWord> tv = tvList.get(i);
            Element tvxml = new Element("tv");
            tvxml.setAttribute(new Attribute("id", i + ""));
            racine.addContent(tvxml);

            for (Map.Entry<String, RequestPictureVisualWord> entry : tv.entrySet()) {

                RequestPictureVisualWord tvm2 = entry.getValue();
                Element vw = new Element("vw");
                vw.setAttribute(new Attribute("b", entry.getKey()));
                vw.setAttribute(new Attribute("nbiq", tvm2.nbiq + ""));
                vw.setAttribute(new Attribute("nbt", tvm2.nbtSum + ""));
                tvxml.addContent(vw);
            }

        }
        return document;
    }


    @Override
    public String toString() {
        String s = "";
        s+="N="+ N + "\n";
        s+="k="+ k + "\n";
        for (int i = 0; i < tvList.size(); i++) {
            s+="TEST VECTOR "+i+"\n";
            ConcurrentHashMap<String, RequestPictureVisualWord> tv = tvList.get(i);
            for (Map.Entry<String, RequestPictureVisualWord> entry : tv.entrySet()) {
                RequestPictureVisualWord tvm2 = entry.getValue();
                Element vw = new Element("vw");
                vw.setAttribute(new Attribute("b", entry.getKey()));
                vw.setAttribute(new Attribute("nbiq", tvm2.nbiq + ""));
                vw.setAttribute(new Attribute("nbt", tvm2.nbtSum + ""));
                s+="b=" + entry.getKey() + "| nbiq="+ tvm2.nbiq + "| nbt=" + tvm2.nbtSum +"\n";
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
    public List<ConcurrentHashMap<String, RequestPictureVisualWord>> getVisualWord() {
        return tvList;
    }

    /**
     * Set the visual word map for each tests vector
     * @param vw Visual word map for each tests vector
     */
    public void setVisualWords(List<ConcurrentHashMap<String, RequestPictureVisualWord>> vw) {
        this.tvList = vw;
    }

    /**
     * Add a new message NBT (from server s) to produce this message.
     * This will add NBT of s for every visual word of each tests vector
     * Synchronized because central server talk to each server
     * on a specific thread
     * @param messageNBT Message NBT
     */
    public synchronized void addNBT(Document messageNBT) {

        Element root = messageNBT.getRootElement();
        List listTV = root.getChildren("tv");
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

    /**
     * Add a new message NBT (from server s) to produce this message.
     * This will add NBT of s for every visual word of each tests vector
     * Synchronized because central server talk to each server
     * on a specific thread
     * @param messageNBT Message NBT
     */
    public synchronized void addNBT(List<ConcurrentHashMap<String,Long>> lists) {

        Iterator it = lists.iterator();
        while (it.hasNext()) {
            ConcurrentHashMap<String,Long> testElem = (ConcurrentHashMap<String,Long>) it.next();
            Enumeration<String> it2 = testElem.keys();
            int i = 0;
            while (it2.hasMoreElements()) {
                String b = it2.nextElement();
                long nbit = testElem.get(b);
                
                RequestPictureVisualWord item = tvList.get(i).get(b);
                //add the NBT of server s to the total NBT
                item.addNbtSum((int)nbit);
                tvList.get(i).put(b, item);

            }
            i++;
        }
    }
}
