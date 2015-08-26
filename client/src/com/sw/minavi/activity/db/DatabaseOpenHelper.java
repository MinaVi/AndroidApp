package com.sw.minavi.activity.db;

import com.sw.minavi.item.EmergencyItem;

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
		localItemTable("local_item_tbl", 0),

		/** talk_groups_tblの定義情報 */
		talkGroupsTable("talk_groups_tbl", 1),

		/** talk_events_tblの定義情報 */
		talkEventsTable("talk_events_tbl", 2),

		/** talk_selects_tblの定義情報 */
		talkSelectsTable("talk_selects_tbl", 3),

		/** settings_tblの定義情報 */
		settingsTable("settings_tbl", 4),
		
		/** local_item_tblの定義情報 */
		emergencyItemTable("emergency_item_tbl", 5);

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
		ID,
		/** テーブル項目:talk_group_id */
		TALK_GROUP_ID,
		/** テーブル項目:message */
		MESSAGE,
		/** テーブル項目:message_en */
		MESSAGE_EN,
		/** テーブル項目:lon */
		LON,
		/** テーブル項目:lat */
		LAT,
		/** テーブル項目:ar_image_name */
		AR_IMAGE_NAME,
		/** テーブル項目:auther */
		ICON_IMAGE_NAME,
		/** テーブル項目:lon */
		SPECIAL_LON_MIN,
		/** テーブル項目:lat */
		SPECIAL_LAT_MIN,
		/** テーブル項目:lon */
		SPECIAL_LON_MAX,
		/** テーブル項目:lat */
		SPECIAL_LAT_MAX,
		/** テーブル項目:createTime */
		CREATE_TIME;

	}

	/** talk_groups_tblの定義情報 */
	public enum TalkGroupsTable {
		/** テーブル項目:talk_group_id */
		talk_group_id("talk_group_id", 0),
		/** テーブル項目:area_id */
		area_id("area_id", 1),
		/** テーブル項目:local_area_id */
		local_area_id("local_area_id", 2),
		/** テーブル項目:background_file_name */
		background_file_name("background_file_name", 3),
		/** テーブル項目:select_flg */
		select_flg("select_flg", 4),
		/** テーブル項目:select_flg */
		next_group_id("next_group_id", 5),
		/** テーブル項目:show_memory_flg */
		show_memory_flg("show_memory_flg", 6),
		/** テーブル項目:enabled_flg */
		is_enabled("is_enabled", 7),
		/** テーブル項目:is_read */
		is_read("is_read", 8);

		/** 項目名称 */
		private final String name;
		/** 項目番号 */
		private final int colNo;

		/**
		 * コンストラクタ
		 * @param name 項目名称
		 * @param colNo 項目番号
		 */
		private TalkGroupsTable(String name, int colNo) {
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

	/** local_item_tblの定義情報 */
	public enum TalkEventsTable {
		/** テーブル項目:talk_event_id */
		talk_event_id("talk_event_id", 0),
		/** テーブル項目:talk_group_id */
		talk_group_id("talk_group_id", 1),
		/** テーブル項目:talk_name */
		talk_name("talk_name", 2),
		/** テーブル項目:talk_name */
		talk_name_en("talk_name_en", 3),
		/** テーブル項目:talk_body */
		talk_body("talk_body", 4),
		/** テーブル項目:talk_body */
		talk_body_en("talk_body_en", 5),
		/** テーブル項目:image_file_name */
		image_file_name("image_file_name", 6),
		/** テーブル項目:image_position_type */
		image_position_type("image_position_type", 7),
		/** テーブル項目:image_animation_type */
		image_animation_type("image_animation_type", 8);

		/** 項目名称 */
		private final String name;
		/** 項目番号 */
		private final int colNo;

		/**
		 * コンストラクタ
		 * @param name 項目名称
		 * @param colNo 項目番号
		 */
		private TalkEventsTable(String name, int colNo) {
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

	/** talk_selects_tblの定義情報 */
	public enum TalkSelectsTable {
		/** テーブル項目:talk_select_id */
		talk_select_id("talk_select_id", 0),
		/** テーブル項目:talk_group_id */
		talk_group_id("talk_group_id", 1),
		/** テーブル項目:answers_count */
		answers_count("answers_count", 2),
		/** テーブル項目:first_answer_body */
		first_answer_body("first_answer_body", 3),
		/** テーブル項目:first_talk_group_id */
		first_talk_group_id("first_talk_group_id", 4),
		/** テーブル項目:second_answer_body */
		second_answer_body("second_answer_body", 5),
		/** テーブル項目:second_talk_group_id */
		second_talk_group_id("second_talk_group_id", 6),
		/** テーブル項目:third_answer_body */
		third_answer_body("third_answer_body", 7),
		/** テーブル項目:third_talk_group_id */
		third_talk_group_id("third_talk_group_id", 8),
		/** テーブル項目:forth_answer_body */
		forth_answer_body("forth_answer_body", 9),
		/** テーブル項目:forth_talk_group_id */
		forth_talk_group_id("forth_talk_group_id", 10);

		/** 項目名称 */
		private final String name;
		/** 項目番号 */
		private final int colNo;

		/**
		 * コンストラクタ
		 * @param name 項目名称
		 * @param colNo 項目番号
		 */
		private TalkSelectsTable(String name, int colNo) {
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

	/** SettingsTableの定義情報 */
	public enum SettingsTable {
		/** テーブル項目:setting_id */
		setting_id("setting_id", 0),
		/** テーブル項目:key */
		key("key", 1),
		/** テーブル項目:value */
		value("value", 1);

		/** 項目名称 */
		private final String name;
		/** 項目番号 */
		private final int colNo;

		/**
		 * コンストラクタ
		 * @param name 項目名称
		 * @param colNo 項目番号
		 */
		private SettingsTable(String name, int colNo) {
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
	
	/** emergency_item_tblの定義情報 */
	public enum EmergencyItemTable {
		/** テーブル項目:id */
		ID,
		/** テーブル項目:talk_group_id */
		TALK_GROUP_ID,
		/** テーブル項目:message */
		MESSAGE,
		/** テーブル項目:message_en */
		MESSAGE_EN,
		/** テーブル項目:lon */
		LON,
		/** テーブル項目:lat */
		LAT,
		/** テーブル項目:ar_image_name */
		AR_IMAGE_NAME,
		/** テーブル項目:auther */
		ICON_IMAGE_NAME,
		/** テーブル項目:lon */
		SPECIAL_LON_MIN,
		/** テーブル項目:lat */
		SPECIAL_LAT_MIN,
		/** テーブル項目:lon */
		SPECIAL_LON_MAX,
		/** テーブル項目:lat */
		SPECIAL_LAT_MAX,
		/** テーブル項目:createTime */
		CREATE_TIME;

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

			// talk_groups_tbleを作成
			StringBuilder createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL,
					new Object[] { Tables.settingsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS,
					new Object[] { SettingsTable.setting_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE,
					new Object[] { SettingsTable.key.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE,
					new Object[] { SettingsTable.value.getName(), "integer", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());

			// local_item_tblを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.localItemTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { LocalItemTable.ID.toString(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.TALK_GROUP_ID.toString(), "integer", })).append(
					",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.MESSAGE.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.MESSAGE_EN.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.LON.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.LAT.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { LocalItemTable.AR_IMAGE_NAME.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { LocalItemTable.ICON_IMAGE_NAME.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.SPECIAL_LON_MIN.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.SPECIAL_LAT_MIN.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.SPECIAL_LON_MAX.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.SPECIAL_LAT_MAX.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.CREATE_TIME.toString(), "text", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());

			// talk_groups_tbleを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.talkGroupsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { TalkGroupsTable.talk_group_id.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.area_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.local_area_id.getName(), "integer", })).append(
					",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkGroupsTable.background_file_name.getName(), "text", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.select_flg.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.next_group_id.getName(), "integer", })).append(
					",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.is_enabled.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.show_memory_flg.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.is_read.getName(), "integer", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());

			// talk_event_tblを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.talkEventsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { TalkEventsTable.talk_event_id.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkEventsTable.talk_group_id.getName(), "integer", })).append(
					",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.talk_name.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.talk_name_en.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.talk_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.talk_body_en.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.image_file_name.getName(), "text", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.image_position_type.getName(), "text", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.image_animation_type.getName(), "text", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());

			// talk_selects_tblを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.talkSelectsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { TalkSelectsTable.talk_select_id.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.talk_group_id.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.answers_count.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkSelectsTable.first_answer_body.getName(), "text", })).append(
					",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.first_talk_group_id.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkSelectsTable.second_answer_body.getName(), "text", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.second_talk_group_id.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkSelectsTable.third_answer_body.getName(), "text", })).append(
					",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.third_talk_group_id.getName(), "integer", }))
					.append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkSelectsTable.forth_answer_body.getName(), "text", })).append(
					",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.forth_talk_group_id.getName(), "integer", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());
			
			// emergency_item_tblを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.emergencyItemTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { EmergencyItemTable.ID.toString(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.TALK_GROUP_ID.toString(), "integer", })).append(
					",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.MESSAGE.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.MESSAGE_EN.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.LON.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.LAT.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { EmergencyItemTable.AR_IMAGE_NAME.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { EmergencyItemTable.ICON_IMAGE_NAME.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.SPECIAL_LON_MIN.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.SPECIAL_LAT_MIN.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.SPECIAL_LON_MAX.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.SPECIAL_LAT_MAX.toString(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { EmergencyItemTable.CREATE_TIME.toString(), "text", }));
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
