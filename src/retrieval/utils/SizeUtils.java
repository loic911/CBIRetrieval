/*
 * Copyright 2009-2014 the original author or authors.
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

/**
 * Size of a generic item
 * Its main function is to allow resizing that keep proportionnality
 * @author Rollus Loic
 */
public class SizeUtils {

    private double height;
    private double width;
    private double maxHeigth;
    private double maxWidth;

    /**
     * Constructor for a size utils
     * @param w Width
     * @param h Height
     * @param maxw Max W
     * @param maxh Min H
     */
    public SizeUtils (int w, int h, int maxw, int maxh) {
        width = (double) w;
        height = (double) h;
        maxWidth = (double) maxw;
        maxHeigth = (double) maxh;
    }

    /**
     * Constructor for a size utils
     * @param w Width
     * @param h Height
     * @param maxw Max W
     * @param maxh Min H
     */
    public SizeUtils (double w, double h, double maxw, double maxh) {
        width = w;
        height = h;
        maxWidth = maxw;
        maxHeigth = maxh;
    }

    /**
     * Compute maximal size of object with two limits (width and height).
     * Keep proportionnality of size: if width=2*height, return a object size
     * where width=2*height
     * @return Size
     */
    public SizeUtils  computeThumbSize() {
        SizeUtils  s = new SizeUtils(height, width, maxWidth, maxHeigth);
        if (maxHeigth < getHeight() || maxWidth < getWidth()) {
            s = minThumbSize();
        } else {
            s = maxThumbSize();
        }
        return s;
    }

    /**
     * Get Height
     * @return Height
     */
    public int getHeight() {
        return (int) height;
    }

    /**
     * Get Width
     * @return Width
     */
    public int getWidth() {
        return (int) width;
    }

    private SizeUtils resize(double scale, double w, double h) {
        double newH = scale * h;
        double newW = scale * w;
        return new SizeUtils (newW, newH, maxWidth, maxHeigth);
    }

    private SizeUtils  minThumbSize() {

        double propH = maxHeigth / getHeight();
        double propW = maxWidth / getWidth();

        if (propH < propW) {
            //resize in function of height
            return resize(propH, getWidth(), getHeight());
        } else {
            //resize in function of width
            return resize(propW, getWidth(), getHeight());
        }
    }

    private SizeUtils  maxThumbSize() {

        double propH = maxHeigth / getHeight();
        double propW = maxWidth / getWidth();

        if (propH < propW) {
            //resize in function of height
            return resize(propH, getWidth(), getHeight());

        } else {
            //resize in function of width
            return resize(propW, getWidth(), getHeight());
        }
    }
}