package com.sw.minavi.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sw.minavi.R;

public class LookAtView extends TableLayout {

	/** Called when the activity is first created. */
	private TextView eyeX;
	private TextView eyeY;
	private TextView eyeZ;
	private TextView centerX;
	private TextView centerY;
	private TextView centerZ;

	public LookAtView(Context context) {
		super(context);

		View layout = LayoutInflater.from(context).inflate(R.layout.look_at,
				this);

		this.eyeX = (TextView) layout.findViewById(R.id.eyeXValue);
		this.eyeY = (TextView) layout.findViewById(R.id.eyeYValue);
		this.eyeZ = (TextView) layout.findViewById(R.id.eyeZValue);
		this.centerX = (TextView) layout.findViewById(R.id.centerXValue);
		this.centerY = (TextView) layout.findViewById(R.id.centerYValue);
		this.centerZ = (TextView) layout.findViewById(R.id.centerZValue);
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
