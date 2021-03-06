package sample;

import Model.MyModel;
import View.MyViewController;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage prim;
    @Override
    public void start(Stage primaryStage) throws Exception{

        // Model and View Model
        MyModel model = new MyModel();
        model.StartServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);

        // FXML and View
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/MyView.fxml"));
        Parent root = loader.load();
        MyViewController view = loader.getController();
        primaryStage.setTitle("Rick And Morty - Maze Game");
        Scene scene = new Scene(root, 800, 750);
        String url = "file:" + System.getProperty("user.dir").replace("\\", "/") + "/resources/Images/pickle.png";
        primaryStage.getIcons().add(new Image(url));

        // Primary Stage last tuning
        view.setViewModel(viewModel);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800); primaryStage.setMinHeight(750);
        primaryStage.setOnCloseRequest(event -> {if(view.Exit()==1) event.consume();});

        // View Background
        (root).setStyle("-fx-background-image: url('/Images/Background2.png');");
        ((BorderPane)root).setBackground(Background.EMPTY);
        view.init();
        prim = primaryStage;
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
