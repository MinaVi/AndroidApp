package com.sw.minavi.activity;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sw.minavi.R;
import com.sw.minavi.activity.beans.TalkBeans;
import com.sw.minavi.activity.db.DatabaseOpenHelper;
import com.sw.minavi.activity.db.StoryEventsTableManager;
import com.sw.minavi.activity.db.StoryGroupsTableManager;
import com.sw.minavi.activity.db.StorySelectsTableManager;
import com.sw.minavi.item.StoryEvent;
import com.sw.minavi.item.StoryGroup;
import com.sw.minavi.item.StorySelect;

public class StoryActivity extends Activity implements OnClickListener {

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
	private int arStoryGroupId = 0;

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

		backImage = (ImageView) findViewById(R.id.back_image);
		charaImageLeft = (ImageView) findViewById(R.id.chara_image_left);
		charaImageRight = (ImageView) findViewById(R.id.chara_image_right);

		//charaImageLeft.setVisibility(View.GONE);
		charaImageRight.setVisibility(View.GONE);

		pinId = getIntent().getExtras().getInt("pinId");
		arStoryGroupId = getIntent().getExtras().getInt("talkGroupId");

		// test
		//setTestTexts();

		// Sampleの登録
		helper = new DatabaseOpenHelper(this);
		StoryGroupsTableManager.getInstance(helper).InsertSample();
		StoryEventsTableManager.getInstance(helper).InsertSample();
		StorySelectsTableManager.getInstance(helper).InsertSample();

		// イベントセット
		ArrayList<StoryGroup> talkGroup = StoryGroupsTableManager.getInstance(helper).GetRecordsByGroupId(arStoryGroupId);
		setTexts(talkGroup);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.back_area) {
			finish();
		} else if (selectingFlg == true) {
			// 一旦トーク内容リセット
			talkTexts = null;

			answerCount = 0;
			textCount = 0;

			if (v.getId() == R.id.answerTextFirstView) {
				talkTexts = answerTextsFirst;
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				viewText();
			} else if (v.getId() == R.id.answerTextSecondView) {
				talkTexts = answerTextsSecond;
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				viewText();
			} else if (v.getId() == R.id.answerTextThirdView) {
				talkTexts = answerTextsThird;
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
				viewText();
			} else if (v.getId() == R.id.answerTextForthView) {
				talkTexts = answerTextsForth;
				selectingFlg = false;
				answersArea.setVisibility(View.GONE);
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

			answersArea.setVisibility(View.VISIBLE);

		} else if (textCount == talkTexts.size()) {
			textCount = 0;
			nameTextView.setText("naviko");
			talkTextView.setText("end");
			if (talkTexts.get(textCount).getPosition() == 0) {
				charaImageLeft.setImageResource(talkTexts.get(textCount).getImageId());
			} else {
				charaImageRight.setVisibility(View.VISIBLE);
				charaImageRight.setImageResource(talkTexts.get(textCount).getImageId());
			}

			// ストーリーモードはここで終了
			finish();

		} else {
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

	private void setTexts(ArrayList<StoryGroup> groups) {
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
		StoryGroup group = null;
		if (arStoryGroupId != 0) {
			groupId = arStoryGroupId;

			// 取得されたグループから対象のグループを選択
			for (int i = 0; i < groups.size(); i++) {
				if(groups.get(i).getStoryGroupId() == arStoryGroupId){

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
			arStoryGroupId = 0;
		} else {
			// 取得されたグループからランダムで表示
			Random rnd = new Random();
			int ran = rnd.nextInt(groups.size());
			group = groups.get(ran);
			groupId = group.getStoryGroupId();
		}
		// 選択されたグループに紐づくイベント取得
		ArrayList<StoryEvent> talkEvents = StoryEventsTableManager.getInstance(helper).GetRecords(groupId);

		// イベント情報をセット
		talkTexts = getTalkBeans(talkEvents);

		// 取得されたグループから選択肢情報を取得
		if (group.getSelectFlg() == 1) {
			ArrayList<StorySelect> selects = StorySelectsTableManager.getInstance(helper).GetRecords(groupId);
			// 一つしか取れないはず
			StorySelect select = selects.get(0);

			answerCount = select.getAnswerCount();

			// 選択肢は最低二つ
			str1 = select.getFirstTalkBody();
			beans = new TalkBeans(str1, "", 0, 0, 0);
			answerTexts.add(beans);
			firstGroupId = select.getFirstStoryGroupId();
			ArrayList<StoryEvent> firstSelectEve = StoryEventsTableManager.getInstance(helper).GetRecords(firstGroupId);
			answerTextsFirst = getTalkBeans(firstSelectEve);

			str1 = select.getSecondTalkBody();
			beans = new TalkBeans(str1, "", 0, 0, 0);
			answerTexts.add(beans);
			secondGroupId = select.getSecondStoryGroupId();
			ArrayList<StoryEvent> secondSelectEve = StoryEventsTableManager.getInstance(helper).GetRecords(secondGroupId);
			answerTextsSecond = getTalkBeans(secondSelectEve);

			if (select.getAnswerCount() > 2) {
				str1 = select.getThirdTalkBody();
				beans = new TalkBeans(str1, "", 0, 0, 0);
				answerTexts.add(beans);
				thirdGroupId = select.getThirdStoryGroupId();
				ArrayList<StoryEvent> thirdSelectEve = StoryEventsTableManager.getInstance(helper).GetRecords(
						thirdGroupId);
				answerTextsThird = getTalkBeans(thirdSelectEve);
			}
			if (select.getAnswerCount() > 3) {
				str1 = select.getForthTalkBody();
				beans = new TalkBeans(str1, "", 0, 0, 0);
				answerTexts.add(beans);
				forthGroupId = select.getForthStoryGroupId();
				ArrayList<StoryEvent> forthSelectEve = StoryEventsTableManager.getInstance(helper).GetRecords(
						forthGroupId);
				answerTextsForth = getTalkBeans(forthSelectEve);
			}

			// 分岐後イベントをそれぞれ取得

		}
	}

	private ArrayList<TalkBeans> getTalkBeans(ArrayList<StoryEvent> storyEvents) {

		ArrayList<TalkBeans> texts = new ArrayList<TalkBeans>();
		String str1 = "";
		String talkName = "";
		int imageId = 0;
		int animationType = 0;
		int pos = 0;
		TalkBeans beans = null;

		// イベント情報をセット
		for (int i = 0; i < storyEvents.size(); i++) {
			str1 = storyEvents.get(i).getTalkBody();
			talkName = storyEvents.get(i).getTalkName();
			imageId = getResources().getIdentifier(storyEvents.get(i).getImageFileName(), "drawable", getPackageName());
			animationType = storyEvents.get(i).getImageAnimationType();
			pos = storyEvents.get(i).getImagePositionType();
			beans = new TalkBeans(str1, talkName, imageId, animationType, pos);
			texts.add(beans);
		}

		return texts;
	}
}
