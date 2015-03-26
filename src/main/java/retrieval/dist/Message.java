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

import org.jdom.Document;

/**
 * Force to implement method for XML message
 * In practice, each class MessageX which implements Message
 * should implement a constructor:
 * MessageX(Document xml) so that client and server can read/write
 * MessageX from/to XML.
 * @author Rollus Loic
 */
public interface Message {

    /**
     * Generate a XML document from the error message
     * @return XML document
     * @throws Exception Error during XML generation
     */
    Document toXML() throws Exception;
}
