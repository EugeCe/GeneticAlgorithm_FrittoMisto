package frittoMisto.tavolo1;

import aima.core.search.framework.problem.GoalTest;
import aima.core.search.local.Individual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.IntStream;

public class GoalTestss implements GoalTest {

    private long interaction = 0;

    //testing porpouse
//    PrintWriter pw;

    public GoalTestss() {

//        //testing porpouse
//        try {
//            File file = new File("logs.txt");
//
//            if(file.exists())
//                file.delete();
//
//            file.createNewFile();
//            pw = new PrintWriter(file);
//            pw.println("INIZIO");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public boolean isGoalState(Object o) {
        Individual<Integer> indi = (Individual<Integer>) o;


//        pw.println("__"+ interaction++ + "__ Is test goal?" + indi.getRepresentation());

        List<Integer> tmp = indi.getRepresentation();

        boolean result = IntStream.range(0, tmp.size()).allMatch(index -> tmp.get(index) == index);

        if(result == true){
            System.out.println(tmp);
        }

        return result;
    }

//    @Override
//    protected void finalize() throws Throwable {
//        pw.flush();
//        pw.close();
//        super.finalize();
//    }
}
