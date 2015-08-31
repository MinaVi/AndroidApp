package com.sw.minavi.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TransportLog
		extends AsyncTask<String, Integer, Integer> {

	ProgressDialog dialog;
	Context context;
	Handler mHandler;

	public TransportLog(Context context, Handler mHandler) {
		this.context = context;
		this.mHandler =  mHandler;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Integer doInBackground(String... params) {

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost method = new HttpPost("http://www.snowwhite.hokkaido.jp/niseko/api/set/locationlog");

		// リクエストパラメータの設定
		List<NameValuePair> posts = new ArrayList<NameValuePair>();
		posts.add(new BasicNameValuePair("lon", params[0]));
		posts.add(new BasicNameValuePair("lat", params[1]));
		posts.add(new BasicNameValuePair("alt", params[2]));
		posts.add(new BasicNameValuePair("acc", params[3]));
		posts.add(new BasicNameValuePair("speed", params[4]));
		posts.add(new BasicNameValuePair("r_datetime", params[5]));
		posts.add(new BasicNameValuePair("mobile_location_id", params[6]));
		posts.add(new BasicNameValuePair("u_id", params[7]));
		posts.add(new BasicNameValuePair("client_id", String.valueOf(1)));

		try {

			method.setEntity(new UrlEncodedFormEntity(posts, "UTF-8"));
			HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();

            if (entity != null) {

            	InputStream is = entity.getContent();

                // コンテンツの読み込み
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                String line;
                while( ( line = reader.readLine() ) != null ){
                	Message msg = Message.obtain();
                	msg.obj = line;
                	mHandler.sendMessage(msg);
                }
            }

			Log.d("post", String.valueOf(status));
			return status;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	protected void onPostExecute(Integer result) {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	protected void onPreExecute() {
	}
}