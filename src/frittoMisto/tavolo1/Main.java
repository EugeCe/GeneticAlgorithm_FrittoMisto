package frittoMisto.tavolo1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final int INDIVIDUAL_LENGTH = 7;
    public static final int INITIAL_POPOLATION = 10;

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

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("C:\\Users\\ErChapo\\IdeaProjects\\genetic\\Results\\pesi.txt"));
        } catch (Exception e) {
            System.out.println("Errore apertura file");
        }

        br.lines().forEach(linea -> {

            List<Integer> pesi = getIndividualRandom_fromString(linea);

            Matcher matcher = new Matcher(pesi);

            matcher.doMatch(10);

        });

    }

    private static List<Integer> getIndividualRandom_fromString(String linea) {

        List<Integer> tmp = new ArrayList<>();


        String[] pesi = linea.split("[\t,]");

        tmp.add(KING_MANHATTAN, Integer.parseInt(pesi[KING_MANHATTAN].trim()));
        tmp.add(KING_CAPTURED_SIDES, Integer.parseInt(pesi[KING_CAPTURED_SIDES].trim()));
        tmp.add(PAWS_DIFFERENCE, Integer.parseInt(pesi[PAWS_DIFFERENCE].trim()));
        tmp.add(PAWS_WHITE, Integer.parseInt(pesi[PAWS_WHITE].trim()));
        tmp.add(VICTORY_PATH, Integer.parseInt(pesi[VICTORY_PATH].trim()));
        tmp.add(VICTORY, Integer.parseInt(pesi[VICTORY].trim()));
        tmp.add(PAWS_BLACK, Integer.parseInt(pesi[PAWS_BLACK].trim()));

        return tmp;
    }

/*




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


    private static List<Integer> getPesi() {

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < INITIAL_POPOLATION; i++) {
            if (i == 1) {

                List<Integer> tmp = new ArrayList<>();
                tmp.add(KING_MANHATTAN, 1);
                tmp.add(KING_CAPTURED_SIDES, -290);
                tmp.add(PAWS_DIFFERENCE, 225);
                tmp.add(PAWS_WHITE, -36);
                tmp.add(VICTORY_PATH, 352);
                tmp.add(VICTORY, 4954);
                tmp.add(PAWS_BLACK, -26);

            } else {
                result.add(getIndividualRandom());
                result.add(getIndividualRandom_fromString(i));
            }
//
        }
        return result;
    }

    private static Individual<Integer> getIndividualRandom() {

//        weight = new double[7];
//        weight[KING_MANHATTAN] = 50;  //manhattan
//        weight[KING_CAPTURED_SIDES] = -100;  //king capture
//        weight[PAWS_DIFFERENCE] = 100;  //lost pawns
//        weight[PAWS_WHITE] = 100 * (16 / 9); //white pieces (difference ?)
//        weight[VICTORY_PATH] = 300;  //victory path
//        weight[VICTORY] = WIN;  //victory
//        weight[PAWS_BLACK] = -100; //black pieces


        List<Integer> tmp = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());

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
//
//        int result = (segno*modulo);

        //QUESTA PARTE PER RIMANERE NELL'INTORNO DEL PUNTO
        int modulo = rand.nextInt(OFFSET + 1);
        int result = i + (segno * modulo);

//        Questo serve a fare in modo che result sia in [-BOUND,+BOUND]
        if (result < -WEIGHTS_BOUND)
            result = 2 * modulo + result;
        if (result > WEIGHTS_BOUND)
            result = -2 * modulo + result;

        return result;
    }

 */
}
