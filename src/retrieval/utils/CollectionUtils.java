/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author lrollus
 */
public class CollectionUtils {

    /**
     * Join all element from words list and separate them with character
     * @param words List of item to join
     * @param character Character for separation
     * @return list of item join with the character
     */
    public static String join(Collection<String> words, String character) {
        if(words.isEmpty()) {
            return "";
        }
        StringBuilder wordList = new StringBuilder();
        for (String word : words) {
            wordList.append(word).append(character);
        }
        String s = new String(wordList.deleteCharAt(wordList.length() - character.length()));
        return s;
    }
    
    /**
     * Join all element from words array and separate them with character
     * @param words Array of item to join
     * @param character Character for separation
     * @return list of item join with the character
     */    
    public static String join(String[] words, String character) {
        if(words.length==0) {
            return "";
        }
        StringBuilder wordList = new StringBuilder();
        for (String word : words) {
            wordList.append(word).append(character);
        }
        String s = new String(wordList.deleteCharAt(wordList.length() - character.length()));
        return s;
    }    

    /**
     * Splits this string around matches of the given character
     * @param words String to split
     * @param character Separate character
     * @return List of string compute by splitting the words list
     */
    public static List<String> split(String words, String character) {
        if(words==null) {
            return new ArrayList<String>();
        }
        if(words.length()==0) {
            return new ArrayList<String>();
        }
        String[] arrays = words.split(character);
        return Arrays.asList(arrays);
    }
    
    /**
     * Splits this string around matches of the given character
     * @param words String to split
     * @param character Separate character
     * @return Array of string compute by splitting the words list
     */    
    public static String[] splitArray(String words, String character) {
        if(words==null) {
            return new String[0];
        }
        if(words.length()==0) {
            return new String[0];
        }
        String[] arrays = words.split(character);
        return arrays;
    }    

    /**
     * Copy all element from words in a new list
     * @param words Element to copy
     * @return List with element
     */
    public static List<String> cloneStringList(Collection<String> words) {
        List<String> wordList = new ArrayList<String>();
        for (String word : words) {
            wordList.add(word);
        }
        return wordList;
    }

    /**
     * Split a list in number list
     * @param list List to split
     * @param number Number of sublist to create
     * @return List of list
     */
    public static ArrayList[] splitList(List<String> list, int number) {
            //split indexCollection files list for each server
            ArrayList[] pictureByServer = new ArrayList[number];
            // initialize all your small ArrayList groups
            for (int i = 0; i < number; i++){
                pictureByServer[i] = new ArrayList<String>();
            }

            double idx = 0d;
            double incrx = (double) ((double) number / (double) list.size());
            // put your results into those arrays
            for (int i = 0; i < list.size(); i++) {
                pictureByServer[(int) Math.floor(idx)].add(list.get(i));
                idx = idx + incrx;
            }
            return pictureByServer;
    }
     
}
