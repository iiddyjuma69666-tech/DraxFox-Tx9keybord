package com.draxfox.tx9keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;

public final class ClipboardPanel {
    public interface Listener {
        void insert(String text);
        void changed(); // called after delete/clear so the caller can redraw
    }

    public static View create(Context c, int bg, int keyBg, int keyStroke, int txt, Listener l){
        LinearLayout root = new LinearLayout(c);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(5, 4, 5, 4);
        root.setBackgroundColor(bg);

        LinearLayout top = new LinearLayout(c);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = new TextView(c);
        title.setText("📋 CLIPBOARD HISTORY");
        title.setTextColor(txt);
        title.setTextSize(12);
        title.setPadding(8,5,8,5);
        top.addView(title, new LinearLayout.LayoutParams(0, 42, 1));

        TextView clearAll = chip(c, "Futa zote", keyBg, keyStroke, txt);
        clearAll.setTextSize(11);
        clearAll.setOnClickListener(v -> { ClipboardHistory.clear(c); l.changed(); });
        top.addView(clearAll, new LinearLayout.LayoutParams(-2, 42));
        root.addView(top);

        ScrollView sv = new ScrollView(c);
        LinearLayout list = new LinearLayout(c);
        list.setOrientation(LinearLayout.VERTICAL);

        List<String> items = ClipboardHistory.list(c);
        if (items.isEmpty()){
            TextView empty = new TextView(c);
            empty.setText("Bado hakuna kilichonakiliwa");
            empty.setTextColor(keyStroke);
            empty.setPadding(10,20,10,20);
            empty.setGravity(Gravity.CENTER);
            list.addView(empty);
        } else {
            for (int i = 0; i < items.size(); i++){
                final int idx = i;
                final String text = items.get(i);
                LinearLayout row = new LinearLayout(c);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);

                TextView t = new TextView(c);
                String preview = text.length() > 60 ? text.substring(0, 60) + "…" : text;
                t.setText(preview);
                t.setTextColor(txt);
                t.setTextSize(13);
                t.setPadding(10,10,6,10);
                t.setOnClickListener(v -> l.insert(text));
                row.addView(t, new LinearLayout.LayoutParams(0, -2, 1));

                TextView del = new TextView(c);
                del.setText("✕");
                del.setTextColor(Color.parseColor("#FF8C8C"));
                del.setTextSize(15);
                del.setPadding(14,6,14,6);
                del.setOnClickListener(v -> { ClipboardHistory.remove(c, idx); l.changed(); });
                row.addView(del, new LinearLayout.LayoutParams(-2, -2));

                list.addView(row);
                View divider = new View(c);
                divider.setBackgroundColor(keyStroke);
                list.addView(divider, new LinearLayout.LayoutParams(-1, 1));
            }
        }
        sv.addView(list);
        root.addView(sv, new LinearLayout.LayoutParams(-1, 190));
        return root;
    }

    private static TextView chip(Context c, String s, int keyBg, int keyStroke, int txt){
        TextView t = new TextView(c);
        t.setText(s);
        t.setGravity(Gravity.CENTER);
        t.setTextColor(txt);
        GradientDrawable g = new GradientDrawable();
        g.setColor(keyBg);
        g.setCornerRadius(14);
        g.setStroke(1, keyStroke);
        t.setBackground(g);
        return t;
    }
}
