package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.activity.db.DatabaseOpenHelper.TalkSelectsTable;
import com.sw.minavi.item.TalkSelect;

/**
 * local_item_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * @author RIOH
 *
 */
public class TalkSelectsTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static TalkSelectsTableManager me = null;

	/**
	 * プライベートコンストラクタ
	 */
	private TalkSelectsTableManager() {
	}

	/**
	 * コンストラクタ
	 * @param helper SQLiteOpenHelper継承クラス
	 */
	private TalkSelectsTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * @param helper SQLiteOpenHelper継承クラス
	 * @return 当該クラス唯一のインスタンス
	 */
	public static TalkSelectsTableManager getInstance(DatabaseOpenHelper helper) {
		if (me == null) {
			me = new TalkSelectsTableManager(helper);
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

			sqliteDB.delete(Tables.talkSelectsTable.getName(), null, null);
			for (String[] data : datas) {
				// insertするデータを作成する
				// talk_group_id項目については自動採番なので用意する必要はない
				ContentValues values = new ContentValues();
				values.put(TalkSelectsTable.talk_select_id.getName(), data[TalkSelectsTable.talk_select_id.getColNo()]);
				values.put(TalkSelectsTable.talk_group_id.getName(), data[TalkSelectsTable.talk_group_id.getColNo()]);
				values.put(TalkSelectsTable.answers_count.getName(), data[TalkSelectsTable.answers_count.getColNo()]);
				values.put(TalkSelectsTable.first_answer_body.getName(), data[TalkSelectsTable.first_answer_body.getColNo()]);
				values.put(TalkSelectsTable.first_talk_group_id.getName(), data[TalkSelectsTable.first_talk_group_id.getColNo()]);
				values.put(TalkSelectsTable.second_answer_body.getName(), data[TalkSelectsTable.second_answer_body.getColNo()]);
				values.put(TalkSelectsTable.second_talk_group_id.getName(), data[TalkSelectsTable.second_talk_group_id.getColNo()]);
				values.put(TalkSelectsTable.third_answer_body.getName(), data[TalkSelectsTable.third_answer_body.getColNo()]);
				values.put(TalkSelectsTable.third_talk_group_id.getName(), data[TalkSelectsTable.third_talk_group_id.getColNo()]);
				values.put(TalkSelectsTable.forth_answer_body.getName(), data[TalkSelectsTable.forth_answer_body.getColNo()]);
				values.put(TalkSelectsTable.forth_talk_group_id.getName(), data[TalkSelectsTable.forth_talk_group_id.getColNo()]);
				// insert
				sqliteDB.insert(Tables.talkSelectsTable.getName(), null, values);
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
	public ArrayList<TalkSelect> GetRecords(int groupId) {
		ArrayList<TalkSelect> values = new ArrayList<TalkSelect>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("SELECT * FROM talk_selects_tbl WHERE talk_group_id = ?", new String[]{String.valueOf(groupId)});

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// TalkSelectsTableの生成・編集
				TalkSelect val = new TalkSelect();
				val.setTalkSelectId(cursor.getInt(TalkSelectsTable.talk_select_id.getColNo()));
				val.setTalkGroupId(cursor.getInt(TalkSelectsTable.talk_group_id.getColNo()));
				val.setAnswersCount(cursor.getInt(TalkSelectsTable.answers_count.getColNo()));
				val.setFirstTalkBody(cursor.getString(TalkSelectsTable.first_answer_body.getColNo()));
				val.setFirstTalkGroupId(cursor.getInt(TalkSelectsTable.first_talk_group_id.getColNo()));
				val.setSecondTalkBody(cursor.getString(TalkSelectsTable.second_answer_body.getColNo()));
				val.setSecondTalkGroupId(cursor.getInt(TalkSelectsTable.second_talk_group_id.getColNo()));	
				val.setThirdTalkBody(cursor.getString(TalkSelectsTable.third_answer_body.getColNo()));
				val.setThirdTalkGroupId(cursor.getInt(TalkSelectsTable.third_talk_group_id.getColNo()));	
				val.setForthTalkBody(cursor.getString(TalkSelectsTable.forth_answer_body.getColNo()));
				val.setForthTalkGroupId(cursor.getInt(TalkSelectsTable.forth_talk_group_id.getColNo()));		

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
