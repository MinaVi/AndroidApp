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
		localItemTable ("local_item_tbl", 0),

		/** talk_groups_tblの定義情報 */
		talkGroupsTable ("talk_groups_tbl", 1),

		/** talk_events_tblの定義情報 */
		talkEventsTable ("talk_events_tbl", 2),

		/** talk_selects_tblの定義情報 */
		talkSelectsTable ("talk_selects_tbl", 3),
		
		/** settings_tblの定義情報 */
		settingsTable ("settings_tbl", 4),

		/** story_groups_tblの定義情報 */
		storyGroupsTable ("story_groups_tbl", 5),

		/** story_events_tblの定義情報 */
		storyEventsTable ("story_events_tbl", 6),

		/** story_selects_tblの定義情報 */
		storySelectsTable ("story_selects_tbl", 7);
		

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
		/** テーブル項目:talk_group_id */
		talk_group_id ("talk_group_id", 1),
		/** テーブル項目:story_group_id */
		story_group_id ("story_group_id", 2),
		/** テーブル項目:message */
		message ("message", 3),
		/** テーブル項目:lon */
		lon ("lon", 4),
		/** テーブル項目:lat */
		lat ("lat", 5),
		/** テーブル項目:ar_image_name */
		ar_image_name ("ar_image_name", 6),
		/** テーブル項目:auther */
		auther ("auther", 7),
		/** テーブル項目:createTime */
		createTime ("createTime", 8);

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
	
	/** talk_groups_tblの定義情報 */
	public enum TalkGroupsTable {
		/** テーブル項目:talk_group_id */
		talk_group_id ("talk_group_id", 0),
		/** テーブル項目:area_id */
		area_id ("area_id", 1),
		/** テーブル項目:local_area_id */
		local_area_id ("local_area_id", 2),
		/** テーブル項目:background_file_name */
		background_file_name ("background_file_name", 3),
		/** テーブル項目:select_flg */
		select_flg ("select_flg", 4);

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
		talk_event_id ("talk_event_id", 0),
		/** テーブル項目:talk_group_id */
		talk_group_id ("talk_group_id", 1),
		/** テーブル項目:talk_name */
		talk_name ("talk_name", 2),
		/** テーブル項目:talk_body */
		talk_body ("talk_body", 3),
		/** テーブル項目:image_file_name */
		image_file_name ("image_file_name", 4),
		/** テーブル項目:image_position_type */
		image_position_type ("image_position_type", 5),
		/** テーブル項目:image_animation_type */
		image_animation_type ("image_animation_type", 6);

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
		talk_select_id ("talk_select_id", 0),
		/** テーブル項目:talk_group_id */
		talk_group_id ("talk_group_id", 1),
		/** テーブル項目:answers_count */
		answers_count ("answers_count", 2),
		/** テーブル項目:first_answer_body */
		first_answer_body ("first_answer_body", 3),
		/** テーブル項目:first_talk_group_id */
		first_talk_group_id ("first_talk_group_id", 4),
		/** テーブル項目:second_answer_body */
		second_answer_body ("second_answer_body", 5),
		/** テーブル項目:second_talk_group_id */
		second_talk_group_id ("second_talk_group_id", 6),
		/** テーブル項目:third_answer_body */
		third_answer_body ("third_answer_body", 7),
		/** テーブル項目:third_talk_group_id */
		third_talk_group_id ("third_talk_group_id", 8),
		/** テーブル項目:forth_answer_body */
		forth_answer_body ("forth_answer_body", 9),
		/** テーブル項目:forth_talk_group_id */
		forth_talk_group_id ("forth_talk_group_id", 10);

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
		setting_id ("setting_id", 0),
		/** テーブル項目:key */
		key ("key", 1),
		/** テーブル項目:value */
		value ("value", 1);

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
	
	
	/** talk_groups_tblの定義情報 */
	public enum StoryGroupsTable {
		/** テーブル項目:story_group_id */
		story_group_id ("story_group_id", 0),
		/** テーブル項目:area_id */
		area_id ("area_id", 1),
		/** テーブル項目:local_area_id */
		local_area_id ("local_area_id", 2),
		/** テーブル項目:background_file_name */
		background_file_name ("background_file_name", 3),
		/** テーブル項目:select_flg */
		select_flg ("select_flg", 4),
		/** テーブル項目:is_read */
		is_read ("is_reead", 5),;

		/** 項目名称 */
		private final String name;
		/** 項目番号 */
		private final int colNo;

		/**
		 * コンストラクタ
		 * @param name 項目名称
		 * @param colNo 項目番号
		 */
		private StoryGroupsTable(String name, int colNo) {
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
	public enum StoryEventsTable {
		/** テーブル項目:story_event_id */
		story_event_id ("story_event_id", 0),
		/** テーブル項目:story_group_id */
		story_group_id ("story_group_id", 1),
		/** テーブル項目:talk_name */
		talk_name ("talk_name", 2),
		/** テーブル項目:talk_body */
		talk_body ("talk_body", 3),
		/** テーブル項目:image_file_name */
		image_file_name ("image_file_name", 4),
		/** テーブル項目:image_position_type */
		image_position_type ("image_position_type", 5),
		/** テーブル項目:image_animation_type */
		image_animation_type ("image_animation_type", 6);

		/** 項目名称 */
		private final String name;
		/** 項目番号 */
		private final int colNo;

		/**
		 * コンストラクタ
		 * @param name 項目名称
		 * @param colNo 項目番号
		 */
		private StoryEventsTable(String name, int colNo) {
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
	public enum StorySelectsTable {
		/** テーブル項目:story_select_id */
		story_select_id ("story_select_id", 0),
		/** テーブル項目:story_group_id */
		story_group_id ("story_group_id", 1),
		/** テーブル項目:answers_count */
		answers_count ("answers_count", 2),
		/** テーブル項目:first_answer_body */
		first_answer_body ("first_answer_body", 3),
		/** テーブル項目:first_story_group_id */
		first_story_group_id ("first_story_group_id", 4),
		/** テーブル項目:second_answer_body */
		second_answer_body ("second_answer_body", 5),
		/** テーブル項目:second_story_group_id */
		second_story_group_id ("second_story_group_id", 6),
		/** テーブル項目:third_answer_body */
		third_answer_body ("third_answer_body", 7),
		/** テーブル項目:third_story_group_id */
		third_story_group_id ("third_story_group_id", 8),
		/** テーブル項目:forth_answer_body */
		forth_answer_body ("forth_answer_body", 9),
		/** テーブル項目:forth_talk_story_id */
		forth_story_group_id ("forth_story_group_id", 10);

		/** 項目名称 */
		private final String name;
		/** 項目番号 */
		private final int colNo;

		/**
		 * コンストラクタ
		 * @param name 項目名称
		 * @param colNo 項目番号
		 */
		private StorySelectsTable(String name, int colNo) {
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
			
			// talk_groups_tbleを作成
			StringBuilder createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.settingsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { SettingsTable.setting_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { SettingsTable.key.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { SettingsTable.value.getName(), "integer", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());

			// local_item_tblを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.localItemTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { LocalItemTable.id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.talk_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.story_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.message.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.lon.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.lat.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { LocalItemTable.ar_image_name.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { LocalItemTable.auther.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { LocalItemTable.createTime.getName(), "text", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());
						
			// talk_groups_tbleを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.talkGroupsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { TalkGroupsTable.talk_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.area_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.local_area_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkGroupsTable.background_file_name.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkGroupsTable.select_flg.getName(), "integer", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());
						
			
			// talk_event_tblを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.talkEventsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { TalkEventsTable.talk_event_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkEventsTable.talk_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.talk_name.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.talk_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.image_file_name.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.image_position_type.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkEventsTable.image_animation_type.getName(), "text", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());
			

			// talk_selects_tblを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.talkSelectsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { TalkSelectsTable.talk_select_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.talk_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.answers_count.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkSelectsTable.first_answer_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.first_talk_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkSelectsTable.second_answer_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.second_talk_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkSelectsTable.third_answer_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.third_talk_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { TalkSelectsTable.forth_answer_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { TalkSelectsTable.forth_talk_group_id.getName(), "integer", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());

			// talk_groups_tbleを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.storyGroupsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { StoryGroupsTable.story_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StoryGroupsTable.area_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StoryGroupsTable.local_area_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StoryGroupsTable.background_file_name.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StoryGroupsTable.select_flg.getName(), "integer", }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StoryGroupsTable.is_read.getName(), "integer", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());
						
			
			// talk_event_tblを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.storyEventsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { StoryEventsTable.story_event_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StoryEventsTable.story_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StoryEventsTable.talk_name.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StoryEventsTable.talk_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StoryEventsTable.image_file_name.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StoryEventsTable.image_position_type.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StoryEventsTable.image_animation_type.getName(), "text", }));
			createSql.append(");");

			// SQLの発行
			db.execSQL(createSql.toString());
			

			// talk_selects_tblを作成
			createSql = new StringBuilder();
			createSql.append(String.format(SQL_CREATE_TBL, new Object[] { Tables.storySelectsTable.getName() }));
			createSql.append(String.format(SQL_CREATE_TBL_SET_PRIMARYS, new Object[] { StorySelectsTable.story_select_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StorySelectsTable.story_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StorySelectsTable.answers_count.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StorySelectsTable.first_answer_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StorySelectsTable.first_story_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StorySelectsTable.second_answer_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StorySelectsTable.second_story_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StorySelectsTable.third_answer_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StorySelectsTable.third_story_group_id.getName(), "integer", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_PLANE, new Object[] { StorySelectsTable.forth_answer_body.getName(), "text", })).append(",");
			createSql.append(String.format(SQL_CREATE_TBL_SET_NOTNULL, new Object[] { StorySelectsTable.forth_story_group_id.getName(), "integer", }));
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
