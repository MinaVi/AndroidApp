package com.sw.minavi.http;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;

public class GetLocalItems extends AsyncTask<String, Integer, Integer> {

	ProgressDialog dialog;
	Context context;
	Handler mHandler;
	ArrayList<String> strs = new ArrayList();

	public GetLocalItems(Context context, Handler mHandler) {
		this.context = context;
		this.mHandler =  mHandler;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		try{
		    XmlPullParser xmlPullParser = Xml.newPullParser();
		 
		    URL url = new URL("http://www.snowwhite.hokkaido.jp/minavicms/datasend/index");
		    URLConnection connection = url.openConnection();
		    xmlPullParser.setInput(connection.getInputStream(), "UTF-8");
		 
		    int eventType;
		    while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
	        	strs.add(xmlPullParser.nextText());
		    }
		} catch (Exception e){
		    Log.d("XmlPullParserSampleUrl", "Error");
		}
		
		return null;
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
