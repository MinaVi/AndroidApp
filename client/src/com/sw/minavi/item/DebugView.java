package com.sw.minavi.item;

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
	private TextView centerX;
	private TextView centerY;
	private TextView centerZ;
	// sensor
	private TextView latitude;
	private TextView longitude;
	private TextView azimuth;
	private TextView roll;
	private TextView pitch;

	public DebugView(Context context) {
		super(context);

		View layout = LayoutInflater.from(context).inflate(R.layout.debug_view,
				this);

		// opengl
		this.eyeX = (TextView) layout.findViewById(R.id.eyeXValue);
		this.eyeY = (TextView) layout.findViewById(R.id.eyeYValue);
		this.eyeZ = (TextView) layout.findViewById(R.id.eyeZValue);
		this.centerX = (TextView) layout.findViewById(R.id.centerXValue);
		this.centerY = (TextView) layout.findViewById(R.id.centerYValue);
		this.centerZ = (TextView) layout.findViewById(R.id.centerZValue);

		// sensor
		this.latitude = (TextView) layout.findViewById(R.id.latitudeValue);
		this.longitude = (TextView) layout.findViewById(R.id.longitudeValue);
		this.azimuth = (TextView) layout.findViewById(R.id.azimuthValue);
		this.roll = (TextView) layout.findViewById(R.id.rollValue);
		this.pitch = (TextView) layout.findViewById(R.id.pitchValue);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void updateStatus(float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ, float upX, float upY, float upZ) {
		this.eyeX.setText(String.valueOf(eyeX));
		this.eyeY.setText(String.valueOf(eyeY));
		this.eyeZ.setText(String.valueOf(eyeZ));
		this.centerX.setText(String.valueOf(centerX));
		this.centerY.setText(String.valueOf(centerY));
		this.centerZ.setText(String.valueOf(centerZ));
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
}
