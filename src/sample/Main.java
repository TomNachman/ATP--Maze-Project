package sample;

import Model.MyModel;
import ViewModel.MyViewModel;
import View.MyViewController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage prim;
    Button b;
    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        model.StartServers();
        MyViewModel viewModel = new MyViewModel(model);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/MyView.fxml"));
        Parent root = loader.load();
        MyViewController view = loader.getController();
        view.setViewModel(viewModel);
        primaryStage.setTitle("Rick And Morty - Maze Game");
        primaryStage.setScene(new Scene(root, 800, 750));
        primaryStage.setMinHeight(750);
        primaryStage.setMinWidth(800);
        primaryStage.setOnCloseRequest(event -> {view.Exit();});
        view.init();

        prim = primaryStage;
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
