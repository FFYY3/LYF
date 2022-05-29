package methods;

import components.ChessGridComponent;
import model.ChessPiece;
import view.GameFrame;

import java.util.ArrayList;
import java.util.List;

public class EasyAlgorithm extends Algorithm {

    @Override
    public int[] playAStepAt(ChessGridComponent[][] chessGrids) {

        int[] result = new int[2];
        double[] scoreReportOfResult = new double[6];

        //得到所有 possible moves
        List<Integer[]> positions = new ArrayList<>(0);
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(chessGrids[i][j].getIsPossibleMove()) {
                    Integer[] position = {i, j};
                    positions.add(position);
                }
            }
        }

        //计算算法所需参数
        double whitePiecesAmount = 0; //白棋数
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(GameFrame.controller.getChessBoardPanel().getChessGrids()[i][j].getChessPiece() == ChessPiece.WHITE) {
                    whitePiecesAmount++;
                }
            }
        }
        double blackPiecesAmount = 0; //黑棋数
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(GameFrame.controller.getChessBoardPanel().getChessGrids()[i][j].getChessPiece() == ChessPiece.BLACK) {
                    blackPiecesAmount++;
                }
            }
        }
        double step = GameFrame.controller.getStep(); //步数


        //选出返回坐标值
        double maxScore = 0;
        for(Integer[] position : positions) {

            final int r = position[0];
            final int c = position[1];
            double score = 0;


            //计算算法所需与当前位置有关的参数

            double sameLineScore = 0; //同行列向分数
            if(r < 7) {
                int counter = 1;
                while(r + counter <= 7 &&
                        GameFrame.controller.getChessBoardPanel().getChessGrids()[r + counter][c].getChessPiece() == ChessPiece.WHITE) {
                    counter++;
                }
                int current = r + counter; ////

                if(current == 8) {
                    //抵到边了
                    counter--;
                    sameLineScore += counter * 4;
                }
                else if(GameFrame.controller.getChessBoardPanel().getChessGrids()[current][c].getChessPiece() == ChessPiece.BLACK) {
                    //被包夹了
                    sameLineScore -= counter * 3;
                }
                else if(GameFrame.controller.getChessBoardPanel().getChessGrids()[current][c].getChessPiece() == null) {
                    //空边，较安全
                    sameLineScore += counter * 2;
                }
            }
            if(r > 0) {
                int counter = 1;
                while(r - counter >= 0 &&
                        GameFrame.controller.getChessBoardPanel().getChessGrids()[r - counter][c].getChessPiece() == ChessPiece.WHITE) {
                    counter++;
                }
                int current = r - counter; ////

                if(current == 8) {
                    //抵到边了
                    counter--;
                    sameLineScore += counter * 4;
                }
                else if(GameFrame.controller.getChessBoardPanel().getChessGrids()[current][c].getChessPiece() == ChessPiece.BLACK) {
                    //被包夹了
                    sameLineScore -= counter * 3;
                }
                else if(GameFrame.controller.getChessBoardPanel().getChessGrids()[current][c].getChessPiece() == null) {
                    //空边，较安全
                    sameLineScore += counter * 2;
                }
            }
            if(c > 0) {
                int counter = 1;
                while(c - counter >= 0 &&
                        GameFrame.controller.getChessBoardPanel().getChessGrids()[r][c - counter].getChessPiece() == ChessPiece.WHITE) {
                    counter++;
                }
                int current = c - counter; ////

                if(current == 8) {
                    //抵到边了
                    counter--;
                    sameLineScore += counter * 4;
                }
                else if(GameFrame.controller.getChessBoardPanel().getChessGrids()[r][current].getChessPiece() == ChessPiece.BLACK) {
                    //被包夹了
                    sameLineScore -= counter * 3;
                }
                else if(GameFrame.controller.getChessBoardPanel().getChessGrids()[r][current].getChessPiece() == null) {
                    //空边，较安全
                    sameLineScore += counter * 2;
                }
            }
            if(c < 7) {
                int counter = 1;
                while(c + counter <= 7 &&
                        GameFrame.controller.getChessBoardPanel().getChessGrids()[r][c + counter].getChessPiece() == ChessPiece.WHITE) {
                    counter++;
                }
                int current = c + counter; ////

                if(current == 8) {
                    //抵到边了
                    counter--;
                    sameLineScore += counter * 4;
                }
                else if(GameFrame.controller.getChessBoardPanel().getChessGrids()[r][current].getChessPiece() == ChessPiece.BLACK) {
                    //被包夹了
                    sameLineScore -= counter * 3;
                }
                else if(GameFrame.controller.getChessBoardPanel().getChessGrids()[r][current].getChessPiece() == null) {
                    //空边，较安全
                    sameLineScore += counter * 2;
                }
            }

            double occupySideScore = 0; //占边的分数
            if(r == 0 || r == 7 || c == 0 || c == 7) {
                occupySideScore = 4;
            }

            //double sameDiagonal = 0; //同斜向分数


            // 1.能够翻转的棋子数的分
            double score1 = 0;
            double convertAmountScore = 0;
            boolean[][] willConvertPositions =
                    GameFrame.controller.getChessBoardPanel().willBeConvertedChessGrids(r, c, ChessPiece.WHITE);
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    if(willConvertPositions[i][j]) {
                        convertAmountScore++;
                    }
                }
            }
            score1 = step * convertAmountScore * 1.6 / Math.log(whitePiecesAmount + 1);


            // 2.同行列向同色棋子的分
            double score2 = sameLineScore * Math.sqrt(step) * 0.7;


            // 3.占角的分
            double cornerPositionScore = 0;
            if((r == 0 && c == 0) || (r == 0 && c == 7) ||
                    (r == 7 && c == 0) || (r == 7 && c == 7)) {
                cornerPositionScore = 4;
            }
            double score3 = cornerPositionScore * sameLineScore * Math.log(step);


            // 4.占边的分
            double score4 = occupySideScore;


            // 5.卡棋眼
            double score5 = 0;
            if(r > 0 && r < 7) {
                if(GameFrame.controller.getChessBoardPanel().getChessGrids()[r - 1][c].getChessPiece() == ChessPiece.BLACK &&
                        GameFrame.controller.getChessBoardPanel().getChessGrids()[r + 1][c].getChessPiece() == ChessPiece.BLACK) {
                    score5 = 8;
                }
            }
            if(c > 0 && c < 7) {
                if(GameFrame.controller.getChessBoardPanel().getChessGrids()[r][c - 1].getChessPiece() == ChessPiece.BLACK &&
                        GameFrame.controller.getChessBoardPanel().getChessGrids()[r][c + 1].getChessPiece() == ChessPiece.BLACK) {
                    score5 = 8;
                }
            }


            //生成分数及报告
            score = score1 + score2 + score3 + score4 + score5;
            if(score <= 0) {
                score = 0.1;
            }
            double[] report = new double[6];
            report[0] = score1;
            report[1] = score2;
            report[2] = score3;
            report[3] = score4;
            report[4] = score5;
            report[5] = score;

            //更新返回坐标值
            if(score > maxScore) {
                result[0] = r;
                result[1] = c;
                for(int i = 0; i < 6; i++) {
                    scoreReportOfResult[i] = report[i];
                }
            }
        }

        //报告该选项的各项分数
        System.out.print("Score report:  ");
        for(double score : scoreReportOfResult) {
            System.out.printf("%.2f  ", score);
        }
        System.out.println();

        return result;
    }

}
