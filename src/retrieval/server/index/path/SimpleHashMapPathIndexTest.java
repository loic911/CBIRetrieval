/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.server.index.path;

import java.util.HashMap;
import retrieval.server.exception.ReadIndexException;

/**
 *
 * @author lrollus
 */
public class SimpleHashMapPathIndexTest extends  SimpleHashMapPathIndex {
    public SimpleHashMapPathIndexTest(boolean read) throws ReadIndexException {
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
