package experimental;

import assets.IdToAsset;
import assets.ImageBuffer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class DisplayIcons {
    static int bigId = 0;

    public static void main(String[] args) throws IOException {
        readAllLines();
    }

    private static void readAllLines() throws IOException {
//        java.io.InputStream is = DisplayIcons.class.getClassLoader().getResourceAsStream("assets/ObjectID.list");
        File file = new File("assets/ObjectID.list");
        String result = new java.io.BufferedReader(new java.io.InputStreamReader(Files.newInputStream(file.toPath()))).lines().collect(java.util.stream.Collectors.joining("\n"));
        String[] split = result.split("\n");
        HashSet<String> hash = new HashSet<>();
        for(String line : split) {
            String[] s = line.split(";");
            if(!hash.contains(s[0])) {
                int id = Integer.parseInt(s[0]);
                extract(id);
            }
            hash.add(s[0]);
        }
    }

    private static void extract(int id) throws IOException {
        BufferedImage img = ImageBuffer.getImage(id);

        try {
            File f = new File("img/" + id + ".png");
            ImageIO.write(img, "PNG", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void display() {
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
