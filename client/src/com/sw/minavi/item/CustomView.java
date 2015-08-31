package com.sw.minavi.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.sw.minavi.R;

public class CustomView extends LinearLayout {

	private DebugView debug;
	private ProduceView produce;
	private MiniMap miniMap;

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View layout = LayoutInflater.from(context).inflate(R.layout.custom_ar_info, this);
		debug = (DebugView) layout.findViewById(R.id.debug);
		produce = (ProduceView) layout.findViewById(R.id.produce);
		miniMap = (MiniMap) layout.findViewById(R.id.miniMap);

//		miniMap.setVisibility(View.INVISIBLE);
		debug.setVisibility(View.INVISIBLE);
	}

	public DebugView getDebug() {
		return debug;
	}

	public ProduceView getProduce() {
		return produce;
	}

	public MiniMap getMiniMap() {
		return miniMap;
	}

}
