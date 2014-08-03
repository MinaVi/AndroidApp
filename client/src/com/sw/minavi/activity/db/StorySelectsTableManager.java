package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sw.minavi.activity.db.DatabaseOpenHelper.StorySelectsTable;
import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.item.StorySelect;

/**
 * local_item_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * @author RIOH
 *
 */
public class StorySelectsTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static StorySelectsTableManager me = null;

	/**
	 * プライベートコンストラクタ
	 */
	private StorySelectsTableManager() {
	}

	/**
	 * コンストラクタ
	 * @param helper SQLiteOpenHelper継承クラス
	 */
	private StorySelectsTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * @param helper SQLiteOpenHelper継承クラス
	 * @return 当該クラス唯一のインスタンス
	 */
	public static StorySelectsTableManager getInstance(DatabaseOpenHelper helper) {
		if (me == null) {
			me = new StorySelectsTableManager(helper);
		}
		return me;
	}

	/**
	 * サンプルデータの登録
	 */
	public void InsertSample() {

		// サンプルデータの準備
		String[][] datas = new String[][] {
				{ "1", "3", "2","YES" , "4", "NO" , "5", "" , "0", "" , "0"},
				{ "2", "6", "4","大自然" , "7", "おいしい食べ物" , "8", "なまら" , "9", "なんくるないさー" , "10"},
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
				values.put(StorySelectsTable.story_select_id.getName(), data[StorySelectsTable.story_select_id.getColNo()]);
				values.put(StorySelectsTable.story_group_id.getName(), data[StorySelectsTable.story_group_id.getColNo()]);
				values.put(StorySelectsTable.answers_count.getName(), data[StorySelectsTable.answers_count.getColNo()]);
				values.put(StorySelectsTable.first_answer_body.getName(), data[StorySelectsTable.first_answer_body.getColNo()]);
				values.put(StorySelectsTable.first_story_group_id.getName(), data[StorySelectsTable.first_story_group_id.getColNo()]);
				values.put(StorySelectsTable.second_answer_body.getName(), data[StorySelectsTable.second_answer_body.getColNo()]);
				values.put(StorySelectsTable.second_story_group_id.getName(), data[StorySelectsTable.second_story_group_id.getColNo()]);
				values.put(StorySelectsTable.third_answer_body.getName(), data[StorySelectsTable.third_answer_body.getColNo()]);
				values.put(StorySelectsTable.third_story_group_id.getName(), data[StorySelectsTable.third_story_group_id.getColNo()]);
				values.put(StorySelectsTable.forth_answer_body.getName(), data[StorySelectsTable.forth_answer_body.getColNo()]);
				values.put(StorySelectsTable.forth_story_group_id.getName(), data[StorySelectsTable.forth_story_group_id.getColNo()]);
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
	public ArrayList<StorySelect> GetRecords(int groupId) {
		ArrayList<StorySelect> values = new ArrayList<StorySelect>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("SELECT * FROM story_selects_tbl WHERE story_group_id = ?", new String[]{String.valueOf(groupId)});

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// TalkSelectsTableの生成・編集
				StorySelect val = new StorySelect();
				val.setStorySelectId(cursor.getInt(StorySelectsTable.story_select_id.getColNo()));
				val.setStoryGroupId(cursor.getInt(StorySelectsTable.story_group_id.getColNo()));
				val.setAnswersCount(cursor.getInt(StorySelectsTable.answers_count.getColNo()));
				val.setFirstTalkBody(cursor.getString(StorySelectsTable.first_answer_body.getColNo()));
				val.setFirstStoryGroupId(cursor.getInt(StorySelectsTable.first_story_group_id.getColNo()));
				val.setSecondTalkBody(cursor.getString(StorySelectsTable.second_answer_body.getColNo()));
				val.setSecondStoryGroupId(cursor.getInt(StorySelectsTable.second_story_group_id.getColNo()));	
				val.setThirdTalkBody(cursor.getString(StorySelectsTable.third_answer_body.getColNo()));
				val.setThirdStoryGroupId(cursor.getInt(StorySelectsTable.third_story_group_id.getColNo()));	
				val.setForthTalkBody(cursor.getString(StorySelectsTable.forth_answer_body.getColNo()));
				val.setForthStoryGroupId(cursor.getInt(StorySelectsTable.forth_story_group_id.getColNo()));		

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
