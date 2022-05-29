import view.GameFrame;
import methods.SimpleMethods;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            int size = 800;
            int gameMode = -1;
            boolean createBoard = false;


            String[] options = {"PVP", "PVC noob", "PVC easy"};
            JOptionPane optionPane = new JOptionPane();
            gameMode = optionPane.showOptionDialog(null, "Choose game mode", null,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if(gameMode == 0) {
                System.out.println("Game Mode PVP was set");
                createBoard = true;
            }
            if(gameMode == 1) {
                System.out.println("Game Mode PVC noob was set");
                createBoard = true;
            }
            if(gameMode == 2) {
                System.out.println("Game Mode PVC easy was set");
                createBoard = true;
            }


            //创建界面
            if(createBoard) {
                GameFrame mainFrame = new GameFrame(size, gameMode);
                mainFrame.setVisible(true);
            }

        });
    }
}
