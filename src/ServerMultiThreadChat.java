import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMultiThreadChat extends Thread {
    private boolean isActive = true;
    private int nbrClient = 0;
    private List<Conversation> clients = new ArrayList<>();

    public static void main(String[] args) {
        new ServerMultiThreadChat().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            while (isActive) {
                Socket socket = serverSocket.accept();
                nbrClient++;
                Conversation conversation = new Conversation(socket,nbrClient);
                clients.add(conversation);
                conversation.start();
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

//        public void broadcastMessage(String message){
//            try {
//                for (Conversation client : clients) {
//                        PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(), true);
//                        printWriter.println(message);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//        public void broadcastMessage(String message, Socket socketClient){
//            try {
//                for (Conversation client : clients) {
//                    if(client.socketClient != socketClient){
//                        PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(), true);
//                        printWriter.println(message);
//                    }
//
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//        }

        public void broadcastMessage(String message, Socket socketClient,int clientNumber){
            try {
                for (Conversation client : clients) {
                    if(client.socketClient != socketClient){
                        if(clientNumber != -1 && client.number == clientNumber){
                            PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(), true);
                            printWriter.println(message);
                        }
                        else if(clientNumber == -1) {
                            PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(), true);
                            printWriter.println(message);
                        }
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
                    if(request.contains("=>")){
                        String[] requestParams = request.split("=>");
                        if(requestParams.length == 2){
                            String message = requestParams[1];
                            int clientNumber = Integer.parseInt(requestParams[0]);
                            broadcastMessage(message,socketClient,clientNumber);
                        }
                    }
                    else{
                        broadcastMessage(request,socketClient,-1);
                    }


                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        }
    }
}
