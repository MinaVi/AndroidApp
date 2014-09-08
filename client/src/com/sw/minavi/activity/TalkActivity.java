package com.sw.minavi.activity;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sw.minavi.R;
import com.sw.minavi.activity.beans.TalkBeans;
import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.TalkEventsTableManager;
import com.sw.minavi.activity.db.TalkGroupsTableManager;
import com.sw.minavi.activity.db.TalkSelectsTableManager;
import com.sw.minavi.item.TalkEvent;
import com.sw.minavi.item.TalkGroup;
import com.sw.minavi.item.TalkSelect;

public class TalkActivity extends Activity implements OnClickListener, LocationListener {

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
	
	// 位置情報取得用
    private OnGetLocationListener mListener = null;
    
    private static final int INTERVAL_TIME = 1000 * 60 * 3;  // 3分
    private static final int INTERVAL_DISTANCE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_talk);
		nameTextView = (TextView) findViewById(R.id.nameText);
		talkTextView = (TextView) findViewById(R.id.talkText);
		answerTextFirstView = (TextView) findViewById(R.id.answerTextFirstView);
		answerTextSecondView = (TextView) findViewById(R.id.answerTextSecondView);
		answerTextThirdView = (TextView) findViewById(R.id.answerTextThirdView);
		answerTextForthView = (TextView) findViewById(R.id.answerTextForthView);

		answersArea = (LinearLayout) findViewById(R.id.answer_area);
		answersArea.setVisibility(View.GONE);
		answerBackImage = (ImageView) findViewById(R.id.answer_back_image);
		answerBackImage.setVisibility(View.GONE);

		backImage = (ImageView) findViewById(R.id.back_btn);
		charaImageLeft = (ImageView) findViewById(R.id.chara_image_left);
		charaImageRight = (ImageView) findViewById(R.id.chara_image_right);
		arBtn = (ImageView) findViewById(R.id.ar_btn);
		checkAreaBtn = (ImageView) findViewById(R.id.check_area_btn);

		//charaImageLeft.setVisibility(View.GONE);
		charaImageRight.setVisibility(View.GONE);

		pinId = getIntent().getExtras().getInt("pinId");

		arTalkGroupId = getIntent().getExtras().getInt("talkGroupId");

		// test
		//setTestTexts();

		// Sampleの登録
		helper = new DatabaseOpenHelper(this);
		TalkGroupsTableManager.getInstance(helper).InsertSample();
		TalkEventsTableManager.getInstance(helper).InsertSample();
		TalkSelectsTableManager.getInstance(helper).InsertSample();

		// イベントセット
		ArrayList<TalkGroup> talkGroup = getTalkGroupIds();
		setTexts(talkGroup);

	}

	// 対象のイベント一覧を取得
	private ArrayList<TalkGroup> getTalkGroupIds() {

		ArrayList<TalkGroup> groups = null;
		if (areaId > 0) {
			groups = TalkGroupsTableManager.getInstance(helper).GetRecordsByAreaId(areaId);
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
		}else if (v.getId() == R.id.ar_btn){
			Intent intent = new Intent(this, GLARActivity.class);
			startActivity(intent);
		} else if (selectingFlg == true) {
			// 一旦トーク内容リセット
			talkTexts = null;

			answerCount = 0;
			textCount = 0;

			if (v.getId() == R.id.answerTextFirstView) {
				talkTexts = answerTextsFirst;
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				answerBackImage.setVisibility(View.GONE);
				viewText();
			} else if (v.getId() == R.id.answerTextSecondView) {
				talkTexts = answerTextsSecond;
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				answerBackImage.setVisibility(View.GONE);
				viewText();
			} else if (v.getId() == R.id.answerTextThirdView) {
				talkTexts = answerTextsThird;
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				answerBackImage.setVisibility(View.GONE);
				viewText();
			} else if (v.getId() == R.id.answerTextForthView) {
				talkTexts = answerTextsForth;
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
			answerTextFirstView.setBackgroundColor(Color.WHITE);
			answerTextSecondView.setText(answerTexts.get(1).getFirstTalkStr());
			answerTextSecondView.setBackgroundColor(Color.WHITE);
			answerTextThirdView.setText("");
			answerTextForthView.setText("");

			if (answerCount > 2) {
				answerTextThirdView.setText(answerTexts.get(2).getFirstTalkStr());
				answerTextThirdView.setBackgroundColor(Color.WHITE);
			}
			if (answerCount > 3) {
				answerTextForthView.setText(answerTexts.get(3).getFirstTalkStr());
				answerTextForthView.setBackgroundColor(Color.WHITE);
			}

			answerBackImage.setVisibility(View.VISIBLE);
			answersArea.setVisibility(View.VISIBLE);

		} else if (textCount == talkTexts.size()) {
			textCount = 0;
			nameTextView.setText("");
			talkTextView.setText("");
			if (talkTexts.get(textCount).getPosition() == 0) {
				charaImageLeft.setImageResource(talkTexts.get(textCount).getImageId());
			} else {
				charaImageRight.setVisibility(View.VISIBLE);
				charaImageRight.setImageResource(talkTexts.get(textCount).getImageId());
			}

			// test
			// setTestTexts();
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
				charaImageLeft.setImageResource(talkTexts.get(textCount).getImageId());
			} else {
				charaImageRight.setImageResource(talkTexts.get(textCount).getImageId());
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
		int firstGroupId = 0;
		int secondGroupId = 0;
		int thirdGroupId = 0;
		int forthGroupId = 0;
		TalkBeans beans = null;

		charaImageRight.setVisibility(View.GONE);

		// ARからの遷移の場合、優先的に表示する。
		int groupId = 0;
		TalkGroup group = null;
		if (arTalkGroupId != 0) {
			groupId = arTalkGroupId;

			// 取得されたグループから対象のグループを選択
			for (int i = 0; i < groups.size(); i++) {
				if(groups.get(i).getTalkGroupId() == arTalkGroupId){

					group = groups.get(i);
					break;
					
				}
			}

			if (group.getBackGroundFileNmae() != null && group.getBackGroundFileNmae().length() > 0) {
				// 背景を特殊背景に変える
				backImage.setImageResource(getResources().getIdentifier(group.getBackGroundFileNmae(), "drawable", getPackageName()));
			}

			// エリアが指定されている場合は、エリア情報のみ
			areaId = group.getAreaId();
			// 次回以降は通常通り
			arTalkGroupId = 0;
		} else {
			// 取得されたグループからランダムで表示
			Random rnd = new Random();
			int ran = rnd.nextInt(groups.size());
			group = groups.get(ran);
			groupId = group.getTalkGroupId();
		}
		// 選択されたグループに紐づくイベント取得
		ArrayList<TalkEvent> talkEvents = TalkEventsTableManager.getInstance(helper).GetRecords(groupId);

		// イベント情報をセット
		talkTexts = getTalkBeans(talkEvents);

		// 取得されたグループから選択肢情報を取得
		if (group.getSelectFlg() == 1) {
			ArrayList<TalkSelect> selects = TalkSelectsTableManager.getInstance(helper).GetRecords(groupId);
			// 一つしか取れないはず
			TalkSelect select = selects.get(0);

			answerCount = select.getAnswerCount();

			// 選択肢は最低二つ
			str1 = select.getFirstTalkBody();
			beans = new TalkBeans(str1, "", 0, 0, 0);
			answerTexts.add(beans);
			firstGroupId = select.getFirstTalkGroupId();
			ArrayList<TalkEvent> firstSelectEve = TalkEventsTableManager.getInstance(helper).GetRecords(firstGroupId);
			answerTextsFirst = getTalkBeans(firstSelectEve);

			str1 = select.getSecondTalkBody();
			beans = new TalkBeans(str1, "", 0, 0, 0);
			answerTexts.add(beans);
			secondGroupId = select.getSecondTalkGroupId();
			ArrayList<TalkEvent> secondSelectEve = TalkEventsTableManager.getInstance(helper).GetRecords(secondGroupId);
			answerTextsSecond = getTalkBeans(secondSelectEve);

			if (select.getAnswerCount() > 2) {
				str1 = select.getThirdTalkBody();
				beans = new TalkBeans(str1, "", 0, 0, 0);
				answerTexts.add(beans);
				thirdGroupId = select.getThirdTalkGroupId();
				ArrayList<TalkEvent> thirdSelectEve = TalkEventsTableManager.getInstance(helper).GetRecords(
						thirdGroupId);
				answerTextsThird = getTalkBeans(thirdSelectEve);
			}
			if (select.getAnswerCount() > 3) {
				str1 = select.getForthTalkBody();
				beans = new TalkBeans(str1, "", 0, 0, 0);
				answerTexts.add(beans);
				forthGroupId = select.getForthTalkGroupId();
				ArrayList<TalkEvent> forthSelectEve = TalkEventsTableManager.getInstance(helper).GetRecords(
						forthGroupId);
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
			talkName = talkEvents.get(i).getTalkName();
			imageId = getResources().getIdentifier(talkEvents.get(i).getImageFileName(), "drawable", getPackageName());
			animationType = talkEvents.get(i).getImageAnimationType();
			pos = talkEvents.get(i).getImagePositionType();
			beans = new TalkBeans(str1, talkName, imageId, animationType, pos);
			texts.add(beans);
		}

		return texts;
	}
	
	/**
	 * LocationMnager取得
	 * @param context
	 * @return
	 */
	private LocationManager getLocationManager(Context context) {
		
		return (LocationManager)context
				.getSystemService(Context.LOCATION_SERVICE);
		
	}

	 /**
	  * 結果通知リスナー
	  */
	public interface OnGetLocationListener {
		
		/**
		 * 緯度、経度取得完了通知
		 * @param latitude     緯度
		 * @param longitude    経度
		 */
		public void onGetLocation(double latitude, double longitude);
	}
	
	/**
	 * 位置情報の取得
	 * @param context
	 * @param listener
	 */
	public void startLocationUpdate(
    		Context context, OnGetLocationListener listener) {
		
		mListener = listener;
		
		// LocationManager取得
		LocationManager mgr = getLocationManager(context);
		if(mgr != null) {
			// 位置情報取得
			mgr.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,  // Provider（GPS）
					//LocationManager.NETWORK_PROVIDER, // Provider（無線ネットワーク）
					INTERVAL_TIME,                    // 通知時間
					INTERVAL_DISTANCE,                // 最小距離
					this);
		}
	}
	
	
	/**
	 * 位置情報取得終了
	 * @param context
	 */
	public void finishGettingLocation(Context context) {
		
		LocationManager mgr = getLocationManager(context);
		if(mgr != null) {
			// 位置情報取得終了
			mgr.removeUpdates(this);
		}
		mListener = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
}
