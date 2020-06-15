package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Main;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.Random;

public class MyViewController implements IView {

    public MazeDisplayer mazeDisplayer;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Label labelCatch;
    public ImageView finishImage;
    public BorderPane BorderPane;
    public Pane MainPane;
    public Pane MazePane;
    public Rectangle MazeRectangle;
    public MenuBar optionsMenu;
    public ImageView background;
    public ChoiceBox Level;
    private MyViewModel viewModel;// = sample.Main.vm;
    private boolean mazeExists = false;
    private boolean boolPhrase = false;
    private String [] phrases = new String[13];
    private Media [] sounds = new Media[13];
    private MediaPlayer [] mediaPlayers = new MediaPlayer[13];
    private Desktop desktop = Desktop.getDesktop();
    private Position startPos;
    private Position goalPos;
    private double mHeight;
    private double mWidth;



    boolean plumbsLoaded = false;
    Media plumbusMedia;
    MediaPlayer plumbusPlayer;

    public void init(){
        GenerateMaze();
        mHeight = BorderPane.getHeight();
        mWidth = BorderPane.getWidth();

        dynamicResize();

        BorderPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mHeight = newValue.doubleValue();
                dynamicResize();
            }
        });

        BorderPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mWidth = newValue.doubleValue();
                optionsMenu.setPrefWidth(mWidth);
                dynamicResize();
            }
        });


    }

    private void dynamicResize(){

        double minSize = Math.min(mHeight, mWidth);
        minSize = Math.min(minSize, 1200);
        double recSize = minSize*0.8;

        MazeRectangle.setHeight(recSize);
        MazeRectangle.setWidth(recSize);

        mazeDisplayer.setHeight(recSize);
        mazeDisplayer.setWidth(recSize);

        background.setX(0);
        background.setY(0);

        //System.out.println(String.format("minSize: (%.2f,%.2f)", mHeight, mWidth));
    }

    public void setViewModel(MyViewModel vm){
        this.viewModel = vm;
    }

    public void setCharacters(){
        Image image = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/resources/Images/Morty.png");
        setCharacters(image);
    }

    public void setCharacters(Image image){
        mazeDisplayer.setCharactersPos(viewModel.getStartPosition(), viewModel.getGoalPosition());
        mazeDisplayer.setCharacterImage(image);
    }

    @Override
    public void displayMaze(int[][] maze) {
        setCharacters();
        mazeDisplayer.setMaze(maze);
    }

    public void displaySolution(Solution sol){
        mazeDisplayer.setSolution(sol);
    }

    public void setMusic() {

        // Main Soundtracks:
        insertMedia("Evil Morty-Remix", sounds.length-2);
        insertMedia("Theme Song",sounds.length-1);

        // CatchPhrases Soundtracks:
        phrases[0] = "Aids!";
        insertMedia("Aids!",0);

        phrases[1] = "Wubba Lubba Dub Dub";
        insertMedia("wabalabadubdub",1);

        phrases[2] = "Hit the sack Jack!";
        insertMedia("Hit The Sack Jack",2);

        phrases[3] = "And that's why I always say, Shumshumschilpiddydah!";
        insertMedia("Shlam Shlam",3);

        phrases[4] = "Rubber baby burger bumpers";
        insertMedia("rubber",4);

        phrases[5] = "And that's the waaaaaaay the news goes!";
        insertMedia("The News",5);

        phrases[6] = "Grassssss....... tastes bad.";
        insertMedia("Grass Taste bad",6);

        phrases[7] = "No jumpin' in the sewer!";
        insertMedia("No jumpin",7);

        phrases[8] = "Uh-oh, somersault jump!";
        insertMedia("Uh-oh",8);

        phrases[9] = "Rikki-Tikki-Tavi, biatch!";
        insertMedia("Rikki-Tikki",9);

        phrases[10] = "Ha ha ha, Yeah! I say that all the time";
        insertMedia("say that all the time",10);

        boolPhrase = true;
    }

    public void GenerateMaze(){
        int rows = 2;
        int cols = 2;

        switch (Level.getValue().toString()){
            case "Very Easy": rows = 5; cols = 5; break;
            case "Easy": rows = 10; cols = 10; break;
            case "Medium": rows = 30; cols = 30; break;
            case "Hard": rows = 50; cols = 50; break;
            case "Very Hard": rows = 70; cols = 70; break;
            case "Expert": rows = 100; cols = 100; break;
            case "Genius": rows = 150; cols = 150; break;
            case "Rick": rows = 200; cols = 200; break;
        }

            // Add Music
            if(!boolPhrase){setMusic();}

            // Change Music
            mediaPlayers[sounds.length-1].stop();
            mediaPlayers[sounds.length-2].setAutoPlay(true);
            mediaPlayers[sounds.length-2].setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayers[sounds.length-2].play();

            // Finish Image
            //finishImage.setVisible(false);

            // Logic and View
            viewModel.generateMaze(rows,cols);
            mazeExists = true;
            this.displayMaze(viewModel.getMaze());
    }

    public void SolveMaze() {
        if (!mazeExists)
            showAlert("Please generate a maze first");
        else {
            this.displaySolution(viewModel.getSolution());
        }
    }

    public void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Invalid Input");
        alert.setContentText(message);
        alert.show();
    }

    public void showAlert(String message, String name){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(name);
        alert.setContentText(message);
        alert.show();
    }

    public void catchPhrase(){
        // Add Music
        if(!boolPhrase){setMusic();}

        Random random = new Random();
        int phrase = random.nextInt(phrases.length-2);
        labelCatch.setText(String.format("\"%s\"",phrases[phrase]));
        mediaPlayers[phrase].play();
    }

    public void keyPressed(javafx.scene.input.KeyEvent keyEvent) throws IOException {
        switch(keyEvent.getCode()){
            case F1: GenerateMaze(); break;
            case F2: saveMaze(); break;
            case F3: loadMaze(); break;
            case CONTROL: { mazeDisplayer.Zoom();}
        }
    }

    public void finishMaze(ActionEvent e) {
        // Add Music
        if(!boolPhrase){setMusic();}

        finishImage.setVisible(true);
        if(mediaPlayers[sounds.length-2].isAutoPlay())
            mediaPlayers[sounds.length-2].stop();
        mediaPlayers[sounds.length-1].setAutoPlay(true);
        mediaPlayers[sounds.length-1].setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayers[sounds.length-1].play();

    }

    private void insertMedia(String name, int index){
        File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/resources/CatchPhrases/" + name + ".mp3");
        String path = file.toURI().toASCIIString();
        sounds[index] = new Media(path);
        mediaPlayers[index] = new MediaPlayer(sounds[index]);
    }

    public void loadMaze(){
        File chosenMaze = openFile();
        if(chosenMaze==null)
        {
            showAlert("Maze loading was cancelled.", "Load Error!");
            return;
        }
        this.viewModel.loadMaze(chosenMaze);
        this.displayMaze(this.viewModel.getMaze());
    }

    private File openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your Rick & Morty maze");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/resources/"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        File chosenFile = fileChooser.showOpenDialog(Main.prim);
        return chosenFile;
    }

    public void saveMaze() throws IOException {
        if(!mazeExists) {
            showAlert("Please generate maze before saving...", "Save Error!");
        }
        else{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save maze");
            fileChooser.setInitialFileName("Rick & Morty Maze");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/resources/"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
            File savedFile = fileChooser.showSaveDialog(null);

            if (savedFile != null) {
                viewModel.saveMaze(savedFile);

                showAlert("Maze saved: " + savedFile.getName(), "Save Success!");
            }
            else {
                showAlert("Maze save was cancelled.", "Save Error!");
            }
        }
    }

    //---- help ----//
    public void openInstructions() throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Instructions");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("Instructions.fxml").openStream());
        stage.setScene(new Scene(root, 400, 400));
        stage.show();
    }

    //---- Properties ---//
    public void openProperties(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Information");
        String genMethod = Server.Configurations.GetProp("GenerateAlgorithm").replaceAll("([A-Z])", " $1");
        String solveMethod = Server.Configurations.GetProp("SolvingAlgorithm").replaceAll("([A-Z])", " $1");
        alert.setContentText("Generator: " + genMethod +"\n" +
                "Searching Algorithm: "+ solveMethod + "\n" +
                "ThreadPool Size: " + Server.Configurations.GetProp("NumOfThreads"));
        alert.show();
    }

    //---- About ----//
    public void openAbout(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("About");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
        stage.setScene(new Scene(root, 600, 340));
        stage.show();
    }

    //---- Exit ----//
    public void Exit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            viewModel.stopServers();
            System.exit(0);
        }
    }

    //---- Watch Rick & Morty ----//
    public void Episodes() throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URL("https://www.adultswim.com/videos/rick-and-morty").toURI());
    }

    //---- How They Do It: Plumbs ----//
    public void Plumbus(){
        if(!plumbsLoaded){
            File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/resources/Videos/Plumbus.mp4");
            String path = file.toURI().toASCIIString();
            plumbusMedia = new Media(path);
            plumbusPlayer = new MediaPlayer(plumbusMedia);
            plumbsLoaded = true;
        }
        plumbusPlayer.setAutoPlay(true);
        MediaView mediaView = new MediaView (plumbusPlayer);

        Group root = new Group();
        root.getChildren().add(mediaView);
        Scene scene = new Scene(root,1280,725);
        Stage plumbusStage = new Stage();
        plumbusStage.setResizable(false);
        plumbusStage.setScene(scene);
        plumbusStage.setTitle("Playing video: How They Do It - Plumbs");
        plumbusStage.show();
        plumbusPlayer.play();
        plumbusStage.setOnCloseRequest(event -> {plumbusPlayer.stop();});

        if(mediaPlayers[sounds.length-1].isAutoPlay()) mediaPlayers[sounds.length-1].stop();
        if(mediaPlayers[sounds.length-2].isAutoPlay()) mediaPlayers[sounds.length-2].stop();
    }

    //---- Worlds ----//
    public void openWorlds(ActionEvent event) throws IOException {
        Stage stageWorld = new Stage();
        stageWorld.setResizable(false);
        stageWorld.setTitle("Rick And Morty Worlds");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("../View/Worlds.fxml").openStream());
        stageWorld.setScene(new Scene(root, 600, 300));
        stageWorld.show();
    }
}