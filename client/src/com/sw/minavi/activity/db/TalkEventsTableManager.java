package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.activity.db.DatabaseOpenHelper.TalkEventsTable;
import com.sw.minavi.item.TalkEvent;

/**
 * local_item_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * @author RIOH
 *
 */
public class TalkEventsTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static TalkEventsTableManager me = null;

	/**
	 * プライベートコンストラクタ
	 */
	private TalkEventsTableManager() {
	}

	/**
	 * コンストラクタ
	 * @param helper SQLiteOpenHelper継承クラス
	 */
	private TalkEventsTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * @param helper SQLiteOpenHelper継承クラス
	 * @return 当該クラス唯一のインスタンス
	 */
	public static TalkEventsTableManager getInstance(DatabaseOpenHelper helper) {
		if (me == null) {
			me = new TalkEventsTableManager(helper);
		}
		return me;
	}

	/**
	 * サンプルデータの登録
	 */
	public void InsertSample() {

		// サンプルデータの準備
		String[][] datas = new String[][] {
				{ "1", "1", "ミナ","こんにちは！" , "nomal_n", "0" , "0"},
				{ "2", "1", "ミナ", "始めまして\nミナと言います。" , "nomal_n", "0" , "0"},
				{ "3", "1", "ミナ", "これから北海道を、\n色々案内しますね。" , "smile_n", "0" , "0"},
				{ "4", "1", "ミナ", "よろしくお願いいたします。" , "smile_n", "0" , "0"},
				{ "5", "2", "ミナ", "ミナです！" , "nomal_n", "0" , "0"},
				{ "6", "2", "ナミ", "ナミです！" , "t_smile_n", "1" , "0"},
				{ "7", "2", "ミナ", "二人合わせて！" , "smile_n", "0" , "0"},
				{ "8", "2", "ナミ", "…え、なにそれ？" , "t_nomal_n", "1" , "0"},
				{ "9", "2", "ミナ", "あれ？ナミちゃんノリ悪いよ～" , "bewilder_n", "0" , "0"},
				{ "10", "2", "ナミ", "無茶ぶりすぎ！" , "t_embarrass_n", "1" , "0"},
				{ "11", "3", "ミナ", "今日はいい天気ですね～" , "nomal_n", "0" , "0"},
				{ "12", "3", "ミナ", "こう天気がいいと\nぶらっと遠出したくなりますよね～" , "smile_n", "0" , "0"},
				{ "13", "4", "ミナ", "やっぱりそうですよね～" , "smile_n", "" , "0"},
				{ "14", "4", "ミナ", "こんな日は美瑛とか富良野とか\n行きたいですよね～" , "smile_n", "0" , "0"},
				{ "15", "5", "ミナ", "あれ、\nもしかしてインドア派ですか？" , "nomal_n", "0" , "0"},
				{ "16", "5", "ミナ", "それとも…実は晴れてると\n思ってるの私だけですか？" , "nomal_n", "0" , "0"},
				{ "17", "6", "ミナ", "ところで質問ですが、" , "nomal_n", "" , "0"},
				{ "18", "6", "ミナ", "北海道と言えば\n何を連想しますか？" , "nomal_n", "" , "0"},
				{ "19", "7", "ミナ", "やはり北海道と言えば\n大自然ですよね！" , "smile_n", "" , "0"},
				{ "20", "7", "ミナ", "草原や森林はもとより、\n広大な湿原、" , "nomal_n", "" , "0"},
				{ "21", "7", "ミナ", "大雪山に代表される山々、" , "nomal_n", "" , "0"},
				{ "22", "7", "ミナ", "世界遺産にもなった知床半島、" , "nomal_n", "" , "0"},
				{ "23", "7", "ミナ", "自然の神秘、\n青い池などはとても魅力的ですよね。" , "nomal_n", "" , "0"},
				{ "24", "8", "ミナ", "本当においしいんですよね！\nかにとかうにとか、" , "smile_n", "" , "0"},
				{ "25", "8", "ミナ", "海鮮がやはり有名ですが,\nジンギスカンや鹿肉、" , "nomal_n", "" , "0"},
				{ "26", "8", "ミナ", "大自然でとれた農作物も\nとてもおいしいです！" , "smile_n", "" , "0"},
				{ "27", "8", "ミナ", "とてもじゃないですけど\nずっと住んでないと食べきれないほどです！" , "nomal_n", "" , "0"},
				{ "28", "9", "ミナ", "これは意外なお答えですね？" , "nomal_n", "" , "0"},
				{ "29", "9", "ミナ", "北海道弁で有名なのは\n「なまら」や「～しょ」ですが、" , "nomal_n", "" , "0"},
				{ "30", "9", "ミナ", "その他にもいろいろありますので、\nぜひ耳を傾けて探してみてください" , "nomal_n", "" , "0"},
				{ "31", "9", "ミナ", "あ、北海道かるた等はお土産やさんにもあるので\n一見の価値ありです！" , "nomal_n", "" , "0"},
				{ "32", "10", "ミナ", "えー…っと\nそれは沖縄の方言ですよ。" , "bewilder_n", "" , "0"},
				{ "33", "10", "ミナ", "北海道風にいうと…" , "nomal_n", "" , "0"},
				{ "34", "10", "ミナ", "『なんとかなるっしょ！』" , "nomal_n", "" , "0"},
				{ "35", "10", "ミナ", "ですかね～。" , "nomal_n", "" , "0"},
		};

		// 書き込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getWritableDatabase();

		try {

			// トランザクションの開始
			sqliteDB.beginTransaction();

			sqliteDB.delete(Tables.talkEventsTable.getName(), null, null);
			for (String[] data : datas) {
				// insertするデータを作成する
				// talk_group_id項目については自動採番なので用意する必要はない
				ContentValues values = new ContentValues();
				values.put(TalkEventsTable.talk_event_id.getName(), data[TalkEventsTable.talk_event_id.getColNo()]);
				values.put(TalkEventsTable.talk_group_id.getName(), data[TalkEventsTable.talk_group_id.getColNo()]);
				values.put(TalkEventsTable.talk_name.getName(), data[TalkEventsTable.talk_name.getColNo()]);
				values.put(TalkEventsTable.talk_body.getName(), data[TalkEventsTable.talk_body.getColNo()]);
				values.put(TalkEventsTable.image_file_name.getName(), data[TalkEventsTable.image_file_name.getColNo()]);
				values.put(TalkEventsTable.image_position_type.getName(), data[TalkEventsTable.image_position_type.getColNo()]);
				values.put(TalkEventsTable.image_animation_type.getName(), data[TalkEventsTable.image_animation_type.getColNo()]);

				// insert
				sqliteDB.insert(Tables.talkEventsTable.getName(), null, values);
			}

			// コミット
			sqliteDB.setTransactionSuccessful();

		} finally {
			// トランザクションの終了
			sqliteDB.endTransaction();
		}
	}

	/**
	 * 以下の条件に該当するlocal_item_tblのレコードをList形式で取得する<br/>
	 * ・抽出項目：指定なし<br/>
	 * ・抽出条件：指定なし<br/>
	 * @return
	 */
	public ArrayList<TalkEvent> GetRecords(int groupId) {
		ArrayList<TalkEvent> values = new ArrayList<TalkEvent>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("SELECT * FROM talk_events_tbl WHERE talk_group_id = ?", new String[]{String.valueOf(groupId)});

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// TalkEventsTableの生成・編集
				TalkEvent val = new TalkEvent();
				val.setTalkEventId(cursor.getInt(TalkEventsTable.talk_event_id.getColNo()));
				val.setTalkGroupId(cursor.getInt(TalkEventsTable.talk_group_id.getColNo()));
				val.setTalkName(cursor.getString(TalkEventsTable.talk_name.getColNo()));
				val.setTalkBody(cursor.getString(TalkEventsTable.talk_body.getColNo()));
				val.setImageFileName(cursor.getString(TalkEventsTable.image_file_name.getColNo()));
				val.setImagePositionType(cursor.getInt(TalkEventsTable.image_position_type.getColNo()));
				val.setImageAnimationType(cursor.getInt(TalkEventsTable.image_animation_type.getColNo()));
				

				// 戻り値のリストに追加
				values.add(val);
			}
		} finally {
			// DBクローズ
			sqliteDB.close();
		}
		return values;
	}
}
