package com.sw.minavi.item;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastRunnable {

	private static final long DILAY = 3000L;
	private Handler handler;
	private Context context;

	public ToastRunnable(Context context) {
		handler = new Handler();
		this.context = context;
	}

	public void run() {
		// Viewの操作だけじゃなくてトーストを出すのにもHandler使わないといけないのか。。。
		handler.post(new Runnable() {
			@Override
			public void run() {
				// トーストを出す。
				Toast.makeText(context, "位置情報を取得中です ..", Toast.LENGTH_SHORT).show();

				handler.postDelayed(this, DILAY);
			}
		});
	}

	public void stop() {
		handler.removeCallbacksAndMessages(null);
	}
}
