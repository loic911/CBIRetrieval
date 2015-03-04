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
import retrieval.utils.FileUtils;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by lrollus on 09/02/15.
 */
public class LearnAndTest {

    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();
        PropertyConfigurator.configure("log4j.properties");

        String learnPath = "/media/DATA_/image/cropClient";
        String testPath = "/media/DATA_/image/cropClient";

//        Map<String,String> annotationsByProject = buildMapFromListing("/media/DATA_/image/annotationterms.csv",1);
//        Map<String,String> annotationsByTerm = buildMapFromListing("/media/DATA_/image/annotationterms.csv",2);

        int numberOfStorage = 4;
        int numberOfSearchThreads = 1;

        ConfigServer cs = new ConfigServer("config/ConfigServer.prop");
        cs.setStoreName("REDIS"); //KYOTOSINGLEFILE
        cs.setIndexPath("index");
        cs.setNumberOfPatch(200);
        cs.setNumberOfTV(5);

        ConfigClient cc = new ConfigClient("config/ConfigClient.prop");
        cc.setNumberOfTV(5);
        cc.setNumberOfPatch(200);

        System.out.println("***************************************************");

        List<String> indexFiles = new ArrayList<String>();
        FileUtils.listFiles(new File(learnPath), indexFiles);
        Collections.sort(indexFiles);
        indexFiles = indexFiles.subList(0,Math.min(indexFiles.size(), 50000));
        Queue<String> queueIndex = new ConcurrentLinkedQueue<String>();
        for(String path : indexFiles) {
            queueIndex.add(path);
        }

        List<String> searchFiles = new ArrayList<String>();
        FileUtils.listFiles(new File(testPath), searchFiles);
        Collections.sort(searchFiles);
        searchFiles = searchFiles.subList(0,Math.min(searchFiles.size(), 1000));
        Queue<String> queueSearch = new ConcurrentLinkedQueue<String>();
        for(String path : searchFiles) {
            queueSearch.add(path);
        }

        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("indexFiles="+queueIndex.size());
        System.out.println("searchFiles="+queueSearch.size());
        System.out.println("***************************************************");

        Queue<Long> timesIndex = new ConcurrentLinkedQueue<Long>();
        RetrievalServer server = new RetrievalServer(cs,"test",false);

        Long start = System.currentTimeMillis();
        if(server.getSize()==0) {
            List<IndexMultiServerThread> threads = new ArrayList<IndexMultiServerThread>();
            for(int i=0;i<numberOfStorage;i++) {
                server.createStorage(i+"");
                RetrievalIndexer ri = new RetrievalIndexerLocalStorage(server.getStorage(i+""),true);
                IndexMultiServerThread thread = new IndexMultiServerThread(ri,queueIndex,timesIndex);
                thread.start();
                threads.add(thread);
            }

            for(int i=0;i<threads.size();i++) {
                System.out.println("WAIT FOR THREAD " + i);
                threads.get(i).join();
            }
        }
        Long totalTimeIndex = System.currentTimeMillis() - start;


        RetrievalClient client = new RetrievalClient(cc, server);

        start = System.currentTimeMillis();

        Queue<Long> timesSearch = new ConcurrentLinkedQueue<Long>();

        ConcurrentHashMap<Long,ResultsSimilarities> results = new ConcurrentHashMap<Long,ResultsSimilarities>();
        List<SearchMultiServerThread> threads = new ArrayList<SearchMultiServerThread>();
        for(int i=0;i<numberOfSearchThreads;i++) {
            SearchMultiServerThread thread = new SearchMultiServerThread(results,new RetrievalClient(cc, server),queueSearch,numberOfSearchThreads,searchFiles.size(),timesSearch);
            thread.start();
            threads.add(thread);
        }
        for(int i=0;i<threads.size();i++) {
            System.out.println("WAIT FOR THREAD " + i);
            threads.get(i).join();
        }


        int total = 0;
        int positive = 0;
        for(Map.Entry<Long,ResultsSimilarities> map : results.entrySet()) {
            Long idImage = map.getKey();
            ResultsSimilarities rs = map.getValue();

            if(rs.getResults().size()>0 && (rs.getResults().get(0).getId().equals(idImage))) {
                positive++;
            }
            total++;
        }

        Long timeSearch = 0l;
        int sizeSearch = timesSearch.size();
        while(!timesSearch.isEmpty()) {
            timeSearch = timeSearch+timesSearch.poll();
        }

        Long timeIndex = 0l;
        int sizeIndex = timesIndex.size();
        while(!timesIndex.isEmpty()) {
            timeIndex = timeIndex+timesIndex.poll();
        }

        System.out.println("total="+total);
        System.out.println("positive="+positive);
        System.out.println("%="+((double)positive/(double)total));
        System.out.println(searchFiles.size() + "images index");
        System.out.println(indexFiles.size() + "images search");
        System.out.println((totalTimeIndex) + "ms for index");
        System.out.println((System.currentTimeMillis()-start) + "ms for search");
        System.out.println(((double)timeIndex/(double)sizeIndex) + "ms for index / image");
        System.out.println(((double)timeSearch/(double)sizeSearch) + "ms for search / image");

    }


    static Map<String,String> buildMapFromListing(String path, int valuePosition) throws Exception {
        Map<String,String> map = new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                map.put(line.split(",")[0],line.split(",")[valuePosition]);
                line = br.readLine();
            }
        }
        return map;
    }

}


class IndexMultiServerThread extends Thread {

    private Queue<String> queue;
    private RetrievalIndexer indexer;
    private Queue<Long> times;

    public IndexMultiServerThread(RetrievalIndexer indexer,Queue<String> queue,Queue<Long> times) {
        this.queue = queue;
        this.indexer = indexer;
        this.times = times;
    }

    public void run() {
        try {
            while (!queue.isEmpty()) {
                String path = queue.poll();
                File imageFile = new File(path);
                System.out.println(indexer + " => Index File:"+imageFile.getName().split("\\.")[0]);
                String imageName = imageFile.getName().split("\\.")[0];
                Long start = System.currentTimeMillis();
                indexer.index(imageFile, Long.parseLong(imageName));
                this.times.add(System.currentTimeMillis()-start);
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}


class SearchMultiServerThread extends Thread {

    private Queue<String> queue;
    private ConcurrentHashMap<Long,ResultsSimilarities> results;
    private RetrievalClient client;
    private int numberOfThread;
    private int numberOfItems;
    private Queue<Long> times;


    public SearchMultiServerThread(ConcurrentHashMap<Long,ResultsSimilarities> results,RetrievalClient search,Queue<String> queue, int numberOfThread,int numberOfItems,Queue<Long> times) {
        this.results = results;
        this.queue = queue;
        this.client = search;
        this.numberOfThread = numberOfThread;
        this.numberOfItems = numberOfItems;
        this.times = times;
    }

    public void run() {
        try {
            int i = 0; //ConcurrentLinkedQueue.size is not constant, so i*8
            while (!queue.isEmpty()) {
                String path = queue.poll();

                Long start = System.currentTimeMillis();

                ResultsSimilarities rs = client.search(ImageIO.read(new File(path)), 10);
                this.times.add(System.currentTimeMillis()-start);
                File imageFile = new File(path);
                String imageName = imageFile.getName().split("\\.")[0];
                this.results.put(Long.parseLong(imageName),rs);
//                System.out.println("*********************** approx "+ (i*numberOfThread) + "/" + (numberOfItems)+" *************************");
                System.out.println("SEARCH="+path);
                System.out.println("RESULTS="+rs);
//                System.out.println("*******************************************************");
//                System.out.println("*******************************************************");
                i++;

            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
