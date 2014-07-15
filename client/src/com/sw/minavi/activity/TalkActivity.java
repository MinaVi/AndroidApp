package com.sw.minavi.activity;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sw.minavi.R;

public class TalkActivity extends Activity implements OnClickListener {

	private TextView nameTextView;
	private TextView talkTextView;

	private LinearLayout answersArea;
	private TextView answerTextFirstView;
	private TextView answerTextSecondView;
	private TextView answerTextThirdView;
	private TextView answerTextForthView;

	private ArrayList<String> talkTexts = new ArrayList<String>();
	private int textCount = 0;

	// 選択中フラグ
	private boolean selectingFlg = false;
	// 選択肢数（分岐フラグを兼ねる）
	private int answerCount = 0;
	// 選択肢テキスト
	private ArrayList<String> answerTexts = new ArrayList<String>();
	// 選択肢の数だけ分岐
	private ArrayList<String> answerTextsFirst = new ArrayList<String>();
	private ArrayList<String> answerTextsSecond = new ArrayList<String>();
	private ArrayList<String> answerTextsThird = new ArrayList<String>();
	private ArrayList<String> answerTextsForth = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// test
		setTestTexts();

		setContentView(R.layout.activity_talk);
		nameTextView = (TextView) findViewById(R.id.nameText);
		talkTextView = (TextView) findViewById(R.id.talkText);
		answerTextFirstView = (TextView) findViewById(R.id.answerTextFirstView);
		answerTextSecondView = (TextView) findViewById(R.id.answerTextSecondView);
		answerTextThirdView = (TextView) findViewById(R.id.answerTextThirdView);
		answerTextForthView = (TextView) findViewById(R.id.answerTextForthView);

		answersArea = (LinearLayout) findViewById(R.id.answer_area);
		answersArea.setVisibility(View.GONE);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.back_area) {
			finish();
		} else if (selectingFlg == true) {

			String showMessage = String.format(
					"%s",
					new Object[] { "hoge" });
			Toast.makeText(TalkActivity.this, showMessage,
					Toast.LENGTH_SHORT).show();

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
			answerTextFirstView.setText(answerTexts.get(0));
			answerTextFirstView.setBackgroundColor(Color.WHITE);
			answerTextSecondView.setText(answerTexts.get(1));
			answerTextSecondView.setBackgroundColor(Color.WHITE);
			answerTextThirdView.setText("");
			answerTextForthView.setText("");

			if (answerCount > 2) {
				answerTextThirdView.setText(answerTexts.get(2));
				answerTextThirdView.setBackgroundColor(Color.WHITE);
			}
			if (answerCount > 3) {
				answerTextForthView.setText(answerTexts.get(3));
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
			talkTextView.setText(talkTexts.get(textCount));
			textCount = textCount + 1;
		}
	}

	// テストテキストセット
	private void setTestTexts() {
		talkTexts = new ArrayList<String>();
		answerTextsFirst = new ArrayList<String>();
		answerTextsSecond = new ArrayList<String>();
		answerTextsThird = new ArrayList<String>();
		answerTextsForth = new ArrayList<String>();
		answerTexts = new ArrayList<String>();

		// とりあえずランダム
		Random rnd = new Random();
		int ran = rnd.nextInt(3) + 1;
		int caseNum = ran % 3;
		if (caseNum == 1) {
			talkTexts.add("こんにちは");
			talkTexts.add("始めまして\nミナと言います。");
			talkTexts.add("これから北海道を、\n色々案内しますね。");
			talkTexts.add("よろしくお願いいたします。");
		} else if (caseNum == 2) {
			// 分岐フラグ
			answerCount = 2;

			// YesNo選択
			talkTexts.add("今日はいい天気ですね～");
			talkTexts.add("こう天気がいいと\nぶらっと遠出したくなりますよね～");

			// 1の場合
			answerTexts.add("Yes");
			answerTextsFirst.add("やっぱりそうですよね～");
			answerTextsFirst.add("こんな日は美瑛とか富良野とか\n行きたいですよね～");
			// 2の場合
			answerTexts.add("No");
			answerTextsSecond.add("あれ、\nもしかしてインドア派ですか？");
			answerTextsSecond.add("それとも…実は晴れてると\n思ってるの私だけですか？");

		} else if (caseNum == 0) {
			answerCount = 4;
			talkTexts.add("ところで質問ですが、");
			talkTexts.add("北海道と言えば\n何を連想しますか？");

			// 1の場合
			answerTexts.add("大自然");
			answerTextsFirst.add("やはり北海道と言えば\n大自然ですよね！");
			answerTextsFirst.add("草原や森林はもとより、\n広大な湿原、");
			answerTextsFirst.add("大雪山に代表される山々、");
			answerTextsFirst.add("世界遺産にもなった知床半島、");
			answerTextsFirst.add("自然の神秘、\n青い池などはとても魅力的ですよね。");

			// 2の場合
			answerTexts.add("おいしい食べ物");
			answerTextsSecond.add("本当においしいんですよね！\nかにとかうにとか、");
			answerTextsSecond.add("海鮮がやはり有名ですが,\nジンギスカンや鹿肉、");
			answerTextsSecond.add("大自然でとれた農作物も\nとてもおいしいです！");
			answerTextsSecond.add("とてもじゃないですけど\nずっと住んでないと食べきれないほどです！");
			// 3の場合
			answerTexts.add("なまら");
			answerTextsThird.add("これは意外なお答えですね？");
			answerTextsThird.add("北海道弁で有名なのは\n「なまら」や「～しょ」ですが、");
			answerTextsThird.add("その他にもいろいろありますので、\nぜひ耳を傾けて探してみてください");
			answerTextsThird.add("あ、北海道かるた等はお土産やさんにもあるので\n一見の価値ありです！");

			// 4の場合
			answerTexts.add("なんくるないさー");
			answerTextsForth.add("えー…っと\nそれは沖縄の方言ですよ？");
			answerTextsForth.add("北海道風にいうと…");
			answerTextsForth.add("『なんとかなるっしょ！』");
			answerTextsForth.add("ですかね～。");

		}
	}

}
