/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server.globaldatabase;

/**
 *
 * @author lrollus
 */
public interface GlobalDatabase {
    
    Object getDatabase();
    
    Object getDatabasePatchs();
    
    Object getDatabasePath();
    
    Object getDatabasePathInverse();
    
    Object getDatabaseCompress();
    
}
