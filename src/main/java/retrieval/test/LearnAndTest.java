package retrieval.test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import retrieval.client.RetrievalClient;
import retrieval.config.ConfigClient;
import retrieval.config.ConfigServer;
import retrieval.dist.ResultsSimilarities;
import retrieval.indexer.RetrievalIndexer;
import retrieval.indexer.RetrievalIndexerLocalStorage;
import retrieval.server.RetrievalServer;
import retrieval.storage.index.ResultSim;
import retrieval.utils.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by lrollus on 09/02/15.
 */
public class LearnAndTest {

    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();
        PropertyConfigurator.configure("log4j.properties");

        String learnPath = "/media/DATA_/image/crop";
        String testPath = "/media/DATA_/image/crop";

        int numberOfStorage = 8;
        int numberOfSearchThreads = 8;

        ConfigServer cs = new ConfigServer("config/ConfigServer.prop");
        cs.setStoreName("MEMORY");
        cs.setIndexPath("redisindex");
        cs.setNumberOfPatch(200);
        cs.setNumberOfTV(5);

        ConfigClient cc = new ConfigClient("config/ConfigClient.prop");
        cc.setNumberOfTV(5);
        cc.setNumberOfPatch(200);


        List<String> indexFiles = new ArrayList<String>();
        FileUtils.listFiles(new File(learnPath), indexFiles);
        indexFiles = indexFiles.subList(0,Math.min(indexFiles.size(), 50000));
        Queue<String> queueIndex = new ConcurrentLinkedQueue<String>();
        for(String path : indexFiles) {
            queueIndex.add(path);
        }

        List<String> searchFiles = new ArrayList<String>();
        FileUtils.listFiles(new File(testPath), searchFiles);
        searchFiles = searchFiles.subList(0,Math.min(searchFiles.size(), 100));
        Queue<String> queueSearch = new ConcurrentLinkedQueue<String>();
        for(String path : indexFiles) {
            queueSearch.add(path);
        }

        RetrievalServer server = new RetrievalServer(cs,"test",false);

        Long start = System.currentTimeMillis();
        if(server.getSize()==0) {
            List<IndexMultiServerThread> threads = new ArrayList<IndexMultiServerThread>();
            for(int i=0;i<numberOfStorage;i++) {
                server.createStorage(i+"");
                RetrievalIndexer ri = new RetrievalIndexerLocalStorage(server.getStorage(i+""),true);
                IndexMultiServerThread thread = new IndexMultiServerThread(ri,queueIndex);
                thread.start();
                threads.add(thread);
            }

            for(int i=0;i<threads.size();i++) {
                System.out.println("WAIT FOR THREAD " + i);
                threads.get(i).join();
            }
        }
        Long timeIndex = System.currentTimeMillis() - start;


        RetrievalClient client = new RetrievalClient(cc, server);

        start = System.currentTimeMillis();

        List<SearchMultiServerThread> threads = new ArrayList<SearchMultiServerThread>();
        for(int i=0;i<numberOfSearchThreads;i++) {
            SearchMultiServerThread thread = new SearchMultiServerThread(new RetrievalClient(cc, server),queueSearch,numberOfSearchThreads,searchFiles.size());
            thread.start();
            threads.add(thread);
        }
        for(int i=0;i<threads.size();i++) {
            System.out.println("WAIT FOR THREAD " + i);
            threads.get(i).join();
        }
        System.out.println(searchFiles.size() + "images index");
        System.out.println(indexFiles.size() + "images search");
        System.out.println((timeIndex) + "ms for index");
        System.out.println((System.currentTimeMillis()-start) + "ms for search");
        System.out.println((timeIndex/indexFiles.size()) + "ms for index / image");
        System.out.println(((System.currentTimeMillis()-start)/searchFiles.size()) + "ms for search / image");

    }
}


class IndexMultiServerThread extends Thread {

    private Queue<String> queue;
    private RetrievalIndexer indexer;

    public IndexMultiServerThread(RetrievalIndexer indexer,Queue<String> queue) {
        this.queue = queue;
        this.indexer = indexer;
    }

    public void run() {
        try {
            while (!queue.isEmpty()) {
                String path = queue.poll();
                File imageFile = new File(path);
                System.out.println(indexer + " => Index File:"+imageFile.getName().split("\\.")[0]);
                String imageName = imageFile.getName().split("\\.")[0];
                indexer.index(imageFile, Long.parseLong(imageName));
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}


class SearchMultiServerThread extends Thread {

    private Queue<String> queue;
    private RetrievalClient client;
    private int numberOfThread;
    private int numberOfItems;

    public SearchMultiServerThread(RetrievalClient search,Queue<String> queue, int numberOfThread,int numberOfItems) {
        this.queue = queue;
        this.client = search;
        this.numberOfThread = numberOfThread;
        this.numberOfItems = numberOfItems;
    }

    public void run() {
        try {
            int i = 0; //ConcurrentLinkedQueue.size is not constant, so i*8
            while (!queue.isEmpty()) {
                String path = queue.poll();

                ResultsSimilarities rs = client.search(ImageIO.read(new File(path)), 10);

                System.out.println("*********************** approx "+ (i*numberOfThread) + "/" + (numberOfItems)+" *************************");
                System.out.println("SEARCH="+path);
                System.out.println("RESULTS="+rs);
                System.out.println("*******************************************************");
                System.out.println("*******************************************************");
                i++;

            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
