package view;

import model.ChessPiece;

import javax.swing.*;
import java.awt.*;

import static java.lang.String.valueOf;

public class StatusPanel extends JPanel {

    private JLabel playerLabel;
    private JLabel scoreLabel;
    private JLabel stepLabel;



    public StatusPanel(int width, int height) {
        this.setSize(width, height);
        this.setLayout(null);
        this.setVisible(true);

        this.playerLabel = new JLabel();
        this.playerLabel.setLocation(0, 10);
        this.playerLabel.setSize((int) (width * 0.4), height);
        this.playerLabel.setFont(new Font("Calibri", Font.BOLD, 30));
        this.setPlayerText(ChessPiece.BLACK.name());
        add(playerLabel);

        this.scoreLabel = new JLabel();
        this.scoreLabel.setLocation((int) (width * 0.35), 10);
        this.scoreLabel.setSize((int) (width * 0.5), height);
        this.scoreLabel.setFont(new Font("Calibri", Font.ITALIC, 25));
        this.setScoreText(2,2);
        add(scoreLabel);

        this.stepLabel = new JLabel();
        this.stepLabel.setLocation((int)(width*0.75),10);
        this.stepLabel.setSize((int)(width * 0.2),height);
        this.stepLabel.setFont(new Font("Calibri", Font.ITALIC, 25));
        this.setStepText(0);
        add(stepLabel);

    }



    public void setScoreText(int black, int white) {
        this.scoreLabel.setText(String.format("BLACK: %d  WHITE: %d ", black, white));
    }

    public void setPlayerText(String playerText) {
        //
        this.playerLabel.setText(playerText + "'s turn");
    }

    public void setStepText(int step) {
        //
        this.stepLabel.setText("Steps: " + valueOf(step));
    }


}
