package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {
    Button b;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../View/MyView.fxml"));
        primaryStage.setTitle("Hello World");
        b = new Button();
        b.setText("TOM");
        b.setOnAction(e->{
            System.out.println("i did it");
        });
        StackPane pane = new StackPane();
        pane.getChildren().add(b);
        primaryStage.setScene(new Scene(pane, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
