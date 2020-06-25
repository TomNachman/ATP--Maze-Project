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

    /** MyViewModel: Constructor - initiate the model */
    public MyViewModel(IModel model) {
        this.model = model;
    }

    /** generateMaze: Function generate a maze */
    public void generateMaze(int rows,int cols){
        model.generateMaze(rows, cols);
    }

    /** getMaze: Function return a array represent of the maze */
    public int[][] getMaze(){
        return model.getMaze();
    }

    /** getSolution: Function return the solution of the maze */
    public Solution getSolution() {
        return model.getMySolution();
    }

    /** MoveCharacter: Function try to move the character on the maze */
    public void MoveCharacter(KeyCode movement){
        model.MoveCharacter(movement);
    }

    /** loadMaze: Function load a maze to the maze */
    public void loadMaze(File file){ model.LoadMaze(file);}

    /** saveMaze: Function save the maze on the given file */
    public void saveMaze(File file){
        model.SaveMaze(file);
    }

    /** getCharacterRowPos: Function return the row position of the character */
    public int getCharacterRowPos() {
        return model.GetCharacterRowPos();
    }

    /** getCharacterRowPos: Function return the col position of the character */
    public int getCharacterColPos() {
        return model.GetCharacterColPos();
    }

    /** stopServers: Function stop the servers */
    public void stopServers(){
        model.stopServers();
    }

    /** getGoalPosition: Function return the goal position of the maze */
    public Position getGoalPosition(){
        return model.getGoalPosition();
    }

    /** setSearchAlgo: Function change try to change the search algorithm */
    public void setSearchAlgo(String str) {
        model.setSearchAlgo(str);
    }

    /** setGeneratingAlgo: Function change try to change the generate algorithm */
    public void setGeneratingAlgo(String str) {model.setGeneratingAlgo(str); }

    /** update: Observer Design Pattern - update of the Observer Override here */
    @Override
    public void update(Observable o, Object arg) {
        if(o==model) {
            setChanged();
            notifyObservers(arg);
        }
    }
}
