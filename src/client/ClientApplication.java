package client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClientApplication extends Application{
	private ArrayList<Thread> threads;
	public static void main(String[] args) {
		launch();
	}
	
	@Override
	public void stop() throws Exception{
		super.stop();
		for(Thread thread: threads) {
			thread.interrupt();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		threads = new ArrayList<Thread>();
		primaryStage.setTitle("Chat Application - Client Side");
		primaryStage.setScene(makeInitScene(primaryStage));
		primaryStage.setWidth(500);
		primaryStage.show();
	}
	
	public Scene makeInitScene(Stage primaryStage){
		GridPane rootPane = new GridPane();
		rootPane.setId("rootPane");
		rootPane.getStylesheets().addAll(this.getClass().getResource("client.css").toExternalForm());
		rootPane.setPadding(new Insets(20));
		rootPane.setVgap(10);
		rootPane.setHgap(10);
		rootPane.setAlignment(Pos.CENTER);
		
		TextField nameField = new TextField();
		TextField hostNameField = new TextField();
		TextField portNumberField = new TextField();
		
		Label nameLabel = new Label("Nickname: ");
		Label hostNameLabel = new Label("Host Address: ");
		Label portNumberLabel = new Label("Port Number: ");
		Label errorLabel = new Label();
		
		Button submitClientInfoButton = new Button("Enter Chat");
		submitClientInfoButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override 
			public void handle(ActionEvent Event) {
				Client client;
				try {
					client = new Client(hostNameField.getText(), Integer.parseInt(portNumberField.getText()), nameField.getText());
					Thread clientThread = new Thread(client);
					clientThread.setDaemon(true);
					clientThread.start();
					threads.add(clientThread);
					
					primaryStage.close();
					primaryStage.setScene(makeChatUI(client));
					primaryStage.show();
				}
				catch(ConnectException e) {
					errorLabel.setTextFill(Color.RED);
					errorLabel.setText(e.toString());
//					errorLabel.setText("Invalid host name, try again");
				}
				catch(NumberFormatException | IOException e) {
					errorLabel.setTextFill(Color.RED);
					errorLabel.setText(e.toString());
//					errorLabel.setText("Invalid port number, try again");
				}
			}
		});
		
		rootPane.add(nameField,  1, 0);
		rootPane.add(nameLabel,  0, 0);
		rootPane.add(hostNameField,  1, 1);
		rootPane.add(hostNameLabel,  0, 1);
		rootPane.add(portNumberField,  1, 2);
		rootPane.add(portNumberLabel,  0, 2);
		rootPane.add(submitClientInfoButton,  1, 3, 2, 3);
		rootPane.add(errorLabel,  1, 6);
		
//		Making the scene then return it 
		return new Scene(rootPane, 400, 400);
	}
	
	public Scene makeChatUI(Client client) {
		GridPane rootPane = new GridPane();
		rootPane.setPadding(new Insets(20));
		rootPane.setAlignment(Pos.CENTER);
		rootPane.setHgap(10);
		rootPane.setVgap(10);
		
		ListView<String> chatListView = new ListView<String>();
		chatListView.setItems(client.chatLog);
		
		TextField chatTextField = new TextField();
		chatTextField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("passing123");
				client.writeToServer(chatTextField.getText());
				chatTextField.clear();
			}
			
		});
		
		rootPane.add(chatListView, 0, 0);
		rootPane.add(chatTextField, 0, 1);
		
		return new Scene(rootPane, 400, 400);
	}
	

}
