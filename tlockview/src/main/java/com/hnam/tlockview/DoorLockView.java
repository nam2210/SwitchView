package com.hnam.tlockview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_UP;
import static com.hnam.tlockview.DoorLockState.STATE_OFFLINE;

/**
 * Created by nampham on 4/24/18.
 */
public class DoorLockView extends RelativeLayout {
    private static final String TAG = DoorLockView.class.getSimpleName();

    public DoorLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private View mBg;
    private View mIndicator;
    private DoorLockState mState = STATE_OFFLINE;


    private int minDimension;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "onMeasure");
        //calculate size of background
        measureChildWithMargins(mBg, widthMeasureSpec, 0, heightMeasureSpec, 0);
        LayoutParams bgParams = (LayoutParams) mBg.getLayoutParams();
        int width = mBg.getMeasuredWidth() + bgParams.leftMargin + bgParams.rightMargin;
        int height = mBg.getMeasuredHeight() + bgParams.topMargin + bgParams.bottomMargin;

        measureChildWithMargins(mIndicator, widthMeasureSpec, 0, heightMeasureSpec, 0);

        if (rippleView != null) {
            measureChildWithMargins(rippleView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }

        //set how big for customView
        int size = Math.min(width, height);
        setMeasuredDimension(resolveSize(size, widthMeasureSpec), resolveSize(size, heightMeasureSpec));
    }

    private int radius = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout");
        //place background view and laid in centerInParent
        LayoutParams bgLayoutParams = (LayoutParams) mBg.getLayoutParams();
        radius = Math.min(mBg.getMeasuredWidth(), mBg.getMeasuredHeight());
        int left = getPaddingLeft() + bgLayoutParams.leftMargin;
        int top = (int) (getPivotY() - radius/2 + getPaddingTop() + bgLayoutParams.topMargin);
        int right = left + radius;
        int bottom = top + radius;

        mBg.layout(left, top, right, bottom);
        //place ripple
        if (rippleView != null) {
//            left = (int) (getPivotX() - Math.round(rippleView.getMeasuredWidth() / 2f));
//            top = (int) ((int) getPivotY() - Math.round(rippleView.getMeasuredHeight() / 2f));
//            right = (int) (getPivotX() + Math.round(rippleView.getMeasuredWidth() / 2f));
//            bottom = (int) ((int) getPivotY() + Math.round(rippleView.getMeasuredHeight() / 2f));
            rippleView.layout(left, top, right, bottom);
        }


        //place stick
        left = (int) (getPivotX() - mIndicator.getMeasuredWidth()/2);
        top = (int) ((int) getPivotY() - (radius/2) * 0.8);
        right = left + mIndicator.getMeasuredWidth();
        bottom = (int) (top + radius * 0.8);

        mIndicator.layout(left, top, right, bottom);


    }

    private MyCircleView rippleView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e(TAG, "onFinishInflate");
        mBg = (View) findViewById(R.id.iv_bg);
        mIndicator = (View) findViewById(R.id.view_indicator);
        //rippleView = findViewById(R.id.ripple);
//        LayoutParams bgParams = (LayoutParams) mBg.getLayoutParams();
//        bgParams.width = LayoutParams.MATCH_PARENT;
//        bgParams.height = LayoutParams.MATCH_PARENT;
//        bgParams.addRule(CENTER_IN_PARENT);
//        mBg.setLayoutParams(bgParams);
//        mBg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_gray_circle));
//
//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                LayoutParams tvParams = (LayoutParams) mIndicator.getLayoutParams();
//                tvParams.height = (int) (minDimension * 0.8f);
//                tvParams.addRule(CENTER_IN_PARENT);
//                mIndicator.setLayoutParams(tvParams);
//            }
//        });


        mBg.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case ACTION_UP: {
                        if (mEvent == EVENT_UP) {
                            //rotate();
                            if (mListener != null) {
                                if (mState == DoorLockState.STATE_LOCK
                                        || mState == DoorLockState.STATE_UNLOCK) {
                                    mListener.onDoorLockPressed();
                                } else {
                                    mListener.onNoControl();
                                }
                            }
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

    public void setOffline() {
        this.mState = STATE_OFFLINE;
        setColor(STATE_OFFLINE);
    }

    public DoorLockState getState() {
        return mState;
    }

    public void control(DoorLockState state) {
        this.mState = state;
        rotate();
        toggleRipple();
    }

    private void setDoorState(DoorLockState state) {
        if (state == DoorLockState.STATE_LOCK || state == DoorLockState.STATE_CONTROL_LOCK) {
            mIndicator.animate()
                    .rotation(90)
                    .start();
        } else if (state == DoorLockState.STATE_UNLOCK || state == DoorLockState.STATE_CONTROL_UNLOCK) {
            mIndicator.animate()
                    .rotation(0)
                    .start();
        } else {
            mIndicator.animate()
                    .rotation(45)
                    .start();
        }
    }

    private void setColor(DoorLockState state) {
        if (state == DoorLockState.STATE_LOCK) {
            mBg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_red_circle));
            mIndicator.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_red_indicator_shadow));
        } else if (state == DoorLockState.STATE_UNLOCK) {
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
            new GestureDetector.SimpleOnGestureListener() {
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

    public void setDoorLockListener(DoorLockListener listener) {
        this.mListener = listener;
    }

    public interface DoorLockListener {
        void onDoorLockPressed();

        void onNoControl();
    }

    private void rotate() {
        float rotation = mIndicator.getRotation();
        if ((mState == DoorLockState.STATE_UNLOCK || mState == DoorLockState.STATE_CONTROL_UNLOCK) && rotation != 0.0f) {
            mIndicator.animate()
                    .rotation(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            setColor(mState);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    })
                    .start();
            //green
        } else if ((mState == DoorLockState.STATE_LOCK || mState == DoorLockState.STATE_CONTROL_LOCK) && rotation != 90.f) {
            mIndicator.animate()
                    .rotation(90)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            setColor(mState);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    })
                    .start();
            //red
        }
    }

    private void toggleRipple(){
        if (rippleView == null) {
            rippleView = new MyCircleView(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rippleView.setLayoutParams(params);
            addView(rippleView);
            rippleView.post(new Runnable() {
                @Override
                public void run() {
                    rippleView.runRipple();
                }
            });
        } else {
            rippleView.stop();
            removeViewAt(getChildCount() - 1);
            rippleView = null;
        }

    }


    private void runRipple(final View view){

        ObjectAnimator objAnim = ObjectAnimator.ofFloat(view, View.SCALE_X, 30);
        ObjectAnimator objAnim1 = ObjectAnimator.ofFloat(view, View.SCALE_Y, 30);
        ObjectAnimator objAnim2 = ObjectAnimator.ofFloat(view, View.ALPHA, 0.3f);



        List<Animator> list = new ArrayList<>();
        list.add(objAnim);
        list.add(objAnim1);
        list.add(objAnim2);


        AnimatorSet set = new AnimatorSet();
        set.playTogether(list);
        set.setInterpolator(new AccelerateInterpolator());
        set.setDuration(1000);


//        AnimatorSet setReturn = new AnimatorSet();
//        setReturn.playTogether(list1);
//        setReturn.setInterpolator(new AccelerateInterpolator());
//        setReturn.setDuration(1000);
//
//        AnimatorSet result = new AnimatorSet();
//        result.playSequentially(set, setReturn);

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e(TAG,"onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e(TAG,"onAnimationEnd");
                removeViewAt(getChildCount() - 1);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e(TAG,"onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e(TAG,"onAnimationRepeat");
            }
        });
        set.start();
    }




}
