package com.sw.minavi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sw.minavi.R;

public class EmergencyActivity extends Activity implements OnClickListener {

	private TextView talkTextView;
	private int ClickCount = 0;
	
	// 設定マネージャー
	private SharedPreferences sPref;

	private String message1 = "地震が発生しました";
	private String message2 = "揺れを感じた方は、災害情報を確認し、";
	private String message3 = "落ち着いて行動してください。";
	private String message4 = "近くの避難所一覧を表示します。";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_emergency);
		talkTextView = (TextView) findViewById(R.id.talkText);
	}
	
	@SuppressLint("CommitPrefEdits") @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		sPref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sPref.edit();
		editor.putBoolean("pref_emergency_flag", true);
		editor.apply();
		
		String lang = sPref.getString("lang", "Japanese");
		
		if(lang.equals("English")){
			message1 = "Large earthquake has occurred.";
			message2 = "Those who felt the shaking, please check the disaster information.";
			message3 = "Please to act calmly.";
			message4 = "It will display the nearby shelter list.";
		}
		
		switch (ClickCount) {
		case 0:
			talkTextView.setText(message1);
			break;
		case 1:
			talkTextView.setText(message2);
			break;
		case 2:
			talkTextView.setText(message3);
			break;
		case 3:
			talkTextView.setText(message4);
			break;
		default:
			Intent intent = new Intent(this, LocationActivity.class);
			startActivity(intent);
			break;
		}
		
		ClickCount++;
	}
	
	

}
