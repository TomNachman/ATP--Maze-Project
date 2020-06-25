package View;

import ViewModel.MyViewModel;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Random;

/**
 *      Class MyViewController: the 'Main' controller of the View (MyView.fxml)
 */
public class MyViewController implements IView, Observer {

    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public Label labelSteps;
    @FXML
    public ImageView speaker;
    @FXML
    public ImageView RickTitle;
    @FXML
    public Pane finishPane;
    @FXML
    public BorderPane BorderPane;
    @FXML
    public Pane MainPane;
    @FXML
    public Pane MazePane;
    @FXML
    public Rectangle MazeRectangle;
    @FXML
    public MenuBar optionsMenu;
    @FXML
    public ChoiceBox<Object> Level;
    @FXML
    public ChoiceBox<Object> Character;
    @FXML
    public Button playAgain;
    @FXML
    public Button NextLevel;
    @FXML
    public Button generateButton;
    @FXML
    public Menu option;
    @FXML
    public ScrollPane scrollPane;

    protected MyViewModel viewModel;
    private boolean mazeExists = false;
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
    private double scrollSize = 0;

    boolean plumbsLoaded = false;
    Media plumbusMedia;
    MediaPlayer plumbusPlayer;


    /** setViewModel: Function get a viewModel and set it as the view model of the class */
    public void setViewModel(MyViewModel vm){
        this.viewModel = vm;
        this.viewModel.addObserver(this);
    }


    // Initiate: All These functions should be called to initialize the Game at the beginning

    /** init: The function initiate the important functions and declaration for the Game in the first time */
    public void init(){

        // initiate
        initiateCharacters();
        initiateLevels();
        setMusic();
        setMazeLevel();
        setCharacters();

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
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setFocusTraversable(false);

        BorderPane.getScene().getWindow().setWidth(800);
        BorderPane.getScene().getWindow().setHeight(750);
    }

    /** initiateCharacters: The function initiate the 'Characters' options */
    private void initiateCharacters(){
        Character.setItems(FXCollections.observableArrayList(
                "Morty", "Summer", "Pickle Rick", "Worm Jerry",
                new Separator(), "Bird-Person", "Scary Terry",
                new Separator(), "Mr. Meeseeks", "Mr. Poopy Buttholee"
        ));
        Character.setValue("Morty");
        Character.setFocusTraversable(false);

    }

    /** initiateLevels: The function initiate the 'Level' options */
    private void initiateLevels(){
        Level.setItems(FXCollections.observableArrayList(
                "Very Easy", "Easy", "Medium",
                new Separator(), "Challenging", "Hard", "Very Hard",
                new Separator(), "Expert", "Genius", "Rick"
        ));
        Level.setValue("Very Easy");
        Level.setFocusTraversable(false);
    }

    /** dynamicResize: The function change dynamic all the properties that have to be change while changing window's size */
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

