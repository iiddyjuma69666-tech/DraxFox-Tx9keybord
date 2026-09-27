package com.draxfox.tx9keyboard;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

/** Tracks recently-used emoji so the "Recent" tab in EmojiPanel has real content.
 *  Stored as a small JSON array inside the same SharedPreferences file as other TX9 prefs. */
public final class EmojiRepository {
    private static final String PREFS = "draxfox_prefs";
    private static final String KEY = "emoji_recent";
    private static final int MAX = 24;

    public static List<String> recent(Context c){
        String raw = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]");
        List<String> out = new ArrayList<>();
        try {
            JSONArray a = new JSONArray(raw);
            for (int i = 0; i < a.length(); i++) out.add(a.getString(i));
        } catch (Exception ignored) { /* corrupted pref value — treat as empty, never crash */ }
        return out;
    }

    public static void addRecent(Context c, String emoji){
        List<String> cur = recent(c);
        cur.remove(emoji);
        cur.add(0, emoji);
        while (cur.size() > MAX) cur.remove(cur.size() - 1);
        JSONArray a = new JSONArray();
        for (String s : cur) a.put(s);
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, a.toString()).apply();
    }
}
