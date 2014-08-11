package com.sw.minavi.item;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class OverlayView extends View {

	private int width;
	private int height;

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
		width = w;
		height = h;
	}

	/**
	 * 描画実行時のイベント
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

//		Paint paint = new Paint();
//		paint.setStyle(Paint.Style.FILL);
//		paint.setARGB(100, 0, 0, 0);
//		// 中央十字表示
//		int len = height / 10;
//		paint.setARGB(255, 255, 0, 0);
//
//		double padding = width / ARAcitivity.SECTOR;
//		int sum = 0;
//		for(int i = 1; i < ARAcitivity.SECTOR; i++) {
//			sum += padding;
//			canvas.drawLine(sum, 0, sum, height, paint);
//		}
	}
}
