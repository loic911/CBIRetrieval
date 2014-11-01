package retrieval.testvector;

import java.util.Random;
import org.apache.log4j.Logger;

/**
 * This class implement a test vector for generation
 * Just need test informations
 * @author Rollus Loic
 */
public class TestVectorGeneration extends TestVector {

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(TestVectorList.class);

    /**
     * Construct a Test Vector without index ands test
     * @param   name   Name of the test vector
     **/
    public TestVectorGeneration(String name) {
        logger.info("TestVectorGeneration: start");
        this.name = name;
        this.randomGenerator = new Random();
        this.tests = null;
    }
}
