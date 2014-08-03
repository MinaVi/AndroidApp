package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sw.minavi.activity.db.DatabaseOpenHelper.StoryGroupsTable;
import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.item.StoryGroup;

/**
 * story_groups_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * @author RIOH
 *
 */
public class StoryGroupsTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static StoryGroupsTableManager me = null;

	/**
	 * プライベートコンストラクタ
	 */
	private StoryGroupsTableManager() {
	}

	/**
	 * コンストラクタ
	 * @param helper SQLiteOpenHelper継承クラス
	 */
	private StoryGroupsTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * @param helper SQLiteOpenHelper継承クラス
	 * @return 当該クラス唯一のインスタンス
	 */
	public static StoryGroupsTableManager getInstance(DatabaseOpenHelper helper) {
		if (me == null) {
			me = new StoryGroupsTableManager(helper);
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
				{ "13", "2", "0", "sarobetu", "0" },
				{ "14", "2", "0", "onsen", "0" },
				{ "15", "2", "0", "hoteltoyotomi", "0" },
				
				
				
		};

		// 書き込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getWritableDatabase();

		try {

			// トランザクションの開始
			sqliteDB.beginTransaction();

			sqliteDB.delete(Tables.storyGroupsTable.getName(), null, null);
			for (String[] data : datas) {
				// insertするデータを作成する
				// talk_group_id項目については自動採番なので用意する必要はない
				ContentValues values = new ContentValues();
				values.put(StoryGroupsTable.story_group_id.getName(), data[StoryGroupsTable.story_group_id.getColNo()]);
				values.put(StoryGroupsTable.area_id.getName(), data[StoryGroupsTable.area_id.getColNo()]);
				values.put(StoryGroupsTable.local_area_id.getName(), data[StoryGroupsTable.local_area_id.getColNo()]);
				values.put(StoryGroupsTable.background_file_name.getName(), data[StoryGroupsTable.background_file_name.getColNo()]);
				values.put(StoryGroupsTable.select_flg.getName(), data[StoryGroupsTable.select_flg.getColNo()]);
				values.put(StoryGroupsTable.is_read.getName(), data[StoryGroupsTable.is_read.getColNo()]);
				
				// insert
				sqliteDB.insert(Tables.storyGroupsTable.getName(), null, values);
			}

			// コミット
			sqliteDB.setTransactionSuccessful();

		} finally {
			// トランザクションの終了
			sqliteDB.endTransaction();
		}
	}

	/**
	 * 以下の条件に該当するstory_groups_tblのレコードをList形式で取得する<br/>
	 * ・抽出項目：指定なし<br/>
	 * ・抽出条件：指定なし<br/>
	 * @return
	 */
	public ArrayList<StoryGroup> GetRecords() {
		ArrayList<StoryGroup> values = new ArrayList<StoryGroup>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("select * from story_groups_tbl", null);

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// StoryGroupsTableの生成・編集
				StoryGroup val = new StoryGroup();
				val.setStoryGroupId(cursor.getInt(StoryGroupsTable.story_group_id.getColNo()));
				val.setAreaId(cursor.getInt(StoryGroupsTable.area_id.getColNo()));
				val.setLocalAreaId(cursor.getInt(StoryGroupsTable.local_area_id.getColNo()));
				val.setBackGroundFileName(cursor.getString(StoryGroupsTable.background_file_name.getColNo()));
				val.setSelectFlg(cursor.getInt(StoryGroupsTable.select_flg.getColNo()));
				val.setSelectFlg(cursor.getInt(StoryGroupsTable.is_read.getColNo()));
				

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
	 * 以下の条件に該当するstory_groups_tblのレコードをList形式で取得する<br/>
	 * ・抽出項目：指定なし<br/>
	 * ・抽出条件：指定なし<br/>
	 * @return
	 */
	public ArrayList<StoryGroup> GetRecordsByAreaId(int areaId) {
		ArrayList<StoryGroup> values = new ArrayList<StoryGroup>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("SELECT * FROM story_groups_tbl WHERE area_id = ?", new String[]{String.valueOf(areaId)});

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// StoryGroupsTableの生成・編集
				StoryGroup val = new StoryGroup();
				val.setStoryGroupId(cursor.getInt(StoryGroupsTable.story_group_id.getColNo()));
				val.setAreaId(cursor.getInt(StoryGroupsTable.area_id.getColNo()));
				val.setLocalAreaId(cursor.getInt(StoryGroupsTable.local_area_id.getColNo()));
				val.setBackGroundFileName(cursor.getString(StoryGroupsTable.background_file_name.getColNo()));
				val.setSelectFlg(cursor.getInt(StoryGroupsTable.select_flg.getColNo()));
				val.setSelectFlg(cursor.getInt(StoryGroupsTable.is_read.getColNo()));
				

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
	 * 以下の条件に該当するstory_groups_tblのレコードをList形式で取得する<br/>
	 * ・抽出項目：指定なし<br/>
	 * ・抽出条件：指定なし<br/>
	 * @return
	 */
	public ArrayList<StoryGroup> GetRecordsByGroupId(int groupId) {
		ArrayList<StoryGroup> values = new ArrayList<StoryGroup>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("SELECT * FROM story_groups_tbl WHERE story_group_id = ?", new String[]{String.valueOf(groupId)});

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// StoryGroupsTableの生成・編集
				StoryGroup val = new StoryGroup();
				val.setStoryGroupId(cursor.getInt(StoryGroupsTable.story_group_id.getColNo()));
				val.setAreaId(cursor.getInt(StoryGroupsTable.area_id.getColNo()));
				val.setLocalAreaId(cursor.getInt(StoryGroupsTable.local_area_id.getColNo()));
				val.setBackGroundFileName(cursor.getString(StoryGroupsTable.background_file_name.getColNo()));
				val.setSelectFlg(cursor.getInt(StoryGroupsTable.select_flg.getColNo()));
				val.setSelectFlg(cursor.getInt(StoryGroupsTable.is_read.getColNo()));
				

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
