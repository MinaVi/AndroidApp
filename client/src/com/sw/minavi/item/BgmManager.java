package com.sw.minavi.item;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;

public class BgmManager {

	private static BgmManager bgmManager;
	private Context c;
	int idBefore;
	BgmManagerState mode;
	MediaPlayer soundPlayer = null;

	public enum BgmManagerState {
		PLAYING, STOP_REQUESTED, STOPPED
	}

	public static BgmManager newIntance(Context c) {

		if (bgmManager == null) {
			bgmManager = new BgmManager(c);
		}
		return bgmManager;
	}

	public BgmManager(Context c) {
		this.c = c;
	}

	public void playSound(int id) {

		// idが-1以外ならその音を再生、-1なら再生を止める
		if (id != -1) {
			// idが前と同じなら音を再再生しない
			if (idBefore != id) {
				if (soundPlayer != null) {
					soundPlayer.pause();
					soundPlayer.release();
				}
				soundPlayer = null;
				soundPlayer = MediaPlayer.create(c, id);
				soundPlayer.setLooping(true);
				soundPlayer.seekTo(0);
				try {
					soundPlayer.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				soundPlayer.start();
				idBefore = id;
			}
			mode = BgmManagerState.PLAYING;
		} else {
			if (soundPlayer != null) {
				soundPlayer.pause();
				soundPlayer.release();
				soundPlayer = null;
				mode = BgmManagerState.STOPPED;
				idBefore = -1;
			}
		}
	}

	public BgmManagerState getMode() {
		return mode;
	}

}