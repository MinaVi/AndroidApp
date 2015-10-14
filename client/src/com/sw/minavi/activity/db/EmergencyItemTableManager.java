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
	/** 言語設定マネージャー */
	public static String lang = "Japanese";

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
				{"1","0","在宅型有料老人ホームあっとほーむ鎌倉","Nursing home","139.551147","35.312412","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"2","0","鎌倉ヒロ病院（新館）","Hospital","139.552246","35.312557","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"3","0","鎌倉バンビル","Building","139.548468","35.30995","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"4","0","野畑ビル","Building","139.547996","35.310261","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"5","0","スタンレー　サバーバンオフィス　サーフサイド","Office","139.5469","35.308364","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"6","0","ビラ・かまくら","Building","139.553796","35.305708","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"7","0","鎌倉市消防本部","Fire station","139.546971","35.310424","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"8","0","由比ガ浜ハイツ","Building","139.54473","35.311372","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"9","0","鎌倉わかみや","Building","139.543995","35.31104","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"10","0","第一小学校","Primary school","139.547816","35.314281","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"11","0","鎌倉女学院","School","139.550175","35.313854","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"12","0","由比ガ浜コーポ１号","Building","139.547503","35.31065","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"13","0","由比ガ浜コーポ２号","Building","139.547503","35.31065","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"14","0","ハピネス由比ガ浜","Building","139.547723","35.315868","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"15","0","ダイヤモンド鎌倉別邸ソサエティ","Building","139.539843","35.313421","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"16","0","早見芸術学園１号館","School","139.550787","35.317812","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"17","0","鎌陽洞ビル","Building","139.548716","35.319265","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"18","0","鎌倉彫会館","Building","139.553249","35.320407","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"19","0","ＫＮビル","Building","139.549552","35.319063","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"20","0","カドキホール","Hall","139.550721","35.315701","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"21","0","櫻井ビル","Building","139.551227","35.320341","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"22","0","かまくら春秋スクエア","Building","139.553523","35.320989","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"23","0","斎藤ビル","Building","139.535684","35.310313","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"24","0","軽費老人ホームきしろホーム","Nursing home","139.53223","35.306725","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"25","0","鎌倉パークホテル","Hotel","139.531823","35.305332","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"26","0","介護老人福祉施設鎌倉清和由比","Nursing home","139.531984","35.30577","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"27","0","江ノ島ビーチハウス","Building","139.489275","35.30865","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"28","0","腰越中央医院","Hospital","139.497225","35.311393","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"29","0","腰越消防出張所","Fire station","139.497558","35.311139","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"30","0","妙本寺","Temple","139.556921","35.317131","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"31","0","来迎寺","Temple","139.555003","35.309638","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"32","0","光明寺","Temple","139.554279","35.303703","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"33","0","長勝寺","Temple","139.556824","35.310203","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"34","0","実相寺","Temple","139.553853","35.308409","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"35","0","第一中学校","Junior high school","139.555719","35.302574","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"36","0","名越クリーンセンター","Building","139.561312","35.308054","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"37","0","紅谷旧市営住宅跡","Open space","139.557959","35.306945","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"38","0","鎌倉わかみや","Building","139.543995","35.31104","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"39","0","鎌倉海浜公園（由比ガ浜地区）","Open space","139.543566","35.309998","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"40","0","御成小学校","Primary school","139.547099","35.318208","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"41","0","御成中学校","Primary school","139.542216","35.318059","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"42","0","光則寺","Temple","139.533027","35.313311","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"43","0","高徳院（大仏）","Temple","139.535845","35.31697","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"44","0","長谷寺","Temple","139.53273","35.311897","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"45","0","御霊神社","Shrine","139.532672","35.311142","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"46","0","鎌倉文学館","Building","139.53887","35.315562","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"47","0","鎌倉海浜公園（稲村ガ崎地区）","Open space","139.525124","35.30266","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"48","0","七里ガ浜ゴルフ場","Open space","139.516598","35.308273","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"49","0","県立鎌倉高等学校","High School","139.503485","35.308624","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"50","0","県立七里ガ浜高等学校","High School","139.512891","35.305556","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"51","0","小動神社","Shrine","139.49358","35.306631","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"52","0","モンタナ修道院","Monastery","139.501674","35.312518","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"53","0","腰越小学校","Primary school","139.492987","35.312502","eme_pin","eme_pin","-1","-1","-1","-1","20150831"},
				{"54","0","小学校","Primary school","140.997731","43.212968","eme_pin","eme_pin","-1","-1","-1","-1","20150831"}

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
				
				// メッセージは言語によって変える
				if(lang.equals("English")){
					val.setMessage(cursor.getString(EmergencyItemTable.MESSAGE_EN.ordinal()));
				}else{
					val.setMessage(cursor.getString(EmergencyItemTable.MESSAGE.ordinal()));
				}
				
				val.setLon(Double.valueOf(cursor.getString(EmergencyItemTable.LON.ordinal())).doubleValue());
				val.setLat(Double.valueOf(cursor.getString(EmergencyItemTable.LAT.ordinal())).doubleValue());
				val.setArImageName(cursor.getString(EmergencyItemTable.AR_IMAGE_NAME.ordinal()));
				val.setIconImageName(cursor.getString(EmergencyItemTable.ICON_IMAGE_NAME.ordinal()));
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
				
				// メッセージは言語によって変える
				if(lang.equals("English")){
					val.setMessage(cursor.getString(EmergencyItemTable.MESSAGE_EN.ordinal()));
				}else{
					val.setMessage(cursor.getString(EmergencyItemTable.MESSAGE.ordinal()));
				}
				
				val.setLon(Double.valueOf(cursor.getString(EmergencyItemTable.LON.ordinal())).doubleValue());
				val.setLat(Double.valueOf(cursor.getString(EmergencyItemTable.LAT.ordinal())).doubleValue());
				val.setArImageName(cursor.getString(EmergencyItemTable.AR_IMAGE_NAME.ordinal()));
				val.setIconImageName(cursor.getString(EmergencyItemTable.ICON_IMAGE_NAME.ordinal()));
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
			val.setIconImageName(record[6]);
			val.setCreateTime(record[7]);

			values.add(val);
		}
		return values;
	}
}
