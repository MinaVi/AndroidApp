package com.sw.minavi.item;

public class TalkGroup {
	private int talk_group_id;
	private int area_id;
	private int local_area_id;
	private String background_file_name;
	private int select_flg;

	public int getTalkGroupId() {
		return this.talk_group_id;
	}
	public void setTalkGroupId(int talk_group_id) {
		this.talk_group_id = talk_group_id;
	}

	public int getAreaId() {
		return this.area_id;
	}
	public void setAreaId(int area_id) {
		this.area_id = area_id;
	}
	
	public int getLocalAreaId() {
		return this.local_area_id;
	}
	public void setLocalAreaId(int local_area_id) {
		this.local_area_id = local_area_id;
	}

	public String getBackGroundFileNmae() {
		return this.background_file_name;
	}
	public void setBackGroundFileName(String background_file_name) {
		this.background_file_name = background_file_name;
	}
	
	public int getSelectFlg() {
		return this.select_flg;
	}
	public void setSelectFlg(int select_flg) {
		this.select_flg = select_flg;
	}

}
