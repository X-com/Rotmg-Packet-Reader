package tomato.gui.security;

import assets.ImageBuffer;
import com.google.gson.Gson;
import tomato.realmshark.ParseEquipment;
import tomato.realmshark.enums.CharacterClass;
import tomato.realmshark.enums.StatPotion;
import util.PropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;

public class SecurityFilterGUI extends JPanel {

    private ParsePanelGUI parrent;

    private ArrayList<FilterEntity> classPoints = new ArrayList<>();
    private ArrayList<FilterEntity> items = new ArrayList<>();
    private ArrayList<JCheckBox> checkBoxStats = new ArrayList<>();

    private JComboBox<String> filterComboBox;
    private JTextField jsonField;
    private JTextField nameField;
    private JTextField exaltSkinPointsField;
    private FilterEntity exaltSkin = new FilterEntity();

    public SecurityFilterGUI(ParsePanelGUI parrent) {
        this.parrent = parrent;
        setLayout(new BorderLayout());

        filterComboBox = new JComboBox<>();
        for (SecurityFilter sf : parrent.getFilters().values()) {
            filterComboBox.addItem(sf.name);
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        top(topPanel);

        JPanel bot = new JPanel(new BorderLayout());
        JLabel json = new JLabel("Json:");
        jsonField = new JTextField();
        jsonField.setEditable(false);
        JButton copy = new JButton("Copy Json");
        copy.addActionListener(this::copy);
        bot.add(json, BorderLayout.WEST);
        bot.add(jsonField, BorderLayout.CENTER);
        bot.add(copy, BorderLayout.EAST);
        add(bot, BorderLayout.SOUTH);


        JPanel leftBox = getScrollPanel(this, BorderLayout.WEST, 160, 400);
        leftColumn(leftBox);

        JPanel itemsBox = getScrollPanel(this, BorderLayout.CENTER, 320, 400);
        rightArea(itemsBox);
    }

    private void rightArea(JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        textFieldOptions(panel);
    }

    private static JPanel getScrollPanel(JPanel panel, String placement, int w, int h) {
        JPanel boxScroll = new JPanel();
        JScrollPane scrollPane = new JScrollPane(boxScroll);
        scrollPane.getVerticalScrollBar().setUnitIncrement(40);
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setPreferredSize(new Dimension(w, h));
        contentPane.add(scrollPane);
        panel.add(contentPane, placement);
        scrollPane.setBounds(0, 0, w + 15, h);
        return boxScroll;
    }

    private void top(JPanel topPanel) {
        filterComboBox.setPreferredSize(new Dimension(230, 0));

        JPanel panel1 = new JPanel();
        topPanel.add(panel1, BorderLayout.NORTH);

        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        JLabel name = new JLabel("Name:");
        nameField = new JTextField(15);
        panel1.add(Box.createHorizontalGlue());
        panel1.add(filterComboBox);

        panel1.add(name);
        panel1.add(nameField);
        panel1.add(Box.createHorizontalGlue());

        JPanel panel2 = new JPanel();
        topPanel.add(panel2, BorderLayout.SOUTH);
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        JButton save = new JButton("Save");
        save.addActionListener(this::save);
        JButton load = new JButton("Load");
        load.addActionListener(this::load);
        JButton delete = new JButton("Delete");
        delete.addActionListener(this::deleteButton);
        JButton clear = new JButton("Clear");
        clear.addActionListener(this::clear);
        JButton paste = new JButton("Paste Json");
        paste.addActionListener(this::paste);
        panel2.add(Box.createHorizontalGlue());
        panel2.add(save);
        panel2.add(load);
        panel2.add(delete);
        panel2.add(clear);
        panel2.add(paste);
        panel2.add(Box.createRigidArea(new Dimension(10, 0)));
        panel2.add(Box.createHorizontalGlue());
    }

    private void copy(ActionEvent actionEvent) {
        String text = jsonField.getText();
        if (text.isEmpty()) return;
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private void save(ActionEvent actionEvent) {
        String name = nameField.getText();
        if (!name.isEmpty()) {
            SecurityFilter sf = new SecurityFilter();
            sf.name = name;
            for (FilterEntity item : items) {
                if (item.checkBox.isSelected()) {
                    sf.itemPoint.put(item.id, item.point);
                }
            }
            sf.exaltSkinPoints = exaltSkin.point;
            for (FilterEntity classPoint : classPoints) {
                sf.classPoint.put(classPoint.id, classPoint.point);
            }
            for (int i = 0; i < checkBoxStats.size(); i++) {
                JCheckBox c = checkBoxStats.get(i);
                sf.statMaxed[i] = c.isSelected();
            }

            Gson gson = new Gson();
            sf.json = gson.toJson(sf);
            jsonField.setText(sf.json);

            saveSF(sf);
        }
    }

    private void saveSF(SecurityFilter sf) {
        if (parrent.getFilters().containsKey(sf.name)) {
            if (ask("Are you sure you want to overwrite: " + sf.name)) {
                parrent.getFilters().put(sf.name, sf);
                saveToProfile();
            }
        } else {
            parrent.getFilters().put(sf.name, sf);
            filterComboBox.addItem(sf.name);
            saveToProfile();
        }
    }

    private void saveToProfile() {
        StringBuilder str = new StringBuilder();
        for (SecurityFilter sf : parrent.getFilters().values()) {
            if (!sf.json.isEmpty()) {
                str.append("§").append(sf.json);
            }
        }
        PropertiesManager.setProperties("securityFilters", str.substring(2));
    }

    private void load(ActionEvent actionEvent) {
        String name = (String) filterComboBox.getSelectedItem();
        if (name == null) return;
        SecurityFilter sf = parrent.getFilters().get(name);
        if (sf == null) return;
        loadSF(sf);
    }

    private void loadSF(SecurityFilter sf) {
        nameField.setText(sf.name);
        for (int i = 0; i < checkBoxStats.size(); i++) {
            JCheckBox c = checkBoxStats.get(i);
            c.setSelected(sf.statMaxed[i]);
        }
        exaltSkinPointsField.setText(String.valueOf(sf.exaltSkinPoints));
        exaltSkin.point = sf.exaltSkinPoints;
        for (FilterEntity classPoint : classPoints) {
            int c = sf.classPoint.get(classPoint.id);
            if (c != 0) {
                classPoint.field.setText(String.valueOf(c));
            } else {
                classPoint.field.setText("");
            }
            classPoint.point = c;
        }
        for (FilterEntity item : items) {
            Integer i = sf.itemPoint.get(item.id);
            if (i == null) {
                item.field.setText("");
                item.field.setEnabled(false);
                item.checkBox.setSelected(false);
                item.point = 0;
            } else {
                if (i != 0) {
                    item.field.setText(String.valueOf(i));
                } else {
                    item.field.setText("");
                }
                item.field.setEnabled(true);
                item.checkBox.setSelected(true);
                item.point = i;
            }
        }
        jsonField.setText(sf.json);
    }

    private void deleteButton(ActionEvent actionEvent) {
        String n = (String) filterComboBox.getSelectedItem();
        if (!ask("Are you sure you want to delete: " + n)) return;
        filterComboBox.removeItem(n);
        parrent.getFilters().remove(n);
        saveToProfile();
    }

    private void clear(ActionEvent actionEvent) {
        clearAll();
    }

    private void paste(ActionEvent actionEvent) {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        if (t == null)
            return;
        String json;
        try {
            json = (String) t.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        SecurityFilter sf = SecurityFilter.loadJson(json);
        saveSF(sf);
        loadSF(sf);
    }

    private void clearAll() {
        nameField.setText("");
        for (int i = 0; i < checkBoxStats.size(); i++) {
            JCheckBox c = checkBoxStats.get(i);
            c.setSelected(false);
        }
        exaltSkinPointsField.setText("");
        exaltSkin.point = 0;
        for (FilterEntity classPoint : classPoints) {
            classPoint.field.setText("");
            classPoint.point = 0;
        }
        for (FilterEntity item : items) {
            item.field.setText("");
            item.field.setEnabled(false);
            item.checkBox.setSelected(false);
            item.point = 0;
        }
        jsonField.setText("");
    }

    private void leftColumn(JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        stats(panel);
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        skinPoints(panel);
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        classes(panel);
    }

    private void stats(JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel n = new JLabel("Stats Maxed");
        panel.add(n, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(8, 1));

        for (StatPotion o : StatPotion.values()) {
            JCheckBox box = new JCheckBox(o.name());
            checkBoxStats.add(box);
            stats.add(box);
        }

        panel.add(stats, BorderLayout.CENTER);

        mainPanel.add(panel);
    }

    private void skinPoints(JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel n = new JLabel("Skin points");
        panel.add(n, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new GridLayout(1, 2));
        panel.add(body, BorderLayout.CENTER);

        exaltSkinPointsField = addTextField(1, exaltSkin);
        body.add(new JLabel("Exalted"));
        body.add(exaltSkinPointsField);

        mainPanel.add(panel);
    }

    private void classes(JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel n = new JLabel("Classe Points");
        panel.add(n, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new GridLayout(CharacterClass.CHAR_CLASS_LIST.length, 2));
        panel.add(body, BorderLayout.CENTER);

        for (CharacterClass s : CharacterClass.CHAR_CLASS_LIST) {
            FilterEntity classPoint = new FilterEntity();
            classPoints.add(classPoint);

            JLabel label = new JLabel(s.name());
            classPoint.id = s.getId();
            classPoint.field = addTextField(1, classPoint);
            body.add(label);
            body.add(classPoint.field);
//            classPoint.field.setText("0");
        }

        mainPanel.add(panel);
    }

    private void textFieldOptions(JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel n = new JLabel("Item Points");
        panel.add(n, BorderLayout.NORTH);

        ArrayList<ParseEquipment.Equipment> list = ParseEquipment.getParseItems();
        list.sort(Comparator.comparing(e -> e.name));

        JPanel body = new JPanel();
        body.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        int count = 0;
        for (ParseEquipment.Equipment e : list) {
            FilterEntity item = new FilterEntity();
            items.add(item);
            item.id = e.id;

            JLabel icon = new JLabel(ImageBuffer.getOutlinedIcon(e.id, 24));
            icon.setText(e.name);
            item.field = addTextField(3, item);
            item.checkBox = new JCheckBox();
            gridBagConstraints.gridy = count;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            body.add(icon, gridBagConstraints);
            gridBagConstraints.gridx = 1;
            body.add(item.field, gridBagConstraints);
            gridBagConstraints.gridx = 2;
            body.add(item.checkBox, gridBagConstraints);
            item.checkBox.addActionListener(e1 -> {
                item.field.setEnabled(item.checkBox.isSelected());
            });
//            item.field.setText("0");
            item.field.setEnabled(false);
            count++;
        }
        panel.add(body, BorderLayout.SOUTH);
        mainPanel.add(panel);
    }

    private static JTextField addTextField(int withNumbers, FilterEntity entity) {
        JTextField comp = new JTextField(withNumbers);
        comp.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char caracter = e.getKeyChar();
                if (!((caracter >= '0') && (caracter <= '9')) && (caracter != '-')) {
                    e.consume();
                }
            }

            public void keyReleased(KeyEvent e) {
                String s = comp.getText();
                try {
                    entity.point = Integer.parseInt(s);
                } catch (NumberFormatException exception) {
                    if (!s.isEmpty()) {
                        comp.setText(String.valueOf(entity.point));
                    }
                }
            }
        });
        return comp;
    }

    private static boolean ask(String message) {
        int dialogResult = JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION);
        return dialogResult == 0;
    }

    public static void open(ParsePanelGUI parsePanelGUI) {
        SecurityFilterGUI filter = new SecurityFilterGUI(parsePanelGUI);

        JButton close = new JButton("Close");
        JOptionPane pane = new JOptionPane(filter, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new JButton[]{close}, close);
        close.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(close);
            pane.setValue(-1);
            w.dispose();
            parsePanelGUI.filterUpdate();
        });
        JDialog dialog = pane.createDialog(null, "Security Filter");
//        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }

    private class FilterEntity {
        int id;
        int point;
        JCheckBox checkBox;
        JTextField field;
    }
}
