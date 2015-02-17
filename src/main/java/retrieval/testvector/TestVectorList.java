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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import retrieval.storage.index.ValueStructure;
import retrieval.utils.ImageData;
import retrieval.utils.PatcheInformation;
import retrieval.utils.PictureUtils;

/**
 * This class is a list that can contains TestVector objetcs
 * Abstract class which can be herited by
 * -TestVectorListServer: visuals words extraction and index
 * -TestVectorListCentralServer: visuals words extraction
 * -TestVectorListGenration: /
 * @author Rollus Loic
 **/
public abstract class TestVectorList extends ArrayList<TestVector> {

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(TestVectorList.class);

    /**
     * Generate a map of Visual words from a picture for each tests vector.
     * Return a list of T map where T is the number of tests vectors
     * @param imgP Picture
     * @param uri Picture path
     * @param N Number of visual words
     * @param resizeMethod Method use to resize patch
     * @param sizeOfPatchW Size of patch (w)
     * @param sizeOfPatchH Size of patch (h)
     * @return Visual words
     * @throws InterruptedException
     */
    public List<ConcurrentHashMap<String, Long>> generateVisualWordFromPicture(
            BufferedImage imgP,
            Long id,
            int N,
            int resizeMethod,
            int sizeOfPatchW,
            int sizeOfPatchH)
            throws InterruptedException {

        ImageData img = new ImageData(imgP);

        int numberOfThread = this.size();
        int numberOfPatchsPerThread = N / numberOfThread;
        int patchsRest = N % numberOfThread;

        //multithread generation, first generate patch
        PatchThread[] patchsthreads = new PatchThread[this.size()];
        for (int i = 0; i < numberOfThread; i++) {
            int patchsNumber;
            if (i != 0) {
                patchsNumber = numberOfPatchsPerThread;
            } else {
                patchsNumber = numberOfPatchsPerThread + patchsRest;
            }

            patchsthreads[i] = new PatchThread(
                    img,
                    patchsNumber,
                    resizeMethod,
                    sizeOfPatchW,
                    sizeOfPatchH);

            patchsthreads[i].start();
        }
        logger.debug("generateVisualWordFromPicture: wait threads...");
        //wait for all thread
        for (int i = 0; i < patchsthreads.length; i++) {
            patchsthreads[i].join();
        }

        //second, generate visual word from patchs
        VisualWordThread[] threads = new VisualWordThread[this.size()];
        List<ConcurrentHashMap<String, Long>> listsVW = new ArrayList<ConcurrentHashMap<String, Long>>();

        for (int i = 0; i < this.size(); i++) {
            threads[i] = new VisualWordThread(
                    this.get(i),
                    img,
                    id,
                    new ConcurrentHashMap<String, Long>(N),
                    N, resizeMethod,
                    sizeOfPatchW,
                    sizeOfPatchH);

            threads[i].start();
        }
        //wait for all thread
        for (int i = 0; i < this.size(); i++) {

            threads[i].join();
            listsVW.add(threads[i].getListsVW());

        }

        return listsVW;
    }

    /**
     * Sort tests vectors list
     */
    public void sort() {
        Collections.sort((List<TestVector>) this);
    }

    /**
     * Print test vector
     */
    public void print() {

        for (int i = 0; i < this.size(); i++) {
            logger.debug("print:" + this.get(i).getName());
        }

    }


}

/**
 * Thread which generate Patchs from a picture
 * @author Rollus Loic
 */
class PatchThread extends Thread {

    private ImageData img;
    private int N;
    private int resizeMethod;
    private int sizeOfPatchW;
    private int sizeOfPatchH;

    /**
     * Constructor for a Thread which generates patchs
     * @param img Picture
     * @param numberOfExtraction Number of patchs
     * @param resizeMethod Method use for resizing patch
     * @param sizeOfPatchW Size of patch (w)
     * @param sizeOfPatchH Size of patch (h)
     */
    PatchThread(
            ImageData img,
            int numberOfExtraction,
            int resizeMethod,
            int sizeOfPatchW,
            int sizeOfPatchH) {

        this.img = img;
        this.N = numberOfExtraction;
        this.resizeMethod = resizeMethod;
        this.sizeOfPatchW = sizeOfPatchW;
        this.sizeOfPatchH = sizeOfPatchH;

    }

    @Override
    public void run() {

        //Generate patchs random information (width, height,position x,...)
        List<PatcheInformation> listPatchInfo = TestVector.generatePatcheInformation(N, img.getWidth(), img.getHeight());

        //Patch extraction and add to picture list of patch
        for (int j = 0; j < listPatchInfo.size(); j++) {
            PatcheInformation pi = listPatchInfo.get(j);
            BufferedImage subImageResize =
                    PictureUtils.extractAndResizePicture(
                    img,
                    pi,
                    sizeOfPatchW,
                    sizeOfPatchH,
                    resizeMethod);
            img.addPatch(subImageResize);
        }
    }
}

/**
 * Thread which generate Visual word from Patch
 * @author Rollus Loic
 */
class VisualWordThread extends Thread {

    private ImageData img;
    private int N;
    private int resizeMethod;
    private int sizeOfPatchW;
    private int sizeOfPatchH;
    private ConcurrentHashMap<String, Long> listsVW;
    private TestVector testVector;
    private Long id;

    /**
     * Constructor for a Thread which generate Visual words for a test vector t
     * @param testVector Tests vector t
     * @param img Source image
     * @param uri Path of image
     * @param listsVW Structure where visual word will be store
     * @param N Number of patch to extract
     * @param resizeMethod Method to use for resizing
     * @param sizeOfPatchW Size of patch (w)
     * @param sizeOfPatchH Size of patch (h)
     */
    VisualWordThread(
            TestVector testVector,
            ImageData img,
            Long id,
            ConcurrentHashMap<String, Long> listsVW,
            int N,
            int resizeMethod,
            int sizeOfPatchW,
            int sizeOfPatchH) {

        this.img = img;
        this.N = N;
        this.resizeMethod = resizeMethod;
        this.sizeOfPatchW = sizeOfPatchW;
        this.sizeOfPatchH = sizeOfPatchH;
        this.listsVW = listsVW;
        this.testVector = testVector;
        this.id = id;
    }

    @Override
    public void run() {

        listsVW = testVector.generateVisualWordFromPicture(
                img,
                id,
                N,
                resizeMethod,
                sizeOfPatchW,
                sizeOfPatchH);

    }

    /**
     * Return Map with produced visual words
     * @return Visual words
     */
    public ConcurrentHashMap<String, Long> getListsVW() {
        return listsVW;
    }

}
