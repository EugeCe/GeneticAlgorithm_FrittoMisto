package frittoMisto.tavolo1;


import it.unibo.ai.didattica.competition.tablut.AI.Clients.ClientPerPesi;
import it.unibo.ai.didattica.competition.tablut.AI.Clients.Utils.MetricsPartita_Genetic;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Matcher{

    private static final int NUMERO_TENTATIVI = 100;
    private PrintWriter pw = null;
    private long iterazione = 0;
    private List<Integer> weights = null;
    private String pesiString = null;
    private List<MetricsPartita_Genetic> risultati;

    public Matcher(List<Integer> weights) {

        this.weights = weights;

        try {

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < weights.size(); i++) {
                sb.append(weights.get(i) + "_");
            }
            pesiString = sb.toString();

            initFile("matchs_");

            pw.println("STARTS AT __" + LocalDateTime.now());

            pw.println(pesiString + System.lineSeparator());

            pw.flush();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doMatch(int numeroMatchsToDo) {

        ClientPerPesi clientMIO = null;
        MetricsPartita_Genetic metrics = null;

        int tentativi = 0;

        Process server = null;
        Process opponent = null;

        risultati = new ArrayList<>();


        int iterazioni = 0;

        while (tentativi < NUMERO_TENTATIVI && iterazioni < numeroMatchsToDo) {

            try {

                TimeUnit.MILLISECONDS.sleep(500);

                ExecutorService executorService = Executors.newCachedThreadPool();

//              Future server = executorService.submit( () -> {
//                    Server.main(new String[0]);
//              });

//              ProcessBuilder pb = new ProcessBuilder("java -jar C:\\Users\\ErChapo\\Desktop\\Server\\Tablut2020_Server.jar");
//              ProcessBuilder pb = new ProcessBuilder("java -jar ./Tablut2020_Server.jar");
                ProcessBuilder pb = new ProcessBuilder();
                pb.directory(new File("C:/Users/ErChapo/Desktop/Server"));

                // server starts
                pb.command("cmd.exe", "/c", ".\\run1.bat");
                server = pb.start();

                TimeUnit.MILLISECONDS.sleep(200);

                //opponent starts
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
                }).get(90, TimeUnit.MINUTES);

//              server.cancel(true);
//              metrics = clientMIO.getMetrics();
                executorService.shutdownNow();

                if (metrics == null) {
                    System.out.println("ERRORE NEL METRICS");
                }else{
                    risultati.add(metrics);
                    pw.println(" __oggetto _metrics_ " + metrics);
                    pw.flush();
                }


                iterazioni++;

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
        } //FINE PARTITE

        pw.flush();
        pw.close();


        initFile("result_");

        StringBuilder sb = new StringBuilder();

        sb.append("partite:       " + risultati.size() + System.lineSeparator() + System.lineSeparator());

        sb.append("vittorie:      " + risultati.stream().filter(MetricsPartita_Genetic::isVictory).count() + System.lineSeparator());
        sb.append("sconfitte:     " + risultati.stream().filter( m -> !m.isVictory() && !m.isDraw()).count() + System.lineSeparator());
        sb.append("pareggi:       " + risultati.stream().filter(MetricsPartita_Genetic::isDraw).count() + System.lineSeparator());

        sb.append(System.lineSeparator());

        sb.append("media pedoni avversari mangiati:      " + risultati.stream().mapToDouble(MetricsPartita_Genetic::getOpponentPawsEaten).average().orElse(Double.NaN) + System.lineSeparator());
        sb.append("media pedoni amici persi:             " + risultati.stream().mapToDouble(MetricsPartita_Genetic::getMinePawsLosts).average().orElse(Double.NaN) + System.lineSeparator());

        pw.print(sb.toString());
        pw.flush();
        pw.close();

        System.out.println("RISULTATI DI " + pesiString);
        System.out.print(sb.toString());
    }

    private void initFile(String suffix) {
        try {

            File file;
            int i = 0;
            do {
                String nome = suffix +  (i == 0 ?  pesiString + ".txt" : pesiString + "(" + i + ").txt");
                file = new File(nome);
                i++;
            }while (file.exists());

            file.createNewFile();

            pw = new PrintWriter(file);

            pw.println(pesiString + System.lineSeparator());
            pw.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
