/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.index.patchs;

import retrieval.storage.exception.ReadIndexException;

/**
 *
 * @author lrollus
 */
public class SimpleHashMapPatchsIndexTest extends SimpleHashMapPatchsIndex {
    public SimpleHashMapPatchsIndexTest(boolean read) throws ReadIndexException {
        super(read);
        map.put(1l, 10);
        map.put(2l, 10);
        map.put(3l, 10);
    }
}
