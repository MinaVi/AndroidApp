package com.sw.minavi.activity;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sw.minavi.R;
import com.sw.minavi.activity.beans.TalkBeans;
import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.LocalItemTableManager;
import com.sw.minavi.activity.db.TalkEventsTableManager;
import com.sw.minavi.activity.db.TalkGroupsTableManager;
import com.sw.minavi.activity.db.TalkSelectsTableManager;
import com.sw.minavi.http.TransportLog;
import com.sw.minavi.item.BgmManager;
import com.sw.minavi.item.LocalItem;
import com.sw.minavi.item.TalkEvent;
import com.sw.minavi.item.TalkGroup;
import com.sw.minavi.item.TalkSelect;

public class TalkActivity extends Activity implements OnClickListener {

	private DatabaseOpenHelper helper;

	private TextView nameTextView;
	private TextView talkTextView;

	private LinearLayout answersArea;
	private TextView answerTextFirstView;
	private TextView answerTextSecondView;
	private TextView answerTextThirdView;
	private TextView answerTextForthView;

	private ImageView backImage;
	private ImageView charaImageLeft;
	private ImageView charaImageRight;
	private ImageView answerBackImage;
	private ImageView arBtn;
	private ImageView checkAreaBtn;

	private ArrayList<TalkBeans> talkTexts = new ArrayList<TalkBeans>();
	private int textCount = 0;
	private boolean toAr = false;

	// 進行中のGroupId
	int groupId = 0;

	// 選択中フラグ
	private boolean selectingFlg = false;
	// 選択肢数（分岐フラグを兼ねる）
	private int answerCount = 0;
	// 選択肢テキスト
	private ArrayList<TalkBeans> answerTexts = new ArrayList<TalkBeans>();

	// 選択肢の数だけ分岐
	private ArrayList<TalkBeans> answerTextsFirst = new ArrayList<TalkBeans>();
	private ArrayList<TalkBeans> answerTextsSecond = new ArrayList<TalkBeans>();
	private ArrayList<TalkBeans> answerTextsThird = new ArrayList<TalkBeans>();
	private ArrayList<TalkBeans> answerTextsForth = new ArrayList<TalkBeans>();

	// ARからのIDを格納
	private int pinId = 0;
	private int areaId = 0;
	private int arTalkGroupId = 0;

	// private ProgressBar progBar;
	private ProgressDialog progLog;

	// 位置情報取得用
	/** 現在ロードしている座標 */
	private Location loadLocation = null;
	/** 位置情報管理 */
	private LocationManager locationManager;
	/** プロバイダ */
	private List<String> providers;
	private LocationListener locationListener;
	private Timer locationTimer;
	private StringBuffer city;
	long time;

	/** 座標アイテム */
	private ArrayList<LocalItem> locationItems = new ArrayList<LocalItem>();

	// 設定マネージャー
	private SharedPreferences sPref;

	private Handler mHandler;

	private MediaPlayer mPlayer;
	private boolean bgmPlayingFlg = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// BGM設定
		// mPlayer = MediaPlayer
		// .create(getApplicationContext(), R.raw.spring_wind);
		// mPlayer.setLooping(true);
		// mPlayer.seekTo(0);
		// if (!mPlayer.isPlaying()) {
		// mPlayer.start();
		// }
		BgmManager.newIntance(getApplicationContext()).playSound(
				R.raw.spring_wind);

		// 設定値の呼び出し
		sPref = PreferenceManager.getDefaultSharedPreferences(this);

		setContentView(R.layout.activity_talk);
		nameTextView = (TextView) findViewById(R.id.nameText);
		talkTextView = (TextView) findViewById(R.id.talkText);
		answerTextFirstView = (TextView) findViewById(R.id.answerTextFirstView);
		answerTextSecondView = (TextView) findViewById(R.id.answerTextSecondView);
		answerTextThirdView = (TextView) findViewById(R.id.answerTextThirdView);
		answerTextForthView = (TextView) findViewById(R.id.answerTextForthView);

		answersArea = (LinearLayout) findViewById(R.id.answer_area);
		answersArea.setVisibility(View.GONE);
		answerBackImage = (ImageView) findViewById(R.id.chara_img);
		answerBackImage.setVisibility(View.GONE);

		backImage = (ImageView) findViewById(R.id.back_btn);

