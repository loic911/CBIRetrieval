package retrieval.utils;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import retrieval.dist.NotValidMessageXMLException;

/**
 * Util methods for network communication
 * @author Rollus Loic
 */
public class NetworkUtils {
    private static Logger logger = Logger.getLogger(NetworkUtils.class);
    /**
     * Write a xml message to a client/server without exception throw
     * Just for error message
     * @param client Entity which will receive message
     * @param xml XML message
     */
    public static void writeXmlToSocketWithoutException(Socket client, Document xml) {
        try {
            writeXmlToSocket(client, xml);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    /**
     * Read a xml message from a client/server without exception throw
     * Just for error message
     * @param client Entity which will send message
     * @return XML message
     */
    public static Document readXmlFromSocketWithoutException(Socket client) {
        try {
            return readXmlFromSocket(client);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }
    
    public static void writeXmlToSocket(Socket client, Document xml)
            throws IOException {
       client.setSoTimeout(60000);
       ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
       oos.writeObject(xml);
       oos.flush();
    }

    
    public static void writeXmlToSocket(Socket client, BufferedImage image)
            throws IOException {
       client.setSoTimeout(60000);
       ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
       oos.writeObject(new ImageSerializable(image));
       oos.flush();
    }        
    
     public static Document readXmlFromSocket(Socket client) throws IOException, NotValidMessageXMLException {
        try {
            if(client.isClosed() || !client.isConnected()) {
                throw new IOException("Connection is closed!");
            }
            //client.setSoTimeout(60000);
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            Document document = (Document)ois.readObject();
            return document;
        } catch (IOException e) {
            logger.error(e);
            throw new NotValidMessageXMLException(e.toString());
        }catch (Exception e) {
            logger.error(e);
            throw new NotValidMessageXMLException(e.toString());
        }

    }   
     
     public static BufferedImage readBufferedImageFromSocket(Socket client) throws IOException, NotValidMessageXMLException {
        try {
            if(client.isClosed() || !client.isConnected()) {
                throw new IOException("Connection is closed!");
            }
            //client.setSoTimeout(60000);
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            ImageSerializable imageSer = (ImageSerializable)ois.readObject();
            return imageSer.getImage();
        } catch (IOException e) {
            logger.error(e);
            throw new NotValidMessageXMLException(e.toString());
        }catch (Exception e) {
            logger.error(e);
            throw new NotValidMessageXMLException(e.toString());
        }

    }        

    public static void saveXml(Document document, String path) throws IOException {

        XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
        sortie.output(document, new FileOutputStream(path));

    }
     
    public static boolean isPortAvailable(int port) {

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }    
}
