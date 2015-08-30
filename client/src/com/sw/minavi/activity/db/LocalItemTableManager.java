package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.os.Message;

import com.sw.minavi.activity.db.DatabaseOpenHelper.LocalItemTable;
import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.http.GetLocalItems;
import com.sw.minavi.item.LocalItem;

/**
 * local_item_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * 
 * @author RIOH
 * 
 */
public class LocalItemTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static LocalItemTableManager me = null;
	/** 言語設定マネージャー */
	public static String lang = "Japanese";


	/**
	 * プライベートコンストラクタ
	 */
	private LocalItemTableManager() {

	}

	/**
	 * コンストラクタ
	 * 
	 * @param helper
	 *            SQLiteOpenHelper継承クラス
	 */
	private LocalItemTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * 
	 * @param helper
	 *            SQLiteOpenHelper継承クラス
	 * @return 当該クラス唯一のインスタンス
	 */
	public static LocalItemTableManager getInstance(DatabaseOpenHelper helper) {
		if (me == null) {
			me = new LocalItemTableManager(helper);
		}
		return me;
	}

	/**
	 * サンプルデータの登録
	 */
	public void InsertSample() {

		// サンプルデータの準備

		
		String[][] datas = new String[][] {

{"1","2","北大１","hokudai１","141.34393","35.873561561281","mina1_nomal","pin","0","0","0","0","20150831"},
{"2","0","北大２","hokudai2","141.344041","43.07268","question","pin","-1","-1","-1","-1","20150831"},
{"2","0","北大３","hokudai3","141.343842","43.072588","mina2_nomal","pin","-1","-1","-1","-1","20150831"},


		};

		// 書き込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getWritableDatabase();

		try {

			// トランザクションの開始
			sqliteDB.beginTransaction();

			sqliteDB.delete(Tables.localItemTable.getName(), null, null);
			for (String[] data : datas) {
				// insertするデータを作成する
				// id項目については自動採番なので用意する必要はない
				ContentValues values = new ContentValues();
				for (LocalItemTable column : LocalItemTable.values()) {
					values.put(column.toString(), data[column.ordinal()]);
				}

				// insert
				sqliteDB.insert(Tables.localItemTable.getName(), null, values);
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
	 * 
	 * @return
	 */
	public ArrayList<LocalItem> GetRecords() {
		ArrayList<LocalItem> values = new ArrayList<LocalItem>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("select * from local_item_tbl", null);

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// LocalItemの生成・編集
				LocalItem val = new LocalItem();
				val.setId(cursor.getInt(LocalItemTable.ID.ordinal()));
				val.setTalkGroupId(cursor.getInt(LocalItemTable.TALK_GROUP_ID
						.ordinal()));

				// メッセージは言語によって変える
				if (lang.equals("English")) {
					val.setMessage(cursor.getString(LocalItemTable.MESSAGE_EN
							.ordinal()));
				} else {
					val.setMessage(cursor.getString(LocalItemTable.MESSAGE
							.ordinal()));
				}

				val.setLon(Double.valueOf(
						cursor.getString(LocalItemTable.LON.ordinal()))
						.doubleValue());
				val.setLat(Double.valueOf(
						cursor.getString(LocalItemTable.LAT.ordinal()))
						.doubleValue());
				val.setArImageName(cursor
						.getString(LocalItemTable.AR_IMAGE_NAME.ordinal()));
				val.setIconImageName(cursor
						.getString(LocalItemTable.ICON_IMAGE_NAME.ordinal()));
				val.setSpecialLonMin(Double.valueOf(
						cursor.getString(LocalItemTable.SPECIAL_LON_MIN
								.ordinal())).doubleValue());
				val.setSpecialLatMin(Double.valueOf(
						cursor.getString(LocalItemTable.SPECIAL_LAT_MIN
								.ordinal())).doubleValue());
				val.setSpecialLonMax(Double.valueOf(
						cursor.getString(LocalItemTable.SPECIAL_LON_MAX
								.ordinal())).doubleValue());
				val.setSpecialLatMax(Double.valueOf(
						cursor.getString(LocalItemTable.SPECIAL_LAT_MAX
								.ordinal())).doubleValue());
				val.setCreateTime(cursor.getString(LocalItemTable.CREATE_TIME
						.ordinal()));

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
	 * 
	 * @return
	 */
	public ArrayList<LocalItem> GetAroundRecords(Location location) {
		ArrayList<LocalItem> values = new ArrayList<LocalItem>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("select * from local_item_tbl", null);

			// 取得したデータをレコード毎に処理する

			while (cursor.moveToNext()) {

				// LocalItemの生成・編集
				LocalItem val = new LocalItem();
				val.setId(cursor.getInt(LocalItemTable.ID.ordinal()));
				val.setTalkGroupId(cursor.getInt(LocalItemTable.TALK_GROUP_ID
						.ordinal()));

				// メッセージは言語によって変える
				if (lang.equals("English")) {
					val.setMessage(cursor.getString(LocalItemTable.MESSAGE_EN
							.ordinal()));
				} else {
					val.setMessage(cursor.getString(LocalItemTable.MESSAGE
							.ordinal()));
				}

				val.setLon(Double.valueOf(
						cursor.getString(LocalItemTable.LON.ordinal()))
						.doubleValue());
				val.setLat(Double.valueOf(
						cursor.getString(LocalItemTable.LAT.ordinal()))
						.doubleValue());
				val.setArImageName(cursor
						.getString(LocalItemTable.AR_IMAGE_NAME.ordinal()));
				val.setIconImageName(cursor
						.getString(LocalItemTable.ICON_IMAGE_NAME.ordinal()));
				val.setSpecialLonMin(Double.valueOf(
						cursor.getString(LocalItemTable.SPECIAL_LON_MIN
								.ordinal())).doubleValue());
				val.setSpecialLatMin(Double.valueOf(
						cursor.getString(LocalItemTable.SPECIAL_LAT_MIN
								.ordinal())).doubleValue());
				val.setSpecialLonMax(Double.valueOf(
						cursor.getString(LocalItemTable.SPECIAL_LON_MAX
								.ordinal())).doubleValue());
				val.setSpecialLatMax(Double.valueOf(
						cursor.getString(LocalItemTable.SPECIAL_LAT_MAX
								.ordinal())).doubleValue());
				val.setCreateTime(cursor.getString(LocalItemTable.CREATE_TIME
						.ordinal()));

				float[] results = new float[1];
				Location.distanceBetween(location.getLatitude(),
						location.getLongitude(), val.getLat(), val.getLon(),
						results);
				if (results[0] > 300) {
					boolean intersect = val.getSpecialLatMin() <= location
							.getLatitude()
							&& val.getSpecialLatMax() >= location.getLatitude()
							&& val.getSpecialLonMin() <= location
									.getLongitude()
							&& val.getSpecialLonMax() >= location
									.getLongitude();
					if (!intersect) {
						continue;
					}
				}

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
	 * 
	 * @return
	 */
	public ArrayList<LocalItem> GetRecordsDebug() {
		ArrayList<LocalItem> values = new ArrayList<LocalItem>();

		// サンプルデータの準備
		String[][] datas = new String[][] {
				{ "1", "1", "message1", "139.701334", "35.658512", "question",
						"auther1", "20130701000000" },
				{ "2", "1", "message2", "139.701331", "35.658513", "question",
						"auther2", "20130701000000" },
				{ "3", "1", "message3", "139.701337", "35.658513", "question",
						"auther3", "20130701000000" },
				{ "4", "1", "message4", "139.701340", "35.658522", "question",
						"auther4", "20130701000000" },
				{ "5", "1", "message5", "139.701334", "35.658523", "question",
						"auther5", "20130701000000" },
				{ "6", "1", "Version2", "141.347506", "43.060656", "question",
						"auther6", "20130701000000" }, };
		for (String[] record : datas) {

			// LocalItemの生成・編集
			LocalItem val = new LocalItem();
			val.setId(Integer.valueOf(record[0]));
			val.setTalkGroupId(Integer.valueOf(record[1]));
			val.setMessage(record[2]);
			val.setLon(Double.valueOf(record[3]).doubleValue());
			val.setLat(Double.valueOf(record[4]).doubleValue());
			val.setArImageName(record[5]);
			val.setIconImageName(record[6]);
			val.setCreateTime(record[7]);

			values.add(val);
		}
		return values;
	}
}
