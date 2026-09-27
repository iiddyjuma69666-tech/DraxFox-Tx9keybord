package com.draxfox.tx9keyboard;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

/** Local, optional audio layer paired with Android's built-in key haptic. */
public final class KeypressFeedback {
    private final SoundPool pool;
    private final int keypress;
    private final int spacebar;

    public KeypressFeedback(Context context){
        AudioAttributes attributes=new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        pool=new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(attributes).build();
        keypress=pool.load(context,R.raw.keypress_soft,1);
        spacebar=pool.load(context,R.raw.spacebar_soft,1);
    }

    public void play(boolean isSpacebar){
        int sound=isSpacebar?spacebar:keypress;
        if(sound!=0) pool.play(sound,0.35f,0.35f,1,0,1f);
    }

    public void release(){pool.release();}
}