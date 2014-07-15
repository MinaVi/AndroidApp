package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
				{ "1", "message1", "139.701334", "35.658512", "auther1", "20130701000000" },
				{ "2", "message2", "139.701331", "35.658513", "auther2", "20130701000000" },
				{ "3", "message3", "139.701337", "35.658513", "auther3", "20130701000000" },
				{ "4", "message4", "139.701340", "35.658522", "auther4", "20130701000000" },
				{ "5", "message5", "139.701334", "35.658523", "auther5", "20130701000000" },
				{ "6", "message6", "139.701329", "35.658521", "auther6", "20130701000000" },
				{ "7", "北大１", "141.343930", "43.072635", "auther1", "20130701000000" },
				{ "8", "北大２", "141.344041", "43.072680", "auther2", "20130701000000" },
				{ "9", "北大３", "141.343842", "43.072588", "auther3", "20130701000000" },
				{ "10", "北大４", "139.701340", "35.658522", "auther4", "20130701000000" },
				{ "11", "北大５", "139.701334", "35.658523", "auther5", "20130701000000" },
				{ "12", "北大６", "139.701329", "35.658521", "auther6", "20130701000000" },
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
				values.put(LocalItemTable.message.getName(), data[1]);
				values.put(LocalItemTable.lon.getName(), data[2]);
				values.put(LocalItemTable.lat.getName(), data[3]);
				values.put(LocalItemTable.auther.getName(), data[4]);
				values.put(LocalItemTable.createTime.getName(), data[5]);

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
				val.setMessage(cursor.getString(LocalItemTable.message.getColNo()));
				val.setLon(Double.valueOf(cursor.getString(LocalItemTable.lon.getColNo())).doubleValue());
				val.setLat(Double.valueOf(cursor.getString(LocalItemTable.lat.getColNo())).doubleValue());
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
}
