package com.mcode.myworld;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.mcode.mjl.andriod.gles20.MVPMatrix;
import com.mcode.mjl.andriod.gles20.WavefrontObj;


class MyWorldRenderer implements GLSurfaceView.Renderer {
	public static String TAG = "GLES20TriangleRenderer"; // For logging purposes
	
	public volatile float mAngleX = 0.0f;
	public volatile float mAngleY = 0.0f;

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

    private MVPMatrix matrix = new MVPMatrix();

    private int mProgram;
    private Context mContext;
    private WavefrontObj wierdObj;
    
    public MyWorldRenderer(Context context) {
        mContext = context;
        wierdObj = new WavefrontObj(matrix, mContext.getResources().openRawResource(R.raw.creature), null);        
    }

    public void onDrawFrame(GL10 glUnused) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

//        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
//        GLES20.glEnableVertexAttribArray(maPositionHandle);
//        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);
//        
//
//        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
//
//        Matrix.setRotateM(mMMatrix, 0, mAngleX, 0f, 1f, 0f);
//        Matrix.rotateM(mMMatrix, 0, mAngleY, 1f, 0f, 0f);
//        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
//
//        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
//
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexList.size(), GLES20.GL_UNSIGNED_SHORT, indexBuffer); 
        
        wierdObj.drawFrame(mProgram,  "aPosition", "uMVPMatrix");
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(matrix.getPMatrix(), 0, -ratio, ratio, -1, 1, 1, 17);
    }
    
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        mProgram = createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }

        Matrix.setLookAtM(matrix.getVMatrix(), 0, 0, 0, -5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
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
