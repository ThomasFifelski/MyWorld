package com.mcode.myworld;

import android.app.Activity;
import android.os.Bundle;


public class MyWorldActivity extends Activity {

    private MyWorldGLSurfaceView mView;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mView = new MyWorldGLSurfaceView(getApplication());
        setContentView(mView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();
    }
}
