package com.sw.minavi.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;

import com.sw.minavi.R;
import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.EmergencyItemTableManager;
import com.sw.minavi.activity.db.LocalItemTableManager;
import com.sw.minavi.activity.db.DatabaseOpenHelper.EmergencyItemTable;
import com.sw.minavi.activity.db.DatabaseOpenHelper.LocalItemTable;
import com.sw.minavi.http.GetGnaviItems;
import com.sw.minavi.http.TransportLog;
import com.sw.minavi.http.GetGnaviItems.AsyncTaskCallback;
import com.sw.minavi.item.ARGLSurfaceView;
import com.sw.minavi.item.BgmManager;
import com.sw.minavi.item.CustomView;
import com.sw.minavi.item.LocalItem;
import com.sw.minavi.item.SensorFilter;
import com.sw.minavi.item.ToastRunnable;
import com.sw.minavi.model.Model;
import com.sw.minavi.util.LocationUtilities;

public class GLARActivity extends Activity implements SensorEventListener,
		LocationListener, OnClickListener, AsyncTaskCallback {

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
	private ArrayList<LocalItem> EmergencyItems = new ArrayList<LocalItem>();

	/** 現在ロードしている座標 */
	private Location loadLocation = null;

	/** 現在位置取得フラグ */
	private boolean isGetLocation = false;
	/** 方位取得フラグ */
	private boolean isGetAzimuth = false;

	/** GLSurfaceView */
	private ARGLSurfaceView glSurfaceView;
	/** GLSurfaceViewに重ねる付加情報ビュー */
	private CustomView customView;

	// 設定マネージャー
	private SharedPreferences sPref;
	boolean emeFlg = false;

	private Handler mHandler;
	private ToastRunnable toastRunnable;

	private ArrayList<LocalItem> gNaviItems = new ArrayList<LocalItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// レイアウトを設定
		setContentView(R.layout.activity_gl);

		// モード判定
		sPref = PreferenceManager.getDefaultSharedPreferences(this);
		emeFlg = sPref.getBoolean("pref_emergency_flag", false);

		toastRunnable = new ToastRunnable(this);
		toastRunnable.run();

		// センサーサービスの起動
		initSensorService();

		// 位置情報サービスの起動
		initLocationService();

		// SQLiteへのアクセス準備
		initDataBaseManage();

		// レイアウト上の各ビューを取得
		findViews();

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

		// BGM復帰
		if (emeFlg == false) {
			BgmManager.newIntance(getApplicationContext()).playSound(R.raw.spring_wind);
		}
		// 加速度センサの登録
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);

		// 地磁気センサの登録
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);

		// 位置情報センサの登録
		initLocationService();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// BGM停止
		BgmManager.newIntance(getApplicationContext()).playSound(-1);

		// SensorManagerの解除
		sensorManager.unregisterListener(this);
		BgmManager.newIntance(getApplicationContext()).playSound(R.raw.spring_wind);

		// LocationManagerの解除
		locationManager.removeUpdates(this);
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
			float azimuth = attitude[0];
			float pitch = attitude[1];
			float roll = attitude[2];

			// ジャイロセンサー情報が取得済みであることを通知
			isGetAzimuth = true;

			// GLSurfaceViewの方位更新イベントを実行
			glSurfaceView.changeAzimuthEvent(azimuth, pitch, roll);
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		if (loadLocation == null) {
			// 1回目の座標取得
			// location情報をサーバーへ送信
			TransportLog tl = new TransportLog(this, mHandler);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd kk':'mm':'ss':'");

			tl.execute(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()),
					String.valueOf(location.getAltitude())
					, String.valueOf(location.getAccuracy()), String.valueOf(location.getSpeed()),
					sdf.format(date), String.valueOf(0), sPref.getString("name", "unknown"));

			// ロード中の座標を更新
			loadLocation = location;

			// 現在位置が取得済みであることを通知
			isGetLocation = true;

			// デバッグ用。現在位置の通知
			//			Toast.makeText(this,
			//					location.getLatitude() + "," + location.getLongitude(),
			//					Toast.LENGTH_SHORT).show();

			// 位置情報更新イベントの実行
			// ぐるナビ情報
			GetGnaviItems gnaviAsync = new GetGnaviItems(this);
			gnaviAsync.execute(String.valueOf(loadLocation.getLatitude()),String.valueOf(loadLocation.getLongitude()));
			
			updateLocationEvent();

		} else {

			// 2回目の座標取得
			double lengthInMeter = LocationUtilities
					.getDistance(location.getLatitude(),
							location.getLongitude(),
							loadLocation.getLatitude(),
							loadLocation.getLongitude(), 10) * 1000;

			// 前回のロード座標から50m以内であれば位置情報を更新しない
			if (lengthInMeter < 50) {

				// アイテム表示がない場合、念のためチェック
				if (locationItems.size() == 0) {
					// 位置情報更新イベントの実行
					updateLocationEvent();
				}

				return;
			}

			// ロード中の座標を更新
			loadLocation = location;

			// 位置情報更新イベントの実行
			updateLocationEvent();
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

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
//		LocalItemTableManager.getInstance(helper).InsertSample();
		EmergencyItemTableManager.getInstance(helper).InsertSample();
		String lang = sPref.getString("lang", "Japanese");
		LocalItemTableManager.lang = lang;
		EmergencyItemTableManager.lang = lang;
	}

	private void findViews() {
		glSurfaceView = (ARGLSurfaceView) findViewById(R.id.arGLSurfaceView);
		customView = (CustomView) findViewById(R.id.customView);
		glSurfaceView.setCustomView(customView);
	}

	private void updateLocationEvent() {

		// 方位と位置の両方が取得出来ていることを確認
		if (isGetAzimuth == false || isGetLocation == false) {
			return;
		}
		toastRunnable.stop();

		// 周辺情報を取得
		// GLSurfaceViewに登録されている位置情報、ロケーション情報を更新
		if (emeFlg == true) {
			EmergencyItems.clear();
			locationItems.clear();
			EmergencyItems.addAll(EmergencyItemTableManager.getInstance(helper).GetAroundRecords(loadLocation));
			EmergencyItems.addAll(gNaviItems);
			glSurfaceView.updateLocation(loadLocation, EmergencyItems);
		} else {
			EmergencyItems.clear();
			locationItems.clear();
			locationItems.addAll(LocalItemTableManager.getInstance(helper).GetAroundRecords(loadLocation));
			locationItems.addAll(gNaviItems);
			glSurfaceView.updateLocation(loadLocation, locationItems);
		}
	}

	@Override
	public void onClick(View v) {

		// 中心との最近隣を計算
		float nearDist = 1000;
		int id = 0;
		for (Model m : this.glSurfaceView.models) {
			Vector3f eye = this.glSurfaceView.camera.getEye();
			float ds = m.distance(eye.x, eye.y, eye.z);
			if (ds < nearDist) {
				nearDist = ds;
				id = m.getItem().getTalkGroupId();
			}
		}

		if (id != 0) {
			//			Intent intent = new Intent();
			//			intent.setClassName("com.sw.minavi",
			//					"com.sw.minavi.activity.TalkActivity");
			//			intent.putExtra("pinId", 0);
			//			intent.putExtra("talkGroupId", this.myGLSurfaceView.centerObjectId);
			//			startActivity(intent);
			//			finish();
		}
	}

	@Override
	public void preExecute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postExecute(ArrayList<HashMap<String, String>> result) {
		// TODO Auto-generated method stub
		
		gNaviItems = new ArrayList<LocalItem>();
		
		for (HashMap<String, String> gItem : result) {
			LocalItem li = new LocalItem();
			li.setMessage(gItem.get("name"));
			li.setLon(Double.valueOf(gItem.get("lon")).doubleValue());
			li.setLat(Double.valueOf(gItem.get("lat")).doubleValue());
			li.setArImageName("imgres");
			li.setIconImageName("imgres");
			li.setSpecialLonMin(-1.0);
			li.setSpecialLatMin(-1.0);
			li.setSpecialLonMax(-1.0);
			li.setSpecialLatMax(-1.0);
			li.setCreateTime("");

			gNaviItems.add(li);
			
		}
	}

	@Override
	public void progressUpdate(int progress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

}