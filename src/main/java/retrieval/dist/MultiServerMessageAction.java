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
import org.jdom.Element;

/**
 * @author Rollus Loic
 */
public class MultiServerMessageAction implements Message, Cloneable {
    
    /**
     * Purge command
     */
    public static final int PURGE = 0;
    
    /**
     * Size command
     */
    public static final int SIZE = 1;
    
    /**
     * Info command
     */
    public static final int INFOS = 2;
    
    public static final int STORAGES = 3;

    public int action;
    public String storage;

    public MultiServerMessageAction(int action) {
        this.action = action;
    }  
    
    public MultiServerMessageAction(int action, String storage) {
        this.action = action;
        this.storage = storage;
    }      

    public String getActionStr() {
        if(action==PURGE) {
            return "PURGE";
        }
        if(action==SIZE) {
            return "SIZE";
        }
        if(action==INFOS) {
            return "INFOS";
        }
        if(action==STORAGES) {
            return "STORAGES";
        }        
        return "ERROR";
    }

    @Override
    public String toString() {
        String s = "MESSAGE SUPER SERVER "+getActionStr()+"\n";
        return s;
    }

    /**
     * Method to build XML document from this message
     * @return XML document
     */
    public Document toXML() {
        Element racine = new Element("MultiServerMessage");
        racine.setAttribute("type",getActionStr());
        if(storage!=null) {
            racine.setAttribute("storage",storage);
        }
        return new Document(racine);
    }
}
