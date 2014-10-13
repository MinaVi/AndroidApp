package com.sw.minavi.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sw.minavi.R;

public class ProduceView extends LinearLayout {
	private TextView title;
	private TextView content;
	private ImageView img;
	private int talkGroupId;

	public ProduceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View layout = LayoutInflater.from(context).inflate(R.layout.custom_produce, this);
		img = (ImageView) layout.findViewById(R.id.produce_img);
		title = (TextView) layout.findViewById(R.id.produce_title);
		content = (TextView) layout.findViewById(R.id.produce_contents);

		ProduceClickListenrer listenrer = new ProduceClickListenrer();
		this.setOnClickListener(listenrer);
	}

	public void updateProduce(int resId, String title, String content, int talkGroupId) {
		this.img.setImageResource(resId);
		this.title.setText(title);
		this.content.setText(content);
		this.talkGroupId = talkGroupId;
	}

	private class ProduceClickListenrer implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (talkGroupId <= 0) {
				return;
			}
			Toast.makeText(getContext(), "talkGroupId:" + talkGroupId, Toast.LENGTH_SHORT).show();


//			switch (v.getId()) {
//			case R.id.produce_img:
//			case R.id.produce_title:
//			case R.id.produce_contents:
//
//				Intent intent = new Intent();
//				intent.setClassName("com.sw.minavi",
//						"com.sw.minavi.activity.TalkActivity");
//				intent.putExtra("talkGroupId", talkGroupId);
//				getContext().startActivity(intent);
//				break;
//			}
		}
	}
}
