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
package retrieval.storage.index;

import retrieval.config.ConfigServer;

import java.io.Serializable;
import java.util.*;

/**
 * This class implements a value structure for the index
 * A value structure is map with a visual word B and contains entry
 * [I,NIBT] for each image I that generate NIBT (!=0) patchs which produced B
 * @author Rollus Loic
 **/
public class ValueStructure implements Serializable {

    /**
     * Total NBT (sum of all NIBT for each I)
     */
    private long Nbt;
    /**
     * Struct with map I and NIBT
     */
    private Map<Long, Integer> valList;
    /**
     * ONLY USED IN COMPRESSION MODE
     * Indicated that structure is full
     */
    boolean flagFull = false;

    /**
     * Configuration object
     */
    /**
     * Construct a value structure
     * @param config Configuration object
     **/
    public ValueStructure(ConfigServer config) {
        Nbt = 0;

        if (config.getStrucType() == 0) {
            valList = new HashMap<Long, Integer>(config.getHashMapStartSize());
        } else {
            valList = new TreeMap<Long, Integer>();
        }
    }

    public ValueStructure(ConfigServer config, int numberOfItemPrevision, long NBT) {

        if (config.getStrucType() == 0) {
            this.valList = new HashMap<Long, Integer>(numberOfItemPrevision * 3);
        }
        else {
            this.valList = new TreeMap<Long, Integer>();
        }

        Nbt = NBT;
    }

    public ValueStructure(ConfigServer config, Map<String, String> mapList, long NBT) {

        if (config.getStrucType() == 0) {
            this.valList = new HashMap<Long, Integer>(mapList.size() * 3);
        }
        else {
            this.valList = new TreeMap<Long, Integer>();
        }

        Nbt = NBT;
        Set<Map.Entry<String, String>> set = mapList.entrySet();

        Iterator<java.util.Map.Entry<String, String>> iterator = set.iterator();
        while (iterator.hasNext()) {
            java.util.Map.Entry<String, String> entry = iterator.next();
            this.valList.put(Long.parseLong(entry.getKey()), (int) Long.parseLong((entry.getValue())));
        }
    }

    /**
     * Delete all item from mapID map (key) in the valueStructure
     * @param mapID Map with picture id as key
     */
    public void deleteValue(Map<Long, Integer> mapID) {
        List<Long> emptyKey = new ArrayList<Long>();
        for (Map.Entry<Long, Integer> entry : valList.entrySet()) {
            Long key = entry.getKey();
            if (mapID.containsKey(key)) {
                emptyKey.add(key);
            }
        }

        for(int i=0;i<emptyKey.size();i++) {
            Long key = emptyKey.get(i);
            Integer value = valList.get(key);
            Nbt = Nbt - value;
            valList.remove(key);
        }

    }

    /**
     * Return a String representation of the value
     * @return String reprsentation of this
     **/
    @Override
    public String toString() {
        return "NBT= " + Nbt + " valList=" + valList;
    }

    /**
     * Add new entry for image I in this (collection for visual word B),
     * If already exist, increment by NIBT.
     * May occurs a compression of map (configuration option)
     * @param I Image I
     * @param NIBT Number of patchs produced by I for visual word B
     **/
    public boolean addEntry(long I, int NIBT) {
        Integer i = valList.get(I);
        if (i != null) {
            Nbt = Nbt + NIBT;
            valList.put(I, new Integer(i.intValue() + NIBT));
        } else {
            Nbt = Nbt + NIBT;
            valList.put(I, new Integer(NIBT));
        }
        return true;
    }

    public void addEntryWithoutNBT(long I, int NIBT) {
        valList.put(I, new Integer(NIBT));
    }

    public boolean isPicturePresent(Long id) {
        return valList.containsKey(id);
    }

    /**
     * Get the NBIT number of the image I
     * @param I Image I
     * @return Occurence number
     **/
    public int getNBIT(Long I) {
        Integer l = valList.get(I);
        if (l != null) {
            return l.intValue();
        } else {
            return 0;
        }
    }

    /**
     * Get the ValList structure
     * @return Val List
     **/
    public Map<Long, Integer> getEntries() {
        return valList;
    }

    /**
     * Get the total number of VisualWord map with this structure
     * @return NUmber of VisualWord
     **/
    public long getNBT() {
        return Nbt;
    }
}
