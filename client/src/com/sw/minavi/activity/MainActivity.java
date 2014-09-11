package com.sw.minavi.activity;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.sw.minavi.R;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView charaImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		charaImage = (ImageView) findViewById(R.id.chara_img);
		
		String[] charas = {"chitoge","nomal_s", "yan", "amuro", "attenboro"};
		
		Random rdmRandom = new Random();
		int k = rdmRandom.nextInt(charas.length);
		

		charaImage.setImageResource(getResources().getIdentifier(charas[k], "drawable",
				getPackageName()));
		
		
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
			intent.putExtra("pinId", 0);
			intent.putExtra("areaId", 0);
			intent.putExtra("talk_group_id", 0);
			startActivity(intent);
		} else if (v.getId() == R.id.ar_icon) {
//			Intent intent = new Intent(this, ARAcitivity.class);
//			startActivity(intent);
			Intent intent = new Intent(this, GLARActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.setting_icon) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}
	}

}
