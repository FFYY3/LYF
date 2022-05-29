package methods;

import components.ChessGridComponent;

public abstract class Algorithm {

    public Algorithm() {}


    //下棋位置
    public abstract int[] playAStepAt(ChessGridComponent[][] chessGrids);
}
