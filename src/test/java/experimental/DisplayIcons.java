package experimental;

import assets.IdToAsset;
import assets.ImageBuffer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DisplayIcons {
    static int bigId = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        frame.setSize(new Dimension(300, 300));
        frame.setLayout(new BorderLayout());
        JTextField comp = new JTextField("1");
        frame.add(comp, BorderLayout.NORTH);
        JLabel label = new JLabel();
        label.setHorizontalAlignment(label.CENTER);
        comp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        bigId = Integer.parseInt(comp.getText());
                        displayImg(label, bigId);
                    } catch (Exception ignore) {
                    }
                }
            }
        });
        frame.addMouseWheelListener(arg -> {
            arg.consume();
            if (arg.getPreciseWheelRotation() > 0) {
                bigId++;
            } else if (arg.getPreciseWheelRotation() < 0) {
                bigId--;
            }
            if (bigId < 1) bigId = 1;
            displayImg(label, bigId);
            comp.setText(String.valueOf(bigId));
        });
        frame.add(label, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void displayImg(JLabel label, int eq) {
        try {
            label.setIcon(ImageBuffer.getOutlinedIcon(eq, 100));
//            label.setText(IdToAsset.objectName(eq));
//                icon[i].setToolTipText(String.format("<html>%s<br>%s</html>", IdToAsset.objectName(eq), enchant));
            label.setToolTipText(IdToAsset.objectName(eq));
        } catch (Exception e) {
        }
    }
}
