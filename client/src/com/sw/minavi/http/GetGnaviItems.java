package com.sw.minavi.http;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.DatabaseOpenHelper.LocalItemTable;

public class GetGnaviItems extends
		AsyncTask<String, Integer, ArrayList<HashMap<String, String>>> {

	ProgressDialog dialog;
	Context context;
	Handler mHandler;
	/** 言語設定マネージャー */
	public static String setLang = "Japanese";
	
	public ArrayList<HashMap<String, String>> gnaviItems = new ArrayList<HashMap<String, String>>();

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;

	public interface AsyncTaskCallback {
		void preExecute();

		void postExecute(ArrayList<HashMap<String, String>> result);

		void progressUpdate(int progress);

		void cancel();
	}

	private AsyncTaskCallback callback = null;;

	public GetGnaviItems(AsyncTaskCallback _callback) {
		this.callback = _callback;
	}

	// public GetGnaviItems(Context context) {
	// this.context = context;
	// }

	@Override
	protected ArrayList<HashMap<String, String>> doInBackground(
			String... params) {
		// TODO Auto-generated method stub

		// アクセスキー
		String acckey = "25745e916833ace4346493a5e123dc19";
		// 緯度
		String lat = params[0];
		// 経度
		String lon = params[1];
		// 範囲
		String range = "1";
		// 返却値言語
		// メッセージは言語によって変える
		String lang = "ja";
		if (setLang.equals("English")) {		
			lang = "en";
		} else if(setLang.equals("Chainese")) {
			lang = "zh_cn";
		}
		
		// 返却形式
		String format = "json";
		// エンドポイント
		String gnaviRestUri = "http://api.gnavi.co.jp/ForeignRestSearchAPI/20150630/";
		String prmFormat = "?format=" + format;
		String prmKeyid = "&keyid=" + acckey;
		String outcoordinates = "&input_coordinates_mode=" + 2;
		String incoordinates = "&coordinates_mode=" + 2;
		String prmLat = "&latitude=" + lat;
		String prmLon = "&longitude=" + lon;
		String prmRange = "&range=" + range;
		String prmLang = "&lang=" + lang;
		String count = "&hit_per_page=50";
		// URI組み立て
		StringBuffer uri = new StringBuffer();
		uri.append(gnaviRestUri);
		uri.append(prmFormat);
		uri.append(prmKeyid);
		uri.append(outcoordinates);
		uri.append(incoordinates);
		uri.append(prmLat);
		uri.append(prmLon);
		uri.append(prmRange);
		uri.append(prmLang);
		uri.append(count);

		// APIを実行し結果を出力
		String url = uri.toString();
		getNodeList(uri.toString());

		try {

		} catch (Exception e) {
			Log.d("XmlPullParserSampleUrl", "Error");
		}

		return gnaviItems;
	}

	private void getNodeList(String url) {
		try {
			URL restSearch = new URL(url);
			HttpURLConnection http = (HttpURLConnection) restSearch
					.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			// Jackson
			ObjectMapper mapper = new ObjectMapper();
			viewJsonNode(mapper.readTree(http.getInputStream()));

		} catch (Exception e) {
			// TODO:例外を考慮していません
		}
	}

	private void viewJsonNode(JsonNode nodeList) {
		if (nodeList != null) {
			// トータルヒット件数
			// String hitcount = "total:"
			// + nodeList.path("total_hit_count").asText();
			// System.out.println(hitcount);
			// restのみ取得
			JsonNode restList = nodeList.path("rest");
			Iterator<JsonNode> rest = restList.iterator();
			// 店舗ID, 店舗名、アクセスを格納
			gnaviItems = new ArrayList<HashMap<String, String>>();
			while (rest.hasNext()) {
				HashMap<String, String> gnaviItem = new HashMap<String, String>();
				JsonNode r = rest.next();
				if (r != null) {
					String id = r.path("id").asText();
					gnaviItem.put("id", id);
					String name = r.path("name").path("name").asText();
					gnaviItem.put("name", name);
					String prText = r.path("pr_short").asText();
					gnaviItem.put("pr_short", prText);

					String lon = r.path("location").path("longitude_wgs84").asText();
					gnaviItem.put("lon", lon);
					String lat = r.path("location").path("latitude_wgs84").asText();
					gnaviItem.put("lat", lat);
					
					String detail = r.path("sales_points").path("pr_long").asText();
					gnaviItem.put("detail", detail);
					
					String address = r.path("contacts").path("address").asText();
					gnaviItem.put("address", address);
					
					if (name.length() > 0 && lon.length() > 0
							&& lat.length() > 0) {
						gnaviItems.add(gnaviItem);
					}
				}
			}
		}
	}

	@Override
	protected void onPreExecute() {
		// super.onPreExecute();
		// callback.preExecute();
	}

	@Override
	protected void onCancelled() {
		// super.onCancelled();
		// callback.cancel();
	}

	@Override
	protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
		if (result != null) {
			super.onPostExecute(result);
			callback.postExecute(result);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// super.onProgressUpdate(values);
		// callback.progressUpdate(values[0]);
	}

}
