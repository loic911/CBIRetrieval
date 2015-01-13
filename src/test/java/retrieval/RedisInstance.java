/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrieval.config.ConfigServer;
import retrieval.utils.FileUtils;

/**
 *
 * @author lrollus
 */
public class RedisInstance {

    List<Process> processRedis = new ArrayList<Process>();

    public static int PORT = 6000;
    private int numberOfRedis;
    static String REDIS_PATH = "testdata/redis";
    static String REDIS_EXEC_PATH = REDIS_PATH+"/redis-server";
    static String REDIS_CONF = REDIS_PATH+"/redis.conf";
    static String DATA_PATH = "testdata/index/redis";

    public RedisInstance() {
        this(1);
    }

    public RedisInstance(int numberOfRedis) {
        this.numberOfRedis = numberOfRedis;
    }

    public int getPort() {
        return getPort(0);
    }

    public int getPort(int incr) {
        return PORT+incr;
    }


    public void initRedis() throws Exception {
        System.out.println("initRedis");

        Runtime runtime = Runtime.getRuntime();

        for(int i=0;i<numberOfRedis;i++) {
            int portRedis = PORT+i;
            System.out.println("Redis: open database localhost:" + portRedis);
            String command = REDIS_EXEC_PATH + " " +  REDIS_CONF + " --port " + portRedis + " --dir "+DATA_PATH+" --dbfilename " + portRedis+".db";
            System.out.println(command);
            processRedis.add(runtime.exec(command));
        }
    }

    public void deleteRedisData() throws Exception {
        FileUtils.deleteAllFilesRecursively(new File(DATA_PATH));
        new File(DATA_PATH).mkdirs();
    }


    public void killRedis() {
        try {
            killRedis(0); } catch(Exception e) {}
    }
    public void killRedisAll() throws Exception{
        for(int i=0;i<numberOfRedis;i++) {
            try {killRedis(i); } catch(Exception e) {}
        }
    }

    private void killRedis(int process) throws Exception{
         ProcessUtils.killUnixProcess(processRedis.get(process));
        try {processRedis.get(process).destroy();}catch(Exception e) {}
    }


}
