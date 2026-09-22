package com.draxfox.tx9keyboard;
import android.content.Context;
import android.content.SharedPreferences;
public final class PrefsManager {
 private final SharedPreferences p; public PrefsManager(Context c){p=c.getSharedPreferences("draxfox_prefs",Context.MODE_PRIVATE);}
 public boolean suggestions(){return p.getBoolean("suggestions",true);} public boolean autoReplace(){return p.getBoolean("auto_replace",false);}
 public String language(){return p.getString("language","sw");} public String theme(){return p.getString("theme","aqua");}
 public void set(String k,Object v){SharedPreferences.Editor e=p.edit(); if(v instanceof Boolean)e.putBoolean(k,(Boolean)v); else e.putString(k,String.valueOf(v));e.apply();}
}
