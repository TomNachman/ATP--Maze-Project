package View;

import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class MazeDisplayer extends Canvas {

    private int [][] maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private Solution sol;
    private Position startPosition;
    private Position goalPosition;
    private Image characterImage;

    public void setMaze(int[][] maze) {
        this.maze = maze;
        drawMaze();
    }

    public void setCharactersPos(Position startPos, Position goalPos) {
        this.startPosition = startPos;
        this.goalPosition = goalPos;
    }

    public void setCharacterImage(Image image){
        this.characterImage = image;
    }

    public void drawMaze() {
        if( maze!=null)
        {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int row = maze.length;
            int col = maze[0].length;
            double cellHeight = canvasHeight/row;
            double cellWidth = canvasWidth/col;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
            graphicsContext.setFill(Color.BLACK);
            double x,y;

            //Draw Maze
            for(int i=0;i<row;i++) {
                for(int j=0;j<col;j++) {
                    y = i * cellHeight;
                    x = j * cellWidth;
                    if(maze[i][j] == 1) // Wall
                        graphicsContext.fillRect(x,y,cellHeight,cellWidth);
                    if(i == this.startPosition.getRowIndex() && j == this.startPosition.getColumnIndex()){
                        graphicsContext.drawImage(this.characterImage, x,y,cellHeight,cellWidth);
                    }

                    if(i == this.goalPosition.getRowIndex() && j == this.goalPosition.getColumnIndex()){
                        System.out.println(System.getProperty("user.dir"));
                        Image image = new Image("file:" + System.getProperty("user.dir").replace("\\", "/") + "/resources/Images/portal.png");
                        graphicsContext.drawImage(image, x,y,cellHeight,cellWidth);
                    }
                }
            }
        }
    }

    public void setSolution(Solution sol){
        this.sol=sol;
        drawSolution();
    }

    public void drawSolution(){
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight/maze.length;
        double cellWidth = canvasWidth/maze[0].length;
        ArrayList<AState> solArray = this.sol.getSolutionPath();
        for(int i=1; i<solArray.size()-1; i++) {
            Position state = solArray.get(i).getPosition();
            int rowState = state.getRowIndex();
            int colState = state.getColumnIndex();
            GraphicsContext gc = getGraphicsContext2D();
            gc.setFill(Color.RED);
            gc.fillRect(colState * cellWidth, rowState * cellHeight, cellWidth, cellHeight);
        }
    }

    public void print() {
        for(int i = 0; i < this.maze.length; ++i) {
            for(int j = 0; j < this.maze[0].length; ++j) {
                if (i == this.startPosition.getRowIndex() && j == this.startPosition.getColumnIndex()) {
                    System.out.print('S');
                } else if (i == this.goalPosition.getRowIndex() && j == this.goalPosition.getColumnIndex()) {
                    System.out.print('E');
                } else {
                    System.out.print(this.maze[i][j] + "");
                }

                if (j == this.maze[0].length - 1) {
                    System.out.println("");
                }
            }
        }
    }

    public void Zoom(){
        setOnScroll(event -> {
            if(event.isControlDown()) {
                double change = event.getDeltaY();
                double zoomConst = 1.03;
                if (change < 0) {
                    zoomConst = 0.97;
                }

                setScaleY(getScaleY() * zoomConst);
                setScaleX(getScaleX() * zoomConst);
                event.consume();
            }
        });
    }
}