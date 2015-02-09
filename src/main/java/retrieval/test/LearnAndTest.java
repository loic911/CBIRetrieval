package retrieval.test;

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
        String learnPath = "/media/DATA_/image/cropSmall";
        String testPath = "/media/DATA_/image/cropSmall";

        int numberOfStorage = 8;

        ConfigServer cs = new ConfigServer("config/ConfigServer.prop");
        cs.setStoreName("REDIS");
        cs.setIndexPath("redisindex");
        cs.setNumberOfPatch(200);
        cs.setNumberOfTV(10);

        ConfigClient cc = new ConfigClient("config/ConfigClient.prop");
        cc.setNumberOfTV(10);
        cc.setNumberOfPatch(200);


        List<String> indexFiles = new ArrayList<String>();
        FileUtils.listFiles(new File(learnPath), indexFiles);
        Queue<String> queue = new ConcurrentLinkedQueue<String>();
        for(String path : indexFiles) {
            queue.add(path);
        }

        List<String> searchFiles = new ArrayList<String>();
        FileUtils.listFiles(new File(testPath), searchFiles);

        RetrievalServer server = new RetrievalServer(cs,"test",false);

        List<RetrievalIndexer> indexers = new ArrayList<RetrievalIndexer>();
        List<IndexMultiServerThread> threads = new ArrayList<IndexMultiServerThread>();
        for(int i=0;i<numberOfStorage;i++) {
            server.createStorage(i+"");
            RetrievalIndexer ri = new RetrievalIndexerLocalStorage(server.getStorage(i+""),false);
            indexers.add(ri);
            threads.add(new IndexMultiServerThread(ri,queue));
            threads.get(i).start();
        }

        for(int i=0;i<threads.size();i++) {
            threads.get(i).join();
        }


        RetrievalClient client = new RetrievalClient(cc, server);


        for(int i=0;i<searchFiles.size();i++) {
            ResultsSimilarities rs = client.search(ImageIO.read(new File(searchFiles.get(i))), 30);

            System.out.println("*******************************************************");
            System.out.println("SEARCH="+searchFiles.get(i));
            System.out.println("RESULTS="+rs);
            System.out.println("*******************************************************");
        }


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
