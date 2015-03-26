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
package retrieval.dist;

/**
 * A xml message exception
 * @author Rollus Loic
 */
public class NotValidMessageXMLException extends Exception {

    /**
     * Creates a new instance of 
     * <code>NotValidMessageXMLException</code> without detail message.
     */
    public NotValidMessageXMLException() {
    }

    /**
     * Constructs an instance of <code>NotValidMessageXMLException</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public NotValidMessageXMLException(String msg) {
        super("NotValidMessageException:"+msg);
    }
}
