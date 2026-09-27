package com.draxfox.tx9keyboard;

import android.content.Context;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

/** Remembers image URIs the user has picked as stickers (persistable read permission was
 *  already taken in StickerPickerActivity), so they can be reused later without re-picking. */
public final class StickerLibrary {
    private static final String PREFS = "draxfox_prefs";
    private static final String KEY = "sticker_library";
    private static final int MAX = 24;

    public static List<String> list(Context c){
        String raw = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]");
        List<String> out = new ArrayList<>();
        try {
            JSONArray a = new JSONArray(raw);
            for (int i = 0; i < a.length(); i++) out.add(a.getString(i));
        } catch (Exception ignored) { }
        return out;
    }

    public static void add(Context c, String uriString){
        if (uriString == null || uriString.isEmpty()) return;
        List<String> cur = list(c);
        cur.remove(uriString);
        cur.add(0, uriString);
        while (cur.size() > MAX) cur.remove(cur.size() - 1);
        save(c, cur);
    }

    public static void remove(Context c, int index){
        List<String> cur = list(c);
        if (index >= 0 && index < cur.size()) { cur.remove(index); save(c, cur); }
    }

    private static void save(Context c, List<String> items){
        JSONArray a = new JSONArray();
        for (String s : items) a.put(s);
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, a.toString()).apply();
    }
}
