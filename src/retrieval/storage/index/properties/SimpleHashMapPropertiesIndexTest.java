/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.properties;

import java.util.HashMap;
import retrieval.storage.exception.ReadIndexException;

/**
 *
 * @author lrollus
 */
public class SimpleHashMapPropertiesIndexTest extends  SimpleHashMapPropertiesIndex {
    public SimpleHashMapPropertiesIndexTest(boolean read) throws ReadIndexException {
          super(read);
          HashMap<String,String> map1 = new HashMap<String,String>();
          map1.put("file","1.jpg");
          map.put(1l, map1);
          
          HashMap<String,String> map2 = new HashMap<String,String>();
          map2.put("file","2.jpg"); 
          map2.put("key","value"); 
          map.put(2l, map2);
          
           HashMap<String,String> map3 = new HashMap<String,String>();
           map3.put("file","3.jpg");         
           map.put(3l, map3);

    }

}
