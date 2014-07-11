package com.sw.minavi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.sw.minavi.R;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		ImageView imageTalkIcon = (ImageView)findViewById(R.id.talk_icon);
		imageTalkIcon.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		        Intent intent = new Intent();
		        intent.setClassName("com.sw.minavi.activity", ".activity.TalkActivity");
		        startActivity(intent);
		    }
		});
		*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.talk_icon) {
			Intent intent = new Intent(this, TalkActivity.class);
			startActivity(intent);
		}

	}

}
