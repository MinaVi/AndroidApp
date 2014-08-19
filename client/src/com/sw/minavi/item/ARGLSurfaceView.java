package com.sw.minavi.item;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
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
import com.sw.minavi.model.TextModel;
import com.sw.minavi.util.GLUtils;
import com.sw.minavi.util.LocationUtilities;
import com.sw.minavi.util.PhysicsUtil;

public class ARGLSurfaceView extends GLSurfaceView implements OnGestureListener {

	private float viewWidth;
	private float viewHeight;

	private Ground ground = new Ground();
	private Grid grid = new Grid();
	private LineOfSight lineOfSight = new LineOfSight();

	private float eyepos[] = new float[3];
	private float centerPos[] = new float[3];
	private float upPos[] = new float[3];

	private OpenGLRenderer renderer;
	private List<LocalItem> locationItems;
	private Location loadLocation;
	private Context activityContext;
	private ArrayList<Model> models = new ArrayList<Model>();
	private ArrayList<TextModel> textModels = new ArrayList<TextModel>();

	private Handler handler;
	private Runnable lookAtRunnable;
	private DebugView debugView;
	private double azimuthRad;
	private float pitch;
	private float roll;
	private int azimuth;

	{
		lookAtRunnable = new Runnable() {

			@Override
			public void run() {
				debugView.updateStatus(eyepos[0], eyepos[1], eyepos[2],
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
		Vector3f rayTo;

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

			// ----------------------------------------------
			// モデルの描画
			// ----------------------------------------------
			Vector3f eyePosVec = new Vector3f(eyepos);
			//Vector3f centerPosVec = new Vector3f(centerPos);
			List<Vector3f> arcSightList = getArcSight(45);

			Collections.sort(models, new ModelComparator());
			for (Model model : models) {

				boolean isIntersect = false;
				for (Vector3f[] vertexList : model.getVector3f()) {
					for (Vector3f arcSightTo : arcSightList) {
						if (GLUtils.intersect(eyePosVec, arcSightTo,
								vertexList)) {
							isIntersect = true;
							break;
						}
					}
				}
				if (isIntersect) {
					gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, white, 0);
					gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, white, 0);
					gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, white, 0);
				} else {
					gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, darkColor, 0);
					gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, darkColor, 0);
					gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, darkColor, 0);
				}
				model.draw(gl);
			}

			// ----------------------------------------------
			// テキストの描画
			// ----------------------------------------------
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, white, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, white, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, white, 0);
			for (TextModel textModel : textModels) {
				textModel.draw(gl);
			}

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
			// // マトリックス記憶
			// gl.glPushMatrix();
			//
			// gl.glLineWidth(1.0f);
			// grid.drawGrid(gl, 50, 50, 1.0f, 1.0f);
			// // マトリックスを戻す
			// gl.glPopMatrix();

			// ----------------------------------------------
			// 視点-モデル間の線分の描画
			// ----------------------------------------------
			// for (Model model : models) {
			// // マトリックス記憶
			// gl.glPushMatrix();
			// gl.glLineWidth(10.0f);
			// lineOfSight.drawLine(gl, eyepos[0], eyepos[1] - 1, eyepos[2],
			// model.getX(), model.getY(), model.getZ());
			// // マトリックスを戻す
			// gl.glPopMatrix();
			// }

			// ----------------------------------------------
			// 視点-注視点間の線分の描画
			// ----------------------------------------------
			// マトリックス記憶
			gl.glPushMatrix();

			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, red, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, red, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, red, 0);
			gl.glLineWidth(10.0f);

			lineOfSight.drawLine(gl, eyepos[0], eyepos[1] - 1, eyepos[2],
					centerPos[0], centerPos[1], centerPos[2]);
			// マトリックスを戻す
			gl.glPopMatrix();

			// ----------------------------------------------
			// 視点-RayTo間の線分の描画
			// ----------------------------------------------
			if (rayTo != null) {
				gl.glPushMatrix(); // マトリックス記憶

				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, green,
						0);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, green,
						0);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
						green, 0);
				gl.glLineWidth(10.0f);

				lineOfSight.drawLine(gl, eyepos[0], eyepos[1] - 1, eyepos[2],
						rayTo.x, rayTo.y, rayTo.z);
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

			HashSet<String> arImgNameSet = new HashSet<String>();
			for (LocalItem locationItem : locationItems) {
				arImgNameSet.add(locationItem.getArImageName());
			}
			int[] arImgTextures = new int[arImgNameSet.size()];
			int[] textImgTextures = new int[locationItems.size()];
			arImgNameSet.clear();

			gl.glGenTextures(arImgTextures.length, arImgTextures, 0);
			gl.glGenTextures(textImgTextures.length, textImgTextures, 0);

			int arImgTextureIndex = 0;
			int textImgTextureIndex = 0;
			HashMap<String, Integer> arImgNameToTextIdMap = new HashMap<String, Integer>();

			for (LocalItem locationItem : locationItems) {

				int arImgTextureId;
				{
					Bitmap arImgBitmap = BitmapFactory.decodeResource(
							getResources(),
							getResources().getIdentifier(
									locationItem.getArImageName(), "drawable",
									activityContext.getPackageName()));
					if (arImgNameToTextIdMap.containsKey(locationItem
							.getArImageName())) {
						arImgTextureId = arImgNameToTextIdMap.get(locationItem
								.getArImageName());
					} else {
						arImgTextureId = arImgTextures[arImgTextureIndex++];
						arImgNameToTextIdMap.put(locationItem.getArImageName(),
								arImgTextureId);
					}

					gl.glBindTexture(GL10.GL_TEXTURE_2D, arImgTextureId);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
					android.opengl.GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0,
							arImgBitmap, 0);
					arImgBitmap.recycle();
				}

				int textImgTextureId;
				Bitmap textBitmap = Bitmap.createBitmap(256, 256,
						Config.ARGB_8888);
				{
					Canvas canvas = new Canvas(textBitmap);
					Paint paint = new Paint();
					paint.setColor(Color.WHITE);
					paint.setStyle(Style.FILL);
					canvas.drawColor(0);
					canvas.drawText(MessageFormat.format(
							"ID[{0}]:Msg[{1}]:Img[{2}]", locationItem.getId(),
							locationItem.getMessage(),
							locationItem.getArImageName()), 0, 15, paint);

					textImgTextureId = textImgTextures[textImgTextureIndex++];

					// テクスチャ情報の設定
					gl.glBindTexture(GL10.GL_TEXTURE_2D, textImgTextureId);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
					android.opengl.GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0,
							textBitmap, 0);

					// bitmapを破棄
					textBitmap.recycle();
				}

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

				int degree = (int) (Math.atan2(xPos - eyepos[0], zPos
						- eyepos[2]) * 180d / Math.PI) + 180;

				models.add(new Model(xPos, 0, zPos, degree, arImgTextureId,
						locationItem));

				textModels.add(new TextModel(xPos, 0, zPos, degree,
						textImgTextureId));
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

				Vector3f rayFrom = new Vector3f(eyepos);
				rayTo = PhysicsUtil
						.getRayTo(x, y, eye, look, up, width, height);

				Model model = getRayIntersectModel(rayFrom, rayTo);
				if (model != null) {
					LocalItem item = model.getItem();
					String message = MessageFormat.format("{0},{1}",
							item.getId(), item.getArImageName());

					Toast.makeText(activityContext, message, Toast.LENGTH_SHORT)
							.show();
				}
				break;
			}

			return true;
		}

		private Model getRayIntersectModel(Vector3f a, Vector3f b) {

			for (Model model : models) {
				for (Vector3f[] posList : model.getVector3f()) {
					if (GLUtils.intersect(a, b, posList)) {
						return model;
					}
				}
			}
			return null;
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
			List<LocalItem> locationItems, DebugView debugView) {
		super(context);

		this.activityContext = context;
		this.loadLocation = loadLocation;
		this.locationItems = locationItems;
		this.debugView = debugView;
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

	public void changeAzimuthEvent(double radian, float pitch, float roll) {
		this.azimuthRad = radian;
		this.azimuth = LocationUtilities
				.radianToDegreeForAzimuth((float) radian);
		this.pitch = pitch;
		this.roll = roll;
		getCenterPos();
	}

	private void getCenterPos() {
		centerPos[0] = (float) (Math.cos(azimuthRad) * eyepos[2] + eyepos[0]);
		centerPos[1] = (float) ((Math.cos(roll) * eyepos[2] + eyepos[1]) * -1.0);
		centerPos[2] = (float) (Math.sin(azimuthRad) * eyepos[2] + eyepos[2]);
	}

	private List<Vector3f> getArcSight(int sight) {

		List<Vector3f> arcSightPointList = new ArrayList<Vector3f>();

		int centerAngle = sight / 2;

		for (int i = 1; i <= centerAngle; i++) {
			double radian = LocationUtilities.degreesToRads(i + azimuth);
			arcSightPointList.add(new Vector3f((float) (Math.cos(radian)
					* eyepos[2] + eyepos[0]), centerPos[1], (float) (Math
					.sin(radian) * eyepos[2] + eyepos[2])));
		}
		for (int i = -centerAngle; i <= -1; i++) {
			double radian = LocationUtilities.degreesToRads(i + azimuth);
			arcSightPointList.add(new Vector3f((float) (Math.cos(radian)
					* eyepos[2] + eyepos[0]), centerPos[1], (float) (Math
					.sin(radian) * eyepos[2] + eyepos[2])));
		}
		return arcSightPointList;
	}

	public Vector3f getMiddlePoint(float x0, float y0, float z0, float x1,
			float y1, float z1) {
		float x = (x1 + x0) / 2.0f;
		float y = (y1 + y0) / 2.0f;
		float z = (z1 + z0) / 2.0f;

		return new Vector3f(x, y, z);
	}
}
