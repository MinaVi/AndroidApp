package com.sw.minavi.item;

import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.sw.minavi.model.Grid;
import com.sw.minavi.model.Ground;
import com.sw.minavi.model.Model;
import com.sw.minavi.util.LocationUtilities;

public class ARGLSurfaceView extends GLSurfaceView implements OnGestureListener {

	private Model model = new Model();
	private Ground ground = new Ground();
	private Grid grid = new Grid();
	private float angle = 0.0f;
	private float eyepos[] = new float[3];
	private float centerPos[] = new float[3];
	private float viewWidth = 0;

	private OpenGLRenderer renderer;
	private List<LocalItem> locationItems;
	private Location loadLocation;
	private Context activityContext;
	private HashMap<Integer, Bitmap> itemIdToTextureMap = new HashMap<Integer, Bitmap>();
	private int[] textures = new int[1];

	// レンダラークラス
	public class OpenGLRenderer implements Renderer, OnTouchListener {

		float lightpos[] = { 0.0f, 0.0f, 4.0f, 0.0f };
		float red[] = { 1.0f, 0.0f, 0.0f, 1.0f };
		float green[] = { 0.0f, 1.0f, 0.0f, 1.0f };
		float blue[] = { 0.0f, 0.0f, 1.0f, 1.0f };
		float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float gray[] = { 0.5f, 0.5f, 0.5f, 1.0f };
		float yellow[] = { 1.0f, 1.0f, 0.0f, 1.0f };
		float darkColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		float brightColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };

		@Override
		public void onDrawFrame(GL10 gl) {

			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

			// ライティングをON
			gl.glEnable(GL10.GL_LIGHTING);
			// 光源を有効にして位置を設定
			gl.glEnable(GL10.GL_LIGHT0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION,
					new float[] { eyepos[0], eyepos[1] + 1.0f, eyepos[2], 1.0f }, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION,
					new float[] { centerPos[0], centerPos[1], centerPos[2], 1.0f }, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, white, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, white, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, white, 0);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();

			// カメラ位置を設定
			GLU.gluLookAt(gl, eyepos[0], eyepos[1], eyepos[2], centerPos[0], centerPos[1], centerPos[2], 0, 1, 0);

			// 軸の描画
			gl.glPushMatrix(); // マトリックス記憶
			gl.glEnable(GL10.GL_DEPTH_TEST);
			gl.glDepthFunc(GL10.GL_LEQUAL);
			gl.glDepthMask(true);
			gl.glLineWidth(5.0f);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, white, 0);
			ground.draw(gl);
			gl.glPopMatrix(); // マトリックスを戻す

			// グリッドの描画
			gl.glPushMatrix(); // マトリックス記憶
			// デプテスト
			gl.glEnable(GL10.GL_DEPTH_TEST);
			gl.glDepthFunc(GL10.GL_LEQUAL);
			gl.glDepthMask(true);

			// アルファテスト
			gl.glEnable(GL10.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL10.GL_GEQUAL, 0.1f);

