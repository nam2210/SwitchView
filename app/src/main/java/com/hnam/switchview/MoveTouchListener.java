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

        this.length = length;
        this.threshold = length / 4;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        this.top = lp.topMargin;
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
                if (v.getY() < top || v.getY() > (length - v.getHeight() - top)) {

                } else {
                    float y = event.getRawY() + dY;
                    if (dY != event.getRawY()) {
                        v.animate()
                                .x(currentX)
                                .y(y)
                                .setDuration(0)
                                .start();
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
