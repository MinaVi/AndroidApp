package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.activity.db.DatabaseOpenHelper.TalkGroupsTable;
import com.sw.minavi.item.TalkGroup;

/**
 * local_item_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * @author RIOH
 *
 */
public class TalkGroupsTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static TalkGroupsTableManager me = null;

	/**
	 * プライベートコンストラクタ
	 */
	private TalkGroupsTableManager() {
	}

	/**
	 * コンストラクタ
	 * @param helper SQLiteOpenHelper継承クラス
	 */
	private TalkGroupsTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * @param helper SQLiteOpenHelper継承クラス
	 * @return 当該クラス唯一のインスタンス
	 */
	public static TalkGroupsTableManager getInstance(DatabaseOpenHelper helper) {
		if (me == null) {
			me = new TalkGroupsTableManager(helper);
		}
		return me;
	}

	/**
	 * サンプルデータの登録
	 */
	public void InsertSample() {

		// サンプルデータの準備
		String[][] datas = new String[][] {
				{ "1", "0", "0", "0", "0", "0", "0", "1", "0"},
				{ "2", "0", "0", "0", "0", "0", "0", "1", "0" },
				{ "3", "0", "0", "0", "1", "0", "0", "1", "0" },
				{ "4", "0", "0", "0", "1", "0", "0", "0", "0" },
				{ "5", "0", "0", "0", "1", "0", "0", "0", "0" },
				{ "6", "0", "0", "0", "1", "0", "0", "1", "0" },
				{ "7", "0", "0", "0", "0", "0", "0", "0", "0"},
				{ "8", "0", "0", "0", "0", "0", "0", "0", "0" },
				{ "9", "0", "0", "0", "1", "0", "0", "0", "0" },
				{ "10", "0", "0", "0", "1", "0", "0", "0", "0" },

				// 地域システム周辺
				{ "11", "1", "0", "0", "0", "0", "0", "0", "0" },
				{ "12", "1", "0", "0", "0", "0", "0", "0", "0" },
				
				{ "13", "0", "0", "0", "1", "0", "0", "1", "0" },

		};

		// 書き込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getWritableDatabase();

		try {

			// トランザクションの開始
			sqliteDB.beginTransaction();

			sqliteDB.delete(Tables.talkGroupsTable.getName(), null, null);
			for (String[] data : datas) {
				// insertするデータを作成する
				// talk_group_id項目については自動採番なので用意する必要はない
				ContentValues values = new ContentValues();
				values.put(TalkGroupsTable.talk_group_id.getName(), data[TalkGroupsTable.talk_group_id.getColNo()]);
				values.put(TalkGroupsTable.area_id.getName(), data[TalkGroupsTable.area_id.getColNo()]);
				values.put(TalkGroupsTable.local_area_id.getName(), data[TalkGroupsTable.local_area_id.getColNo()]);
				values.put(TalkGroupsTable.background_file_name.getName(),
						data[TalkGroupsTable.background_file_name.getColNo()]);
				values.put(TalkGroupsTable.select_flg.getName(), data[TalkGroupsTable.select_flg.getColNo()]);
				values.put(TalkGroupsTable.next_group_id.getName(), data[TalkGroupsTable.next_group_id.getColNo()]);
				values.put(TalkGroupsTable.show_memory_flg.getName(), data[TalkGroupsTable.show_memory_flg.getColNo()]);
				values.put(TalkGroupsTable.is_enabled.getName(), data[TalkGroupsTable.is_enabled.getColNo()]);
				values.put(TalkGroupsTable.is_read.getName(), data[TalkGroupsTable.is_read.getColNo()]);

				// insert
				sqliteDB.insert(Tables.talkGroupsTable.getName(), null, values);
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
	public ArrayList<TalkGroup> GetRecords() {
		ArrayList<TalkGroup> values = new ArrayList<TalkGroup>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("select * from talk_groups_tbl WHERE is_enabled = 1", null);

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// TalkGroupsTableの生成・編集
				TalkGroup val = new TalkGroup();
				val.setTalkGroupId(cursor.getInt(TalkGroupsTable.talk_group_id.getColNo()));
				val.setAreaId(cursor.getInt(TalkGroupsTable.area_id.getColNo()));
				val.setLocalAreaId(cursor.getInt(TalkGroupsTable.local_area_id.getColNo()));
				val.setBackGroundFileName(cursor.getString(TalkGroupsTable.background_file_name.getColNo()));
				val.setSelectFlg(cursor.getInt(TalkGroupsTable.select_flg.getColNo()));
				val.setNextGroupId(cursor.getInt(TalkGroupsTable.next_group_id.getColNo()));
				val.setShowMemoryFlg(cursor.getInt(TalkGroupsTable.show_memory_flg.getColNo()));
				val.setIsEnabled(cursor.getInt(TalkGroupsTable.is_enabled.getColNo()));
				val.setIsRead(cursor.getInt(TalkGroupsTable.is_read.getColNo()));

				// 戻り値のリストに追加
				values.add(val);
			}
		} finally {
			// DBクローズ
			sqliteDB.close();
		}
		return values;
	}

	/**
	 * 以下の条件に該当するlocal_item_tblのレコードをList形式で取得する<br/>
	 * ・抽出項目：指定なし<br/>
	 * ・抽出条件：指定なし<br/>
	 * @return
	 */
	public ArrayList<TalkGroup> GetRecordsByAreaId(int areaId) {
		ArrayList<TalkGroup> values = new ArrayList<TalkGroup>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("SELECT * FROM talk_groups_tbl WHERE area_id = ? AND is_enabled = 1",
					new String[] { String.valueOf(areaId) });

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// TalkGroupsTableの生成・編集
				TalkGroup val = new TalkGroup();
				val.setTalkGroupId(cursor.getInt(TalkGroupsTable.talk_group_id.getColNo()));
				val.setAreaId(cursor.getInt(TalkGroupsTable.area_id.getColNo()));
				val.setLocalAreaId(cursor.getInt(TalkGroupsTable.local_area_id.getColNo()));
				val.setBackGroundFileName(cursor.getString(TalkGroupsTable.background_file_name.getColNo()));
				val.setSelectFlg(cursor.getInt(TalkGroupsTable.select_flg.getColNo()));
				val.setNextGroupId(cursor.getInt(TalkGroupsTable.next_group_id.getColNo()));
				val.setShowMemoryFlg(cursor.getInt(TalkGroupsTable.show_memory_flg.getColNo()));
				val.setIsEnabled(cursor.getInt(TalkGroupsTable.is_enabled.getColNo()));
				val.setIsRead(cursor.getInt(TalkGroupsTable.is_read.getColNo()));

				// 戻り値のリストに追加
				values.add(val);
			}
		} finally {
			// DBクローズ
			sqliteDB.close();
		}
		return values;
	}

	public boolean updateIsRead(String talkGroupId) {
		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		// 対象のデータを抽出
		int nextGroupId = 0;
		try {
			// select
			Cursor cursor = sqliteDB.rawQuery("SELECT * FROM talk_groups_tbl WHERE talk_group_id = ?",
					new String[] { talkGroupId });

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {
				nextGroupId = cursor.getInt(TalkGroupsTable.next_group_id.getColNo());
			}

			// 既読処理
			ContentValues cv = new ContentValues();
			cv.put("is_read", 1);
			sqliteDB.update("talk_groups_tbl", cv, "talk_group_id = " + talkGroupId, null);

			// 次のイベント解放
			if (nextGroupId != 0) {

				cv = new ContentValues();
				cv.put("is_enabled", 1);
				sqliteDB.update("talk_groups_tbl", cv, "talk_group_id = " + String.valueOf(nextGroupId), null);
			}
		} finally {
			sqliteDB.close();
		}
		return true;

	}
}
