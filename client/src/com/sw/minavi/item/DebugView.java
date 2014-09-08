package com.sw.minavi.item;

import javax.vecmath.Vector3f;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sw.minavi.R;

public class DebugView extends TableLayout {

	/** Called when the activity is first created. */
	// opengl
	private TextView eyeX;
	private TextView eyeY;
	private TextView eyeZ;
	private TextView lookX;
	private TextView lookY;
	private TextView lookZ;
	// sensor
	private TextView latitude;
	private TextView longitude;
	private TextView azimuth;
	private TextView roll;
	private TextView pitch;
	private TextView talkGroupId;

	public DebugView(Context context) {
		super(context);

		View layout = LayoutInflater.from(context).inflate(R.layout.debug_view,
				this);

		// opengl
		this.eyeX = (TextView) layout.findViewById(R.id.eyeXValue);
		this.eyeY = (TextView) layout.findViewById(R.id.eyeYValue);
		this.eyeZ = (TextView) layout.findViewById(R.id.eyeZValue);
		this.lookX = (TextView) layout.findViewById(R.id.lookXValue);
		this.lookY = (TextView) layout.findViewById(R.id.lookYValue);
		this.lookZ = (TextView) layout.findViewById(R.id.lookZValue);

		// sensor
		this.latitude = (TextView) layout.findViewById(R.id.latitudeValue);
		this.longitude = (TextView) layout.findViewById(R.id.longitudeValue);
		this.azimuth = (TextView) layout.findViewById(R.id.azimuthValue);
		this.roll = (TextView) layout.findViewById(R.id.rollValue);
		this.pitch = (TextView) layout.findViewById(R.id.pitchValue);
		
		// Id
		this.talkGroupId = (TextView) layout.findViewById(R.id.talkGroupIdValue);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void updateStatus(Camera3D camera) {

		Vector3f eye = camera.getEye();
		Vector3f look = camera.getLook();
		Vector3f up = camera.getUp();

		this.eyeX.setText(String.valueOf(eye.x));
		this.eyeY.setText(String.valueOf(eye.y));
		this.eyeZ.setText(String.valueOf(eye.z));
		this.lookX.setText(String.valueOf(look.x));
		this.lookY.setText(String.valueOf(look.y));
		this.lookZ.setText(String.valueOf(look.z));
	}

	public void updateLocation(double latitude, double longitude) {
		this.latitude.setText(String.valueOf(latitude));
		this.longitude.setText(String.valueOf(longitude));
	}

	public void updateSensor(int azimuth, float roll, float pitch) {
		this.azimuth.setText(String.valueOf(azimuth));
		this.roll.setText(String.valueOf(roll));
		this.pitch.setText(String.valueOf(pitch));

	}
	
	public void updateID(int id) {
		this.talkGroupId.setText(String.valueOf(id));
	}
}
