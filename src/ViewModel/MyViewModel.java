package ViewModel;

import Model.IModel;

public class MyViewModel {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
    }
    public void generatrMaze(int rows,int cols){
        model.generateMaze(rows, cols);
    }
    public int[][] getMaze(){return model.getMaze();}
}
