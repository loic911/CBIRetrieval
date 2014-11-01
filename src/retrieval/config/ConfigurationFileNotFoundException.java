package retrieval.config;

/**
 * Configuration File was not found
 * @author Rollus Loic
 */
public class ConfigurationFileNotFoundException extends Exception {

    /**
     * Creates a new instance of
     * <code>ConfigurationFileNotFound</code> without detail message.
     */
    public ConfigurationFileNotFoundException() {
    }


    /**
     * Constructs an instance of
     * <code>ConfigurationFileNotFound</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ConfigurationFileNotFoundException(String msg) {
        super(msg);
    }
}
