package com.sw.minavi.activity;

import com.sw.minavi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EmergencyActivity extends Activity implements OnClickListener {

	private TextView talkTextView;
	private int ClickCount = 0;

	final String message1 = "地震が発生しました";
	final String message2 = "揺れを感じた方は、災害情報を確認し、";
	final String message3 = "落ち着いて行動してください。";
	final String message4 = "近くの避難所一覧を表示します。";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_emergency);
		talkTextView = (TextView) findViewById(R.id.talkText);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

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
