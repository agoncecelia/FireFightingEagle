package com.fluskat.firefightingeagle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by A.Kajtazi on 5/9/2017.
 */

public class MarkMeSafeActivity extends AppCompatActivity {
    private Button markMeSafe;

    private MarkMeSafeActivity getContext() {
        return MarkMeSafeActivity.this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_safe);
        markMeSafe = (Button) findViewById(R.id.mark_me_safe);
        markMeSafe.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}
