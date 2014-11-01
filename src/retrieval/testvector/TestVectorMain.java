package retrieval.testvector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import retrieval.testvector.generator.TestVectorWriting;
import retrieval.testvector.generator.exception.TestsVectorsArgumentException;
import retrieval.testvector.generator.exception.TestsVectorsWritingException;

/**
 * This class is an exemple of how to use CreateTestVector and ReadTestVector
 * interface
 * @author Loic Rollus
 **/
public class TestVectorMain {

    /**
     * Logger
     */
    static Logger logger = Logger.getLogger(TestVectorMain.class);

    /**
     * Write some test vectors on the disk
     * arg1: Number of vector to build
     * arg2: Number of test for each vector
     * arg3: X max for pixel retrieve
     * arg4: Y max for pixel retrieve
     * arg5: First value for test comparaison
     * arg6: Last value for test comparaison
     * arg7: First type of test to make (R=0,G=1,B=2,H=3,S=4,V=5)
     * arg8: Last type of test to make (R=0,G=1,B=2,H=3,S=4,V=5)
     * arg9: Path to store tests vectors
     * @param  args  Arguments
     **/
    public static void main(String[] args) throws Exception {

        try {
            BasicConfigurator.configure();

            logger.info("main: Generation of tests vectors");
            int numberOfGeneration = Integer.parseInt(args[0]);
            int numberOfTest = Integer.parseInt(args[1]);
            int xMax = Integer.parseInt(args[2]);
            int yMax = Integer.parseInt(args[3]);
            int thresholdMin = 0;
            int thresholdMax = 0;
            int firstValue = Integer.parseInt(args[4]);
            int lastValue = Integer.parseInt(args[5]);
            int firstPosition = Integer.parseInt(args[6]);
            int lastPosition = Integer.parseInt(args[7]);
            String buildDirectory = args[8];

            logger.info("main: Write tests vectots");
            write(numberOfGeneration,numberOfTest,xMax,yMax,thresholdMin,thresholdMax,firstValue,lastValue,firstPosition,lastPosition,buildDirectory);
            logger.info("main: end of generation");

        } catch (TestsVectorsArgumentException e) {
            logger.error("main:" + e);
        } catch (TestsVectorsWritingException e) {
            logger.error("main:" + e);
        } catch (Exception e) {
            logger.error("main:" + e);
        }

    }

    /**
     * Write tests vector on disk
     * @param configVT Tests vectors generation configuration
     * @throws TestsVectorsArgumentException Exception caused by an argument error
     * @throws TestsVectorsWritingException Exception caused by an IO Error
     */
    public static void write(
            int numberOfVector,
            int numberOfTest,
            int xMax,
            int yMax,
            int thresholdMin,
            int thresholdMax,
            int firstValue,
            int lastValue,
            int firstPosition,
            int lastPosition,
            String buildDirectory) throws TestsVectorsArgumentException, TestsVectorsWritingException {

        String[] storeName = new String[numberOfVector];
        for (int i = 0; i < numberOfVector; i++) {
            storeName[i] = "" + (i + 1);
        }

        TestVectorWriting tvg = new TestVectorWriting(storeName,
                numberOfVector,
                numberOfTest,
                xMax,
                yMax,
                thresholdMin,
                thresholdMax,
                firstValue,
                lastValue,
                firstPosition,
                lastPosition);
        tvg.build(buildDirectory);
    }
}

