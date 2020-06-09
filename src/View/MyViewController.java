package View;

import ViewModel.MyViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.awt.event.KeyEvent;
import java.util.Random;

public class MyViewController implements IView {

    public MazeDisplayer mazeDisplayer;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Label labelCatch;
    private MyViewModel viewModel = sample.Main.vm;
    private boolean mazeExists=false;
    private boolean boolPhrase = false;
    private String [] phrases = new String[20];

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
        if(!boolPhrase){
            phrases[0] = "go Fuck";
            phrases[1] = "Shut Up Morty";
            phrases[2] = "We Could Have Die Rick";
            phrases[3] = "wubba lubba dub dub";
            phrases[4] = "Hit the sack Jack!";
            phrases[5] = "And that's why I always say, Shumshumschilpiddydah!";
            phrases[6] = "Rubber baby burger bumpers";
            phrases[7] = "Shut the fuck up, Morty";
            phrases[8] = "And that's the waaaaaaay the news goes!";
            phrases[9] = "And that's why I always say, shum-shum-schlippety-dop!";
            phrases[10] = "Grassssss....... tastes bad.";
            phrases[11] = "No jumpin' in the sewer!";

        }
        Random random = new Random();
        labelCatch.setText(String.format("\"%s\"",phrases[random.nextInt(12)] ));
    }

    public void keyPressed(javafx.scene.input.KeyEvent keyEvent) {
        switch(keyEvent.getCode()){
            case F1: GenerateMaze(); break;
            case CONTROL: mazeDisplayer.Zoom();
        }
    }
}
