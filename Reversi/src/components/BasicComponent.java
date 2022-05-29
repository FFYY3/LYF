package components;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class BasicComponent extends JComponent {
    public BasicComponent() {
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                onMouseClicked();
            }
            @Override
            public void mouseEntered(MouseEvent e){
                super.mouseEntered(e);
                onMouseEntered();
            }
            @Override
            public void mouseExited(MouseEvent e){
                super.mouseExited(e);
                onMouseExited();
            }

        });
    }

    public abstract void onMouseClicked();
    public abstract void onMouseEntered();
    public abstract void onMouseExited();
}
