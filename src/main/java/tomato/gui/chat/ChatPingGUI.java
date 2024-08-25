package tomato.gui.chat;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ChatPingGUI extends JPanel {

    private static ArrayList<JTextField> textFieldNames = new ArrayList<>();

    public ChatPingGUI(ArrayList<String> pingMessages) {
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

    private void addTextFields(JPanel mainPanel, ArrayList<String> pingMessages) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Chat Messages to ping on."), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        topPanel.add(body, BorderLayout.CENTER);

        if (pingMessages != null && !pingMessages.isEmpty()) {
            for (String s : pingMessages) {
                JTextField comp1 = new JTextField(s);
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

    public static void open(ChatGUI main) {
        textFieldNames.clear();
        ChatPingGUI chatping = new ChatPingGUI(main.getPingMessages());

        JButton close = new JButton("Close");
        JOptionPane pane = new JOptionPane(chatping, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new JButton[]{close}, close);
        close.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(close);
            pane.setValue(-1);
            w.dispose();
            ArrayList<String> arr = new ArrayList<>();
            for (JTextField f : textFieldNames) {
                if (!f.getText().replaceAll(" ", "").isEmpty() && !arr.contains(f.getText())) {
                    arr.add(f.getText());
                }
            }
            main.setPingMessages(arr);
        });
        JDialog dialog = pane.createDialog(main, "Chat Ping Messages");
//        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }
}
