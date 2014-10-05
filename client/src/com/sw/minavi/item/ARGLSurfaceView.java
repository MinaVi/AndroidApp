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
import android.content.Intent;
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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sw.minavi.R;
import com.sw.minavi.model.Lockon;
import com.sw.minavi.model.Model;
import com.sw.minavi.model.TextModel;
import com.sw.minavi.util.GLUtils;
import com.sw.minavi.util.LocationUtilities;
import com.sw.minavi.util.PhysicsUtil;

public class ARGLSurfaceView extends GLSurfaceView {

	private Location loadLocation;
	private List<LocalItem> locationItems = new ArrayList<LocalItem>();
	private OpenGLRenderer renderer;

	public ArrayList<Model> models = new ArrayList<Model>();
	private ArrayList<TextModel> textModels = new ArrayList<TextModel>();
	private Lockon lockon;

	private CustomView customView;

	private int pitch;
	private int roll;
	private int azimuth;

	private boolean isUpdateLocation = false;

	public Camera3D camera;

	private Model lockonModel;

	private Handler handler;
	private Runnable extraRunnable;
	{
		extraRunnable = new Runnable() {
			@Override
			public void run() {

				ProduceView produce = customView.getProduce();
				if (lockonModel == null) {
					produce.setVisibility(View.INVISIBLE);
				} else {
					LocalItem item = lockonModel.getItem();
					produce.updateProduce(
							getResources().getIdentifier(
									item.getArImageName(), "drawable",
									getContext().getPackageName()),
							item.getMessage(),
							item.getLat() + "," + item.getLon());

					produce.setVisibility(View.VISIBLE);
				}
			}
		};
	}

	public ARGLSurfaceView(Context context) {
		super(context);
		init(); // 初期化
	}

