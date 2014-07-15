package com.sw.minavi.beans;


public class TalkBeans {

	String firstTalkStr = null;
	int imageId = 0;
	int animetionType = 0;
	int position = 0;

	public String getFirstTalkStr() {
		return firstTalkStr;
	}
	private void setFirstTalkStr(String str) {
		firstTalkStr = str;
	}

	public int getImageId() {
		return imageId;
	}
	private void setImageId(int id) {
		imageId = id;
	}

	public int getAnimetion() {
		return animetionType;
	}
	private void setAnimetion(int type) {
		animetionType = type;
	}

	public int getPosition() {
		return position;
	}
	private void setPosition(int pos) {
		position = pos;
	}
	
	public TalkBeans(String str1, int id, int type, int pos) {
		setFirstTalkStr(str1);
		setImageId(id);
		setAnimetion(type);
		setPosition(pos);
	}

}
