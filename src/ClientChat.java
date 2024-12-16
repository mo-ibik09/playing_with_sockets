import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.*;
import java.net.Socket;

public class ClientChat extends Application{
    PrintWriter pw;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Client Chat");
        BorderPane borderPane = new BorderPane();

        //button
        Label labelHost = new Label("Host : ");
        TextField textFieldHost = new TextField("localhost");
        Label labelPort = new Label("Port : ");
        TextField textFieldPort = new TextField("1234");
        Button buttonConnect = new Button("Connect");


        HBox hBox = new HBox();hBox.setSpacing(10);hBox.setPadding(new Insets(10));

        hBox.setBackground(new Background(new BackgroundFill(Color.ORANGE,null,null)));
        hBox.getChildren().addAll(labelHost,textFieldHost,labelPort,textFieldPort,buttonConnect);
        borderPane.setTop(hBox);


        VBox vbox = new VBox();vbox.setSpacing(10);vbox.setPadding(new Insets(10));



        ObservableList<String> listModel = FXCollections.observableArrayList();

        ListView<String> listView = new ListView<>(listModel);
        vbox.getChildren().add(listView);
        borderPane.setCenter(vbox);

        Label labelMessage = new Label("Message : ");
        TextField textFieldMessage = new TextField();textFieldMessage.setPrefSize(400,30);
        Button buttonSend = new Button("Send");

        HBox hBoxBottom = new HBox();hBoxBottom.setSpacing(10);hBoxBottom.setPadding(new Insets(10));

        hBoxBottom.getChildren().addAll(labelMessage,textFieldMessage,buttonSend);
        borderPane.setBottom(hBoxBottom);


        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();


        buttonConnect.setOnAction((evnt)->{
            String host = textFieldHost.getText();
            int port = Integer.parseInt(textFieldPort.getText());
            try {
                Socket socket = new Socket(host,port);
                InputStream is =socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                pw = new PrintWriter(socket.getOutputStream(),true);
                new Thread(() -> {

                    while (true) {

                        try {
                            String message = br.readLine();
                            Platform.runLater(()->{
                                listModel.add(message);
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonSend.setOnAction((evnt)->{
            String message = textFieldMessage.getText();
            pw.println(message);
        });

    }
}
