package retrieval.multiserver;

import org.apache.log4j.*;
import retrieval.config.*;

/**
 * Main class for server
 * @author Rollus Loic
 */
public class MultiServerMain {

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

            MultiServer multiServer = new MultiServer(cc, "main", 0, false);
            multiServer.loadWithSocket(Integer.parseInt(args[1]));

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
