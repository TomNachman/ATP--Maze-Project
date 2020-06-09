package Model;

import IO.MyDecompressorInputStream;
import Server.Server;
import Client.Client;
import Client.IClientStrategy;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyModel implements IModel {

    private Server mazeGenerateServer;
    private Server solveMazeServer;
    private Maze myMaze;

    public void StartServers() {
        mazeGenerateServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGenerateServer.start();
        solveMazeServer.start();
    }
    public void stopServers() {
        mazeGenerateServer.stop();
        solveMazeServer.stop();
    }

    @Override
    public int[][] getMaze() {
        return myMaze.getMazeArray();
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
                        byte[] decompressedMaze = new byte[width*height+20 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/];
                        is.read(decompressedMaze);
                        myMaze = new Maze(decompressedMaze);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}
