package controller;

import components.ChessGridComponent;
import methods.Algorithm;
import methods.EasyAlgorithm;
import methods.NoobAlgorithm;
import model.ChessPiece;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameController {

    private int gameMode;  // 0: PVP, 1: PVC noob, 2: PVC easy
    private JLabel gameModeLabel;

    private ChessBoardPanel chessBoardPanel;
    private StatusPanel statusPanel;
    private JButton cheatModeButton;

    private boolean isCheatMode;
    private ChessPiece currentPlayer;
    private int blackScore;
    private int whiteScore;
    private int step;

    private List<Object[]> gameCache;
    private List<String[]> gameCacheInString;



    public GameController(ChessBoardPanel chessBoardPanel, StatusPanel statusPanel, JButton cheatModeButton,
                          JLabel gameModeLabel, int gameMode) {

        this.gameMode = gameMode;
        this.gameModeLabel = gameModeLabel;

        this.chessBoardPanel = chessBoardPanel;
        this.statusPanel = statusPanel;
        this.cheatModeButton = cheatModeButton;

        this.currentPlayer = ChessPiece.BLACK;
        blackScore = 2;
        whiteScore = 2;
        step = 0;

        gameCache = new ArrayList<>(0);
        gameCacheInString = new ArrayList<>(0);
        cache();
        System.out.printf("Cached step %d\n", step);
        System.out.printf("Cached totally %d steps(includes step 0)\n", gameCache.size());

    }



    //动作

    public void playerPlaysAStepAndRefresh(int row, int col) {

        chessBoardPanel.getChessGrids()[row][col].setChessPiece(currentPlayer);
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(chessBoardPanel.getChessGrids()[i][j].getWillBeConverted()) {
                    chessBoardPanel.getChessGrids()[i][j].setChessPiece(currentPlayer);
                }
            }
        }

        swapPlayer();
        addStep();
        countScore();
        refreshStatusPanel();

        chessBoardPanel.clearChessGridsMarks();
        chessBoardPanel.getChessGrids()[row][col].setIsTheLastMove(true);
        setIsPossibleMoveChessGrids();

    }  //玩家走一步棋，然后刷新游戏

    public void computerPlaysAStepAndRefresh() {

        Algorithm algorithm;
        if(gameMode == 1) {
            algorithm = new NoobAlgorithm();
        }else{
            algorithm = new EasyAlgorithm();
        }

        int[] position = algorithm.playAStepAt(chessBoardPanel.getChessGrids());

        //刷新能被翻转的棋格数据
        boolean[][] willBeConvertedChessPieces = willBeConvertedChessGrids(position[0], position[1]);
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                chessBoardPanel.getChessGrids()[i][j].setWillBeConverted(willBeConvertedChessPieces[i][j]);
            }
        }

        //下棋并刷新棋盘数据
        playerPlaysAStepAndRefresh(position[0], position[1]);
    }

    public void restart() {
        ChessGridComponent.be2_gameEnded = false;

        isCheatMode = false;
        cheatModeButton.setText("off");
        currentPlayer = ChessPiece.BLACK;
        step = 0;

        chessBoardPanel.clearChessGrids();
        chessBoardPanel.initializeChessGrids();
        chessBoardPanel.repaint();

        resetStatusPanel();

        gameCache.clear();
        gameCacheInString.clear();
        cache();
    }

    public void undo() {
        if(gameCache.size() > 1) {

            Object[] lastCache;
            swapPlayer();
            if(gameMode == 0 || onePlayerHasNoPossibleMoves()) {
                //上面的第二个条件旨在判定是否是“虽然是人机模式但机器人因为没有合法棋格而被跳过”的情况
                gameCache.remove(step);
                gameCacheInString.remove(step);
                lastCache = gameCache.get(step - 1);

                step--;
            }else{
                swapPlayer();

                gameCache.remove(step);
                gameCacheInString.remove(step);
                gameCache.remove(step - 1);
                gameCacheInString.remove(step - 1);
                lastCache = gameCache.get(step - 2);

                step = step - 2;
            }

            currentPlayer = (ChessPiece)lastCache[2];

            int[][] lastChessGrids = (int[][])lastCache[1];
            chessBoardPanel.clearChessGrids();
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    if(lastChessGrids[i][j] == -1 || lastChessGrids[i][j] == -10) {
                        chessBoardPanel.getChessGrids()[i][j].setChessPiece(ChessPiece.BLACK);
                        if(lastChessGrids[i][j] == -10) {
                            chessBoardPanel.getChessGrids()[i][j].setIsTheLastMove(true);
                        }
                    }
                    else if(lastChessGrids[i][j] == 1 || lastChessGrids[i][j] == 10) {
                        chessBoardPanel.getChessGrids()[i][j].setChessPiece(ChessPiece.WHITE);
                        if(lastChessGrids[i][j] == 10) {
                            chessBoardPanel.getChessGrids()[i][j].setIsTheLastMove(true);
                        }
                    }
                    else {
                        chessBoardPanel.getChessGrids()[i][j].setChessPiece(null);
                    }
                }
            }
            chessBoardPanel.markIsPossibleMoveChessGrids();
            chessBoardPanel.repaint();

            isCheatMode = (boolean)lastCache[3];
            if(isCheatMode) {
                cheatModeButton.setText("on");
            }else{
                cheatModeButton.setText("off");
            }

            countScore();
            refreshStatusPanel();

        }
    }

    public void loadGame() {
        //todo: read date from file
        JFrame frame = new JFrame();

        //得到选项的字符串数组：selectionsInString
        File file = new File("./");
        File[] files = file.listFiles();
        List<File> fileList = new ArrayList<>(0);

        for(File checkFile : files) {
            if(checkFile.isFile()) {
                fileList.add(checkFile);
            }
        }
        String[] selections = new String[fileList.size()];

        int fileCounter = 0;
        for(File selectableFile : fileList) {
            selections[fileCounter] = selectableFile.getName();
            fileCounter++;
        }

        JPanel upperPanel = new JPanel();
        JPanel netherPanel = new JPanel();

        JLabel label = new JLabel("games ");

        JList list = new JList(selections);  //选项列表
        list.setVisibleRowCount(5);
        list.setPreferredSize(new Dimension(200, 100));

        JScrollPane scrollPane = new JScrollPane(list);  //滚动窗条

        JButton button = new JButton("confirm");
        button.setSize(new Dimension(25, 10));
        button.addActionListener(e -> {
            List<String> selectedGame = list.getSelectedValuesList();

            if(selectedGame.size() == 1) {
                String gameFileName = selectedGame.get(0);
                try {

                    FileReader fileReader = new FileReader(gameFileName);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    String line;
                    String generalGameString = "";
                    String gameString = "";
                    String gameStringInHashCode;

                    //将分为多行的文档读取成单个字符串
                    while ((line = bufferedReader.readLine()) != null) {
                        generalGameString += (line + "\n");
                    }
                    String[] stringPieces = generalGameString.split("\n");

                    //翻译出 gameString 和 gameStringInHashCode
                    for(int i = 0; i < stringPieces.length - 1; i++) {
                        gameString += stringPieces[i] + "\n";
                    }
                    gameStringInHashCode = String.valueOf(gameString.hashCode());

                    //判断存档是否有效
                    if(gameStringInHashCode.equals(stringPieces[stringPieces.length - 1])) {
                        gameCache.clear();
                        gameCacheInString.clear();

                        frame.dispose();

                        String[] gameStrings = gameString.split("\n");
                        int stepCounter = 0;
                        for(String currentGameString : gameStrings) {
                            refreshGameWithCacheInString(currentGameString, stepCounter);
                            stepCounter++;
                        }
                        chessBoardPanel.markIsPossibleMoveChessGrids();

                        if(gameMode == 0) {
                            gameModeLabel.setText("PVP");
                        }
                        else if(gameMode == 1) {
                            gameModeLabel.setText("<html>PVC<br/>noob</html>");
                        }
                        else {
                            gameModeLabel.setText("<html>PVC<br/>easy</html>");
                        }

                    }else{
                        JOptionPane.showMessageDialog(null,
                                "Invalid archived game.", "failed", JOptionPane.INFORMATION_MESSAGE);
                    }

                    fileReader.close();

                } catch (Exception e2) {
                    //e2.printStackTrace();
                    frame.dispose();
                    JOptionPane.showMessageDialog(null,
                            "Failed to open this one.", "failed", JOptionPane.INFORMATION_MESSAGE);
                }

            }else{
                JOptionPane.showMessageDialog(null,
                        "Invalid choice.", "failed", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        frame.setLayout(new GridLayout(2,1));
        upperPanel.add(label);
        upperPanel.add(scrollPane);
        netherPanel.add(button);
        frame.add(upperPanel);
        frame.add(netherPanel);

        frame.setTitle("load");
        frame.setSize(300, 250);
        frame.setLocationRelativeTo(chessBoardPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }  // readFileData

    public void saveGame(String gameFileName) {
        //todo: write data into file
        boolean toDo = true;
        File gameFile = new File("./");

        File[] files = gameFile.listFiles();
        for(File file : files) {
            if(file.getName().equals(gameFileName)) {
                JOptionPane.showMessageDialog(null,
                        "This game has already existed.", "failed", JOptionPane.INFORMATION_MESSAGE);
                toDo = false;
                break;
            }
        }
        if(toDo) {
            try {
                String gameString = "";
                for(String[] currentGameCache : gameCacheInString) {
                    String currentGameString = currentGameCache[0] + ":" + currentGameCache[1] + ":" +
                            currentGameCache[2] + ":" + currentGameCache[3] + "\n";
                    gameString += currentGameString;
                }
                System.out.println("gameString: " + gameString);
                int gameStringInHashCode = gameString.hashCode();
                System.out.println("gameString in hash code: " + gameStringInHashCode);

                Formatter output = new Formatter(gameFileName);
                output.format(String.valueOf(gameString + gameStringInHashCode));
                output.close();
                JOptionPane.showMessageDialog(null,
                        "Saved successfully.", "succeeded", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                //e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "This game name is illegal.", "failed", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }  // writeDataToFile

    public boolean changeCheatMode() {
        if(isCheatMode) {
            isCheatMode = false;
            return false;
        }else{
            isCheatMode = true;
            return true;
        }
    }

    public void setIsPossibleMoveChessGrids() {
        boolean unused;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                unused = isPossibleMove(i, j);
            }
        }
    }

    public Object[] currentGameToCache() {
        Object[] result = new Object[4];

        //step: equals the index

        //gameMode
        result[0] = gameMode;

        //chessGrids
        ChessGridComponent[][] currentChessGrids = chessBoardPanel.getChessGrids();
        int[][] currentChessGridsToCache = new int[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(currentChessGrids[i][j].getChessPiece() == ChessPiece.BLACK) {
                    if(currentChessGrids[i][j].getIsTheLastMove()) {
                        currentChessGridsToCache[i][j] = -10;
                    }else{
                        currentChessGridsToCache[i][j] = -1;
                    }
                }
                else if(currentChessGrids[i][j].getChessPiece() == ChessPiece.WHITE) {
                    if(currentChessGrids[i][j].getIsTheLastMove()) {
                        currentChessGridsToCache[i][j] = 10;
                    }else{
                        currentChessGridsToCache[i][j] = 1;
                    }
                }
                else {
                    currentChessGridsToCache[i][j] = 0;
                }
            }
        }
        result[1] = currentChessGridsToCache;

        //currentPlayer
        result[2] = currentPlayer;

        //isCheatMode
        result[3] = isCheatMode;

        return result;
    }

    public String[] currentGameToCacheInString() {
        String[] result = new String[4];

        //step: equals the index

        //gameMode
        result[0] = gameMode + "";

        //chessGrids
        ChessGridComponent[][] currentChessGrids = chessBoardPanel.getChessGrids();
        result[1] = "";
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(currentChessGrids[i][j].getChessPiece() == ChessPiece.BLACK) {
                    if(currentChessGrids[i][j].getIsTheLastMove()) {
                        result[1] += 4;
                    }else{
                        result[1] += 2;
                    }
                }
                else if(currentChessGrids[i][j].getChessPiece() == ChessPiece.WHITE) {
                    if(currentChessGrids[i][j].getIsTheLastMove()) {
                        result[1] += 3;
                    }else{
                        result[1] += 1;
                    }
                }
                else {
                    result[1] += 0;
                }
            }
        }

        //currentPlayer
        if(currentPlayer == ChessPiece.BLACK) {
            result[2] = 2 + "";
        }else{
            result[2] = 1 + "";
        }

        //isCheatMode
        if(isCheatMode) {
            result[3] = 1 + "";
        }else{
            result[3] = 0 + "";
        }

        return result;
    }

    public void refreshGameWithCacheInString(String currentGameString, int step) {
        String[] currentGameCache = currentGameString.split("\n")[0].split(":");
        chessBoardPanel.clearChessGrids();

        //gameMode
        if(Integer.parseInt(currentGameCache[0]) == 0) {
            gameMode = 0;
        }
        else if(Integer.parseInt(currentGameCache[0]) == 1) {
            gameMode = 1;
        }
        else {
            gameMode = 2;
        }

        //chessGrids
        char[] chessValues = currentGameCache[1].toCharArray();
        int r = 0;
        int c = 0;
        for(char chessValue : chessValues) {
            if(chessValue == '2' || (int)chessValue == '4') {
                chessBoardPanel.getChessGrids()[r][c].setChessPiece(ChessPiece.BLACK);
                if((int)chessValue == '4') {
                    chessBoardPanel.getChessGrids()[r][c].setIsTheLastMove(true);
                }
            }
            else if(chessValue == '1' || chessValue == '3') {
                chessBoardPanel.getChessGrids()[r][c].setChessPiece(ChessPiece.WHITE);
                if(chessValue == '3') {
                    chessBoardPanel.getChessGrids()[r][c].setIsTheLastMove(true);
                }
            }
            else {
                chessBoardPanel.getChessGrids()[r][c].setChessPiece(null);
            }

            if(c == 7) {
                c = 0;
                r++;
            }else{
                c++;
            }
        }

        chessBoardPanel.markIsPossibleMoveChessGrids();

        //currentPlayer
        if(Integer.parseInt(currentGameCache[2]) == 2) {
            currentPlayer = ChessPiece.BLACK;
        }else{
            currentPlayer = ChessPiece.WHITE;
        }

        //CheatMode
        if(Integer.parseInt(currentGameCache[3]) == 1) {
            isCheatMode = true;
            cheatModeButton.setText("on");
        }else{
            isCheatMode = false;
            cheatModeButton.setText("off");
        }

        this.step = step;
        countScore();
        refreshStatusPanel();
        chessBoardPanel.repaint();

        cache();
    }


    //响应

    ////刷新的子方法
    public void swapPlayer() {
        currentPlayer = (currentPlayer == ChessPiece.BLACK) ? ChessPiece.WHITE : ChessPiece.BLACK;
    }
    public void addStep() {
        //
        step += 1;
    }
    public void countScore() {
        //todo: modify the countScore method
        this.blackScore = 0;
        this.whiteScore = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(chessBoardPanel.getChessGrids()[i][j].getChessPiece() == ChessPiece.BLACK) {
                    this.blackScore++;
                }
                else if(chessBoardPanel.getChessGrids()[i][j].getChessPiece() == ChessPiece.WHITE) {
                    this.whiteScore++;
                }
            }
        }
    }
    public void refreshStatusPanel() {
        statusPanel.setPlayerText(currentPlayer.name());
        statusPanel.setScoreText(blackScore, whiteScore);
        statusPanel.setStepText(step);
    }
    public void resetStatusPanel() {
        statusPanel.setPlayerText(ChessPiece.BLACK.name());
        statusPanel.setScoreText(2, 2);
        statusPanel.setStepText(0);
    }

    public void check() {
        if(onePlayerHasNoPossibleMoves() && !isGameOver()) {
            if(gameMode == 0) {
                swapPlayer();
                chessBoardPanel.markIsPossibleMoveChessGrids();
                JOptionPane.showMessageDialog(null, "Swapped player because there is no possible moves.",
                        "", JOptionPane.INFORMATION_MESSAGE);

                chessBoardPanel.repaint();
                refreshStatusPanel();
            }else{
                if(currentPlayer == ChessPiece.BLACK) {
                    System.out.println("Player has no possible moves.");
                    swapPlayer();
                    chessBoardPanel.markIsPossibleMoveChessGrids();
                    JOptionPane.showMessageDialog(null, "Swapped player because there is no possible moves.",
                            "", JOptionPane.INFORMATION_MESSAGE);

                    computerPlaysAStepAndRefresh();
                    cache();
                    while(onePlayerHasNoPossibleMoves() && !isGameOver()) {
                        swapPlayer();
                        computerPlaysAStepAndRefresh();
                        cache();
                    }

                }else{
                    System.out.println("Computer has no possible moves.");
                    swapPlayer();
                    chessBoardPanel.markIsPossibleMoveChessGrids();
                    JOptionPane.showMessageDialog(null, "Swapped player because there is no possible moves.",
                            "", JOptionPane.INFORMATION_MESSAGE);
                    ChessGridComponent.be1 = false;
                }
            }
        }
        else if(isGameOver()) {
            ChessGridComponent.be2_gameEnded = true;
            if(gameMode == 0) {
                if(blackScore > whiteScore) {
                    JOptionPane.showMessageDialog(null, "BLACK Player won!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else if(blackScore < whiteScore) {
                    JOptionPane.showMessageDialog(null, "WHITE Player won!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Ended in a draw!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }else{
                if(blackScore > whiteScore) {
                    JOptionPane.showMessageDialog(null, "You won!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else if(blackScore < whiteScore) {
                    JOptionPane.showMessageDialog(null, "You lost!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Ended in a draw!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }  //检验游戏状态并做出响应
    ////子方法
    public boolean isGameOver() {
        boolean isGameOver = false;
       if(onePlayerHasNoPossibleMoves()) {
           swapPlayer();
           chessBoardPanel.markIsPossibleMoveChessGrids();
           if(onePlayerHasNoPossibleMoves()) {
               isGameOver = true;
           }
           swapPlayer();
       }
       if(isGameOver) {
           return true;
       }else{
           return false;
       }
    }
    public boolean onePlayerHasNoPossibleMoves() {
        ChessGridComponent[][] checkedChessGrids = chessBoardPanel.getChessGrids();

        if(isCheatMode && currentPlayer == ChessPiece.BLACK) {
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    if (checkedChessGrids[i][j].getChessPiece() == null) {
                        return false;
                    }
                }
            }
        }else{
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    if (checkedChessGrids[i][j].getIsPossibleMove()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void cache() {
        //
        gameCache.add(currentGameToCache());
        gameCacheInString.add(currentGameToCacheInString());
    }  //缓存：自动存储当前游戏内容


    //读取和设置数据

    public int getStep() {
        return step;
    }
    public boolean getIsCheatMode() {
        return isCheatMode;
    }
    public ChessPiece getCurrentPlayer() {
        return currentPlayer;
    }
    public int getGameMode() {
        return gameMode;
    }
    public ChessBoardPanel getChessBoardPanel() {
        return chessBoardPanel;
    }
    public List<Object[]> getGameCache() {
        //
        return gameCache;
    }
    public void setChessBoardPanel(ChessBoardPanel chessBoardPanel) {
        this.chessBoardPanel = chessBoardPanel;
    }
    public void setCheatModeButton(JButton cheatModeButton) {
        this.cheatModeButton = cheatModeButton;
    }


    //分析

    public boolean isPossibleMove(int row, int col) {
        return chessBoardPanel.isPossibleMove(row, col, currentPlayer);
    }  //判断在当前棋格下棋是否为有效走法

    public boolean canClick(int row, int col) {
        if(isCheatMode) {
            if(chessBoardPanel.getChessPiece(row, col) == null) {
                return true;
            }else{
                return false;
            }
        }else{
            return isPossibleMove(row, col);
        }
    }  //判断玩家能否在当前棋格下棋

    public boolean[][] willBeConvertedChessGrids(int row, int col) {
        return chessBoardPanel.willBeConvertedChessGrids(row, col, currentPlayer);
    }  //条件：该棋格为合法棋格








}
