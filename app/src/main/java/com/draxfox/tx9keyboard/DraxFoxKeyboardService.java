package com.draxfox.tx9keyboard;

import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.inputmethodservice.InputMethodService;import android.net.Uri;import android.os.*;import android.util.Log;import android.view.*;import android.view.inputmethod.*;import android.widget.*;import java.util.*;

public class DraxFoxKeyboardService extends InputMethodService {
 private static final String TAG="DraxFoxTX9";
 private static DraxFoxKeyboardService instance; private LinearLayout root,suggestRow,keys,panelBox; private PrefsManager prefs; private boolean shifted=false,symbols=false,emojis=false,clipboardOpen=false,isPasswordField=false;
 private final SuggestionEngine engine=new SuggestionEngine();
 private FontStyler.Style style=FontStyler.Style.NORMAL; private TextView aaBtn;
 private int bg,keyBg,keyStroke,txt,themeIndex=0,handMode=0; // handMode: 0 normal, 1 left, 2 right
 private boolean haptic=true,soundOn=false,wallpaperOn=false;
 private KeypressFeedback sound;
 private ClipboardManager.OnPrimaryClipChangedListener clipListener;
 private static final String[] THEME_NAMES={"aqua","light","carbon","forest","sunset"};
 // {bg, keyBg, keyStroke, text}
 private static final int[][] THEME_TABLE={
  {0xFF081018,0xFF1B2D38,0xFF37525E,0xFFFFFFFF}, // aqua
  {0xFFF0F6F9,0xFFFFFFFF,0xFFC6D4DB,0xFF0A141C}, // light
  {0xFF0D0D0D,0xFF1E1E1E,0xFF3A3A3A,0xFFFFFFFF}, // carbon
  {0xFF0C1A12,0xFF1E3326,0xFF335A42,0xFFEFFFF5}, // forest
  {0xFF1A0F1F,0xFF3A1F3A,0xFF55305A,0xFFFFF0F5}, // sunset
 };

