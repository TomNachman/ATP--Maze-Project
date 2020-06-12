package View;

import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class MazeDisplayer extends Canvas {

    private int [][] maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private Solution sol;

    public void setMaze(int[][] maze) {
        this.maze = maze;
        Mazedraw();
    }
    public void setSolution(Solution sol){
        this.sol=sol;
        SolutionDraw();
    }

    public void Mazedraw()
    {
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
            for(int i=0;i<row;i++)
            {
                for(int j=0;j<col;j++)
                {
                    if(maze[i][j] == 1) // Wall
                    {
                        x = i *cellHeight;
                        y = j *  cellWidth;
                        graphicsContext.fillRect(x,y,cellHeight,cellWidth);
                    }
                }
            }

        }
    }
    public void SolutionDraw(){
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight/maze.length;
        double cellWidth = canvasWidth/maze[0].length;
        ArrayList<AState> solArray = this.sol.getSolutionPath();
        for(int i=0; i<solArray.size(); i++) {
            Position state = solArray.get(i).getPosition();
            int rowState = state.getRowIndex();
            int colState = state.getColumnIndex();
            GraphicsContext gc = getGraphicsContext2D();
            gc.setFill(Color.RED);
            gc.fillRect(colState * cellWidth, rowState * cellHeight, cellWidth, cellHeight);
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