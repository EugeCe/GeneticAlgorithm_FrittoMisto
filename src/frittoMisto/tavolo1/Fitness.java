package frittoMisto.tavolo1;

import aima.core.search.local.FitnessFunction;
import aima.core.search.local.Individual;
import it.unibo.ai.didattica.competition.tablut.AI.Clients.ClientPerPesi;
import it.unibo.ai.didattica.competition.tablut.AI.Clients.Utils.MetricsPartita_Genetic;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Fitness implements FitnessFunction<Integer> {

    private static final int NUMERO_TENTATIVI = 3;
    private PrintWriter pw = null;
    private long iterazione = 0;

    public Fitness() {

        try {
            File file = new File("log_Fitness"+System.currentTimeMillis()+".txt");

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

    @Override
    public double apply(Individual<Integer> individual) {

        System.out.println("match __" + iterazione++ + "__**********************************************************************************************************");

        List<Integer> weights = individual.getRepresentation();

        double result = 0;
        ClientPerPesi clientMIO = null;
        MetricsPartita_Genetic metrics = null;

        int tentativi = 0;

        Process server = null;
        Process opponent = null;


        while (metrics == null && tentativi < NUMERO_TENTATIVI) {


            try {

                TimeUnit.MILLISECONDS.sleep(500);

                ExecutorService executorService = Executors.newCachedThreadPool();

                //if you have server as library:
//              Future server = executorService.submit( () -> {
//                    Server.main(new String[0]);
//              });

                ProcessBuilder pb = new ProcessBuilder();
                //TODO
                pb.directory(new File("C:/path/to/bin/directory"));

                //TODO
                //run1 -> "start java -jar ./server.jar"
                pb.command("cmd.exe", "/c", ".\\run1.bat");
                server = pb.start();

                TimeUnit.MILLISECONDS.sleep(200);

                //TODO
                //run2 -> "start java -jar ./opponent.jar"
                pb.command("cmd.exe", "/c", ".\\run2.bat");
                opponent = pb.start();

                //if you have opponent as library:
//              executorService.submit( () -> {
//                try {
//                    TablutAIBlackClient.main(new String[0]);
//                } catch (Exception e) {
//                    System.out.println("Opponent exception");
//                    e.printStackTrace();
//                }
//            });

                //our client
                clientMIO = new ClientPerPesi("white",
                        weights.get(Main.KING_MANHATTAN),
                        weights.get(Main.KING_CAPTURED_SIDES),
                        weights.get(Main.PAWS_DIFFERENCE),
                        weights.get(Main.PAWS_WHITE),
                        weights.get(Main.VICTORY_PATH),
//                        weights.get(Main.VICTORY),
                        5000,
                        weights.get(Main.PAWS_BLACK)
                );

                ClientPerPesi finalClientMIO = clientMIO;

                metrics = executorService.submit(() -> {
                    return finalClientMIO.getMetrics();
                }).get(5, TimeUnit.MINUTES);

//              server.cancel(true);

                executorService.shutdownNow();


            } catch (Exception e) {
                e.printStackTrace();
                metrics = null;
                if(server != null){
                    server.destroyForcibly();
                }
                if(opponent != null){
                    opponent.destroyForcibly();
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                tentativi++;
            }
        }

        if (metrics == null) {
            System.out.println("ERRORE NEL METRICS");
            return 0;
        }

//        System.out.println(" __oggetto _metrics_ "+ metrics);

        pw.println("_metrics_ " + metrics + " con pesi: " +  individual.getRepresentation());
        pw.flush();

        if (metrics.isVictory())
            result = result + 600;
        if (metrics.isDraw())
            result = result + 100;
        if (!metrics.isDraw() && !metrics.isVictory())
            result = -600;

        result = result + 10*metrics.getOpponentPawsEaten() - 10*metrics.getMinePawsLosts() - (metrics.getTime() / 1000.0);


        return result;
    }
}
