package com.sw.minavi.item;

public class StoryEvent {
	private int story_event_id;
	private int story_group_id;
	private String talk_name;
	private String talk_body;
	private String image_file_name;
	private int image_position_type;
	private int image_animation_type;
	

	public int getStoryEventId() {
		return this.story_event_id;
	}
	public void setStoryEventId(int story_event_id) {
		this.story_event_id = story_event_id;
	}

	public String getTalkName() {
		return this.talk_name;
	}
	public void setTalkName(String talk_name) {
		this.talk_name = talk_name;
	}

	public int getStoryGroupId() {
		return this.story_group_id;
	}
	public void setStoryGroupId(int story_group_id) {
		this.story_group_id = story_group_id;
	}

	public String getTalkBody() {
		return this.talk_body;
	}
	public void setTalkBody(String talk_body) {
		this.talk_body = talk_body;
	}

	public String getImageFileName() {
		return this.image_file_name;
	}
	public void setImageFileName(String image_file_name) {
		this.image_file_name = image_file_name;
	}

	public int getImagePositionType() {
		return this.image_position_type;
	}
	public void setImagePositionType(int image_position_type) {
		this.image_position_type = image_position_type;
	}

	public int getImageAnimationType() {
		return this.image_animation_type;
	}
	public void setImageAnimationType(int image_animation_type) {
		this.image_animation_type = image_animation_type;
	}
}
