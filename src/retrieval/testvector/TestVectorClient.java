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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class implement a test vector for central server.
 * A central server do not need index but well tests list
 * @author Rollus Loic
 */
public class TestVectorClient extends TestVector {

    /**
     * Construct a Test Vector
     * @param   name   Name of the test vector
     * @param   pts   List of Points to compare
     * @param   val   List of Value for the comparaison
     * @param   pos   List of position for the comparaison
     **/
    public TestVectorClient(String name,List<String> pts,List<String> val,List<String> pos) {
        this.name = name;
        this.randomGenerator = new Random();
        this.tests = new ArrayList<TestPoint>(pts.size());

        //Create the new list of test with the pts and the val list
        for (int i = 0; i < pts.size(); i++) {
            tests.add(new TestPoint(new PatchPoint(pts.get(i)),Double.parseDouble(val.get(i)),Integer.parseInt(pos.get(i))));
        }
    }
}