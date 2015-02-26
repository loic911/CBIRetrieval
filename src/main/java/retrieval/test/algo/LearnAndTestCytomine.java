package retrieval.test.algo;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Created by lrollus on 09/02/15.
 */
public class LearnAndTestCytomine {

//    static int MAX_INDEX = 1000; //Integer.MAX_VALUE;
    static int MAX_SEARCH = 10000; //Integer.MAX_VALUE;

    static int MAX_INDEX = Integer.MAX_VALUE;
//    static int MAX_SEARCH = Integer.MAX_VALUE;

    String learnPath = "/media/DATA_/backup/retrieval/set";
    String testPath = "/media/DATA_/backup/retrieval/set";

    Map<String,String> projectByAnnotation;// = buildMapFromListing("/media/DATA_/backup/retrieval/annotationterms_filtered.csv",1);
    Map<String,String> termByAnnotation;// = buildMapFromListing("/media/DATA_/backup/retrieval/annotationterms_filtered.csv",2);

    int numberOfSearchThreads = 8;

    Queue<String> queueIndex;
    Map<String,List<String>> listIndexByProject;
    Queue<String> queueSearch;

    Queue<Long> timesIndex = new ConcurrentLinkedQueue<Long>();
    Queue<Long> timesSearch = new ConcurrentLinkedQueue<Long>();
    Map<Long,ResultsSimilarities> results = new ConcurrentHashMap<>();

    RetrievalServer server;

    ConfigServer cs;
    ConfigClient cc;

    Long totalTimeIndex;
    Long totalTimeSearch;



    public LearnAndTestCytomine() throws Exception {
        cs = new ConfigServer("config/ConfigServer.prop");
        cs.setStoreName("MEMORY"); //KYOTOSINGLEFILE
        cs.setIndexPath("index");
        cs.setNumberOfPatch(100);
        cs.setNumberOfTV(3);

        cc = new ConfigClient("config/ConfigClient.prop");
        cc.setNumberOfTV(3);
        cc.setNumberOfPatch(100);

        projectByAnnotation = buildMapFromListing("/media/DATA_/backup/retrieval/annotationterms_filtered.csv",1);
        termByAnnotation = buildMapFromListing("/media/DATA_/backup/retrieval/annotationterms_filtered.csv",2);

        server = new RetrievalServer(cs,"test",false);
    }

    public void index() throws Exception {
        System.out.println("***************************************************");

        queueIndex = addAllFilesToQueue(learnPath,MAX_INDEX);

        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("indexFiles="+queueIndex.size());
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");

        listIndexByProject = new  HashMap<>();
        while(!queueIndex.isEmpty()) {
            String path = queueIndex.poll();
            String imageName = new File(path).getName().split("\\.")[0];
            String project = projectByAnnotation.get(imageName);

            if(!listIndexByProject.containsKey(project)) {
                listIndexByProject.put(project, new ArrayList<>());
            }

            List<String> images = listIndexByProject.get(project);
            images.add(path);
            listIndexByProject.put(project,images);
        }




        Set<String> projects = projectByAnnotation.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());

        List<String> projectsAll = projectByAnnotation.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());

        System.out.println("projectsAll="+projectsAll.size());

        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        for(String project : projects) {
            System.out.println(project);
            System.out.println(project+"="+listIndexByProject.get(project).size());

        }
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");
//        Map<String,Long> projectsCount = projectsAll.stream().collect(Collectors.toMap(String::toString,p->1L,(oldCount, newCount) -> oldCount++));

        Map<String,List<String>> projectsCount2 = projectsAll.stream().collect(Collectors.groupingBy(String::toString));

