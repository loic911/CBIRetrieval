package retrieval.testvector;

import java.util.*;

/**
 * This class implement a test vector for central server.
 * A central server do not need index but well tests list
 * @author Rollus Loic
 */
public class TestVectorCentralServer extends TestVector {

    /**
     * Construct a Test Vector
     * @param   name   Name of the test vector
     * @param   pts   List of Points to compare
     * @param   val   List of Value for the comparaison
     * @param   pos   List of position for the comparaison
     **/
    public TestVectorCentralServer(String name,List<String> pts,List<String> val,List<String> pos) {
        this.name = name;
        this.randomGenerator = new Random();
        this.tests = new ArrayList<TestPoint>(pts.size());

        //Create the new list of test with the pts and the val list
        for (int i = 0; i < pts.size(); i++) {
            tests.add(new TestPoint(new PatchPoint(pts.get(i)),Double.parseDouble(val.get(i)),Integer.parseInt(pos.get(i))));
        }
    }
}