package tomato.gui.maingui;

import tomato.backend.data.TomatoData;
import tomato.realmshark.Sound;
import util.PropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CustomPingGUI extends JPanel {

    private static ArrayList<JTextField> textFieldNames = new ArrayList<>();

    public CustomPingGUI(ArrayList<Integer> pingMessages) {
        setLayout(new BorderLayout());

        JPanel boxScroll = new JPanel();
        JScrollPane scrollPane = new JScrollPane(boxScroll);

        int w = 260;
        int h = 150;
        scrollPane.setBounds(0, 0, w + 15, h);
        scrollPane.getVerticalScrollBar().setUnitIncrement(40);
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(w, h));
        contentPane.add(scrollPane);
        add(contentPane, BorderLayout.CENTER);

        addTextFields(boxScroll, pingMessages);
    }

    private void addTextFields(JPanel mainPanel, ArrayList<Integer> pingMessages) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Entity ID to ping on."), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        topPanel.add(body, BorderLayout.CENTER);

        if (pingMessages != null && !pingMessages.isEmpty()) {
            for (int s : pingMessages) {
                JTextField comp1 = new JTextField();
                comp1.setText(String.valueOf(s));
                textFieldNames.add(comp1);
                body.add(comp1);
            }
        } else {
            JTextField comp1 = new JTextField();
            textFieldNames.add(comp1);
            body.add(comp1);
        }

        JPanel bot = new JPanel();
        JButton addButton = new JButton("+");
        addButton.addActionListener(e -> {
            JTextField comp2 = new JTextField();
            textFieldNames.add(comp2);
            body.add(comp2);
            revalidate();
        });
        bot.setPreferredSize(new Dimension(250, 34));
        bot.add(addButton);
        topPanel.add(bot, BorderLayout.SOUTH);

        mainPanel.add(topPanel);
    }

    public static void open(TomatoData data) {
        Sound.custom.play();
        textFieldNames.clear();
        CustomPingGUI chatping = new CustomPingGUI(data.getEntityIdPings());

        JButton close = new JButton("Save");
        JOptionPane pane = new JOptionPane(chatping, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new JButton[]{close}, close);
        close.addActionListener(e -> {
            for (JTextField f : textFieldNames) {
                String text = f.getText().replaceAll(" ", "");
                if (text.isEmpty()) continue;
                try {
                    Integer.parseInt(text);
                } catch (NumberFormatException ignore) {
                    JOptionPane.showMessageDialog(null, "Entity IDs are not valid. The text fields must only contain numbers.");
                    return;
                }
            }
            Window w = SwingUtilities.getWindowAncestor(close);
            pane.setValue(-1);
            w.dispose();
            ArrayList<Integer> arr = new ArrayList<>();
            for (JTextField f : textFieldNames) {
                if (!f.getText().replaceAll(" ", "").isEmpty() && !arr.contains(f.getText())) {
                    Integer e1 = Integer.valueOf(f.getText());
                    arr.add(e1);
                }
            }
            setPingIds(data, arr);
        });
        JDialog dialog = pane.createDialog(null, "Custom Entity ID Ping");
//        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }

    public static void setPingIds(TomatoData data, ArrayList<Integer> pingList) {
        data.setIdEntityPing(pingList);
        if (pingList.isEmpty()) {
            PropertiesManager.setProperties("entityIdPings", "");
            return;
        }
        StringBuilder s = new StringBuilder();
        for (Integer i : pingList) {
            s.append("§").append(i);
        }
        PropertiesManager.setProperties("entityIdPings", s.substring(2));
    }

    public static void loadIdPing(TomatoData data) {
        ArrayList<Integer> arr = new ArrayList<>();
        String messages = PropertiesManager.getProperty("entityIdPings");
        if (messages == null) return;
        for (String s : messages.split("§")) {
            if (!s.isEmpty()) {
                arr.add(Integer.valueOf(s));
            }
        }
        data.setIdEntityPing(arr);
    }
}
