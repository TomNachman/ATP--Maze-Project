package View;

import ViewModel.MyViewModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Random;

public class MyViewController implements IView {

    public MazeDisplayer mazeDisplayer;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Label labelCatch;
    public ImageView finishImage;
    private MyViewModel viewModel = sample.Main.vm;
    private boolean mazeExists=false;
    private boolean boolPhrase = false;
    private String [] phrases = new String[13];
    private Media [] sounds = new Media[13];
    private MediaPlayer [] mediaPlayers = new MediaPlayer[13];


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void diplayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
    }
    public void GenerateMaze(){
        try{
            int rows = Integer.parseInt(textField_mazeRows.getText());
            int cols = Integer.parseInt(textField_mazeColumns.getText());
            if(rows<2 || cols<2) {
                showAlert("please enter valid rows and cols \n rows: above 2 \n cols: above 2");
            }
            else{

                // Music
                if(!mazeExists){
                    insertMedia("Evil Morty-Remix", sounds.length-2);
                    insertMedia("Theme Song",sounds.length-1);
                }

                mediaPlayers[sounds.length-1].stop();
                mediaPlayers[sounds.length-2].setAutoPlay(true);
                mediaPlayers[sounds.length-2].setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayers[sounds.length-2].play();


                // Finish Image
                finishImage.setVisible(false);

                // Logic and View
                viewModel.generatrMaze(rows,cols);
                mazeExists = true;
                this.diplayMaze(viewModel.getMaze());
            }
        }catch (NumberFormatException ex) {
            showAlert("Please enter Integers Only Bitch");
        }
    }
    public void SolveMaze()
    {
        if (!mazeExists)
            showAlert("Please generate a maze first");
        else {
            showAlert("Solving Maze ... ");
        }
    }
    public void showAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Invalid Input");
        alert.setContentText(message);
        alert.show();
    }

    public void catchPhrase(){
        if(!boolPhrase)
        {
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
        }

        Random random = new Random();
        int phrase = random.nextInt(phrases.length-2);
        labelCatch.setText(String.format("\"%s\"",phrases[phrase]));
        //System.out.println(phrase);
        mediaPlayers[phrase].play();
    }

    public void keyPressed(javafx.scene.input.KeyEvent keyEvent) {
        switch(keyEvent.getCode()){
            case F1: GenerateMaze(); break;
            case CONTROL: mazeDisplayer.Zoom();
        }
    }

    public void finishMaze(ActionEvent e) {
        finishImage.setVisible(true);
        mediaPlayers[mediaPlayers.length-2].stop();
        mediaPlayers[sounds.length-1].setAutoPlay(true);
        mediaPlayers[sounds.length-1].setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayers[sounds.length-1].play();

    }

    private void insertMedia(String name, int index)
    {
        File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/resources/CatchPhrases/" + name + ".mp3");
        String path = file.toURI().toASCIIString();
        sounds[index] = new Media(path);
        mediaPlayers[index] = new MediaPlayer(sounds[index]);
    }

}