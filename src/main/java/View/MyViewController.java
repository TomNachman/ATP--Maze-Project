package View;

import ViewModel.MyViewModel;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sample.Main;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Random;

public class MyViewController implements IView, Observer {

    public MazeDisplayer mazeDisplayer;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Label labelCatch;
    public ImageView speaker;
    public Pane finishPane;
    public BorderPane BorderPane;
    public Pane MainPane;
    public Pane MazePane;
    public Rectangle MazeRectangle;
    public MenuBar optionsMenu;
    public ImageView background;
    public ChoiceBox<Object> Level;
    public ChoiceBox<Object> Character;
    public Button NextLevel;
    public Button generateButton;


    protected MyViewModel viewModel;
    private boolean mazeExists = false;
    private boolean boolPhrase = false;
    private final String [] phrases = new String[13];
    protected final Media [] sounds = new Media[13];
    protected final MediaPlayer [] mediaPlayers = new MediaPlayer[13];
    private Media failMedia;
    private MediaPlayer failMediaPlayer;
    private int rows;
    private int cols;
    private double mHeight;
    private double mWidth;
    private static final Logger LOG = LogManager.getLogger();


    boolean plumbsLoaded = false;
    Media plumbusMedia;
    MediaPlayer plumbusPlayer;

    public void init(){
        initiateCharacters();
        initiateLevels();
        setMazeLevel();
        GenerateMaze();
        setCharacters();

        mHeight = BorderPane.getHeight();
        mWidth = BorderPane.getWidth();
        dynamicResize();

        // Dynamic Properties
        BorderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            mHeight = newValue.doubleValue();
            dynamicResize();
        });
        BorderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            mWidth = newValue.doubleValue();
            optionsMenu.setPrefWidth(mWidth);
            dynamicResize();
        });

        Level.setOnAction(event -> setMazeLevel());
        Character.setOnAction(event -> setCharacters());
        speaker.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> setSpeaker());
        viewModel.addObserver(this);
    }

    public void setViewModel(MyViewModel vm){
        this.viewModel = vm;
        this.viewModel.addObserver(this);
    }

    private void dynamicResize(){

        double minSize = Math.min(mHeight, mWidth);
        minSize = Math.min(minSize, 1200);
        double recSize = minSize*0.8;

        MazeRectangle.setHeight(recSize);
        MazeRectangle.setWidth(recSize);

        mazeDisplayer.setHeight(recSize);
        mazeDisplayer.setWidth(recSize);

        // ChoiceBoxes
        Character.setLayoutX(mazeDisplayer.getLayoutX() + (mazeDisplayer.getWidth()-560)/2);
        Character.setLayoutY(mazeDisplayer.getLayoutY()+ mazeDisplayer.getHeight() +5);

        Level.setLayoutX(Character.getLayoutX() + Character.getWidth() + 10);
        Level.setLayoutY(mazeDisplayer.getLayoutY()+ mazeDisplayer.getHeight() +5);

        // Generate Button
        generateButton.setLayoutX(Level.getLayoutX() + Level.getWidth() + 10);
        generateButton.setLayoutY(mazeDisplayer.getLayoutY()+ mazeDisplayer.getHeight() +5);

        // Finish Pane
        finishPane.setLayoutX(mazeDisplayer.getLayoutX() + (mazeDisplayer.getWidth()-finishPane.getWidth())/2);
        finishPane.setLayoutY(mazeDisplayer.getLayoutY() + (mazeDisplayer.getHeight()-finishPane.getHeight())/2);

        background.setX(0);
        background.setY(0);

        mazeDisplayer.drawMaze();
        mazeDisplayer.ReDrawCharacter();
        mazeDisplayer.drawPortal();
    }

    private void setSpeaker(){
        if(mediaPlayers[sounds.length-2].isMute()){
            speaker.setImage(new Image(getClass().getResource("/Images/soundOn.png").toString()));
            mediaPlayers[sounds.length-2].setMute(false);
        }
        else{
            speaker.setImage(new Image(getClass().getResource("/Images/soundOff.png").toString()));
            mediaPlayers[sounds.length-2].setMute(true);
        }
    }

    private void initiateCharacters(){
        Character.setItems(FXCollections.observableArrayList(
                "Morty", "Summer", "Pickle Rick", "Worm Jerry",
                new Separator(), "Bird-Person", "Scary Terry",
                new Separator(), "Mr. Meeseeks", "Mr. Poopy Buttholee"
        ));
        Character.setValue("Morty");
        Character.setFocusTraversable(false);

    }

    private void initiateLevels(){
        Level.setItems(FXCollections.observableArrayList(
                "Very Easy", "Easy", "Medium",
                new Separator(), "Challenging", "Hard", "Very Hard",
                new Separator(), "Expert", "Genius", "Rick"
        ));
        Level.setValue("Very Easy");
        Level.setFocusTraversable(false);
    }

    public void setCharacters(){
        String url = "file:" + System.getProperty("user.dir").replace("\\", "/") + "/resources/Images/";
        switch (Character.getValue().toString()){
            case "Morty": url = url.concat("Morty.png"); break;
            case "Bird-Person": url = url.concat("Birdperson.png"); break;
            case "Scary Terry": url = url.concat("ScaryTerrytransparent.gif"); break;
            case "Worm Jerry": url = url.concat("Jerry.png"); break;
            case "Mr. Meeseeks": url = url.concat("Mr_Meeseeks.png"); break;
            case "Mr. Poopy Buttholee": url = url.concat("mr-poopy-buttholee.png"); break;
            case "Summer": url = url.concat("Summer.png"); break;
            case "Pickle Rick": url = url.concat("pickle.png"); break;
        }
        Image image = new Image(url);
        setCharacters(image);
        mazeDisplayer.ReDrawCharacter();
    }

    public void setCharacters(Image image){
        mazeDisplayer.setCharacterImage(image);
        mazeDisplayer.setCharactersPosition(viewModel.getCharacterRowPos(), viewModel.getCharacterColPos());
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
        mazeDisplayer.setGoalPostion(viewModel.getGoalPosition());
        mazeDisplayer.drawPortal();
        setCharacters();
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

    public void setMazeLevel(){
        switch (Level.getValue().toString()){
            case "Very Easy": rows = 5; cols = 5; break;
            case "Easy": rows = 8; cols = 8; break;
            case "Medium": rows = 15; cols = 15; break;
            case "Challenging": rows = 30; cols = 30; break;
            case "Hard": rows = 50; cols = 50; break;
            case "Very Hard": rows = 70; cols = 70; break;
            case "Expert": rows = 100; cols = 100; break;
            case "Genius": rows = 150; cols = 150; break;
            case "Rick": rows = 200; cols = 200; break;
        }
        GenerateMaze();
    }

    public void GenerateMaze(){
        // Add Music
        if(!boolPhrase){setMusic();}

        // Change Music
        mediaPlayers[sounds.length-1].stop();
        mediaPlayers[sounds.length-2].setAutoPlay(true);
        mediaPlayers[sounds.length-2].setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayers[sounds.length-2].play();

        // Finish Pane
        finishPane.setVisible(false);

        // Logic and View
        viewModel.generateMaze(rows,cols);
        mazeExists = true;
        this.displayMaze(viewModel.getMaze());
        LOG.info("User generated new maze");
    }

    public void SolveMaze() {
        if (!mazeExists)
            showAlert("Please generate a maze first");
        else {
            LOG.info("user requested solution");
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
            default: viewModel.MoveCharacter(keyEvent.getCode());break;
        }
        if (viewModel.getCharacterRowPos() == viewModel.getGoalPosition().getRowIndex() && viewModel.getCharacterColPos() == viewModel.getGoalPosition().getColumnIndex()){
            finishMaze();
        }
    }

    public void mazeMouseClicked(){
        mazeDisplayer.requestFocus();
    }

    public void finishMaze()  {

        // Add Music
        if(!boolPhrase){setMusic();}
        finishPane.setVisible(true);
        if(Level.getValue().equals("Rick"))
            NextLevel.setDisable(true);
        if(mediaPlayers[sounds.length-2].isAutoPlay())
            mediaPlayers[sounds.length-2].stop();
        mediaPlayers[sounds.length-1].setAutoPlay(true);
        mediaPlayers[sounds.length-1].setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayers[sounds.length-1].play();
        LOG.info("user solved the maze");

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
            LOG.error("could not load maze ");
            showAlert("Maze loading was cancelled.", "Load Error!");
            return;
        }
        this.viewModel.loadMaze(chosenMaze);
        this.displayMaze(this.viewModel.getMaze());
        LOG.info("user loaded maze "+ chosenMaze.getName());
    }

    private File openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your Rick & Morty maze");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/resources/"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        return fileChooser.showOpenDialog(Main.prim);
    }

    public void saveMaze() {
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
            LOG.info("user saved maze as "+savedFile.getName());
            if (savedFile != null) {
                viewModel.saveMaze(savedFile);

                showAlert("Maze saved: " + savedFile.getName(), "Save Success!");
            }
            else {
                showAlert("Maze save was cancelled.", "Save Error!");
            }
        }
    }

    public void nextLevel(){
        NextLevel.setDisable(false);
        switch (Level.getValue().toString()){
            case "Very Easy": Level.setValue("Easy"); break;
            case "Easy": Level.setValue("Medium"); break;
            case "Medium": Level.setValue("Challenging"); break;
            case "Challenging": Level.setValue("Hard"); break;
            case "Hard": Level.setValue("Very Hard"); break;
            case "Very Hard": Level.setValue("Expert"); break;
            case "Expert": Level.setValue("Genius"); break;
            case "Genius": Level.setValue("Rick"); break;
            case "Rick":
                NextLevel.setDisable(true);
                NextLevel.setVisible(false);
            break;
        }
    }

    //---- help ----//
    public void openInstructions() throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Instructions");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("Instructions.fxml").openStream());
        stage.setScene(new Scene(root, 600, 400));
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
    public void openAbout() throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("About");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
        stage.setScene(new Scene(root, 600, 340));
        stage.show();
    }

    //---- Exit ----//
    public int Exit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            viewModel.stopServers();
            Platform.exit();
            return 0;
        }
        return 1;
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
        plumbusStage.setOnCloseRequest(event -> plumbusPlayer.stop());

        if(mediaPlayers[sounds.length-1].isAutoPlay()) mediaPlayers[sounds.length-1].stop();
        if(mediaPlayers[sounds.length-2].isAutoPlay()) mediaPlayers[sounds.length-2].stop();
    }

    private void musicFail(){
        if(null==failMediaPlayer){
            File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/resources/CatchPhrases/bipSound.mp3");
            String path = file.toURI().toASCIIString();
            failMedia = new Media(path);
            failMediaPlayer = new MediaPlayer(failMedia);
        }
        failMediaPlayer.setVolume(0.5);
        failMediaPlayer.stop();
        failMediaPlayer.play();
    }

    @Override
    public void update(Observable o, Object arg) {
        switch((String)arg){
            case "moved": mazeDisplayer.setCharactersPosition(viewModel.getCharacterRowPos(), viewModel.getCharacterColPos());break;
            case "notMoved": musicFail(); break;
            case "generated": displayMaze(viewModel.getMaze()); break;
        }
    }

}