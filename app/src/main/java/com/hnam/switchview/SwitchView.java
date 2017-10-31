package com.hnam.switchview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by nampham on 10/26/17.
 */

public class SwitchView extends RelativeLayout{
    private static final String TAG = SwitchView.class.getSimpleName();

    private boolean isEnable = false;

    public SwitchView(Context context) {
        super(context);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Activity getActivity() {
        return (Activity) getContext();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getActivity().getLayoutInflater().inflate(R.layout.view_lock, this);
        init();
    }

    private View indicator;
    private RelativeLayout container;

    private View getIndicator() {
        if (indicator == null) {
            indicator = findViewById(R.id.indicator);
        }
        return indicator;
    }

    public RelativeLayout getContainer() {
        if (container == null) {
            container = (RelativeLayout) findViewById(R.id.relativeLayout);
        }
        return container;
    }

    private MoveTouchListener moveTouchListener;

    private void init(){
        getContainer().post(new Runnable() {
            @Override
            public void run() {
                moveTouchListener = new MoveTouchListener(getContainer(), getIndicator());
                moveTouchListener.setCallback(callback);
                getIndicator().setOnTouchListener(moveTouchListener);
                moveIndicator();
            }
        });
    }

    private MoveTouchListener.OnMoveTouchCallback callback = new MoveTouchListener.OnMoveTouchCallback() {
        @Override
        public void onTap() {

        }

        @Override
        public void onChangePosition() {
            isEnable = !isEnable;
        }

        @Override
        public void onDoNotChangePosition() {

        }


        @Override
        public void onScaleUp(float scale) {
            SwitchView.this.setPivotY(getHeight());
            SwitchView.this.animate()
                    .scaleY(1 + scale )
                    .scaleX(1 - scale)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(150)
                    .start();
        }



        @Override
        public void onScaleDown(float scale) {
            SwitchView.this.setPivotY(0);
            SwitchView.this.animate()
                    .scaleY(1 + scale )
                    .scaleX(1 - scale)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(150)
                    .start();
        }

        @Override
        public void onNormal() {
            SwitchView.this.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .start();
        }
    };

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        this.isEnable = enable;
    }

    private void moveIndicator(){
        float x = getIndicator().getX();
        int height = getContainer().getHeight();
        LayoutParams params = (LayoutParams) getIndicator().getLayoutParams();
        int top = params.topMargin;
        if (isEnable){
            getIndicator().setX(x);
            getIndicator().setY(top);
        } else {
            getIndicator().setX(x);
            getIndicator().setY(height - getIndicator().getHeight() - top);
        }

    }
}
