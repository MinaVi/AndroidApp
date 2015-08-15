package com.sw.minavi.activity.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sw.minavi.activity.db.DatabaseOpenHelper.Tables;
import com.sw.minavi.activity.db.DatabaseOpenHelper.TalkEventsTable;
import com.sw.minavi.item.TalkEvent;

/**
 * local_item_tblに関するデータ抽出・追加・更新・削除機能を提供するクラス
 * @author RIOH
 *
 */
public class TalkEventsTableManager {

	/** SQLiteOpenHelper継承クラス */
	private DatabaseOpenHelper helper = null;
	/** 当該クラスが提供する唯一のインスタンス */
	private static TalkEventsTableManager me = null;

	/**
	 * プライベートコンストラクタ
	 */
	private TalkEventsTableManager() {
	}

	/**
	 * コンストラクタ
	 * @param helper SQLiteOpenHelper継承クラス
	 */
	private TalkEventsTableManager(DatabaseOpenHelper helper) {
		this.helper = helper;
	}

	/**
	 * 当該クラス唯一のインスタンスを取得する
	 * @param helper SQLiteOpenHelper継承クラス
	 * @return 当該クラス唯一のインスタンス
	 */
	public static TalkEventsTableManager getInstance(DatabaseOpenHelper helper) {
		if (me == null) {
			me = new TalkEventsTableManager(helper);
		}
		return me;
	}

