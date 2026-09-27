package com.draxfox.tx9keyboard;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/** Small, dependency-free motion and shape helpers shared by the keyboard and settings screen. */
public final class MotionEffects {
    private MotionEffects() {}

    public static int dp(Context context, float value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    public static GradientDrawable shape(int fill, int stroke, float radiusDp, Context context) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(fill);
        shape.setCornerRadius(dp(context, radiusDp));
        if (stroke != Color.TRANSPARENT) shape.setStroke(dp(context, 1), stroke);
        return shape;
    }

    public static Drawable ripple(Context context, int fill, int stroke, float radiusDp, int rippleColor) {
        GradientDrawable content = shape(fill, stroke, radiusDp, context);
        GradientDrawable mask = shape(Color.WHITE, Color.TRANSPARENT, radiusDp, context);
        return new RippleDrawable(ColorStateList.valueOf(rippleColor), content, mask);
    }

    /** Gentle press feedback; returning false keeps the view's normal click handling intact. */
    @SuppressLint("ClickableViewAccessibility")
    public static void attachPress(View view) {
        view.setOnTouchListener((target, event) -> {
            if (!motionEnabled(target.getContext())) {
                target.setScaleX(1f);
                target.setScaleY(1f);
                return false;
            }
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                target.animate().scaleX(0.94f).scaleY(0.94f).setDuration(80)
                        .setInterpolator(new DecelerateInterpolator()).start();
            } else if (event.getActionMasked() == MotionEvent.ACTION_UP
                    || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                target.animate().scaleX(1f).scaleY(1f).setDuration(190)
                        .setInterpolator(new OvershootInterpolator(1.35f)).start();
            }
            return false;
        });
    }

    /** Fade + short upward settle, used when a panel or screen first appears. */
    public static void enter(View view, long delayMs) {
        if (!motionEnabled(view.getContext())) {
            view.setAlpha(1f);
            view.setTranslationY(0f);
            return;
        }
        view.setAlpha(0f);
        view.setTranslationY(dp(view.getContext(), 10));
        view.animate().alpha(1f).translationY(0f).setStartDelay(delayMs).setDuration(240)
                .setInterpolator(new DecelerateInterpolator(1.4f)).start();
    }

    public static boolean motionEnabled(Context context) {
        return ValueAnimator.areAnimatorsEnabled();
    }
}