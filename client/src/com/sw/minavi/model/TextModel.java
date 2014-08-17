package com.sw.minavi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.opengl.GLUtils;

public class TextModel {
	private FloatBuffer buffer; // 頂点用バッファ
	private float[] vertex;
	private float x, y, z;
	private int degree;
	private float[] positions;
	private FloatBuffer posBuffer;
	private String text;
	private int[] textures;

	public TextModel(float x, float y, float z, int degree, String text, int[] textures) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.degree = degree;
		this.text = text;
		this.textures = textures;

		vertex = new float[] { 0.0f, 0.0f,// 左上
				0.0f, 1.0f,// 左下
				1.0f, 0.0f,// 右上
				1.0f, 1.0f,// 右下
		};
		ByteBuffer vb = ByteBuffer.allocateDirect(vertex.length * 4);
		vb.order(ByteOrder.nativeOrder());
		buffer = vb.asFloatBuffer();
		buffer.put(vertex);
		buffer.position(0);

		positions = new float[] { -1.0f, 1.0f, 0.0f, // 左上（uv一行目に対応）
				-1.0f, -1.0f, 0.0f, // 左下（uv二行目に対応）
				1.0f, 1.0f, 0.0f, // 右上（uv三行目に対応）
				1.0f, -1.0f, 0.0f, // 右下（uv四行目に対応）
		};

		ByteBuffer pb = ByteBuffer.allocateDirect(positions.length * 4);
		pb.order(ByteOrder.nativeOrder());
		posBuffer = pb.asFloatBuffer();
		posBuffer.put(positions);
		posBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glPushMatrix(); // マトリックス記憶

		gl.glTranslatef(x, y, z);
		gl.glRotatef(degree, 0, 1, 0);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		Bitmap bitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		{
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			canvas.drawColor(0);
			canvas.drawText(text, 0, 15, paint);
		}

		// テクスチャ情報の設定
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// bitmapを破棄
		bitmap.recycle();

		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, buffer);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, posBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glPopMatrix(); // マトリックスを戻す
	}
}