	public ARGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(); // 初期化
	}

	public void setCustomView(CustomView customView) {
		this.customView = customView;
	}

	public void updateLocation(Location loadLocation, List<LocalItem> locationItems) {
		this.loadLocation = loadLocation;
		this.locationItems = locationItems;

		// 位置情報の更新を通知
		this.isUpdateLocation = true;
	}

	public void changeAzimuthEvent(double azimuthRad, float pitchRad, float rollRad) {

		// 方位・ロール・ピッチをdegreeに変換
		this.azimuth = LocationUtilities
				.radianToDegreeForAzimuth((float) azimuthRad);
		this.pitch = LocationUtilities
				.radianToDegreeForAzimuth(pitchRad);
		this.roll = LocationUtilities
				.radianToDegreeForAzimuth(rollRad);

		// カメラの回転
		camera.rotateLook((float) azimuthRad, rollRad);

		// デバッグ情報の更新
		customView.setGyroSensorValues(azimuth, roll, pitch);
		customView.setCameraValues(camera);
	}

	private void init() {

		// 背景を透過
		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setEGLConfigChooser(8, 8, 8, 8, 0, 0);

		// カメラの作成
		camera = new Camera3D(
				new float[] { 0.0f, 0.5f, 5.0f },
				new float[] { 0.0f, 0.0f, 0.0f },
				new float[] { 0.0f, 1.0f, 0.0f });

		// レンダラーの指定
		renderer = new OpenGLRenderer();
		this.setRenderer(renderer);
		this.setOnTouchListener(renderer);

		// ハンドラの作成
		this.handler = new Handler();
	}

	class OpenGLRenderer implements Renderer, OnTouchListener {

		private float lockonAngle = 0.0f;

		@Override
		public void onDrawFrame(GL10 gl) {

			// ----------------------------------------------
			// モデル描画前
			// ----------------------------------------------
			// クリア処理
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

			// ライティングをON
			gl.glEnable(GL10.GL_LIGHTING);

			// 光源の位置を取得
			Vector3f middle = GLUtils.getMiddlePoint(camera.getEye(), camera.getLook());
			float[] lightPos = new float[] { middle.x, middle.y, middle.x, 0.0f };

			// 光源を有効にして位置を設定
			gl.glEnable(GL10.GL_LIGHT0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, GLUtils.WHITE, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, GLUtils.WHITE, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, GLUtils.WHITE, 0);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();

			// アルファブレンド
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

			// 陰面消去
			gl.glEnable(GL10.GL_CULL_FACE);
			gl.glCullFace(GL10.GL_BACK);
			// スムースシェーディング
			gl.glShadeModel(GL10.GL_SMOOTH);
			// マテリアル
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, GLUtils.GRAY, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, GLUtils.GRAY, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, GLUtils.GRAY, 0);
			gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 80f);

			// カメラ位置を設定
			camera.gluLookAt(gl);

			// モデルの更新
			updateModels(gl);

			// ----------------------------------------------
			// モデルの描画
			// ----------------------------------------------
			if (isExistModels()) {

				List<Model> highlightModels = new ArrayList<Model>();
				List<Vector3f> arcSightList = getArcSight(45);
				Vector3f eye = camera.getEye();

				Collections.sort(models, new ModelComparator());

				for (Model model : models) {

					boolean isIntersect = false;
					for (Vector3f[] vertexList : model.getVector3f()) {
						for (Vector3f arcSightTo : arcSightList) {
							if (GLUtils.intersect(eye, arcSightTo,
									vertexList)) {
								isIntersect = true;
								break;
							}
						}
						if (GLUtils.intersect(eye, camera.getLook(),
								vertexList)) {
							highlightModels.add(model);
						}
					}
					if (isIntersect) {
						gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, GLUtils.WHITE, 0);
						gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, GLUtils.WHITE, 0);
						gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, GLUtils.WHITE, 0);
					} else {
						gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, GLUtils.DARK, 0);
						gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, GLUtils.DARK, 0);
						gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, GLUtils.DARK, 0);
					}
					model.draw(gl);
				}

				// ----------------------------------------------
				// モデルのロックオン
				// ----------------------------------------------
				float maxDistance = 0;
				Model tmpLockonModel = null;
				for (Model highlightModel : highlightModels) {
					float distance = highlightModel.distance(camera.getLook());
					if (maxDistance < distance) {
						maxDistance = distance;
						tmpLockonModel = highlightModel;
					}
				}
				lockonModel = tmpLockonModel;
			}
			handler.post(extraRunnable);

			// ----------------------------------------------
			// テキストの描画
			// ----------------------------------------------
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, GLUtils.WHITE, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, GLUtils.WHITE, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, GLUtils.WHITE, 0);
			for (TextModel textModel : textModels) {
				textModel.draw(gl);
			}

			// ----------------------------------------------
			// ロックオンサイトの描画
			// ----------------------------------------------
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, GLUtils.WHITE, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, GLUtils.WHITE, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, GLUtils.WHITE, 0);
			lockon.draw(gl, camera, azimuth, pitch, roll, lockonAngle);

			// ----------------------------------------------
			// モデル描画後処理
			// ----------------------------------------------
			// ロックオンサイトのアングルを更新
			lockonAngle += 1.0f;
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {

			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluPerspective(gl, 45f, (float) width / height, 1f, 50f);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {

			gl.glClearColor(0.0f, 0.0f, 0.0f, 0);
			gl.glClearDepthf(1.0f);

			// ロックオンサイトの作成
			createLockonSight(gl);
		}

		private void createLocationModels(GL10 gl) {
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
									getContext().getPackageName()));

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

					float[] results = new float[1];
					Location.distanceBetween(loadLocation.getLatitude(),
							loadLocation.getLongitude(),
							locationItem.getLat(), locationItem.getLon(), results);

					canvas.drawText(MessageFormat.format(
							"場所[{0}]:距離[{1}m]", locationItem.getMessage(), results[0]), 0, 20, paint);

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

				float[] results = new float[1];
				Location.distanceBetween(loadLatitude, loadLongitude, itemLatitude,
						itemLongitude, results);

				double azimuthRad = LocationUtilities.getDirectionRad(
						itemLatitude, itemLongitude, loadLatitude,
						loadLongitude);

				float scaleLength = results[0];

				Vector3f eye = camera.getEye();
				float xPos = (float) (Math.cos(azimuthRad) * scaleLength + eye.x);
				float zPos = (float) (Math.sin(azimuthRad) * scaleLength + eye.z);

				int degree = (int) (Math.atan2(xPos - eye.x, zPos - eye.z) * 180d / Math.PI) + 180;

				models.add(new Model(xPos, 0, zPos, degree, arImgTextureId,
						locationItem));
				textModels.add(new TextModel(xPos, 0, zPos, degree,
						textImgTextureId));

			}
		}

		private void createLockonSight(GL10 gl) {

			// ロックオンサイトイメージの取得
			Bitmap imgBitmap = BitmapFactory.decodeResource(
					getResources(),
					R.drawable.sight_large);

			// テクスチャIDの取得
			int[] imgTextures = new int[1];
			gl.glGenTextures(imgTextures.length, imgTextures, 0);
			int imgTextureId = imgTextures[0];

			// テクスチャのバインド
			gl.glBindTexture(GL10.GL_TEXTURE_2D, imgTextureId);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			android.opengl.GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, imgBitmap, 0);

			// bitmapの破棄
			imgBitmap.recycle();

			// ロックオンサイトの作成
			lockon = new Lockon(imgTextureId);
		}

		private void updateModels(GL10 gl) {
			// 位置情報が更新されているか判定
			if (isUpdateLocation) {

				// 現在表示しているモデルをクリア
				models.clear();
				textModels.clear();

				// ロケーションモデルの作成
				createLocationModels(gl);

				// 位置情報の更新フラグをOFFに設定
				isUpdateLocation = false;
			}
		}

		private boolean isExistModels() {
			return models.size() > 0;
		}

		private List<Vector3f> getArcSight(int sight) {

			List<Vector3f> arcSightPointList = new ArrayList<Vector3f>();

			int centerAngle = sight / 2;

			Vector3f eye = camera.getEye();
			Vector3f look = camera.getLook();

			for (int i = 1; i <= centerAngle; i++) {
				double radian = LocationUtilities.degreesToRads(i + azimuth);
				float x = (float) (Math.cos(radian) * eye.z + eye.x);
				float y = look.y;
				float z = (float) (Math.sin(radian) * eye.z + eye.z);
				arcSightPointList.add(new Vector3f(x, y, z));
			}
			for (int i = -centerAngle; i <= -1; i++) {
				double radian = LocationUtilities.degreesToRads(i + azimuth);
				float x = (float) (Math.cos(radian) * eye.z + eye.x);
				float y = look.y;
				float z = (float) (Math.sin(radian) * eye.z + eye.z);
				arcSightPointList.add(new Vector3f(x, y, z));
			}
			return arcSightPointList;
		}

		@Override
		public synchronized boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				break;

			case MotionEvent.ACTION_DOWN:
				break;

			case MotionEvent.ACTION_UP:

				Vector3f eye = camera.getEye();
				Vector3f look = camera.getLook();
				Vector3f up = camera.getUp();

				int x = (int) event.getX();
				int y = (int) event.getY();

				int width = getWidth();
				int height = getHeight();

				Vector3f rayTo = PhysicsUtil.getRayTo(x, y, eye, look, up, width, height);

				Model model = getRayIntersectModel(eye, rayTo);
				if (model != null) {
					LocalItem item = model.getItem();

					if (item.getTalkGroupId() != 0) {
						Intent intent = new Intent();
						intent.setClassName("com.sw.minavi",
								"com.sw.minavi.activity.TalkActivity");
						intent.putExtra("pinId", item.getId());
						intent.putExtra("talkGroupId", item.getTalkGroupId());
						getContext().startActivity(intent);
					}
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
		@Override
		public int compare(Model s, Model t) {
			Vector3f eye = camera.getEye();
			float ds = s.distance(eye.x, eye.y, eye.z);
			float dt = t.distance(eye.x, eye.y, eye.z);
			if (ds == dt)
				return 0;
			else if (ds < dt)
				return 1;
			else
				return -1;
		}
	}
}
