package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sw.minavi.activity.db.DatabaseOpenHelper.StoryEventsTable;
import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.item.StoryEvent;

/**
 * local_item_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * @author RIOH
 *
 */
public class StoryEventsTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static StoryEventsTableManager me = null;

	/**
	 * プライベートコンストラクタ
	 */
	private StoryEventsTableManager() {
	}

	/**
	 * コンストラクタ
	 * @param helper SQLiteOpenHelper継承クラス
	 */
	private StoryEventsTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * @param helper SQLiteOpenHelper継承クラス
	 * @return 当該クラス唯一のインスタンス
	 */
	public static StoryEventsTableManager getInstance(DatabaseOpenHelper helper) {
		if (me == null) {
			me = new StoryEventsTableManager(helper);
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
				
		};

		// 書き込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getWritableDatabase();

		try {

			// トランザクションの開始
			sqliteDB.beginTransaction();

			sqliteDB.delete(Tables.storyEventsTable.getName(), null, null);
			for (String[] data : datas) {
				// insertするデータを作成する
				// talk_group_id項目については自動採番なので用意する必要はない
				ContentValues values = new ContentValues();
				values.put(StoryEventsTable.story_event_id.getName(), data[StoryEventsTable.story_event_id.getColNo()]);
				values.put(StoryEventsTable.story_group_id.getName(), data[StoryEventsTable.story_group_id.getColNo()]);
				values.put(StoryEventsTable.talk_name.getName(), data[StoryEventsTable.talk_name.getColNo()]);
				values.put(StoryEventsTable.talk_body.getName(), data[StoryEventsTable.talk_body.getColNo()]);
				values.put(StoryEventsTable.image_file_name.getName(), data[StoryEventsTable.image_file_name.getColNo()]);
				values.put(StoryEventsTable.image_position_type.getName(), data[StoryEventsTable.image_position_type.getColNo()]);
				values.put(StoryEventsTable.image_animation_type.getName(), data[StoryEventsTable.image_animation_type.getColNo()]);

				// insert
				sqliteDB.insert(Tables.storyEventsTable.getName(), null, values);
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
	public ArrayList<StoryEvent> GetRecords(int groupId) {
		ArrayList<StoryEvent> values = new ArrayList<StoryEvent>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("SELECT * FROM story_events_tbl WHERE story_group_id = ?", new String[]{String.valueOf(groupId)});

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// StoryEventsTableの生成・編集
				StoryEvent val = new StoryEvent();
				val.setStoryEventId(cursor.getInt(StoryEventsTable.story_event_id.getColNo()));
				val.setStoryGroupId(cursor.getInt(StoryEventsTable.story_group_id.getColNo()));
				val.setTalkName(cursor.getString(StoryEventsTable.talk_name.getColNo()));
				val.setTalkBody(cursor.getString(StoryEventsTable.talk_body.getColNo()));
				val.setImageFileName(cursor.getString(StoryEventsTable.image_file_name.getColNo()));
				val.setImagePositionType(cursor.getInt(StoryEventsTable.image_position_type.getColNo()));
				val.setImageAnimationType(cursor.getInt(StoryEventsTable.image_animation_type.getColNo()));
				

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
