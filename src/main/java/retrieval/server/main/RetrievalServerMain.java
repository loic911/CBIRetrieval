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
package retrieval.server.main;

import org.apache.log4j.BasicConfigurator;
import retrieval.config.ConfigServer;
import retrieval.config.ConfigurationFileNotFoundException;
import retrieval.server.RetrievalServer;

/**
 * Main class for server
 * @author Rollus Loic
 */
public class RetrievalServerMain {

    /**
     * Main method for server execution
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            BasicConfigurator.configure();
            ConfigServer cc;
            try {
                cc = new ConfigServer(args[0]);
            } catch (Exception e) {
                throw new ConfigurationFileNotFoundException("Configuration file was not found or invalid:" + e.getMessage());
            }
            if(args.length>2) {
                cc.setStoreName(args[2]);
            }
            RetrievalServer multiServer = new RetrievalServer(cc, "main", 0, false);
            multiServer.loadWithSocket(Integer.parseInt(args[1]));

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
