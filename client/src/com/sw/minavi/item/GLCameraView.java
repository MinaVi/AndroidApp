package com.sw.minavi.item;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class GLCameraView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder;
	private Camera camera;
	private Context activityContext;

	public GLCameraView(Context context) {
		super(context);
		this.activityContext = context;

		// サーフェイスホルダーの生成
		holder = getHolder();
		holder.addCallback(this);

		// プッシュバッッファの指定
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public GLCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// サーフェイスホルダーの生成
		holder = getHolder();
		holder.addCallback(this);

		// プッシュバッッファの指定
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		camera.stopPreview();

		List<Size> previewSizes = camera.getParameters()
				.getSupportedPreviewSizes();

		Camera.Parameters parameters = camera.getParameters();
		WindowManager windowManager = (WindowManager) activityContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		int rotation = display.getRotation();
		int direction = 0;
		switch (rotation) {
		case Surface.ROTATION_0: {
			direction = 90;
			for (Size size : previewSizes) {
				try {
					parameters.setPreviewSize(size.width, size.height);
					break;
				} catch (RuntimeException e) {
				}
			}
			break;
		}
		case Surface.ROTATION_90: {
			direction = 0;
			Size size = previewSizes.get(0);
			parameters.setPreviewSize(size.width, size.height);
			break;
		}
		case Surface.ROTATION_180: {
			direction = 270;
			for (Size size : previewSizes) {
				try {
					parameters.setPreviewSize(size.width, size.height);
					break;
				} catch (RuntimeException e) {
				}
			}
			break;
		}
		case Surface.ROTATION_270: {
			direction = 180;
			Size size = previewSizes.get(0);
			parameters.setPreviewSize(size.width, size.height);
			break;
		}
		}
		camera.setDisplayOrientation(direction);
		parameters.setRotation(direction);
		// camera.setPreviewDisplay(getHolder());

		camera.setParameters(parameters);
		camera.startPreview();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// カメラの初期化
		try {
			camera = Camera.open();
			// _camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(holder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// カメラのプレビュー停止
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}
}