package com.sw.minavi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class DynamicModel {

	public static void draw(GL10 gl, float[] vertex, float x, float y, float z,
			float degree) {

		ByteBuffer vb = ByteBuffer.allocateDirect(vertex.length * 4);
		vb.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = vb.asFloatBuffer();
		buffer.put(vertex);
		buffer.position(0);

		gl.glPushMatrix(); // マトリックス記憶

		gl.glTranslatef(x, y, z);
		gl.glRotatef(degree, 0, 1, 0);

		// 背景透過イメージの有効化
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);

		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);
		gl.glPopMatrix(); // マトリックスを戻す
	}
}