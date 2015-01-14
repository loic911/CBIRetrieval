/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.properties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kyotocabinet.Cursor;
import kyotocabinet.DB;
import org.apache.log4j.Logger;
import retrieval.server.globaldatabase.GlobalDatabase;
import retrieval.storage.exception.CloseIndexException;
import retrieval.storage.exception.StartIndexException;
/**
 *
 * @author lrollus
 */
public class KyotoCabinetPropertiesIndexSingleFile implements PicturePropertiesIndex{
    /**
     * BDB database object for map and mapreverse
     */
    protected DB map;
    protected String prefix;
    protected String idServer;
    
    /**
     * Number of pictures indexed
     */
    public long numberOfItem;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(KyotoCabinetPropertiesIndexSingleFile.class);

    /**
     * Constructor for a BDB path index
     * @param idStore Id of store
     * @param configStore Configuration object
     * @param read If true, read index (if already exist) else create new index
     * @throws StartIndexException Error during the start of index
     */
    public KyotoCabinetPropertiesIndexSingleFile(GlobalDatabase global,String idServer) throws StartIndexException {

        try {
            logger.info("KyotoCabinetPathIndexSingleFile: start");
            this.idServer = idServer;
            this.map = (DB)global.getDatabaseProperties();
            this.prefix = idServer + "#";

            ///if empty insert first tuple
            logger.info("getSize="+getSize());
            if (getSize() == 0) {
                setCountValue(0);
            }
            logger.info("getSize="+getSize());            
            
        } catch (Exception e) {
            throw new StartIndexException(e.getMessage());
        }
    }
    
   
    
    public int getCountValue() {
       String data = map.get("COUNT#"+idServer);
       if(data==null) {
           return 0;
       }
       int lValue = Integer.parseInt(data);
       return lValue;
    }
    
    public void setCountValue(long value) {
       map.set("COUNT#"+idServer, value+"");
    }    
    
    public void incrCountSize() {
       int value = getCountValue(); 
       value++;
       setCountValue(value);
    }
    
    public void decrCountSize() {
       int value = getCountValue(); 
       value--;
       setCountValue(value);
    }    

    /**
     * Get the size of the map
     * @return Size of the map
     */
    public int getSize() {
        int count = getCountValue();
        if(count==-1) {
            count=0;
        }
        return count;
    }
      
    
    public static Map<String,String> convertStringToMap(String str) {
        System.out.println("1659:"+str);
        if(str==null) return new HashMap<String,String>();
        if(str.equals("NULL") || str.equals("")) return new HashMap<String,String>();
        String[] properties = str.split(";@;");
        System.out.println("propertiesproperties="+str);
        System.out.println("propertiesproperties="+properties.length);
        Map<String,String> map = new HashMap<String,String>();
        for(int i=0;i<properties.length;i=i+2) {
            map.put(properties[i], properties[i+1]);
        }
        return map;
    }
    
    
    public static String convertMapToString(Map<String,String> strMap) {    
        String str = "";
   
        if(strMap==null) {
            return "NULL";
            
        } else if(!strMap.isEmpty()) {
            for(Map.Entry<String,String> entry : strMap.entrySet()) {
                str = str + entry.getKey() + ";@;" + entry.getValue() + ";@;";
            }
            str = str.substring(0, str.length()-3);
        }
        return str;
    }    

    /**
     * Get a map with all pictures
     * @return All pictures map
     */
    public Map<Long, Map<String,String>> getMap() {
        Map<Long, Map<String,String>> hashmap = new HashMap<Long, Map<String,String>>(2048);

        Cursor cur = map.cursor();
        cur.jump();
        String[] rec;
        while ((rec = cur.get_str(true)) != null) {
            if(!rec[0].startsWith("COUNT#") && rec[0].startsWith(prefix)) {
               hashmap.put(Long.parseLong(rec[0].replaceFirst(prefix, "")),convertStringToMap(rec[1])); 
            }                
        }
        cur.disable();

        return hashmap;
    }
    
    /**
     * Get a map with all pictures
     * @return All pictures map
     */
    public List<Long> getIdsList() {
        List<Long> list = new ArrayList<Long>();

        Cursor cur = map.cursor();
        cur.jump();
        String[] rec;
        while ((rec = cur.get_str(true)) != null) {
            if(!rec[0].startsWith("COUNT#") && rec[0].startsWith(prefix)) {
                list.add(Long.parseLong(rec[0].replaceFirst(prefix, "")));
            }
        }
        cur.disable();

        return list;
    }    

