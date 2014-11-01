/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.utils;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import retrieval.config.ConfigServer;
import retrieval.server.index.ValueStructure;
import retrieval.utils.Value.EntryInfo;
import retrieval.utils.Value.ValueInfo;
/**
 * Util class use to convert data
 * @author Rollus Loïc
 */
public class ConvertUtils {

   /**
     * Convert a Bool String as a Hexa String
     * @param bool Bool string
     * @return Hexa string
     */
    public static String convertHexa(String bool) {
        return Integer.toString(Integer.parseInt(bool, 2), 16);
    }

    /**
     *
     * Convert a ValueStructure to a Protocol Buffer object
     * (lighter and faster serialization)
     * @param vs ValueStructure
     * @return Protocol Buffer Object
     */
    public static ValueInfo convertObjectToProtoBuf(ValueStructure vs) {

        Map<Long, Integer> entries = vs.getEntries();
        Iterator<Map.Entry<Long,Integer>> it2 = entries.entrySet().iterator();

        List<EntryInfo> entryList = new ArrayList<EntryInfo>(entries.size());
        while (it2.hasNext()) {
           Map.Entry<Long,Integer> elem = it2.next();
           entryList.add(EntryInfo.newBuilder().setIdImage(elem.getKey()).setOccNumber(elem.getValue()).build());
        }
        return ValueInfo.newBuilder().setNbt(vs.getNBT()).addAllImageSet(entryList).build();

    }

    /**
     * Convert a Protocol Buffer Object to a ValueSTructure
     * @param value Protocol Buffer Object
     * @return ValueStructure
     */
    public static ValueStructure convertProtoBufToValueStructure(ValueInfo value, ConfigServer configStore) {
        long nbt = value.getNbt();
        ValueStructure valueStructure = new ValueStructure(configStore, value.getImageSetCount(), nbt);
        for (EntryInfo entry : value.getImageSetList()) {
            valueStructure.addEntryWithoutNBT(entry.getIdImage(),entry.getOccNumber());
        }
        return valueStructure;
    }



    public static byte[] convertValueStructureToByteArray(ValueStructure vs) {
        try {
            // Serialize to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(vs);
            out.close();
            // Get the bytes of the serialized object
            byte[] buf = bos.toByteArray();
            return buf;
        } catch (IOException ex) {
            Logger.getLogger(ConvertUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    public static ValueStructure convertByteArrayToValueStructure(byte[] value) {
        try {
            // see Reading a File into a Byte Array for the implementation of this method
            // Deserialize from a byte array
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(value));
            ValueStructure valueStruct = (ValueStructure) in.readObject();
            in.close();
            return valueStruct;
        } catch (IOException ex) {
            Logger.getLogger(ConvertUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConvertUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
