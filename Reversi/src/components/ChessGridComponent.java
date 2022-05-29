package components;

import model.*;
import view.GameFrame;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class ChessGridComponent extends BasicComponent {

    public static int chessSize;
    public static int gridSize;
    public static int blankSize;

    public static Color gridColor1 = new Color(243, 172, 95);
    public static Color gridColor2 = new Color(203, 139, 78);
    public static Color possibleMovesColor = new Color(125, 125, 125);
    public static Color canBeClickedColor = new Color(204, 156, 119);
    public static Color lastMoveColor = new Color(255, 5, 5);
    public static Color willBeConvertedColor = new Color(210, 210, 210);

    private ChessPiece chessPiece;

    private int row;
    private int col;

    private boolean isPossibleMove;
    private boolean isTheLastMove;
    private boolean canBeClicked;
    private boolean willBeConverted;



    public ChessGridComponent(int row, int col) {
        this.setSize(gridSize, gridSize);

        this.row = row;
        this.col = col;

        isPossibleMove = false;
        isTheLastMove = false;
        canBeClicked = false;
        willBeConverted = false;

    }



    //响应

    @Override
    public void onMouseEntered() {
        //System.out.printf("%s entered (%d, %d)\n", GameFrame.controller.getCurrentPlayer(), row, col);

        //是否可点击
        if(GameFrame.controller.canClick(row, col)) {
            canBeClicked = true;
        }

        //是否是合法棋格
        if(GameFrame.controller.isPossibleMove(row, col)) {
            boolean[][] willBeConvertedChessPieces = GameFrame.controller.willBeConvertedChessGrids(row, col);
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    GameFrame.controller.getChessBoardPanel().getChessGrids()[i][j].willBeConverted = willBeConvertedChessPieces[i][j];
                }
            }
        }

        GameFrame.controller.getChessBoardPanel().repaint();
    }

    @Override
    public void onMouseClicked() {
        System.out.printf("%s clicked (%d, %d)\n", GameFrame.controller.getCurrentPlayer(), row, col);
        //todo: complete mouse click method
        be1 = true; ////

        //是否可点击：即是否可以在此处下棋
        if (GameFrame.controller.canClick(row, col)) {

            GameFrame.controller.playerPlaysAStepAndRefresh(row, col);
            GameFrame.controller.getChessBoardPanel().repaint();

            GameFrame.controller.check();

            if(!be2_gameEnded) {
                GameFrame.controller.cache();
                System.out.printf("Cached step %d\n", GameFrame.controller.getStep());
                System.out.printf("Cached totally %d steps(includes step 0)\n", GameFrame.controller.getGameCache().size());

                //PVC模式
                if(GameFrame.controller.getGameMode() != 0 && be1) {

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {

                            GameFrame.controller.computerPlaysAStepAndRefresh();
                            GameFrame.controller.getChessBoardPanel().repaint();

                            GameFrame.controller.check();

                            GameFrame.controller.cache();
                            System.out.printf("Cached step %d\n", GameFrame.controller.getStep());
                            System.out.printf("Cached totally %d steps(includes step 0)\n", GameFrame.controller.getGameCache().size());

                            if(ChessGridComponent.be2_gameEnded) {
                                GameFrame.controller.restart();
                            }
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 360);
                }

            }else{
                GameFrame.controller.restart();
            }

        }
    }

    @Override
    public void onMouseExited() {
        //System.out.printf("%s exited (%d, %d)\n", GameFrame.controller.getCurrentPlayer(), row, col);
        setCanBeClicked(false);
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                GameFrame.controller.getChessBoardPanel().getChessGrids()[i][j].setWillBeConverted(false);
                GameFrame.controller.getChessBoardPanel().getChessGrids()[i][j].repaint();
            }
        }
    }


    //动作

    public void paintComponent(Graphics g) {
        super.printComponents(g);
        drawChessGridComponent(g);
    }
    ////子方法
    public void drawChessGridComponent(Graphics g) {

        if (row % 2 == 0){
            if (col % 2 == 0){
                g.setColor(gridColor2);
            }else{
                g.setColor(gridColor1);
            }
        }else{
            if (col % 2 == 0){
                g.setColor(gridColor1);
            }else{
                g.setColor(gridColor2);
            }
        }

        g.fillRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
        Graphics2D g2 = (Graphics2D) g;

        if (this.chessPiece != null) {
            g.setColor(chessPiece.getColor());
            g.fillOval((gridSize - chessSize) / 2, (gridSize - chessSize) / 2, chessSize, chessSize);
        }
        if (this.willBeConverted){
            g2.setColor(willBeConvertedColor);
            g2.setStroke(new BasicStroke(3.5F));
            g2.drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
        }
        if (this.canBeClicked){
            g2.setColor(canBeClickedColor);
            g2.fillRect(0,0,this.getWidth()-1,this.getHeight()-1);
        }
        if (this.isPossibleMove){
            g2.setColor(possibleMovesColor);
            if (this.canBeClicked){
                g2.setColor(new Color(180, 171, 171));
            }
            g.fillOval((gridSize - blankSize) / 2, (gridSize - blankSize) / 2, blankSize, blankSize);
        }
        if (this.isTheLastMove){
            g.setColor(lastMoveColor);
            g.fillOval((gridSize- (int)(chessSize*0.2))/2,(gridSize-(int)(chessSize*0.2))/2,(int)(chessSize*0.2),(int)(chessSize*0.2));
        }

    }


    //读取和设置数据

    public int getRow() {
        //
        return row;
    }
    public int getCol() {
        //
        return col;
    }
    public ChessPiece getChessPiece() {
        //
        return chessPiece;
    }
    public boolean getIsPossibleMove() {
        //
        return isPossibleMove;
    }
    public boolean getIsTheLastMove() {
        //
        return isTheLastMove;
    }
    public boolean getWillBeConverted() {
        //
        return willBeConverted;
    }
    public void setChessPiece(ChessPiece chessPiece) {
        //
        this.chessPiece = chessPiece;
    }  //放置棋子
    public void setIsPossibleMove(boolean toDo) {
        if(toDo) {
            isPossibleMove = true;
        }else{
            isPossibleMove = false;
        }
    }
    public void setIsTheLastMove(boolean toDo) {
        if(toDo) {
            isTheLastMove = true;
        }else{
            isTheLastMove = false;
        }
    }
    public void setCanBeClicked(boolean toDo) {
        if(toDo) {
            canBeClicked = true;
        }else{
            canBeClicked = false;
        }
    }
    public void setWillBeConverted(boolean toDo) {
        if(toDo) {
            willBeConverted = true;
        }else{
            willBeConverted = false;
        }
    }





    //补丁
    public static boolean be1 = true;
    public static boolean be2_gameEnded = false;

}
