package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;


public class Main extends Application {

public static void main(String[] args)
{Application.launch(args);}
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("RF Tool");
        primaryStage.setScene(new Scene(root, 1250, 600));
        primaryStage.show();

    }
}
