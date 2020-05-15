package frittoMisto.tavolo1;

import aima.core.search.local.FitnessFunction;
import aima.core.search.local.GeneticAlgorithm;
import aima.core.search.local.Individual;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class GeneticConStampaSuFile<A> extends GeneticAlgorithm<A> {

    public static final String LOG_FILE = "bestFiles/best_";
    private PrintWriter pw = null;

    public GeneticConStampaSuFile(int individualLength, Collection<A> finiteAlphabet, double mutationProbability) {
        super(individualLength, finiteAlphabet, mutationProbability);
        initLogFile();
    }

    public GeneticConStampaSuFile(int individualLength, Collection<A> finiteAlphabet, double mutationProbability, Random random) {
        super(individualLength, finiteAlphabet, mutationProbability, random);
        initLogFile();
    }

    private void initLogFile() {
        try {
            File file = new File(LOG_FILE + System.currentTimeMillis() +".txt");
            if (file.exists())
                file.delete();
            file.createNewFile();
            pw = new PrintWriter(file);
            pw.println("STARTS AT __" + LocalTime.now());
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //INSERITO SOLO PER POTER STAMPARE SU FILE OGNI TANTO
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


    @Override
    protected void finalize() throws Throwable {
        pw.flush();
        pw.close();
        super.finalize();
    }
}
