package frittoMisto.tavolo1;

import aima.core.search.local.FitnessFunction;
import aima.core.search.local.Individual;
import it.unibo.ai.didattica.competition.tablut.AI.Clients.ClientPerPesi;
import it.unibo.ai.didattica.competition.tablut.AI.Clients.Utils.MetricsPartita_Genetic;
import it.unibo.ai.didattica.competition.tablut.client.TablutAIBlackClient;
import it.unibo.ai.didattica.competition.tablut.server.Server;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Fitness implements FitnessFunction<Integer> {

    private static final int NUMERO_TENTATIVI = 3;
    private PrintWriter pw = null;
    private long iterazione = 0;

    public Fitness() {

        try {
            File file = new File("log_Fitness.txt");

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
    protected void finalize() throws Throwable {
        pw.flush();
        pw.close();
        super.finalize();
    }

    @Override
    public double apply(Individual<Integer> individual) {

        System.out.println("ITERAZIONE __" + iterazione++ + "__**********************************************************************************************************");

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

//              //TODO. QUANTO CAZZO SONO POTENTE?
//              Future server = executorService.submit( () -> {
//                    Server.main(new String[0]);
//              });

//              ProcessBuilder pb = new ProcessBuilder("java -jar C:\\Users\\ErChapo\\Desktop\\Server\\Tablut2020_Server.jar");
//              ProcessBuilder pb = new ProcessBuilder("java -jar ./Tablut2020_Server.jar");
                ProcessBuilder pb = new ProcessBuilder();
                pb.directory(new File("C:/Users/ErChapo/Desktop/Server"));

                pb.command("cmd.exe", "/c", ".\\run1.bat");
                server = pb.start();

                TimeUnit.MILLISECONDS.sleep(200);

                pb.command("cmd.exe", "/c", ".\\run2.bat");
                opponent = pb.start();


//            executorService.submit( () -> {
//                try {
//                    TablutAIBlackClient.main(new String[0]);
//                } catch (Exception e) {
//                    System.out.println("Nell'esecuzione di coolish");
//                    e.printStackTrace();
//                }
//
//            });

                //CREO IL MIO CLIENT BIANCO
                clientMIO = new ClientPerPesi("white",
                        weights.get(Main.KING_MANHATTAN),
                        weights.get(Main.KING_CAPTURED_SIDES),
                        weights.get(Main.PAWS_DIFFERENCE),
                        weights.get(Main.PAWS_WHITE),
                        weights.get(Main.VICTORY_PATH),
                        weights.get(Main.VICTORY),
                        weights.get(Main.PAWS_BLACK)
                );

                ClientPerPesi finalClientMIO = clientMIO;


                //SE VUOI TI MANDO IL MIO IBAN COSI MI FAI UN IL BONIFICO
                metrics = executorService.submit(() -> {
                    return finalClientMIO.getMetrics();
                }).get(5, TimeUnit.MINUTES);

//              server.cancel(true);

                executorService.shutdownNow();

//              metrics = clientMIO.getMetrics();
                tentativi++;

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
            }
        }

        if (metrics == null) {
            System.out.println("ERRORE NEL METRICS");
            return 0;
        }

//        System.out.println(" __oggetto _metrics_ "+ metrics);

        pw.println(" __oggetto _metrics_ " + metrics);
        pw.flush();

        if (metrics.isVictory())
            result = result + 400;
        if (metrics.isDraw())
            result = result + 100;
        if (!metrics.isDraw() && !metrics.isVictory())
            result = -300;

        result = metrics.getOpponentPawsEaten() - metrics.getMinePawsLosts() - (metrics.getTime() / 1000.0);


        return result;
        //TUTTI crescenti
//        return IntStream.range(0, integers.size()).filter(index -> integers.get(index) == index).count();
    }
}
