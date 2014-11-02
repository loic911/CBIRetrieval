/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval.storage.index.path;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import retrieval.config.ConfigServer;
import retrieval.multiserver.globaldatabase.GlobalDatabase;
import retrieval.multiserver.globaldatabase.KyotoCabinetDatabase;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class KyotoCabinetPathIndexSingleFileTest extends KyotoCabinetPathIndexTestAbstract{
    
    public KyotoCabinetPathIndexSingleFileTest() {
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
        GlobalDatabase database = new KyotoCabinetDatabase(config);
        mainIndex = new KyotoCabinetPathIndexSingleFile(database,"0"); 
        } catch(Exception e) {
            e.printStackTrace();
        }        
    }
    
    @After
    public void tearDown() {
    }
}
