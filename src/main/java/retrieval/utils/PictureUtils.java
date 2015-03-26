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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Class with util image method
 * @author Loic Rollus
 */
public class PictureUtils {

    /**
     * Convert r,g,b to h,s,v
     * @param r Red value
     * @param g Green value
     * @param b Blue value
     * @param hsv Array which will contain hsv values
     */
    public static void RGBtoHSV(int r, int g, int b, double hsv[]) {

        int min;    //Min. value of RGB
        int max;    //Max. value of RGB
        int delMax; //Delta RGB value

        if (r > g) {
            min = g;
            max = r;
        } else {
            min = r;
            max = g;
        }
        if (b > max) {
            max = b;
        }
        if (b < min) {
            min = b;
        }

        delMax = max - min;

        float H = 0, S;
        float V = max;

        if (delMax == 0) {
            H = 0;
            S = 0;
        } else {
            S = delMax / 255f;
            if (r == max) {
                H = ((g - b) / (float) delMax) * 60;
            } else if (g == max) {
                H = (2 + (b - r) / (float) delMax) * 60;
            } else if (b == max) {
                H = (4 + (r - g) / (float) delMax) * 60;
            }
        }

        hsv[3] = (H + 60)/1.411764706d;
        hsv[4] = (S * 100);
        hsv[5] = V;
    }

    /**
     * Convert rgb value to h,s,v
     * @param rgb RGB value
     * @param hsv Array which will contain hsv values
     */
    public static void RGBToHSV(int rgb, double[] hsv) {
        RGBtoHSV((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, hsv);
    }


    /**
     * Resize picture img to targetwidth and targetHeight
     * Not use by patchs generator,
     * but to resize picture in client, thumbnail in server...
     * @param img Image img
     * @param targetWidth Width
     * @param targetHeight Height
     * @return Resize image
     */
    public static BufferedImage ResizePicture(BufferedImage img, int targetWidth, int targetHeight) {
        double scalex = (double) targetWidth / (double) img.getWidth();
        double scaley = (double) targetHeight / (double) img.getHeight();
        AffineTransformOp affineTransformOp = new AffineTransformOp(AffineTransform.getScaleInstance(scalex, scaley), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage windowBufferedImage = affineTransformOp.filter(img, null);
        return windowBufferedImage;
    }

    /**
     * Extract a subimage (pi.getW()*pi.getH) of img from pi.getX() to pi.getY() and resize it in a patch of targetWidth*targetHeight
     * @param img Image object which contains the original image
     * @param pi Information (x,y,w,h) about the extraction
     * @param targetWidth Width of the patch
     * @param targetHeight Height of the patch
     * @param method Method use for resize
     * @return Patch
     */
    public static BufferedImage extractAndResizePicture(ImageData img, PatcheInformation pi, int targetWidth, int targetHeight, int method) {
        switch (method) {
            case 1:
                return extractAndResizePicture1(img, pi, targetWidth, targetHeight);

            case 2:
                return extractAndResizePicture2(img, pi, targetWidth, targetHeight);

            case 3:
                return extractAndResizePicture3(img, pi, targetWidth, targetHeight);

            case 4:
                return extractAndResizePicture4(img, pi, targetWidth, targetHeight);
            default:
                return null;              
        }
    }

    private static BufferedImage extractAndResizePicture1(ImageData img, PatcheInformation pi, int targetWidth, int targetHeight) {
        BufferedImage subImage = img.getImg().getSubimage(pi.getX(), pi.getY(), pi.getW(), pi.getH());
        int type = subImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : subImage.getType();
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.drawImage(subImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resizedImage;

    }

    private static BufferedImage extractAndResizePicture2(ImageData img, PatcheInformation pi, int targetWidth, int targetHeight) {
        BufferedImage subImage = img.getImg().getSubimage(pi.getX(), pi.getY(), pi.getW(), pi.getH());
        BufferedImage patch = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = patch.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(subImage, 0, 0, targetWidth, targetHeight, null);
        return patch;
    }

    private static BufferedImage extractAndResizePicture3(ImageData img, PatcheInformation pi, int targetWidth, int targetHeight) {
        BufferedImage subImage = img.getImg().getSubimage(pi.getX(), pi.getY(), pi.getW(), pi.getH());
        double scalex = (double) targetWidth / (double) subImage.getWidth();
        double scaley = (double) targetHeight / (double) subImage.getHeight();
        AffineTransformOp affineTransformOp = new AffineTransformOp(AffineTransform.getScaleInstance(scalex, scaley), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage windowBufferedImage = affineTransformOp.filter(subImage, null);
        return windowBufferedImage;
    }

    private static BufferedImage extractAndResizePicture4(ImageData img, PatcheInformation pi, int targetWidth, int targetHeight) {
        BufferedImage subImage = img.getImg().getSubimage(pi.getX(), pi.getY(), pi.getW(), pi.getH());
        double scalex = (double) targetWidth / (double) subImage.getWidth();
        double scaley = (double) targetHeight / (double) subImage.getHeight();
        AffineTransformOp affineTransformOp = new AffineTransformOp(AffineTransform.getScaleInstance(scalex, scaley), AffineTransformOp.TYPE_BILINEAR);
        BufferedImage windowBufferedImage = affineTransformOp.filter(subImage, null);
        return windowBufferedImage;
    }
}