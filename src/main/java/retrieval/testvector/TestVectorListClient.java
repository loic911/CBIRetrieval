/*
 * Copyright 2015 ROLLUS Loïc
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

/**
 * Tests Vectors List from central server which extends Abstract class for
 * Test vector list.
 * Does not implements special methods, just override add/get
 * @author Rollus Loic
 */
public class TestVectorListClient extends TestVectorList {

    /**
     * Get the Tests vector central server at index i
     * @param i Index
     * @return Test vector central server i
     */
    @Override public TestVectorClient get(int i)
    {
        return (TestVectorClient)super.get(i);
    }

    /**
     * Add Tests vector central server at list
     * @param tvs Test vector central server
     */
    public void add(TestVectorClient tvs)
    {
        super.add(tvs);
    }

}
