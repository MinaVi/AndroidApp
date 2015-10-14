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
	/** 言語設定マネージャー */
	public static String lang = "Japanese";

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
				
				{ "1", "1", "ミナ", "MINA","こんにちは！", "Hello!" , "mina2_nomal", "0" , "0"},
				{ "2", "1", "ミナ", "MINA", "始めまして\nミナと言います。", "I am Mina." , "mina2_nomal", "0" , "0"},
				{ "3", "1", "ミナ", "MINA", "これからいろんなところを、\n案内させていただきます。", "I will guide the tourist destination." , "mina2_smile", "0" , "0"},
				{ "4", "1", "ミナ", "MINA", "よろしくお願いいたします。", "Thank you." , "mina2_smile", "0" , "0"},
				{ "5", "2", "ミナ", "MINA", "ミナです！", "I am Mina！" , "mina2_nomal", "0" , "0"},
				{ "6", "2", "ナミ", "NAMI", "ナミです！", "I am Nami!" , "nami1_smile", "1" , "0"},
				{ "7", "2", "ミナ", "MINA", "二人合わせて！", "Our name is conbined …" , "mina2_smile", "0" , "0"},
				{ "8", "2", "ナミ", "NAMI", "…え、なにそれ？", "What?" , "nami1_nomal", "1" , "0"},
				{ "9", "2", "ミナ", "MINA", "あれ？ナミちゃんノリ悪いよ～", "Hey, Adjust the breath!" , "mina2_marume_ase", "0" , "0"},
				{ "10", "2", "ナミ", "NAMI", "無茶ぶりすぎ！", "Reckless!" , "nami1_sad", "1" , "0"},
				{ "11", "3", "ミナ", "MINA", "今日はいい天気ですね～", "It is a good weather today." , "mina2_nomal", "0" , "0"},
				{ "12", "3", "ミナ", "MINA", "こう天気がいいと\nぶらっと遠出したくなりますよね～", "Why not want to outing?" , "mina2_smile", "0" , "0"},
				{ "13", "4", "ミナ", "MINA", "やっぱりそうですよね～", "Yes!" , "mina2_smile", "" , "0"},
				{ "14", "4", "ミナ", "MINA", "こんな日は美瑛とか富良野とか\n行きたいですよね～", "I think I want to go Biei ,Hurano." , "mina2_smile", "0" , "0"},
				{ "15", "5", "ミナ", "MINA", "あれ、\nもしかして{0}さんはインドア派ですか？", "{0} is indoor?" , "mina2_nomal", "0" , "0"},
				{ "16", "5", "ミナ", "MINA", "それとも…実は晴れてると\n思ってるの私だけですか？", "Or the weather is not sunny?" , "mina2_nomal", "0" , "0"},
				{ "17", "6", "ミナ", "MINA", "ところで質問ですが、", "Question!" , "mina2_nomal", "" , "0"},
				{ "18", "6", "ミナ", "MINA", "北海道と言えば\n何を連想しますか？", "Are you sure you want to associate with what Speaking of Hokkaido?" , "mina2_nomal", "" , "0"},
				{ "19", "7", "ミナ", "MINA", "やはり北海道と言えば\n大自然ですよね！", "It's also a big natural Speaking of Hokkaido!" , "mina2_smile", "" , "0"},
				{ "20", "7", "ミナ", "MINA", "草原や森林はもとより、\n広大な湿原、", "Grassland, Forest,Wetlands… " , "mina2_nomal", "" , "0"},
				{ "21", "7", "ミナ", "MINA", "大雪山に代表される山々、", "Mountains..." , "mina2_nomal", "" , "0"},
				{ "22", "7", "ミナ", "MINA", "世界遺産にもなった知床半島、", "Shiretoko..." , "mina2_nomal", "" , "0"},
				{ "23", "7", "ミナ", "MINA", "自然の神秘、\n青い池などはとても魅力的ですよね。", "I'm like AoiIke is very attractive" , "mina2_nomal", "" , "0"},
				{ "24", "8", "ミナ", "MINA", "本当においしいんですよね！\nかにとかうにとか、", "Really delicious, Crab...Sea urchin..." , "mina2_smile", "" , "0"},
				{ "25", "8", "ミナ", "MINA", "海鮮がやはり有名ですが,\nジンギスカンや鹿肉、", "Lamb meat..." , "mina2_nomal", "" , "0"},
				{ "26", "8", "ミナ", "MINA", "大自然でとれた農作物も\nとてもおいしいです！", "Crops that were caught in the wilderness is also very tasty!" , "mina2_smile", "" , "0"},
				{ "27", "8", "ミナ", "MINA", "とてもじゃないですけど\nずっと住んでないと食べきれないほどです！", "Much living in it is enough to not eat that it is not!" , "mina2_nomal", "" , "0"},
				{ "28", "9", "ミナ", "MINA", "これは意外なお答えですね？", "This would be surprising answer?" , "mina2_nomal", "" , "0"},
				{ "29", "9", "ミナ", "MINA", "北海道弁で有名なのは\n「なまら」や「～しょ」ですが、", "The famous in Hokkaido valve NAMARA" , "mina2_nomal", "" , "0"},
				{ "30", "9", "ミナ", "MINA", "その他にもいろいろありますので、\nぜひ耳を傾けて探してみてください", "Since there are other in various also, please look for it by all means listen" , "mina2_nomal", "" , "0"},
				{ "31", "9", "ミナ", "MINA", "あ、北海道かるた等はお土産やさんにもあるので\n一見の価値ありです！", "Hokkaido karuta is also a souvenir shop" , "mina2_nomal", "" , "0"},
				{ "32", "10", "ミナ", "MINA", "えー…っと\nそれは沖縄の方言ですよ。", "It is the Okinawan dialect" , "mina2_marume_ase", "" , "0"},
				{ "33", "10", "ミナ", "MINA", "北海道風にいうと…", "Referred to in Hokkaido style..." , "mina2_nomal", "" , "0"},
				{ "34", "10", "ミナ", "MINA", "『なんとかなるっしょ！』", "Somehow become Ssho!" , "mina2_nomal", "" , "0"},
				{ "35", "10", "ミナ", "MINA", "ですかね～。", "isn't it?" , "mina2_nomal", "" , "0"},
				
				{ "36", "11", "ミナ", "MINA","ここは北海道大学です。", "Here is the Hokkaido University" , "mina2_nomal", "0" , "0"},
				{ "37", "11", "ミナ", "MINA", "文学研究科地域分析室に近いですね。", "It is close to the Graduate School of Letters regional analysis room." , "mina2_nomal", "0" , "0"},
				
				{ "38", "12", "ミナ", "MINA","地域分析室ではGISをはじめ\n様々なシステムを利用しています。", "210 in makes use of the beginning various system GIS." , "mina2_nomal", "0" , "0"},
				{ "39", "12", "ミナ", "MINA", "実は私の生まれ故郷でもあるんですよ。", "Actually, it's also in my home." , "mina2_smile", "0" , "0"},

