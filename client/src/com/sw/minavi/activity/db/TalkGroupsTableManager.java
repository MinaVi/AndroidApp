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
				{ "1", "0", "0", "0", "0"},
				{ "2", "0", "0", "0", "0" },
				{ "3", "0", "0", "0", "1" },
				{ "6", "0", "0", "0", "1" },
				
				// 地域システム周辺
				{ "11", "1", "0", "0", "0" },
				{ "12", "1", "0", "0", "0" },
				
				// 猿払道の駅
				{ "13", "2", "0", "hoteltoyotomi", "0" },
				{ "14", "2", "0", "onsen", "0" },
				{ "15", "2", "0", "hoteltoyotomi", "0" },
				
				// もさっぷ
				{ "16", "3", "0", "yashiki_n", "0" },
				
				
				
				
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
				values.put(TalkGroupsTable.background_file_name.getName(), data[TalkGroupsTable.background_file_name.getColNo()]);
				values.put(TalkGroupsTable.select_flg.getName(), data[TalkGroupsTable.select_flg.getColNo()]);

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
			cursor = sqliteDB.rawQuery("select * from talk_groups_tbl", null);

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// TalkGroupsTableの生成・編集
				TalkGroup val = new TalkGroup();
				val.setTalkGroupId(cursor.getInt(TalkGroupsTable.talk_group_id.getColNo()));
				val.setAreaId(cursor.getInt(TalkGroupsTable.area_id.getColNo()));
				val.setLocalAreaId(cursor.getInt(TalkGroupsTable.local_area_id.getColNo()));
				val.setBackGroundFileName(cursor.getString(TalkGroupsTable.background_file_name.getColNo()));
				val.setSelectFlg(cursor.getInt(TalkGroupsTable.select_flg.getColNo()));
				

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
			cursor = sqliteDB.rawQuery("SELECT * FROM talk_groups_tbl WHERE area_id = ?", new String[]{String.valueOf(areaId)});

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// TalkGroupsTableの生成・編集
				TalkGroup val = new TalkGroup();
				val.setTalkGroupId(cursor.getInt(TalkGroupsTable.talk_group_id.getColNo()));
				val.setAreaId(cursor.getInt(TalkGroupsTable.area_id.getColNo()));
				val.setLocalAreaId(cursor.getInt(TalkGroupsTable.local_area_id.getColNo()));
				val.setBackGroundFileName(cursor.getString(TalkGroupsTable.background_file_name.getColNo()));
				val.setSelectFlg(cursor.getInt(TalkGroupsTable.select_flg.getColNo()));
				

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
