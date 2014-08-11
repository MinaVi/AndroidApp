package com.sw.minavi.item;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * カメラビュー
 *
 * @author RIOH
 *
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	/** サーフェイスホルダー */
	private SurfaceHolder surfaceHolder;
	/** カメラ */
	private Camera camera;

	/**
	 * コンストラクタ
	 *
	 * @param context
	 *            コンテキスト
	 */
	@SuppressWarnings("deprecation")
	public CameraView(Context context) {
		super(context);

		// サーフェイスホルダーの取得とコールバック通知先の指定
		// SURFACE_TYPE_PUSH_BUFFERSの設定はAndroid3.0未満に必要
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@SuppressWarnings("deprecation")
	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// サーフェイスホルダーの取得とコールバック通知先の指定
		// SURFACE_TYPE_PUSH_BUFFERSの設定はAndroid3.0未満に必要
		this.surfaceHolder = this.getHolder();
		this.surfaceHolder.addCallback(this);
		this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	@SuppressWarnings("deprecation")
	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// サーフェイスホルダーの取得とコールバック通知先の指定
		// SURFACE_TYPE_PUSH_BUFFERSの設定はAndroid3.0未満に必要
		this.surfaceHolder = this.getHolder();
		this.surfaceHolder.addCallback(this);
		this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * サーフェイス変更時のイベント
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
			camera.stopPreview();
//			Camera.Parameters param = camera.getParameters();
//			WindowManager wm = (WindowManager) Global.ARAcitivity
//					.getSystemService(Context.WINDOW_SERVICE);
//			Display dp = wm.getDefaultDisplay();
//			int rot = dp.getRotation();
//			int d = 0;
//			switch (rot) {
//			case Surface.ROTATION_0:
//				d = 90;
//				param.setPreviewSize(height, width);
//				break;
//			case Surface.ROTATION_90:
//				d = 0;
//				param.setPreviewSize(width, height);
//				break;
//			case Surface.ROTATION_180:
//				d = 270;
//				param.setPreviewSize(height, width);
//				break;
//			case Surface.ROTATION_270:
//				d = 180;
//				param.setPreviewSize(width, height);
//				break;
//			}
//			camera.setDisplayOrientation(d);
//			param.setRotation(d);
//			// camera.setPreviewDisplay(getHolder());
//
//			camera.setParameters(param);
			camera.startPreview();
	}

	/**
	 * サーフェイスが作成された時のイベント
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// カメラを開く
			this.camera = Camera.open();

			// プレビューディスプレイの設定
			this.camera.setPreviewDisplay(this.surfaceHolder);
		} catch (Exception e) {
			// カメラを解放する
			this.camera.release();
		}
	}

	/**
	 * サーフェイスが破棄された時のイベント
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		// プレビューを停止
		this.camera.stopPreview();

		// カメラを解放
		this.camera.release();
		this.camera = null;
	}
	
	public Camera getCamera() {
		return camera;
	}
}
