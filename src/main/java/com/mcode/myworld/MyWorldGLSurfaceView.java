/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mcode.myworld;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

class MyWorldGLSurfaceView extends GLSurfaceView {
	MyWorldRenderer renderer;
    public MyWorldGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        renderer = new MyWorldRenderer(context);
        setRenderer(renderer);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	Log.e("onTouchEvent", "X: " + e.getX());
    	renderer.mAngleX = e.getX();
    	renderer.mAngleY = e.getY();
    	return true;
    }
}