		// 左キャラクター初期設定
		charaImageLeft = (ImageView) findViewById(R.id.chara_image_left);


		charaImageRight = (ImageView) findViewById(R.id.chara_image_right);
		arBtn = (ImageView) findViewById(R.id.ar_btn);
		checkAreaBtn = (ImageView) findViewById(R.id.check_area_btn);

		// progBar = (ProgressBar) findViewById(R.id.progressBar1);
		progLog = new ProgressDialog(this);

		// ProgressDialog のタイトルを設定
		progLog.setTitle("位置情報取得");

		// ProgressDialog のメッセージを設定
		progLog.setMessage("しばらくお待ちください");

		// ProgressDialog の確定（false）／不確定（true）を設定します
		progLog.setIndeterminate(false);

		// ProgressDialog のスタイルを水平スタイルに設定
		// progLog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 円スタイルの場合
		progLog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// ProgressDialog のキャンセルが可能かどうか
		progLog.setCancelable(true);

		// progBar.setVisibility(View.GONE);

		// charaImageLeft.setVisibility(View.GONE);
		charaImageRight.setVisibility(View.GONE);

		pinId = getIntent().getExtras().getInt("pinId");

		arTalkGroupId = getIntent().getExtras().getInt("talkGroupId");

		// test
		// setTestTexts();

		// Sampleの登録
		helper = new DatabaseOpenHelper(this);
		TalkGroupsTableManager.getInstance(helper).InsertSample();
		TalkEventsTableManager.getInstance(helper).InsertSample();
		TalkSelectsTableManager.getInstance(helper).InsertSample();

		String lang = sPref.getString("lang", "Japanese");
		TalkEventsTableManager.lang = lang;

		// handler準備
		mHandler = new Handler() {
			public void handleMassage(Message msg) {
				// メッセージ表示

			};
		};

