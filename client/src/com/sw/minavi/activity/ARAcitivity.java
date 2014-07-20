package com.sw.minavi.activity;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sw.minavi.R;
import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.LocalItemTableManager;
import com.sw.minavi.item.LocalItem;
import com.sw.minavi.item.Pair;
import com.sw.minavi.item.PinButton;
import com.sw.minavi.util.LocationUtilities;

public class ARAcitivity extends Activity implements SensorEventListener,
		LocationListener {

	protected SensorManager sensorManager;
	protected LocationManager locationManager;
	protected List<String> providers;

	protected TextView geoText;
	protected TextView logText;
	protected TextView provText;
	protected TextView azimuthText;
	protected TextView pitchText;
	protected TextView rollText;
	protected TextView preAzimuthText;
	protected FrameLayout frameLayout;

	protected DatabaseOpenHelper helper;

	float[] inRotationMatrix = new float[9];
	float[] outRotationMatrix = new float[9];
	float[] gravity = new float[3];
	float[] geomagnetic = new float[3];
	float[] attitude = new float[3];

	int imgWidth = 0;
	int imgHeight = 0;

	Location curLocation = null;

	HashSet<PinButton> dispItemSet = new HashSet<PinButton>();

	int azimuth = 0;

	ArrayList<LocalItem> values = new ArrayList<LocalItem>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ar);
		findViews();
		initLocationService();
		initSensorService();

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		imgWidth = bitmap.getWidth();
		imgHeight = bitmap.getHeight();

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
			values = LocalItemTableManager.getInstance(helper).GetRecords();

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

	protected void refreshGeoLocation(Location location) {

		String desc = location.getLongitude() + ", " + location.getLatitude();
		geoText.setText(desc);

		Toast.makeText(ARAcitivity.this, desc, Toast.LENGTH_SHORT).show();

		// 表示情報の一覧を取得(当該座標の直近のデータのみ)
		values = LocalItemTableManager.getInstance(helper).GetRecords();

		// 現在位置の保存
		curLocation = location;

		// アイテムの描画
		displayItems();
	}

	protected void displayItems() {

		if (curLocation == null) {
			return;
		}

		System.out.println("call displayItems");

		// double curLon = 139.701334; // 現在位置から取得
		// double curLat = 35.658517; // 現在位置から取得

		double curLat = curLocation.getLatitude();
		double curLon = curLocation.getLongitude();

		// for (PinButton delPin : dispItemSet) {
		// frameLayout.removeView(delPin);
		// }
		// dispItemSet.clear();

		List<Pair<PinButton, FrameLayout.LayoutParams>> addItemList = new ArrayList<Pair<PinButton, FrameLayout.LayoutParams>>();

		for (LocalItem val : values) {

			// 2点間の距離を計算(当該座標とイベント座標)
			float distance = LocationUtilities.getDistance(curLat, curLon,
					val.getLat(), val.getLon(), 6) * 1000; // [km]から[m]に変換
			// イベント座標が存在する方位角を計算
			int direction = LocationUtilities.getDirection(curLat, curLon,
					val.getLat(), val.getLon());

			int allowAngleOfView = 60; // 画角

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

			} else {

				int maxLimit = azimuth + allowAngleOfView;
				int minLimit = azimuth - allowAngleOfView;
				azimuthOK = (minLimit <= direction) && (direction <= maxLimit);
			}

			// 当該座標とイベントの距離が10[m]以内であるか判定
			boolean distanceOK = distance <= 1000;

			// 描画許可・不許可問わず利用するので、このタイミングでピンを作成
			PinButton pin = createPiButton(val, direction, distance);

			if (distanceOK && azimuthOK) {
				// 描画許可
				// 既に登録済みのアイテムは追加しない(座標が移動してしまうため)
				FrameLayout.LayoutParams layotParams = getRdmMrgnLayout();

				// 表示画像のサイズ修正
				// リソースからbitmapを作成
				Bitmap image = BitmapFactory.decodeResource(getResources(),
						getResources().getIdentifier(val.getArImageName(), "drawable", getPackageName()));

				if (image != null) {
					// 画像サイズ取得
					int width = image.getWidth();
					int height = image.getHeight();

					layotParams.width = width;
					layotParams.height = height;
					
				}
				
				if (!dispItemSet.contains(pin)) {
					// frameLayout.addView(pin, pin.getId(), layotParams);
					// dispItemSet.add(pin);
				}
				
				addItemList.add(new Pair<PinButton, FrameLayout.LayoutParams>(
						pin, layotParams));
			}
		}

		ArrayList<PinButton> delButtonList = new ArrayList<PinButton>(
				dispItemSet);
		for (Pair<PinButton, FrameLayout.LayoutParams> btnPosPair : addItemList) {
			PinButton pin = btnPosPair.getLeft();
			FrameLayout.LayoutParams layotParams = btnPosPair.getRight();

			if (dispItemSet.contains(pin)) {
				System.out.println(MessageFormat.format("既に描画されています。ID=[{0}]",
						pin.getId()));
				// そのまま描画させるので、削除対象ボタン一覧から削除
				delButtonList.remove(pin);
			} else {
				System.out.println(MessageFormat.format("まだ描画されていません。ID=[{0}]",
						pin.getId()));
				// 描画対象ボタンの内、新しいボタンは新規に描画
				frameLayout.addView(pin, pin.getId(), layotParams);

				dispItemSet.add(pin);
			}
		}

		// 不要になったボタンは削除
		for (PinButton pin : delButtonList) {
			frameLayout.removeView(pin);
			dispItemSet.remove(pin);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		provText.setText(location.getProvider());
		refreshGeoLocation(location);
		Toast.makeText(ARAcitivity.this, "onLocationChanged",
				Toast.LENGTH_SHORT).show();
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
	public void onSensorChanged(SensorEvent event) {

		// イベントが発生したセンサがどのセンサか判定する
		switch (event.sensor.getType()) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			// 地磁気センサから地磁気を取得
			geomagnetic = event.values.clone();
			break;
		case Sensor.TYPE_ACCELEROMETER:
			// 加速度センサから加速度を取得
			gravity = event.values.clone();
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
			azimuthText.setText(Integer.toString((int) (LocationUtilities
					.radianToDegreeForAzimuth(attitude[0]))));
			pitchText.setText(Integer.toString((int) (LocationUtilities
					.radianToDegree(attitude[1]))));
			rollText.setText(Integer.toString((int) (LocationUtilities
					.radianToDegree(attitude[2]))));

			// 方位角の設定
			int tmpAzimuth = (int) (LocationUtilities
					.radianToDegreeForAzimuth(attitude[0]));

			boolean noChangeFLag = false;
			if (tmpAzimuth + 10 > 359) {
				noChangeFLag = (azimuth >= tmpAzimuth - 10) && (azimuth <= 359);

				if (!noChangeFLag) {
					noChangeFLag = (azimuth >= 0)
							&& (azimuth <= ((tmpAzimuth + 10) - 359));
				}

			} else if (tmpAzimuth - 10 < 0) {
				noChangeFLag = (azimuth <= tmpAzimuth + 10) && (azimuth >= 0);

				if (!noChangeFLag) {
					noChangeFLag = (azimuth <= 359)
							&& (azimuth >= (359 - (10 - tmpAzimuth)));
				}
			} else {
				noChangeFLag = (azimuth >= tmpAzimuth - 10)
						&& (azimuth <= tmpAzimuth + 10);
			}

			if (!noChangeFLag) {
				azimuth = tmpAzimuth;
				preAzimuthText.setText(String.valueOf(azimuth));
				displayItems();
			}
		}
	}

	public FrameLayout.LayoutParams getRdmMrgnLayout() {
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);

		int heightPixels = displayMetrics.heightPixels;
		int widthPixels = displayMetrics.widthPixels;

		int rdmHeight = (int) Math.round(Math.floor(Math.random()
				* (heightPixels + 1)));
		int rdmWidth = (int) Math.round(Math.floor(Math.random()
				* (widthPixels + 1)));

		if (heightPixels - rdmHeight < imgWidth)
			rdmHeight = heightPixels - imgWidth;
		if (widthPixels - rdmWidth < imgWidth)
			rdmWidth = widthPixels - imgWidth;

		// FrameLayout.LayoutParams rLayoutParams = new
		// FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
		// FrameLayout.LayoutParams.WRAP_CONTENT);
		FrameLayout.LayoutParams rLayoutParams = new FrameLayout.LayoutParams(
				imgWidth, imgHeight);
		rLayoutParams.gravity = Gravity.NO_GRAVITY;
		//rLayoutParams.setMargins(rdmWidth, rdmHeight, 0, 0);
		
		// TODO　調整中
		// ランダムに配置
		Random rdm = new Random();
		rLayoutParams.setMargins(100 + rdm.nextInt(700)+100, 100, 0, 0);	// XperiaA用
		return rLayoutParams;
	}

	public PinButton createPiButton(LocalItem item, int azimuth, float distance) {

		int useImage = getResources()
				.getIdentifier(String.valueOf(item.getArImageName()), "drawable", getPackageName());

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
				//
				//				String showMessage = String.format(
				//						"%s(id:%s, 経度:%s,緯度:%s, 方位角:%s,当該座標との距離:%s[m])",
				//						new Object[] { pin.message, pin.id, pin.lon, pin.lat,
				//								pin.azimuth, pin.distance });
				//				Toast.makeText(ARAcitivity.this, showMessage,
				//						Toast.LENGTH_SHORT).show();

				Intent intent = new Intent();
				intent.setClassName("com.sw.minavi", "com.sw.minavi.activity.TalkActivity");
				intent.putExtra("pinId", pin.id);
				intent.putExtra("talkGroupId", pin.talk_group_id);
				startActivity(intent);
				finish();
			}
		});
		return btn;
	}
}
