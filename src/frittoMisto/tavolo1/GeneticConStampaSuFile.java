package frittoMisto.tavolo1;

import aima.core.search.framework.problem.GoalTest;
import aima.core.search.local.FitnessFunction;
import aima.core.search.local.GeneticAlgorithm;
import aima.core.search.local.Individual;
import aima.core.util.CancelableThread;
import aima.core.util.Util;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.DoubleStream;

public class GeneticConStampaSuFile<A> extends GeneticAlgorithm<A> {

    public static final String LOG_FILE = "bestFiles/best_";
    private PrintWriter pw = null;

    private double[] fValuesBest = null;
//    private List<GeneticAlgorithm.ProgressTracer<A>> progressTracers;

    public GeneticConStampaSuFile(int individualLength, Collection<A> finiteAlphabet, double mutationProbability) {
        super(individualLength, finiteAlphabet, mutationProbability);

    }

    @Override
    public Individual<A> geneticAlgorithm(Collection<Individual<A>> initPopulation, FitnessFunction<A> fitnessFn, GoalTest goalTest, long maxTimeMilliseconds) {

        initLogFile();

        Individual<A> bestIndividual = null;

        List<Individual<A>> population = new ArrayList<>(initPopulation);
        List newPopulation = new ArrayList<>();


        this.validatePopulation((Collection)population);
        this.updateMetrics((Collection)population, 0, 0L);
        long startTime = System.currentTimeMillis();
        int itCount = 0;

        //calcolo soltanto una volta fValues altrimenti facciamo notte
        double[] fValuesOld = getFValues(population, fitnessFn);

        do {

            bestIndividual = getBestIndividual(fValuesOld, population);
            pw.println(bestIndividual.getRepresentation());
            pw.flush();

            newPopulation = nextGeneration((List)population, fitnessFn, fValuesOld);

            double[] fValuesNew = getFValues(newPopulation, fitnessFn);

//            pw.println("VECCHI:  " + getString(fValuesOld)) ;
//            pw.println("figli:  " + getString(fValuesNew)) ;

            fValuesBest = null;
            population = getBestPopulation(fValuesOld, population, fValuesNew, newPopulation);

            fValuesOld = fValuesBest.clone();

//            pw.println("nuovi:  " + getString(fValuesOld));


            ++itCount;
            this.updateMetrics((Collection)population, itCount, System.currentTimeMillis() - startTime);
        } while((maxTimeMilliseconds <= 0L || System.currentTimeMillis() - startTime <= maxTimeMilliseconds) && !CancelableThread.currIsCanceled() && !goalTest.isGoalState(bestIndividual));


        pw.println(System.lineSeparator() + "Finito di rinnovare le generazioni, best:");
        pw.flush();
        //qui si fa nuovamente un giro di giostra per ottenere il migliore individuo
        bestIndividual = this.retrieveBestIndividual((Collection)population, fitnessFn);

//        notifyProgressTracers(itCount, (Collection)population);
        return bestIndividual;
    }

