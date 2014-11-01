/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval;

import java.io.File;
import retrieval.config.ConfigServer;
import retrieval.utils.FileUtils;
import retrieval.utils.ProcessUtils;

/**
 *
 * @author lrollus
 */
public class RedisInstance {
    ConfigServer config;
    Process processRedis1;
    Process processRedis2;

    public RedisInstance(ConfigServer config) {
        this.config = config;
    }

    public void initRedis() throws Exception {
        System.out.println("RetrievalMultiServerRedisTest");
        config.setStoreName("REDIS");
        config.setIndexPath(config.getIndexPath() + config.getStoreName() + "/");
        System.out.println("DELETE FILES");
        FileUtils.deleteAllFilesRecursively(new File(config.getIndexPath()));
        System.out.println("RUNTIME");
        Runtime runtime = Runtime.getRuntime();
        processRedis1 = runtime.exec("testdata/redis-2.2.13/src/redis-server testdata/redis-2.2.13/redis1.conf");
        System.out.println("PROCESS:"+ProcessUtils.getUnixPID(processRedis1));
    }

    public void initRedisAll() throws Exception {
        config = ConfigServer.getConfigServerForTest();
        System.out.println("RetrievalMultiServerRedisTest");
        config.setStoreName("REDIS");
        config.setIndexPath(config.getIndexPath() + config.getStoreName() + "/");
         System.out.println("DELETE FILES");
        FileUtils.deleteAllFilesRecursively(new File(config.getIndexPath()));
         System.out.println("RUNTIME");
        Runtime runtime = Runtime.getRuntime();
        processRedis1 = runtime.exec("testdata/redis-2.2.13/src/redis-server testdata/redis-2.2.13/redis1.conf");
        processRedis2 = runtime.exec("testdata/redis-2.2.13/src/redis-server testdata/redis-2.2.13/redis2.conf");
        System.out.println("PROCESS:"+ProcessUtils.getUnixPID(processRedis1) + " " + ProcessUtils.getUnixPID(processRedis2));
    }



    public void killRedis() {
        try {killRedis1(); } catch(Exception e) {}
    }
    public void killRedisAll() throws Exception{
        try {killRedis1(); } catch(Exception e) {}
        try {killRedis2(); } catch(Exception e) {}
    }

    private void killRedis1() throws Exception{
         ProcessUtils.killUnixProcess(processRedis1);
        try {processRedis1.destroy();}catch(Exception e) {}
    }
    private void killRedis2() throws Exception{
         ProcessUtils.killUnixProcess(processRedis2);
        try {processRedis2.destroy();}catch(Exception e) {}
    }

}
