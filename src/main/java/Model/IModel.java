package Model;

import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;

public interface IModel {
    int[][] getMaze();
    void generateMaze(int width, int height);
    int GetCharacterRowPos();
    int GetCharacterColPos();
    void MoveCharacter(KeyCode movement);
    boolean isFinished();
    void solveMaze();
    void SaveMaze(File file);
    void LoadMaze(File file);
    void stopServers();
    Solution getMySolution();
    Position getStartPosition();
    Position getGoalPosition();
}
