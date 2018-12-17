package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;


public class Main extends Application {

public static void main(String[] args)
{Application.launch(args);}
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setTitle("RF Tool");
        primaryStage.setScene(new Scene(root, 1200, 675));
        primaryStage.show();
        }
}

