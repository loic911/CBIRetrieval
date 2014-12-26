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
package retrieval.testvector.generator;

import java.io.File;
import java.util.Arrays;
import org.apache.log4j.Logger;
import retrieval.testvector.TestVectorGeneration;
import retrieval.testvector.generator.exception.TestsVectorsArgumentException;
import retrieval.testvector.generator.exception.TestsVectorsWritingException;
import retrieval.utils.FileUtils;

/**
 * This class implement a test vector writer (interface CreateTestVector).
 * A test vector is a list of association: <Pixel;Value>.
 * Value of each Pixels will be compare with Value
 * @author Rollus Loic
 **/
public class TestVectorWriting {

    /** Number of test vector **/
    private int numberOfGeneration;
    /** Number of test in a test vector **/
    private int numberOfTest;
    /** Max X of test pixel **/
    private int maxSizeX;
    /** Max Y of test pixel **/
    private int maxSizeY;
    /** Min value of pixel test **/
    private double firstValue;
    /** Max value of pixel test **/
    private double lastValue;
    /** Test value will be max lastValue-ThresholdMax  **/
    private int ThresholdMax;
    /** Test value will be min firstValue+ThresholdMin  **/
    private int ThresholdMin;
    /** First value indice (ex: RGB = (1,2,3) - first 2 = compare G or B) **/
    private int firstPosition;
    /** Last value indice (ex: RGB = (1,2,3) - last 2 = compare R or G) **/
    private int lastPosition;
    private String[] storeName;
    static Logger logger = Logger.getLogger(TestVectorWriting.class);

    /**
     * Constructs a new TestVecorWriter
     * @param      storeName    Name of tests vectors
     * @param      numberOfGeneration    Number of test vector to generate
     * @param      numberOfTest   Number of test in each test vector
     * @param      maxSizeX   Max X of test pixel
     * @param      maxSizeY   Max Y of test pixel
     * @param      ThresholdMin   Test value will be min firstValue+ThresholdMin
     * @param      ThresholdMax   Test value will be max lastValue-ThresholdMax
     * @param      firstValue   Min value of pixel test
     * @param      lastValue   Max value of pixel test
     * @param      firstPosition   First value of Pixel which can be compare
     * @param      lastPosition   Last value of Pixel which can be compare
     * @exception  TestsVectorsArgumentException  if initial argument are wrong
     */
    public TestVectorWriting(
            String[] storeName,
            int numberOfGeneration,
            int numberOfTest,
            int maxSizeX,
            int maxSizeY,
            int ThresholdMin,
            int ThresholdMax,
            double firstValue,
            double lastValue,
            int firstPosition,
            int lastPosition)
            throws TestsVectorsArgumentException {


        logger.info("TestVectorWriting: build " + numberOfGeneration
                + " vectors of " + numberOfTest + " tests");

        //TODO: le store contient un nombre delement differenet du nbre generat
        if (storeName.length != numberOfGeneration) {
            throw new TestsVectorsArgumentException("NumberOfGeneration must be equal to storeName size");
        }

        if (numberOfGeneration < 1) {
            throw new TestsVectorsArgumentException("NumberOfGeneration must be >0");
        }
        if (numberOfTest < 1) {
            throw new TestsVectorsArgumentException("NumberOfTest must be >0");
        }
        if (firstValue > lastValue) {
            throw new TestsVectorsArgumentException("firstValue must be <lastValue");
        }
        if (firstPosition > lastPosition) {
            throw new TestsVectorsArgumentException("firstPosition must be <lastPosition");
        }
        if ((firstValue + ThresholdMin) >= (lastValue - ThresholdMax)) {
            throw new TestsVectorsArgumentException("ThresholdMin+FirstValue " + "must be < LastValue-ThrasholdMax");
        }

        this.storeName = Arrays.copyOf(storeName, storeName.length);
        
        this.numberOfGeneration = numberOfGeneration;
        this.numberOfTest = numberOfTest;
        this.maxSizeX = maxSizeX;
        this.maxSizeY = maxSizeY;
        this.firstValue = firstValue;
        this.lastValue = lastValue;
        this.ThresholdMin = ThresholdMin;
        this.ThresholdMax = ThresholdMax;
        this.firstPosition = firstPosition;
        this.lastPosition = lastPosition;

    }

    /**
     * Erase all files into directory and construct
     * new test vectors of the TestVectorWriting.
     * @param      directory   Path where test vectors files must be stored
     * @exception  TestsVectorsWritingException   if directory is not valid
     */
    public void build(String directory)
            throws TestsVectorsWritingException {

        logger.info("build: build in " + directory);

        try {
            File f = new File(directory);
            logger.info("build: rease all in " + directory);
            FileUtils.deleteAllFiles(f);

            logger.info("build: create " + directory + " if not exists");
            if (!f.exists()) {
                f.mkdirs();
            }
        } catch (Exception e) {
            logger.error("build: cannot build test vector:" + e);
            throw new TestsVectorsWritingException(e + " : can't build test vector");
        }

        try {

            logger.info("build: build all vectors...");
            for (int i = 0; i < numberOfGeneration; i++) {
                logger.info("build: vector " + i + "...");
                TestVectorGeneration testVector = new TestVectorGeneration(storeName[i]);
                testVector.generateVector(numberOfTest, maxSizeX, maxSizeY,
                        firstValue + ThresholdMin, lastValue - ThresholdMax,
                        firstPosition, lastPosition);
                testVector.saveTest(directory);
            }
        } catch (Exception e) {
            logger.error("build: cannot build test vector:" + e);
            throw new TestsVectorsWritingException(e + " : can't build test vector");
        }
    }
}
