package View;

import ViewModel.MyViewModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class MyViewController implements IView {
    public MazeDisplayer mazeDisplayer;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    private MyViewModel viewModel = sample.Main.vm;
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
}
