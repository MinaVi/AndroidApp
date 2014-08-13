package com.sw.minavi.item;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GLCameraView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder _holder;
	private Camera _camera;

	public GLCameraView(Context context) {
		super(context);

		// サーフェイスホルダーの生成  
		_holder = getHolder();
		_holder.addCallback(this);

		// プッシュバッッファの指定  
		_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public GLCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// サーフェイスホルダーの生成  
		_holder = getHolder();
		_holder.addCallback(this);

		// プッシュバッッファの指定  
		_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// カメラのプレビュー開始  
		Camera.Parameters parameters = _camera.getParameters();
		List<Size> previewSizes = _camera.getParameters().getSupportedPreviewSizes();
		Size size = previewSizes.get(0);
		parameters.setPreviewSize(size.width, size.height);
		_camera.setParameters(parameters);
		_camera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// カメラの初期化  
		try {
			_camera = Camera.open();
			//_camera.setDisplayOrientation(90);
			_camera.setPreviewDisplay(holder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// カメラのプレビュー停止  
		_camera.setPreviewCallback(null);
		_camera.stopPreview();
		_camera.release();
		_camera = null;
	}
}