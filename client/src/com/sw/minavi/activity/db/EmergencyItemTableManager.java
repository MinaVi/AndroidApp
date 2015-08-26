package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.sw.minavi.activity.db.DatabaseOpenHelper.EmergencyItemTable;
import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.item.EmergencyItem;

/**
 * emergency_item_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * @author RIOH
 *
 */
public class EmergencyItemTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static EmergencyItemTableManager me = null;

	/**
	 * プライベートコンストラクタ
	 */
	private EmergencyItemTableManager() {
	}

	/**
	 * コンストラクタ
	 * @param helper SQLiteOpenHelper継承クラス
	 */
	private EmergencyItemTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * @param helper SQLiteOpenHelper継承クラス
	 * @return 当該クラス唯一のインスタンス
	 */
	public static EmergencyItemTableManager getInstance(DatabaseOpenHelper helper) {
		if (me == null) {
			me = new EmergencyItemTableManager(helper);
		}
		return me;
	}

	/**
	 * サンプルデータの登録
	 */
	public void InsertSample() {

		// サンプルデータの準備
		String[][] datas = new String[][] {
				{ "7", "1", "避難所１", "141.343930", "43.072635", "refuge01", "pin", "-1.0", "-1.0", "-1.0", "-1.0", "20130701000000" },
				{ "8", "2", "避難所２", "141.344041", "43.072680", "refuge01", "pin", "-1.0", "-1.0", "-1.0", "-1.0", "20130701000000" },
				{ "9", "3", "避難所３", "141.343842", "43.072588", "refuge01", "pin", "-1.0", "-1.0", "-1.0", "-1.0", "20130701000000" },
				{ "10", "4", "避難所４", "139.701340", "35.658522", "refuge01", "pin", "-1.0", "-1.0", "-1.0", "-1.0", "20130701000000" },
				{ "14", "5", "避難所イベント", "141.338632", "43.106693", "refuge01", "pin", "141.331322", "43.070880", "141.347216",
						"43.090761", "20130701000000" },

		};

		// 書き込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getWritableDatabase();

		try {

			// トランザクションの開始
			sqliteDB.beginTransaction();

			sqliteDB.delete(Tables.emergencyItemTable.getName(), null, null);
			for (String[] data : datas) {
				// insertするデータを作成する
				// id項目については自動採番なので用意する必要はない
				ContentValues values = new ContentValues();
				for (EmergencyItemTable column : EmergencyItemTable.values()) {
					values.put(column.toString(), data[column.ordinal()]);
				}

				// insert
				sqliteDB.insert(Tables.emergencyItemTable.getName(), null, values);
			}

			// コミット
			sqliteDB.setTransactionSuccessful();

		} finally {
			// トランザクションの終了
			sqliteDB.endTransaction();
		}
	}

	/**
	 * 以下の条件に該当するemergency_item_tblのレコードをList形式で取得する<br/>
	 * ・抽出項目：指定なし<br/>
	 * ・抽出条件：指定なし<br/>
	 * @return
	 */
	public ArrayList<EmergencyItem> GetRecords() {
		ArrayList<EmergencyItem> values = new ArrayList<EmergencyItem>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("select * from emergency_item_tbl", null);

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// EmergencyItemの生成・編集
				EmergencyItem val = new EmergencyItem();
				val.setId(cursor.getInt(EmergencyItemTable.ID.ordinal()));
				val.setTalkGroupId(cursor.getInt(EmergencyItemTable.TALK_GROUP_ID.ordinal()));
				val.setMessage(cursor.getString(EmergencyItemTable.MESSAGE.ordinal()));
				val.setLon(Double.valueOf(cursor.getString(EmergencyItemTable.LON.ordinal())).doubleValue());
				val.setLat(Double.valueOf(cursor.getString(EmergencyItemTable.LAT.ordinal())).doubleValue());
				val.setArImageName(cursor.getString(EmergencyItemTable.AR_IMAGE_NAME.ordinal()));
				val.seticonImageName(cursor.getString(EmergencyItemTable.ICON_IMAGE_NAME.ordinal()));
				val.setSpecialLonMin(Double.valueOf(cursor.getString(EmergencyItemTable.SPECIAL_LON_MIN.ordinal())).doubleValue());
				val.setSpecialLatMin(Double.valueOf(cursor.getString(EmergencyItemTable.SPECIAL_LAT_MIN.ordinal())).doubleValue());
				val.setSpecialLonMax(Double.valueOf(cursor.getString(EmergencyItemTable.SPECIAL_LON_MAX.ordinal())).doubleValue());
				val.setSpecialLatMax(Double.valueOf(cursor.getString(EmergencyItemTable.SPECIAL_LAT_MAX.ordinal())).doubleValue());
				val.setCreateTime(cursor.getString(EmergencyItemTable.CREATE_TIME.ordinal()));

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
	 * 以下の条件に該当するemergency_item_tblのレコードをList形式で取得する<br/>
	 * ・抽出項目：指定なし<br/>
	 * ・抽出条件：指定なし<br/>
	 * @return
	 */
	public ArrayList<EmergencyItem> GetAroundRecords(Location location) {
		ArrayList<EmergencyItem> values = new ArrayList<EmergencyItem>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("select * from emergency_item_tbl", null);

			// 取得したデータをレコード毎に処理する

			while (cursor.moveToNext()) {

				// EmergencyItemの生成・編集
				EmergencyItem val = new EmergencyItem();
				val.setId(cursor.getInt(EmergencyItemTable.ID.ordinal()));
				val.setTalkGroupId(cursor.getInt(EmergencyItemTable.TALK_GROUP_ID.ordinal()));
				val.setMessage(cursor.getString(EmergencyItemTable.MESSAGE.ordinal()));
				val.setLon(Double.valueOf(cursor.getString(EmergencyItemTable.LON.ordinal())).doubleValue());
				val.setLat(Double.valueOf(cursor.getString(EmergencyItemTable.LAT.ordinal())).doubleValue());
				val.setArImageName(cursor.getString(EmergencyItemTable.AR_IMAGE_NAME.ordinal()));
				val.seticonImageName(cursor.getString(EmergencyItemTable.ICON_IMAGE_NAME.ordinal()));
				val.setSpecialLonMin(Double.valueOf(cursor.getString(EmergencyItemTable.SPECIAL_LON_MIN.ordinal())).doubleValue());
				val.setSpecialLatMin(Double.valueOf(cursor.getString(EmergencyItemTable.SPECIAL_LAT_MIN.ordinal())).doubleValue());
				val.setSpecialLonMax(Double.valueOf(cursor.getString(EmergencyItemTable.SPECIAL_LON_MAX.ordinal())).doubleValue());
				val.setSpecialLatMax(Double.valueOf(cursor.getString(EmergencyItemTable.SPECIAL_LAT_MAX.ordinal())).doubleValue());
				val.setCreateTime(cursor.getString(EmergencyItemTable.CREATE_TIME.ordinal()));

				float[] results = new float[1];
				Location.distanceBetween(location.getLatitude(), location.getLongitude(), val.getLat(), val.getLon(), results);
				if (results[0] > 300) {
					boolean intersect = val.getSpecialLatMin() <= location.getLatitude() &&
							val.getSpecialLatMax() >= location.getLatitude() &&
							val.getSpecialLonMin() <= location.getLongitude() &&
							val.getSpecialLonMax() >= location.getLongitude();
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
	 * 以下の条件に該当するemergency_item_tblのレコードをList形式で取得する<br/>
	 * ・抽出項目：指定なし<br/>
	 * ・抽出条件：指定なし<br/>
	 * @return
	 */
	public ArrayList<EmergencyItem> GetRecordsDebug() {
		ArrayList<EmergencyItem> values = new ArrayList<EmergencyItem>();

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

			// EmergencyItemの生成・編集
			EmergencyItem val = new EmergencyItem();
			val.setId(Integer.valueOf(record[0]));
			val.setTalkGroupId(Integer.valueOf(record[1]));
			val.setMessage(record[2]);
			val.setLon(Double.valueOf(record[3]).doubleValue());
			val.setLat(Double.valueOf(record[4]).doubleValue());
			val.setArImageName(record[5]);
			val.seticonImageName(record[6]);
			val.setCreateTime(record[7]);

			values.add(val);
		}
		return values;
	}
}
