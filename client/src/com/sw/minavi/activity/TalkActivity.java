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
import com.sw.minavi.beans.TalkBeans;

public class TalkActivity extends Activity implements OnClickListener {

	private TextView nameTextView;
	private TextView talkTextView;

	private LinearLayout answersArea;
	private TextView answerTextFirstView;
	private TextView answerTextSecondView;
	private TextView answerTextThirdView;
	private TextView answerTextForthView;

	private ImageView backImage;
	private ImageView charaImageLeft;

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

		pinId = getIntent().getExtras().getInt("pinId");

		if (pinId > 0) {
			// 背景を特殊背景に変える
			backImage.setImageResource(getResources().getIdentifier("aoiike", "drawable", getPackageName()));
		}

		// test
		setTestTexts();

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
			
			// test
			setTestTexts();
		} else {
			nameTextView.setText("naviko");
			talkTextView.setText(talkTexts.get(textCount).getFirstTalkStr());
			charaImageLeft.setImageResource(talkTexts.get(textCount).getImageId());
			textCount = textCount + 1;
		}
	}

	// テストテキストセット
	private void setTestTexts() {
		talkTexts = new ArrayList<TalkBeans>();
		answerTextsFirst = new ArrayList<TalkBeans>();
		answerTextsSecond = new ArrayList<TalkBeans>();
		answerTextsThird = new ArrayList<TalkBeans>();
		answerTextsForth = new ArrayList<TalkBeans>();
		answerTexts = new ArrayList<TalkBeans>();
		String str1 = "";
		int id = 0;
		int type = 0;
		int pos = 0;
		TalkBeans beans = null;

		// test
		id = getResources().getIdentifier("nomal_n", "drawable", getPackageName());
		type = 0;
		pos = 0;

		// とりあえずランダム
		Random rnd = new Random();
		int ran = rnd.nextInt(3) + 1;
		int caseNum = ran % 3;

		if (pinId > 0) {
			// 分岐フラグ
			answerCount = 2;

			// YesNo選択
			str1 = "ここはもしかして、\nVersin2本社じゃないですか？";
			beans = new TalkBeans(str1, id, type, pos);

			talkTexts.add(beans);

			// 1の場合
			str1 = "yes";
			beans = new TalkBeans(str1, id, type, pos);
			answerTexts.add(beans);

			// YesNo選択
			str1 = "あー…";
			beans = new TalkBeans(str1, id, type, pos);
			id = getResources().getIdentifier("chicane_n", "drawable", getPackageName());
			answerTextsFirst.add(beans);

			str1 = "きっと今日も皆さん、\n苦労なさってるんですね；";
			beans = new TalkBeans(str1, id, type, pos);
			id = getResources().getIdentifier("embarrass_n", "drawable", getPackageName());
			answerTextsFirst.add(beans);
			// 2の場合
			str1 = "no";
			beans = new TalkBeans(str1, id, type, pos);
			answerTexts.add(beans);

			str1 = "あれ、\nおかしいですね…？";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsSecond.add(beans);

			str1 = "…みつびきちゃん\nさぼったな（ボソッ）";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsSecond.add(beans);

		} else if (caseNum == 1) {
			str1 = "こんにちは！";
			beans = new TalkBeans(str1, id, type, pos);
			talkTexts.add(beans);

			str1 = "始めまして\nミナと言います。";
			beans = new TalkBeans(str1, id, type, pos);
			talkTexts.add(beans);

			str1 = "これから北海道を、\n色々案内しますね。";
			id = getResources().getIdentifier("smile_n", "drawable", getPackageName());
			beans = new TalkBeans(str1, id, type, pos);
			talkTexts.add(beans);

			str1 = "よろしくお願いいたします。";
			id = getResources().getIdentifier("smile_n", "drawable", getPackageName());
			beans = new TalkBeans(str1, id, type, pos);
			talkTexts.add(beans);

		} else if (caseNum == 2) {
			// 分岐フラグ
			answerCount = 2;

			// YesNo選択
			str1 = "今日はいい天気ですね～";
			beans = new TalkBeans(str1, id, type, pos);
			talkTexts.add(beans);

			str1 = "こう天気がいいと\nぶらっと遠出したくなりますよね～";
			beans = new TalkBeans(str1, id, type, pos);
			talkTexts.add(beans);

			// 1の場合
			str1 = "yes";
			beans = new TalkBeans(str1, id, type, pos);
			answerTexts.add(beans);

			str1 = "やっぱりそうですよね～";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsFirst.add(beans);

			str1 = "こんな日は美瑛とか富良野とか\n行きたいですよね～";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsFirst.add(beans);

			// 2の場合
			str1 = "no";
			beans = new TalkBeans(str1, id, type, pos);
			answerTexts.add(beans);

			str1 = "あれ、\nもしかしてインドア派ですか？";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsSecond.add(beans);

			str1 = "それとも…実は晴れてると\n思ってるの私だけですか？";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsSecond.add(beans);

		} else if (caseNum == 0) {
			answerCount = 4;
			str1 = "ところで質問ですが、";
			beans = new TalkBeans(str1, id, type, pos);
			talkTexts.add(beans);
			str1 = "北海道と言えば\n何を連想しますか？";
			beans = new TalkBeans(str1, id, type, pos);
			talkTexts.add(beans);

			// 1の場合
			str1 = "大自然";
			beans = new TalkBeans(str1, id, type, pos);
			answerTexts.add(beans);


			str1 = "やはり北海道と言えば\n大自然ですよね！";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsFirst.add(beans);

			str1 = "草原や森林はもとより、\n広大な湿原、";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsFirst.add(beans);

			str1 = "大雪山に代表される山々、";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsFirst.add(beans);

			str1 = "世界遺産にもなった知床半島、";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsFirst.add(beans);

			str1 = "自然の神秘、\n青い池などはとても魅力的ですよね。";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsFirst.add(beans);

			// 2の場合
			str1 = "おいしい食べ物";
			beans = new TalkBeans(str1, id, type, pos);
			answerTexts.add(beans);

			str1 = "本当においしいんですよね！\nかにとかうにとか、";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsSecond.add(beans);
			str1 = "海鮮がやはり有名ですが,\nジンギスカンや鹿肉、";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsSecond.add(beans);
			str1 = "大自然でとれた農作物も\nとてもおいしいです！";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsSecond.add(beans);
			str1 = "とてもじゃないですけど\nずっと住んでないと食べきれないほどです！";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsSecond.add(beans);

			// 3の場合
			str1 = "なまら";
			beans = new TalkBeans(str1, id, type, pos);
			answerTexts.add(beans);

			str1 = "これは意外なお答えですね？";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsThird.add(beans);
			str1 = "北海道弁で有名なのは\n「なまら」や「～しょ」ですが、";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsThird.add(beans);
			str1 = "その他にもいろいろありますので、\nぜひ耳を傾けて探してみてください";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsThird.add(beans);
			str1 = "あ、北海道かるた等はお土産やさんにもあるので\n一見の価値ありです！";
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsThird.add(beans);

			// 4の場合
			str1 = "なんくるないさー";
			beans = new TalkBeans(str1, id, type, pos);
			answerTexts.add(beans);
			
			str1 = "えー…っと\nそれは沖縄の方言ですよ。";
			id = getResources().getIdentifier("bewilder_n", "drawable", getPackageName());
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsForth.add(beans);
			str1 = "北海道風にいうと…";
			id = getResources().getIdentifier("nomal_n", "drawable", getPackageName());
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsForth.add(beans);
			str1 = "『なんとかなるっしょ！』";
			id = getResources().getIdentifier("smile_n", "drawable", getPackageName());
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsForth.add(beans);
			str1 = "ですかね～。";
			id = getResources().getIdentifier("nomal_n", "drawable", getPackageName());
			beans = new TalkBeans(str1, id, type, pos);
			answerTextsForth.add(beans);
		}
	}

}
