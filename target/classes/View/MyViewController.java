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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
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
import sun.security.krb5.internal.rcache.DflCache;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MyViewController implements IView, Observer {

    public MazeDisplayer mazeDisplayer;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Label labelCatch;
    public Label labelSteps;
    public ImageView speaker;
    public ImageView RickTitle;
    public Pane finishPane;
    public BorderPane BorderPane;
    public Pane MainPane;
    public Pane MazePane;
    public Rectangle MazeRectangle;
    public MenuBar optionsMenu;
    public ChoiceBox<Object> Level;
    public ChoiceBox<Object> Character;
    public Button playAgain;
    public Button NextLevel;
    public Button generateButton;
    public Menu option;


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
    private Scene myScene;
    private int stepCounter;
    private static final Logger LOG = LogManager.getLogger();

    boolean plumbsLoaded = false;
    Media plumbusMedia;
    MediaPlayer plumbusPlayer;

    public void init(){
        initiateCharacters();
        initiateLevels();

        setMusic();

        setMazeLevel();
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

    public void setViewModel(MyViewModel vm, Scene scene){
        this.viewModel = vm;
        this.viewModel.addObserver(this);
        this.myScene = scene;
    }

    private void dynamicResize(){

        double minSize = Math.min(mHeight, mWidth);
        minSize = Math.min(minSize, 950);
        double recSize = minSize*0.8;

        double titleSize = Math.pow(recSize,1.2);
        if(minSize>=850) titleSize*=2;

        RickTitle.setFitHeight(40 * titleSize/2000);
        RickTitle.setFitWidth(150 * titleSize/2000);

        MazePane.setLayoutX(-0.5*(recSize-800));
        RickTitle.setLayoutX(MazePane.getLayoutX() + (recSize-RickTitle.getFitWidth())/2);
        RickTitle.setLayoutY(MazePane.getLayoutY()-RickTitle.getFitHeight());

        MazeRectangle.setHeight(recSize);
        MazeRectangle.setWidth(recSize);

        mazeDisplayer.setHeight(recSize);
        mazeDisplayer.setWidth(recSize);

        // ChoiceBoxes
        Character.setLayoutX(MazePane.getLayoutX() + (mazeDisplayer.getWidth()-560)/2);
        Character.setLayoutY(MazePane.getLayoutY() + mazeDisplayer.getHeight() +5);

        Level.setLayoutX(Character.getLayoutX() + Character.getWidth() + 10);
        Level.setLayoutY(MazePane.getLayoutY() + mazeDisplayer.getHeight() +5);

        // Generate Button
        generateButton.setLayoutX(Level.getLayoutX() + Level.getWidth() + 10);
        generateButton.setLayoutY(MazePane.getLayoutY() + mazeDisplayer.getHeight() +5);

        // Speaker Button
        speaker.setLayoutX(Character.getLayoutX() - 100);
        speaker.setLayoutY(MazePane.getLayoutY() + mazeDisplayer.getHeight());

        // Finish Pane
        finishPane.setLayoutX(mazeDisplayer.getLayoutX() + (mazeDisplayer.getWidth()-finishPane.getWidth())/2);
        finishPane.setLayoutY(mazeDisplayer.getLayoutY() + (mazeDisplayer.getHeight()-finishPane.getHeight())/2);

        mazeDisplayer.drawMaze();
        mazeDisplayer.drawPortal();
        mazeDisplayer.ReDrawCharacter();
    }

    private void setSpeaker(){
        if(mediaPlayers[sounds.length-2].isMute()){
            speaker.setImage(new Image(getClass().getResource("/Images/soundOn.png").toString()));
            mediaPlayers[sounds.length-2].setMute(false);
            mediaPlayers[sounds.length-1].setMute(false);
            failMediaPlayer.setMute(false);

        }
        else{
            speaker.setImage(new Image(getClass().getResource("/Images/soundOff.png").toString()));
            mediaPlayers[sounds.length-2].setMute(true);
            mediaPlayers[sounds.length-1].setMute(true);
            failMediaPlayer.setMute(true);
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
        setCharacters();
        mazeDisplayer.drawPortal();
    }

    public void displaySolution(Solution sol){
        mazeDisplayer.setSolution(sol);
    }

    public void setMusic() {

        setFailStep();

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

        mediaPlayers[3].setOnEndOfMedia(() -> {
            mediaPlayers[sounds.length-1].setVolume(0.2);
            mediaPlayers[sounds.length-1].setAutoPlay(true);
            mediaPlayers[sounds.length-1].setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayers[sounds.length-1].play();
            playAgain.setDisable(false);
            NextLevel.setDisable(false);
        });

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
        // enabled Buttons
        Character.setDisable(false);
        Level.setDisable(false);
        generateButton.setDisable(false);
        NextLevel.setDisable(true);
        playAgain.setDisable(true);

        // Change Music
        mediaPlayers[sounds.length-1].stop();
        mediaPlayers[sounds.length-1].setVolume(1.2);
        mediaPlayers[sounds.length-2].setAutoPlay(true);
        mediaPlayers[sounds.length-2].setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayers[sounds.length-2].play();

        // Finish Pane
        finishPane.setVisible(false);

        // Logic and View
        viewModel.generateMaze(rows,cols);
        mazeExists = true;
        this.displayMaze(viewModel.getMaze());
        mazeDisplayer.drawPortal();

        this.stepCounter = 0;
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

    public void keyPressed(javafx.scene.input.KeyEvent keyEvent) {
        if (!finishPane.isVisible()) {
            switch(keyEvent.getCode()){
                case F1: GenerateMaze(); break;
                case F2: saveMaze(); break;
                case F3: loadMaze(); break;
                case CONTROL: { mazeDisplayer.Zoom();}
                default: viewModel.MoveCharacter(keyEvent.getCode());break;
            }

            if (mazeDisplayer.characterPositionRow == viewModel.getGoalPosition().getRowIndex() && mazeDisplayer.characterPositionColumn == viewModel.getGoalPosition().getColumnIndex()) {
                finishMaze();
            }
        }
    }

    public void mazeMouseClicked(){
        mazeDisplayer.requestFocus();
    }

    public void finishMaze() {
        // Add Music
        finishPane.setVisible(true);
        labelSteps.setText(String.format("You Moved %d Steps.", this.stepCounter));
        if(Level.getValue().equals("Rick"))
            NextLevel.setDisable(true);
        if(mediaPlayers[sounds.length-2].isAutoPlay())
            mediaPlayers[sounds.length-2].stop();

        Character.setDisable(true);
        Level.setDisable(true);
        generateButton.setDisable(true);

        mediaPlayers[3].stop();
        mediaPlayers[3].play();

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
        if(chosenMaze!=null)
        {
            this.viewModel.loadMaze(chosenMaze);
            this.displayMaze(this.viewModel.getMaze());
            LOG.info("user loaded maze "+ chosenMaze.getName());
        }

    }

    private File openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your Rick & Morty maze");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/resources/Mazes"));
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
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/resources/Mazes"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
            File savedFile = fileChooser.showSaveDialog(null);
            if (savedFile != null) {
                viewModel.saveMaze(savedFile);
                LOG.info("user saved maze as "+savedFile.getName());
                showAlert("Maze saved: " + savedFile.getName(), "Save Success!");
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

    public void changeLevel(){
        switch (this.viewModel.getMaze().length){
            case 5: Level.setValue("Very Easy"); break;
            case 8: Level.setValue("Easy"); break;
            case 15: Level.setValue("Medium"); break;
            case 30: Level.setValue("Challenging"); break;
            case 50: Level.setValue("Hard"); break;
            case 70: Level.setValue("Very Hard"); break;
            case 100: Level.setValue("Expert"); break;
            case 150: Level.setValue("Genius"); break;
            case 200: Level.setValue("Rick"); break;
        }
    }

    //---- help ----//
    public void openInstructions() throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Instructions");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("Instructions.fxml").openStream());
        stage.setScene(new Scene(root, 700, 400));
        stage.show();
    }

    //---- Properties ---//
    public void selectSearchingAlgo(ActionEvent event) {
        event.consume();
        viewModel.setSearchAlgo(event.getSource().toString().contains("BFS")? "BFS":
                                event.getSource().toString().contains("DFS")? "DFS": "best");
    }

    public void selectGeneratingAlgo(ActionEvent event) {
        event.consume();
        viewModel.setGeneratingAlgo(event.getSource().toString().contains("simple")? "simple": "complicated");
    }

    //---- About ----//
    public void openAbout() throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("About");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
        stage.setScene(new Scene(root, 700, 400));
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
        plumbusStage.setOnCloseRequest(event ->
        {
            plumbusPlayer.stop();
            mediaPlayers[sounds.length-2].setMute(false);
            mediaPlayers[sounds.length-1].setMute(false);

        });

        if(mediaPlayers[sounds.length-1].isAutoPlay()) mediaPlayers[sounds.length-1].setMute(true);
        if(mediaPlayers[sounds.length-2].isAutoPlay()) mediaPlayers[sounds.length-2].setMute(true);
    }

    private void setFailStep(){
        File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/resources/CatchPhrases/bipSound.mp3");
        String path = file.toURI().toASCIIString();
        failMedia = new Media(path);
        failMediaPlayer = new MediaPlayer(failMedia);
    }

    private void musicFail(){
        failMediaPlayer.setVolume(0.2);
        failMediaPlayer.stop();
        failMediaPlayer.play();
    }

    public void dragPlayer(MouseEvent mouseEvent) {
        double cellHeight = mazeDisplayer.getHeight()/ viewModel.getMaze().length;
        double cellWidth = mazeDisplayer.getWidth() / viewModel.getMaze()[0].length;
        double mouseX =(int) ((mouseEvent.getX()) / (cellWidth));
        double mouseY =(int) ((mouseEvent.getY()) / (cellHeight));
        //if not finished
        if(!(viewModel.getCharacterRowPos() == viewModel.getGoalPosition().getRowIndex() && viewModel.getCharacterColPos() == viewModel.getGoalPosition().getColumnIndex())){
            if(mouseY < viewModel.getCharacterRowPos() && mouseX==viewModel.getCharacterColPos())
                viewModel.MoveCharacter(KeyCode.UP);
            if(mouseY > viewModel.getCharacterRowPos() && mouseX==viewModel.getCharacterColPos())
                viewModel.MoveCharacter(KeyCode.DOWN);
            if(mouseX < viewModel.getCharacterColPos() && mouseY == viewModel.getCharacterRowPos())
                viewModel.MoveCharacter(KeyCode.LEFT);
            if(mouseX > viewModel.getCharacterColPos() && mouseY == viewModel.getCharacterRowPos())
                viewModel.MoveCharacter(KeyCode.RIGHT);

        }
        else finishMaze();
    }

    @Override
    public void update(Observable o, Object arg) {
        switch((String)arg){
            case "moved":
                if(!finishPane.isVisible()) {
                    mazeDisplayer.setCharactersPosition(viewModel.getCharacterRowPos(), viewModel.getCharacterColPos());
                    stepCounter++;
                }
                break;
            case "illegalMove": musicFail(); break;
            case "Loaded": changeLevel(); break;
            case "algorithmChanged": GenerateMaze(); break;
            case "generated": displayMaze(viewModel.getMaze()); break;
        }
    }
}