package com.sw.minavi.activity.beans;





public class TalkBeans {

	String firstTalkStr = "";
	String talkName = "";
	int imageId = 0;
	int animetionType = 0;
	int position = 0;
	int talkGroupId = 0;
	
	public String getFirstTalkStr() {
		return firstTalkStr;
	}
	private void setFirstTalkStr(String str) {
		this.firstTalkStr = str;
	}

	public String getTalkName() {
		return talkName;
	}
	private void setTalkName(String str) {
		this.talkName = str;
	}
	
	public int getImageId() {
		return imageId;
	}
	private void setImageId(int id) {
		this.imageId = id;
	}

	public int getAnimetion() {
		return animetionType;
	}
	private void setAnimetion(int type) {
		this.animetionType = type;
	}

	public int getPosition() {
		return position;
	}
	private void setPosition(int pos) {
		this.position = pos;
	}
	

	public int getTalkGroupId() {
		return talkGroupId;
	}
	private void setTalkGroupId(int talkGroupId) {
		this.talkGroupId = talkGroupId;
	}
	
	public TalkBeans(int talkGroupId, String str1, String talkName, int id, int type, int pos) {
		setTalkGroupId(talkGroupId);
		setFirstTalkStr(str1);
		setTalkName(talkName);
		setImageId(id);
		setAnimetion(type);
		setPosition(pos);
	}

}
