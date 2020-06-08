package View;

import ViewModel.MyViewModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class MyViewController implements IView {
    public MazeDisplayer mazeDisplayer;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    private MyViewModel viewModel;
    private boolean mazeExists=false;

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
            if(rows<0 || cols<0) {
                showAlert("please enter positive parameters");
            }
            else{
                viewModel.generatrMaze(rows,cols);
                mazeExists = true;
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
        alert.setContentText(message);
        alert.show();
    }
}
