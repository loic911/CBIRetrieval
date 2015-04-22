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
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
/*
 * Copyright 2015 ROLLUS Loïc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class LearnAndTestCytomine {

    //static int MAX_INDEX = 100; //Integer.MAX_VALUE;
    //static int MAX_SEARCH = 10; //Integer.MAX_VALUE;

    static boolean SEARCH_ON_PROJECT_WITH_SAME_ONTOLOGY = true;

    //static boolean SINGLE_STORAGE = false; => TODO IMPLEMENT

    static int MAX_INDEX = Integer.MAX_VALUE;
    static int MAX_SEARCH = Integer.MAX_VALUE;

    String learnPath = "/media/DATA_/backup/retrieval/set";
    String testPath = "/media/DATA_/backup/retrieval/set";

    Map<String,String> projectByAnnotation;// = buildMapFromListing("/media/DATA_/backup/retrieval/annotationterms_filtered.csv",1);
    Map<String,String> termByAnnotation;// = buildMapFromListing("/media/DATA_/backup/retrieval/annotationterms_filtered.csv",2);

    int numberOfSearchThreads = 8;

    Queue<String> queueIndex;
    Map<String,List<String>> listIndexByProject;
    Queue<String> queueSearch;

    Map<String, List<String>> projectsPerOntology = buildProjectPerOntology("/media/DATA_/backup/retrieval/ontology_per_term.csv");
    Map<String, String> ontologyPerProject = buildOntologyPerProject("/media/DATA_/backup/retrieval/ontology_per_term.csv");

    Map<String,List<String>> storagesForAnnotation = new HashMap<>();


    Queue<Long> timesIndex = new ConcurrentLinkedQueue<Long>();
    Queue<Long> timesSearch = new ConcurrentLinkedQueue<Long>();
    Queue<Long> timesSearchSingleThreaded = new ConcurrentLinkedQueue<Long>();
    Map<Long,ResultsSimilarities> results = new ConcurrentHashMap<>();

    RetrievalServer server;

    ConfigServer cs;
    ConfigClient cc;

    Long totalTimeIndex;
    Long totalTimeSearch;



    public LearnAndTestCytomine() throws Exception {
        cs = new ConfigServer("config/ConfigServer.prop");
        cs.setStoreName("MEMORY");
        cs.setIndexPath("index");
        cs.setNumberOfPatch(100);//200
        cs.setNumberOfTV(1);

        cc = new ConfigClient("config/ConfigClient.prop");
        cc.setNumberOfTV(1);
        cc.setNumberOfPatch(100);

        System.out.println("N="+cs.getNumberOfPatch());
        System.out.println("T="+cs.getNumberOfTV());
        System.out.println("STORENAME="+cs.getStoreName());
        System.out.println("COMPRESS="+cs.getIndexCompressThreshold());

        if(cs.getNumberOfPatch() != cc.getNumberOfPatch()) throw new Exception("N is different for client and server");
        if(cs.getNumberOfTV() != cc.getNumberOfTV()) throw new Exception("T is different for client and server");


        projectByAnnotation = buildMapFromListing("/media/DATA_/backup/retrieval/set.csv",1);
        termByAnnotation = buildMapFromListing("/media/DATA_/backup/retrieval/set.csv",2);

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
            //System.out.println(project+"="+listIndexByProject.get(project).size());

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
                if(listIndexByProject.get(project)!=null) {
                    RetrievalIndexer ri = new RetrievalIndexerLocalStorage(server.getStorage(project),true);
                    IndexMultiServerThread thread = new IndexMultiServerThread(ri,listIndexByProject.get(project), timesIndex,termByAnnotation,projectsCount2.get(project).size());
                    thread.start();
                    threads.add(thread);
                }

            }

            for(int i=0;i<threads.size();i++) {
                System.out.println("WAIT FOR THREAD " + i);
                threads.get(i).join();
            }
        }
        totalTimeIndex = System.currentTimeMillis() - start;


        System.out.println("***************************************************");
        System.out.println("***************************************************");
        //server.getInfos().entrySet().forEach(System.out::println);


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

        for(String searchImage : queueSearch) {

            String imageName = new File(searchImage).getName().split("\\.")[0];
            String project = projectByAnnotation.get(imageName);

//            System.out.println("project="+project);
//            System.out.println("ontology="+ontologyPerProject.get(project));
//            System.out.println(ontologyPerProject);

            String ontology = ontologyPerProject.get(project);
            List<String> projects = projectsPerOntology.get(ontology);

//            System.out.println("ontology="+ontology);
//            System.out.println("projects="+projects);
//            System.out.println("imageName="+imageName);

            storagesForAnnotation.put(imageName,projects);
        }



        List<SearchMultiServerThread> threads = new ArrayList<SearchMultiServerThread>();
        for(int i=0;i<numberOfSearchThreads;i++) {
            SearchMultiServerThread thread = new SearchMultiServerThread(results,storagesForAnnotation,new RetrievalClient(cc, server),queueSearch,numberOfSearchThreads,numberOfSearch,timesSearch);
            thread.start();
            threads.add(thread);
        }
        for(int i=0;i<threads.size();i++) {
            System.out.println("WAIT FOR THREAD " + i);
            threads.get(i).join();
        }
        totalTimeSearch = System.currentTimeMillis() - start;


    }

    public void searchSingle() throws Exception {
        Queue<String> queueSearch = addAllFilesToQueue(testPath,1000);
        int numberOfSearch = queueSearch.size();
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("searchfiles="+numberOfSearch);
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        Map<String,List<String>> storagesForAnnotation = new HashMap<>();
        for(String searchImage : queueSearch) {
            String imageName = new File(searchImage).getName().split("\\.")[0];
            String project = projectByAnnotation.get(imageName);
            String ontology = ontologyPerProject.get(project);
            List<String> projects = projectsPerOntology.get(ontology);
            storagesForAnnotation.put(imageName,projects);
        }
        RetrievalClient client = new RetrievalClient(cc, server);
        int i = 0;
        while (!queueSearch.isEmpty()) {
            String path = queueSearch.poll();
            File imageFile = new File(path);
            String imageName = imageFile.getName().split("\\.")[0];
            List<String> storages = null;

            if(LearnAndTestCytomine.SEARCH_ON_PROJECT_WITH_SAME_ONTOLOGY) {
                storages = storagesForAnnotation.get(imageName);
            }
            Long start = System.currentTimeMillis();
            ResultsSimilarities rs = client.search(ImageIO.read(new File(path)), 15,storages);
            timesSearchSingleThreaded.add(System.currentTimeMillis()-start);
            if(i%10==0) {
                System.out.println("*********************** search single thread " + (i) + "/" + (numberOfSearch) + " *************************");
            }
            i++;

        }
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


        LongSummaryStatistics statsSearchSingle = timesSearchSingleThreaded.stream().
                collect(LongSummaryStatistics::new,
                        LongSummaryStatistics::accept,
                        LongSummaryStatistics::combine
                );


        DoubleSummaryStatistics statsFirstSame =  new DoubleSummaryStatistics();
        DoubleSummaryStatistics statsFirst =  new DoubleSummaryStatistics();
        DoubleSummaryStatistics statsTen =  new DoubleSummaryStatistics();
        Map<String,DoubleSummaryStatistics> statsFirstSameByTerm =  new TreeMap<>();
        Map<String,DoubleSummaryStatistics> statsFirstByTerm =  new TreeMap<>();
        Map<String,DoubleSummaryStatistics> statsTenByTerm =  new TreeMap<>();


        for(Map.Entry<Long,ResultsSimilarities> map : results.entrySet()) {
            Long idImage = map.getKey();
            ResultsSimilarities rs = map.getValue();
            List<ResultSim> results = rs.getResults();
            List<ResultSim> resultsWithoutRequestImage = results.stream().filter(x -> !x.getId().equals(idImage)).collect(Collectors.toList());


            double valueFirst = 0;
            if(results.size()>0) {
                //check if first
                valueFirst = results.get(0).getId().equals(idImage)?1:0;
            }
            statsFirstSame.accept(valueFirst);
            addResultForTerm(statsFirstSameByTerm,termByAnnotation.get(idImage+""),valueFirst);


            double valueFirstSameTerm = 0;
            if(resultsWithoutRequestImage.size()>0) {
                //check if first has same term
                String term = resultsWithoutRequestImage.get(0).getProperties().get("term");
                valueFirstSameTerm = term.equals(termByAnnotation.get(idImage + "")) ? 1 : 0;
            }
            statsFirst.accept(valueFirstSameTerm);
            addResultForTerm(statsFirstByTerm,termByAnnotation.get(idImage+""),valueFirstSameTerm);



            double valueTenFirstSameTerm = 0;
            if(resultsWithoutRequestImage.size()>0) {
                //check if 5 first has same term
                DoubleSummaryStatistics subResults = new DoubleSummaryStatistics();
                //System.out.println("****************************");
                for(int i=0;i<10;i++) {
                    //System.out.println("=> " + termByAnnotation.get(idImage+""));
                    if(i<resultsWithoutRequestImage.size()) {
                        String term = resultsWithoutRequestImage.get(i).getProperties().get("term");
                        //System.out.println("====> " + term);
                        subResults.accept((term!=null && term.equals(termByAnnotation.get(idImage+"")))?1:0);
                    } else {
                        subResults.accept(0); //no results
                    }
                }
                //System.out.println("========> " + subResults.getAverage());
                valueTenFirstSameTerm = subResults.getAverage();

            }
            statsTen.accept(valueTenFirstSameTerm);
            addResultForTerm(statsTenByTerm,termByAnnotation.get(idImage+""),valueTenFirstSameTerm);

        }




        String filename = new Date().toString();
        File output = new File("/media/DATA_/backup/retrieval/results/"+filename);

        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(output))) {

                out.print("SEARCH_ON_PROJECT_WITH_SAME_ONTOLOGY = "+ SEARCH_ON_PROJECT_WITH_SAME_ONTOLOGY + " N="+cs.getNumberOfPatch() + "T="+cs.getNumberOfTV() + "STORENAME="+cs.getStoreName() + "COMPRESS="+cs.getIndexCompressThreshold());

                for(Map.Entry<Long,ResultsSimilarities> map : results.entrySet()) {

                    String key = map.getKey()+"";
                    String value = "";
                    for(ResultSim rs : map.getValue().getResults()) {
                        value = value + ";"+rs.getId() +";"+rs.getSimilarities();
                    }
                    out.print(key+value+"\n");

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




        System.out.println("Total time INDEX:"+totalTimeIndex);
        System.out.println("Total time SEARCH:"+totalTimeSearch);

        System.out.println("ALL INDEX:"+statsIndex);
        System.out.println("ALL SEARCH "+numberOfSearchThreads+" thread:"+statsSearch);
        System.out.println("ALL SEARCH SINGLE THREAD:"+statsSearchSingle);

        System.out.println("************************************************************************");
        System.out.println("************************************************************************");
        System.out.println("************************************************************************");
        System.out.println("STATS result 1 = req annotation:"+statsFirstSame);
        statsFirstSameByTerm.entrySet().forEach(System.out::println);
        System.out.println("Average percentage:"+computeAverage(statsFirstSameByTerm));
        System.out.println("************************************************************************");
        System.out.println("************************************************************************");
        System.out.println("************************************************************************");
        System.out.println("STATS result-(req annotation) 1  = req term :"+statsFirst);
        statsFirstByTerm.entrySet().forEach(System.out::println);
        System.out.println("Average percentage:"+computeAverage(statsFirstByTerm));
        System.out.println("************************************************************************");
        System.out.println("************************************************************************");
        System.out.println("************************************************************************");
        System.out.println("STATS result-(req annotation) 10  = req term :"+statsTen);
        statsTenByTerm.entrySet().forEach(System.out::println);
        System.out.println("Average percentage:"+computeAverage(statsTenByTerm));
        System.out.println("************************************************************************");
        System.out.println("************************************************************************");
        System.out.println("************************************************************************");


        System.out.println("Results in " + output.getAbsolutePath());
    }


//    2. >> Aussi vu la distribution très déséquilibrée du nombre d'annotations
//            >> par terme, ce serait bien de calculer aussi en plus ces pourcentages
//    >> moyennés par termes, c-à-d tu calcules le pourcentage pour chaque terme,
//    >> puis à la fin tu fais la somme de ces pourcentages divisé par le nombre
//    >> de termes.

    private Double computeAverage(Map<String,DoubleSummaryStatistics> map) {
        Double sum = map.values().stream()
                .map(DoubleSummaryStatistics::getAverage)
                .reduce(0d, Double::sum);
        return sum/map.size();

//        for(DoubleSummaryStatistics stats : map.values()) {
//            stats
//        }
    }






    private void addResultForTerm(Map<String,DoubleSummaryStatistics> map, String term, Double value) {
        DoubleSummaryStatistics stat = map.get(term);
        if(stat==null) {
            stat = new DoubleSummaryStatistics();
        }
        stat.accept(value);
        map.put(term,stat);
    }

//    private Map<String,Double> buildResultTerms(Map<String,DoubleSummaryStatistics> map) {
////        Aussi vu la distribution très déséquilibrée du nombre d'annotations
////        par terme, ce serait bien de calculer aussi en plus ces pourcentages
////        moyennés par termes, c-à-d tu calcules le pourcentage pour chaque terme,
////                puis à la fin tu fais la somme de ces pourcentages divisé par le nombre
////        de termes.
//        Map<String,Double> results = new HashMap<>();
//        double total = map.values().stream().map(DoubleSummaryStatistics::getAverage).reduce(0d,Double::sum);
//
//        for(Map.Entry entry : map.entrySet()) {
//            results.put(entry.getKey(),)
//        }
//
//
//    }







    private Queue<String> addAllFilesToQueue(String path, int max) {
        Queue<String> queueIndex  = new ConcurrentLinkedQueue<String>();
        List<String> indexFiles = new ArrayList<String>();
        FileUtils.listFiles(new File(path), indexFiles);
        //Collections.sort(indexFiles);
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
        System.gc();
        Thread.sleep(1000);

        cyto.searchSingle();
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

    static Map<String, List<String>> buildProjectPerOntology(String path) throws Exception {
        int POSITION_PROJECT = 0;
        int POSITION_ONTOLOGY = 1;

        List<Map.Entry<String,String>> entries = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                entries.add(new AbstractMap.SimpleEntry<>(line.split(";")[POSITION_PROJECT].trim(), line.split(";")[POSITION_ONTOLOGY].trim()));
                line = br.readLine();
            }
        }


        Map<String, List<String>> map = entries.stream().collect(
                Collectors.groupingBy(Map.Entry::getValue,
                                    Collectors.mapping(Map.Entry::getKey,
                                            Collectors.toList()))
        );
        return map;
    }

    static Map<String, String> buildOntologyPerProject(String path) throws Exception {
        int POSITION_PROJECT = 0;
        int POSITION_ONTOLOGY = 1;

        Map<String, String> map = new HashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                map.put(line.split(";")[POSITION_PROJECT].trim(), line.split(";")[POSITION_ONTOLOGY].trim());
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
                if(i%50==0) {
                    System.out.println("*********************** index approx "+ (i) + "/" + (numberOfItems)+" *************************");
                }
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
    private Map<String,List<String>> storagesForAnnotation;


    public SearchMultiServerThread(Map<Long,ResultsSimilarities> results,Map<String,List<String>> storagesForAnnotation,RetrievalClient search,Queue<String> queue, int numberOfThread,int numberOfItems,Queue<Long> times) {
        this.results = results;
        this.queue = queue;
        this.client = search;
        this.numberOfThread = numberOfThread;
        this.numberOfItems = numberOfItems;
        this.times = times;
        this.storagesForAnnotation = storagesForAnnotation;
    }

    public void run() {
        try {
            int i = 0; //ConcurrentLinkedQueue.size is not constant, so i*8
            while (!queue.isEmpty()) {
                String path = queue.poll();
                File imageFile = new File(path);

                String imageName = imageFile.getName().split("\\.")[0];



                List<String> storages = null;

                if(LearnAndTestCytomine.SEARCH_ON_PROJECT_WITH_SAME_ONTOLOGY) {
                    storages = storagesForAnnotation.get(imageName);
                }
                //System.out.println(imageFile + "=====>"+storages);

                Long start = System.currentTimeMillis();
                ResultsSimilarities rs = client.search(ImageIO.read(new File(path)), 15,storages);
                this.times.add(System.currentTimeMillis()-start);
                this.results.put(Long.parseLong(imageName),rs);
                if(i%50==0) {
                    System.out.println("*********************** search approx " + (i * numberOfThread) + "/" + (numberOfItems) + " *************************");
                }
                i++;

            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
