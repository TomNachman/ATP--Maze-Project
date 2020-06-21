package sample;

import Model.MyModel;
import ViewModel.MyViewModel;
import View.MyViewController;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.Observer;

public class Main extends Application {
    public static Stage prim;
    Button b;
    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        model.StartServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/MyView.fxml"));
        Parent root = loader.load();
        MyViewController view = loader.getController();
        view.setViewModel(viewModel);


        primaryStage.setTitle("Rick And Morty - Maze Game");
        primaryStage.setScene(new Scene(root, 850, 750));
        primaryStage.setMinHeight(750); primaryStage.setMinWidth(800);
        primaryStage.setOnCloseRequest(event -> {if(view.Exit()==1) event.consume();});

        String image = "../../../../resources/Images/Background2.png";
        String style = "-fx-background-image: url('"+image+"');";
        (root).setStyle(style);
        //((BorderPane)root).setBackground(Background.EMPTY);

        view.init();

        prim = primaryStage;
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
