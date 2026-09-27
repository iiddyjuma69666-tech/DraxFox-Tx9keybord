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
 box.addView(title("Clipboard",16));
 CheckBox clipHist=new CheckBox(this);clipHist.setText("Hifadhi Clipboard History (bonyeza 📋 muda mrefu kuiona)");clipHist.setTextColor(Color.WHITE);clipHist.setChecked(ClipboardHistory.enabled(this));clipHist.setOnCheckedChangeListener((b,c)->ClipboardHistory.setEnabled(this,c));box.addView(clipHist);
 Button clearClip=new Button(this);clearClip.setText("FUTA CLIPBOARD HISTORY YOTE");clearClip.setOnClickListener(v->{ClipboardHistory.clear(this);Toast.makeText(this,"Clipboard history imefutwa",Toast.LENGTH_SHORT).show();});box.addView(clearClip);
 box.addView(title("Muonekano (Theme)",16));
 Spinner themeSpin=new Spinner(this);
 String[] themeNames={"Aqua (bluu giza)","Light (mwangaza)","Carbon (nyeusi)","Forest (kijani)","Sunset (machweo)"};
 String[] themeKeys={"aqua","light","carbon","forest","sunset"};
 themeSpin.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,themeNames));
 int curThemeIdx=0;for(int i=0;i<themeKeys.length;i++)if(themeKeys[i].equals(prefs.theme()))curThemeIdx=i;
 themeSpin.setSelection(curThemeIdx);
 themeSpin.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){
  public void onNothingSelected(android.widget.AdapterView<?> a){}
  public void onItemSelected(android.widget.AdapterView<?> a,View v,int p,long id){prefs.set("theme",themeKeys[p]);DraxFoxKeyboardService live=DraxFoxKeyboardService.getInstance();if(live!=null)live.applyTheme();}
 });
 box.addView(themeSpin);

 box.addView(title("One-hand mode",16));
 Spinner handSpin=new Spinner(this);
 String[] handNames={"Normal","Mkono wa kushoto","Mkono wa kulia"};
 handSpin.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,handNames));
 handSpin.setSelection(prefs.handMode());
 handSpin.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){
  public void onNothingSelected(android.widget.AdapterView<?> a){}
  public void onItemSelected(android.widget.AdapterView<?> a,View v,int p,long id){prefs.set("hand_mode",p);DraxFoxKeyboardService live=DraxFoxKeyboardService.getInstance();if(live!=null)live.applyTheme();}
 });
 box.addView(handSpin);

 CheckBox hap=new CheckBox(this);hap.setText("Haptic feedback (mtetemo unapobonyeza key)");hap.setTextColor(Color.WHITE);hap.setChecked(prefs.haptic());hap.setOnCheckedChangeListener((b,c)->{prefs.set("haptic",c);DraxFoxKeyboardService live=DraxFoxKeyboardService.getInstance();if(live!=null)live.applyTheme();});box.addView(hap);
 CheckBox snd=new CheckBox(this);snd.setText("Sauti ya key (keypress sound)");snd.setTextColor(Color.WHITE);snd.setChecked(prefs.soundOn());snd.setOnCheckedChangeListener((b,c)->{prefs.set("sound_on",c);DraxFoxKeyboardService live=DraxFoxKeyboardService.getInstance();if(live!=null)live.applyTheme();});box.addView(snd);
 CheckBox wp=new CheckBox(this);wp.setText("Wallpaper ya Theme (background yenye rangi laini)");wp.setTextColor(Color.WHITE);wp.setChecked(prefs.wallpaperOn());wp.setOnCheckedChangeListener((b,c)->{prefs.set("wallpaper_on",c);DraxFoxKeyboardService live=DraxFoxKeyboardService.getInstance();if(live!=null)live.applyTheme();});box.addView(wp);
 Spinner lang=new Spinner(this);String[] ls={"Swahili","English"};lang.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,ls));lang.setSelection("en".equals(prefs.language())?1:0);lang.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> a){}public void onItemSelected(android.widget.AdapterView<?> a,View v,int p,long id){prefs.set("language",p==1?"en":"sw");}});box.addView(lang);
 TextView note=title("Correction rule: TX9 shows suggestions first. It does not silently replace your words unless you explicitly enable Auto-replace.",13);note.setTextColor(Color.LTGRAY);box.addView(note);
 ScrollView sv=new ScrollView(this);sv.addView(box);setContentView(sv);}
}
