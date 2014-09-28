package com.sw.minavi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sw.minavi.R;
import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.LocalItemTableManager;
import com.sw.minavi.item.CameraView;
import com.sw.minavi.item.LocalItem;
import com.sw.minavi.item.PinButton;
import com.sw.minavi.item.SensorFilter;
import com.sw.minavi.util.LocationUtilities;

public class ARAcitivity extends Activity implements SensorEventListener,
		LocationListener {

	/** 画面の分割数 */
	public static final int SECTOR = 24;

	/** センサ管理 */
	protected SensorManager sensorManager;
	/** 位置情報管理 */
	protected LocationManager locationManager;
	/** プロバイダ */
	protected List<String> providers;

	/** デバッグ用：座標 */
	protected TextView geoText;
	/** デバッグ用：ログメッセージ */
	protected TextView logText;
	/** デバッグ用：プロバイダ名 */
	protected TextView provText;
	/** デバッグ用：方位 */
	protected TextView azimuthText;
	/** デバッグ用：ピッチ */
	protected TextView pitchText;
	/** デバッグ用：ロール */
	protected TextView rollText;
	/** デバッグ用：以前の方位 */
	protected TextView preAzimuthText;

	/** サーフェイスビューが配置されるレイアウト */
	protected FrameLayout frameLayout;

	/** DB操作オブジェクト */
	protected DatabaseOpenHelper helper;

	/** 加速度と地磁気から求めた回転行列 */
	float[] inRotationMatrix = new float[9];
	/** 世界測地系に変換した回転行列 */
	float[] outRotationMatrix = new float[9];
	/** 加速度センサの取得値 */
	float[] gravity = new float[3];
	/** 地磁気センサの取得値 */
	float[] geomagnetic = new float[3];
	/** 回転角 */
	float[] attitude = new float[3];

	/** 現在位置 */
	Location curLocation = null;

	/** ピンのIDに対するレイアウト */
	HashMap<Integer, FrameLayout.LayoutParams> pinToLayoutParamsMap = new HashMap<Integer, FrameLayout.LayoutParams>();

	/** 現在の方位角 */
	int azimuth = 0;

	/** 座標アイテム */
	ArrayList<LocalItem> locationItems = new ArrayList<LocalItem>();

	/** 加速度センサのフィルター */
	SensorFilter gravitySensorFilter = new SensorFilter();
	/** 地磁気センサのフィルター */
	SensorFilter magneticSensorFilter = new SensorFilter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ar);

		// 各viewをメンバ変数に設定
		findViews();

		// 位置情報サービスの開始
		initLocationService();

		// 各センサーサービスの開始
		initSensorService();

		// Sampleの登録
		helper = new DatabaseOpenHelper(this);
		LocalItemTableManager.getInstance(helper).InsertSample();

		// デバック時に表示
		TableLayout textViewLayer = (TableLayout) findViewById(R.id.text_view_layer);
		textViewLayer.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 全プロバイダの登録
		for (String provider : providers) {
			locationManager.requestLocationUpdates(provider, 0, 1, this);
		}

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
	public void onPause() {
		super.onPause();

		// SensorManagerの解除
		sensorManager.unregisterListener(this);

		// LocationManagerの解除
		locationManager.removeUpdates(this);
	}

	public void onClickLocal(View v) {

		if (curLocation != null) {
			String desc = curLocation.getLongitude() + ", "
					+ curLocation.getLatitude();
			geoText.setText(desc);

			// 表示情報の一覧を取得(当該座標の直近のデータのみ)
			locationItems = LocalItemTableManager.getInstance(helper).GetRecords();
			// values =
			// LocalItemTableManager.getInstance(helper).GetRecordsDebug();

			// アイテムの描画
			displayItems();
		}
	}

	protected void findViews() {
		frameLayout = (FrameLayout) findViewById(R.id.frame);
		azimuthText = (TextView) findViewById(R.id.azimuth);
		pitchText = (TextView) findViewById(R.id.pitch);
		rollText = (TextView) findViewById(R.id.roll);
		geoText = (TextView) findViewById(R.id.geo);
		logText = (TextView) findViewById(R.id.log);
		provText = (TextView) findViewById(R.id.provider);
		preAzimuthText = (TextView) findViewById(R.id.prevAzimuth);
	}

	protected void initSensorService() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	protected void initLocationService() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		providers = locationManager.getProviders(true);
	}

	protected void initDatabaseHelper() {
		helper = new DatabaseOpenHelper(this);
	}

	private static boolean isDebug = false;

	protected void refreshGeoLocation(Location location) {

		String desc = location.getLongitude() + ", " + location.getLatitude();
		geoText.setText(desc);

		Toast.makeText(ARAcitivity.this, desc, Toast.LENGTH_SHORT).show();

		//TODO 将来的に座標の変動によって座標アイテムを取得するか判定する
		if (isDebug == false) {

			// 表示情報の一覧を取得(当該座標の直近のデータのみ)
			locationItems = LocalItemTableManager.getInstance(helper).GetRecords();
	
			// 現在位置の保存
			curLocation = location;

			for (LocalItem val : locationItems) {

				//TODO 将来的にレイアウト上のピンを全て除外する必要がある
				//				for (int viewIndex = 0; viewIndex < frameLayout.getChildCount(); viewIndex++) {
				//					View childView = frameLayout.getChildAt(viewIndex);
				//					if (childView.getClass().equals(PinButton.class)) {
				//						frameLayout.removeViewAt(viewIndex);
				//					}
				//				}
				
				// 描画許可・不許可問わず利用するので、このタイミングでピンを作成
				PinButton pin = createPiButton(val, 0, 0);

				// 仮のレイアウトを作成
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(0, 0);

				// 全てのピンをレイアウトに追加
				frameLayout.addView(pin, pin.getId(), layoutParams);

				pin.setVisibility(View.INVISIBLE);
			}
			
			displayItems();
			
			isDebug = true;
		}
	}

	protected void displayItems() {

		if (curLocation == null) {
			return;
		}

		double curLat = curLocation.getLatitude();
		double curLon = curLocation.getLongitude();
		// double curLon = 139.701334;
		// double curLat = 35.658517;

		HashMap<Integer, FrameLayout.LayoutParams> addPinIdToLayoutParamsMap = new HashMap<Integer, FrameLayout.LayoutParams>();
		for (LocalItem locationItem : locationItems) {

			// 2点間の距離を計算(当該座標とイベント座標)
			float distance = LocationUtilities.getDistance(curLat, curLon,
					locationItem.getLat(), locationItem.getLon(), 6) * 1000; // [km]から[m]に変換

			// イベント座標が存在する方位角を計算
			int direction = LocationUtilities.getDirection(curLat, curLon,
					locationItem.getLat(), locationItem.getLon());

			CameraView cameraView = (CameraView) findViewById(R.id.camera);
			int allowAngleOfView = (int) cameraView.getCamera().getParameters().getHorizontalViewAngle(); // 画角
			int misorientation = 0; // 2点間の方位差

			// 方位角±allowAngleOfView以内の範囲にあるか判定
			boolean azimuthOK = false;
			if (azimuth + allowAngleOfView > 359) {
				// 反時計回りの方向に許容角度を加算した結果、1回転するケース

				// 0度～N度の範囲内にあるか
				int zeroOverLimit = azimuth + allowAngleOfView - 359;
				boolean okFlag1 = (0 <= direction)
						&& (direction <= zeroOverLimit);

				// N度～359度の範囲内にあるか
				int lowLimit = azimuth - allowAngleOfView;
				boolean okFlag2 = (lowLimit <= direction) && (direction <= 359);

				// どちらかの条件を満たしていれば範囲内とみなす
				azimuthOK = okFlag1 || okFlag2;

				if (okFlag1) {
					// 0度～N度の範囲内にある時
					// イベント方位に359を加算し、カメラ方向との方位差を求める
					misorientation = (direction + 359) - azimuth;

				} else if (okFlag2) {
					// イベント方位とカメラ方向の方位差を求める
					misorientation = direction - azimuth;
				}

			} else if (azimuth - allowAngleOfView < 0) {
				// 時計回りの方向に許容角度を減算した結果、1回転するケース

				// N度～359殿範囲内にあるか
				int maxUnderLimit = azimuth - allowAngleOfView + 359;
				boolean okFlag1 = (maxUnderLimit <= direction)
						&& (direction <= 359);

				// 0度～N度の範囲内にあるか
				int zeroOverLimit = azimuth + allowAngleOfView;
				boolean okFlag2 = (0 <= direction)
						&& (direction <= zeroOverLimit);

				// どちらかの条件を満たしていれば範囲内とみなす
				azimuthOK = okFlag1 || okFlag2;

				if (okFlag1) {
					// イベント方位を359を減算し、カメラ方向との方位差を求める
					misorientation = (direction - 359) - azimuth;
				} else if (okFlag2) {
					// 0度～N度の範囲内にある時
					// イベント方位とカメラ方向の方位差を求める
					misorientation = direction - azimuth;
				}

			} else {
				// 1回転しないケース
				int maxLimit = azimuth + allowAngleOfView;
				int minLimit = azimuth - allowAngleOfView;
				azimuthOK = (minLimit <= direction) && (direction <= maxLimit);

				misorientation = direction - azimuth;
			}

			// 当該座標とイベントの距離が10[m]以内であるか判定
			boolean distanceOK = distance <= 1000;

			if (distanceOK && azimuthOK) {
				// 距離と方位が条件を描画満たすケース

				// 表示画像のサイズ修正
				// リソースからbitmapを作成
				Bitmap image = BitmapFactory.decodeResource(
						getResources(),
						getResources().getIdentifier(locationItem.getArImageName(),
								"drawable", getPackageName()));

				// 画像が取得できない場合は描画しない
				if (image == null) {
					continue;
				}

				// ピンのレイアウトを取得
				FrameLayout.LayoutParams layoutParams = getPinLayout(misorientation, allowAngleOfView, image);

				// ピンとレイアウトを画面描画対象のアイテムとして保存
				addPinIdToLayoutParamsMap.put(
						locationItem.getId(), layoutParams);
			}
		}

		HashMap<Integer, FrameLayout.LayoutParams> newPinToLayoutParamsMap = new HashMap<Integer, FrameLayout.LayoutParams>();
		// フレームレイアウト上の子要素の数だけループ
		for (int viewIndex = 0; viewIndex < frameLayout.getChildCount(); viewIndex++) {
			View childView = frameLayout.getChildAt(viewIndex);
			
			// Viewがピンか判定
			if (childView.getClass().equals(PinButton.class)) {
				PinButton pin = (PinButton) childView;
				
				// 画面描画対象のピンか判定
				if (addPinIdToLayoutParamsMap.containsKey(pin.id)) {

					// ピンのレイアウトを取得
					FrameLayout.LayoutParams layoutParams = addPinIdToLayoutParamsMap.get(pin.id);

					// 既に画面上に表示されているピンか判定
					if (pinToLayoutParamsMap.containsKey(pin.id)) {
						// 既に画面上に表示されているケース

						// 以前のレイアウトを取得
						FrameLayout.LayoutParams oldLayoutParams = pinToLayoutParamsMap
								.get(pin.id);

						// レイアウトのマージンを再定義する(leftMarginのみ最新のものを適用)
						layoutParams.setMargins(layoutParams.leftMargin,
								oldLayoutParams.topMargin, oldLayoutParams.rightMargin,
								oldLayoutParams.bottomMargin);
					}

					// レイアウトを再定義し、描画
					childView.setLayoutParams(layoutParams);
					childView.setVisibility(View.VISIBLE);

					// 最新の描画済みオブジェクトとして登録
					newPinToLayoutParamsMap.put(pin.id, layoutParams);
				} else {
					// 追加対象でないピンは描画しない
					childView.setVisibility(View.INVISIBLE);
				}
			}
		}

		// 現在のマップをクリアし、最新の描画済みオブジェクト情報を保存
		pinToLayoutParamsMap.clear();
		pinToLayoutParamsMap = newPinToLayoutParamsMap;
	}

	@Override
	public void onLocationChanged(Location location) {
		provText.setText(location.getProvider());
		refreshGeoLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public synchronized void onSensorChanged(SensorEvent event) {

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

			// 各回転角の表示.
			// ラジアンから度に変換した後に表示
			azimuthText.setText(Integer.toString((LocationUtilities
					.radianToDegreeForAzimuth(attitude[0]))));
			pitchText.setText(Integer.toString((LocationUtilities
					.radianToDegree(attitude[1]))));
			rollText.setText(Integer.toString((LocationUtilities
					.radianToDegree(attitude[2]))));

			// 方位角の設定
			int tmpAzimuth = (LocationUtilities
					.radianToDegreeForAzimuth(attitude[0]));

			// 前回から方位差がN度ずれた場合のみ描画イベントを開始
			int limit = 8;
			boolean noChangeFLag = false;
			if (tmpAzimuth + limit > 359) {
				noChangeFLag = (azimuth >= tmpAzimuth - limit) && (azimuth <= 359);

				if (!noChangeFLag) {
					noChangeFLag = (azimuth >= 0)
							&& (azimuth <= ((tmpAzimuth + limit) - 359));
				}

			} else if (tmpAzimuth - limit < 0) {
				noChangeFLag = (azimuth <= tmpAzimuth + limit) && (azimuth >= 0);

				if (!noChangeFLag) {
					noChangeFLag = (azimuth <= 359)
							&& (azimuth >= (359 - (limit - tmpAzimuth)));
				}
			} else {
				noChangeFLag = (azimuth >= tmpAzimuth - limit)
						&& (azimuth <= tmpAzimuth + limit);
			}
			if (!noChangeFLag) {
				azimuth = tmpAzimuth;
				preAzimuthText.setText(String.valueOf(azimuth));
				displayItems();
			}
		}
	}

	/**
	 *
	 * @param angle
	 *            カメラ方位からイベント方位への方位差(-60～60)
	 * @param allowAngleOfView 
	 * @param image 
	 * @return
	 */
	public FrameLayout.LayoutParams getPinLayout(int angle, int allowAngleOfView, Bitmap image) {

		// フレームレイアウトの高さ・幅を取得
		int heightPixels = frameLayout.getHeight();
		int widthPixels = frameLayout.getWidth();

		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();

		// 高さはランダム配置(画像の高さを考慮する)
		int rdmHeight = (int) Math.round(Math.floor(Math.random()
				* (heightPixels + 1)));
		if (heightPixels - rdmHeight < imgWidth) {
			rdmHeight = heightPixels - imgWidth;
		}

		FrameLayout.LayoutParams rLayoutParams = new FrameLayout.LayoutParams(
				imgWidth, imgHeight, Gravity.NO_GRAVITY);

		double threshould = (double) (angle + allowAngleOfView) / (double) (allowAngleOfView * 2);

		rLayoutParams.setMargins((int) (widthPixels * threshould), rdmHeight, 0, 0);
		return rLayoutParams;
	}

	public PinButton createPiButton(LocalItem item, int azimuth, float distance) {

		int useImage = getResources().getIdentifier(
				String.valueOf(item.getArImageName()), "drawable",
				getPackageName());

		PinButton btn = new PinButton(this);
		btn.setImageResource(useImage);
		btn.message = item.getMessage();
		btn.id = item.getId();
		btn.talk_group_id = item.getTalkGroupId();
		btn.lon = String.valueOf(item.getLon());
		btn.lat = String.valueOf(item.getLat());
		btn.azimuth = String.valueOf(azimuth);
		btn.distance = String.valueOf(distance);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PinButton pin = (PinButton) v;

				// TODO ボタンクリックイベント
				//				String showmessage = String.format(
				//						"%s(id:%s, 経度:%s,緯度:%s, 方位角:%s,当該座標との距離:%s[m])",
				//						new Object[] { pin.message, pin.id, pin.lon, pin.lat,
				//								pin.azimuth, pin.distance });
				//				Toast.makeText(ARAcitivity.this, showmessage,
				//						Toast.LENGTH_SHORT).show();

				Intent intent = new Intent();
				intent.setClassName("com.sw.minavi",
						"com.sw.minavi.activity.TalkActivity");
				intent.putExtra("pinId", pin.id);
				intent.putExtra("talkGroupId", pin.talk_group_id);
				startActivity(intent);
				finish();
			}
		});
		return btn;
	}
}
