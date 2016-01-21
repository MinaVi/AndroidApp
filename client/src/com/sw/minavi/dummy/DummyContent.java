package com.sw.minavi.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	// 外部接続用
	public static String name;
	
	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	static {
		// Add 3 sample items.
		addItem(new DummyItem("1", "情報が読み込めませんでした", "情報が読み込めませんでした"));
//		addItem(new DummyItem("2", "Item 2", "piyopiyopiyo"));
//		addItem(new DummyItem("3", "Item 3", "hogepiyo"));
		
	}

	public static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String name;
		public String content;

		public DummyItem(String id, String name, String content) {
			this.id = id;
			this.name = name;
			this.content = content;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
