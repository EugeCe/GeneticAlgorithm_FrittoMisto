package frittoMisto.tavolo1;

import java.util.Arrays;

public class ArrayDeiPesi {

    int[] arrayDeiPesi;

    public ArrayDeiPesi(int[] arrayDeiPesi) {
        this.arrayDeiPesi = arrayDeiPesi;
    }

    public ArrayDeiPesi() {
        this.arrayDeiPesi = new int[10];
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("__");

        for (int i =0; i < arrayDeiPesi.length; i++){
            sb.append(" " + arrayDeiPesi[i]+ ", ");

        }
        sb.append("__");
        return sb.toString();
    }
}
