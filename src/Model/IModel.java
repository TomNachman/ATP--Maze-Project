package Model;

import javafx.scene.input.KeyCode;

public interface IModel {
    int[][] getMaze();
    void generateMaze(int width, int height);
    int GetCharacterRowPos();
    int GetCharacterColPos();
    void MoveCharacter(KeyCode movement);
}
