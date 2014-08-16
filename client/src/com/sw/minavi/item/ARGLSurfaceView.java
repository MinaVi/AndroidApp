package com.sw.minavi.item;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Handler;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.sw.minavi.model.Grid;
import com.sw.minavi.model.Ground;
import com.sw.minavi.model.LineOfSight;
import com.sw.minavi.model.Model;
import com.sw.minavi.util.LocationUtilities;
import com.sw.minavi.util.PhysicsUtil;

public class ARGLSurfaceView extends GLSurfaceView implements OnGestureListener {

	private Ground ground = new Ground();
	private Grid grid = new Grid();
	private LineOfSight lineOfSight = new LineOfSight();
	private float angle = 0.0f;
	private float eyepos[] = new float[3];
	private float centerPos[] = new float[3];
	private float upPos[] = new float[3];
	private float viewWidth = 0;
	private int viewHeight;

	private OpenGLRenderer renderer;
	private List<LocalItem> locationItems;
	private Location loadLocation;
	private Context activityContext;
	private int[] textures = new int[1];
	private ArrayList<Model> models = new ArrayList<Model>();

	private Handler handler;
	private Runnable lookAtRunnable;
	private LookAtView lookAtView;

	{
		lookAtRunnable = new Runnable() {

			@Override
			public void run() {
				lookAtView.updateStatus(eyepos[0], eyepos[1], eyepos[2],
						centerPos[0], centerPos[1], centerPos[2], upPos[0],
						upPos[1], upPos[2]);
			}
		};
	}

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
		Vector3f point;

