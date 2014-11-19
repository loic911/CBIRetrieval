package retrieval.storage.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import retrieval.config.ConfigServer;
import retrieval.storage.exception.ReadIndexException;

/**
 * This class implement a set of pictures that has been deleted from server but
 * that already exists on server index. Server must be purge to delete all these data.
 * @author lrollus
 */
public class PicturesToPurge {
    
    private static Logger logger = Logger.getLogger(PicturesToPurge.class);
    
    private Map<Long, Integer> mustBePurge;
    private ConfigServer config = null;
    private String idServer;
    
    
    public PicturesToPurge(String idServer,ConfigServer config,Map<Long, Integer> mustBePurge) {
        this.idServer = idServer;
        this.config = config;
        this.mustBePurge = mustBePurge;
    }

    public int size() {
        return mustBePurge.size();
    }
    
    public void putToPurge(Map<Long, Integer> toPurge) {
        mustBePurge.putAll(toPurge);
        writePurgeIndex();
    }
    
    public Map<Long, Integer> getPicturesToPurge() {
        return mustBePurge;
    }
    
    public void clear() {
        mustBePurge.clear();
        writePurgeIndex();        
    }

    private void writePurgeIndex()  {
        if(config.getStoreName().equals("MEMORY")) {
            return;
        }
        logger.info("writePurgeIndex " +config.getIndexPath() +"/purge/purge"+idServer+".ser");
        FileOutputStream fos;
        ObjectOutputStream out;
        try
        {
            File dir = new File(config.getIndexPath() +"/purge");
            if(!dir.exists()) {
                dir.mkdirs();
            }
            fos = new FileOutputStream(config.getIndexPath() +"/purge/purge"+idServer+".ser");
            out = new ObjectOutputStream(fos);
            out.writeObject(mustBePurge);
            out.close();
        }
        catch(IOException ex)
        {
            logger.error(ex.toString());
        }
    }

    public static PicturesToPurge readPurgeIndex(String idServer,ConfigServer config) throws ReadIndexException {
       if(config.getStoreName().equals("MEMORY")) {
           return new PicturesToPurge(idServer,config,new HashMap<Long, Integer>());
       }
       String path = config.getIndexPath() +"/purge/purge"+idServer+".ser";
       logger.info("readPurgeIndex " +path);
       if(!new File(path).exists()) {
           return new PicturesToPurge(idServer,config,new HashMap<Long, Integer>());
       }

       try
       {
         FileInputStream fis = new FileInputStream(path);
         ObjectInputStream in = new ObjectInputStream(fis);
         Map<Long, Integer>  mustBePurge = (HashMap<Long, Integer>)in.readObject();
         in.close();
         return new PicturesToPurge(idServer,config,mustBePurge);
       }
       catch(Exception ex)
       {
         throw new ReadIndexException("Purge index:"+ex);
      }
    }
    
}
