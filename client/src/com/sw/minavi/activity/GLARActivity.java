package com.sw.minavi.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.vecmath.Vector3f;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.sw.minavi.R;
import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.LocalItemTableManager;
import com.sw.minavi.http.TransportLog;
import com.sw.minavi.item.ARGLSurfaceView;
import com.sw.minavi.item.DebugView;
import com.sw.minavi.item.GLCameraView;
import com.sw.minavi.item.LocalItem;
import com.sw.minavi.item.MiniMap;
import com.sw.minavi.item.SensorFilter;
import com.sw.minavi.model.Model;
import com.sw.minavi.util.LocationUtilities;

public class GLARActivity extends Activity implements SensorEventListener,
		LocationListener, OnClickListener {

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
	private DebugView debugView;
	private MiniMap miniMap;

	// 設定マネージャー
	private SharedPreferences sPref;
	private Handler mHandler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// レイアウトを設定
		setContentView(R.layout.activity_gl);

		// センサーサービスの起動
		initSensorService();

		// 位置情報サービスの起動
		initLocationService();

		// SQLiteへのアクセス準備
		initDataBaseManage();

		// handler準備
		mHandler = new Handler() {
			public void handleMassage(Message msg) {
				// メッセージ表示

			};
		};
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

			int dir = this.getWindowManager().getDefaultDisplay().getRotation();

			switch (dir) {
			case Surface.ROTATION_0:
				SensorManager.remapCoordinateSystem(inRotationMatrix,
						SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
						outRotationMatrix);
				SensorManager.getOrientation(outRotationMatrix, attitude);
				break;

			case Surface.ROTATION_90:
				SensorManager.getOrientation(inRotationMatrix, attitude);
				break;

			case Surface.ROTATION_180:
				SensorManager.remapCoordinateSystem(inRotationMatrix,
						SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
						outRotationMatrix);
				SensorManager.getOrientation(outRotationMatrix, attitude);
				break;

			case Surface.ROTATION_270:
				SensorManager.remapCoordinateSystem(inRotationMatrix,
						SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
						outRotationMatrix);
				SensorManager.getOrientation(outRotationMatrix, attitude);
				break;

			default:
				break;
			}

			// 方位(ラジアン)、ピッチ、ロールを取得
			azimuthRad = attitude[0];
			pitch = attitude[1];
			roll = attitude[2];
			isGetAzimuth = true;

			if (myGLSurfaceView != null) {
				myGLSurfaceView.changeAzimuthEvent(azimuthRad, pitch, roll);
			}

			if (debugView != null) {
				debugView.updateSensor(
						LocationUtilities.radianToDegreeForAzimuth(azimuthRad),
						roll, pitch);
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
			// location情報をサーバーへ送信
			TransportLog tl = new TransportLog(this, mHandler);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd kk':'mm':'ss':'");

//			tl.execute(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()),
//					String.valueOf(location.getAltitude())
//					, String.valueOf(location.getAccuracy()), String.valueOf(location.getSpeed()),
//					sdf.format(date), String.valueOf(0), sPref.getString("name", "unknown"));

			// ロード中の座標を更新
			loadLocation = location;

			isGetLocation = true;

			// Toast.makeText(this,
			// location.getLatitude() + "," + location.getLongitude(),
			// Toast.LENGTH_SHORT).show();
			addViews();

		} else {
			// 2回目の座標取得
			double lengthInMeter = LocationUtilities
					.getDistance(location.getLatitude(),
							location.getLongitude(),
							loadLocation.getLatitude(),
							loadLocation.getLongitude(), 10) * 1000;

			if (lengthInMeter < 50) {
				// Toast.makeText(
				// this,
				// location.getLatitude() + "," + location.getLongitude()
				// + "," + lengthInMeter + "m", Toast.LENGTH_SHORT)
				// .show();
				return;
			}

			// ロード中の座標を更新
			loadLocation = location;

			if (debugView != null) {
				debugView.updateLocation(loadLocation.getLatitude(),
						loadLocation.getLongitude());
			}
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
//		loadLocation.setLongitude(141.343739);
//		loadLocation.setLatitude(43.072665);
		locationItems = LocalItemTableManager.getInstance(helper).GetAroundRecords(loadLocation);

		// ジェスチャーを検出する
		// this.gesDetector = new GestureDetector(this, myGLSurfaceView);

		// OpenGL用のビューの生成
		TextView produceText = new TextView(this);//;(TextView) findViewById(R.id.produceText);
		this.debugView = new DebugView(this);
		this.cameraView = new GLCameraView(this);
		this.miniMap = new MiniMap(this);
		this.myGLSurfaceView = new ARGLSurfaceView(this, loadLocation,
				locationItems, debugView, miniMap, produceText);

		// 生成したビューを画面に追加
		setContentView(myGLSurfaceView);
		addContentView(cameraView, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		addContentView(debugView, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		addContentView(miniMap, new LayoutParams(300,
				400));
		addContentView(produceText, new LayoutParams(300,
				400));
		miniMap.setVisibility(View.GONE);

		// デバッグ情報の更新
		debugView.updateLocation(loadLocation.getLatitude(),
				loadLocation.getLongitude());
	}

	@Override
	public void onClick(View v) {

		// 中心との最近隣を計算
		float nearDist = 1000;
		int id = 0;
		for (Model m : this.myGLSurfaceView.models) {
			Vector3f eye = this.myGLSurfaceView.camera.getEye();
			float ds = m.distance(eye.x, eye.y, eye.z);
			if(ds < nearDist){
				nearDist = ds;
				id = m.getItem().getTalkGroupId();
			}
		}

		debugView.updateID(id);


		if(id != 0){
//			Intent intent = new Intent();
//			intent.setClassName("com.sw.minavi",
//					"com.sw.minavi.activity.TalkActivity");
//			intent.putExtra("pinId", 0);
//			intent.putExtra("talkGroupId", this.myGLSurfaceView.centerObjectId);
//			startActivity(intent);
//			finish();
		}
	}

}