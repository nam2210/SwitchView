package com.hnam.tlockview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by nampham on 4/24/18.
 */
public class DoorLockView extends RelativeLayout{
    private static final String TAG = DoorLockView.class.getSimpleName();
    public DoorLockView(Context context) {
        super(context);
        init(context);
    }

    public DoorLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DoorLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ImageView mBg;
    private View mIndicator;
    private DoorLockState mState;

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.door_view, this);
        Log.e(TAG, "init layout");
    }

    private int minDimension;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        minDimension = Math.min(widthSize, heightSize);
        Log.e(TAG, "onMeasure>>>>: "+ minDimension);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e(TAG, "onFinishInfalte>>>>");
        mBg = (ImageView) findViewById(R.id.iv_bg);
        mIndicator = (View) findViewById(R.id.view_indicator);

        LayoutParams bgParams = (LayoutParams) mBg.getLayoutParams();
        bgParams.width = LayoutParams.MATCH_PARENT;
        bgParams.height = LayoutParams.MATCH_PARENT;
        bgParams.addRule(CENTER_IN_PARENT);
        mBg.setLayoutParams(bgParams);
        mBg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_lock_shadow));

        this.post(new Runnable() {
            @Override
            public void run() {
                LayoutParams tvParams = (LayoutParams) mIndicator.getLayoutParams();
                tvParams.height = (int) (minDimension * 0.8f);
                tvParams.addRule(CENTER_IN_PARENT);
                mIndicator.setLayoutParams(tvParams);
            }
        });

        mBg.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                switch (event.getAction()){
                    case ACTION_UP:{
                        if (mEvent == EVENT_UP){
                            rotate();
                            mListener.onDoorLockPressed();
                        }
                        mEvent = EVENT_NORMAL;
                        //scaleUp();

                        break;
                    }
                }
                return true;
            }
        });

    }

    public void setState(DoorLockState state) {
        this.mState = state;
        setDoorState(state);
    }

    public void setStateWithAnimation(DoorLockState state) {
        this.mState = state;
        rotate();
    }



    private void setDoorState(DoorLockState state){
        if (state == DoorLockState.STATE_LOCK) {
            mIndicator.animate()
                    .rotation(90)
                    .start();
        } else {
            mIndicator.animate()
                    .rotation(0)
                    .start();
        }
    }

    private int mEvent = EVENT_NORMAL;
    private static final int EVENT_NORMAL = -1;
    private static final int EVENT_DOWN = 1;
    private static final int EVENT_UP = 2;
    private static final int EVENT_OTHER = 3;
    private GestureDetector gestureDetector = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    //do something
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    mEvent = EVENT_UP;
                    return super.onSingleTapUp(e);
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    mEvent = EVENT_DOWN;
                    //scaleDown();
                    return super.onDown(e);
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    mEvent = EVENT_OTHER;
                    super.onLongPress(e);
                }
            });

    private DoorLockListener mListener;
    public void setDoorLockListener(DoorLockListener listener){
        this.mListener = listener;
    }
    public interface DoorLockListener{
        void onDoorLockPressed();
    }

    private void rotate(){
        if (mState == DoorLockState.STATE_UNLOCK) {
            mIndicator.animate()
                    .rotation(90)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .start();
            mState = DoorLockState.STATE_LOCK;
        } else {
            mIndicator.animate()
                    .rotation(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .start();
            mState = DoorLockState.STATE_UNLOCK;
        }
    }


}