 // ---------- Lifecycle (crash-safety: an IME crash takes away the user's keyboard system-wide) ----------
 @Override public void onCreate(){
  super.onCreate();instance=this;
  try{
   prefs=new PrefsManager(this);
   ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
   if(cm!=null){
    clipListener=()->{
     try{
      if(!ClipboardHistory.enabled(this))return;
      ClipData d=cm.getPrimaryClip();
      if(d!=null&&d.getItemCount()>0){CharSequence t=d.getItemAt(0).coerceToText(this);if(t!=null)ClipboardHistory.add(this,t.toString());}
     }catch(Exception e){Log.e(TAG,"clip listener error",e);}
    };
    cm.addPrimaryClipChangedListener(clipListener);
   }
   sound=new KeypressFeedback(this);
  }catch(Exception e){Log.e(TAG,"onCreate: prefs init failed",e);}
 }
 @Override public void onDestroy(){
  try{ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);if(cm!=null&&clipListener!=null)cm.removePrimaryClipChangedListener(clipListener);}catch(Exception ignored){}
  try{if(sound!=null)sound.release();}catch(Exception ignored){}
  instance=null;super.onDestroy();
 }
 public static DraxFoxKeyboardService getInstance(){return instance;}

 @Override public View onCreateInputView(){
  try{ if(prefs==null)prefs=new PrefsManager(this); initTheme(); return build(); }
  catch(Exception e){ Log.e(TAG,"onCreateInputView failed, showing fallback view",e); return fallbackView(); }
 }

 /** Minimal, crash-proof view shown only if something above throws — keeps the user able to at least close/switch keyboards instead of losing input entirely. */
 private View fallbackView(){
  TextView t=new TextView(this); t.setText("TX9 imepata hitilafu. Fungua Settings za simu → Language & input → badilisha keyboard, kisha jaribu tena.");
  t.setPadding(24,24,24,24); t.setTextColor(Color.WHITE); t.setBackgroundColor(Color.BLACK); return t;
 }

 @Override public void onStartInputView(EditorInfo info, boolean restarting){
  super.onStartInputView(info, restarting);
  try{
   isPasswordField = isPasswordField(info);
   symbols=false;
   refreshAll();
  }catch(Exception e){ Log.e(TAG,"onStartInputView error",e); }
 }

 @Override public void onFinishInputView(boolean finishingInput){
  super.onFinishInputView(finishingInput);
  try{ if(emojis||clipboardOpen){ emojis=false;clipboardOpen=false; if(panelBox!=null)panelBox.setVisibility(View.GONE); if(keys!=null)keys.setVisibility(View.VISIBLE); } }
  catch(Exception e){ Log.e(TAG,"onFinishInputView error",e); }
 }

 @Override public void onConfigurationChanged(android.content.res.Configuration newConfig){
  try{ super.onConfigurationChanged(newConfig); }
  catch(Exception e){ Log.e(TAG,"onConfigurationChanged error",e); }
 }

 private boolean isPasswordField(EditorInfo info){
  if(info==null) return false;
  int cls=info.inputType & EditorInfo.TYPE_MASK_CLASS;
  int var=info.inputType & EditorInfo.TYPE_MASK_VARIATION;
  if(cls==EditorInfo.TYPE_CLASS_TEXT && (var==EditorInfo.TYPE_TEXT_VARIATION_PASSWORD||var==EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD||var==EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) return true;
  if(cls==EditorInfo.TYPE_CLASS_NUMBER && var==EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD) return true;
  return false;
 }

 // ---------- Theme ----------
 private void initTheme(){
  themeIndex=0;for(int i=0;i<THEME_NAMES.length;i++)if(THEME_NAMES[i].equals(prefs.theme())){themeIndex=i;break;}
  bg=THEME_TABLE[themeIndex][0];keyBg=THEME_TABLE[themeIndex][1];keyStroke=THEME_TABLE[themeIndex][2];txt=THEME_TABLE[themeIndex][3];
  handMode=prefs.handMode();haptic=prefs.haptic();soundOn=prefs.soundOn();wallpaperOn=prefs.wallpaperOn();
 }
 private int wallpaperRes(){
  switch(themeIndex){
   case 1: return R.drawable.wallpaper_light;
   case 2: return R.drawable.wallpaper_carbon;
   case 3: return R.drawable.wallpaper_forest;
   case 4: return R.drawable.wallpaper_sunset;
   default: return R.drawable.wallpaper_aqua;
  }
 }
 /** Called from SettingsActivity when theme/hand-mode changes, so the keyboard updates live without needing to reopen it. */
 public void applyTheme(){try{if(prefs==null)return;initTheme();setInputView(build());}catch(Exception e){Log.e(TAG,"applyTheme error",e);}}

 private TextView key(String label,int weight){TextView b=new TextView(this);b.setText(label);b.setTextColor(txt);b.setTextSize(17);b.setGravity(Gravity.CENTER);b.setTypeface(null,Typeface.BOLD);b.setPadding(2,2,2,2);GradientDrawable g=new GradientDrawable();g.setColor(keyBg);g.setCornerRadius(15);g.setStroke(1,keyStroke);b.setBackground(g);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,52,weight);lp.setMargins(3,3,3,3);b.setLayoutParams(lp);MotionEffects.attachPress(b);b.setOnClickListener(v->{if(haptic)v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);if(soundOn&&sound!=null)sound.play("SPACE".equals(label));safePress(label);});return b;}
 private LinearLayout row(){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);r.setGravity(Gravity.CENTER);return r;}

 private View build(){
  root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(5,5,5,5);
  if(wallpaperOn){try{root.setBackgroundResource(wallpaperRes());}catch(Exception e){root.setBackgroundColor(bg);}}else{root.setBackgroundColor(bg);}
  LinearLayout toolbar=row(); toolbar.addView(key("😀",1));toolbar.addView(key("📋",1));aaBtn=key(FontStyler.shortLabel(style),1);toolbar.addView(aaBtn);toolbar.addView(key("文",1));toolbar.addView(key("🤚",1));toolbar.addView(key("⚙",1));root.addView(toolbar,new LinearLayout.LayoutParams(-1,48));
  ((TextView)toolbar.getChildAt(0)).setOnClickListener(v->toggleEmoji());
  ((TextView)toolbar.getChildAt(1)).setOnClickListener(v->safe(this::pasteFromClipboard));
  ((TextView)toolbar.getChildAt(1)).setOnLongClickListener(v->{toggleClipboardHistory();return true;});
  aaBtn.setOnClickListener(v->safe(this::cycleFont));
  ((TextView)toolbar.getChildAt(3)).setOnClickListener(v->safe(this::translateSelected));
  ((TextView)toolbar.getChildAt(4)).setOnClickListener(v->safe(this::cycleHandMode));
  ((TextView)toolbar.getChildAt(5)).setOnClickListener(v->startActivity(new Intent(this,SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
  suggestRow=row();root.addView(suggestRow,new LinearLayout.LayoutParams(-1,46));
  keys=new LinearLayout(this);keys.setOrientation(LinearLayout.VERTICAL);
  LinearLayout keysWrap=row();
  if(handMode==1){ // left-hand: keyboard on the left, empty space on the right
   keysWrap.addView(keys,new LinearLayout.LayoutParams(0,-2,7));
   View sp=new View(this);keysWrap.addView(sp,new LinearLayout.LayoutParams(0,-2,3));
  } else if(handMode==2){ // right-hand: empty space on the left, keyboard on the right
   View sp=new View(this);keysWrap.addView(sp,new LinearLayout.LayoutParams(0,-2,3));
   keysWrap.addView(keys,new LinearLayout.LayoutParams(0,-2,7));
  } else {
   keysWrap.addView(keys,new LinearLayout.LayoutParams(-1,-2));
  }
  root.addView(keysWrap,new LinearLayout.LayoutParams(-1,-2));
  panelBox=new LinearLayout(this);panelBox.setVisibility(View.GONE);root.addView(panelBox,new LinearLayout.LayoutParams(-1,240));refreshAll();return root;
 }

 /** 🤚 button: cycles Normal → Left-hand → Right-hand → Normal. Rebuilds the view since the
  *  layout structure (spacer + keyboard width) changes, not just colors. */
 private void cycleHandMode(){
  handMode=(handMode+1)%3;prefs.set("hand_mode",handMode);
  String label=handMode==0?"Normal":handMode==1?"Mkono wa kushoto":"Mkono wa kulia";
  Toast.makeText(this,"Mode: "+label,Toast.LENGTH_SHORT).show();
  setInputView(build());
 }

 private void refreshAll(){safe(this::refreshSuggestions);safe(this::refreshKeys);}

 private void refreshSuggestions(){
  if(suggestRow==null)return; suggestRow.removeAllViews();
  if(!prefs.suggestions()||isPasswordField){ suggestRow.setVisibility(View.GONE); return; } // never suggest inside password fields
  suggestRow.setVisibility(View.VISIBLE);
  InputConnection ic=getCurrentInputConnection();String word="";
  if(ic!=null){ CharSequence before=ic.getTextBeforeCursor(60,0); String s=before==null?"":before.toString(); String[] p=s.split("\\s+"); if(p.length>0)word=p[p.length-1]; }
  for(String s:engine.suggest(word,prefs.language())){TextView t=key(s,1);t.setTextSize(13);t.setOnClickListener(v->safe(()->acceptSuggestion(s,word)));suggestRow.addView(t);}
  TextView st=key("STICKER",1);st.setTextSize(9);st.setOnClickListener(v->openStickerPicker());suggestRow.addView(st);
 }

 private void acceptSuggestion(String s,String typed){InputConnection ic=getCurrentInputConnection();if(ic==null)return;ic.deleteSurroundingText(typed.length(),0);ic.commitText(s+" ",1);refreshSuggestions();}

 private void refreshKeys(){
  if(keys==null)return; keys.removeAllViews();
  if(emojis||clipboardOpen){keys.setVisibility(View.GONE);return;} keys.setVisibility(View.VISIBLE);
  String[][] q=symbols?new String[][]{{"1","2","3","4","5","6","7","8","9","0"},{"@","#","$","%","&","*","-","+","(",")"},{"!","?","/","\"",":",";","_",".","⌫"},{"ABC","🌐","SPACE","↵"}}:new String[][]{{"q","w","e","r","t","y","u","i","o","p"},{"a","s","d","f","g","h","j","k","l"},{"⇧","z","x","c","v","b","n","m","⌫"},{"🌐","123","SPACE","↵"}};
  for(String[] rr:q){LinearLayout r=row();for(String x:rr){int wt=x.equals("SPACE")?4:(x.equals("⇧")||x.equals("⌫")||x.equals("123")||x.equals("ABC")||x.equals("↵")?2:1);r.addView(key(display(x),wt));}keys.addView(r);}
 }

 private String display(String x){return shifted&&x.length()==1&&Character.isLetter(x.charAt(0))?x.toUpperCase(Locale.ROOT):x;}

 private void safePress(String x){ safe(()->press(x)); }

 private void press(String x){
  InputConnection ic=getCurrentInputConnection(); if(ic==null){ Log.w(TAG,"press(): no InputConnection"); return; }
  switch(x){
   case"⌫":ic.deleteSurroundingText(1,0);break;
   case"SPACE":if(!isPasswordField)autoReplaceIfNeeded(ic);ic.commitText(" ",1);break;
   case"↵":ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_ENTER));break;
   case"⇧":shifted=!shifted;refreshKeys();break;
   case"123":symbols=true;refreshKeys();break;
   case"ABC":symbols=false;refreshKeys();break;
   case"🌐":prefs.set("language",prefs.language().equals("sw")?"en":"sw");break;
   default:String out=display(x);if(out.length()==1&&(Character.isLetter(out.charAt(0))||Character.isDigit(out.charAt(0))))out=FontStyler.apply(out,style);ic.commitText(out,1);if(shifted){shifted=false;refreshKeys();}break;
  }
  refreshSuggestions();
 }

 /** Auto-replace: fires only when the Settings toggle is ON, never inside password fields, and only on a known exact typo (never a guess). */
 private void autoReplaceIfNeeded(InputConnection ic){
  if(!prefs.autoReplace()||isPasswordField)return;
  CharSequence before=ic.getTextBeforeCursor(40,0);if(before==null)return;
  String s=before.toString();String[] p=s.split("\\s+");if(p.length==0)return;
  String word=p[p.length-1];if(word.isEmpty())return;
  String fix=engine.autoCorrect(word,prefs.language());
  if(fix!=null){ic.deleteSurroundingText(word.length(),0);ic.commitText(fix,1);}
 }

 /** Aa button: with a selection, restyles that selection right away; with no selection, cycles the style used for future typing. */
 private void cycleFont(){
  InputConnection ic=getCurrentInputConnection();CharSequence sel=ic==null?null:ic.getSelectedText(0);
  if(sel!=null&&sel.length()>0){ic.commitText(FontStyler.apply(sel.toString(),style),1);Toast.makeText(this,"Style imetumika kwa maneno uliyochagua",Toast.LENGTH_SHORT).show();return;}
  style=FontStyler.next(style);if(aaBtn!=null)aaBtn.setText(FontStyler.shortLabel(style));Toast.makeText(this,"Font: "+FontStyler.fullLabel(style)+" — herufi zijazo zitabadilika",Toast.LENGTH_SHORT).show();
 }

 private void toggleEmoji(){ safe(()->{ clipboardOpen=false; emojis=!emojis; renderPanel(); }); }
 private void toggleClipboardHistory(){ safe(()->{ emojis=false; clipboardOpen=!clipboardOpen; renderPanel(); }); }

 /** Draws whichever panel (emoji, clipboard, or neither) matches current state into panelBox. */
 private void renderPanel(){
  panelBox.removeAllViews();
  if(emojis){
   panelBox.setVisibility(View.VISIBLE);keys.setVisibility(View.GONE);
   View ep=EmojiPanel.create(this,bg,keyBg,keyStroke,txt,new EmojiPanel.Listener(){
    public void emoji(String e){InputConnection ic=getCurrentInputConnection();if(ic!=null)ic.commitText(e,1);}
    public void pickSticker(){openStickerPicker();}
    public void insertSticker(Uri uri){DraxFoxKeyboardService.this.insertSticker(uri);}
   });
   panelBox.addView(ep);MotionEffects.enter(ep,0);
  } else if(clipboardOpen){
   panelBox.setVisibility(View.VISIBLE);keys.setVisibility(View.GONE);
   View cp=ClipboardPanel.create(this,bg,keyBg,keyStroke,txt,new ClipboardPanel.Listener(){
    public void insert(String text){InputConnection ic=getCurrentInputConnection();if(ic!=null)ic.commitText(text,1);}
    public void changed(){renderPanel();}
   });
   panelBox.addView(cp);MotionEffects.enter(cp,0);
  } else {
   panelBox.setVisibility(View.GONE);keys.setVisibility(View.VISIBLE);
  }
 }

 private void openStickerPicker(){try{startActivity(new Intent(this,StickerPickerActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));}catch(Exception e){Log.e(TAG,"openStickerPicker failed",e);Toast.makeText(this,"Imeshindikana kufungua sticker picker",Toast.LENGTH_SHORT).show();}}

 public void insertSticker(Uri uri){
  InputConnection ic=getCurrentInputConnection();if(ic==null||uri==null)return;
  try{
   ClipDescription d=new ClipDescription("DraxFox TX9 Sticker",new String[]{getContentResolver().getType(uri)!=null?getContentResolver().getType(uri):"image/*"});
   InputContentInfo info=new InputContentInfo(uri,d,null);
   int flags=InputConnection.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
   boolean ok=ic.commitContent(info,flags,null);
   if(!ok){
    ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
    cm.setPrimaryClip(ClipData.newUri(getContentResolver(),"TX9 Sticker",uri));
    Toast.makeText(this,"App hii haipokei sticker moja kwa moja. Image imewekwa Clipboard.",Toast.LENGTH_LONG).show();
   } else Toast.makeText(this,"Sticker imeingizwa",Toast.LENGTH_SHORT).show();
  }catch(Exception e){ Log.e(TAG,"insertSticker failed",e); Toast.makeText(this,"Sticker haikuingizwa: "+e.getMessage(),Toast.LENGTH_LONG).show(); }
 }

 private void pasteFromClipboard(){
  ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
  if(cm!=null&&cm.hasPrimaryClip()){
   ClipData d=cm.getPrimaryClip();
   if(d!=null&&d.getItemCount()>0){ CharSequence t=d.getItemAt(0).coerceToText(this); InputConnection ic=getCurrentInputConnection(); if(ic!=null)ic.commitText(t,1); }
  }
 }

 private void translateSelected(){
  InputConnection ic=getCurrentInputConnection();if(ic==null)return;
  CharSequence sel=ic.getSelectedText(0);
  if(sel==null||sel.length()==0){ sel=ic.getTextBeforeCursor(120,0); }
  if(sel==null||sel.length()==0){ Toast.makeText(this,"Andika au chagua maneno kwanza",Toast.LENGTH_SHORT).show(); return; }
  String from=prefs.language(),to=from.equals("sw")?"en":"sw";
  String out=TranslatorEngine.offline(sel.toString(),from,to);
  ic.commitText(out,1);
 }

 // ---------- crash guard ----------
 private interface Action { void run(); }
 private void safe(Action a){ try{ a.run(); }catch(Exception e){ Log.e(TAG,"Unhandled error, keyboard kept alive",e); } }
}
