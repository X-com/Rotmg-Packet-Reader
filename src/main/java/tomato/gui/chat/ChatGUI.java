package tomato.gui.chat;

import packets.incoming.TextPacket;
import tomato.backend.data.TomatoData;
import tomato.gui.TomatoGUI;
import tomato.gui.stats.DungeonStats;
import tomato.gui.stats.FameTrackerGUI;
import tomato.gui.stats.LootGUI;
import tomato.realmshark.Sound;
import util.Util;

import javax.swing.*;
import java.awt.*;

public class ChatGUI extends JPanel {

    private static JTextArea textAreaChatAll;
    private static JTextArea textAreaChatPm;
    private static JTextArea textAreaChatParty;
    private static JTextArea textAreaChatGuild;
    public static boolean save;
    private static TomatoData data;

    public ChatGUI(TomatoData data) {
        ChatGUI.data = data;
        setLayout(new BorderLayout());

        textAreaChatAll = new JTextArea();
        textAreaChatPm = new JTextArea();
        textAreaChatParty = new JTextArea();
        textAreaChatGuild = new JTextArea();

        JPanel all = new JPanel(new BorderLayout());
        JPanel pm = new JPanel(new BorderLayout());
        JPanel party = new JPanel(new BorderLayout());
        JPanel guild = new JPanel(new BorderLayout());

        all.add(TomatoGUI.createTextArea(textAreaChatAll, false));
        pm.add(TomatoGUI.createTextArea(textAreaChatPm, false));
        party.add(TomatoGUI.createTextArea(textAreaChatParty, false));
        guild.add(TomatoGUI.createTextArea(textAreaChatGuild, false));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("All", all);
        tabbedPane.addTab("PM", pm);
        tabbedPane.addTab("Party", party);
        tabbedPane.addTab("Guild", guild);
        add(tabbedPane);
    }

    /**
     * Add text to the chat text area.
     *
     * @param s The text to be added at the end of text area.
     */
    public static void appendTextAreaChat(String s) {
        if (textAreaChatAll != null) textAreaChatAll.append(s);
    }

    /**
     * Clears the chat text area.
     */
    public static void clearTextAreaChat() {
        textAreaChatAll.setText("");
    }

    /**
     * Sets the font of the text area.
     *
     * @param font Font to be set.
     */
    public static void editFont(Font font) {
        textAreaChatAll.setFont(font);
    }

    /**
     * Updates chat with chat message.
     *
     * @param p Text packet with chat data.
     */
    public static void updateChat(TextPacket p) {
        String a = "";
        int type = 0;
        boolean isPlayer = false;
        if (data.player != null) {
            isPlayer = p.name.equals(data.player.name());
        }
        if (p.recipient.contains("*Guild*")) {
            type = 1;
            a = "[Guild]";
            if (!isPlayer && Sound.playGuildSound) {
                Sound.guild.play();
            }
        } else if (p.recipient.contains("*Party*")) {
            type = 2;
            a = "[Party]";
            if (!isPlayer && Sound.playPartySound) {
                Sound.party.play();
            }
        } else if (!p.recipient.trim().isEmpty()) {
            type = 3;
            if (!isPlayer && Sound.playPmSound) {
                Sound.pm.play();
            }
            a = "[PM]";
        }
        String s = String.format("%s %s[%s]: %s", Util.getHourTime(), a, p.name.split(",")[0], p.text);
        switch (type) {
            case 1:
                if (textAreaChatGuild != null) textAreaChatGuild.append(s + "\n");
                break;
            case 2:
                if (textAreaChatParty != null) textAreaChatParty.append(s + "\n");
                break;
            case 3:
                if (textAreaChatPm != null) textAreaChatPm.append(s + "\n");
                break;
        }
        if (textAreaChatAll != null) textAreaChatAll.append(s + "\n");
        if (save) {
            Util.print("chat/chat", s);
        }
    }
}
