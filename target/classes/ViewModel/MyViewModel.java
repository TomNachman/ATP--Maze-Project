package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
    }

    public void generateMaze(int rows,int cols){
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

    public void loadMaze(File file){ model.LoadMaze(file);}

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

    public void stopServers(){
        model.stopServers();
    }

    public Position getStartPosition(){
        return model.getStartPosition();
    }

    public Position getGoalPosition(){
        return model.getGoalPosition();
    }

    public void setSearchAlgo(String str) {
        model.setSearchAlgo(str);
    }

    public void setGeneratingAlgo(String str) {model.setGeneratingAlgo(str); }


    @Override
    public void update(Observable o, Object arg) {
        if(o==model) {
            setChanged();
            notifyObservers(arg);
        }
    }
}
