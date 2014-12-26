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
package retrieval.testvector;
import java.util.StringTokenizer;

/**
 * This class implement a simple PatchPoint
 * @author Loic Rollus
 **/
public class PatchPoint {

    private int x;
    private int y;

    /**
     * Construct a PatchPoint
     * @param   x   X value of the point
     * @param   y   Y value of the point
     */
    public PatchPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Construct a PatchPoint
     * @param   value   X and Y value of the point in format x,y
     */
    public PatchPoint(String value) {
        StringTokenizer st = new StringTokenizer(value, ",");
        x = Integer.valueOf(st.nextToken());
        y = Integer.valueOf(st.nextToken());
    }

    /**
     * Getter of X
     * @return X value
     */
    public int getX() {
        return x;
    }

    /**
     * Getter of Y
     * @return Y value
     */
    public int getY() {
        return y;
    }

    /**
     * Return a String value of this point in x,y format
     * @return X and Y value (x,y)
     */
    @Override public String toString() {
        return x + "," + y;
    }
}
