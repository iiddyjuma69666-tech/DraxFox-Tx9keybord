package com.draxfox.tx9keyboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** First screen the user sees when opening the app — brand identity + quick actions.
 *  Settings/feature configuration lives in SettingsActivity, reached from here. */
public class MainActivity extends Activity {
    private static final int ACCENT = 0xFF2E8FFF; // metallic blue — matches the TX9 launcher icon
    private static final int BG     = 0xFF081018;
    private static final int CARD   = 0xFF13202B;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40, 40, 40, 40);

        // ---- Logo block ----
        TextView logo = new TextView(this);
        logo.setText("TX9");
        logo.setTextColor(ACCENT);
        logo.setTextSize(64);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        root.addView(logo);

        TextView sub = new TextView(this);
        sub.setText("K E Y B O A R D");
        sub.setTextColor(Color.WHITE);
        sub.setTextSize(20);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 4, 0, 0);
        root.addView(sub);

        TextView tagline = new TextView(this);
        tagline.setText("Kiswahili ↔ English • Suggestions • Stickers • Themes");
        tagline.setTextColor(0xFF8FA6B3);
        tagline.setTextSize(13);
        tagline.setGravity(Gravity.CENTER);
        tagline.setPadding(0, 14, 0, 50);
        root.addView(tagline);

        // ---- Action buttons ----
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 130);
        btnLp.setMargins(0, 0, 0, 20);

        TextView enable = actionButton("⌨  ENABLE / CHOOSE TX9 KEYBOARD", ACCENT, Color.WHITE);
        enable.setLayoutParams(btnLp);
        enable.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        root.addView(enable);

        TextView settings = actionButton("⚙  SETTINGS", CARD, Color.WHITE);
        LinearLayout.LayoutParams btnLp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 130);
        settings.setLayoutParams(btnLp2);
        settings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        root.addView(settings);

        // ---- Footer ----
        View spacer = new View(this);
        root.addView(spacer, new LinearLayout.LayoutParams(0, 0, 1)); // pushes footer down if screen is tall

        TextView footer = new TextView(this);
        footer.setText("Powered by DraxX9");
        footer.setTextColor(0xFF5A7A8C);
        footer.setTextSize(12);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, 40, 0, 0);
        root.addView(footer);

        setContentView(root);
    }

    private TextView actionButton(String label, int bgColor, int textColor){
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextColor(textColor);
        t.setTextSize(15);
        t.setTypeface(null, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        GradientDrawable g = new GradientDrawable();
        g.setColor(bgColor);
        g.setCornerRadius(24);
        if (bgColor == CARD) g.setStroke(2, 0xFF2A3F4B);
        t.setBackground(g);
        return t;
    }
}
