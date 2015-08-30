package com.sw.minavi.activity;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sw.minavi.R;
import com.sw.minavi.item.BgmManager;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView charaImage;
	private MediaPlayer mPlayer;
	private boolean bgmPlayingFlg = false;

	private ImageView talkbtn;

	// 設定マネージャー
	private SharedPreferences sPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean emeFlg = sPref.getBoolean("pref_emergency_flag", false);

		if (emeFlg == true) {
			talkbtn = (ImageView) findViewById(R.id.talk_icon);
			talkbtn.setVisibility(View.GONE);
			charaImage = (ImageView) findViewById(R.id.chara_img);
			charaImage.setImageResource(getResources().getIdentifier("mina2_emergency",
					"drawable", getPackageName()));
			
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.bg_layput);
			layout.setBackgroundResource(R.drawable.resize);
		}else{
			BgmManager.newIntance(getApplicationContext()).playSound(
					R.raw.kibou_no_hikari);

			charaImage = (ImageView) findViewById(R.id.chara_img);

			String[] charas = { "mina1_smile", "nami1_smile", "rise_sammar_smile_up", "rise_sammar_nomal", "mina2_smile" };

			Random rdmRandom = new Random();
			int k = rdmRandom.nextInt(charas.length);

			charaImage.setImageResource(getResources().getIdentifier(charas[k],
					"drawable", getPackageName()));

		}

		// BGM 再生
		// mPlayer = MediaPlayer.create(getApplicationContext(),
		// R.raw.kibou_no_hikari);
		// mPlayer.setLooping(true);
		// mPlayer.seekTo(0);
		//
		// if (!mPlayer.isPlaying()) {
		// mPlayer.start();
		// }
		
		/*
		 * ImageView imageTalkIcon = (ImageView)findViewById(R.id.talk_icon);
		 * imageTalkIcon.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { Intent intent = new Intent();
		 * intent.setClassName("com.sw.minavi.activity",
		 * ".activity.TalkActivity"); startActivity(intent); } });
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
			// Intent intent = new Intent(this, ARAcitivity.class);
			// startActivity(intent);
			bgmPlayingFlg = true;
			Intent intent = new Intent(this, GLARActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.setting_icon) {
			bgmPlayingFlg = false;
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.map_icon) {
			bgmPlayingFlg = true;
			Intent intent = new Intent(this, LocationActivity.class);
			startActivity(intent);
		}
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
		BgmManager.newIntance(getApplicationContext()).playSound(
				R.raw.kibou_no_hikari);
	}

}
