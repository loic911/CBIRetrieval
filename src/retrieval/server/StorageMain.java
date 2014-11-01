package retrieval.server;

import retrieval.config.*;
import org.apache.log4j.*;

/**
 * Main class for server
 * @author Rollus Loic
 */
public class StorageMain {

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
                if (args.length == 3) {
                    cc.setStoreName(args[2]);
                }
            } catch (Exception e) {
                throw new ConfigurationFileNotFoundException("Configuration file was not found or invalid:" + e.getMessage());
            }

            Storage server = new Storage("0",cc);
            server.start();

        } catch (Exception e) {
           System.out.println(e.toString());
        }
    }
}
