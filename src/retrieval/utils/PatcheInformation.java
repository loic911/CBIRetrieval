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
 * This class implements a PatchInformation
 * @author Loic Rollus
 **/
public class PatcheInformation {

    private final int x;
    private final int y;
    private final int w;
    private final int h;

    /**
     * Construct a PatchInformation Object
     * @param   x   X
     * @param   y   Y
     * @param   w   Width
     * @param   h   Height
     **/
    public PatcheInformation(int x,int y,int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Return a String format of a PatchPoint
     * @return   String value
     **/
    @Override public String toString()
    {
        return "Point("+x+","+y+") with ("+w+","+h+")";
    }

    /**
     * Getter X
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Getter Y
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Getter W
     * @return the w
     */
    public int getW() {
        return w;
    }

    /**
     * Getter H
     * @return the h
     */
    public int getH() {
        return h;
    }
}
