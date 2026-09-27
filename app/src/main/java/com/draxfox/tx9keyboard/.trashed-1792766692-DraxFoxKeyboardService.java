package com.draxfox.tx9keyboard;

import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.inputmethodservice.InputMethodService;import android.net.Uri;import android.os.*;import android.view.*;import android.view.inputmethod.*;import android.widget.*;import java.util.*;

public class DraxFoxKeyboardService extends InputMethodService {
 private static DraxFoxKeyboardService instance; private LinearLayout root,suggestRow,keys,emojiBox; private PrefsManager prefs; private boolean shifted=false,symbols=false,emojis=false,light=false;
 private final SuggestionEngine engine=new SuggestionEngine();
 private FontStyler.Style style=FontStyler.Style.NORMAL; private TextView aaBtn;
 private int bg,keyBg,keyStroke,txt;
 @Override public void onCreate(){super.onCreate();instance=this;}
 @Override public void onDestroy(){instance=null;super.onDestroy();}
 public static DraxFoxKeyboardService getInstance(){return instance;}
 @Override public View onCreateInputView(){prefs=new PrefsManager(this);initTheme();return build();}
 private void initTheme(){light="light".equals(prefs.theme());bg=light?Color.rgb(240,246,249):Color.rgb(8,16,24);keyBg=light?Color.rgb(255,255,255):Color.rgb(27,45,56);keyStroke=light?Color.rgb(198,212,219):Color.rgb(55,82,94);txt=light?Color.rgb(10,20,28):Color.WHITE;}
 /** Called from SettingsActivity when the theme is toggled, so the keyboard updates live without needing to reopen it. */
 public void applyTheme(){if(prefs==null)return;initTheme();setInputView(build());}
 private TextView key(String label,int weight){TextView b=new TextView(this);b.setText(label);b.setTextColor(txt);b.setTextSize(17);b.setGravity(Gravity.CENTER);b.setTypeface(null,Typeface.BOLD);b.setPadding(2,2,2,2);GradientDrawable g=new GradientDrawable();g.setColor(keyBg);g.setCornerRadius(15);g.setStroke(1,keyStroke);b.setBackground(g);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,52,weight);lp.setMargins(3,3,3,3);b.setLayoutParams(lp);b.setOnClickListener(v->press(label));return b;}
 private LinearLayout row(){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);r.setGravity(Gravity.CENTER);return r;}
 private View build(){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(5,5,5,5);root.setBackgroundColor(bg);
  LinearLayout toolbar=row(); toolbar.addView(key("😀",1));toolbar.addView(key("📋",1));aaBtn=key(FontStyler.shortLabel(style),1);toolbar.addView(aaBtn);toolbar.addView(key("文",1));toolbar.addView(key("⚙",1));root.addView(toolbar,new LinearLayout.LayoutParams(-1,48));
  ((TextView)toolbar.getChildAt(0)).setOnClickListener(v->toggleEmoji());
  ((TextView)toolbar.getChildAt(1)).setOnClickListener(v->pasteFromClipboard());
  aaBtn.setOnClickListener(v->cycleFont());
  ((TextView)toolbar.getChildAt(3)).setOnClickListener(v->translateSelected());
  ((TextView)toolbar.getChildAt(4)).setOnClickListener(v->startActivity(new Intent(this,SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
  suggestRow=row();root.addView(suggestRow,new LinearLayout.LayoutParams(-1,46));keys=new LinearLayout(this);keys.setOrientation(LinearLayout.VERTICAL);root.addView(keys,new LinearLayout.LayoutParams(-1,-2));emojiBox=new LinearLayout(this);emojiBox.setVisibility(View.GONE);root.addView(emojiBox,new LinearLayout.LayoutParams(-1,240));refreshAll();return root; }
 private void refreshAll(){refreshSuggestions();refreshKeys();}
 private void refreshSuggestions(){suggestRow.removeAllViews();if(!prefs.suggestions()){suggestRow.setVisibility(View.GONE);return;}suggestRow.setVisibility(View.VISIBLE);InputConnection ic=getCurrentInputConnection();String word="";if(ic!=null){CharSequence before=ic.getTextBeforeCursor(60,0);String s=before==null?"":before.toString();String[] p=s.split("\\s+");if(p.length>0)word=p[p.length-1];}
  for(String s:engine.suggest(word,prefs.language())){TextView t=key(s,1);t.setTextSize(13);t.setOnClickListener(v->acceptSuggestion(s,word));suggestRow.addView(t);}TextView st=key("STICKER",1);st.setTextSize(9);st.setOnClickListener(v->openStickerPicker());suggestRow.addView(st);}
 private void acceptSuggestion(String s,String typed){InputConnection ic=getCurrentInputConnection();if(ic==null)return;ic.deleteSurroundingText(typed.length(),0);ic.commitText(s+" ",1);refreshSuggestions();}
 private void refreshKeys(){keys.removeAllViews();if(emojis){keys.setVisibility(View.GONE);return;}keys.setVisibility(View.VISIBLE);String[][] q=symbols?new String[][]{{"1","2","3","4","5","6","7","8","9","0"},{"@","#","$","%","&","*","-","+","(",")"},{"!","?","/","\"",":",";","_",".","⌫"},{"ABC","🌐","SPACE","↵"}}:new String[][]{{"q","w","e","r","t","y","u","i","o","p"},{"a","s","d","f","g","h","j","k","l"},{"⇧","z","x","c","v","b","n","m","⌫"},{"🌐","123","SPACE","↵"}};for(String[] rr:q){LinearLayout r=row();for(String x:rr){int wt=x.equals("SPACE")?4:(x.equals("⇧")||x.equals("⌫")||x.equals("123")||x.equals("ABC")||x.equals("↵")?2:1);r.addView(key(display(x),wt));}keys.addView(r);}}
 private String display(String x){return shifted&&x.length()==1&&Character.isLetter(x.charAt(0))?x.toUpperCase(Locale.ROOT):x;}
 private void press(String x){InputConnection ic=getCurrentInputConnection();if(ic==null)return;switch(x){
  case"⌫":ic.deleteSurroundingText(1,0);break;
  case"SPACE":autoReplaceIfNeeded(ic);ic.commitText(" ",1);break;
  case"↵":ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_ENTER));break;
  case"⇧":shifted=!shifted;refreshKeys();break;
  case"123":symbols=true;refreshKeys();break;
  case"ABC":symbols=false;refreshKeys();break;
  case"🌐":prefs.set("language",prefs.language().equals("sw")?"en":"sw");break;
  default:String out=display(x);if(out.length()==1&&(Character.isLetter(out.charAt(0))||Character.isDigit(out.charAt(0))))out=FontStyler.apply(out,style);ic.commitText(out,1);if(shifted){shifted=false;refreshKeys();}break;
 }refreshSuggestions();}
 /** Auto-replace: fires only when the Settings toggle is ON, and only on a known exact typo (never a guess). */
 private void autoReplaceIfNeeded(InputConnection ic){if(!prefs.autoReplace())return;CharSequence before=ic.getTextBeforeCursor(40,0);if(before==null)return;String s=before.toString();String[] p=s.split("\\s+");if(p.length==0)return;String word=p[p.length-1];if(word.isEmpty())return;String fix=engine.autoCorrect(word,prefs.language());if(fix!=null){ic.deleteSurroundingText(word.length(),0);ic.commitText(fix,1);}}
 /** Aa button: with a selection, restyles that selection right away; with no selection, cycles the style used for future typing. */
 private void cycleFont(){InputConnection ic=getCurrentInputConnection();CharSequence sel=ic==null?null:ic.getSelectedText(0);
  if(sel!=null&&sel.length()>0){ic.commitText(FontStyler.apply(sel.toString(),style),1);Toast.makeText(this,"Style imetumika kwa maneno uliyochagua",Toast.LENGTH_SHORT).show();return;}
  style=FontStyler.next(style);if(aaBtn!=null)aaBtn.setText(FontStyler.shortLabel(style));Toast.makeText(this,"Font: "+FontStyler.fullLabel(style)+" — herufi zijazo zitabadilika",Toast.LENGTH_SHORT).show();}
 private void toggleEmoji(){emojis=!emojis;emojiBox.removeAllViews();if(emojis){emojiBox.setVisibility(View.VISIBLE);emojiBox.addView(EmojiPanel.create(this,light,new EmojiPanel.Listener(){public void emoji(String e){InputConnection ic=getCurrentInputConnection();if(ic!=null)ic.commitText(e,1);}public void sticker(){openStickerPicker();}}));keys.setVisibility(View.GONE);}else{emojiBox.setVisibility(View.GONE);keys.setVisibility(View.VISIBLE);} }
 private void openStickerPicker(){startActivity(new Intent(this,StickerPickerActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));}
 public void insertSticker(Uri uri){InputConnection ic=getCurrentInputConnection();if(ic==null||uri==null)return;try{ClipDescription d=new ClipDescription("DraxFox TX9 Sticker",new String[]{getContentResolver().getType(uri)!=null?getContentResolver().getType(uri):"image/*"});InputContentInfo info=new InputContentInfo(uri,d,null);int flags=InputConnection.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;boolean ok=ic.commitContent(info,flags,null);if(!ok){ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);cm.setPrimaryClip(ClipData.newUri(getContentResolver(),"TX9 Sticker",uri));Toast.makeText(this,"App hii haipokei sticker moja kwa moja. Image imewekwa Clipboard.",Toast.LENGTH_LONG).show();}else Toast.makeText(this,"Sticker imeingizwa",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Sticker haikuingizwa: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
 private void pasteFromClipboard(){ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);if(cm.hasPrimaryClip()){ClipData d=cm.getPrimaryClip();if(d!=null&&d.getItemCount()>0){CharSequence t=d.getItemAt(0).coerceToText(this);InputConnection ic=getCurrentInputConnection();if(ic!=null)ic.commitText(t,1);}}}
 private void translateSelected(){InputConnection ic=getCurrentInputConnection();if(ic==null)return;CharSequence sel=ic.getSelectedText(0);if(sel==null||sel.length()==0){sel=ic.getTextBeforeCursor(120,0);}if(sel==null)return;String from=prefs.language(),to=from.equals("sw")?"en":"sw";String out=TranslatorEngine.offline(sel.toString(),from,to);ic.commitText(out,1);}
}