		@Override
		public void onDrawFrame(GL10 gl) {

			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

			Vector3f middlePoint = getMiddlePoint(eyepos[0], eyepos[1],
					eyepos[2], centerPos[0], centerPos[1], centerPos[2]);

			// ライティングをON
			gl.glEnable(GL10.GL_LIGHTING);
			// 光源を有効にして位置を設定
			gl.glEnable(GL10.GL_LIGHT0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, new float[] {
					middlePoint.x, middlePoint.y, middlePoint.x, 0.0f }, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, white, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, white, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, white, 0);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();

			// カメラ位置を設定
			GLU.gluLookAt(gl, eyepos[0], eyepos[1], eyepos[2], centerPos[0],
					centerPos[1], centerPos[2], upPos[0], upPos[1], upPos[2]);
			handler.post(lookAtRunnable);

			// ------------------------------------------------
			// 軸の描画
			// ------------------------------------------------
			// gl.glPushMatrix(); // マトリックス記憶
			// gl.glEnable(GL10.GL_DEPTH_TEST);
			// gl.glDepthFunc(GL10.GL_LEQUAL);
			// gl.glDepthMask(true);
			// gl.glLineWidth(5.0f);
			// gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,
			// GL10.GL_AMBIENT_AND_DIFFUSE, white, 0);
			// ground.draw(gl);
			// gl.glPopMatrix(); // マトリックスを戻す

			// ------------------------------------------------
			// グリッドの描画
			// ------------------------------------------------
			// gl.glPushMatrix(); // マトリックス記憶
			// // デプテスト
			// gl.glEnable(GL10.GL_DEPTH_TEST);
			// gl.glDepthFunc(GL10.GL_LEQUAL);
			// gl.glDepthMask(true);
			//
			// // アルファテスト
			// gl.glEnable(GL10.GL_ALPHA_TEST);
			// gl.glAlphaFunc(GL10.GL_GEQUAL, 0.1f);
			//
			// // テクスチャ
			// gl.glEnable(GL10.GL_TEXTURE_2D);
			// // 線の長さ
			// gl.glLineWidth(1.0f);
			// gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,
			// GL10.GL_AMBIENT_AND_DIFFUSE, green, 0);
			// grid.drawGrid(gl, 20, 20, 10, 10);
			//
			// gl.glLineWidth(10.0f);
			// Grid.drawLine(gl, eyepos[0], eyepos[1], eyepos[2], centerPos[0],
			// centerPos[1], centerPos[2]);
			// gl.glPopMatrix(); // マトリックスを戻す

			// ------------------------------------------------
			// ???の描画
			// ------------------------------------------------
			// gl.glPushMatrix(); // マトリックス記憶
			// gl.glEnable(GL10.GL_DEPTH_TEST);
			// gl.glDepthFunc(GL10.GL_LEQUAL);
			// gl.glDepthMask(true);
			// gl.glLineWidth(10.0f);
			// gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,
			// GL10.GL_AMBIENT_AND_DIFFUSE, green, 0);
			// // lineOfSight.drawLine(gl, eyepos[0], eyepos[1] - 1, eyepos[2],
			// // centerPos[0], centerPos[1], centerPos[2]);
			// gl.glPopMatrix(); // マトリックスを戻す
			//

			// デプステスト
			// gl.glEnable(GL10.GL_DEPTH_TEST);
			// gl.glDepthFunc(GL10.GL_LEQUAL); gl.glDepthMask(true);

			// アルファブレンド
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

			// アルファテスト
			// gl.glEnable(GL10.GL_ALPHA_TEST);
			// gl.glAlphaFunc(GL10.GL_GEQUAL, 0.1f);

			// 陰面消去
			gl.glEnable(GL10.GL_CULL_FACE);
			gl.glCullFace(GL10.GL_BACK);
			// スムースシェーディング
			gl.glShadeModel(GL10.GL_SMOOTH);
			// マテリアル
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, gray, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, gray, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, gray, 0);
			gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 80f);

			Collections.sort(models, new ModelComparator());
			for (Model model : models) {
				// 描画
				model.draw(gl);

				// マトリックス記憶
				gl.glPushMatrix();
				gl.glLineWidth(10.0f);
				lineOfSight.drawLine(gl, eyepos[0], eyepos[1] - 1, eyepos[2],
						model.getX(), model.getY(), model.getZ());
				// マトリックスを戻す
				gl.glPopMatrix();
			}

			// マトリックス記憶
			gl.glPushMatrix();
			gl.glLineWidth(10.0f);
			lineOfSight.drawLine(gl, eyepos[0], eyepos[1] - 1, eyepos[2],
					centerPos[0], centerPos[1], centerPos[2]);
			// マトリックスを戻す
			gl.glPopMatrix();

			if (point != null) {
				gl.glPushMatrix(); // マトリックス記憶

				gl.glLineWidth(10.0f);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK,
						GL10.GL_AMBIENT_AND_DIFFUSE, green, 0);

				lineOfSight.drawLine(gl, eyepos[0], eyepos[1] - 1, eyepos[2],
						point.x, point.y, point.z);
				gl.glPopMatrix(); // マトリックスを戻す
			}
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluPerspective(gl, 50f, (float) width / height, 0.01f, 100f);

			viewWidth = width;
			viewHeight = height;
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

			upPos[0] = 0;
			upPos[1] = 1;
			upPos[2] = 0;

			ground.initialize(gl, config);

			for (LocalItem locationItem : locationItems) {
				Bitmap image = BitmapFactory.decodeResource(
						getResources(),
						getResources().getIdentifier(
								locationItem.getArImageName(), "drawable",
								activityContext.getPackageName()));

				// テクスチャを生成
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glGenTextures(1, textures, 0);

				double itemLatitude = locationItem.getLat();
				double itemLongitude = locationItem.getLon();

				double loadLatitude = loadLocation.getLatitude();
				double loadLongitude = loadLocation.getLongitude();

				// double lengthInMeter = LocationUtilities.getDistance(
				// itemLatitude, itemLongitude, loadLatitude,
				// loadLongitude, 10) * 1000.0;
				// double azimuth = LocationUtilities.getDirection(itemLatitude,
				// itemLongitude, loadLatitude, loadLongitude);
				double azimuthRad = LocationUtilities.getDirectionRad(
						itemLatitude, itemLongitude, loadLatitude,
						loadLongitude);

				// float scaleLength = (float) (lengthInMeter / 1000.0);
				float scaleLength = 5;
				float xPos = (float) (Math.cos(azimuthRad) * scaleLength + eyepos[0]);
				float zPos = (float) (Math.sin(azimuthRad) * scaleLength + eyepos[2]);

				float degree = (float) (Math.atan2(xPos - eyepos[0], zPos
						- eyepos[2]) * 180d / Math.PI) + 180;

				models.add(new Model(xPos, 0, zPos, degree, textures, image,
						locationItem));
			}
		}

		@Override
		public synchronized boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				break;

			case MotionEvent.ACTION_DOWN:
				break;

			case MotionEvent.ACTION_UP:
				Vector3f eye = new Vector3f(eyepos);
				Vector3f look = new Vector3f(centerPos);
				Vector3f up = new Vector3f(upPos);

				int x = (int) event.getX();
				int y = (int) event.getY();

				int width = getWidth();
				int height = getHeight();

				point = PhysicsUtil
						.getRayTo(x, y, eye, look, up, width, height);

				Vector3f rayStart = new Vector3f(eyepos);
				Vector3f rayDir = new Vector3f(centerPos);
				if (isHitArea(rayStart, rayDir)) {

				}
				break;
			}

			return true;
		}

		private boolean isHitArea(Vector3f rayStart, Vector3f rayEnd) {

			for (Model model : models) {

				Vector3f[] posList = model.getVector3f(0, 0, 0);

				Vector3f rayDir = new Vector3f(rayEnd);
				rayDir.sub(rayStart);
				rayDir.normalize();

				// 法線の計算
				Vector3f t0 = new Vector3f(posList[1]);
				Vector3f t1 = new Vector3f(posList[2]);
				t0.sub(posList[0]);
				t1.sub(posList[0]);
				Vector3f normal = new Vector3f();
				normal.cross(t0, t1);
				normal.normalize();

				// 線分判定
				Vector3f xp = new Vector3f(posList[0]);
				xp.sub(rayStart);
				float xpn = xp.dot(normal);
				float vn = rayDir.dot(normal);

				// カリングと発散を外す
				if (-0.00001f <= vn) {
					continue;
				}

				// 当たってる場所までの長さ
				float t = xpn / vn;

				// 後ろ向きのRAYは無視する
				if (t < 0.0f) {
					continue;
				}

				LocalItem item = model.getItem();
				String message = MessageFormat.format("{0},{1}", item.getId(),
						item.getArImageName());
				// String message = MessageFormat.format(
				// "rayStart[{0}]:rayDir[{1}]:HitArea[{2},{3},{4},{5}]",
				// rayStart.toString(), rayDir.toString(),
				// posList[0].toString(), posList[1].toString(),
				// posList[2].toString(), posList[3].toString());
				Toast.makeText(activityContext, message, Toast.LENGTH_SHORT)
						.show();

				return true;
			}

			return false;
		}
	}

	class ModelComparator implements Comparator<Model> {
		public int compare(Model s, Model t) {
			float ds = s.distance(eyepos[0], eyepos[1], eyepos[2]);
			float dt = t.distance(eyepos[0], eyepos[1], eyepos[2]);
			if (ds == dt)
				return 0;
			else if (ds < dt)
				return 1;
			else
				return -1;
		}
	}

	// サーフェースビューのコンストラクタ
	public ARGLSurfaceView(Context context, Location loadLocation,
			List<LocalItem> locationItems, LookAtView lookAtView) {
		super(context);

		this.activityContext = context;
		this.loadLocation = loadLocation;
		this.locationItems = locationItems;
		this.lookAtView = lookAtView;
		this.handler = new Handler();

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
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2,
			float distx, float disty) {
		// eyepos[0] += distx * 0.01;
		// eyepos[1] += disty * 0.01;
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		return false;
	}

	public void changeCenterPos(double radian, float pitch, float roll) {
		centerPos[0] = (float) (Math.cos(radian) * eyepos[2] + eyepos[0]);
		centerPos[1] = (float) ((Math.cos(roll) * eyepos[2] + eyepos[1]) * -1.0);
		centerPos[2] = (float) (Math.sin(radian) * eyepos[2] + eyepos[2]);
	}

	public Vector3f getMiddlePoint(float x0, float y0, float z0, float x1,
			float y1, float z1) {
		float x = (x1 + x0) / 2.0f;
		float y = (y1 + y0) / 2.0f;
		float z = (z1 + z0) / 2.0f;

		return new Vector3f(x, y, z);
	}
}