//				{ "40", "13", "ミナ","近くに何かあるみたいですよ。調べてみますか？。" , "mina2_nomal", "0" , "0"},
//				{ "41", "14", "ミナ","ARカメラを起動します。" , "mina2_nomal", "0" , "0"},
//				{ "42", "15", "ミナ","わかりました。" , "mina2_nomal", "0" , "0"},
				
				{ "40", "13", "リセ", "RISE","ここは茶房ヌプリという喫茶店よ。", "This cafe called Sabo Nupuri.", "rise_sammar_nomal_up", "0" , "0"},
				{ "41", "13", "リセ", "RISE", "ニセコの喫茶店はいろいろあるけどここは駅の中にあるから、", "Cafe in Niseko are various, this cafe is because there in the station," , "rise_sammar_nomal_up", "0" , "0"},
				{ "42", "13", "リセ", "RISE", "電車で来た時に、すぐ一息つけるわ。", "When you came by train, I'll put immediately breath." , "rise_sammar_nomal_up", "0" , "0"},
				{ "43", "13", "リセ", "RISE", "もちろん珈琲やケーキとかあるけど、私のおすすめは「ヌプリカレー」ね。", "Though there Toka coffee and cakes, I recommend I Nupuri curry." , "rise2_sammar_smile_up", "0" , "0"},
				{ "44", "13", "リセ", "RISE", "すごくコクが合って、それでいてまろやか…って言ってたら食べたくなっちゃった。", "There is a very full-bodied, yet it's mellow." , "rise_sammar_smile_up", "0" , "0"},
				{ "45", "13", "リセ", "RISE", "あと、店内にはたくさんの時計があるけど、どれも同じ時間で止まっているのよ。", "But the store there is a lot of clock, none No has stopped at the same time." , "rise_sammar_nomal_up", "0" , "0"},
				{ "46", "13", "リセ", "RISE", "・・・", "・・・" , "rise_sammar_sad_up", "0" , "0"},
				{ "47", "13", "リセ", "RISE", "あの時間が何を指しているのか、興味があるならお店の人にたずねてみるといいよ。", "Whether that time is what the points, it is good to try to ask the shop of people if you are interested." , "rise_sammar_nomal_up", "0" , "0"},
				
				{ "48", "14", "ミナ", "MINA", "ここは？もしかして民家に迷い込んで・・・", "here? ... Do you mean wandered into a house" , "mina_nomal_up", "0" , "0"},
				{ "49", "14", "リセ", "RISE", "いいえ、ここはそば処「楽一」よ。まぁ、入り口に小さい旗が立っているだけだから、間違えるのも無理はないけどね。", "No, This is Rakuichi, japanese moodle 'SOBA' restrant 'SOBA'." 
					, "rise_winter_nomal_up", "1" , "0"},
				{ "50", "14", "リセ", "RISE", "ここのそばは注文されてから打ち始めるから、実際に食べられるまで時間があるの。", "'SOBA' can be started out from being order" , "rise_winter_nomal_up", "1" , "0"},
				{ "51", "14", "リセ", "RISE", "その間、家族や仲間と話したり、お酒を飲んだりして時間の流れを楽しむのよ。", "In the meantime, you can talk with family and friends, and No enjoy the flow of the time by drinking sake." , "rise2_winter_smile_up", "1" , "0"},
				{ "52", "14", "リセ", "RISE", "そばはニセコの清水を利用していて、のどごしがすごくいいの。", "'SOBA' If you are using the Shimizu of Niseko, the is very good Nodogoshi." , "rise_winter_smile_up", "1" , "0"},
				{ "53", "14", "ミナ", "MINA", "それは是非食べてみたいですね！", "It It is by all means eat like!" , "mina_smile_up", "0" , "0"},
				{ "54", "14", "リセ", "RISE", "夜は懐石料理がでるけど、私にはとても高くてまだ食べたことがないの。でもいつか食べに来ようと思ってるわ！", "I evening out is kaiseki cuisine, is the never ate still to me is very high.", 
					"rise_winter_nigawarai_up", "1" , "0"},
				{ "55", "14", "ミナ", "MINA", "それは楽しみですね♪", "That's fun ♪" , "mina_smile_up", "0" , "0"},
				
