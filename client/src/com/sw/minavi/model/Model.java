package com.sw.minavi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.sw.minavi.item.LocalItem;

public class Model {

	private FloatBuffer buffer; // 頂点用バッファ
	private FloatBuffer normalBuffer; // 法線用バッファ
	private FloatBuffer textureBuffer; // テクスチャ用バッファ
	private float[] vertex;
	private float x, y, z, degree;
	private int[] textures;
	private Bitmap image;
	private LocalItem item;

	public Model(float x, float y, float z, float degree, int[] textures,
			Bitmap image, LocalItem item) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.degree = degree;
		this.image = image;
		this.textures = textures;
		this.item = item;

		vertex = new float[] { -1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, -1.0f,
				0.0f, };
		ByteBuffer vb = ByteBuffer.allocateDirect(vertex.length * 4);
		vb.order(ByteOrder.nativeOrder());
		buffer = vb.asFloatBuffer();
		buffer.put(vertex);
		buffer.position(0);

		float normal[] = { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, };
		ByteBuffer nb = ByteBuffer.allocateDirect(normal.length * 4);
		nb.order(ByteOrder.nativeOrder());
		normalBuffer = nb.asFloatBuffer();
		normalBuffer.put(normal);
		normalBuffer.position(0);

		float texture[] = { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f, };
		ByteBuffer tb = ByteBuffer.allocateDirect(texture.length * 4);
		tb.order(ByteOrder.nativeOrder());
		textureBuffer = tb.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glPushMatrix(); // マトリックス記憶

		gl.glTranslatef(x, y, z);
		gl.glRotatef(degree, 0, 1, 0);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		if (image != null) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
		}

		// 背景透過イメージの有効化
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);

		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);

		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glPopMatrix(); // マトリックスを戻す
	}

	public Vector3f[] getVector3f(float posX, float posY, float posZ) {
		return new Vector3f[] {
				new Vector3f(vertex[0] + posX, vertex[1] + posY, vertex[2]
						+ posZ),
				new Vector3f(vertex[3] + posX, vertex[4] + posY, vertex[5]
						+ posZ),
				new Vector3f(vertex[6] + posX, vertex[7] + posY, vertex[8]
						+ posZ),
				new Vector3f(vertex[9] + posX, vertex[10] + posY, vertex[11]
						+ posZ),
				new Vector3f(vertex[12] + posX, vertex[13] + posY, vertex[14]
						+ posZ),
				new Vector3f(vertex[15] + posX, vertex[16] + posY, vertex[17]
						+ posZ), };
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public LocalItem getItem() {
		return item;
	}

	public float distance(float ex, float ey, float ez) {
		double dx = this.x - ex;
		double dy = this.y - ey;
		double dz = this.z - ez;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}