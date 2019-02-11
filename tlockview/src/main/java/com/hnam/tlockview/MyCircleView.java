package com.hnam.tlockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by nampham on 2/11/19.
 */
public class MyCircleView extends View {

    private static final String TAG = MyCircleView.class.getSimpleName();

    public MyCircleView(Context context) {
        super(context);
        init();
    }

    public MyCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint paint;
    public void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

    }

    private float radius = -1;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radius = Math.min(getWidth(), getHeight());
        if (index !=-1){
            Log.e(TAG, "radius=%s" + radius + "index=" + index * radius/10);
            //®Log.e(TAG, "alpha" + (float)(alpha));
            paint.setAlpha((int) (alpha * 255));
            canvas.drawCircle(radius / 2f, radius / 2f, index * radius/10 * 0.5f, paint);
        } else {
            Log.e(TAG, "jump here");
            //®Log.e(TAG, "alpha" + (float)(alpha));
            paint.setAlpha((int) (alpha * 255));
            canvas.drawCircle(radius / 2f, radius / 2f, radius*0.5f, paint);
        }

    }

    private int index = -1;
    private float alpha = 1;
    public void runRipple(){
        index++;
        alpha = (10-index)/10f;
        if (index == 11){
            index = 0;
            alpha = 1;
        }

        invalidate();
        postDelayed(startRunnable,100);
    }

    public void stop(){
        removeCallbacks(startRunnable);
    }

    private Runnable startRunnable = new Runnable() {
        @Override
        public void run() {
            runRipple();
        }
    };
}
