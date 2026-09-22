package com.draxfox.tx9keyboard;
import android.app.*;import android.os.*;import android.content.*;import android.graphics.Color;import android.view.*;import android.view.inputmethod.InputMethodManager;import android.widget.*;
public class SettingsActivity extends Activity{
 LinearLayout box; PrefsManager prefs;
 @Override public void onCreate(Bundle b){super.onCreate(b);prefs=new PrefsManager(this); show();}
 TextView title(String s,int z){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(z);t.setPadding(18,18,18,10);return t;}
 void show(){box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(18,30,18,18);box.setBackgroundColor(Color.rgb(8,16,24));box.addView(title("TX9",38));box.addView(title("DraxFox TX9 Keyboard",22));box.addView(title("Real Android keyboard • suggestions • translator",14));
 Button enable=new Button(this);enable.setText("ENABLE / CHOOSE KEYBOARD");enable.setOnClickListener(v->{startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));});box.addView(enable);
 CheckBox sug=new CheckBox(this);sug.setText("Show word suggestions");sug.setTextColor(Color.WHITE);sug.setChecked(prefs.suggestions());sug.setOnCheckedChangeListener((b,c)->prefs.set("suggestions",c));box.addView(sug);
 CheckBox auto=new CheckBox(this);auto.setText("Auto-replace words (OFF by default)");auto.setTextColor(Color.WHITE);auto.setChecked(prefs.autoReplace());auto.setOnCheckedChangeListener((b,c)->prefs.set("auto_replace",c));box.addView(auto);
 CheckBox thm=new CheckBox(this);thm.setText("Light theme (bofya kubadilisha rangi ya keyboard)");thm.setTextColor(Color.WHITE);thm.setChecked("light".equals(prefs.theme()));thm.setOnCheckedChangeListener((b,c)->{prefs.set("theme",c?"light":"aqua");DraxFoxKeyboardService live=DraxFoxKeyboardService.getInstance();if(live!=null)live.applyTheme();});box.addView(thm);
 Spinner lang=new Spinner(this);String[] ls={"Swahili","English"};lang.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,ls));lang.setSelection("en".equals(prefs.language())?1:0);lang.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> a){}public void onItemSelected(android.widget.AdapterView<?> a,View v,int p,long id){prefs.set("language",p==1?"en":"sw");}});box.addView(lang);
 TextView note=title("Correction rule: TX9 shows suggestions first. It does not silently replace your words unless you explicitly enable Auto-replace.",13);note.setTextColor(Color.LTGRAY);box.addView(note);setContentView(box);}
}
