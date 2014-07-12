package com.sw.minavi.item;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class OverlayView extends View {

	public OverlayView(Context context) {
		super(context);
		setFocusable(true);
	}

	public OverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
	}

	public OverlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFocusable(true);
	}

	/**
	 * サイズ変更時（縦横の変更）のイベント
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// サイズ変更は考慮してない
	}

	/**
	 * 描画実行時のイベント
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
