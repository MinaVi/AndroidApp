package com.sw.minavi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Model {

	private FloatBuffer buffer; // 頂点用バッファ
	private FloatBuffer normalBuffer; // 法線用バッファ
	private FloatBuffer textureBuffer; // テクスチャ用バッファ

	public Model() {
		float vertex[] = {
				0.0f, 0.0f, 0.0f, // 左上
				1.0f, 0.0f, 0.0f, // 右上
				0.0f, 1.0f, 0.0f, // 左下
				1.0f, 1.0f, 0.0f, // 右下
		};
		ByteBuffer vb = ByteBuffer.allocateDirect(vertex.length * 4);
		vb.order(ByteOrder.nativeOrder());
		buffer = vb.asFloatBuffer();
		buffer.put(vertex);
		buffer.position(0);

		float normal[] = {
				1.0f, 0.0f, 1.0f,
				-1.0f, 0.0f, 1.0f,
				0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 0.0f,
		};
		ByteBuffer nb = ByteBuffer.allocateDirect(normal.length * 4);
		nb.order(ByteOrder.nativeOrder());
		normalBuffer = nb.asFloatBuffer();
		normalBuffer.put(normal);
		normalBuffer.position(0);

		float texture[] = {
				1.0f, 1.0f,
				0.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 0.0f,
		};
		ByteBuffer tb = ByteBuffer.allocateDirect(texture.length * 4);
		tb.order(ByteOrder.nativeOrder());
		textureBuffer = tb.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);

		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);

		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}
}