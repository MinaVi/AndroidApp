package com.sw.minavi.item;

import java.text.MessageFormat;

import javax.vecmath.Vector3f;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sw.minavi.R;

public class CustomView extends LinearLayout {

	private TextView currentAzimuth;
	private TextView currentRoll;
	private TextView currentPitch;
	private TextView currentEye;
	private TextView currentLook;
	private TextView currentUp;
	private ProduceView produce;

	private MessageFormat xyzFormat = new MessageFormat("x:{0}, y:{1}, z:{2}");

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View layout = LayoutInflater.from(context).inflate(R.layout.custom_ar_info, this);
		currentAzimuth = (TextView) layout.findViewById(R.id.currentAzimuth);
		currentRoll = (TextView) layout.findViewById(R.id.currentRoll);
		currentPitch = (TextView) layout.findViewById(R.id.currentPitch);
		currentUp = (TextView) layout.findViewById(R.id.currentUp);

		currentEye = (TextView) layout.findViewById(R.id.currentEye);
		currentLook = (TextView) layout.findViewById(R.id.currentLook);

		produce = (ProduceView) layout.findViewById(R.id.produce);
	}

	public void setGyroSensorValues(int azimuth, int roll, int pitch) {
		currentAzimuth.setText(String.valueOf(azimuth));
		currentRoll.setText(String.valueOf(roll));
		currentPitch.setText(String.valueOf(pitch));
	}

	public void setCameraValues(Camera3D camera) {
		currentEye.setText(formatCameraValue(camera.getEye()));
		currentLook.setText(formatCameraValue(camera.getLook()));
		currentUp.setText(formatCameraValue(camera.getUp()));
	}

	private String formatCameraValue(Vector3f vec) {
		return xyzFormat.format(new Object[] { vec.x, vec.y, vec.z });
	}

	public ProduceView getProduce() {
		return produce;
	}
}