//        System.out.println("projectsCount2="+projectsCount2);
//
//        System.exit(0);

        Long start = System.currentTimeMillis();
        if(server.getSize()==0) {
            List<IndexMultiServerThread> threads = new ArrayList<IndexMultiServerThread>();
            for(String project : projects) {
                server.createStorage(project);
                RetrievalIndexer ri = new RetrievalIndexerLocalStorage(server.getStorage(project),true);
                IndexMultiServerThread thread = new IndexMultiServerThread(ri,listIndexByProject.get(project), timesIndex,termByAnnotation,projectsCount2.get(project).size());
                thread.start();
                threads.add(thread);
            }

            for(int i=0;i<threads.size();i++) {
                System.out.println("WAIT FOR THREAD " + i);
                threads.get(i).join();
            }
        }
        totalTimeIndex = System.currentTimeMillis() - start;

        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("totalTimeIndex="+totalTimeIndex+"ms");
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");



    }

    public void search() throws Exception {
        queueSearch = addAllFilesToQueue(testPath,MAX_SEARCH);
        int numberOfSearch = queueSearch.size();
        Long start = System.currentTimeMillis();
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("indexFiles="+numberOfSearch);
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");

        List<SearchMultiServerThread> threads = new ArrayList<SearchMultiServerThread>();
        for(int i=0;i<numberOfSearchThreads;i++) {
            SearchMultiServerThread thread = new SearchMultiServerThread(results,new RetrievalClient(cc, server),queueSearch,numberOfSearchThreads,numberOfSearch,timesSearch);
            thread.start();
            threads.add(thread);
        }
        for(int i=0;i<threads.size();i++) {
            System.out.println("WAIT FOR THREAD " + i);
            threads.get(i).join();
        }
        totalTimeSearch = System.currentTimeMillis() - start;


    }


    public void printStats() {

        LongSummaryStatistics statsIndex = timesIndex.stream().
                collect(LongSummaryStatistics::new,
                        LongSummaryStatistics::accept,
                        LongSummaryStatistics::combine
                );

        LongSummaryStatistics statsSearch = timesSearch.stream().
                collect(LongSummaryStatistics::new,
                        LongSummaryStatistics::accept,
                        LongSummaryStatistics::combine
                );


        DoubleSummaryStatistics statsFirstSame =  new DoubleSummaryStatistics();
        DoubleSummaryStatistics statsFirst =  new DoubleSummaryStatistics();
        DoubleSummaryStatistics statsFive =  new DoubleSummaryStatistics();

        for(Map.Entry<Long,ResultsSimilarities> map : results.entrySet()) {
            Long idImage = map.getKey();
            ResultsSimilarities rs = map.getValue();
            List<ResultSim> results = rs.getResults();
            List<ResultSim> resultsWithoutRequestImage = results.stream().filter(x -> !x.getId().equals(idImage)).collect(Collectors.toList());


            if(results.size()>0) {
                //check if first
                statsFirstSame.accept(results.get(0).getId().equals(idImage)?1:0);
            } else {
                statsFirstSame.accept(0);
            }

            if(resultsWithoutRequestImage.size()>0) {
                //check if first has same term
                String term = results.get(0).getProperties().get("term");
                statsFirst.accept(term.equals(termByAnnotation.get(idImage+""))?1:0);
            } else {
                statsFirst.accept(0);
            }

            if(resultsWithoutRequestImage.size()>0) {
                //check if 5 first has same term
                DoubleSummaryStatistics subResults = new DoubleSummaryStatistics();
                for(int i=0;i<5;i++) {
                    if(i<resultsWithoutRequestImage.size()) {
                        String term = results.get(i).getProperties().get("term");
                        subResults.accept(term.equals(termByAnnotation.get(idImage+""))?1:0);
                    } else {
                        subResults.accept(0); //no results
                    }
                }
                statsFive.accept(subResults.getAverage());

            } else {
                statsFive.accept(0);
            }
        }

        System.out.println("Total time INDEX:"+totalTimeIndex);
        System.out.println("Total time SEARCH:"+totalTimeSearch);

        System.out.println("ALL INDEX:"+statsIndex);
        System.out.println("ALL SEARCH:"+statsSearch);

        System.out.println("STATS result 1 = req annotation:"+statsFirstSame);
        System.out.println("STATS result-(req annotation) 1  = req term :"+statsFirst);
        System.out.println("STATS result-(req annotation) 5  = req term :"+statsFive);
    }









    private Queue<String> addAllFilesToQueue(String path, int max) {
        Queue<String> queueIndex  = new ConcurrentLinkedQueue<String>();
        List<String> indexFiles = new ArrayList<String>();
        FileUtils.listFiles(new File(path), indexFiles);
        Collections.shuffle(indexFiles);

        indexFiles = indexFiles.subList(0,Math.min(indexFiles.size(), max));
        for(String p : indexFiles) {
            queueIndex.add(p);
        }
        return queueIndex;
    }








    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();
        PropertyConfigurator.configure("log4j.properties");

        LearnAndTestCytomine cyto = new LearnAndTestCytomine();
        cyto.index();
        cyto.search();
        cyto.printStats();
    }


    static Map<String,String> buildMapFromListing(String path, int valuePosition) throws Exception {
        Map<String,String> map = new HashMap<>();
        boolean firstLine = true; //header
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                if(!firstLine) {
                    map.put(line.split(",")[0],line.split(",")[valuePosition]);

                }
                firstLine = false;
                line = br.readLine();
            }
        }
        return map;
    }

}


class IndexMultiServerThread extends Thread {

    private List<String> list;
    private RetrievalIndexer indexer;
    private Queue<Long> times;
    private Map<String,String> annotationsByTerm;
    private int numberOfItems;

    public IndexMultiServerThread(RetrievalIndexer indexer,List<String> list,Queue<Long> times,Map<String,String> annotationsByTerm,int numberOfItems) {
        this.list = list;
        this.indexer = indexer;
        this.times = times;
        this.annotationsByTerm = annotationsByTerm;
        this.numberOfItems = numberOfItems;
    }

    public void run() {
        try {
            int i=0;
            for (String path : list) {

                File imageFile = new File(path);
                //System.out.println(indexer + " => Index File:"+imageFile.getName().split("\\.")[0]);
                String imageName = imageFile.getName().split("\\.")[0];
                Long start = System.currentTimeMillis();
                Map<String,String> properties = new TreeMap<>();
                properties.put("term",annotationsByTerm.get(imageName));
                indexer.index(imageFile, Long.parseLong(imageName),properties);
                this.times.add(System.currentTimeMillis()-start);
                System.out.println("*********************** index approx "+ (i) + "/" + (numberOfItems)+" *************************");
                i++;
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}


class SearchMultiServerThread extends Thread {

    private Queue<String> queue;
    private Map<Long,ResultsSimilarities> results;
    private RetrievalClient client;
    private int numberOfThread;
    private int numberOfItems;
    private Queue<Long> times;


    public SearchMultiServerThread(Map<Long,ResultsSimilarities> results,RetrievalClient search,Queue<String> queue, int numberOfThread,int numberOfItems,Queue<Long> times) {
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
                ResultsSimilarities rs = client.search(ImageIO.read(new File(path)), 15);
                this.times.add(System.currentTimeMillis()-start);
                File imageFile = new File(path);
                String imageName = imageFile.getName().split("\\.")[0];
                this.results.put(Long.parseLong(imageName),rs);
                System.out.println("*********************** search approx "+ (i*numberOfThread) + "/" + (numberOfItems)+" *************************");
                i++;

            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
