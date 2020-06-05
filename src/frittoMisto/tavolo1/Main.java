package frittoMisto.tavolo1;

import aima.core.search.local.Individual;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static final int INDIVIDUAL_LENGTH = 7;
    public static final int INITIAL_POPOLATION = 12;

    public static final int KING_MANHATTAN = 0;
    public static final int KING_CAPTURED_SIDES = 1;
    public static final int PAWS_DIFFERENCE = 2;
    public static final int PAWS_WHITE = 3;
    public static final int VICTORY_PATH = 4;
    public static final int VICTORY = 5;
    public static final int PAWS_BLACK = 6;


    public static final int WEIGHTS_BOUND = 500;
    public static final int OFFSET = 200;

    public static void main(String[] args) {

        System.out.println("**********************************************************************************************************");

        List<Integer> finiteAlphabet = new ArrayList<>();
        for (int i = -WEIGHTS_BOUND; i <= WEIGHTS_BOUND; i++) {
            finiteAlphabet.add(i);
        }


//      GeneticAlgorithm<Integer> algorithm = new GeneticAlgorithm<>(INDIVIDUAL_LENGTH, finiteAlphabet, 0.3);
        GeneticConStampaSuFile<Integer> algorithm = new GeneticConStampaSuFile<>(INDIVIDUAL_LENGTH, finiteAlphabet, 0.5);

        //For doubles. In this case is not necessary to have the precision of doubles
//      GeneticAlgorithmForNumbers algorithm = new GeneticAlgorithmForNumbers(INDIVIDUAL_LENGTH, -WEIGHTS_BOUND, WEIGHTS_BOUND, 0.3);

        List<Individual<Integer>> popolazione = getPopolazione();

        Individual<Integer> result = algorithm.geneticAlgorithm(popolazione, new Fitness(), 150);

        //if you have a specific goal
//        GoalTest goalTest = new GoalTest();
//        Individual<Integer> result = algorithm.geneticAlgorithm(popolazione, new Fitness(), goalTest, 60000);


        System.out.println("**********************************************************************************************************");

        System.out.println("KING_MANHATTAN :        __" + result.getRepresentation().get(KING_MANHATTAN));
        System.out.println("KING_CAPTURED_SIDES :   __" + result.getRepresentation().get(KING_CAPTURED_SIDES));
        System.out.println("PAWS_DIFFERENCE :       __" + result.getRepresentation().get(PAWS_DIFFERENCE) + " NON CONSIDERATA NEL CLIENT");
        System.out.println("PAWS_WHITE :            __" + result.getRepresentation().get(PAWS_WHITE));
        System.out.println("VICTORY_PATH :          __" + result.getRepresentation().get(VICTORY_PATH));
        System.out.println("VICTORY :               __" + result.getRepresentation().get(VICTORY));
        System.out.println("PAWS_BLACK :            __" + result.getRepresentation().get(PAWS_BLACK));

        System.out.println("**********************************************************************************************************");
        System.out.println(algorithm.getMetrics().toString());
        System.out.println("**********************************************************************************************************" + System.lineSeparator());

    }

    private static List<Individual<Integer>> getPopolazione() {

        List<Individual<Integer>> result = new ArrayList<>();
        for (int i = 0; i < INITIAL_POPOLATION; i++) {
            //Create population pseudo-randomly
            result.add(getIndividualRandom(i));

            //Create population by retrieving it from file
//            result.add(getIndividual_fromFile(i));
        }
        return result;
    }

    private static Individual<Integer> getIndividual_fromFile(int i) {

        List<Integer> tmp = new ArrayList<>();

        try {
            //TODO
            BufferedReader br = new BufferedReader(new FileReader("C:\\basePerPopolazione.txt"));

            String linea = br.lines().skip(i).findFirst().get();

            String[] pesi = linea.split("[\t,]");

            tmp.add(KING_MANHATTAN, Integer.parseInt(pesi[KING_MANHATTAN].trim()));
            tmp.add(KING_CAPTURED_SIDES, Integer.parseInt(pesi[KING_CAPTURED_SIDES].trim()));
            tmp.add(PAWS_DIFFERENCE, Integer.parseInt(pesi[PAWS_DIFFERENCE].trim()));
            tmp.add(PAWS_WHITE, Integer.parseInt(pesi[PAWS_WHITE].trim()));
            tmp.add(VICTORY_PATH, Integer.parseInt(pesi[VICTORY_PATH].trim()));
            tmp.add(VICTORY, Integer.parseInt(pesi[VICTORY].trim()));
            tmp.add(PAWS_BLACK, Integer.parseInt(pesi[PAWS_BLACK].trim()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Individual<>(tmp);
    }

    private static Individual<Integer> getIndividualRandom(int i) {

        //Initial weights
//        weight = new double[7];
//        weight[KING_MANHATTAN] = 50;  //manhattan
//        weight[KING_CAPTURED_SIDES] = -100;  //king capture
//        weight[PAWS_DIFFERENCE] = 100;  //lost pawns
//        weight[PAWS_WHITE] = 100 * (16 / 9); //white pieces (difference ?)
//        weight[VICTORY_PATH] = 300;  //victory path
//        weight[VICTORY] = WIN;  //victory
//        weight[PAWS_BLACK] = -100; //black pieces


        List<Integer> tmp = new ArrayList<>();
        Random rand = new Random(i);

        tmp.add(KING_MANHATTAN, dammiRandom(50, rand));
        tmp.add(KING_CAPTURED_SIDES, dammiRandom(-100, rand));
        tmp.add(PAWS_DIFFERENCE, dammiRandom(100, rand));
        tmp.add(PAWS_WHITE, dammiRandom(177, rand));
        tmp.add(VICTORY_PATH, dammiRandom(300, rand));
        tmp.add(VICTORY, dammiRandom(5000, rand));
        tmp.add(PAWS_BLACK, dammiRandom(-100, rand));


        return new Individual<>(tmp);
    }

    private static int dammiRandom(int i, Random rand) {

        int segno = rand.nextBoolean() ? -1 : 1;

//        int modulo = rand.nextInt(WEIGHTS_BOUND + 1);
//        int result = (segno*modulo);

        //This is necessary to create a pseudo-random value that is neighborhood of i.
        int modulo = rand.nextInt(OFFSET + 1);
        int result = i + (segno * modulo);

//       result in [-BOUND,+BOUND]
        if (result < -WEIGHTS_BOUND)
            result = 2 * modulo + result;
        if (result > WEIGHTS_BOUND)
            result = -2 * modulo + result;

        return result;
    }
}
