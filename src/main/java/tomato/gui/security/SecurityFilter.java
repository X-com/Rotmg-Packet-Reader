package tomato.gui.security;

import com.google.gson.Gson;

import java.util.TreeMap;

public class SecurityFilter {
    public String name;
    public transient String json;
    public int exaltSkinPoints;
    public boolean[] statMaxed = new boolean[8];
    public TreeMap<Integer, Integer> itemPoint = new TreeMap<>();
    public TreeMap<Integer, Integer> classPoint = new TreeMap<>();

    public static SecurityFilter loadJson(String json) {
        try {
            SecurityFilter sf = new Gson().fromJson(json, SecurityFilter.class);
            sf.json = json;
            return sf;
        } catch (Exception e) {
            return null;
        }
    }
}
