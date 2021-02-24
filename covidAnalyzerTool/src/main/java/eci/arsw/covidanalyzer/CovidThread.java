package eci.arsw.covidanalyzer;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CovidThread extends Thread{
    private List<File> files;
    private TestReader testReader;
    private ResultAnalyzer resultAnalyzer;
    private boolean suspender;
    private AtomicInteger amountOfFilesProcessed;



    public CovidThread(List<File> filesDivision, AtomicInteger count) {
        this.files = filesDivision;
        testReader = new TestReader();
        resultAnalyzer = new ResultAnalyzer();
        suspender = false;
        amountOfFilesProcessed = count;
    }

    @Override
    public void run() {
        synchronized (this) {
            for (File resultFile : this.files) {
                while (suspender) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                List<Result> results = testReader.readResultsFromFile(resultFile);
                System.out.println("Archivo procesado.");
                for (Result result : results) {
                    resultAnalyzer.addResult(result);
                }
                amountOfFilesProcessed.getAndIncrement();
            }
        }
    }

    synchronized void suspenderHilo(){
        suspender=true;
    }

    synchronized void renaudarHilo(){
        suspender=false;
        notify();
    }

    public Set<Result> getPositivePeople() {
        return resultAnalyzer.listOfPositivePeople();
    }
}
