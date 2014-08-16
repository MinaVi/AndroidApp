package com.sw.minavi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout.LayoutParams;

import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.LocalItemTableManager;
import com.sw.minavi.item.ARGLSurfaceView;
import com.sw.minavi.item.GLCameraView;
import com.sw.minavi.item.LocalItem;
import com.sw.minavi.item.LookAtView;
import com.sw.minavi.item.SensorFilter;
import com.sw.minavi.util.LocationUtilities;

public class GLARActivity extends Activity implements SensorEventListener,
		LocationListener {

	/** ジェスチャー検出器 */
	private GestureDetector gesDetector = null;
	/** センサ管理 */
	private SensorManager sensorManager;

	/** 加速度センサのフィルター */
	private SensorFilter gravitySensorFilter = new SensorFilter();
	/** 地磁気センサのフィルター */
	private SensorFilter magneticSensorFilter = new SensorFilter();
	/** 位置情報管理 */
	private LocationManager locationManager;
	/** プロバイダ */
	private List<String> providers;

	/** 加速度と地磁気から求めた回転行列 */
	private float[] inRotationMatrix = new float[9];
	/** 世界測地系に変換した回転行列 */
	private float[] outRotationMatrix = new float[9];
	/** 加速度センサの取得値 */
	private float[] gravity = null;
	/** 地磁気センサの取得値 */
	private float[] geomagnetic = null;
	/** 回転角 */
	private float[] attitude = new float[3];
	/** DB操作オブジェクト */
	private DatabaseOpenHelper helper;

	/** 座標アイテム */
	private ArrayList<LocalItem> locationItems = new ArrayList<LocalItem>();

	/** 現在ロードしている座標 */
	private Location loadLocation = null;

	private float azimuthRad;
	private float pitch;
	private float roll;

	private boolean isGetLocation = false;
	private boolean isGetAzimuth = false;

	private ARGLSurfaceView myGLSurfaceView;
	private GLCameraView cameraView;
	private LookAtView lookAtView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// レイアウトを設定
		// setContentView(R.layout.activity_main);

		// センサーサービスの起動
		initSensorService();

		// 位置情報サービスの起動
		initLocationService();

		// SQLiteへのアクセス準備
		initDataBaseManage();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 加速度センサの登録
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);

		// 地磁気センサの登録
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// SensorManagerの解除
		sensorManager.unregisterListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gesDetector != null) {
			return gesDetector.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// イベントが発生したセンサがどのセンサか判定する
		switch (event.sensor.getType()) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			// 地磁気センサから地磁気を取得
			geomagnetic = event.values.clone();

			// ノイズの除去。サンプルが揃うまでは描画処理を行わない
			magneticSensorFilter.addSample(geomagnetic);
			if (!magneticSensorFilter.isSampleEnable()) {
				return;
			}
			geomagnetic = magneticSensorFilter.getParam();
			break;
		case Sensor.TYPE_ACCELEROMETER:
			// 加速度センサから加速度を取得
			gravity = event.values.clone();

			// ノイズの除去。サンプルが揃うまでは描画処理を行わない
			gravitySensorFilter.addSample(gravity);
			if (!gravitySensorFilter.isSampleEnable()) {
				return;
			}
			gravity = gravitySensorFilter.getParam();
			break;
		}

		// 地磁気センサと加速度センサの両方が取得出来た時だけ、画面を更新する
		if (geomagnetic != null && gravity != null) {

			// 回転行列の取得
			// <メモ>
			// 第1引数:回転行列(参照渡しで設定される)
			// 第2引数:傾きに関わるらしいが..用途不明
			// 第3引数:加速度センサの取得値
			// 第4引数:地磁気センサの取得値
			SensorManager.getRotationMatrix(inRotationMatrix, null, gravity,
					geomagnetic);

			// 回転行列を世界座標系を変換する
			SensorManager.remapCoordinateSystem(inRotationMatrix,
					SensorManager.AXIS_X, SensorManager.AXIS_Y,
					outRotationMatrix);

			// 回転角の取得
			SensorManager.getOrientation(inRotationMatrix, attitude);

			// 方位(ラジアン)、ピッチ、ロールを取得
			azimuthRad = attitude[0];
			pitch = attitude[1];
			roll = attitude[2];
			isGetAzimuth = true;

			if (myGLSurfaceView != null) {
				myGLSurfaceView.changeCenterPos(azimuthRad, pitch, roll);
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		if (isGetAzimuth == false) {
			return;
		}

		if (loadLocation == null) {
			// 1回目の座標取得

			// ロード中の座標を更新
			loadLocation = location;

			isGetLocation = true;

//			Toast.makeText(this,
//					location.getLatitude() + "," + location.getLongitude(),
//					Toast.LENGTH_SHORT).show();
			addViews();

		} else {
			// 2回目の座標取得
			double lengthInMeter = LocationUtilities
					.getDistance(location.getLatitude(),
							location.getLongitude(),
							loadLocation.getLatitude(),
							loadLocation.getLongitude(), 10) * 1000;

			if (lengthInMeter < 50) {
//				Toast.makeText(
//						this,
//						location.getLatitude() + "," + location.getLongitude()
//								+ "," + lengthInMeter + "m", Toast.LENGTH_SHORT)
//						.show();
				return;
			}

			// ロード中の座標を更新
			loadLocation = location;

			// TODO 再レンダリング
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	private void initSensorService() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	private void initLocationService() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		providers = locationManager.getProviders(true);

		// 全プロバイダの登録
		for (String provider : providers) {
			locationManager.requestLocationUpdates(provider, 0, 1, this);
		}
	}

	private void initDataBaseManage() {
		this.helper = new DatabaseOpenHelper(this);
		LocalItemTableManager.getInstance(helper).InsertSample();
	}

	private void addViews() {

		if (isGetAzimuth == false || isGetLocation == false) {
			return;
		}

		// 周辺情報を取得
		// TODO 範囲によって取得情報をフィルタリング
		locationItems = LocalItemTableManager.getInstance(helper).GetRecords();

		// OpenGL用のビューの生成
		this.lookAtView = new LookAtView(this);
		this.cameraView = new GLCameraView(this);
		this.myGLSurfaceView = new ARGLSurfaceView(this, loadLocation,
				locationItems, lookAtView);

		// ジェスチャーを検出する
		// this.gesDetector = new GestureDetector(this, myGLSurfaceView);

		setContentView(myGLSurfaceView);
		addContentView(cameraView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addContentView(lookAtView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
}