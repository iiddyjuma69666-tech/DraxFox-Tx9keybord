package com.draxfox.tx9keyboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.InputStream;
import java.util.List;

/** Emoji + Sticker panel: real categories (Recent / Smileys / People / Nature / Symbols)
 *  plus a persistent Sticker Library tab (images the user picked before, reusable without
 *  re-opening the file picker every time). Colors are passed in from the current theme so
 *  the panel always matches whichever of the 5 TX9 themes is active. */
public final class EmojiPanel {
    private static final String TAG = "DraxFoxTX9";
    public interface Listener { void emoji(String e); void pickSticker(); void insertSticker(Uri uri); }

    private static final String[] SMILEYS = {"😀","😃","😄","😁","😆","😅","😂","🤣","😊","😇","🙂","🙃","😉","😌","😍","🥰","😘","😗","😎","🤩","🤔","😴","🤗","🤭","🤫","😐","😶","🙄","😏","😣","😥","😮","🤐","😯","😪","😫","🥱","😛","😜","🤪","🤨","🧐","🤓","😕","😟","🙁","☹️","😲","😳","🥺","😭","😤","😡","🤬","🤯","😱","😨","😰","😓","🤠","🥳","😈","👿","💀","☠️","👻","👽","🤖","💩"};
    private static final String[] PEOPLE  = {"👍","👎","👏","🙌","🙏","🤝","👌","✌️","🤞","🤟","🤘","👊","✊","💪","👋","👀","🫶","💋","💅","🙋","🙆","🙅","🧑","👨","👩","🧒","👶","🧓","👴","👵","💃","🕺"};
    private static final String[] NATURE  = {"🔥","✨","⭐","🌟","☀️","🌙","🌧️","🌊","🌸","🌹","🌻","🌴","🍀","🌈","⚡","❄️","🐶","🐱","🐘","🦁","🐒","🐦","🐟","🦋","🍎","🍌","🍕","🍔","🍟","☕","🍫","🍉"};
    private static final String[] SYMBOLS = {"❤️","🧡","💛","💚","💙","💜","🖤","🤍","🤎","💔","💕","💖","💗","💘","💝","💯","🎉","🎊","🎂","🎁","⚽","🏆","🚗","✈️","🌍","💤","💢","💬","🔔","📌"};

    private static String[] category(int i){
        switch (i){ case 0: return SMILEYS; case 1: return PEOPLE; case 2: return NATURE; default: return SYMBOLS; }
    }

    /** bg/keyBg/keyStroke/txt come straight from the active TX9 theme's color set. */
    public static View create(Context c, int bg, int keyBg, int keyStroke, int txt, Listener l){
        LinearLayout root = new LinearLayout(c);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(5,4,5,4);
        root.setBackgroundColor(bg);

        LinearLayout top = new LinearLayout(c);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView pick = chip(c, "＋ CHAGUA STICKER MPYA", keyBg, keyStroke, txt);
        pick.setTextSize(11);
        pick.setOnClickListener(v -> l.pickSticker());
        top.addView(pick, new LinearLayout.LayoutParams(-2, 40));
        root.addView(top);

        LinearLayout tabs = new LinearLayout(c);
        tabs.setGravity(Gravity.CENTER);
        String[] tabIcons = {"🕓","😀","👋","🌿","💯","🖼️"}; // Recent, Smileys, People, Nature, Symbols, Stickers
        FrameLayout gridHolder = new FrameLayout(c);

        for (int i = 0; i < tabIcons.length; i++){
            final int catIndex = i - 1; // -1 recent, 0..3 categories, 4 stickers
            TextView tab = chip(c, tabIcons[i], keyBg, keyStroke, txt);
            tab.setTextSize(14);
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, 40, 1);
            tp.setMargins(2,2,2,2);
            tab.setLayoutParams(tp);
            tab.setOnClickListener(v -> renderGrid(gridHolder, c, keyBg, keyStroke, txt, catIndex, l));
            tabs.addView(tab);
        }
        root.addView(tabs);

        renderGrid(gridHolder, c, keyBg, keyStroke, txt, -1, l); // open on Recent by default
        root.addView(gridHolder, new LinearLayout.LayoutParams(-1, 150));
        return root;
    }

    private static void renderGrid(FrameLayout holder, Context c, int keyBg, int keyStroke, int txt, int catIndex, Listener l){
        holder.removeAllViews();

        if (catIndex == 4){ renderStickers(holder, c, keyStroke, txt, l); return; }

        String[] set = catIndex == -1 ? EmojiRepository.recent(c).toArray(new String[0]) : category(catIndex);
        if (set.length == 0){
            holder.addView(emptyLabel(c, "Bado hakuna emoji zilizotumika hivi karibuni", keyStroke));
            return;
        }
        ScrollView sv = new ScrollView(c);
        GridLayout grid = new GridLayout(c);
        grid.setColumnCount(8);
        for (String e : set){
            TextView b = chip(c, e, keyBg, keyStroke, txt);
            b.setTextSize(25);
            b.setOnClickListener(v -> { EmojiRepository.addRecent(c, e); l.emoji(e); });
            GridLayout.LayoutParams gp = new GridLayout.LayoutParams();
            gp.width = 0; gp.height = 52; gp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            grid.addView(b, gp);
        }
        sv.addView(grid);
        holder.addView(sv);
    }

    private static void renderStickers(FrameLayout holder, Context c, int keyStroke, int txt, Listener l){
        List<String> saved = StickerLibrary.list(c);
        if (saved.isEmpty()){
            holder.addView(emptyLabel(c, "Bado hakuna sticker iliyohifadhiwa. Bonyeza ‘+ CHAGUA STICKER MPYA’.", keyStroke));
            return;
        }
        ScrollView sv = new ScrollView(c);
        GridLayout grid = new GridLayout(c);
        grid.setColumnCount(4);
        for (int i = 0; i < saved.size(); i++){
            final int idx = i;
            Uri uri = Uri.parse(saved.get(i));
            ImageView iv = new ImageView(c);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Bitmap bmp = loadThumbnail(c, uri, 160);
            if (bmp != null) iv.setImageBitmap(bmp); else iv.setImageDrawable(null);
            iv.setOnClickListener(v -> l.insertSticker(uri));
            iv.setOnLongClickListener(v -> { StickerLibrary.remove(c, idx); renderStickers(holder, c, keyStroke, txt, l); return true; });
            GridLayout.LayoutParams gp = new GridLayout.LayoutParams();
            gp.width = 0; gp.height = 90; gp.setMargins(3,3,3,3); gp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            grid.addView(iv, gp);
        }
        sv.addView(grid);
        holder.addView(sv);
    }

    private static Bitmap loadThumbnail(Context c, Uri uri, int targetPx){
        try (InputStream is = c.getContentResolver().openInputStream(uri)){
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, bounds);
            int sample = 1;
            while ((bounds.outWidth / sample) > targetPx * 2) sample *= 2;
            try (InputStream is2 = c.getContentResolver().openInputStream(uri)){
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = sample;
                return BitmapFactory.decodeStream(is2, null, opts);
            }
        } catch (Exception e) {
            Log.e(TAG, "sticker thumbnail load failed (file may have been moved/deleted)", e);
            return null;
        }
    }

    private static TextView emptyLabel(Context c, String text, int mutedColor){
        TextView empty = new TextView(c);
        empty.setText(text);
        empty.setTextColor(mutedColor);
        empty.setPadding(10,20,10,20);
        empty.setGravity(Gravity.CENTER);
        return empty;
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
