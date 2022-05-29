package methods;

import components.ChessGridComponent;
import model.ChessPiece;
import view.GameFrame;

import java.util.ArrayList;
import java.util.List;

public class NoobAlgorithm extends Algorithm {

    @Override
    public int[] playAStepAt(ChessGridComponent[][] chessGrids) {

        int[] result = new int[2];

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

        //选出返回坐标值
        int maxConvertAmount = 0;
        for(Integer[] position : positions) {

            //计算能够翻转的棋子数
            boolean[][] willConvertPositions =
                    GameFrame.controller.getChessBoardPanel().willBeConvertedChessGrids(position[0], position[1], ChessPiece.WHITE);
            int convertAmount = 0;
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    if(willConvertPositions[i][j]) {
                        convertAmount++;
                    }
                }
            }

            //更新返回坐标值
            if(convertAmount > maxConvertAmount) {
                result[0] = position[0];
                result[1] = position[1];
            }
        }

        return result;
    }

}
