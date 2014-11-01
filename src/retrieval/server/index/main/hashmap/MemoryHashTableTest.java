/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.server.index.main.hashmap;

import retrieval.config.ConfigServer;
import retrieval.server.exception.ReadIndexException;
import retrieval.server.index.ValueStructure;

/**
 *
 * @author lrollus
 */
public class MemoryHashTableTest extends MemoryHashTable{
    public static String NAME = "MEMORYTEST";

    public MemoryHashTableTest(int t,ConfigServer configStore, boolean read) throws ReadIndexException {
        super(configStore,read);
        if(t==1) {
            hashmap.put("000", new ValueStructure(configStore, new long[] {1l},new int[] {3}, 3l)); //0
            hashmap.put("001", new ValueStructure(configStore, new long[] {1l,2l,3l},new int[] {1,10,1}, 12)); //1
            hashmap.put("010", new ValueStructure(configStore, new long[] {1},new int[] {6}, 6)); //2
            hashmap.put("111", new ValueStructure(configStore, new long[] {3},new int[] {9}, 9)); //7
        } else if(t==2) {
            hashmap.put("000", new ValueStructure(configStore, new long[] {1},new int[] {4}, 4)); //0
            hashmap.put("001", new ValueStructure(configStore, new long[] {2,3},new int[] {9,3}, 12)); //1
            hashmap.put("011", new ValueStructure(configStore, new long[] {1,2},new int[] {2,1}, 3)); //2
            hashmap.put("100", new ValueStructure(configStore, new long[] {1,3},new int[] {4,7}, 11)); //7
        } else throw new ReadIndexException("ERROR: t =" + t);

    }
}
