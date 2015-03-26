/*
 * Copyright 2015 ROLLUS Loïc
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
package retrieval.utils;

import org.apache.log4j.Logger;
import retrieval.storage.exception.PictureNotFoundException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * This class store util method for IO
 * @author Rollus Loic
 */
public class FileUtils {
    private static Logger logger = Logger.getLogger(FileUtils.class);  
    
    /**
     * List all file from file/directory f (and its subdirectories) in list v
     * Don't list picture in cbirthumb directory (thumbnail directory)
     * @param f File f
     * @param v Destination list
     */
    public static void listFiles(File f, List<String> v) {

        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isDirectory()) {
                //directory which contain no thumb, list its files
                    listFiles(fs[i], v);
            } else {
                //its a file
                v.add(fs[i].getAbsolutePath());
            }
        }

    }
    
    /**
     * List all file from file/directory f (and its subdirectories) in list v
     * Don't list picture in cbirthumb directory (thumbnail directory)
     * @param f File f
     * @param v Destination list
     * @param format Accepted format
     */
    public static void listFiles(File f, List<String> v, String[] format) {
        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isDirectory()) {
                //directory which contain no thumb, list its files
                    listFiles(fs[i], v, format);
            } else {
                //its a file
                if(isValidFormat(fs[i],format)) {
                    v.add(fs[i].getAbsolutePath());
                }               
            }
        }
    } 
    
    private static boolean isValidFormat(File f,String[] format) {
       boolean valid = false;
       String formatFile = getUpperCaseFormat(f);
       for(String allowedFormat : format) {
           if(formatFile.equals(allowedFormat.toUpperCase())) {
               valid = true;
           }
       }
       return valid;  
    }
    
    private static String getUpperCaseFormat(File f) {
        String name = f.getName().toUpperCase();
        int pos = name.lastIndexOf('.');
        if(pos==-1 || name.length()<(pos+1)) {
            return "";
        }
        String ext = name.substring(pos+1); 
        return ext;
    }

    /**
     * Dlete all files and directory from directory
     * @param directory Directory
     */
    public static void deleteAllFiles(File directory) {
        File[] fs = directory.listFiles();
        if (fs != null) {
            for (int i = 0; i < fs.length; i++) {
                fs[i].delete();
            }
        }
    }
    
    public static void deleteAllFilesRecursively(File directory) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(directory);
    }
    
    public static void deleteAllSubFilesRecursively(File directory) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(directory);
        directory.mkdirs();
    }    

    public static BufferedImage readPicture(String uri) throws IOException {
        
        File f = new File(uri);
        boolean isURL = false;

        if (!f.canRead()) {isURL = true;}

        BufferedImage img = null;
        
        if (isURL) {
                img = ImageIO.read(new URL(uri));
        } else {
            logger.info(uri + " is a local file");
            img = ImageIO.read(new File(uri));
        }
        return img;

    }
    
    public static BufferedImage readPictureFromPath(File file) throws PictureNotFoundException {
        try {
            return ImageIO.read(file);
        } catch(IOException e) {
            throw new PictureNotFoundException("Cannot read: " + file.getAbsolutePath() + ": " + e.toString());
        }
    }
    
     public static BufferedImage readPictureFromUrl(URL url) throws PictureNotFoundException {
        try {
            return ImageIO.read(url);
        } catch(IOException e) {
            throw new PictureNotFoundException("Cannot read: " + url.toString() + ": " + e.toString());
        }
    }   
    
public static void copyFile(File sourceFile, File destFile) throws IOException {
    if(!destFile.exists()) {
        destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;

    try {
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        destination.transferFrom(source, 0, source.size());
    }
    finally {
        if(source != null) {
            source.close();
        }
        if(destination != null) {
            destination.close();
        }
    }
}    
}
