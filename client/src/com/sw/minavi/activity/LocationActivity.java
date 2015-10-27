package com.sw.minavi.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.sw.minavi.R;
import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.EmergencyItemTableManager;
import com.sw.minavi.activity.db.LocalItemTableManager;
import com.sw.minavi.http.GetGnaviItems;
import com.sw.minavi.http.GetGnaviItems.AsyncTaskCallback;
import com.sw.minavi.http.TransportLog;
import com.sw.minavi.item.BgmManager;
import com.sw.minavi.item.EmergencyItem;
import com.sw.minavi.item.LocalItem;
import com.sw.minavi.item.parseJsonpOfDirectionAPI;
//import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
//import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
//import com.google.android.gms.location.LocationClient;
//import android.support.v4.app.Fragment;

public class LocationActivity extends FragmentActivity implements
		GoogleApiClient.OnConnectionFailedListener,
		android.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
		OnClickListener, OnCheckedChangeListener, AsyncTaskCallback {

	GoogleMap gMap;
	TileOverlay tileOverlay;

	private static final int MENU_A = 0;
	private static final int MENU_B = 1;
	private static final int MENU_c = 2;

	public static String posinfo = "";
	public static String info_A = "";
	public static String info_B = "";
	ArrayList<LatLng> markerPoints;

	public static MarkerOptions options;
	public static MarkerOptions emeitems = null;
	public static MarkerOptions items = null;
	public ProgressDialog progressDialog;

	public String travelMode = "walking";// default
	// public final static String MODE_DRIVING = "driving";
	// public final static String MODE_WALKING = "walking";

	/** DB操作オブジェクト */
	private DatabaseOpenHelper helper;

	/** 設定マネージャー */
	private SharedPreferences sPref;
	boolean emeFlg = false;

	PolylineOptions lineOptions = null;

	private Handler mHandler;

	/** 座標アイテム */
	private ArrayList<LocalItem> LocalItems = new ArrayList<LocalItem>(); // 通常モード用
	private ArrayList<EmergencyItem> EmergencyItems = new ArrayList<EmergencyItem>(); // 災害モード用

	/** 位置情報管理 */
	private LocationManager locationManager;
	/** プロバイダ */
	private List<String> providers;

	private ToggleButton btn;

	private MediaPlayer mPlayer;
	private boolean bgmPlayingFlg = false;

	public int mode = 1;// デフォルトは詳細
	private Location myLocation;
	private GoogleApiClient mLocationClient = null;
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	

	UrlTileProvider tileProvider = new UrlTileProvider(256, 256) {
		@Override
		public URL getTileUrl(int x, int y, int zoom) {

			/* Define the URL pattern for the tile images */
			// String s =
			// String.format("http://my.image.server/images/%d/%d/%d.png",
			// zoom, x, y);
			String s = String
					.format("http://cyberjapandata.gsi.go.jp/xyz/bousai_app/h27/tsunami2_r/%d/%d/%d.png",
							zoom, x, y);

			if (!checkTileExists(x, y, zoom)) {
				return null;
			}

			try {
				return new URL(s);
			} catch (MalformedURLException e) {
				throw new AssertionError(e);
			}
		}

		/*
		 * Check that the tile server supports the requested x, y and zoom.
		 * Complete this stub according to the tile range you support. If you
		 * support a limited range of tiles at different zoom levels, then you
		 * need to define the supported x, y range at each zoom level.
		 */
		private boolean checkTileExists(int x, int y, int zoom) {
			int minZoom = 10;
			int maxZoom = 18;

			if ((zoom < minZoom || zoom > maxZoom)) {
				return false;
			}

			return true;
		}
	};

	UrlTileProvider tileProviderBase = new UrlTileProvider(256, 256) {
		@Override
		public URL getTileUrl(int x, int y, int zoom) {

			/* Define the URL pattern for the tile images */
			// String s =
			// String.format("http://my.image.server/images/%d/%d/%d.png",
			// zoom, x, y);
			String s = String.format(
					"http://cyberjapandata.gsi.go.jp/xyz/std/%d/%d/%d.png",
					zoom, x, y);

			if (!checkTileExists(x, y, zoom)) {
				return null;
			}

			try {
				return new URL(s);
			} catch (MalformedURLException e) {
				throw new AssertionError(e);
			}
		}

		/*
		 * Check that the tile server supports the requested x, y and zoom.
		 * Complete this stub according to the tile range you support. If you
		 * support a limited range of tiles at different zoom levels, then you
		 * need to define the supported x, y range at each zoom level.
		 */
		private boolean checkTileExists(int x, int y, int zoom) {
			//			int minZoom = 10;
			//			int maxZoom = 18;
			//
			//			if ((zoom < minZoom || zoom > maxZoom)) {
			//				return false;
			//			}

			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		// BGM設定
		// mPlayer = MediaPlayer
		// .create(getApplicationContext(), R.raw.spring_wind);
		// mPlayer.setLooping(true);
		// mPlayer.seekTo(0);
		// if (!mPlayer.isPlaying()) {
		// mPlayer.start();
		// }
		// モード判定
		sPref = PreferenceManager.getDefaultSharedPreferences(this);
		emeFlg = sPref.getBoolean("pref_emergency_flag", false);

		if (emeFlg == false) {
			BgmManager.newIntance(getApplicationContext()).playSound(
					R.raw.spring_wind);
			bgmPlayingFlg = true;
		}

		// routeモード
		btn = (ToggleButton) findViewById(R.id.routeCheck);

		// 現在地取得
		// LocationManagerの取得
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// GPSから現在地の情報を取得
		final Criteria criteria = new Criteria();
		final String provider = locationManager.getBestProvider(criteria, true);
		myLocation = locationManager.getLastKnownLocation(provider);

		// initLocationService();

		// プログレス
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("検索中......");
		progressDialog.hide();

		// SQLiteへのアクセス準備
		initDataBaseManage();
		// 初期化
		markerPoints = new ArrayList<LatLng>();

		// initLocationService();

		SupportMapFragment mapfragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		gMap = mapfragment.getMap();
		if (gMap != null) {
			gMap.setMyLocationEnabled(true);
		}
		// mLocationClient = new LocationClient(getApplicationContext(), this,
		// this); // ConnectionCallbacks, OnConnectionFailedListener
		mLocationClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		if (mLocationClient != null) {
			// Google Play Servicesに接続
			mLocationClient.connect();
		}

		// 初期位置（テスト用）
		// LatLng location = new LatLng(34.802556297454004, 135.53884506225586);

		if (emeFlg == true) {
			setEmeItemOnGmap();
			TileOverlayOptions t2 = new TileOverlayOptions()
					.tileProvider(tileProvider);
			t2.zIndex(2);
			tileOverlay = gMap.addTileOverlay(t2);
		} else {
			setItemOnGmap();
		}

		if (gMap != null && myLocation != null) {

			// gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
			// 現在地に移動
			CameraPosition cameraPos = new CameraPosition.Builder()
					.target(new LatLng(myLocation.getLatitude(), myLocation
							.getLongitude())).zoom(17.0f).bearing(0).build();
			gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

			gMap.setMyLocationEnabled(true);

			TileOverlayOptions t1 = new TileOverlayOptions()
					.tileProvider(tileProviderBase);
			t1.zIndex(1);
			tileOverlay = gMap.addTileOverlay(t1);

			// クリックリスナー
			gMap.setOnMapClickListener(new OnMapClickListener() {
				@Override
				public void onMapClick(LatLng point) {
					// routeモードかどうかチェック
					boolean checked = btn.isChecked();
					if (checked == true) {
						// ルート検索モード

						// ３度目クリックでスタート地点を再設定
						// if (markerPoints.size() > 1) {
						markerPoints.clear();
						gMap.clear();
						markerPoints.add(new LatLng(myLocation.getLatitude(),
								myLocation.getLongitude()));
						// }

						markerPoints.add(point);

						options = new MarkerOptions();
						options.position(point);

						gMap.addMarker(options);

						gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
							@Override
							public boolean onMarkerClick(Marker marker) {
								// TODO Auto-generated method stub

								String title = marker.getTitle();
								if (title.equals("A")) {
									marker.setSnippet(info_A);

								} else if (title.equals("B")) {
									marker.setSnippet(info_B);
								}

								return false;
							}
						});

						// ルート検索
						gMap.clear();

						routeSearch();
						// オブジェクト取得
						// if (emeFlg == true) {
						// setEmeItemOnGmap();
						// tileOverlay = gMap
						// .addTileOverlay(new TileOverlayOptions()
						// .tileProvider(tileProviderBase));
						// tileOverlay = gMap
						// .addTileOverlay(new TileOverlayOptions()
						// .tileProvider(tileProvider));
						// // 描画
						// gMap.addPolyline(lineOptions);
						// } else {
						// setItemOnGmap();
						// tileOverlay = gMap
						// .addTileOverlay(new TileOverlayOptions()
						// .tileProvider(tileProviderBase));
						// // 描画
						// gMap.addPolyline(lineOptions);
						// }
					} else {
						// 詳細モード
					}
				}
			});

			// アイコン情報
			if (emeFlg == true) {
//				setEmeItemOnGmap();
//				TileOverlayOptions t2 = new TileOverlayOptions()
//						.tileProvider(tileProvider);
//				t2.zIndex(2);
//				tileOverlay = gMap.addTileOverlay(t2);
				// tileOverlay = gMap.addTileOverlay(new TileOverlayOptions()
				// .tileProvider(tileProviderBase));
				// tileOverlay = gMap.addTileOverlay(new TileOverlayOptions()
				// .tileProvider(tileProvider));
			} else {
//				setItemOnGmap();
			}

//			KmlLayer layer = new KmlLayer(getMap(), R.raw.kmlFile, getApplicationContext());
			
			markerPoints.clear();
			markerPoints.add(new LatLng(myLocation.getLatitude(), myLocation
					.getLongitude()));
			
		}

		// handler準備
		mHandler = new Handler() {
			public void handleMassage(Message msg) {
				// メッセージ表示

			};
		};
		
		// ぐるナビ情報
		GetGnaviItems gnaviAsync = new GetGnaviItems(this);
		gnaviAsync.execute(String.valueOf(myLocation.getLatitude()),String.valueOf(myLocation.getLongitude()));
		

		android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
		SupportMapFragment fragment = (SupportMapFragment) manager
				.findFragmentById(R.id.map);
		GoogleMap map = fragment.getMap();
		if (map != null) {
			map.getUiSettings().setZoomControlsEnabled(true);
		}

	}

	// private void initLocationService() {
	// locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	// providers = locationManager.getProviders(true);
	//
	// // 全プロバイダの登録
	// for (String provider : providers) {
	// locationManager.requestLocationUpdates(provider, 0, 1,
	// (android.location.LocationListener) this);
	// }
	//
	// }

	private void routeSearch() {
		progressDialog.show();

		LatLng origin = markerPoints.get(0);
		LatLng dest = markerPoints.get(1);

		String url = getDirectionsUrl(origin, dest);

		DownloadTask downloadTask = new DownloadTask();

		downloadTask.execute(url);

	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		String sensor = "sensor=false";

		// パラメータ
		String parameters = str_origin + "&" + str_dest + "&" + sensor
				+ "&language=ja" + "&mode=" + travelMode;

		// JSON指定
		String output = "json";

		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.connect();

			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	private class DownloadTask extends AsyncTask<String, Void, String> {
		// 非同期で取得

		@Override
		protected String doInBackground(String... url) {

			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			parserTask.execute(result);
		}
	}

	/* parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				parseJsonpOfDirectionAPI parser = new parseJsonpOfDirectionAPI();

				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// ルート検索で得た座標を使って経路表示
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {

			ArrayList<LatLng> points = null;
			// PolylineOptions lineOptions = null;
			// MarkerOptions markerOptions = new MarkerOptions();

			if (result.size() != 0) {

				for (int i = 0; i < result.size(); i++) {
					points = new ArrayList<LatLng>();
					lineOptions = new PolylineOptions();

					List<HashMap<String, String>> path = result.get(i);

					for (int j = 0; j < path.size(); j++) {
						HashMap<String, String> point = path.get(j);

						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);

						points.add(position);
					}

					// ポリライン
					lineOptions.addAll(points);
					lineOptions.width(10);
					lineOptions.color(0x550000ff);

				}

				// 描画
				lineOptions.zIndex(3);
				gMap.addPolyline(lineOptions);
				if (emeFlg == true) {
					setEmeItemOnGmap();
					TileOverlayOptions t1 = new TileOverlayOptions()
							.tileProvider(tileProviderBase);
					t1.zIndex(1);
					tileOverlay = gMap.addTileOverlay(t1);
					TileOverlayOptions t2 = new TileOverlayOptions()
							.tileProvider(tileProviderBase);
					t2.zIndex(2);
					tileOverlay = gMap.addTileOverlay(t2);
				} else {
					setItemOnGmap();
					TileOverlayOptions t1 = new TileOverlayOptions()
							.tileProvider(tileProviderBase);
					t1.zIndex(1);
					tileOverlay = gMap.addTileOverlay(t1);
				}

			} else {
				gMap.clear();
				Toast.makeText(LocationActivity.this, "ルート情報を取得できませんでした",
						Toast.LENGTH_LONG).show();
			}
			progressDialog.hide();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		menu.add(0, MENU_A, 0, "Info");
		menu.add(0, MENU_B, 0, "Legal Notices");
		menu.add(0, MENU_c, 0, "Mode");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_A:
			// show_mapInfo();
			return true;

		case MENU_B:
			// Legal Notices(免責事項)

			String LicenseInfo = GooglePlayServicesUtil
					.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
			AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(
					LocationActivity.this);
			LicenseDialog.setTitle("Legal Notices");
			LicenseDialog.setMessage(LicenseInfo);
			LicenseDialog.show();

			return true;

		case MENU_c:
			// show_settings();
			return true;

		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// if (v.getId() == R.id.detail_btn) {
		// mode = 1;
		// // 現在地に移動
		// CameraPosition cameraPos = new CameraPosition.Builder()
		// .target(new LatLng(myLocation.getLatitude(), myLocation
		// .getLongitude())).zoom(17.0f).bearing(0).build();
		// gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
		// // ルート検索
		// gMap.clear();
		// // オブジェクト取得
		// if (emeFlg == true) {
		// setEmeItemOnGmap();
		// tileOverlay = gMap.addTileOverlay(new TileOverlayOptions()
		// .tileProvider(tileProvider));
		// } else {
		// setItemOnGmap();
		// }
		// } else if (v.getId() == R.id.route_btn) {
		// mode = 2;
		// }
		if (v.getId() == R.id.map_back_btn) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// mLocationClient.requestLocationUpdates(REQUEST, this); //
		// LocationListener

	}

	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	// private void initLocationService() {
	// locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	// providers = locationManager.getProviders(true);
	//
	// // 全プロバイダの登録
	// // for (String provider : providers) {
	// // locationManager.requestLocationUpdates(provider, 0, 1, this);
	// // }
	//
	// }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		myLocation = location;

		// 現在地に移動
		if (gMap != null) {
			LatLng latLng = new LatLng(location.getLatitude(),
					location.getLongitude());
			gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		}

		// オブジェクト取得
		if (emeFlg == true) {
			setEmeItemOnGmap();
			tileOverlay = gMap.addTileOverlay(new TileOverlayOptions()
					.tileProvider(tileProvider));
		} else {
			setItemOnGmap();
		}

		markerPoints.clear();
		markerPoints.add(new LatLng(location.getLatitude(), location
				.getLongitude()));

		setLocation(location);
	}

	private void setItemOnGmap() {
		// オブジェクト取得
		LocalItems = LocalItemTableManager.getInstance(helper).GetRecords();
		// オブジェクト配置
		for (LocalItem LocalItem : LocalItems) {

			items = new MarkerOptions();
			items.icon(BitmapDescriptorFactory.fromResource(getResources()
					.getIdentifier(LocalItem.getIconImageName(), "drawable",
							getPackageName())));
			items.title(LocalItem.getMessage());
			items.position(new LatLng(LocalItem.getLat(), LocalItem.getLon()));
			gMap.addMarker(items);
		}
	}

	private void setEmeItemOnGmap() {
		// オブジェクト取得
		EmergencyItems = EmergencyItemTableManager.getInstance(helper)
				.GetRecords();
		// オブジェクト配置
		for (EmergencyItem EmergencyItem : EmergencyItems) {

			emeitems = new MarkerOptions();
			emeitems.icon(BitmapDescriptorFactory.fromResource(getResources()
					.getIdentifier(EmergencyItem.getIconImageName(),
							"drawable", getPackageName())));
			emeitems.title(EmergencyItem.getMessage());
			emeitems.position(new LatLng(EmergencyItem.getLat(), EmergencyItem
					.getLon()));
			gMap.addMarker(emeitems);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	private void initDataBaseManage() {
		this.helper = new DatabaseOpenHelper(this);

		// ネット経由でデータ取得
		// GetLocalItems getLocalItems = new GetLocalItems(this, mHandler);
		// getLocalItems.execute();

//		LocalItemTableManager.getInstance(helper).InsertSample();
		EmergencyItemTableManager.getInstance(helper).InsertSample();
		String lang = sPref.getString("lang", "Japanese");
		LocalItemTableManager.lang = lang;
		EmergencyItemTableManager.lang = lang;
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

		// // routeモードかどうかチェック
		// boolean checked = btn.isChecked();
		//
		// // TODO Auto-generated method stub
		// if (checked == false) {
		// mode = 1;
		// // 現在地に移動
		// CameraPosition cameraPos = new CameraPosition.Builder()
		// .target(new LatLng(myLocation.getLatitude(), myLocation
		// .getLongitude())).zoom(17.0f).bearing(0).build();
		// gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
		// // ルート検索クリア
		// gMap.clear();
		// // オブジェクト取得
		// if (emeFlg == true) {
		// setEmeItemOnGmap();
		// tileOverlay = gMap.addTileOverlay(new TileOverlayOptions()
		// .tileProvider(tileProvider));
		// } else {
		// setItemOnGmap();
		// }
		// } else {
		// mode = 2;
		// }
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!bgmPlayingFlg) {
			BgmManager.newIntance(getApplicationContext()).playSound(-1);
		}
		bgmPlayingFlg = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (emeFlg == false) {
			super.onStart();
			BgmManager.newIntance(getApplicationContext()).playSound(
					R.raw.spring_wind);
			bgmPlayingFlg = true;
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void setLocation(final Location location) {

		// location情報をサーバーへ送信
		TransportLog tl = new TransportLog(this, mHandler);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy'-'MM'-'dd kk':'mm':'ss':'");

		tl.execute(String.valueOf(location.getLongitude()),
				String.valueOf(location.getLatitude()),
				String.valueOf(location.getAltitude()),
				String.valueOf(location.getAccuracy()),
				String.valueOf(location.getSpeed()), sdf.format(date),
				String.valueOf(0), sPref.getString("name", "unknown"));

	}
	
    public void preExecute() {
        //だいたいの場合ダイアログの表示などを行う
    }
    public void progressUpdate(int progress) {
        //だいたいプログレスダイアログを進める
    }
    public void cancel() {
        //キャンセルされた時になんかする
    }

	public void postExecute(ArrayList<HashMap<String, String>> result) {
		// TODO Auto-generated method stub
		// AsyncTaskの結果を受け取ってなんかする
		for (HashMap<String, String> gItem : result) {

			items = new MarkerOptions();
			items.icon(BitmapDescriptorFactory.fromResource(getResources()
					.getIdentifier("imgres", "drawable", getPackageName())));
			items.title(gItem.get("name"));
			double lat = Double.parseDouble(gItem.get("lat"));
			double lon = Double.parseDouble(gItem.get("lon"));
			items.position(new LatLng(lat, lon));
			gMap.addMarker(items);
		}
	}

}