			//　テクスチャ
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// 線の長さ
			gl.glLineWidth(1.0f);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, green, 0);
			grid.drawGrid(gl, 20, 20, 10, 10);
			gl.glPopMatrix(); // マトリックスを戻す

			// １つめの描画
			//						gl.glPushMatrix(); // マトリックス記憶
			//						gl.glTranslatef(1, 0, 0);
			//						gl.glRotatef(angle, 0, 1, 0);
			//						gl.glEnable(GL10.GL_CULL_FACE);
			//						gl.glCullFace(GL10.GL_BACK);
			//						// デプステスト
			//						gl.glEnable(GL10.GL_DEPTH_TEST);
			//						gl.glDepthFunc(GL10.GL_LEQUAL);
			//						gl.glDepthMask(true);
			//			
			//						gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, gray, 0);
			//						gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, gray, 0);
			//						gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, gray, 0);
			//						gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 80f);
			//						gl.glShadeModel(GL10.GL_FLAT);
			//						model.draw(gl);
			//						gl.glPopMatrix(); // マトリックスを戻す
			//
			//			// ふたつめの描画
			//			gl.glPushMatrix(); // マトリックス記憶
			//			gl.glTranslatef(-1, 0, 0);
			//			gl.glRotatef(angle, 0, 1, 0);
			//			gl.glCullFace(GL10.GL_BACK);
			//			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, gray, 0);
			//			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, gray, 0);
			//			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, gray, 0);
			//			gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 80f);
			//			// スムースシェーディング
			//			gl.glShadeModel(GL10.GL_SMOOTH);
			//			model.draw(gl);
			//			gl.glPopMatrix(); // マトリックスを戻す

			for (LocalItem locationItem : locationItems) {
				double itemLatitude = locationItem.getLat();
				double itemLongitude = locationItem.getLon();

				double loadLatitude = loadLocation.getLatitude();
				double loadLongitude = loadLocation.getLongitude();

				double lengthInMeter = LocationUtilities.getDistance(itemLatitude, itemLongitude, loadLatitude,
						loadLongitude, 10) * 1000.0;
				double azimuth = LocationUtilities.getDirection(itemLatitude, itemLongitude, loadLatitude,
						loadLongitude);
				double azimuthRad = LocationUtilities.getDirectionRad(itemLatitude, itemLongitude, loadLatitude,
						loadLongitude);

				//float scaleLength = (float) (lengthInMeter / 1000.0);
				float scaleLength = 2;
				float xPos = (float) (Math.cos(azimuthRad) * scaleLength + eyepos[0]);
				float zPos = (float) (Math.sin(azimuthRad) * scaleLength + eyepos[2]);

				//float degree = (float) (Math.atan2(eyepos[0] - xPos, eyepos[2] - zPos) * 180d / Math.PI);
				float degree = (float) (Math.atan2(xPos - eyepos[0], zPos - eyepos[2]) * 180d / Math.PI) + 180;

				gl.glPushMatrix(); // マトリックス記憶

				gl.glTranslatef(xPos, 0, zPos);
				gl.glRotatef(degree, 0, 1, 0);

				gl.glEnable(GL10.GL_CULL_FACE);
				gl.glCullFace(GL10.GL_BACK);

				// デプステスト
				gl.glEnable(GL10.GL_DEPTH_TEST);
				gl.glDepthFunc(GL10.GL_LEQUAL);
				gl.glDepthMask(true);

				//gl.glCullFace(GL10.GL_BACK);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, darkColor, 0);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, brightColor, 0);
				gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 80f);

				// スムースシェーディング
				gl.glShadeModel(GL10.GL_SMOOTH);

				if (itemIdToTextureMap.containsKey(locationItem.getId())) {

					// テクスチャの設定
					Bitmap image = itemIdToTextureMap.get(locationItem.getId());
					gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
					GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
				}

				// 背景透過イメージの有効化
				gl.glEnable(GL10.GL_BLEND);
				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

				// 描画
				model.draw(gl);

				gl.glDisable(GL10.GL_BLEND);
				gl.glPopMatrix(); // マトリックスを戻す
			}

			angle += 0.5; // 回転角度は最後に計算
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluPerspective(gl, 50f, (float) width / height, 0.01f, 100f);

			viewWidth = width;
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0);
			gl.glClearDepthf(1.0f);
			eyepos[0] = 0;
			eyepos[1] = 0.5f;
			eyepos[2] = 5;

			centerPos[0] = 0;
			centerPos[1] = 0;
			centerPos[2] = 0;

			ground.initialize(gl, config);

			for (LocalItem locationItem : locationItems) {
				Bitmap image = BitmapFactory.decodeResource(
						getResources(),
						getResources().getIdentifier(locationItem.getArImageName(),
								"drawable", activityContext.getPackageName()));

				// テクスチャを生成
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glGenTextures(1, textures, 0);
				itemIdToTextureMap.put(locationItem.getId(), image);
			}
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				Toast.makeText(activityContext, "MOVE:" + event.getX() + "," + event.getY(), Toast.LENGTH_SHORT).show();
				break;
			case MotionEvent.ACTION_DOWN: 
				Toast.makeText(activityContext, "DOWN:" + event.getX() + "," + event.getY(), Toast.LENGTH_SHORT).show();
				break;
			case MotionEvent.ACTION_UP: 
				Toast.makeText(activityContext, "UP:ViewPort:" + event.getX() + "," + event.getY() + ":CENTER:" + centerPos[0] + "," + centerPos[1] + "," + centerPos[2], Toast.LENGTH_SHORT).show();
				break;
			}

			return true;
		}
	}

	// サーフェースビューのコンストラクタ
	public ARGLSurfaceView(Context context, Location loadLocation, List<LocalItem> locationItems) {
		super(context);

		this.activityContext = context;
		this.loadLocation = loadLocation;
		this.locationItems = locationItems;

		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		renderer = new OpenGLRenderer();
		setRenderer(renderer);
		setOnTouchListener(renderer);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float distx, float disty) {
//		eyepos[0] += distx * 0.01;
//		eyepos[1] += disty * 0.01;
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	float degree = 0;

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		//
		//		if (viewWidth / 2 < event.getX()) {
		//			degree += 5;
		//		} else {
		//			degree -= 5;
		//		}
		//
		//		double radian = degree * Math.PI / 180.0;
		//
		//		centerPos[0] = (float) (Math.cos(radian) * eyepos[2] + eyepos[0]);
		//		centerPos[2] = (float) (Math.sin(radian) * eyepos[2] + eyepos[2]);
		// renderer.onTouch(this, event);
		//		Toast.makeText(getContext(), event.getX() + "," + event.getY(), Toast.LENGTH_SHORT).show();
		return false;
	}

	public void changeCenterPos(double radian, float pitch, float roll) {
		centerPos[0] = (float) (Math.cos(radian) * eyepos[2] + eyepos[0]);
		centerPos[1] = (float) ((Math.cos(roll) * eyepos[2] + eyepos[1]) * -1.0);
		centerPos[2] = (float) (Math.sin(radian) * eyepos[2] + eyepos[2]);
	}
}
