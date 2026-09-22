package com.draxfox.tx9keyboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class StickerPickerActivity extends Activity {
    private static final int PICK_IMAGE = 901;
    @Override public void onCreate(Bundle b){ super.onCreate(b); pick(); }
    private void pick(){
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("image/*"); i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION); startActivityForResult(i,PICK_IMAGE);
    }
    @Override protected void onActivityResult(int r,int c,Intent d){ super.onActivityResult(r,c,d); if(r==PICK_IMAGE && c==RESULT_OK && d!=null && d.getData()!=null){
        Uri u=d.getData(); try{getContentResolver().takePersistableUriPermission(u,d.getFlags()&Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}
        if(DraxFoxKeyboardService.getInstance()!=null) DraxFoxKeyboardService.getInstance().insertSticker(u); else getSharedPreferences("draxfox_prefs",MODE_PRIVATE).edit().putString("pending_sticker_uri",u.toString()).apply();
    } finish(); }
}
