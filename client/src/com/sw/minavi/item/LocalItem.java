package com.sw.minavi.item;


public class LocalItem {
	private int id;
	private int talk_group_id;
	private int story_group_id;
	private String message;
	private double lon;
	private double lat;
	private String arImageName;
	private String auther;
	private String createTime;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getTalkGroupId() {
		return this.talk_group_id;
	}

	public void setTalkGroupId(int talk_group_id) {
		this.talk_group_id = talk_group_id;
	}

	public int getStoryGroupId() {
		return this.story_group_id;
	}

	public void setStoryGroupId(int story_group_id) {
		this.story_group_id = story_group_id;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getLon() {
		return this.lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return this.lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getArImageName() {
		return arImageName;
	}

	public void setArImageName(String arImageName) {
		this.arImageName = arImageName;
	}
	
	public String getAuther() {
		return this.auther;
	}

	public void setAuther(String auther) {
		this.auther = auther;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return String.format("ID:%s - %s",
				new Object[] { this.id, this.message });
	}
}
