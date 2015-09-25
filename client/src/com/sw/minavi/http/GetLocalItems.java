package com.sw.minavi.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

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
//		    XmlPullParser xmlPullParser = Xml.newPullParser();
		 
		    URL url = new URL("http://www.snowwhite.hokkaido.jp/minavicms/datasend/index");
		    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		    connection.connect();
//		    InputStream text = connection.getInputStream();
//		    String ss = text.toString();
//		    xmlPullParser.setInput(connection.getInputStream(), "UTF-8");
//		 
//		    int eventType;
		    
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
		    
            // xmlparse
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in);
            
            // 現在のnodeの種類を取得
            int type = parser.getEventType();
            // ドキュメントの最後まで読み込み
            ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String,String>>();
        	HashMap<String, String> item = new HashMap<String, String>();
            while(type != XmlPullParser.END_DOCUMENT) {
            	if(type == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    type = parser.next();
                    
                    if(name.equals("item")) {
                    	item = new HashMap<String, String>();
                    }
                    
                    if(name.equals("talk_group_id")) {
                    	item.put("talk_group_id",parser.getText());
                    }else if(name.equals("title")){
                    	item.put("title",parser.getText());
                    }else if(name.equals("title_en")){
                    	item.put("title_en",parser.getText());
                    }else if(name.equals("lon")){
                    	item.put("lon",parser.getText());
                    }else if(name.equals("lat")){
                    	item.put("lat",parser.getText());
                    }else if(name.equals("ar_image_name")){
                    	item.put("ar_image_name",parser.getText());
                    }else if(name.equals("pin")){
                    	item.put("pin",parser.getText());
                    }else if(name.equals("lon_min")){
                    	item.put("lon_min",parser.getText());
                    }else if(name.equals("lat_min")){
                    	item.put("lat_min",parser.getText());
                    }else if(name.equals("lon_max")){
                    	item.put("lon_max",parser.getText());
                    }else if(name.equals("lat_max")){
                    	item.put("lat_max",parser.getText());
                    }else if(name.equals("is_enabled")){
                    	item.put("is_enabled",parser.getText());
                    }else if(name.equals("is_removed")){
                    	item.put("is_removed",parser.getText());
                    }
                }else if(type == XmlPullParser.END_TAG){
                    String name = parser.getName();
                    
                    if(name.equals("item")) {
                    	items.add(item);
                    }
                }
            	
            	//TODO データの更新
            	
            	 // END_TAG読み飛ばし
                type = parser.next();
            }
//		    while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
//	        	strs.add(xmlPullParser.nextText());
//		    }
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
