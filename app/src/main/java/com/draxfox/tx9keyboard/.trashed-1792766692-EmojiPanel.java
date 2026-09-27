package com.draxfox.tx9keyboard;

import android.content.Context;import android.graphics.Color;import android.graphics.drawable.GradientDrawable;import android.view.*;import android.widget.*;import java.util.*;

public final class EmojiPanel {
 public interface Listener{void emoji(String e);void sticker();}
 private static final String[] EMOJI={"😀","😃","😄","😁","😆","😅","😂","🤣","😊","😇","🙂","🙃","😉","😌","😍","🥰","😘","😗","😎","🤩","🤔","😴","🤗","🤭","🤫","😐","😶","🙄","😏","😣","😥","😮","🤐","😯","😪","😫","🥱","😌","😛","😜","🤪","🤨","🧐","🤓","😕","😟","🙁","☹️","😲","😳","🥺","😭","😤","😡","🤬","🤯","😱","😨","😰","😓","🤠","🥳","😈","👿","💀","☠️","👻","👽","🤖","💩","🔥","✨","⭐","🌟","💯","❤️","🧡","💛","💚","💙","💜","🖤","🤍","🤎","💔","💕","💖","💗","💘","💝","👍","👎","👏","🙌","🙏","🤝","👌","✌️","🤞","🤟","🤘","👊","✊","💪","👋","👀","🫶","🎉","🎊","🎂","🎁","⚽","🏆","🚗","✈️","🌍","☀️","🌙","🌧️","🌊","🌸","🌹","🍕","🍔","🍟","🍎","🍌","☕","❤️‍🔥","😂❤️","🔥🔥"};
 public static View create(Context c,boolean light,Listener l){
  LinearLayout root=new LinearLayout(c);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(5,4,5,4);root.setBackgroundColor(light?Color.rgb(240,246,249):Color.rgb(10,20,28));
  LinearLayout top=new LinearLayout(c);top.setGravity(Gravity.CENTER_VERTICAL);
  TextView title=new TextView(c);title.setText("😀  EMOJI  •  STICKERS");title.setTextColor(light?Color.rgb(10,20,28):Color.WHITE);title.setTextSize(12);title.setPadding(8,5,8,5);top.addView(title,new LinearLayout.LayoutParams(0,42,1));
  TextView sticker=button(c,"＋ IMAGE STICKER",light);sticker.setTextSize(11);sticker.setOnClickListener(v->l.sticker());top.addView(sticker,new LinearLayout.LayoutParams(-2,42));root.addView(top);
  ScrollView sv=new ScrollView(c);GridLayout grid=new GridLayout(c);grid.setColumnCount(8);for(String e:EMOJI){TextView b=button(c,e,light);b.setTextSize(25);b.setPadding(0,0,0,0);b.setOnClickListener(v->l.emoji(((TextView)v).getText().toString()));grid.addView(b,new ViewGroup.LayoutParams(0,52)); GridLayout.LayoutParams gp=(GridLayout.LayoutParams)b.getLayoutParams();gp.width=0;gp.height=52;gp.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);b.setLayoutParams(gp);}sv.addView(grid);root.addView(sv,new LinearLayout.LayoutParams(-1,190));return root;
 }
 private static TextView button(Context c,String s,boolean light){TextView t=new TextView(c);t.setText(s);t.setGravity(Gravity.CENTER);t.setTextColor(light?Color.rgb(10,20,28):Color.WHITE);GradientDrawable g=new GradientDrawable();g.setColor(light?Color.WHITE:Color.rgb(27,45,56));g.setCornerRadius(14);g.setStroke(1,light?Color.rgb(198,212,219):Color.rgb(55,82,94));t.setBackground(g);return t;}
}
