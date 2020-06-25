package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Configurations;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;

public class MyModel extends Observable implements IModel {

    private Server mazeGenerateServer;
    private Server solveMazeServer;
    private Maze myMaze;
    private int CharacterPosRow = 1;
    private int CharacterPosCol = 1;
    private Position Goal;
    private boolean finished = false;
    private Solution mySolution;
    private static final Logger LOG = LogManager.getLogger();

    /** StartServers: Function start the Servers generate the maze and solve it */
    public void StartServers() {
        mazeGenerateServer = new Server(5420, 1000, new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5421, 1000, new ServerStrategySolveSearchProblem());
        mazeGenerateServer.start();
        //Configurator.setRootLevel(Level.INFO);
        LOG.info("Maze-Generating server started");
        solveMazeServer.start();
        LOG.info("Solve-Maze server started");
    }

    /** stopServers: Function stop the Servers */
    public void stopServers() {
        mazeGenerateServer.stop();
        LOG.info("Maze-Generating server stopped");
        solveMazeServer.stop();
        LOG.info("Solve-Maze server stopped");
    }

    /** getMaze: Function return the array represent of the maze */
    @Override
    public int[][] getMaze() {
        return myMaze.getMazeArray();
    }

    /** isFinished: Function return if the player finished the game */
    public boolean isFinished() {
        return finished;
    }

    /** getMySolution: Function return the solution of the maze */
    public Solution getMySolution(){
        solveMaze();
        return mySolution;
    }

