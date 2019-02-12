package com.hnam.tlockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Timer;

/**
 * Created by nampham on 2/11/19.
 */
public class MyCircleView extends View {

    private static final String TAG = MyCircleView.class.getSimpleName();

    public MyCircleView(Context context) {
        super(context);
        init(context, null);
    }

    public MyCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Paint paint;
    private int color;
    public void init(Context context, AttributeSet attrs){
        if (attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyCircleView);
            color = a.getColor(R.styleable.MyCircleView_cv_color, ContextCompat.getColor(context, R.color.colorAccent));
            a.recycle();
        } else {
            color = ContextCompat.getColor(context, R.color.colorAccent);
        }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        Log.e(TAG,"init paint="+color);
        paint.setColor(color);

    }

    private float radius = -1;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radius = Math.min(getWidth(), getHeight());
        if (index !=-1){
            paint.setAlpha((int) (alpha * 255));
            canvas.drawCircle(radius / 2f, radius / 2f, index * radius/FRAME_INDEX * 0.5f, paint);
        } else {
            paint.setAlpha((int) (alpha * 255));
            canvas.drawCircle(radius / 2f, radius / 2f, radius*0.5f, paint);
        }

    }
    private final static int FRAME_INDEX = 20;
    private int index = -1;
    private float alpha = 1;
    private boolean isRunning = false;
    public void startRipple(){
        if (!isRunning) {
            setVisibility(View.VISIBLE);
            isRunning = true;
            runRipple();
        }
    }

    private void runRipple(){
        index++;
        alpha = (FRAME_INDEX-index)/(float)FRAME_INDEX;
        if (index == FRAME_INDEX+1){
            index = 0;
            alpha = 1;
        }

        invalidate();
        postDelayed(startRunnable,50);//chạy 1 sẽ vẽ hết được vòng tròn full size
    }

    public void stopRipple(){
        removeCallbacks(startRunnable);
        setVisibility(View.GONE);
        index = -1;
        alpha = 1;
        isRunning = false;
    }

    public boolean isRunning(){
        return isRunning;
    }

    private Runnable startRunnable = new Runnable() {
        @Override
        public void run() {
            runRipple();

        }
    };
}
