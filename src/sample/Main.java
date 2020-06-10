package sample;

import Model.MyModel;
import View.MyViewController;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    public static MyViewModel vm;
    public static Stage prim;
    Button b;
    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        model.StartServers();
        vm = new MyViewModel(model);
        Parent root = FXMLLoader.load(getClass().getResource("../View/Try.fxml"));
        primaryStage.setTitle("Rick And Morty - Maze Game");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
