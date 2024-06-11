package tomato.realmshark;

import org.xml.sax.SAXException;
import util.StringXML;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParseDungeonMods {

    private static final String MODS_XML_PATH = "assets/xml/mods.xml";
    private static final HashMap<Integer, String> ID_TO_NAME_MODS = new HashMap<>();
    private static final HashMap<String, Integer> NAME_TO_ID_MODS = new HashMap<>();

    /**
     * Load Dungeon modifiers XML data to get names from file.
     */
    static {
        try {
            FileInputStream file = new FileInputStream(MODS_XML_PATH);
            String result = new BufferedReader(new InputStreamReader(file)).lines().collect(Collectors.joining("\n"));
            StringXML base = StringXML.getParsedXML(result);
            for (StringXML xml : base) {
                if (Objects.equals(xml.name, "DungeonModifier")) {
                    ParseDungeonMods.DungeonModifier modifier = new ParseDungeonMods.DungeonModifier();

                    for (StringXML info : xml) {
                        if (Objects.equals(info.name, "id")) {
                            modifier.name = info.value;
                        }
                        if (Objects.equals(info.name, "type")) {
                            modifier.modId = Short.decode(info.value);
                        }
                    }
                    for (StringXML x : xml) {
                        if (Objects.equals(x.name, "Description")) {
                            modifier.description = x.children.get(0).value;
                        }
                    }
                    ID_TO_NAME_MODS.put(modifier.modId, modifier.name);
                    NAME_TO_ID_MODS.put(modifier.name, modifier.modId);
                }
            }
            NAME_TO_ID_MODS.put("|S", -11);
            NAME_TO_ID_MODS.put("|A", -12);
            NAME_TO_ID_MODS.put("|B", -13);
            NAME_TO_ID_MODS.put("|C", -14);
            NAME_TO_ID_MODS.put("|D", -15);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static int[] getModIds(String dungeonString) {
        if (dungeonString.isEmpty()) return new int[0];
        String[] split = dungeonString.split(";");

        int[] array = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            String key = split[i];
            Integer integer = NAME_TO_ID_MODS.get(key);
            array[i] = integer;
        }

        return array;
    }

    private static class DungeonModifier {
        public int modId;
        public String name;
        public String description;
    }
}
