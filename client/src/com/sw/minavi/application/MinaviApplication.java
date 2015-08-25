package com.sw.minavi.application;

import com.growthpush.GrowthPush;
import com.growthpush.handler.DefaultReceiveHandler;
import com.growthpush.model.Environment;
import com.sw.minavi.BuildConfig;
import com.sw.minavi.activity.EmergencyActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class MinaviApplication extends Application {

	@Override
	public void onCreate() {
		// プッシュ通知？
		GrowthPush.getInstance().initialize(getApplicationContext(),8247,"OxnK9E2mJqRtkyWal5ix3rnLokuyYFLf", BuildConfig.DEBUG ? Environment.development : Environment.production, true).register("605563933504");
		GrowthPush.getInstance().trackEvent("Launch");
		GrowthPush.getInstance().setDeviceTags();
		
		GrowthPush.getInstance().setReceiveHandler(new DefaultReceiveHandler(new DefaultReceiveHandler.Callback() {
			@Override
			public void onOpen(Context context, Intent intent) {
				final Intent emeIntent = new Intent(context, EmergencyActivity.class);
				emeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(emeIntent);
			}
			
		}));
	}
	
}
