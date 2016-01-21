package com.sw.minavi.item;

public class LocalItem {
	private int id;
	private int talkGroupId;
	private String message;
	private String detail;
	private double lon;
	private double lat;
	private String arImageName;
	private String iconImageName;
	private double specialLonMin;
	private double specialLatMin;
	private double specialLonMax;
	private double specialLatMax;
	private String createTime;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTalkGroupId() {
		return this.talkGroupId;
	}

	public void setTalkGroupId(int talkGroupId) {
		this.talkGroupId = talkGroupId;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
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

	public String getIconImageName() {
		return this.iconImageName;
	}

	public void setIconImageName(String iconImageName) {
		this.iconImageName = iconImageName;
	}

	public double getSpecialLonMin() {
		return specialLonMin;
	}

	public void setSpecialLonMin(double specialLonMin) {
		this.specialLonMin = specialLonMin;
	}

	public double getSpecialLatMin() {
		return specialLatMin;
	}

	public void setSpecialLatMin(double specialLatMin) {
		this.specialLatMin = specialLatMin;
	}

	public double getSpecialLonMax() {
		return specialLonMax;
	}

	public void setSpecialLonMax(double specialLonMax) {
		this.specialLonMax = specialLonMax;
	}

	public double getSpecialLatMax() {
		return specialLatMax;
	}

	public void setSpecialLatMax(double specialLatMax) {
		this.specialLatMax = specialLatMax;
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
