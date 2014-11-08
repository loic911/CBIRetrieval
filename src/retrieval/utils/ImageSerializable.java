/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.utils;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author lrollus
 */
public class ImageSerializable  implements Serializable{
      int width; int height; int[] pixels;

     public ImageSerializable(BufferedImage bi) { 
          width = bi.getWidth(); 
          height = bi.getHeight(); 
          pixels = new int[width * height]; 
          int[] tmp=bi.getRGB(0,0,width,height,pixels,0,width); 
     }

     public BufferedImage getImage() { 
          BufferedImage bi = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
          bi.setRGB(0,0,width,height,pixels,0,width);
     return bi; 
     }    
}
