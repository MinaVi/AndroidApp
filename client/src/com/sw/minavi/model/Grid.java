package com.sw.minavi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Grid {

	/**
	 * 
	 * @param gl
	 * @param xNum x方向の線の数
	 * @param zNum z方向の線の数
	 * @param xSize x方向の線の間隔
	 * @param zSize y方向の線の間隔
	 */
	public void drawGrid(GL10 gl, int xNum, int zNum, float xSize, float zSize) {
		for (float x = -xSize; x <= xSize; x += xSize / xNum) {
			drawLine(gl, x, 0.0f, -zSize, x, 0.0f, zSize);
		}
		for (float z = -zSize; z <= zSize; z += zSize / zNum) {
			drawLine(gl, -xSize, 0.0f, z, xSize, 0.0f, z);
		}
	}

	public static final void drawLine(GL10 gl, float x0, float y0, float z0, float x1, float y1, float z1) {
		float[] vertices = {
				x0, y0, z0,
				x1, y1, z1,
		};
		FloatBuffer polygonVertices = makeFloatBuffer(vertices);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, polygonVertices);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDrawArrays(GL10.GL_LINES, 0, 2);
	}

	private static FloatBuffer makeFloatBuffer(float[] vertices) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		
		FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
		floatBuffer.put(vertices);
		floatBuffer.position(0);
		return floatBuffer;
	}
}
