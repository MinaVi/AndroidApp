package com.sw.minavi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Ground {
	private ShortBuffer indexBuffer;
	private FloatBuffer vertexBuffer;

	private float[] axis = {
			0, 0, 0,
			5, 0, 0,
			0, 5, 0,
			0, 0, 5
	};
	private short[] indices = {
			1, 0, 2, 0, 3, 0
	};

	/*
	 * データの初期化 
	 */
	public void initialize(GL10 gl, EGLConfig config) {

		ByteBuffer vbb = ByteBuffer.allocateDirect(axis.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();

		vertexBuffer.put(axis);
		indexBuffer.put(indices);

		vertexBuffer.position(0);
		indexBuffer.position(0);
	}

	/*
	 * 描画
	 */
	public void draw(GL10 gl) {
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glDrawElements(GL10.GL_LINES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
	}
}