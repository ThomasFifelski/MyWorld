package com.mcode.myworld;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.mcode.mjl.andriod.gles20.matrix.MVPMatrix;
import com.mcode.mjl.andriod.gles20.model.Model;
import com.mcode.mjl.andriod.gles20.model.WavefrontObj;
import com.mcode.mjl.andriod.gles20.shaders.SimpleShaderProgram;


class MyWorldRenderer implements GLSurfaceView.Renderer {
	public static String TAG = "GLES20TriangleRenderer"; // For logging purposes

    private MVPMatrix matrix = new MVPMatrix();

    private Context mContext;
    private Model wierdObj;
    
    public MyWorldRenderer(Context context) {
        mContext = context;     
    }
    
    public MVPMatrix getMatrix() {
    	return matrix;
    }

    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        
        wierdObj.drawFrame();
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(matrix.getPMatrix(), 0, -ratio, ratio, -1, 1, 1, 17);
    }
    
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
    	try {
    		wierdObj = new WavefrontObj(new SimpleShaderProgram(), matrix, mContext.getAssets().open("cube.obj"), mContext);  
    	} catch(IOException e) {
    		// file not found
    		Log.e(TAG, "Model file not found!");
    	}
        Matrix.setLookAtM(matrix.getVMatrix(), 0, 0, 0, -5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }
}
