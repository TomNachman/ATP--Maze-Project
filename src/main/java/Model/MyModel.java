package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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

    public void StartServers() {
        mazeGenerateServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGenerateServer.start();
        //Configurator.setRootLevel(Level.INFO);
        LOG.info("Maze-Generating server started");
        solveMazeServer.start();
        LOG.info("Solve-Maze server started");


    }

    public void stopServers() {
        mazeGenerateServer.stop();
        LOG.info("Maze-Generating server stopped");
        solveMazeServer.stop();
        LOG.info("Solve-Maze server stopped");
    }

    @Override
    public int[][] getMaze() {
        return myMaze.getMazeArray();
    }

    public boolean isFinished() {
        return finished;
    }

    public Solution getMySolution(){
        solveMaze();
        return mySolution;
    }

    @Override
    public void solveMaze(){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
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

    @Override
    public void generateMaze(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
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
                        byte[] decompressedMaze = new byte[width*height+20 /**CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/];
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

    @Override
    public void MoveCharacter(KeyCode movement){
        boolean flag = true;
        switch (movement.getName()){
            case "8":
            case "Up":
                if(CharacterPosRow-1>=0){
                    if(!myMaze.isWall(CharacterPosRow-1,CharacterPosCol)) {
                        CharacterPosRow--;
                    }
                    else MusicFail();
                }
                else MusicFail();
                break;

            case "2":
            case "Down":
                if (CharacterPosRow+1 < myMaze.getMazeArray().length){
                    if(!myMaze.isWall(CharacterPosRow+1,CharacterPosCol)) CharacterPosRow++;
                    else MusicFail();
                }
                else MusicFail();
                break;

            case "6":
            case "Right":
                if (CharacterPosCol+1 < myMaze.getMazeArray()[0].length){
                    if(!myMaze.isWall(CharacterPosRow,CharacterPosCol+1)) CharacterPosCol++;
                    else MusicFail();
                }
                else MusicFail();
                break;

            case "4":
            case "Left":
                if (CharacterPosCol-1 >=0){
                    if(!myMaze.isWall(CharacterPosRow,CharacterPosCol-1)) CharacterPosCol--;
                    else MusicFail();
                }
                else MusicFail();
                break;

            case "9":
                if(CharacterPosRow-1>=0 && CharacterPosCol+1 < myMaze.getMazeArray()[0].length){
                    if(!(myMaze.isWall(CharacterPosRow-1,CharacterPosCol+1))){
                        CharacterPosCol++;
                        CharacterPosRow--;
                    }
                    else MusicFail();
                }
                else MusicFail();
                break;

            case "7":
                if(CharacterPosRow-1>=0 && CharacterPosCol-1>=0){
                    if(!(myMaze.isWall(CharacterPosRow-1,CharacterPosCol-1))){
                        CharacterPosCol--;
                        CharacterPosRow--;
                    }
                    else MusicFail();
                }
                else MusicFail();
                break;

            case "3":
                if(CharacterPosRow+1 < myMaze.getMazeArray().length && CharacterPosCol+1 < myMaze.getMazeArray()[0].length){
                    if(!(myMaze.isWall(CharacterPosRow+1,CharacterPosCol+1))){
                        CharacterPosCol++;
                        CharacterPosRow++;
                    }
                    else MusicFail();
                }
                else MusicFail();
                break;

            case "1":
                if(CharacterPosRow+1 < myMaze.getMazeArray().length && CharacterPosCol-1 >=0){
                    if(!(myMaze.isWall(CharacterPosRow+1,CharacterPosCol-1))){
                        CharacterPosCol--;
                        CharacterPosRow++;
                    }
                    else MusicFail();
                }
                else MusicFail();
                break;
            default:
                flag = false;
                break;
        }
        setChanged();
        notifyObservers(flag? "moved":"notMoved");
        if(CharacterPosCol==Goal.getColumnIndex() && CharacterPosRow==Goal.getRowIndex()){
            finished=true;
        }
    }

    @Override
    public int GetCharacterRowPos(){ return CharacterPosRow;}

    @Override
    public int GetCharacterColPos(){ return CharacterPosCol;}

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

    @Override
    public void LoadMaze(File file) {
        try {
            InputStream in = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(in);
            byte[] loadedMaze = (byte[])objectIn.readObject();
            objectIn.close();
            in.close();
            myMaze = new Maze(loadedMaze);
        } catch (IOException | ClassNotFoundException var7) {
            var7.printStackTrace();
        }
    }

    @Override
    public Position getStartPosition() {
        return myMaze.getStartPosition();
    }

    @Override
    public Position getGoalPosition() {
        return myMaze.getGoalPosition();
    }

    public void MusicFail(){
        //TODO: replace loser with fail sound
        File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/resources/CatchPhrases/Loser.mp3");
        String path = file.toURI().toASCIIString();
        Media musicFile = new Media(path);
        MediaPlayer mediaplayerBackground = new MediaPlayer(musicFile);
        mediaplayerBackground.setVolume(0.3);
        mediaplayerBackground.play();
    }
}