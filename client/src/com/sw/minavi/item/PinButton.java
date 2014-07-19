package com.sw.minavi.item;

import android.content.Context;
import android.widget.ImageButton;

public class PinButton extends ImageButton {

	public int id = 0;
	public int talkGroupId = 0;
	public String message = "";
	public String lon = "";
	public String lat = "";
	public String azimuth = "";
	public String distance = "";

	public PinButton(Context context) {
		super(context);
	}

	@Override
	public boolean equals(Object o) {
		if ((o instanceof PinButton) && (((PinButton) o).id == this.id)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return id;
	}
}
