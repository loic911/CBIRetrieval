package retrieval.config;

import java.io.*;
import java.util.Properties;

/**
 * Configuration abstract class
 * @author Rollus Loic
 */
public abstract class Config {
    
    protected static String propertiesError = "ERRORPROPERTIES";

    /**
     * Read configuration files
     * @param file Configuration files
     * @return Properties
     * @throws FileNotFoundException Configuration File not found
     * @throws IOException Error during read of configuration file
     */
    public static Properties read(String file) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        FileInputStream in = new FileInputStream(file);
        prop.load(in);
        in.close();
        return prop;
    }

    /**
     * Write configuration file
     * @param p Properties
     * @param file Configuration file
     * @throws FileNotFoundException Configuration File not found
     * @throws IOException Error during read of configuration file
     */
    public static void write(Properties p, String file) throws FileNotFoundException, IOException {
        FileOutputStream in = new FileOutputStream(file);
        p.store(in, "icbr");
        in.close();
    }

    /**
     * Write configuration file on outputstream
     * @param properties Properties objetc
     * @param outputStream Stream to write it
     * @throws IOException Exception during the write
     */
    protected static void store(Properties properties, OutputStream outputStream) throws IOException {
        try {
            properties.store(new BufferedOutputStream(outputStream), null);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
