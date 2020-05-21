package frittoMisto.tavolo1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

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
            //TODO
            //the file containing the weights to try
            br = new BufferedReader(new FileReader("C:\\Path\\to\\pesi.txt"));
        } catch (Exception e) {
            System.out.println("Error. weights file");
        }

        br.lines().forEach(linea -> {

            List<Integer> pesi = getIndividualRandom_fromString(linea);

            DoMatches matcher = new DoMatches(pesi);

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
}
