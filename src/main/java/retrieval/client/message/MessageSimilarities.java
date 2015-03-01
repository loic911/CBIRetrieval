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

import org.jdom.Document;
import org.jdom.Element;
import retrieval.dist.RequestPictureVisualWord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Message XML for similarities
 * A central server will send information of request picture to each server.
 * @author Rollus Loic
 */
public class MessageSimilarities implements Cloneable {

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
                //System.out.println("tvList="+tvList);
                RequestPictureVisualWord item = tvList.get(i).get(vw);
                //System.out.println("vw="+vw);
                //add the NBT of server s to the total NBT
                item.addNbtSum(nbit);
                tvList.get(i).put(vw, item);

            }
            i++;
        }
    }

}
