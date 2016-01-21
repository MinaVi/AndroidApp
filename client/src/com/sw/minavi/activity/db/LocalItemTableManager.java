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


{"1","12","210","hokudai210","hokudai210","210","hokudai210","hokudai210","141.3438259172218","43.07265368774401","mina1_nomal","mina_pin","0","0","0","0","20150831"},
{"2","11","北海道大学","HokkaidoUniv","北海道大学","北海道大学","hokudai210","北海道大学","141.34742544171837","43.07116458468558","question","mina_pin","-1","-1","-1","-1","20150831"},

// ニセコ
{"2","13","茶房ヌプリ","SabouNupuri","萨博Nupuri","茶房ヌプリ","hokudai210","萨博Nupuri","140.6848768996914","42.8089283078543","mina2_nomal","mina_pin","-1","-1","-1","-1","20150831"},
{"3","14","雅楽","Garaku","古代宫廷音乐","雅楽","Garaku","古代宫廷音乐","140.64001951038688","42.84285156923757","mina2_nomal","mina_pin","-1","-1","-1","-1","20150831"},

//鎌倉
{"4","15","江ノ島電鉄","EnoshimaTrain","江之岛电铁","島電鉄","EnoshimaTrain","江之岛电铁","139.550028","35.318579","mina2_nomal","mina_pin","-1","-1","-1","-1","20150831"},
{"5","16","鎌倉大仏","KamakuraDaibutu","镰仓大佛","鎌倉大仏","KamakuraDaibutu","镰仓大佛","139.535725","35.316852","mina2_nomal","mina_pin","-1","-1","-1","-1","20150831"},
{"6","17","鶴岡八幡宮　源平池","GenpeiIke","鹤冈八幡源平的池塘","岡八幡宮　源平池","GenpeiIke","鹤冈八幡源平的池塘","139.555278","35.324237","mina2_nomal","mina_pin","-1","-1","-1","-1","20150831"},
{"7","18","鶴岡八幡宮　本宮","TuruokaHachimangu","鹤冈八幡神社本宫","鶴岡八幡宮　本宮","TuruokaHachimangu","鹤冈八幡神社本宫","139.556222","35.325865","mina2_nomal","mina_pin","-1","-1","-1","-1","20150831"},
{"8","20","小樽水族館","EnoshimaTrain","小樽水族馆","小樽水族館","EnoshimaTrain","小樽水族馆","141.011795","43.237091","mina2_nomal","mina_pin","-1","-1","-1","-1","20150831"},

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
				} else if(lang.equals("Chainese")) {
					val.setMessage(cursor.getString(LocalItemTable.MESSAGE_CN
							.ordinal()));
				} else {
					val.setMessage(cursor.getString(LocalItemTable.MESSAGE
							.ordinal()));
				}
				
				// メッセージは言語によって変える
				if (lang.equals("English")) {
					val.setDetail(cursor.getString(LocalItemTable.DETAIL_EN
							.ordinal()));
				} else if(lang.equals("Chainese")) {
					val.setDetail(cursor.getString(LocalItemTable.DETAIL_CN
							.ordinal()));
				} else {
					val.setDetail(cursor.getString(LocalItemTable.DETAIL
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
				} else if(lang.equals("Chainese")) {
					val.setMessage(cursor.getString(LocalItemTable.MESSAGE_CN
							.ordinal()));
				} else {
					val.setMessage(cursor.getString(LocalItemTable.MESSAGE
							.ordinal()));
				}

				// メッセージは言語によって変える
				if (lang.equals("English")) {
					val.setDetail(cursor.getString(LocalItemTable.DETAIL_EN
							.ordinal()));
				} else if(lang.equals("Chainese")) {
					val.setDetail(cursor.getString(LocalItemTable.DETAIL_CN
							.ordinal()));
				} else {
					val.setDetail(cursor.getString(LocalItemTable.DETAIL
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