    /**
     * Add a new picture path, generate a new id and get the id
     * @param path Picture path
     * @return Picture ID
     */
    public Long addPicture(Long id, Map<String,String> properties) {
        try {
                String propertiesStr = convertMapToString(properties);
                map.set(this.prefix+id,propertiesStr );
                incrCountSize();
                Date date = Calendar.getInstance().getTime();
                logger.info(";" + date.getTime() + ";" + "" + id + ";" + propertiesStr);
                return id;

        } catch (Exception ex) {
            logger.error(ex.toString());
            return -1l;
        }
    }

    /**
     * Get a picture path from the image ID
     * @param imageID Image id
     * @return Picture path
     */
    public Map<String,String> getPictureProperties(Long id) {
        return convertStringToMap(map.get(this.prefix+id));
    }


//    /**
//     * Get a picture id from a picture path
//     * @param imagePath Picture path
//     * @return Picture id
//     */
//    public Integer getPictureId(String imagePath) {
//        String value = map.get(this.prefix+imagePath);
//        if(value!=null){
//           return Integer.parseInt(value);
//        }
//        else{
//            return -1;
//        }
//
//    }
    
//    /**
//     * Check if map contains picture path
//     * @param path Picture path
//     * @return True if map contains picture path, else false
//     */
//    public boolean containsPicture(String path) {
//        return getPictureId(path) != -1;
//    }
    
    public boolean containsPicture(Long id) {
        return map.get(this.prefix+id) != null;
    }
    /**
     * Print index
     */
    public void print() {
        Cursor cur = map.cursor();
        cur.jump();
        String[] rec;
        while ((rec = cur.get_str(true)) != null) {

              logger.info(rec[0].split("#")[1] + "=" + rec[1]);

        }
        cur.disable();
        Cursor cur2 = map.cursor();
        cur2.jump();
        String[] rec2;
        while ((rec2 = cur.get_str(true)) != null) {

              logger.info(rec2[0].split("#")[1] + "=" + rec2[1]);

        }
        cur2.disable();
    }

    /**
     * Delete all path from picturesPath list and get their id
     * @param picturesPath Pictures paths that mus be delete
     * @return Pictures paths deleted id
     */
    public Map<Long, Integer> delete(List<Long> ids) {
        Map<Long, Integer> picturesID = new HashMap<Long, Integer>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            //logger.info("delete: " + ids.get(i));
            System.out.println(map.get(this.prefix+ids.get(i)));
            String value = map.get(this.prefix+ids.get(i));
            if(value!=null) {
                //map.
                logger.info("delete: id=" + ids.get(i));
                //if(id!=null) {
                //    logger.info("delete: " + ids.get(i));
                picturesID.put(ids.get(i), 0);
                map.remove(this.prefix+ids.get(i));
                decrCountSize();                
            }

        }
        return picturesID;
    }

    /**
     * Close index
     * @throws CloseIndexException Exception during the close
     */
    public void close() throws CloseIndexException {
        //closer bdb
        try {
            closeBDB();
        } catch (Exception e) {
            logger.error(e.toString());
            throw new CloseIndexException();
        }
    }

    /** Closes the database. */
    private void closeBDB() throws Exception {
        map.close();
    }

    public void sync() {

    }


//    public int getLastElement() {
//        try {
//            //logger.info("getLastElement()");
//            String value = map.get(this.prefix+"-1");
//            //logger.info("value="+value);
//            //inversedb.put(null, databaseEntryPath, databaseEntryLastId);
//           // Date date = Calendar.getInstance().getTime();
//            if(value!=null)
//            {
//                return Integer.parseInt(value);
//            }
//        }
//        catch (Exception ex) {
//            logger.error(ex.toString());
//        }
//        return -1;
//    }
//
//    public void setLastElement(int i)
//    {
//        //logger.info("setLastElement()");
//        try {
//            map.set(this.prefix+"-1",i+"");
//        }
//        catch (Exception ex) {
//            logger.error(ex.toString());
//        }
//    }    
}
