package com.draxfox.tx9keyboard;

import android.content.Context;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

/** Stores recently-copied text so the user can reuse it later. Newest item first.
 *  Respects an on/off flag (Settings → Clipboard) for privacy — when off, nothing is recorded. */
public final class ClipboardHistory {
    private static final String PREFS = "draxfox_prefs";
    private static final String KEY = "clip_history";
    private static final int MAX = 30;
    private static final int MAX_ITEM_LEN = 4000; // skip absurdly long copies (whole pages, etc.)

    public static boolean enabled(Context c){
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("clip_history_enabled", true);
    }

    public static void setEnabled(Context c, boolean v){
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("clip_history_enabled", v).apply();
    }

    public static List<String> list(Context c){
        String raw = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]");
        List<String> out = new ArrayList<>();
        try {
            JSONArray a = new JSONArray(raw);
            for (int i = 0; i < a.length(); i++) out.add(a.getString(i));
        } catch (Exception ignored) { }
        return out;
    }

    public static void add(Context c, String text){
        if (text == null) return;
        String t = text.trim();
        if (t.isEmpty() || t.length() > MAX_ITEM_LEN) return;
        List<String> cur = list(c);
        cur.remove(t);        // move existing entry to the top instead of duplicating
        cur.add(0, t);
        while (cur.size() > MAX) cur.remove(cur.size() - 1);
        save(c, cur);
    }

    public static void remove(Context c, int index){
        List<String> cur = list(c);
        if (index >= 0 && index < cur.size()) { cur.remove(index); save(c, cur); }
    }

    public static void clear(Context c){ save(c, new ArrayList<>()); }

    private static void save(Context c, List<String> items){
        JSONArray a = new JSONArray();
        for (String s : items) a.put(s);
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, a.toString()).apply();
    }
}
