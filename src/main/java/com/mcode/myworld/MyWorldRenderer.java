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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.mcode.mjl.andriod.gles20.WavefrontObj;


class MyWorldRenderer implements GLSurfaceView.Renderer {
	public static String TAG = "GLES20TriangleRenderer"; // For logging purposes
	
	public volatile float mAngleX = 0.0f;
	public volatile float mAngleY = 0.0f;
	
	private List<Float> vertexList = new ArrayList<Float>();
	private List<Short> indexList = new ArrayList<Short>();
	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
	
	private static final int SHORT_SIZE_BYTES = 2;
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 3 * FLOAT_SIZE_BYTES;


    private final String mVertexShader =
        "uniform mat4 uMVPMatrix;     \n" +
        "attribute vec4 aPosition;    \n" +
        "void main() {                \n" +
        "  gl_Position = uMVPMatrix * aPosition; \n" +
        "}\n";

    private final String mFragmentShader =
        "precision mediump float;\n" +
        "void main() {\n" +
        "  gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0); \n" +
        "}\n";

    private float[] mMVPMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mMMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    private int mProgram;
    private int muMVPMatrixHandle;
    private int maPositionHandle;

    private Context mContext;
    
    
    public MyWorldRenderer(Context context) {
        mContext = context;
        WavefrontObj wierdObj = new WavefrontObj(mContext.getResources().openRawResource(R.raw.creature), null);
//        loadWavefront();
//       
//        vertexBuffer = ByteBuffer.allocateDirect(vertexList.size() * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        float[] vertexArray = new float[vertexList.size()];
//        for(int i = 0; i < vertexList.size(); i ++) {
//        	Log.i(TAG, "" + vertexList.get(i));
//        	vertexArray[i] = vertexList.get(i);
//        }
//        vertexBuffer.put(vertexArray).position(0);
//        indexBuffer = ByteBuffer.allocateDirect(indexList.size() * SHORT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
//        short[] indexArray = new short[indexList.size()];
//        for(int i = 0; i < indexList.size(); i ++) {
//        	indexArray[i] = indexList.get(i);
//        }
//        indexBuffer.put(indexArray).position(0);
        
    }

    public void onDrawFrame(GL10 glUnused) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);
        

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        Matrix.setRotateM(mMMatrix, 0, mAngleX, 0f, 1f, 0f);
        Matrix.rotateM(mMMatrix, 0, mAngleY, 1f, 0f, 0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexList.size(), GLES20.GL_UNSIGNED_SHORT, indexBuffer); 

    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 17);
    }
    
    private void loadWavefront() {
    	InputStream is = mContext.getResources().openRawResource(R.raw.creature);
    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
    	try {
			String line = br.readLine();
			while(line != null) {
				String[] e = line.split(" ");
				if(e.length > 1) {
					if(e[0].equals("v")) {
						for(int i = 1; i < e.length; i++) {
							vertexList.add(Float.parseFloat(e[i]));
						}
					} else if (e[0].equals("f")) {
						for(int i = 1; i < e.length; i++) {
							indexList.add((short)(Short.parseShort(e[i]) - 1));
							Log.w(TAG, e[i]);
						}
					}
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} 
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        mProgram = createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, pixelShader);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
}
