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

import org.apache.log4j.Logger;

import java.util.Random;

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
