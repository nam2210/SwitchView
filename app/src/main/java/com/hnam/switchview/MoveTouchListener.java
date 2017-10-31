package com.hnam.switchview;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

    private int top; //position of indicator when it is align top edge
    private int bottom; // position of indicator when it is align bottom edge

    private View container;


    public MoveTouchListener(View container, View indicator) {
        currentX = indicator.getX();
        currentY = indicator.getY();

        this.container = container;
        this.length = container.getHeight();
        this.threshold = length / 4;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) indicator.getLayoutParams();
        this.top = lp.topMargin;
        this.bottom = length - indicator.getHeight() - top;

    }

    OnMoveTouchCallback callback;

    public void setCallback(OnMoveTouchCallback callback) {
        this.callback = callback;
    }

    private float dX, dY;

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
                float y = event.getRawY() + dY;
                if (y > top && y < bottom){
                    v.animate()
                            .x(currentX)
                            .y(y)
                            .setDuration(0)
                            .start();
                } else {
                    if (y >= 0 && y < bottom){
                        float scale = (top - y) / v.getHeight();
                        callback.onScaleUp(scale);
                    } else if (y <= (bottom + (top)) && y > top){
                        float scale = (y - bottom) / v.getHeight();
                        callback.onScaleDown(scale);
                    }
                }
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
                    currentY = currentY != top ? top : bottom;
                    v.animate()
                            .x(currentX)
                            .y(currentY)
                            .setDuration(300)
                            .setInterpolator(new LinearInterpolator())
                            .start();
                    callback.onChangePosition();
                }
                callback.onNormal();

                break;
            default: {
                if (Math.abs(v.getY() - currentY) <= threshold) {
                    v.animate()
                            .x(currentX)
                            .y(currentY)
                            .setDuration(300)
                            .setInterpolator(new LinearInterpolator())
                            .start();
                    callback.onDoNotChangePosition();
                } else {
                    currentY = currentY != top ? top : bottom;
                    v.animate()
                            .x(currentX)
                            .y(currentY)
                            .setDuration(300)
                            .setInterpolator(new LinearInterpolator())
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

        void onScaleUp(float scale);

        void onScaleDown(float scale);

        void onNormal();
    }
}
