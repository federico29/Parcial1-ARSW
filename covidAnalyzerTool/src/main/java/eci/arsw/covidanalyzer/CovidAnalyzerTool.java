package eci.arsw.covidanalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Camel Application
 */
public class CovidAnalyzerTool {

    private ResultAnalyzer resultAnalyzer;
    private TestReader testReader;
    private int amountOfFilesTotal;
    private AtomicInteger amountOfFilesProcessed;
    private boolean suspender;

    public CovidAnalyzerTool() {
        resultAnalyzer = new ResultAnalyzer();
        testReader = new TestReader();
        amountOfFilesProcessed = new AtomicInteger();
        amountOfFilesProcessed.set(0);
        suspender = false;
    }

    public void processResultData(List<File> filesDivision) {
        synchronized (this) {
            while (suspender) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (File resultFile : filesDivision) {
                List<Result> results = testReader.readResultsFromFile(resultFile);
                for (Result result : results) {
                    resultAnalyzer.addResult(result);
                }
                amountOfFilesProcessed.incrementAndGet();
            }
        }
    }

    private List<File> getResultFileList() {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public Set<Result> getPositivePeople(List<CovidThread> hilos) {
        Set<Result> positivePeople = new HashSet<>();
        for (CovidThread hilo: hilos){
            positivePeople.addAll(hilo.getPositivePeople());
        }
        return positivePeople;
    }

    public AtomicInteger getAmountOfFilesProcessed(){
        return amountOfFilesProcessed;
    }

    private List<File> divideFiles(List<File> resultFileList, int min, int max) {
        return resultFileList.subList(min, max);
    }

    public static void pararHilos(List<CovidThread> hilos){
        for (CovidThread hilo: hilos){
            hilo.suspenderHilo();
        }
    }

    public static void reanudarHilos(List<CovidThread> hilos){
        for (CovidThread hilo: hilos){
            hilo.renaudarHilo();
        }
    }

    public void showReport(List<CovidThread> hilos){
        String message = "Processed %d out of %d files.\nFound %d positive people:\n%s";
        Set<Result> positivePeople = getPositivePeople(hilos);
        String affectedPeople = positivePeople.stream().map(Result::toString).reduce("", (s1, s2) -> s1 + "\n" + s2);
        message = String.format(message, amountOfFilesProcessed.get(), amountOfFilesTotal, positivePeople.size(), affectedPeople);
        System.out.println(message);
    }

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String[] args) throws Exception {
        CovidAnalyzerTool covidAnalyzerTool = new CovidAnalyzerTool();
        Integer numberOfThreads = 5;
        List<File> resultFileList = covidAnalyzerTool.getResultFileList();
        covidAnalyzerTool.amountOfFilesTotal = resultFileList.size();
        List<CovidThread> threads = new ArrayList<>();
        AtomicInteger count = covidAnalyzerTool.getAmountOfFilesProcessed();
        Integer cont = 0;

        System.out.println(resultFileList.size());
        System.out.println(resultFileList.size() % numberOfThreads != 0);

        if (resultFileList.size() % numberOfThreads != 0) {
            for (int i = 0; i < numberOfThreads - 1; i++) {
                List<File> filesDivision = covidAnalyzerTool.divideFiles(resultFileList, cont, cont + numberOfThreads);
                System.out.println(cont + "---" + (cont + numberOfThreads - 1));
                threads.add(new CovidThread(filesDivision, count));
                cont = cont + numberOfThreads;
            }
            System.out.println(cont + "---" + (resultFileList.size() - 1));
            List<File> filesDivision = covidAnalyzerTool.divideFiles(resultFileList, cont, resultFileList.size());
            threads.add(new CovidThread(filesDivision, count));
        } else {
            for (int i = 0; i < numberOfThreads; i++) {
                List<File> filesDivision = covidAnalyzerTool.divideFiles(resultFileList, cont, cont + numberOfThreads);
                threads.add(new CovidThread(filesDivision, count));
                cont = cont + numberOfThreads;
            }
        }

        threads.forEach(thread -> {
            thread.start();
        });

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.contains("exit")){
                break;
            } else if(line.isEmpty()){
                if (covidAnalyzerTool.suspender){
                    reanudarHilos(threads);
                }else{
                    pararHilos(threads);
                    covidAnalyzerTool.showReport(threads);
                }
            }else if (!covidAnalyzerTool.suspender && !line.isEmpty()){
                covidAnalyzerTool.showReport(threads);
            }
        }
    }

}