        generateButton.setLayoutY(mazeDisplayer.getLayoutY());
        scrollPane.setPrefSize(mazeDisplayer.getHeight()+3, mazeDisplayer.getWidth()+3);

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
        scrollSize = mazeDisplayer.getHeight();
    }


    //Characters: These functions control the characters of the game

    /** setCharacters: The function set the Character according to the player's decision. default - Morty */
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

    /** setCharacters(Image): The function set the Character according to'Image' input */
    public void setCharacters(Image image){
        mazeDisplayer.setCharacterImage(image);
        mazeDisplayer.setCharactersPosition(viewModel.getCharacterRowPos(), viewModel.getCharacterColPos());
    }


    // Level: These functions control the Level of the Game

    /** setMazeLevel: Function set the Level of the game according to player decision */
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

    /** changeLevel: Function set the Level of the game according to Loaded Maze */
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

    /** nextLevel: Function set the Level of the game according to Previous Level */
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


    // Display: These functions control the Game display

    /** displayMaze: Function get array represent the maze and display it on the canvas */
    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
        mazeDisplayer.setGoalPostion(viewModel.getGoalPosition());
        setCharacters();
        mazeDisplayer.drawPortal();
    }

    /** displaySolution: Function get solution and display it on the canvas */
    public void displaySolution(Solution sol){
        mazeDisplayer.setSolution(sol);
    }


    // Maze: These functions control the Game (Generate, Solve, Finish)

    /** GenerateMaze: Function generate new maze, according to level, character */
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
        mazeDisplayer.setWidth(scrollPane.getWidth()-3);
        mazeDisplayer.setHeight(scrollPane.getHeight()-3);

    }

    /** SolveMaze: Function solve maze and represent it by calling 'displaySolution' */
    public void SolveMaze() {
        if (!mazeExists)
            showAlert("Please generate a maze first");
        else {
            LOG.info("user requested solution");
            this.displaySolution(viewModel.getSolution());
        }
    }

    /** finishMaze: Function finish maze, start music, show 'finishPane' and give options to continue */
    public void finishMaze() {
        // Add Music
        finishPane.setVisible(true);
        labelSteps.setText(String.format("You Moved %d Steps.", this.stepCounter));
        if(Level.getValue().equals("Rick"))
            NextLevel.setDisable(true);

        for (MediaPlayer mediaPlayer:mediaPlayers)
            mediaPlayer.stop();

        Character.setDisable(true);
        Level.setDisable(true);
        generateButton.setDisable(true);

        mediaPlayers[3].play();
        LOG.info("user solved the maze");
    }


    // Alert: These functions helps showing alert like we want

    /** showAlert: Function that helps us show a message */
    public void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Invalid Input");
        alert.setContentText(message);
        alert.show();
    }

    /** showAlert: Function that helps us show a message and change the header */
    public void showAlert(String message, String name){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(name);
        alert.setContentText(message);
        alert.show();
    }


    // Movement and Controller: These functions control the movement and keyboard effect

    /** keyPressed: Function help us control the player key pressed and chose function accord to his choise */
    public void keyPressed(javafx.scene.input.KeyEvent keyEvent) {
        if (!finishPane.isVisible()) {
            switch(keyEvent.getCode()){
                case F1: GenerateMaze(); break;
                case F2: saveMaze(); break;
                case F3: loadMaze(); break;
                case CONTROL: {
                    if(scrollSize == 0) scrollSize = mazeDisplayer.getHeight();
                    mazeDisplayer.getScene().setOnScroll(event -> {
                        if(event.isControlDown()){
                            if(mazeDisplayer.getHeight() >= scrollSize){
                                mazeDisplayer.setHeight(mazeDisplayer.getHeight()*(event.getDeltaY()>0?1.03: mazeDisplayer.getHeight() == scrollSize? 1: 0.97));
                                mazeDisplayer.setWidth(mazeDisplayer.getWidth()*(event.getDeltaY()>0?1.03: mazeDisplayer.getHeight() == scrollSize? 1: 0.97));
                                mazeDisplayer.drawMaze();
                                mazeDisplayer.drawPortal();
                                mazeDisplayer.ReDrawCharacter();
                            }
                            else{
                                mazeDisplayer.setHeight(scrollPane.getHeight()-3);
                                mazeDisplayer.setWidth(scrollPane.getWidth()-3);
                            }
                            mazeDisplayer.requestFocus();
                            event.consume();
                        }
                    });
                }
                default: viewModel.MoveCharacter(keyEvent.getCode());break;
            }

            if (mazeDisplayer.characterPositionRow == viewModel.getGoalPosition().getRowIndex() && mazeDisplayer.characterPositionColumn == viewModel.getGoalPosition().getColumnIndex()) {
                finishMaze();
            }
        }
    }

    /** dragPlayer: Function allowed us to move the 'Character' by hold and drag it with the mouse */
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

    /** mazeMouseClicked: Function request focus to the mazeDisplayer */
    public void mazeMouseClicked(){
        mazeDisplayer.requestFocus();
    }


    //Menu Bar: These functions are options chose from the Menu Bar

    /** saveMaze: Function save the current Maze to a file in a '.Maze' format */
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

    /** openFile: Function open a chosen file ('.maze' file) */
    private File openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your Rick & Morty maze");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/resources/Mazes"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        return fileChooser.showOpenDialog(Main.prim);
    }

    /** loadMaze: Function Load the chosen file into the canvas as a new Maze */
    public void loadMaze(){
        File chosenMaze = openFile();
        if(chosenMaze!=null)
        {
            this.viewModel.loadMaze(chosenMaze);
            this.displayMaze(this.viewModel.getMaze());
            LOG.info("user loaded maze "+ chosenMaze.getName());
        }

    }

    /** selectSearchingAlgo: Function change the configuration file (search mode) according the player decision */
    public void selectSearchingAlgo(ActionEvent event) {
        event.consume();
        viewModel.setSearchAlgo(event.getSource().toString().contains("BFS")? "BFS":
                                event.getSource().toString().contains("DFS")? "DFS": "best");
    }

    /** selectSearchingAlgo: Function change the configuration file (generate mode) according the player decision */
    public void selectGeneratingAlgo(ActionEvent event) {
        event.consume();
        viewModel.setGeneratingAlgo(event.getSource().toString().contains("simple")? "simple": "complicated");
    }

    /** openInstructions: Function open the basic Instructions of the maze */
    public void openInstructions() throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Instructions");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("Instructions.fxml").openStream());
        stage.setScene(new Scene(root, 700, 400));
        stage.show();
    }

    /** openAbout: Function open the about of the maze */
    public void openAbout() throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("About");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
        stage.setScene(new Scene(root, 700, 400));
        stage.show();
    }

    /** Exit: Function will exit the program according to player decision */
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


    // Music: These functions related to soundtracks and Catchphrases, control and fail music */

    /** insertMedia: Function get an name of a music file an index and insert the music to an array of mediaPlayer */
    private void insertMedia(String name, int index){
        File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/resources/CatchPhrases/" + name + ".mp3");
        String path = file.toURI().toASCIIString();
        sounds[index] = new Media(path);
        mediaPlayers[index] = new MediaPlayer(sounds[index]);
    }

    /** setSpeaker: Function determine if mute music or not according the previous state */
    private void setSpeaker(){
        if(mediaPlayers[sounds.length-2].isMute()){
            speaker.setImage(new Image(getClass().getResource("/Images/soundOn.png").toString()));
            for (MediaPlayer mediaPlayer : mediaPlayers) {
                mediaPlayer.setMute(false);
            }
            failMediaPlayer.setMute(false);

        }
        else{
            speaker.setImage(new Image(getClass().getResource("/Images/soundOff.png").toString()));
            for (MediaPlayer mediaPlayer : mediaPlayers) {
                mediaPlayer.setMute(true);
            }
            failMediaPlayer.setMute(true);
        }
    }

    /** setMusic: Function initiate the Music the game will need */
    public void setMusic() {

        setFailMusic();

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
            if(finishPane.isVisible()){
                mediaPlayers[sounds.length-1].setVolume(0.5);
                mediaPlayers[sounds.length-1].setAutoPlay(true);
                mediaPlayers[sounds.length-1].setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayers[sounds.length-1].play();
                playAgain.setDisable(false);
                NextLevel.setDisable(false);
            }
        });

        for(MediaPlayer mediaPlayer: mediaPlayers)
            mediaPlayer.setVolume(1.3);
    }

    /** setFailMusic: Function set the fail music */
    private void setFailMusic(){
        File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/resources/CatchPhrases/bipSound.mp3");
        String path = file.toURI().toASCIIString();
        failMedia = new Media(path);
        failMediaPlayer = new MediaPlayer(failMedia);
    }

    /** musicFail: Function play the music of a fail movement */
    private void musicFail(){
        failMediaPlayer.setVolume(0.2);
        failMediaPlayer.stop();
        failMediaPlayer.play();
    }

    /** catchPhrase: Function randomize catchPhrase */
    public void catchPhrase(){
        Random random = new Random();
        for(int i = 0 ; i<mediaPlayers.length-2;i++)
            mediaPlayers[i].stop();
        int phrase = random.nextInt(phrases.length-2);
        mediaPlayers[phrase].play();
    }


    // Extra: These functions are fun menu items*/
    /** Episodes: Function connect the player to the 'Rick and Morty Episodes' online */
    public void Episodes() throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URL("https://www.adultswim.com/videos/rick-and-morty").toURI());
    }

    /** Plumbs: Function show the player the famous video 'Plumbs, How they do it' */
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


    /** update: Observer Design Pattern - update of the Observer Override here */
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