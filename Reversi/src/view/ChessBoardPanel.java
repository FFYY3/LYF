package view;

import components.ChessGridComponent;
import model.ChessPiece;

import javax.swing.*;
import java.awt.*;

public class ChessBoardPanel extends JPanel {

    private final int CHESS_COUNT = 8;
    private ChessGridComponent[][] chessGrids;



    public ChessBoardPanel(int width, int height) {
        this.setVisible(true);
        this.setFocusable(true);
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        int length = Math.min(width, height);
        this.setSize(length, length);
        ChessGridComponent.gridSize = length / CHESS_COUNT;
        ChessGridComponent.chessSize = (int) (ChessGridComponent.gridSize * 0.8);
        ChessGridComponent.blankSize = (int) (ChessGridComponent.chessSize * 0.5);

        System.out.printf("width = %d height = %d gridSize = %d chessSize = %d blankSize = %d\n",
                width, height, ChessGridComponent.gridSize, ChessGridComponent.chessSize, ChessGridComponent.blankSize);

        //绘制空棋盘
        chessGrids = new ChessGridComponent[CHESS_COUNT][CHESS_COUNT];
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                ChessGridComponent gridComponent = new ChessGridComponent(i, j);
                gridComponent.setLocation(j * ChessGridComponent.gridSize, i * ChessGridComponent.gridSize);
                chessGrids[i][j] = gridComponent;
                this.add(chessGrids[i][j]);
            }
        }

        initializeChessGrids();
        repaint();
    }



    //动作

    public void initializeChessGrids() {

        //放置四个棋子
        chessGrids[3][3].setChessPiece(ChessPiece.WHITE);
        chessGrids[3][4].setChessPiece(ChessPiece.BLACK);
        chessGrids[4][3].setChessPiece(ChessPiece.BLACK);
        chessGrids[4][4].setChessPiece(ChessPiece.WHITE);

        //计算标记点
        boolean unused;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                unused = isPossibleMove(i, j, ChessPiece.BLACK);
            }
        }
    }  //对全空棋盘使用
    public void clearChessGrids() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                chessGrids[i][j].setChessPiece(null);
                chessGrids[i][j].setIsPossibleMove(false);
                chessGrids[i][j].setCanBeClicked(false);
                chessGrids[i][j].setIsTheLastMove(false);
                chessGrids[i][j].setWillBeConverted(false);
            }
        }
    }  //清空棋盘：棋子和标记点
    public void clearChessGridsMarks() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                chessGrids[i][j].setIsPossibleMove(false);
                chessGrids[i][j].setIsTheLastMove(false);
                chessGrids[i][j].setCanBeClicked(false);
                chessGrids[i][j].setWillBeConverted(false);
            }
        }
    }  //清空标记点
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }


    //分析

    public boolean isPossibleMove(int row, int col, ChessPiece currentPlayer) {
        //todo: complete this method
        if(getChessPiece(row, col) == null) {

            int color;
            if(currentPlayer == ChessPiece.BLACK) {
                color = -1;
            }else{
                color = 1;
            }

            int[][] board = new int[8][8];
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    if(chessGrids[i][j].getChessPiece() == null) {
                        board[i][j] = 0;
                    }
                    else if(chessGrids[i][j].getChessPiece() == ChessPiece.BLACK) {
                        board[i][j] = -1;
                    }
                    else {
                        board[i][j] = 1;
                    }
                }
            }

            if (col < 6) {                   // Detect rightward.
                if (board[row][col + 1] + color == 0) {

                    int counter = 2;
                    while (board[row][col + counter] + color == 0) {
                        ++counter;
                        if (col + counter > 7) {
                            --counter;
                            break;
                        }
                    }
                    if (board[row][col + counter] == color) {
                        chessGrids[row][col].setIsPossibleMove(true);
                        return true;
                    }

                }
            }
            if (row < 6) {              // Detect downward.
                if (board[row + 1][col] + color == 0) {

                    int counter = 2;
                    while (board[row + counter][col] + color == 0) {
                        ++counter;
                        if (row + counter > 7) {
                            --counter;
                            break;
                        }
                    }
                    if (board[row+counter][col] == color) {
                        chessGrids[row][col].setIsPossibleMove(true);
                        return true;
                    }

                }
            }
            if (col > 1) {               // Detect leftward.
                if (board[row][col - 1] + color == 0) {

                    int counter = 2;
                    while (board[row][col-counter] + color == 0) {
                        ++counter;
                        if (col - counter < 0) {
                            --counter;
                            break;
                        }
                    }
                    if (board[row][col-counter] == color) {
                        chessGrids[row][col].setIsPossibleMove(true);
                        return true;
                    }

                }
            }
            if (row > 1) {              // Detect upward.
                if (board[row-1][col] + color == 0) {

                    int counter = 2;
                    while (board[row-counter][col] + color == 0) {
                        ++counter;
                        if (row - counter < 0) {
                            --counter;
                            break;
                        }
                    }
                    if (board[row-counter][col] == color) {
                        chessGrids[row][col].setIsPossibleMove(true);
                        return true;
                    }

                }
            }
            if (col < 6 && row > 1) {         // Detect upper-rightward.
                if (board[row-1][col+1] + color == 0) {

                    int counter = 2;
                    while (board[row-counter][col+counter] + color == 0) {
                        ++counter;
                        if (row - counter < 0 | col + counter > 7) {
                            --counter;
                            break;
                        }
                    }
                    if (board[row-counter][col+counter] == color) {
                        chessGrids[row][col].setIsPossibleMove(true);
                        return true;
                    }

                }
            }
            if (col < 6 && row < 6) {         // Detect lower-rightward.
                if (board[row + 1][col + 1] + color == 0) {

                    int counter = 2;
                    while (board[row + counter][col + counter] + color == 0) {
                        ++counter;
                        if (row + counter > 7 | col + counter > 7) {
                            --counter;
                            break;
                        }
                    }
                    if (board[row + counter][col + counter] == color) {
                        chessGrids[row][col].setIsPossibleMove(true);
                        return true;
                    }

                }
            }
            if (col > 1 && row < 6) {         // Detect lower-leftward.
                if (board[row + 1][col - 1] + color == 0) {

                    int counter = 2;
                    while (board[row + counter][col - counter] + color == 0) {
                        ++counter;
                        if (row + counter > 7 | col - counter < 0) {
                            --counter;
                            break;
                        }
                    }
                    if (board[row + counter][col - counter] == color) {
                        chessGrids[row][col].setIsPossibleMove(true);
                        return true;
                    }

                }
            }
            if (col > 1 && row > 1) {         // Detect upper-leftward.
                if (board[row - 1][col - 1] + color == 0) {

                    int counter = 2;
                    while (board[row - counter][col - counter] + color == 0) {
                        ++counter;
                        if (row - counter < 0 | col - counter < 0) {
                            --counter;
                            break;
                        }
                    }
                    if (board[row - counter][col - counter] == color) {
                        chessGrids[row][col].setIsPossibleMove(true);
                        return true;
                    }

                }
            }
        }
        chessGrids[row][col].setIsPossibleMove(false);
        return false;
    }  //判断该棋格是否为合法棋格，并设置该棋格的对应变量:isPossibleMove
    public boolean[][] willBeConvertedChessGrids(int row, int col, ChessPiece currentPlayer) {

        boolean[][] result = new boolean[8][8];

        int color;
        if(currentPlayer == ChessPiece.BLACK) {
            color = -1;
        }else{
            color = 1;
        }

        int[][] board = new int[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(chessGrids[i][j].getChessPiece() == null) {
                    board[i][j] = 0;
                }
                else if(chessGrids[i][j].getChessPiece() == ChessPiece.BLACK) {
                    board[i][j] = -1;
                }
                else {
                    board[i][j] = 1;
                }
            }
        }

        if (col < 6) {                   // Detect rightward.
            if (board[row][col + 1] + color == 0) {

                int counter = 2;
                while (board[row][col + counter] + color == 0) {
                    ++counter;
                    if (col + counter > 7) {
                        --counter;
                        break;
                    }
                }
                if (board[row][col + counter] == color) {
                    for(int j=1; j<counter; ++j){
                        result[row][col+j] = true;
                    }
                }

            }
        }
        if (row < 6) {              // Detect downward.
            if (board[row + 1][col] + color == 0) {

                int counter = 2;
                while (board[row + counter][col] + color == 0) {
                    ++counter;
                    if (row + counter > 7) {
                        --counter;
                        break;
                    }
                }
                if (board[row+counter][col] == color) {
                    for(int j=1; j<counter; ++j){
                        result[row+j][col] = true;
                    }
                }

            }
        }
        if (col > 1) {               // Detect leftward.
            if (board[row][col - 1] + color == 0) {

                int counter = 2;
                while (board[row][col-counter] + color == 0) {
                    ++counter;
                    if (col - counter < 0) {
                        --counter;
                        break;
                    }
                }
                if (board[row][col-counter] == color) {
                    for(int j=1; j<counter; ++j){
                        result[row][col-j] = true;
                    }
                }

            }
        }
        if (row > 1) {              // Detect upward.
            if (board[row-1][col] + color == 0) {

                int counter = 2;
                while (board[row-counter][col] + color == 0) {
                    ++counter;
                    if (row - counter < 0) {
                        --counter;
                        break;
                    }
                }
                if (board[row-counter][col] == color) {
                    for(int j=1; j<counter; ++j){
                        result[row-j][col] = true;
                    }
                }

            }
        }
        if (col < 6 & row > 1) {         // Detect upper-rightward.
            if (board[row-1][col+1] + color == 0) {

                int counter = 2;
                while (board[row-counter][col+counter] + color == 0) {
                    ++counter;
                    if (row - counter < 0 | col + counter > 7) {
                        --counter;
                        break;
                    }
                }
                if (board[row-counter][col+counter] == color) {
                    for(int j=1; j<counter; ++j){
                        result[row-j][col+j] = true;
                    }
                }

            }
        }
        if (col < 6 & row < 6) {         // Detect lower-rightward.
            if (board[row + 1][col + 1] + color == 0) {

                int counter = 2;
                while (board[row + counter][col + counter] + color == 0) {
                    ++counter;
                    if (row + counter > 7 | col + counter > 7) {
                        --counter;
                        break;
                    }
                }
                if (board[row + counter][col + counter] == color) {
                    for(int j=1; j<counter; ++j){
                        result[row+j][col+j] = true;
                    }
                }

            }
        }
        if (col > 1 & row < 6) {         // Detect lower-leftward.
            if (board[row + 1][col - 1] + color == 0) {

                int counter = 2;
                while (board[row + counter][col - counter] + color == 0) {
                    ++counter;
                    if (row + counter > 7 | col - counter < 0) {
                        --counter;
                        break;
                    }
                }
                if (board[row + counter][col - counter] == color) {
                    for(int j=1; j<counter; ++j){
                        result[row+j][col-j] = true;
                    }
                }

            }
        }
        if (col > 1 & row > 1) {         // Detect upper-leftward.
            if (board[row - 1][col - 1] + color == 0) {

                int counter = 2;
                while (board[row - counter][col - counter] + color == 0) {
                    ++counter;
                    if (row - counter < 0 | col - counter < 0) {
                        --counter;
                        break;
                    }
                }
                if (board[row - counter][col - counter] == color) {
                    for(int j=1; j<counter; ++j){
                        result[row-j][col-j] = true;
                    }
                }

            }
        }

        return result;
    }  //条件：该棋格为合法棋格
    public void markIsPossibleMoveChessGrids() {
        boolean unused;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                unused = isPossibleMove(i, j, GameFrame.controller.getCurrentPlayer());
            }
        }
    }  //条件：对没有被标记过的棋盘使用


    //读取和设置数据

    public ChessGridComponent[][] getChessGrids() {
        //
        return chessGrids;
    }
    public ChessPiece getChessPiece(int row, int col) {
        //
        return chessGrids[row][col].getChessPiece();
    }
    public void setChessGrids(ChessGridComponent[][] chessGrids) {
        //
        this.chessGrids = chessGrids;
    }


}