    /** solveMaze: Function solve the maze */
    @Override
    public void solveMaze(){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5421, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        Maze maze = myMaze;
                        toServer.writeObject(maze);
                        toServer.flush();
                        mySolution = (Solution) fromServer.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
            LOG.info("Server generated solution");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /** generateMaze: Function generate the maze */
    @Override
    public void generateMaze(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5420, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[width*height+20];
                        is.read(decompressedMaze);
                        myMaze = new Maze(decompressedMaze);
                        CharacterPosCol = myMaze.getStartPosition().getColumnIndex();
                        CharacterPosRow = myMaze.getStartPosition().getRowIndex();
                        Goal = myMaze.getGoalPosition();
                        notifyObservers("generated");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            LOG.info("client connected at: " + InetAddress.getLocalHost() + " ,port: " + 5400);
            client.communicateWithServer();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /** MoveCharacter: Function move the Character on the maze (if the movement is legal) */
    @Override
    public void MoveCharacter(KeyCode movement){
        boolean flag = true, flag2 = false;
        switch (movement.getName()){
            case "8":
            case "Up":
                if(CharacterPosRow-1>=0 &&!myMaze.isWall(CharacterPosRow-1,CharacterPosCol))
                    CharacterPosRow--;
                else {
                    flag = false;
                    flag2 = true;
                }
                break;

            case "2":
            case "Down":
                if (CharacterPosRow+1 < myMaze.getMazeArray().length && !myMaze.isWall(CharacterPosRow+1,CharacterPosCol))
                    CharacterPosRow++;
                else {
                    flag = false;
                    flag2 = true;
                }
                break;

            case "6":
            case "Right":
                if (CharacterPosCol+1 < myMaze.getMazeArray()[0].length && !myMaze.isWall(CharacterPosRow,CharacterPosCol+1))
                    CharacterPosCol++;
                else {
                    flag = false;
                    flag2 = true;
                }
                break;

            case "4":
            case "Left":
                if (CharacterPosCol-1 >=0 && !myMaze.isWall(CharacterPosRow,CharacterPosCol-1))
                    CharacterPosCol--;
                else {
                    flag = false;
                    flag2 = true;
                }
                break;

            case "9":
                if(CharacterPosRow-1>=0 && CharacterPosCol+1 < myMaze.getMazeArray()[0].length &&
                        !(myMaze.isWall(CharacterPosRow-1,CharacterPosCol+1)) &&
                        (!myMaze.isWall(CharacterPosRow-1, CharacterPosCol) || !myMaze.isWall(CharacterPosRow, CharacterPosCol+1)))
                {
                    CharacterPosCol++;
                    CharacterPosRow--;
                }
                else {
                    flag = false;
                    flag2 = true;
                }
                break;

            case "7":
                if(CharacterPosRow-1>=0 && CharacterPosCol-1>=0 &&!(myMaze.isWall(CharacterPosRow-1,CharacterPosCol-1)) &&
                    (!myMaze.isWall(CharacterPosRow-1, CharacterPosCol) || !myMaze.isWall(CharacterPosRow, CharacterPosCol-1)))
                {
                    CharacterPosCol--;
                    CharacterPosRow--;
                }
                else {
                    flag = false;
                    flag2 = true;
                }
                break;

            case "3":
                if(CharacterPosRow+1 < myMaze.getMazeArray().length && CharacterPosCol+1 < myMaze.getMazeArray()[0].length &&
                    !(myMaze.isWall(CharacterPosRow+1,CharacterPosCol+1)) &&
                    (!myMaze.isWall(CharacterPosRow+1, CharacterPosCol) || !myMaze.isWall(CharacterPosRow, CharacterPosCol+1)))
                {
                        CharacterPosCol++;
                        CharacterPosRow++;
                }
                else {
                    flag = false;
                    flag2 = true;
                }
                break;

            case "1":
                if(CharacterPosRow+1 < myMaze.getMazeArray().length && CharacterPosCol-1 >=0 &&
                    !(myMaze.isWall(CharacterPosRow+1,CharacterPosCol-1)) &&
                    (!myMaze.isWall(CharacterPosRow+1, CharacterPosCol) || !myMaze.isWall(CharacterPosRow, CharacterPosCol-1)))
                {
                        CharacterPosCol--;
                        CharacterPosRow++;
                }
                else {
                    flag = false;
                    flag2 = true;
                }
                break;
            default:
                flag = false;
                break;
        }
        setChanged();
        notifyObservers(flag? "moved" : flag2? "illegalMove" : "notMoved");

        if(CharacterPosCol==Goal.getColumnIndex() && CharacterPosRow==Goal.getRowIndex()){
            finished=true;
        }
    }

    /** GetCharacterRowPos: Function return the Row index position of the Character in the maze */
    @Override
    public int GetCharacterRowPos(){ return CharacterPosRow;}

    /** GetCharacterRowPos: Function return the Col index position of the Character in the maze */
    @Override
    public int GetCharacterColPos(){ return CharacterPosCol;}

    /** SaveMaze: Function save the maze */
    @Override
    public void SaveMaze(File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            ObjectOutputStream objectOutput = new ObjectOutputStream(out);
            objectOutput.writeObject(myMaze.toByteArray());
            objectOutput.flush();
            objectOutput.close();
        } catch (IOException var8) {
            var8.printStackTrace();
        }
    }

    /** LoadMaze: Function load a '.Maze' the maze */
    @Override
    public void LoadMaze(File file) {
        try {
            InputStream in = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(in);
            byte[] loadedMaze = (byte[])objectIn.readObject();
            objectIn.close();
            in.close();
            myMaze = new Maze(loadedMaze);
            this.CharacterPosRow = myMaze.getStartPosition().getRowIndex();
            this.CharacterPosCol = myMaze.getStartPosition().getColumnIndex();
            setChanged();
            notifyObservers("Loaded");
        } catch (IOException | ClassNotFoundException var7) {
            var7.printStackTrace();
        }
    }

    /** getStartPosition: Function return the start Position of the maze */
    @Override
    public Position getStartPosition() {
        return myMaze.getStartPosition();
    }

    /** getGoalPosition: Function return the goal Position of the maze */
    @Override
    public Position getGoalPosition() {
        return myMaze.getGoalPosition();
    }

    /** setSearchAlgo: Function change the configuration file according to input (search part) */
    public void setSearchAlgo(String str) {
        Configurations.SetSearchingAlgo(str);
    }

    /** setSearchAlgo: Function change the configuration file according to input (generate part) */
    @Override
    public void setGeneratingAlgo(String str) {
        Configurations.SetGeneratingAlgo(str);
        setChanged();
        notifyObservers("algorithmChanged");
    }

}