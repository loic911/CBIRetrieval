/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.server.index.path;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import retrieval.config.ConfigServer;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class KyotoCabinetPathIndexMultipleFileTest extends KyotoCabinetPathIndexTestAbstract {
    

    public KyotoCabinetPathIndexMultipleFileTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        enableLog();       
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        try {
        config = new ConfigServer("config/ConfigServer.prop");
        FileUtils.deleteAllSubFilesRecursively(new File(config.getIndexPath()));
        mainIndex = new KyotoCabinetPathIndexMultipleFile(config, "0"); 
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @After
    public void tearDown() {
    }

}
