import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMultiThread extends Thread {
    private boolean isActive = true;
    private int nbrClient = 0;

    public static void main(String[] args) {
        new ServerMultiThread().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
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

                while(true){
                    String request = br.readLine();
                    String response ="Length = " + request.length();
                    pw.println(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        }
    }
}
