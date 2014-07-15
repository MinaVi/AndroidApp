package com.sw.minavi.activity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteDB操作に関するヘルパークラス
 * @author RIOH
 *
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

	/** データベース名の定数 */
	private static final String DB_NAME = "WALKER_DB";

	/** テーブル定義情報 */
	public enum Tables {
		/** local_item_tblの定義情報 */
		localItemTable ("local_item_tbl", 0);

		/** テーブル名称 */
		private final String name;
		/** テーブル番号 */
		private final int id;

		/**
		 * コンストラクタ
		 * @param name テーブル名称
		 * @param id テーブル番号
		 */
		private Tables(String name, int id) {
			this.name = name;
			this.id = id;
		}

		/**
		 * テーブル名称を取得します
		 * @return テーブル名称
		 */
		public String getName() {
			return name;
		}

		/**
		 * テーブル番号を取得します
		 * @return テーブル番号
		 */
		public int getId() {
			return id;
		}
	}

	/** local_item_tblの定義情報 */
	public enum LocalItemTable {
		/** テーブル項目:id */
		id ("id", 0),
		/** テーブル項目:message */
		message ("message", 1),
		/** テーブル項目:lon */
		lon ("lon", 2),
		/** テーブル項目:lat */
		lat ("lat", 3),
		/** テーブル項目:image_path */
		image_path ("image_path", 4),
		/** テーブル項目:auther */
		auther ("auther", 5),
		/** テーブル項目:createTime */
		createTime ("createTime", 6);

		/** 項目名称 */
		private final String name;
		/** 項目番号 */
		private final int colNo;

		/**
		 * コンストラクタ
		 * @param name 項目名称
		 * @param colNo 項目番号
		 */
		private LocalItemTable(String name, int colNo) {
			this.name = name;
			this.colNo = colNo;
		}

		/**
		 * 項目名称を取得します
		 * @return 項目名称
		 */
		public String getName() {
			return name;
		}

		/**
		 * 項目番号を取得します
		 * @return 項目番号
		 */
		public int getColNo() {
			return colNo;
		}
	}

	/**
	 * コンストラクタ
	 * @param context コンテキスト
	 */
	public DatabaseOpenHelper(Context context) {
		// 指定したデータベース名が存在しない場合は、新たに作成されonCreate()が呼ばれる
		// バージョンを変更するとonUpgrade()が呼ばれる
		super(context, DB_NAME, null, 1);
	}

	/**
	 * コンストラクタ
	 * @param context コンテキスト
	 * @param name DB名称
	 * @param factory
	 * @param version
	 */
	public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/** create文の部品 */
	private static String SQL_CREATE_TBL = "create table %s (";
	/** create文の部品 */
	private static String SQL_CREATE_TBL_SET_PRIMARYS = " %s %s primary key";
	/** create文の部品 */
	private static String SQL_CREATE_TBL_SET_NOTNULL = " %s %s NOT NULL ";
	/** create文の部品 */
	private static String SQL_CREATE_TBL_SET_PLANE = " %s %s ";

	@Override
	public void onCreate(SQLiteDatabase db) {
		// トランザクションの開始
		db.beginTransaction();

		try {

			// local_item_tblを作成
			StringBuilder createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.localItemTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { LocalItemTable.id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.message.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.lon.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.lat.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { LocalItemTable.image_path.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { LocalItemTable.auther.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.createTime.getName(), "text", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());

			// コミット
			db.setTransactionSuccessful();
		} finally {

			// トランザクションの終了
			db.endTransaction();
		}
	}

	/**
	 * データベースの更新<br/>
	 * 親クラスのコンストラクタに渡すversionを変更したときに呼び出される
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 未定義
	}

}