//				{ "17", "3", "ナミ", "NAMI", "ここで問題です！", "Question!" , "nami_glass_smile_up", "0" , "0"},
//				{ "18", "3", "ナミ", "NAMI", "現在ではニセコバスの本社や、レジャー関連会社の倉庫になっていますが、", "Headquarters and of Nisekobasu in the current, but has become a warehouse of leisure-related company," , "nami_glass_nomal_up", "0" , "0"},
//				{ "19", "3", "ナミ", "NAMI", "元々は何を貯蔵するための倉庫だったでしょうか？", "Either would have been a warehouse for the storage of what originally?" , "nami_glass_nomal_up", "0" , "0"},
//				
//				{ "20", "4", "ナミ", "NAMI", "正解です！", "the correct answer!" , "nami_glass_smile_up", "0" , "0"},
//				{ "21", "4", "ナミ", "NAMI", "もともと秋に収穫した米を貯蔵していたこの倉庫ですが、米倉庫自体は郊外に移転しました。", "It is originally this warehouse that had been stored rice were harvested in the fall, but the US warehouse itself has moved to the suburbs." , "nami_glass_nomal_up", "0" , "0"},
//				{ "22", "4", "ナミ", "NAMI", "でも歴史的価値も高いので保存し、現在では観光資源として再利用する動きが出ています。", "But to save because the historical value is also high, and he has movement is now to be re-used as tourism resources." , "nami_glass_nomal_up", "0" , "0"},
//				
//				{ "23", "5", "ナミ", "NAMI", "残念、不正解です。", "Unfortunately, it is incorrect" , "nami_glass_gakkari_up", "0" , "0"},
//				{ "24", "5", "ナミ", "NAMI", "もともと秋に収穫した米を貯蔵していたこの倉庫ですが、米倉庫自体は郊外に移転しました。", "It is originally this warehouse that had been stored rice were harvested in the fall, but the US warehouse itself has moved to the suburbs." , "nami_glass_nomal_up", "0" , "0"},
//				{ "25", "5", "ナミ", "NAMI", "でも歴史的価値も高いので保存し、現在では観光資源として再利用する動きが出ています。", "But to save because the historical value is also high, and he has movement is now to be re-used as tourism resources." , "nami_glass_nomal_up", "0" , "0"},
	
		
				{ "56", "15", "ミナ", "MINA", "この辺りは江ノ島電鉄という私鉄が走っていますよ。", "This area has been running a private railway that Enoshima Electric Railway." , "mina_nomal_up", "0" , "0"},
				{ "57", "15", "ミナ", "MINA", "沿線には歴史ある名所や観光名所が多く点在しています。", "Historic attractions and tourist attractions have a lot dotted with wayside." , "mina_nomal_up", "0" , "0"},
				{ "58", "15", "ミナ", "MINA", "また時期によってはイベントもいろいろ開催されています。", "Also depending on the time also has been variously held events" , "mina_nomal_up", "0" , "0"},
				{ "59", "15", "ミナ", "MINA", "何度乗っても楽しめる！ぜひ江ノ島電鉄に乗って、沿線を満喫してみてくださいね。", "You can enjoy even ride again! Riding on all means Enoshima Electric Railway, but please try to enjoy the wayside." , "mina_smile_up", "0" , "0"},
				
				{ "60", "16", "リセ", "RISE", "うわ…大きいねぇ", "It's so Big..." , "rise_sammar_smile_up", "1" , "0"},
				{ "61", "16", "ミナ", "MINA", "ここが鎌倉で最も有名なスポットの一つ、鎌倉の大仏ですよ。", "Here is one of the most famous spot in Kamakura, is the Great Buddha of Kamakura." , "mina_nomal_up", "0" , "0"},
				{ "62", "16", "ミナ", "MINA", "いつ作られたのか、どういう経緯で作られたのか、まだまだ不明なことが多い大仏ですが、", "When it made was whether, to what was made in what circumstances, it is Buddha thing still unclear in many cases" , "mina_nomal_up", "0" , "0"},
				{ "63", "16", "ミナ", "MINA", "多くの人を引き付けているのは確かです。", "It is certainly has attracted a lot of people." , "mina_smile_up", "0" , "0"},
				{ "64", "16", "ミナ", "MINA", "地震や津波の被害にあったともいわれています。ここまで津波の被害があったということなんですね。", "It is also said that there was a victim of the earthquake and tsunami. It's such that there was a tsunami up to here." , "mina_nomal_up", "0" , "0"},
				{ "65", "16", "リセ", "RISE", "え？ここまで波がきたの？", "Huh? No waves came up to here?" , "rise_sammar_sad_up", "1" , "0"},				
				{ "66", "16", "ミナ", "MINA", "はい。ですから、このあたりも津波想定に入っているので、", "So, this area also has entered the tsunami expected" , "mina_nomal_up", "0" , "0"},
				{ "67", "16", "ミナ", "MINA", "アプリを「災害モード」にして、確認してみてください。", "In the application to the disaster mode, please try to check." , "mina_nomal_up", "0" , "0"},
				
				{ "68", "17", "ミナ", "MINA", "ここは源平池の近くですね。", "Here we are close to the Genpei pond." , "mina_nomal_up", "0" , "0"},
				{ "69", "17", "ミナ", "MINA", "源平池はその名の通り、源氏と平家を表した二つの池からなっています。", "Genpei pond as its name suggests, consists of two ponds, which represents the Genji and Heike." , "mina_nomal_up", "0" , "0"},
				{ "70", "17", "ミナ", "MINA", "紅、白、それぞれの蓮（ハス）がとてもきれいです。", "Red, white, each of lotus is very beautiful." , "mina_smile_up", "0" , "0"},
				{ "71", "17", "ミナ", "MINA", "蓮は仏教でも特別な意味を持つ植物でも知られています。", "Lotus is also known in plants that have special meaning in Buddhism." , "mina_nomal_up", "0" , "0"},
				{ "72", "17", "ミナ", "MINA", "一蓮托生、なんて言葉もありますよね。", "IchirenTakusyo, I think you also words." , "mina_nomal_up", "0" , "0"},
				{ "73", "17", "ミナ", "MINA", "紅蓮って地獄の名前でもなかったっけ？。", "The Kke was not a hell of a name I Guren?" , "rise_sammar_sad_up", "1" , "0"},
				{ "74", "17", "ミナ", "MINA", "なぜハーフでニセコ育ちの貴方がそんなに詳しいのですか…", "Why is your Niseko grow so much more in half" , "mina1_marume_ase", "0" , "0"},
				{ "75", "17", "ミナ", "MINA", "へっへーん！", "What is it!" , "rise_sammar_smile_up", "1" , "0"},

				{ "76", "18", "ミナ", "MINA", "鶴岡八幡宮本宮が近いですよ。", "Tsuruoka Hachiman Shrine Hongu is close." , "mina1_nomal", "0" , "0"},
				{ "77", "18", "ミナ", "MINA", "鎌倉時代に、源頼朝によって遷座されたこの鶴岡八幡宮ですが、", "In Kamakura period, but this is the Tsuruoka Hachiman Shrine, which is Senza by Minamoto Yoritomo" , "mina2_nomal", "0" , "0"},
				{ "78", "18", "ミナ", "MINA", "実は一度火事で焼失しているんです。", "Actually, I've been burned in the fire once." , "mina2_sad", "0" , "0"},
				{ "79", "18", "ミナ", "MINA", "現在の本宮は江戸時代に竣工したものです。", "The current main shrine is what was completed in the Edo era" , "mina2_nomal", "0" , "0"},
				{ "80", "18", "ミナ", "MINA", "毎年、１２月１６日には「御鎮座記念祭」が執り行われます。", "Every year, in December 16th it will be celebrated is 御鎮座記念祭" , "mina2_smile", "0" , "0"},
				{ "81", "18", "ミナ", "MINA", "ただ実はここも関東大震災の影響で倒壊しているのです。", "But in fact even here it has collapsed under the influence of the Great Kanto Earthquake." , "mina2_sad", "0" , "0"},
				{ "82", "18", "ミナ", "MINA", "多くの人の努力が実り、現在の形になっているんですね。", "Fruitful efforts of many people, but I have been in the current form." , "mina2_smile", "0" , "0"},
				
				{ "83", "20", "ミナ", "MINA", "小樽水族館には様々な魚たちがいますよ。", "Fruitful efforts of many people, but I have been in the current form." , "mina2_smile", "0" , "0"},
				{ "84", "20", "ミナ", "MINA", "このエントランスには小樽で保護されたウミガメが、みんなに笑顔にしています。", "Fruitful efforts of many people, but I have been in the current form." , "mina2_smile", "0" , "0"},
				
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
				values.put(TalkEventsTable.talk_name_en.getName(), data[TalkEventsTable.talk_name_en.getColNo()]);
				values.put(TalkEventsTable.talk_body.getName(), data[TalkEventsTable.talk_body.getColNo()]);
				values.put(TalkEventsTable.talk_body_en.getName(), data[TalkEventsTable.talk_body_en.getColNo()]);
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
				
				// メッセージは言語によって変える
				if (lang.equals("English")) {
					val.setTalkName(cursor.getString(TalkEventsTable.talk_name_en
							.getColNo()));
					val.setTalkBody(cursor.getString(TalkEventsTable.talk_body_en
							.getColNo()));
				}else{
					val.setTalkName(cursor.getString(TalkEventsTable.talk_name
							.getColNo()));
					val.setTalkBody(cursor.getString(TalkEventsTable.talk_body
							.getColNo()));
				}

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
