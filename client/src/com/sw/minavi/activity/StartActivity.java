package com.sw.minavi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.growthpush.GrowthPush;
import com.growthpush.handler.DefaultReceiveHandler;
import com.growthpush.model.Environment;
import com.sw.minavi.BuildConfig;
import com.sw.minavi.R;

public class StartActivity extends Activity implements OnClickListener, AnimationListener {

	// 設定マネージャー
	private SharedPreferences sPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// テスト用
//		final Intent emeIntent = new Intent(this, EmergencyActivity.class);
//		emeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(emeIntent);
//		finish();

		final Intent intent = new Intent(this, MainActivity.class);
		
		// 設定値の呼び出し
		sPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean emeFlg = sPref.getBoolean("pref_emergency_flag", false);

		if (emeFlg == true) {
			startActivity(intent);
			finish();
		}
		
		setContentView(R.layout.activity_start);
		
		ImageView img = (ImageView) findViewById(R.id.logoImage);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.start_logo_animation);
		
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				startActivity(intent);
				finish();
			}
		});
		
		img.startAnimation(anim);
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		finish();
	}
}
