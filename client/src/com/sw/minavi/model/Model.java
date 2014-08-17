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
	private float x, y, z;
	private int degree;
	private int[] textures;
	private Bitmap image;
	private LocalItem item;

	public Model(float x, float y, float z, int degree, int[] textures,
			Bitmap image, LocalItem item) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.degree = degree;
		this.image = image;
		this.textures = textures;
		this.item = item;

		float[] relativeVertex = new float[] {
				-1.0f, -1.0f, 0.0f,
				 1.0f, -1.0f, 0.0f,
				 1.0f,  1.0f, 0.0f,
				 1.0f,  1.0f, 0.0f,
				-1.0f,  1.0f, 0.0f,
				-1.0f, -1.0f, 0.0f, };

		int index = 0;
		float[] midPoint = new float[] { 0.0f, 0.0f, 0.0f };
		float[] tmpVertex = new float[18];
		while (index < relativeVertex.length) {
			// 1件分の頂点を配列化
			float[] v = new float[] {
					relativeVertex[index + 0],
					relativeVertex[index + 1],
					relativeVertex[index + 2] };

			// 中点midPointを基点に頂点の位置をdegree度回転させる
			float[] rotateV =com.sw.minavi.util.GLUtils.getRotatePos(v, midPoint, -degree);

			// xyzの値を元に平行移動し、頂点の絶対座標を算出
			tmpVertex[index + 0] = rotateV[0] + x;
			tmpVertex[index + 1] = rotateV[1] + y;
			tmpVertex[index + 2] = rotateV[2] + z;
			index += 3;
		}
		vertex = tmpVertex;

//		vertex = new float[] { -1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f,
//				1.0f, 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, -1.0f,
//				0.0f, };
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

		//gl.glTranslatef(x, y, z);
		//gl.glRotatef(degree, 0, 1, 0);
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

	public Vector3f[][] getVector3f() {
		Vector3f[][] triangles = new Vector3f[2][3];
		triangles[0] = new Vector3f[] {
				new Vector3f(vertex[0], vertex[1], vertex[2]),
				new Vector3f(vertex[3], vertex[4], vertex[5]),
				new Vector3f(vertex[6], vertex[7], vertex[8]),};

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

	public float getDegree() {
		return degree;
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