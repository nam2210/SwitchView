package com.hnam.tlockview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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

    private View mBg;
    private View mIndicator;
    private DoorLockState mState;

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.door_view, this);
    }

    private int minDimension;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        minDimension = Math.min(widthSize, heightSize);
    }

    final ViewGroup mParent = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBg = (View) findViewById(R.id.iv_bg);
        mIndicator = (View) findViewById(R.id.view_indicator);

        LayoutParams bgParams = (LayoutParams) mBg.getLayoutParams();
        bgParams.width = LayoutParams.MATCH_PARENT;
        bgParams.height = LayoutParams.MATCH_PARENT;
        bgParams.addRule(CENTER_IN_PARENT);
        mBg.setLayoutParams(bgParams);
        mBg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_normal_circle));

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
                            //rotate();
                            mListener.onDoorLockPressed();
                        }
                        mEvent = EVENT_NORMAL;

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
        setColor(state);
    }


    public void control(DoorLockState state) {
        this.mState = state;
        rotate();
    }



    private void setDoorState(DoorLockState state){
        if (state == DoorLockState.STATE_LOCK || state == DoorLockState.STATE_CONTROL_CLOCK) {
            mIndicator.animate()
                    .rotation(90)
                    .start();
        } else if (state == DoorLockState.STATE_UNLOCK || state == DoorLockState.STATE_CONTROL_UN_CLOCK){
            mIndicator.animate()
                    .rotation(0)
                    .start();
        } else {
            mIndicator.animate()
                    .rotation(45)
                    .start();
        }
    }

    private void setColor(DoorLockState state){
        if (state == DoorLockState.STATE_LOCK) {
            mBg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_red_circle));
            mIndicator.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_red_indicator_shadow));
        } else if (state == DoorLockState.STATE_UNLOCK){
            mBg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_green_circle));
            mIndicator.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_green_indicator_shadow));
        } else {
            mBg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_gray_circle));
            mIndicator.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_gray_indicator_shadow));
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
        float rotation = mIndicator.getRotation();
        if ((mState == DoorLockState.STATE_UNLOCK || mState == DoorLockState.STATE_CONTROL_UN_CLOCK) && rotation != 0.0f) {
            mIndicator.animate()
                    .rotation(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .start();
            //green
        } else if ((mState == DoorLockState.STATE_LOCK || mState == DoorLockState.STATE_CONTROL_CLOCK) && rotation != 90.f){
            mIndicator.animate()
                    .rotation(90)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .start();
            //blue
        }
    }


}