		// イベントセット
		ArrayList<TalkGroup> talkGroup = getTalkGroupIds();
		setTexts(talkGroup);

	}

	// 対象のイベント一覧を取得
	private ArrayList<TalkGroup> getTalkGroupIds() {

		ArrayList<TalkGroup> groups = null;
		if (areaId > 0) {
			groups = TalkGroupsTableManager.getInstance(helper)
					.GetRecordsByAreaId(areaId);
		} else {
			groups = TalkGroupsTableManager.getInstance(helper).GetRecords();
		}

		return groups;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.talk_back_btn) {
			finish();
		} else if (v.getId() == R.id.ar_btn) {
			bgmPlayingFlg = true;
			Intent intent = new Intent(this, GLARActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.check_area_btn) {
			startLocationService();

			// if (city != null) {
			// selectingFlg = false;
			// textCount = 1;
			// answerCount = 0;
			// talkTexts = new ArrayList<TalkBeans>();
			// talkTexts.add(new TalkBeans(0, null, null, 0, 0, 0));
			// nameTextView.setText("ミナ");
			// talkTextView.setText("現在地は「" + city + "」です");
			// charaImageLeft.setImageResource(R.drawable.nomal_n);
			// city = null;
			//
			// if (isExistItem() == true) {
			// toAr = true;
			// // 一旦トーク内容リセット
			// talkTexts = null;
			//
			// answerCount = 0;
			// textCount = 0;
			//
			// ArrayList<TalkGroup> talkGroup = getTalkGroupIds();
			// setTexts(talkGroup);
			//
			// }
			//
			// }

		} else if (selectingFlg == true) {
			// 一旦トーク内容リセット
			talkTexts = null;

			answerCount = 0;
			textCount = 0;

			if (v.getId() == R.id.answerTextFirstView) {
				talkTexts = answerTextsFirst;
				groupId = talkTexts.get(0).getTalkGroupId();
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				answerBackImage.setVisibility(View.GONE);
				viewText();
			} else if (v.getId() == R.id.answerTextSecondView) {
				talkTexts = answerTextsSecond;
				groupId = talkTexts.get(0).getTalkGroupId();
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				answerBackImage.setVisibility(View.GONE);
				viewText();
			} else if (v.getId() == R.id.answerTextThirdView) {
				talkTexts = answerTextsThird;
				groupId = talkTexts.get(0).getTalkGroupId();
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				answerBackImage.setVisibility(View.GONE);
				viewText();
			} else if (v.getId() == R.id.answerTextForthView) {
				talkTexts = answerTextsForth;
				groupId = talkTexts.get(0).getTalkGroupId();
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				answerBackImage.setVisibility(View.GONE);
				viewText();
			}
		} else if (v.getId() == R.id.talkText) {
			viewText();
		}

	}

	private void viewText() {
		if ((textCount == talkTexts.size()) && answerCount > 0) {
			// 分岐に入る
			selectingFlg = true;
			answerTextFirstView.setText(answerTexts.get(0).getFirstTalkStr());
			// answerTextFirstView.setBackgroundColor(Color.WHITE);
			answerTextSecondView.setText(answerTexts.get(1).getFirstTalkStr());
			// answerTextSecondView.setBackgroundColor(Color.WHITE);
			answerTextThirdView.setText("");
			answerTextForthView.setText("");

			if (answerCount > 2) {
				answerTextThirdView.setText(answerTexts.get(2)
						.getFirstTalkStr());
				// answerTextThirdView.setBackgroundColor(Color.WHITE);
			}
			if (answerCount > 3) {
				answerTextForthView.setText(answerTexts.get(3)
						.getFirstTalkStr());
				// answerTextForthView.setBackgroundColor(Color.WHITE);
			}

			answerBackImage.setVisibility(View.VISIBLE);
			answersArea.setVisibility(View.VISIBLE);

		} else if (textCount == talkTexts.size()) {

			// ARからの遷移だった場合、ARに戻す
			if (arTalkGroupId != 0) {
				bgmPlayingFlg = true;
				Intent intent = new Intent(this, GLARActivity.class);
				startActivity(intent);
//				finish();
			}
			
			textCount = 0;
			nameTextView.setText("");
			talkTextView.setText("");
			if (talkTexts.get(textCount).getPosition() == 0) {
				charaImageLeft.setImageResource(talkTexts.get(textCount)
						.getImageId());
			} else {
				charaImageRight.setVisibility(View.VISIBLE);
				charaImageRight.setImageResource(talkTexts.get(textCount)
						.getImageId());
			}

			// 既読処理
			helper = new DatabaseOpenHelper(this);
			TalkGroupsTableManager.getInstance(helper).updateIsRead(
					String.valueOf(groupId));

			// ARへ遷移
			// TODO 固定値
//			if (groupId == 14) {
//				bgmPlayingFlg = true;
//				Intent intent = new Intent(this, GLARActivity.class);
//				startActivity(intent);
//			}

			// 次の会話内容セット
			// イベントセット
			ArrayList<TalkGroup> talkGroup = getTalkGroupIds();
			charaImageRight.setVisibility(View.GONE);
			setTexts(talkGroup);

			// メニュー表示
			arBtn.setVisibility(View.VISIBLE);
			checkAreaBtn.setVisibility(View.VISIBLE);

		} else {

			// メニュー非表示
			arBtn.setVisibility(View.GONE);
			checkAreaBtn.setVisibility(View.GONE);

			nameTextView.setText(talkTexts.get(textCount).getTalkName());
			talkTextView.setText(talkTexts.get(textCount).getFirstTalkStr());
			if (talkTexts.get(textCount).getPosition() == 0) {
				charaImageLeft.setImageResource(talkTexts.get(textCount)
						.getImageId());
			} else {
				charaImageRight.setImageResource(talkTexts.get(textCount)
						.getImageId());
				charaImageRight.setVisibility(View.VISIBLE);
			}
			textCount = textCount + 1;
		}
	}

	private void setTexts(ArrayList<TalkGroup> groups) {
		talkTexts = new ArrayList<TalkBeans>();
		answerTexts = new ArrayList<TalkBeans>();
		answerTextsFirst = new ArrayList<TalkBeans>();
		answerTextsSecond = new ArrayList<TalkBeans>();
		answerTextsThird = new ArrayList<TalkBeans>();
		answerTextsForth = new ArrayList<TalkBeans>();
		String str1 = "";
		int selectTalkGroupId = 0;
		int firstGroupId = 0;
		int secondGroupId = 0;
		int thirdGroupId = 0;
		int forthGroupId = 0;
		TalkBeans beans = null;

		charaImageRight.setVisibility(View.GONE);

		// ARからの遷移の場合、優先的に表示する。
		// groupId = 0;
		TalkGroup group = null;
		if (arTalkGroupId != 0) {
			groupId = arTalkGroupId;

			// 取得されたグループから対象のグループを選択
			for (int i = 0; i < groups.size(); i++) {
				if (groups.get(i).getTalkGroupId() == arTalkGroupId) {

					group = groups.get(i);
					break;

				}
			}

			if (group.getBackGroundFileNmae() != null
					&& group.getBackGroundFileNmae().length() > 0) {
				// 背景を特殊背景に変える
				backImage.setImageResource(getResources().getIdentifier(
						group.getBackGroundFileNmae(), "drawable",
						getPackageName()));
			}

			// エリアが指定されている場合は、エリア情報のみ
			areaId = group.getAreaId();
			// 次回以降は通常通り
//			arTalkGroupId = 0;
		} else if (toAr == true) {
			// TODO　固定値
			groupId = 13;
			// 取得されたグループから対象のグループを選択
			for (int i = 0; i < groups.size(); i++) {
				if (groups.get(i).getTalkGroupId() == groupId) {

					group = groups.get(i);
					break;

				}
			}

			if (group.getBackGroundFileNmae() != null
					&& group.getBackGroundFileNmae().length() > 0) {
				// 背景を特殊背景に変える
				backImage.setImageResource(getResources().getIdentifier(
						group.getBackGroundFileNmae(), "drawable",
						getPackageName()));
			}

			// エリアが指定されている場合は、エリア情報のみ
			areaId = group.getAreaId();
			// 次回以降は通常通り
			toAr = false;
			arTalkGroupId = 0;
		} else {
			// 取得されたグループからランダムで表示
			Random rnd = new Random();
			int ran = rnd.nextInt(groups.size());
			group = groups.get(ran);
			
			// TODO debaug
			if (group.getBackGroundFileNmae() != null
					&& group.getBackGroundFileNmae().length() > 2) {
				// 背景を特殊背景に変える
				backImage.setImageResource(getResources().getIdentifier(
						group.getBackGroundFileNmae(), "drawable",
						getPackageName()));
			}else{
				backImage.setImageResource(getResources().getIdentifier(
						"aoiike", "drawable",
						getPackageName()));
			}
			
			groupId = group.getTalkGroupId();
		}

		// 選択されたグループに紐づくイベント取得
		ArrayList<TalkEvent> talkEvents = TalkEventsTableManager.getInstance(
				helper).GetRecords(groupId);

		// イベント情報をセット
		talkTexts = getTalkBeans(talkEvents);

		// 取得されたグループから選択肢情報を取得
		if (group.getSelectFlg() == 1) {
			ArrayList<TalkSelect> selects = TalkSelectsTableManager
					.getInstance(helper).GetRecords(groupId);
			// 一つしか取れないはず
			TalkSelect select = selects.get(0);

			answerCount = select.getAnswerCount();

			// 選択肢は最低二つ
			str1 = select.getFirstTalkBody();
			selectTalkGroupId = select.getFirstTalkGroupId();
			beans = new TalkBeans(selectTalkGroupId, str1, "", 0, 0, 0);
			answerTexts.add(beans);
			firstGroupId = select.getFirstTalkGroupId();
			ArrayList<TalkEvent> firstSelectEve = TalkEventsTableManager
					.getInstance(helper).GetRecords(firstGroupId);
			answerTextsFirst = getTalkBeans(firstSelectEve);

			str1 = select.getSecondTalkBody();
			selectTalkGroupId = select.getFirstTalkGroupId();
			beans = new TalkBeans(selectTalkGroupId, str1, "", 0, 0, 0);
			answerTexts.add(beans);
			secondGroupId = select.getSecondTalkGroupId();
			ArrayList<TalkEvent> secondSelectEve = TalkEventsTableManager
					.getInstance(helper).GetRecords(secondGroupId);
			answerTextsSecond = getTalkBeans(secondSelectEve);

			if (select.getAnswerCount() > 2) {
				str1 = select.getThirdTalkBody();
				selectTalkGroupId = select.getFirstTalkGroupId();
				beans = new TalkBeans(selectTalkGroupId, str1, "", 0, 0, 0);
				answerTexts.add(beans);
				thirdGroupId = select.getThirdTalkGroupId();
				ArrayList<TalkEvent> thirdSelectEve = TalkEventsTableManager
						.getInstance(helper).GetRecords(thirdGroupId);
				answerTextsThird = getTalkBeans(thirdSelectEve);
			}
			if (select.getAnswerCount() > 3) {
				str1 = select.getForthTalkBody();
				selectTalkGroupId = select.getFirstTalkGroupId();
				beans = new TalkBeans(selectTalkGroupId, str1, "", 0, 0, 0);
				answerTexts.add(beans);
				forthGroupId = select.getForthTalkGroupId();
				ArrayList<TalkEvent> forthSelectEve = TalkEventsTableManager
						.getInstance(helper).GetRecords(forthGroupId);
				answerTextsForth = getTalkBeans(forthSelectEve);
			}

			// 分岐後イベントをそれぞれ取得

		}
	}

	private ArrayList<TalkBeans> getTalkBeans(ArrayList<TalkEvent> talkEvents) {

		ArrayList<TalkBeans> texts = new ArrayList<TalkBeans>();
		String str1 = "";
		String talkName = "";
		int imageId = 0;
		int animationType = 0;
		int pos = 0;
		TalkBeans beans = null;

		// イベント情報をセット
		for (int i = 0; i < talkEvents.size(); i++) {
			str1 = talkEvents.get(i).getTalkBody();

			// 名前を挿入
			str1 = MessageFormat.format(str1,
					sPref.getString("name", "unknown"));

			talkName = talkEvents.get(i).getTalkName();
			imageId = getResources().getIdentifier(
					talkEvents.get(i).getImageFileName(), "drawable",
					getPackageName());
			animationType = talkEvents.get(i).getImageAnimationType();
			pos = talkEvents.get(i).getImagePositionType();
			beans = new TalkBeans(talkEvents.get(i).getTalkGroupId(), str1,
					talkName, imageId, animationType, pos);
			texts.add(beans);
		}

		return texts;
	}

	private void startLocationService() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 位置情報機能非搭載端末の場合
		if (locationManager == null) {
			// 何も行いません
			return;
		}

		// @see
		// http://developer.android.com/reference/android/location/LocationManager.html#getBestProvider%28android.location.Criteria,%20boolean%29
		final Criteria criteria = new Criteria();
		// PowerRequirement は設定しないのがベストプラクティス
		// Accuracy は設定しないのがベストプラクティス
		// criteria.setAccuracy(Criteria.ACCURACY_FINE); ← Accuracy
		// で最もやってはいけないパターン
		// 以下は必要により
		criteria.setBearingRequired(false); // 方位不要
		criteria.setSpeedRequired(false); // 速度不要
		criteria.setAltitudeRequired(false); // 高度不要

		final String provider = locationManager.getBestProvider(criteria, true);
		if (provider == null) {
			// 位置情報が有効になっていない場合は、Google Maps アプリライクな [現在地機能を改善] ダイアログを起動します。
			new AlertDialog.Builder(this)
					.setTitle("現在地機能を改善")
					.setMessage(
							"現在、位置情報は一部有効ではないものがあります。次のように設定すると、もっともすばやく正確に現在地を検出できるようになります:\n\n● 位置情報の設定でGPSとワイヤレスネットワークをオンにする\n\n● Wi-Fiをオンにする")
					.setPositiveButton("設定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int which) {
									// 端末の位置情報設定画面へ遷移
									try {
										startActivity(new Intent(
												"android.settings.LOCATION_SOURCE_SETTINGS"));
									} catch (final ActivityNotFoundException e) {
										// 位置情報設定画面がない糞端末の場合は、仕方ないので何もしない
									}
								}
							})
					.setNegativeButton("スキップ",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int which) {
								} // 何も行わない
							}).create().show();

			stopLocationService();
			return;
		}

		// 最後に取得できた位置情報が5分以内のものであれば有効とします。
		// final Location lastKnownLocation =
		// locationManager.getLastKnownLocation(provider);
		// // XXX - 必要により判断の基準を変更してください。
		// if (lastKnownLocation != null && (new Date().getTime() -
		// lastKnownLocation.getTime()) <= (5 * 60 * 1000L)) {
		// setLocation(lastKnownLocation);
		// return;
		// }

		// Toast の表示と LocationListener の生存時間を決定するタイマーを起動します。
		locationTimer = new Timer(true);
		time = 0L;
		final Handler handler = new Handler();
		locationTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {

						// ProgressDialog のキャンセルされた時に呼び出されるコールバックを登録
						progLog.setOnCancelListener(new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								// Thread を停止
								// Toast.makeText(TalkActivity.this,
								// "現在地を特定できませんでした。", Toast.LENGTH_LONG)
								// .show();
								stopLocationService();
								cancel();
								checkAreaBtn.setVisibility(View.VISIBLE);
								progLog.cancel();
							}
						});

						if (time == 1000L) {
							// Toast.makeText(TalkActivity.this, "現在地を特定しています。",
							// Toast.LENGTH_LONG).show();

							checkAreaBtn.setVisibility(View.GONE);
							// ProgressDialog を表示
							progLog.show();

						} else if (time >= (30 * 1000L)) {
							// Toast.makeText(TalkActivity.this,
							// "現在地を特定できませんでした。", Toast.LENGTH_LONG)
							// .show();
							stopLocationService();
							cancel();
							progLog.cancel();
							checkAreaBtn.setVisibility(View.VISIBLE);
							return;
						}
						time = time + 1000L;
					}
				});
			}
		}, 0L, 1000L);

		// 位置情報の取得を開始します。
		locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(final Location location) {
				setLocation(location);
			}

			@Override
			public void onProviderDisabled(final String provider) {
			}

			@Override
			public void onProviderEnabled(final String provider) {
			}

			@Override
			public void onStatusChanged(final String provider,
					final int status, final Bundle extras) {
			}
		};
		locationManager.requestLocationUpdates(provider, 60000, 0,
				locationListener);

	}

	void stopLocationService() {
		if (locationTimer != null) {
			locationTimer.cancel();
			locationTimer.purge();
			locationTimer = null;
		}
		if (locationManager != null) {
			if (locationListener != null) {
				locationManager.removeUpdates(locationListener);
				locationListener = null;
			}
			locationManager = null;
		}
	}

	private void setLocation(final Location location) {
		stopLocationService();
		loadLocation = location;

		// location情報をサーバーへ送信
		TransportLog tl = new TransportLog(this, mHandler);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy'-'MM'-'dd kk':'mm':'ss':'");

		tl.execute(String.valueOf(location.getLongitude()),
				String.valueOf(location.getLatitude()),
				String.valueOf(location.getAltitude()),
				String.valueOf(location.getAccuracy()),
				String.valueOf(location.getSpeed()), sdf.format(date),
				String.valueOf(0), sPref.getString("name", "unknown"));

		// TODO: ここに位置情報が取得できた場合の処理を記述します。
		Geocoder mGeocoder = new Geocoder(getApplicationContext(), Locale.JAPAN);

		try {
			List<Address> addrs = mGeocoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 1);

			city = new StringBuffer();
			for (Address addr : addrs) {
				int idx = addr.getMaxAddressLineIndex();
				for (int i = 1; i <= idx; i++) {
					city.append(addr.getAddressLine(i));
				}
			}
			checkAreaBtn.setVisibility(View.VISIBLE);
			progLog.cancel();

			if (city != null) {
				selectingFlg = false;
				textCount = 1;
				answerCount = 0;
				talkTexts = new ArrayList<TalkBeans>();
				talkTexts.add(new TalkBeans(0, null, null, 0, 0, 0));
				nameTextView.setText("ミナ");
				talkTextView.setText("現在地は「" + city + "」です");
				charaImageLeft.setImageResource(R.drawable.mina1_nomal);
				city = null;

				if (isExistItem() == true) {
					toAr = true;
					// 一旦トーク内容リセット
					talkTexts = null;

					answerCount = 0;
					textCount = 0;

					ArrayList<TalkGroup> talkGroup = getTalkGroupIds();
					setTexts(talkGroup);

				}

			}

		} catch (Exception e) {

		}

	}

	private boolean isExistItem() {
		locationItems = LocalItemTableManager.getInstance(helper)
				.GetAroundRecords(loadLocation);

		if (locationItems.size() > 0) {
			return true;
		}

		return false;

	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocationService();
		if (!bgmPlayingFlg) {
			BgmManager.newIntance(getApplicationContext()).playSound(-1);
		}
		bgmPlayingFlg = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		BgmManager.newIntance(getApplicationContext()).playSound(
				R.raw.spring_wind);
	}

}
