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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class implements a Image information
 * @author Rollus Loic
 **/
public class ImageData {

    /**
     * Picture
     */
    private BufferedImage img;
    /**
     * Picture's patchs
     */
    private ConcurrentLinkedQueue<BufferedImage> patchs;

    /**
     * Construct image data
     * @param   img   Image
     **/
    public ImageData(BufferedImage img) {
        this.img = img;
        this.patchs = new ConcurrentLinkedQueue<BufferedImage>();
    }

    /**
     * Add a Patch for this picture
     * @param   img   Patch
     **/
    public void addPatch(BufferedImage img) {
        patchs.add(img);
    }

    /**
     * Get all of the patch for this picture
     * @param N Number of patch (JUST A OPTIMISATION PARAMETER!)
     * @return All of patches of this picture
     **/
    public List<BufferedImage> getPatchs(int N) {
        /**
         * Its better to put the final size for an arraylist when construct them
         * We can make it with patchs.size() instead of an argument N.
         * But the size method for concurrentLinkedqueue is linear (not constant!)
         **/
        List<BufferedImage> list =
                new ArrayList<BufferedImage>(N);

        //transfert item from queue to list
        Iterator<BufferedImage> itqueue = patchs.iterator();
        while (itqueue.hasNext()) {
            list.add(itqueue.next());
        }
        return list;
    }

    /**
     * Getter image
     * @return The img
     */
    public BufferedImage getImg() {
        return img;
    }

    /**
     * Getter the image width
     * @return Image width
     */
    public int getWidth() {
        return img.getWidth();
    }

    /**
     * Getter the image height
     * @return Image height
     */
    public int getHeight() {
        return img.getHeight();
    }
}
