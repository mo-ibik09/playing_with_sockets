import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServerMultiThreadGame extends Thread {
    private boolean isActive = true;
    private int nbrClient = 0;
    private int nbrSecret;
    private boolean end;
    private String winner;

    public static void main(String[] args) {
        new ServerMultiThreadGame().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            nbrSecret = new Random().nextInt(1000); // 0 to 999
            System.out.println("Secret number is " + nbrSecret);
            end = false;
            while (isActive) {
                Socket socket = serverSocket.accept();
                nbrClient++;
                new Conversation(socket,nbrClient).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    class Conversation extends Thread {
        private Socket socketClient;
        private int number;

        public Conversation(Socket socketClient, int number) {
            this.socketClient = socketClient;
            this.number = number;
        }

        @Override
        public void run() {
            InputStream is = null;
            try {
                is = socketClient.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                OutputStream os = socketClient.getOutputStream();
                PrintWriter pw = new PrintWriter(os, true);
                String IPClient = socketClient.getRemoteSocketAddress().toString();
                System.out.println("Client " + number + " connected from " + IPClient);
                pw.println("Hello client " + number);
                pw.println("Guess the number between 0 and 999");

                while(true){
                    String request = br.readLine();
                    int guess = 0;
                    boolean correctFormatRequest = false;
                    try{
                        guess = Integer.parseInt(request);
                        correctFormatRequest = true;
                    }
                    catch (NumberFormatException e){
                        pw.println("Invalid number");
                        correctFormatRequest = false;
                    }
                    if(correctFormatRequest){
                        System.out.println("Client " + number + "with IP : "+IPClient + "guessed " + guess);
                        if(end == false){
                            if(guess > nbrSecret) {
                                pw.println("Too high");
                            }
                            else if(guess < nbrSecret) {
                                pw.println("Too low");
                            }
                            else {
                                pw.println("You win!");
                                winner = "Client " + number + "with IP " + IPClient + " wins!";
                                System.out.println("Congrats! " + winner);
                                end = true;
                            }
                        }else{
                            pw.println("Game over! the winner is : " + winner);
                        }
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        }
    }
}
