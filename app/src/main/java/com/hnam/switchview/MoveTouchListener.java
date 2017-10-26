package com.hnam.switchview;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by nampham on 4/14/17.
 */

public class MoveTouchListener implements View.OnTouchListener {
    private static final String TAG = MoveTouchListener.class.getSimpleName();
    private float currentX;
    private float currentY;
    private final int length;
    private int threshold;
    private int top;

    public MoveTouchListener(View v, int length) {
        currentX = v.getX();
        currentY = v.getY();
        Log.e(TAG,"x: " + currentX + " y: " + currentY + " height:" + length);
        this.length = length;
        this.threshold = length / 4;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        this.top = lp.topMargin;
    }

    OnMoveTouchCallback callback;

    public void setCallback(OnMoveTouchCallback callback) {
        this.callback = callback;
    }

    float dX, dY;

    //JUST MOVE LEFT AND RIGHT
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //check whatever view at edge or not to show text or not
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                this.callback.onTap();
                break;
            case MotionEvent.ACTION_MOVE:
                //check when user gesture down
                v.animate()
                        .x(currentX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();

                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(v.getY() - currentY) <= threshold) {
                    v.animate()
                            .x(currentX)
                            .y(currentY)
                            .setDuration(300)
                            .setInterpolator(new LinearInterpolator())
                            .start();
                    callback.onDoNotChangePosition();
                } else {
                    currentY = currentY != top ? top : (length - v.getHeight() - top);
                    v.animate()
                            .x(currentX)
                            .y(currentY)
                            .setDuration(300)
                            .setInterpolator(new LinearInterpolator())
                            .start();
                    callback.onChangePosition();
                }


                break;
            default: {
                Log.e(TAG, "action default");
                if (Math.abs(v.getY() - currentY) <= threshold) {
                    v.animate()
                            .x(currentX)
                            .y(currentY)
                            .setDuration(400)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    callback.onDoNotChangePosition();
                } else {
                    //currentY = currentY != 0 ? 0 : length - v.getHeight();
                    currentY = currentY != 0 ? v.getPaddingTop() : length - v.getHeight() - v.getPaddingTop();
                    v.animate()
                            .x(currentX)
                            .y(currentY)
                            .setDuration(400)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    callback.onChangePosition();

                }
                return true;
            }
        }
        return true;
    }

    public interface OnMoveTouchCallback {
        void onTap();

        void onChangePosition();

        void onDoNotChangePosition();
    }
}
