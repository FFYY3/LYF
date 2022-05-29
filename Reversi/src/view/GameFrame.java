package view;


import components.ChessGridComponent;
import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    public static GameController controller;
    private ChessBoardPanel chessBoardPanel;
    private StatusPanel statusPanel;
    private JButton cheatModeButton;
    private JLabel gameModeLabel;


    public GameFrame(int frameSize, int gameMode) {

        this.setTitle("2021F CS102A Project Reversi");
        this.setLayout(null);

        //获取窗口边框的长度，将这些值加到主窗口大小上，这能使窗口大小和预期相符
        Insets inset = this.getInsets();
        this.setSize(frameSize + inset.left + inset.right, frameSize + inset.top + inset.bottom);

        this.setLocationRelativeTo(null);


        //创造棋盘面板、顶部状态栏面板
        {
            chessBoardPanel = new ChessBoardPanel((int) (this.getWidth() * 0.8), (int) (this.getHeight() * 0.7));
            chessBoardPanel.setLocation((this.getWidth() - chessBoardPanel.getWidth()) / 2,
                    (this.getHeight() - chessBoardPanel.getHeight()) / 3);

            statusPanel = new StatusPanel((int) (this.getWidth() * 0.8), (int) (this.getHeight() * 0.1));
            statusPanel.setLocation((this.getWidth() - chessBoardPanel.getWidth()) / 2, 0);

            this.add(chessBoardPanel);
            this.add(statusPanel);
        }

        //创造侧面操纵栏：作弊模式
        {
            int W, H, w, h, a, b, d, D;
            W = this.getWidth();
            H = this.getHeight();
            w = chessBoardPanel.getWidth();
            h = chessBoardPanel.getHeight();
            a = (W - w) / 4;  //按钮宽度
            b = h / 10;  //按钮高度
            d = h / 33;  //按钮间距
            D = (W - w) / 12;  //按钮与棋盘间距

            JLabel cheatModeLabel = new JLabel("<html>Cheat<br/>Mode:</html>");
            cheatModeLabel.setSize(a, b);
            cheatModeLabel.setLocation((W + w) / 2 + D, (H + h) / 2 - 3 * b - d);
            cheatModeLabel.setFont(new Font("Calibri", Font.BOLD, 16));
            add(cheatModeLabel);

            cheatModeButton = new JButton("off");
            cheatModeButton.setSize(a, b);
            cheatModeButton.setLocation((W + w) / 2 + D, (H + h) / 2 - 2 * b);
            add(cheatModeButton);
            cheatModeButton.addActionListener(e -> {
                System.out.println("clicked Cheat Mode Btn");
                boolean isCheatMode = controller.changeCheatMode();
                if(isCheatMode) {
                    cheatModeButton.setText("on");
                }else{
                    cheatModeButton.setText("off");
                }
                controller.check();
                if(ChessGridComponent.be2_gameEnded) {
                    controller.restart();
                }
            });
        }

        //创造底部操纵栏：重开、悔棋、运行、保存
        {
            int W, H, w, h, a, b, d;
            W = this.getWidth();
            H = this.getHeight();
            w = chessBoardPanel.getWidth();
            h = chessBoardPanel.getHeight();
            a = w / 5;  //按钮宽度
            b = (H - h) / 4;  //按钮高度
            d = w / 20;  //按钮间距

            JButton restartBtn = new JButton("Restart");
            restartBtn.setSize(a, b);
            restartBtn.setLocation((W - w) / 2, (H + h) / 2);
            add(restartBtn);
            restartBtn.addActionListener(e -> {
                System.out.println("clicked restart Btn");
                controller.restart();
            });

            JButton undoBtn = new JButton("Undo");
            undoBtn.setSize(a, b);
            undoBtn.setLocation((W - w) / 2 + (a + d), (H + h) / 2);
            add(undoBtn);
            undoBtn.addActionListener(e -> {
                System.out.println("clicked Undo Btn");
                controller.undo();
            });

            JButton loadGameBtn = new JButton("Load");
            loadGameBtn.setSize(a, b);
            loadGameBtn.setLocation((W - w) / 2 + (2 * a + 3 * d), (H + h) / 2);
            add(loadGameBtn);
            loadGameBtn.addActionListener(e -> {
                System.out.println("clicked Load Btn");
                controller.loadGame();
            });

            JButton saveGameBtn = new JButton("Save");
            saveGameBtn.setSize(a, b);
            saveGameBtn.setLocation((W - w) / 2 + (3 * a + 4 * d), (H + h) / 2);
            add(saveGameBtn);
            saveGameBtn.addActionListener(e -> {
                System.out.println("clicked Save Btn");
                String gameFileName = JOptionPane.showInputDialog(this, "input game name");
                if(gameFileName != null && !gameFileName.equals("")) {
                    controller.saveGame(gameFileName);
                }
            });
        }

        //创造侧面状态栏：游戏模式
        {
            int W, H, w, h, a, b, d, D;
            W = this.getWidth();
            H = this.getHeight();
            w = chessBoardPanel.getWidth();
            h = chessBoardPanel.getHeight();
            a = (W - w) / 4;  //按钮宽度
            b = h / 6;  //按钮高度
            d = h / 33;  //按钮间距
            D = (W - w) / 12;  //按钮与棋盘间距


            gameModeLabel = new JLabel();
            if(gameMode == 0) {
                gameModeLabel.setText("PVP");
            }
            else if(gameMode == 1) {
                gameModeLabel.setText("<html>PVC<br/>noob</html>");
            }
            else {
                gameModeLabel.setText("<html>PVC<br/>easy</html>");
            }
            gameModeLabel.setSize(a, b);
            gameModeLabel.setLocation((W + w) / 2 + D, (H - h) / 2);
            gameModeLabel.setFont(new Font("Calibri", Font.BOLD, 16));
            add(gameModeLabel);
        }

        //创造控制器
        controller = new GameController(chessBoardPanel, statusPanel, cheatModeButton, gameModeLabel, gameMode);



        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
