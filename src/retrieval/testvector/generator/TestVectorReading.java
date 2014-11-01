package retrieval.testvector.generator;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import retrieval.config.ConfigServer;
import retrieval.multiserver.globaldatabase.GlobalDatabase;
import retrieval.multiserver.globaldatabase.KyotoCabinetDatabase;
import retrieval.server.exception.TestsVectorsNotFoundServerException;
import retrieval.server.index.main.kyoto.KyotoCabinetHashTableMultipleFile;
import retrieval.testvector.TestVectorCentralServer;
import retrieval.testvector.TestVectorListCentralServer;
import retrieval.testvector.TestVectorListServer;
import retrieval.testvector.TestVectorServer;

/**
 * This class implement a test vector reader (interface ReadTestVector).
 * A test vector is a list of association: <Pixel;Value>.
 * Value of each Pixels will be compare with Value
 * @author Loic Rollus
 **/
public class TestVectorReading {

    static Logger logger = Logger.getLogger(TestVectorReading.class);

    /**
     * Read test vector for a client (central server)
     * @param directory Directory
     * @return Test vector central server list
     * @throws TestsVectorsNotFoundServerException Cannot read tests vectors
     */
    public static TestVectorListCentralServer readClient(String directory) throws TestsVectorsNotFoundServerException {

        logger.debug("readClient:" + directory);
        TestVectorListCentralServer lists = new TestVectorListCentralServer();
        File f = new File(directory);
        if (!f.exists()) {
            throw new TestsVectorsNotFoundServerException();
        }

        //read only xml files
        File[] fs = f.listFiles(new XMLFileFilter());
        Arrays.sort(fs, NameFileComparator.NAME_COMPARATOR);
        logger.debug("readClient: read...");

        for (int i = 0; i < fs.length; i++) {

            try {

                List<String> key = new ArrayList<String>();
                List<String> val = new ArrayList<String>();
                List<String> pos = new ArrayList<String>();
                String storeName = fillTestVector(fs[i], key, val, pos);

                logger.debug("readClient: [" + i + "]" + storeName);

                TestVectorCentralServer tv = new TestVectorCentralServer(storeName, key, val, pos);
                lists.add(tv);

            } catch (Exception e) {
                throw new TestsVectorsNotFoundServerException(e.toString());
            }
            lists.sort();
        }
        return lists;

    }

    /**
     * Read test vector for a client (central server)
     * @param directory Directory
     * @param readIndex If true, read index if already exists
     * @param config Configuration object for index store
     * @return Tests vectors server list
     * @throws TestsVectorsNotFoundServerException Cannot read tests vectors
     */
    public static TestVectorListServer readServer(String idServer,String directory, ConfigServer config,Object database) throws Exception {

        logger.info("readServer:" + directory);
        TestVectorListServer lists = new TestVectorListServer();

        File f = new File(directory);
        if (!f.exists()) {
            logger.error("tests vector not found in "+directory);
            throw new TestsVectorsNotFoundServerException("File " + f + " not found");
        }

        //read only xml files
        File[] fs = f.listFiles(new XMLFileFilter());
        logger.info("readClient: fs="+fs.length);
        Arrays.sort(fs, NameFileComparator.NAME_COMPARATOR);
        logger.info("readClient:"+Arrays.toString(fs));
        config.setNumberOfTestVector(fs.length);

        
        //init here client for redis/nessDB
        
        for (int i = 0; i < fs.length; i++) {
                List<String> key = new ArrayList<String>();
                List<String> val = new ArrayList<String>();
                List<String> pos = new ArrayList<String>();
                logger.info("fillTestVector");
                String storeName = fillTestVector(fs[i], key, val, pos);

                logger.info("readClient: [" + i + "]" + storeName);
                TestVectorServer tv = new TestVectorServer(idServer,storeName, key, val, pos, config,database);
                lists.add(tv);
            lists.sort();
        }
        return lists;
    }


    /**
     * Read tests vector from xml files
     * @param f File f
     * @param key Key (pixel)
     * @param val Value (threshold)
     * @param pos Position (position)
     * @return Test vector name
     * @throws Exception Error during the read of the file
     */
    public static String fillTestVector(File f, List<String> key, List<String> val, List<String> pos) throws Exception {
        logger.info("fillTestVector from "+ f);
        org.jdom.Document document;
        Element racine;
        
        logger.info("SAXBuilder");
        SAXBuilder sxb = new SAXBuilder();
        logger.info("init SAXBuilder");
        /* Structure of a test vector xml file
         *<?xml version="1.0" encoding="UTF-8"?>
         *<vector>
         *<value key="..." value="..." position="..."/>
         *<value key="..." value="..." position="..."/>
         *...
         * </vector>
         */

        document = sxb.build(f);
        racine = document.getRootElement();
        List listAssoc = racine.getChildren("value");
        String storeName = racine.getAttributeValue("store");
        Iterator it = listAssoc.iterator();
        while (it.hasNext()) {
            Element assoc = (Element) it.next();
            key.add(assoc.getAttributeValue("key"));
            val.add(assoc.getAttributeValue("value"));
            pos.add(assoc.getAttributeValue("position"));
        }
        
        return storeName;
    }
}

/*
 * Filter for xml files
 * @author Loic Rollus
 */
class XMLFileFilter implements FileFilter {

    public boolean accept(File pathname) {
        if (pathname.getName().toLowerCase().endsWith(".xml")) {
            return true;
        } else {
            return false;
        }
    }
}
