package tomato.gui.chat;

import com.google.gson.Gson;
import packets.incoming.TextPacket;
import tomato.backend.data.TomatoData;
import tomato.gui.TomatoGUI;
import tomato.realmshark.Sound;
import util.PropertiesManager;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

public class ChatGUI extends JPanel {

    private static JTextArea textAreaChatAll;
    private static JTextArea textAreaChatPm;
    private static JTextArea textAreaChatParty;
    private static JTextArea textAreaChatGuild;
    public static boolean save;
    private static TomatoData data;

    private static ArrayList<String> blockedSpam;
    private static final String API_URL = "https://api.realmshark.cc/blocked-keywords";

    private static ArrayList<String> pingMessages;

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

        loadBlockedSpam();
        loadChatPingMessages();
    }

    /**
     * Creates a server request worker to request from server phrases to be blocked by chat. Phrases used by bots.
     */
    private void loadBlockedSpam() {
        try {
            // Create a URL object
            URL url = new URL(API_URL);

            // Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Check response code and follow redirect if necessary
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String newUrl = conn.getHeaderField("Location");
                URL redirectedUrl = new URL(newUrl);
                conn = (HttpURLConnection) redirectedUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            }

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Process the response (assuming it's a JSON array of keywords)
            parseAndUpdateBlockedSpam(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error during HTTP request: " + e.getMessage());
        }
//        System.out.println("Repopulating List\n" + blockedSpam);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::loadBlockedSpam, 30, 30, TimeUnit.MINUTES);
    }

    /**
     * Parse the JSON response and update the list of blocked potential spam.
     *
     * @param jsonResponse The JSON response from the API.
     */
    private static void parseAndUpdateBlockedSpam(String jsonResponse) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        blockedSpam = new Gson().fromJson(jsonResponse, listType);
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
        boolean containsBlockedSpam = blockedSpam.stream().anyMatch(p.text::contains);
        if (containsBlockedSpam) {
            return;
        }

        String a = "";
        int type = 0;
        boolean isPlayer = false;
        if (data.player != null) {
            isPlayer = p.name.equals(data.player.name());
        }
        String name = p.name.split(",")[0];
        boolean pinged = false;
        if (p.recipient.contains("*Guild*")) {
            type = 1;
            a = "[Guild]";
            if (!isPlayer && Sound.playGuildSound) {
                Sound.guild.play();
                pinged = true;
            }
        } else if (p.recipient.contains("*Party*")) {
            type = 2;
            a = "[Party]";
            if (!isPlayer && Sound.playPartySound) {
                Sound.party.play();
                pinged = true;
            }
        } else if (!p.recipient.trim().isEmpty()) {
            type = 3;
            a = "[PM]";
            if (!isPlayer && Sound.playPmSound) {
                Sound.pm.play();
                pinged = true;
            }

            if (data.player != null) {
                if (p.recipient.equals(data.player.name())) {
                    a += " From: ";
                } else if (name.equals(data.player.name())) {
                    name = p.recipient.split(",")[0];
                    a += " To: ";
                }
            }
        }
        if (!pinged) {
            for (String s : pingMessages) {
                if (p.text.contains(s)) {
                    Sound.pm.play();
                    break;
                }
            }
        }
        String s = String.format("%s %s[%s]: %s", Util.getHourTime(), a, name, p.text);
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

        String response = getString(p);

        if (response != null) {
            String responseFormatted = String.format("%s %s[Umi Response]: %s", Util.getHourTime(), a, response);
            switch (type) {
                case 1:
                    if (textAreaChatGuild != null) textAreaChatGuild.append(responseFormatted + "\n");
                    break;
                case 2:
                    if (textAreaChatParty != null) textAreaChatParty.append(responseFormatted + "\n");
                    break;
                case 3:
                    if (textAreaChatPm != null) textAreaChatPm.append(responseFormatted + "\n");
                    break;
            }
            if (textAreaChatAll != null) textAreaChatAll.append(responseFormatted + "\n");

            // Reset the flames for loot tracking here.
            data.resetMoonlightFlames();
        }

        if (save) {
            Util.print("chat/chat", s);
        }
    }

    private static String getString(TextPacket p) {
        String response = null;
        if ("I've been intrigued by folktales from foreign lands recently.".equals(p.text) && "#Village Girl Umi".equals(p.name)) {
            response = "The Happy Prince";
        } else if ("The delicious smells coming from the festival stalls are making me hungry...".equals(p.text) && "#Village Girl Umi".equals(p.name)) {
            response = "Mushroom";
        } else if ("How did you find tonight's performance? It looked extremely fun, I couldn't help cheering you on!".equals(p.text) && "#Village Girl Umi".equals(p.name)) {
            response = "Carosburg";
        }
        return response;
    }

    public void setPingMessages(ArrayList<String> messages) {
        pingMessages = messages;

        if (messages.isEmpty()) return;
        StringBuilder s = new StringBuilder();
        for (String m : messages) {
            s.append("§").append(m);
        }
        PropertiesManager.setProperties("chatPingMessages", s.substring(2));
    }

    public ArrayList<String> getPingMessages() {
        return pingMessages;
    }

    public void loadChatPingMessages() {
        String messages = PropertiesManager.getProperty("chatPingMessages");
        pingMessages = new ArrayList<>();
        pingMessages.addAll(Arrays.asList(messages.split("§")));
    }
}
