package View;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MazeDisplayer extends Canvas {

    private int [][] maze;

    public void setMaze(int[][] maze) {
        this.maze = maze;
        draw();
    }

    public void draw()
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