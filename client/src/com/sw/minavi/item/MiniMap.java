package com.sw.minavi.item;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

import com.sw.minavi.model.Model;
import com.sw.minavi.util.LocationUtilities;

public class MiniMap extends View {
	public static final int CUBE_COLOR = Color.rgb(10, 10, 10);
	public static final int DOMINO_COLOR = Color.GRAY;

	private Canvas canvas;
	private Paint paint;

	private List<MapPoint> points = new ArrayList<MiniMap.MapPoint>();

	private int azimuth;

	private class MapPoint {
		private float x, y;
		private int color;

		private MapPoint() {
		}

		private MapPoint(float x, float y, int color) {
			this.x = x;
			this.y = y;
			this.color = color;
		}
	}

	public MiniMap(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.paint = new Paint();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (this.canvas == null) {
			this.canvas = canvas;
		}

		final float centerX = canvas.getWidth() / 2;
		final float centerY = canvas.getHeight() / 2;
		final float outCircleRadius = centerX;
		final float inCircleRadius = outCircleRadius - 20;

		// ミニマップの縁を描画
		this.paint.setColor(Color.rgb(50, 50, 50));
		this.paint.setAlpha(255);
		canvas.drawCircle(centerX, centerY, outCircleRadius, this.paint);

		// ミニマップの背景を描画
		this.paint.setColor(Color.rgb(10, 10, 10));
		this.paint.setAlpha(255);
		canvas.drawCircle(centerX, centerY, inCircleRadius, this.paint);

		// 方位：北の描画
		this.paint.setAntiAlias(true);
		this.paint.setStrokeWidth(5.0f);
		this.paint.setColor(Color.rgb(255, 255, 255));
		this.paint.setAlpha(0x77);
		this.paint.setTextSize(35);
		this.paint.setTextAlign(Align.CENTER);
		this.paint.setStyle(Paint.Style.STROKE);
		canvas.drawText("N", centerX, centerY - inCircleRadius + 10, this.paint);

		this.paint.setAntiAlias(true);
		this.paint.setStrokeWidth(0);
		this.paint.setColor(Color.rgb(255, 70, 70));
		this.paint.setAlpha(255);
		this.paint.setTextSize(35);
		this.paint.setTextAlign(Align.CENTER);
		this.paint.setStyle(Paint.Style.FILL);
		canvas.drawText("N", centerX, centerY - inCircleRadius + 10, this.paint);

		// カメラ位置の描画
		this.paint.setColor(Color.rgb(255, 0, 0));
		this.paint.setAlpha(255);
		canvas.drawCircle(centerX, centerY, 5, this.paint);

		// 画角線の描画
		double azimuthRightRad = LocationUtilities.degreesToRads(LocationUtilities.correctAzimuth(azimuth) + 270 + 30);
		float rightX = (float) (Math.cos(azimuthRightRad) * inCircleRadius + centerX);
		float rightY = (float) (Math.sin(azimuthRightRad) * inCircleRadius + centerY);

		double azimuthLeftRad = LocationUtilities.degreesToRads(LocationUtilities.correctAzimuth(azimuth) + 270 - 30);
		float leftX = (float) (Math.cos(azimuthLeftRad) * inCircleRadius + centerX);
		float leftY = (float) (Math.sin(azimuthLeftRad) * inCircleRadius + centerY);

		this.paint.setColor(Color.rgb(255, 255, 255));
		this.paint.setAlpha(255);
		canvas.drawLine(centerX, centerY, rightX, rightY, this.paint);
		canvas.drawLine(centerX, centerY, leftX, leftY, this.paint);

		canvas.save();
		canvas.rotate(70, centerX, centerY);
		for (MapPoint mp : points) {
			this.paint.setColor(mp.color);
			canvas.drawCircle(mp.x, mp.y, 5, paint);
		}
		canvas.restore();
	}

	public void syncMap(Camera3D camera, List<Model> models, int azimuth) {

		this.azimuth = azimuth;

		points.clear();
		if (canvas != null) {
			for (Model model : models) {
				float x = model.getX();
				float z = model.getZ();

				MapPoint point = new MapPoint();
				convertPoint3Dto2D(point, camera.getEye(), x, z, Color.WHITE);
				points.add(point);
			}
		}

		this.invalidate();
	}

	private MapPoint convertPoint3Dto2D(MapPoint mp, Vector3f eye, float convX, float convY, int color) {
		final float x3D = convX;
		final float y3D = convY;

		final int maxScope3D = 50;
		final int maxScope2D = canvas.getWidth();

		final float cen3D = maxScope3D / 2;
		final float cen2D = maxScope2D / 2;

		mp.x = (((eye.x - x3D) * cen2D) / cen3D) + cen2D;
		mp.y = maxScope2D - ((((eye.y - y3D) * cen2D) / cen3D) + cen2D);
		mp.color = color;

		return mp;
	}
}
