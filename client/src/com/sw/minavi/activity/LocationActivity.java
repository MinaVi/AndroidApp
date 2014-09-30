package com.sw.minavi.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sw.minavi.R;
import com.sw.minavi.item.parseJsonpOfDirectionAPI;

public class LocationActivity extends FragmentActivity {

	GoogleMap gMap;
	private static final int MENU_A = 0;
	private static final int MENU_B = 1;
	private static final int MENU_c = 2;

	public static String posinfo = "";
	public static String info_A = "";
	public static String info_B = "";
	ArrayList<LatLng> markerPoints;

	public static MarkerOptions options;

	public ProgressDialog progressDialog;

	public String travelMode = "driving";//default

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		//プログレス
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("検索中だす......");
		progressDialog.hide();

		//初期化
		markerPoints = new ArrayList<LatLng>();

		SupportMapFragment mapfragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

		gMap = mapfragment.getMap();

		//初期位置
		LatLng location = new LatLng(34.802556297454004, 135.53884506225586);

		if (gMap != null) {
			gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

			gMap.setMyLocationEnabled(true);

			//クリックリスナー
			gMap.setOnMapClickListener(new OnMapClickListener() {
				@Override
				public void onMapClick(LatLng point) {

					//３度目クリックでスタート地点を再設定
					if (markerPoints.size() > 1) {
						markerPoints.clear();
						gMap.clear();
					}

					markerPoints.add(point);

					options = new MarkerOptions();
					options.position(point);

					if (markerPoints.size() == 1) {
						//options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
						options.icon(BitmapDescriptorFactory.fromResource(R.drawable.question));
						options.title("A");

					} else if (markerPoints.size() == 2) {
						//options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
						options.icon(BitmapDescriptorFactory.fromResource(R.drawable.question));
						options.title("B");

					}

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

					if (markerPoints.size() >= 2) {
						//ルート検索
						routeSearch();
					}
				}
			});
		}
	}

	private void routeSearch() {
		progressDialog.show();

		LatLng origin = markerPoints.get(0);
		LatLng dest = markerPoints.get(1);

		String url = getDirectionsUrl(origin, dest);

		DownloadTask downloadTask = new DownloadTask();

		downloadTask.execute(url);

	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		String sensor = "sensor=false";

		//パラメータ
		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&language=ja" + "&mode=" + travelMode;

		//JSON指定
		String output = "json";

		String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

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

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

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
		//非同期で取得

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

	/*parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

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

		//ルート検索で得た座標を使って経路表示
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {

			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();

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

					//ポリライン
					lineOptions.addAll(points);
					lineOptions.width(10);
					lineOptions.color(0x550000ff);

				}

				//描画
				gMap.addPolyline(lineOptions);
			} else {
				gMap.clear();
				Toast.makeText(LocationActivity.this, "ルート情報を取得できませんでした", Toast.LENGTH_LONG).show();
			}
			progressDialog.hide();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		menu.add(0, MENU_A, 0, "Info");
		menu.add(0, MENU_B, 0, "Legal Notices");
		menu.add(0, MENU_c, 0, "Mode");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case MENU_A:
			//show_mapInfo();
			return true;

		case MENU_B:
			//Legal Notices(免責事項)

			String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
			AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(LocationActivity.this);
			LicenseDialog.setTitle("Legal Notices");
			LicenseDialog.setMessage(LicenseInfo);
			LicenseDialog.show();

			return true;

		case MENU_c:
			//show_settings();
			return true;

		}
		return false;
	}

	//リ･ルート検索
	private void re_routeSearch() {
		progressDialog.show();

		LatLng origin = markerPoints.get(0);
		LatLng dest = markerPoints.get(1);

		//
		gMap.clear();

		//マーカー
		//A
		options = new MarkerOptions();
		options.position(origin);
		options.icon(BitmapDescriptorFactory.fromResource(R.drawable.question));
		options.title("A");
		options.draggable(true);
		gMap.addMarker(options);
		//B
		options = new MarkerOptions();
		options.position(dest);
		options.icon(BitmapDescriptorFactory.fromResource(R.drawable.question));
		options.title("B");
		options.draggable(true);
		gMap.addMarker(options);

		String url = getDirectionsUrl(origin, dest);

		DownloadTask downloadTask = new DownloadTask();

		downloadTask.execute(url);

	}
}