    private String getString(double[] fValuesBest) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fValuesBest.length; i++){
            sb.append("  " + fValuesBest[i]);
        }
        return sb.toString();
    }

    private Individual<A> getBestIndividual(double[] fValues, List<Individual<A>> population) {

        Double max = Double.NEGATIVE_INFINITY;
        int bestIndex = 0;
        for(int i = 0; i < fValues.length; i++){
            if(fValues[i]>max){
                bestIndex = i;
            }
        }
        return population.get(bestIndex);
    }

    private List getBestPopulation(double[] fValuesOld, List<Individual<A>> oldPopulation, double[] fValuesNew, List<Individual<A>> newPopulation) {
        List<Individual<A>> result = new ArrayList<>(oldPopulation.size());

        fValuesBest = new double[newPopulation.size()];

        for (int i = 0; i < fValuesNew.length; i++){

            int index = isBest(fValuesNew[i], fValuesOld);
            if(index >= 0){
                fValuesBest[i] =  fValuesOld[i];
                //in modo tale che non viene ripescato
                fValuesOld[i] = Double.NEGATIVE_INFINITY;
                result.add(i, oldPopulation.get(index));
            } else{
                fValuesBest[i] = fValuesNew[i];
                result.add(i, newPopulation.get(i));
            }
        }
        return result;
    }

    private int isBest(double v, double[] fValuesOld) {
        for (int i = 0; i < fValuesOld.length; i++){
            if(v < fValuesOld[i]){
                return i;
            }
        }
        return -1;
    }

    protected List<Individual<A>> nextGeneration(List<Individual<A>> population, FitnessFunction<A> fitnessFn, double[] fValues) {
        List<Individual<A>> newPopulation = new ArrayList(population.size());

        for(int i = 0; i < population.size(); ++i) {
            //calcolo soltanto una volta fValues altrimenti facciamo notte
            Individual<A> x = randomSelection(population, fitnessFn, fValues);
            Individual<A> y = randomSelection(population, fitnessFn, fValues);

            Individual<A> child = this.reproduce(x, y);
            if (this.random.nextDouble() <= this.mutationProbability) {
                child = this.mutate(child);
            }

            newPopulation.add(child);
        }

//        this.notifyProgressTracers(this.getIterations(), population);
        return newPopulation;
    }

    private double[] getFValues(List<Individual<A>> population, FitnessFunction<A> fitnessFn) {
        double[] fValues = new double[population.size()];
        for(int i = 0; i < population.size(); ++i) {
            fValues[i] = fitnessFn.apply((Individual)population.get(i));
        }
        fValues = Util.normalize(fValues);
        return fValues;
    }

    public GeneticConStampaSuFile(int individualLength, Collection<A> finiteAlphabet, double mutationProbability, Random random) {
        super(individualLength, finiteAlphabet, mutationProbability, random);
        initLogFile();
    }

    protected Individual<A> randomSelection(List<Individual<A>> population, FitnessFunction<A> fitnessFn, double[] fValues) {
        Individual<A> selected = (Individual)population.get(population.size() - 1);

        double prob = this.random.nextDouble();
        double totalSoFar = 0.0D;

        for(int i = 0; i < fValues.length; ++i) {
            totalSoFar += fValues[i];
            if (prob <= totalSoFar) {
                selected = (Individual)population.get(i);
                break;
            }
        }

        selected.incDescendants();
        return selected;
    }

    private void initLogFile() {
        try {
            File file = new File(LOG_FILE + System.currentTimeMillis() +".txt");
            if (file.exists())
                file.delete();
            file.createNewFile();
            pw = new PrintWriter(file);
            pw.println("STARTS AT __" + LocalDateTime.now());
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //For every population the best individual is written on file
    @Override
    public Individual<A> retrieveBestIndividual(Collection<Individual<A>> population, FitnessFunction<A> fitnessFn) {
        Individual<A> bestIndividual = null;
        double bestSoFarFValue = -1.0D / 0.0;
        Iterator var6 = population.iterator();

        while(var6.hasNext()) {
            Individual<A> individual = (Individual)var6.next();
            double fValue = fitnessFn.apply(individual);
            if (fValue > bestSoFarFValue) {
                bestIndividual = individual;
                bestSoFarFValue = fValue;
            }
        }

        pw.println(bestIndividual.getRepresentation());
        pw.flush();

        return bestIndividual;
    }

//    private void notifyProgressTracers(int itCount, Collection<Individual<A>> generation) {
//        Iterator var3 = this.progressTracers.iterator();
//
//        while(var3.hasNext()) {
//            GeneticAlgorithm.ProgressTracer<A> tracer = (GeneticAlgorithm.ProgressTracer)var3.next();
//            tracer.traceProgress(this.getIterations(), generation);
//        }
//
//    }

}
