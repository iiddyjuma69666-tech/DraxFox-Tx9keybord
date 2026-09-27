package com.draxfox.tx9keyboard;
import android.content.Context;
import android.content.SharedPreferences;
public final class PrefsManager {
 private final SharedPreferences p; public PrefsManager(Context c){p=c.getSharedPreferences("draxfox_prefs",Context.MODE_PRIVATE);}
 public boolean suggestions(){return p.getBoolean("suggestions",true);} public boolean autoReplace(){return p.getBoolean("auto_replace",false);}
 public String language(){return p.getString("language","sw");} public String theme(){return p.getString("theme","aqua");}
 public int handMode(){return p.getInt("hand_mode",0);} public boolean haptic(){return p.getBoolean("haptic",true);} public boolean soundOn(){return p.getBoolean("sound_on",false);} public boolean wallpaperOn(){return p.getBoolean("wallpaper_on",false);}
 public void set(String k,Object v){SharedPreferences.Editor e=p.edit(); if(v instanceof Boolean)e.putBoolean(k,(Boolean)v); else if(v instanceof Integer)e.putInt(k,(Integer)v); else e.putString(k,String.valueOf(v));e.apply();}
}
