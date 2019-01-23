package com.hnam.sw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hnam.tlockview.DoorLockState;
import com.hnam.tlockview.DoorLockView;

public class DoorViewActivity extends AppCompatActivity {
    private static final String TAG = DoorViewActivity.class.getSimpleName();
    private boolean isLock = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_view);
        final DoorLockView view = findViewById(R.id.doorlock);
        view.setDoorLockListener(new DoorLockView.DoorLockListener() {
            @Override
            public void onDoorLockPressed() {
                Log.e(TAG, "Door pressed");
            }

            @Override
            public void onNoControl() {

            }
        });
        view.setState(DoorLockState.STATE_LOCK);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLock = !isLock;
                view.control(isLock ? DoorLockState.STATE_LOCK : DoorLockState.STATE_UNLOCK);
            }
        });
    }
}