	/**
	 * サンプルデータの登録
	 */
	public void InsertSample() {

		// サンプルデータの準備
		String[][] datas = new String[][] {
				/*
				{ "1", "1", "ミナ","こんにちは！" , "mina2_nomal", "0" , "0"},
				{ "2", "1", "ミナ", "始めまして\nミナと言います。" , "mina2_nomal", "0" , "0"},
				{ "3", "1", "ミナ", "これから北海道を、\n色々案内しますね。" , "mina2_smile", "0" , "0"},
				{ "4", "1", "ミナ", "よろしくお願いいたします。" , "mina2_smile", "0" , "0"},
				{ "5", "2", "ミナ", "ミナです！" , "mina2_nomal", "0" , "0"},
				{ "6", "2", "ナミ", "ナミです！" , "nami1_smile", "1" , "0"},
				{ "7", "2", "ミナ", "二人合わせて！" , "mina2_smile", "0" , "0"},
				{ "8", "2", "ナミ", "…え、なにそれ？" , "nami1_nomal", "1" , "0"},
				{ "9", "2", "ミナ", "あれ？ナミちゃんノリ悪いよ～" , "mina2_marume_ase", "0" , "0"},
				{ "10", "2", "ナミ", "無茶ぶりすぎ！" , "nami1_sad", "1" , "0"},
				{ "11", "3", "ミナ", "今日はいい天気ですね～" , "mina2_nomal", "0" , "0"},
				{ "12", "3", "ミナ", "こう天気がいいと\nぶらっと遠出したくなりますよね～" , "mina2_smile", "0" , "0"},
				{ "13", "4", "ミナ", "やっぱりそうですよね～" , "mina2_smile", "" , "0"},
				{ "14", "4", "ミナ", "こんな日は美瑛とか富良野とか\n行きたいですよね～" , "mina2_smile", "0" , "0"},
				{ "15", "5", "ミナ", "あれ、\nもしかして{0}さんはインドア派ですか？" , "mina2_nomal", "0" , "0"},
				{ "16", "5", "ミナ", "それとも…実は晴れてると\n思ってるの私だけですか？" , "mina2_nomal", "0" , "0"},
				{ "17", "6", "ミナ", "ところで質問ですが、" , "mina2_nomal", "" , "0"},
				{ "18", "6", "ミナ", "北海道と言えば\n何を連想しますか？" , "mina2_nomal", "" , "0"},
				{ "19", "7", "ミナ", "やはり北海道と言えば\n大自然ですよね！" , "mina2_smile", "" , "0"},
				{ "20", "7", "ミナ", "草原や森林はもとより、\n広大な湿原、" , "mina2_nomal", "" , "0"},
				{ "21", "7", "ミナ", "大雪山に代表される山々、" , "mina2_nomal", "" , "0"},
				{ "22", "7", "ミナ", "世界遺産にもなった知床半島、" , "mina2_nomal", "" , "0"},
				{ "23", "7", "ミナ", "自然の神秘、\n青い池などはとても魅力的ですよね。" , "mina2_nomal", "" , "0"},
				{ "24", "8", "ミナ", "本当においしいんですよね！\nかにとかうにとか、" , "mina2_smile", "" , "0"},
				{ "25", "8", "ミナ", "海鮮がやはり有名ですが,\nジンギスカンや鹿肉、" , "mina2_nomal", "" , "0"},
				{ "26", "8", "ミナ", "大自然でとれた農作物も\nとてもおいしいです！" , "mina2_smile", "" , "0"},
				{ "27", "8", "ミナ", "とてもじゃないですけど\nずっと住んでないと食べきれないほどです！" , "mina2_nomal", "" , "0"},
				{ "28", "9", "ミナ", "これは意外なお答えですね？" , "mina2_nomal", "" , "0"},
				{ "29", "9", "ミナ", "北海道弁で有名なのは\n「なまら」や「～しょ」ですが、" , "mina2_nomal", "" , "0"},
				{ "30", "9", "ミナ", "その他にもいろいろありますので、\nぜひ耳を傾けて探してみてください" , "mina2_nomal", "" , "0"},
				{ "31", "9", "ミナ", "あ、北海道かるた等はお土産やさんにもあるので\n一見の価値ありです！" , "mina2_nomal", "" , "0"},
				{ "32", "10", "ミナ", "えー…っと\nそれは沖縄の方言ですよ。" , "mina2_marume_ase", "" , "0"},
				{ "33", "10", "ミナ", "北海道風にいうと…" , "mina2_nomal", "" , "0"},
				{ "34", "10", "ミナ", "『なんとかなるっしょ！』" , "mina2_nomal", "" , "0"},
				{ "35", "10", "ミナ", "ですかね～。" , "mina2_nomal", "" , "0"},
				
				{ "36", "11", "ミナ","ここは北海道大学です。" , "mina2_nomal", "0" , "0"},
				{ "37", "11", "ミナ", "文学研究科地域分析室に近いですね。。" , "mina2_nomal", "0" , "0"},
				
				{ "38", "12", "ミナ","地域分析しつではGISをはじめ\n様々なシステムを利用しています。" , "mina2_nomal", "0" , "0"},
				{ "39", "12", "ミナ", "実は私の生まれ故郷でもあるんですよ。" , "mina2_smile", "0" , "0"},

				{ "40", "13", "ミナ","近くに何かあるみたいですよ。調べてみますか？。" , "mina2_nomal", "0" , "0"},
				{ "41", "14", "ミナ","ARカメラを起動します。" , "mina2_nomal", "0" , "0"},
				{ "42", "15", "ミナ","わかりました。" , "mina2_nomal", "0" , "0"},
				*/
				{ "1", "1", "リセ","ここは茶房ヌプリという喫茶店よ。" , "rise_sammar_nomal_up", "0" , "0"},
				{ "2", "1", "リセ", "ニセコの喫茶店はいろいろあるけどここは駅の中にあるから、" , "rise_sammar_nomal_up", "0" , "0"},
				{ "3", "1", "リセ", "電車で来た時に、すぐ一息つけるわ。" , "rise_sammar_nomal_up", "0" , "0"},
				{ "4", "1", "リセ", "もちろん珈琲やケーキとかあるけど、私のおすすめは「ヌプリカレー」ね。" , "rise2_sammar_smile_up", "0" , "0"},
				{ "5", "1", "リセ", "すごくコクが合って、それでいてまろやか…って言ってたら食べたくなっちゃった。" , "rise_sammar_smile_up", "0" , "0"},
				{ "6", "1", "リセ", "あと、店内にはたくさんの時計があるけど、どれも同じ時間で止まっているのよ。" , "rise_sammar_nomal_up", "0" , "0"},
				{ "7", "1", "リセ", "・・・" , "rise_sammar_sad_up", "0" , "0"},
				{ "8", "1", "リセ", "あの時間が何を指しているのか、興味があるならお店の人にたずねてみるといいよ。" , "rise_sammar_nomal_up", "0" , "0"},
				
				{ "9", "2", "ミナ", "ここは？もしかして民家に迷い込んで・・・" , "mina_nomal_up", "0" , "0"},
				{ "10", "2", "リセ", "いいえ、ここはそば処「楽一」よ。まぁ、入り口に小さい旗が立っているだけだから、間違えるのも無理はないけどね。" 
					, "rise_winter_nomal_up", "1" , "0"},
				{ "11", "2", "リセ", "ここのそばは注文されてから打ち始めるから、実際に食べられるまで時間があるの。" , "rise_winter_nomal_up", "1" , "0"},
				{ "12", "2", "リセ", "その間、家族や仲間と話したり、お酒を飲んだりして時間の流れを楽しむのよ。" , "rise2_winter_smile_up", "1" , "0"},
				{ "13", "2", "リセ", "そばはニセコの清水を利用していて、のどごしがすごくいいの。" , "rise_winter_smile_up", "1" , "0"},
				{ "14", "2", "ミナ", "それは是非食べてみたいですね！" , "mina_smile_up", "0" , "0"},
				{ "15", "2", "リセ", "夜は懐石料理がでるけど、私にはとても高くてまだ食べたことがないの。でもいつか食べに来ようと思ってるわ！" 
					, "rise_winter_nigawarai_up", "1" , "0"},
				{ "16", "2", "ミナ", "それは楽しみですね♪" , "mina_smile_up", "0" , "0"},
				
				{ "17", "3", "ナミ", "ここで問題です！" , "nami_glass_smile_up", "0" , "0"},
				{ "18", "3", "ナミ", "現在ではニセコバスの本社や、レジャー関連会社の倉庫になっていますが、" , "nami_glass_nomal_up", "0" , "0"},
				{ "19", "3", "ナミ", "元々は何を貯蔵するための倉庫だったでしょうか？" , "nami_glass_nomal_up", "0" , "0"},
				
				{ "20", "4", "ナミ", "正解です！" , "nami_glass_smile_up", "0" , "0"},
				{ "21", "4", "ナミ", "もともと秋に収穫した米を貯蔵していたこの倉庫ですが、米倉庫自体は郊外に移転しました。" , "nami_glass_nomal_up", "0" , "0"},
				{ "22", "4", "ナミ", "でも歴史的価値も高いので保存し、現在では観光資源として再利用する動きが出ています。" , "nami_glass_nomal_up", "0" , "0"},
				
				{ "23", "5", "ナミ", "残念、不正解です。" , "nami_glass_gakkari_up", "0" , "0"},
				{ "24", "5", "ナミ", "もともと秋に収穫した米を貯蔵していたこの倉庫ですが、米倉庫自体は郊外に移転しました。" , "nami_glass_nomal_up", "0" , "0"},
				{ "25", "5", "ナミ", "でも歴史的価値も高いので保存し、現在では観光資源として再利用する動きが出ています。" , "nami_glass_nomal_up", "0" , "0"},
				
		};

		// 書き込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getWritableDatabase();

		try {

			// トランザクションの開始
			sqliteDB.beginTransaction();

			sqliteDB.delete(Tables.talkEventsTable.getName(), null, null);
			for (String[] data : datas) {
				// insertするデータを作成する
				// talk_group_id項目については自動採番なので用意する必要はない
				ContentValues values = new ContentValues();
				values.put(TalkEventsTable.talk_event_id.getName(), data[TalkEventsTable.talk_event_id.getColNo()]);
				values.put(TalkEventsTable.talk_group_id.getName(), data[TalkEventsTable.talk_group_id.getColNo()]);
				values.put(TalkEventsTable.talk_name.getName(), data[TalkEventsTable.talk_name.getColNo()]);
				values.put(TalkEventsTable.talk_body.getName(), data[TalkEventsTable.talk_body.getColNo()]);
				values.put(TalkEventsTable.image_file_name.getName(), data[TalkEventsTable.image_file_name.getColNo()]);
				values.put(TalkEventsTable.image_position_type.getName(), data[TalkEventsTable.image_position_type.getColNo()]);
				values.put(TalkEventsTable.image_animation_type.getName(), data[TalkEventsTable.image_animation_type.getColNo()]);

				// insert
				sqliteDB.insert(Tables.talkEventsTable.getName(), null, values);
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
	public ArrayList<TalkEvent> GetRecords(int groupId) {
		ArrayList<TalkEvent> values = new ArrayList<TalkEvent>();
		Cursor cursor = null;

		// 読み込み用のDBオブジェクトを取得
		SQLiteDatabase sqliteDB = helper.getReadableDatabase();

		try {
			// select
			cursor = sqliteDB.rawQuery("SELECT * FROM talk_events_tbl WHERE talk_group_id = ?", new String[]{String.valueOf(groupId)});

			// 取得したデータをレコード毎に処理する
			while (cursor.moveToNext()) {

				// TalkEventsTableの生成・編集
				TalkEvent val = new TalkEvent();
				val.setTalkEventId(cursor.getInt(TalkEventsTable.talk_event_id.getColNo()));
				val.setTalkGroupId(cursor.getInt(TalkEventsTable.talk_group_id.getColNo()));
				val.setTalkName(cursor.getString(TalkEventsTable.talk_name.getColNo()));
				val.setTalkBody(cursor.getString(TalkEventsTable.talk_body.getColNo()));
				val.setImageFileName(cursor.getString(TalkEventsTable.image_file_name.getColNo()));
				val.setImagePositionType(cursor.getInt(TalkEventsTable.image_position_type.getColNo()));
				val.setImageAnimationType(cursor.getInt(TalkEventsTable.image_animation_type.getColNo()));
				

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
