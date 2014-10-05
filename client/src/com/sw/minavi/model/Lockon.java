package com.sw.minavi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.sw.minavi.item.Camera3D;

public class Lockon {
	private FloatBuffer buffer; // 頂点用バッファ
	private FloatBuffer normalBuffer; // 法線用バッファ
	private FloatBuffer textureBuffer; // テクスチャ用バッファ
	private float[] vertex;
	private float x, y, z;
	private int imgTextureId;
	private int[] degreeToAngle = new int[360];

	public Lockon(int imgTextureId) {
		int angle = 270;
		for (int degree = 0; degree < 90; degree++) {
			degreeToAngle[degree] = angle;
			angle--;
		}
		angle = 180;
		for (int degree = 90; degree < 180; degree++) {
			degreeToAngle[degree] = angle;
			angle--;
		}
		angle = 90;
		for (int degree = 180; degree < 270; degree++) {
			degreeToAngle[degree] = angle;
			angle--;
		}
		angle = 0;
		for (int degree = 270; degree < 360; degree++) {
			degreeToAngle[degree] = angle;
			angle--;
		}

		this.imgTextureId = imgTextureId;

		float vertex[] = {
				-1.0f, -1.0f, 0.0f,
				1.0f, -1.0f, 0.0f,
				1.0f, 1.0f, 0.0f,
				1.0f, 1.0f, 0.0f,
				-1.0f, 1.0f, 0.0f,
				-1.0f, -1.0f, 0.0f,
		};
		ByteBuffer vb = ByteBuffer.allocateDirect(vertex.length * 4);
		vb.order(ByteOrder.nativeOrder());
		buffer = vb.asFloatBuffer();
		buffer.put(vertex);
		buffer.position(0);

		float normal[] = {
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
		};
		ByteBuffer nb = ByteBuffer.allocateDirect(normal.length * 4);
		nb.order(ByteOrder.nativeOrder());
		normalBuffer = nb.asFloatBuffer();
		normalBuffer.put(normal);
		normalBuffer.position(0);

		float texture[] = {
				0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, };
		ByteBuffer tb = ByteBuffer.allocateDirect(texture.length * 4);
		tb.order(ByteOrder.nativeOrder());
		textureBuffer = tb.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
	}

	public void draw(GL10 gl, Camera3D camera, int degree, int pitch, int roll, float angle) {
		gl.glPushMatrix(); // マトリックス記憶

		Vector3f middle = camera.getMiddleEyeAndLook(10);

		gl.glTranslatef(middle.x, middle.y, middle.z);
		gl.glRotatef(degreeToAngle[degree], 0, 1, 0);
		gl.glRotatef(degreeToAngle[roll], 1, 0, 0);
		gl.glRotatef(angle, 0, 0, 1);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, imgTextureId);

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

	public Vector3f[][] getVector3f() {
		Vector3f[][] triangles = new Vector3f[2][3];
		triangles[0] = new Vector3f[] {
				new Vector3f(vertex[0], vertex[1], vertex[2]),
				new Vector3f(vertex[3], vertex[4], vertex[5]),
				new Vector3f(vertex[6], vertex[7], vertex[8]), };

		triangles[1] = new Vector3f[] {
				new Vector3f(vertex[9], vertex[10], vertex[11]),
				new Vector3f(vertex[12], vertex[13], vertex[14]),
				new Vector3f(vertex[15], vertex[16], vertex[17]), };

		return triangles;
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

	public float distance(float ex, float ey, float ez) {
		double dx = this.x - ex;
		double dy = this.y - ey;
		double dz = this.z - ez;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}
