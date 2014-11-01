package retrieval.testvector;

/**
 * Tests Vectors List from central server which extends Abstract class for
 * Test vector list.
 * Does not implements special methods, just override add/get
 * @author Rollus Loic
 */
public class TestVectorListCentralServer extends TestVectorList {

    /**
     * Get the Tests vector central server at index i
     * @param i Index
     * @return Test vector central server i
     */
    @Override public TestVectorCentralServer get(int i)
    {
        return (TestVectorCentralServer)super.get(i);
    }

    /**
     * Add Tests vector central server at list
     * @param tvs Test vector central server
     */
    public void add(TestVectorCentralServer tvs)
    {
        super.add(tvs);
    }

}
