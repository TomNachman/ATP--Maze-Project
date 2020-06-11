package ViewModel;

import Model.IModel;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;

public class MyViewModel {
    private IModel model;
    public int x;

    public MyViewModel(IModel model) {
        this.model = model;
    }

    public void generatrMaze(int rows,int cols){
        model.generateMaze(rows, cols);
    }

    public int[][] getMaze(){
        return model.getMaze();
    }

    public Solution getSolution() {
        return model.getMySolution();
    }

    public void MoveCharacter(KeyCode movement){
        model.MoveCharacter(movement);
    }

    public void loadMaze(File file){
        model.LoadMaze(file);
    }

    public void saveMaze(File file){
        model.SaveMaze(file);
    }

    public boolean isFinished() {
        return model.isFinished();
    }

    public int getCharacterRowPos() {
        return model.GetCharacterRowPos();
    }

    public int getCharacterColPos() {
        return model.GetCharacterColPos();
    }

    public  void stopServers(){
        model.stopServers();
    }
}
