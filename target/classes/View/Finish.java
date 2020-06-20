package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import sample.Main;

import java.io.IOException;

public class Finish extends MyViewController {
    public ImageView FinishImage;
    public void nextlevel(ActionEvent event) throws IOException {
        this.mediaPlayers[this.sounds.length-2].stop();
        Main.prim = (Stage) this.BorderPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/MyView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Main.prim.setScene(scene);
        Main.prim.setResizable(true);
        Main.prim.setTitle("Rick And Morty - Maze Game");
        MyViewController view = loader.getController();
        view.setViewModel(this.viewModel);
        Main.prim.setOnCloseRequest(e->{
            e.consume();
            view.Exit();
        });
        Main.prim.centerOnScreen();
        Main.prim.show();
        this.viewModel.addObserver(view);
    }

}
