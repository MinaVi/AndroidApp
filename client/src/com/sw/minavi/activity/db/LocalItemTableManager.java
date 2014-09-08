package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.sw.minavi.activity.db.DatabaseOpenHelper.LocalItemTable;
import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.item.LocalItem;

/**
 * local_item_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * @author RIOH
 *
 */
public class LocalItemTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static LocalItemTableManager me = null;

	/**
	 * プライベートコンストラクタ
	 */
	private LocalItemTableManager() {
	}

	/**
	 * コンストラクタ
	 * @param helper SQLiteOpenHelper継承クラス
	 */
	private LocalItemTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * @param helper SQLiteOpenHelper継承クラス
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
				//				{ "1","1", "message1", "139.701334", "35.658512", "question", "auther1", "20130701000000" },
				//				{ "2","1", "message2", "139.701331", "35.658513", "question", "auther2", "20130701000000" },
				//				{ "3","1", "message3", "139.701337", "35.658513", "question", "auther3", "20130701000000" },
				//				{ "4","1", "message4", "139.701340", "35.658522", "question", "auther4", "20130701000000" },
				//				{ "5","1", "message5", "139.701334", "35.658523", "question", "auther5", "20130701000000" },
				//				{ "6","1", "Version2", "141.347506", "43.060656", "question", "auther6", "20130701000000" },
				{ "7", "11", "北大１", "141.343930", "43.072635", "nomal_ss", "auther1", "20130701000000" },
				{ "8", "13", "北大２", "141.344041", "43.072680", "question", "auther2", "20130701000000" },
				{ "9", "14", "北大３", "141.343842", "43.072588", "nomal_ss", "auther3", "20130701000000" },
				{ "10", "15", "北大４", "139.701340", "35.658522", "question", "auther4", "20130701000000" },
				//{ "11", "11", "北大５", "139.701334", "35.658523", "question", "auther5", "20130701000000" },
				//{ "12", "1", "大通り", "141.347702", "43.059936", "nomal_ss", "auther6", "20130701000000" },
				//{ "13","11", "麻生", "141.338918", "43.107020", "chitoge", "auther6", "20130701000000" },

				// 猿払村道の駅
				//{ "14","13", "豊富温泉", "141.840295", "45.07146,", "chitoge", "auther6", "20130701000000" },
				//{ "15","14", "温泉", "141.840273", "45.074233", "buraito", "auther6", "20130701000000" },
				// テスト
				{ "14", "13", "豊富温泉", "141.840295", "45.07146", "chitoge", "auther6", "20130701000000" },
				{ "15", "14", "温泉", "141.840273", "45.074233", "buraito", "auther6", "20130701000000" },
				{ "16", "15", "温泉", "141.840273", "45.074233", "yan", "auther6", "20130701000000" },

				// 宗谷岬
				//				{ "16","15", "猿払村道の駅クイズ", "141.338320", "43.106800", "t_nomal_n", "auther6", "20130701000000" },

				// とよとみ温泉
				//				{ "17","17", "温泉", "141.338918", "43.059936", "yan", "auther6", "20130701000000" },
				//				{ "18","18", "豊富温泉", "141.338320", "43.106800", "rurusy", "auther6", "20130701000000" },

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
				values.put(LocalItemTable.id.getName(), data[0]);
				values.put(LocalItemTable.talk_group_id.getName(), data[1]);
				values.put(LocalItemTable.message.getName(), data[2]);
				values.put(LocalItemTable.lon.getName(), data[3]);
				values.put(LocalItemTable.lat.getName(), data[4]);
				values.put(LocalItemTable.ar_image_name.getName(), data[5]);
				values.put(LocalItemTable.auther.getName(), data[6]);
				values.put(LocalItemTable.createTime.getName(), data[7]);

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
				val.setId(cursor.getInt(LocalItemTable.id.getColNo()));
				val.setTalkGroupId(cursor.getInt(LocalItemTable.talk_group_id.getColNo()));
				val.setMessage(cursor.getString(LocalItemTable.message.getColNo()));
				val.setLon(Double.valueOf(cursor.getString(LocalItemTable.lon.getColNo())).doubleValue());
				val.setLat(Double.valueOf(cursor.getString(LocalItemTable.lat.getColNo())).doubleValue());
				val.setArImageName(cursor.getString(LocalItemTable.ar_image_name.getColNo()));
				val.setAuther(cursor.getString(LocalItemTable.auther.getColNo()));
				val.setCreateTime(cursor.getString(LocalItemTable.createTime.getColNo()));

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

				double latitude = Double.valueOf(cursor.getString(LocalItemTable.lat.getColNo())).doubleValue();
				double longitude = Double.valueOf(cursor.getString(LocalItemTable.lon.getColNo())).doubleValue();

				float[] results = new float[1];
				Location.distanceBetween(location.getLatitude(), location.getLongitude(), latitude, longitude, results);
				if (results[0] > 300) {
					continue;
				}

				// LocalItemの生成・編集
				LocalItem val = new LocalItem();
				val.setId(cursor.getInt(LocalItemTable.id.getColNo()));
				val.setTalkGroupId(cursor.getInt(LocalItemTable.talk_group_id.getColNo()));
				val.setMessage(cursor.getString(LocalItemTable.message.getColNo()));
				val.setLon(Double.valueOf(cursor.getString(LocalItemTable.lon.getColNo())).doubleValue());
				val.setLat(Double.valueOf(cursor.getString(LocalItemTable.lat.getColNo())).doubleValue());
				val.setArImageName(cursor.getString(LocalItemTable.ar_image_name.getColNo()));
				val.setAuther(cursor.getString(LocalItemTable.auther.getColNo()));
				val.setCreateTime(cursor.getString(LocalItemTable.createTime.getColNo()));

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
	public ArrayList<LocalItem> GetRecordsDebug() {
		ArrayList<LocalItem> values = new ArrayList<LocalItem>();

		// サンプルデータの準備
		String[][] datas = new String[][] {
				{ "1", "1", "message1", "139.701334", "35.658512", "question", "auther1", "20130701000000" },
				{ "2", "1", "message2", "139.701331", "35.658513", "question", "auther2", "20130701000000" },
				{ "3", "1", "message3", "139.701337", "35.658513", "question", "auther3", "20130701000000" },
				{ "4", "1", "message4", "139.701340", "35.658522", "question", "auther4", "20130701000000" },
				{ "5", "1", "message5", "139.701334", "35.658523", "question", "auther5", "20130701000000" },
				{ "6", "1", "Version2", "141.347506", "43.060656", "question", "auther6", "20130701000000" },
		};
		for (String[] record : datas) {

			// LocalItemの生成・編集
			LocalItem val = new LocalItem();
			val.setId(Integer.valueOf(record[0]));
			val.setTalkGroupId(Integer.valueOf(record[1]));
			val.setMessage(record[2]);
			val.setLon(Double.valueOf(record[3]).doubleValue());
			val.setLat(Double.valueOf(record[4]).doubleValue());
			val.setArImageName(record[5]);
			val.setAuther(record[6]);
			val.setCreateTime(record[7]);

			values.add(val);
		}
		return values;
	}
}
