package com.sw.minavi.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sw.minavi.R;

public class ProduceView extends LinearLayout {
	private TextView title;
	private TextView content;
	private ImageView img;

	public ProduceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View layout = LayoutInflater.from(context).inflate(R.layout.custom_produce, this);
		img = (ImageView) layout.findViewById(R.id.produce_img);
		title = (TextView) layout.findViewById(R.id.produce_title);
		content = (TextView) layout.findViewById(R.id.produce_contents);
	}

	public void updateProduce(int resId, String title, String content) {
		this.img.setImageResource(resId);
		this.title.setText(title);
		this.content.setText(content);
	}
}
