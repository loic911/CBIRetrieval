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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.config.Config;
import retrieval.utils.ConvertUtils;
import retrieval.utils.ImageData;
import retrieval.utils.PatcheInformation;
import retrieval.utils.PictureUtils;

/**
 * This class implements a generic Tests Vector
 * A test vector contains a list of tests
 * @author Rollus Loic
 **/
public abstract class TestVector implements Comparable {

    /** 
     * list of tests: [Pixel ; Value ]
     **/
    protected List<TestPoint> tests;
    /** 
     * Name of the vector test (usually the name of the file)
     **/
    protected String name;
    /** 
     * Object which generate Random value
     **/
    protected Random randomGenerator;
    /**
     * Configuration object
     */
    protected Config config;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(TestVector.class);

    /**
     * Compare this with object other
     * @param   o   Object that must be compare width this
     * @return   0 if this is equal to other, 1 if other is bigger than this
     * else -1
     **/
    public int compareTo(Object o) {
        TestVector other = (TestVector) o;
        if (other == this) {
            return 0;
        } else {
            int name1 = Integer.parseInt(other.name);
            int name2 = Integer.parseInt(this.name);
            if (name2 > name1) {
                return 1;
            } else if (name2 < name1) {
                return -1;
            }
            return 0;//equal
        }
    }

    /**
     * Getter of name
     * @return name of the Test vector
     **/
    public String getName() {
        return name;
    }

    /**
     * Generate Patch Information [x,y,w,h]
     * @param numberOfGeneration Patch generation
     * @param wPicture   Width of the pitcure
     * @param hPicture   Height of the picture
     * @return List of Patch Information
     **/
    public static List<PatcheInformation> generatePatcheInformation(int numberOfGeneration, int wPicture, int hPicture) {

        Random randomGenerator = new Random();
        List<PatcheInformation> listPatch = new ArrayList<PatcheInformation>();

        for (int j = 0; j < numberOfGeneration; j++) {
            int minwidth = 1 + 1 * (Math.min(wPicture, hPicture)) / 100;
            int maxwidth = 100 * (Math.min(wPicture, hPicture)) / 100;
            int wOfPatch = minwidth + randomGenerator.nextInt(maxwidth - minwidth);
            int hOfPatch = wOfPatch;
            int xOfPatch = randomGenerator.nextInt(wPicture - wOfPatch);
            int yOfPatch = randomGenerator.nextInt(hPicture - hOfPatch);

            PatcheInformation patch = new PatcheInformation(xOfPatch, yOfPatch, wOfPatch, hOfPatch);
            listPatch.add(patch);
        }
        return listPatch;
    }

    /**
     * Return a list of visual words exctracted from patchs by the test vector
     * @param   patchs   List of patchs
     * @return   Map with visual words
     **/
    public synchronized ConcurrentHashMap<String, Long> generateVisualWordFromPatchs(List<BufferedImage> patchs) {

        ConcurrentHashMap<String, Long> visualWords = new ConcurrentHashMap<String, Long>(patchs.size());

        for (int i = 0; i < patchs.size(); i++) {
            String vw = ConvertUtils.convertHexa(analyseToVisualWord(patchs.get(i)));
            Long n = visualWords.get(vw);
            if (n == null) {
                visualWords.put(vw, new Long(1));
            } else {
                visualWords.put(vw, n + 1);
            }
        }
        return visualWords;
    }

    /**
     * Generate a map of Visual words from a picture with tests vector tests
     * @param img Picture
     * @param pictureUri Picture path
     * @param N Number of visual words
     * @param resizeMethod Method use to resize patch
     * @param sizeOfPatchW Size of patch (w)
     * @param sizeOfPatchH Size of patch (h)
     * @return Visual words
     */
    public synchronized ConcurrentHashMap<String, Long> generateVisualWordFromPicture(
            ImageData img,
            Long id,
            int N,
            int resizeMethod,
            int sizeOfPatchW,
            int sizeOfPatchH) {

        ConcurrentHashMap<String, Long> visualWords = generateVisualWordFromPatchs(img.getPatchs(N));
        return visualWords;
    }

    /**
     * Take a subimage of imageID and return a visualword
     * Rem: Very critical part of the code (performance!)
     * @param   img   A patch
     * @return A visual word
     **/
    public synchronized String analyseToVisualWord(BufferedImage img) {

        char[] visualword = new char[tests.size()];
        double[] rbghsv = new double[6];
        for (int i = 0; i < tests.size(); i++) {

            TestPoint test = tests.get(i);

            int rgb = img.getRGB(test.getX(), test.getY());

            //If position is under 3, don't convert rgb (position 0-1-2) to hsv (position 3-4-5)
            if (test.getPosition() >= 3) {
                PictureUtils.RGBToHSV(rgb, rbghsv);
            }

            //convert to rgb
            rbghsv[0] = (rgb >> 16) & 0xFF;
            rbghsv[1] = (rgb >> 8) & 0xFF;
            rbghsv[2] = (rgb >> 0) & 0xFF;

            //logger.info(rgb + "---" + (rbghsv[0]) +"#"+(rbghsv[1]) +"#"+(rbghsv[2]) +"#"+(rbghsv[3]) +"#"+(rbghsv[4]) +"#"+(rbghsv[5]));
            if (rbghsv[test.getPosition()] > test.getValue()) {
                visualword[i] = '1';
            } else {
                visualword[i] = '0';
            }

        }
        return new String(visualword);
    }

    /**
     * Build a vector with 'numberOfTest' test.
     * Each test map a point x,y (width x upper or equal than 'xMax'
     * and y lower or equal than 'yMax') and a value
     * (valueMin lower or equal than value upper or equal than valueMax)
     * @param   numberOfTest   Number of test
     * @param   xMax   Max x
     * @param   yMax   Max y
     * @param   valueMin   Min value
     * @param   valueMax   Max value
     * @param   firstPosition  First index position (r=0,g,b,h,s,v=5);
     * @param   lastPosition   Last index position (r=0,g,b,h,s,v=5);
     **/
    public void generateVector(
            int numberOfTest,
            int xMax,
            int yMax,
            double valueMin,
            double valueMax,
            int firstPosition,
            int lastPosition) {

        tests = new ArrayList<TestPoint>();

        for (int i = 0; i < numberOfTest; i++) {
            PatchPoint pi = new PatchPoint(randomGenerator.nextInt(xMax), randomGenerator.nextInt(yMax));
            double val = randomNumber(valueMin, valueMax);
            int pos = randomGenerator.nextInt(lastPosition - firstPosition + 1) + firstPosition;
            tests.add(new TestPoint(pi, val, pos));
        }

    }

    /**
     * Save test vector
     * @param   directory   Path where to save test vector
     * @throws   IOException   If directory is not valid
     **/
    public synchronized void saveTest(String directory) throws IOException {
        String[] key = new String[tests.size()];
        String[] val = new String[tests.size()];
        String[] pos = new String[tests.size()];

        for (int i = 0; i < tests.size(); i++) {
            key[i] = tests.get(i).getPoint().toString();
            val[i] = tests.get(i).getValue() + "";
            pos[i] = tests.get(i).getPosition() + "";
        }
        logger.info("saveTest: " + directory);
        AssociativeArrayXML xml = new AssociativeArrayXML(name, key, val, pos);
        xml.saveXML(directory);
    }

    /**
     * Generate a random double between Min and Max
     * @param   min   Min
     * @param   max   Max
     * @return   random double
     **/
    protected double randomNumber(double min, double max) {
        return min + Double.valueOf(Math.random() * (max - min));
    }
}
