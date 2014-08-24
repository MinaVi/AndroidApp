package com.sw.minavi.item;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.opengl.GLU;

public class Camera3D {
	private Context context;

	private Vector3f eye = new Vector3f(2f, 10f, 3f);
	private Vector3f look = new Vector3f();
	private Vector3f up = new Vector3f(0, 0, 1);

	public Camera3D(Context context, float[] eye, float[] look, float[] up) {
		this.context = context;
		this.eye = new Vector3f(eye);
		this.look = new Vector3f(look);
		this.up = new Vector3f(up);
	}

	public void gluLookAt(GL10 gl) {
		GLU.gluLookAt(gl, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);
	}

	public Vector3f getEye() {
		return this.eye;
	}

	public Vector3f getLook() {
		return this.look;
	}

	public Vector3f getUp() {
		return this.up;
	}

	public void rotateLook(float azimuthRad, float roll) {
		float x = (float) (Math.cos(azimuthRad) * eye.z + eye.x);
		float y = (float) ((Math.cos(roll) * eye.z + eye.y) * -1.0);
		float z = (float) (Math.sin(azimuthRad) * eye.z + eye.z);

		setLook(x, y, z);
	}

	public void setEye(float x, float y, float z) {
		this.eye.x = x;
		this.eye.y = y;
		this.eye.z = z;
	}

	public void setLook(float x, float y, float z) {
		this.look.x = x;
		this.look.y = y;
		this.look.z = z;
	}
}
