package com.anisbulbul.voicerecorder.play_list;

public class ItemDetails {

	// audio file details
	private String voiceTitle;
	private String voiceDuration;
	private String voiceDate;
	private String voiceSize;

	// constructor to initialize audio iteme
	public ItemDetails(String voiceTitle, String voiceDuration,
			String voiceDate, String voiceSize) {
		super();
		this.voiceTitle = voiceTitle;
		this.voiceDuration = voiceDuration;
		this.voiceDate = voiceDate;
		this.voiceSize = voiceSize;
	}

	public String getVoiceDate() {
		return voiceDate;
	}

	public String getVoiceDuration() {
		return voiceDuration;
	}

	public String getVoiceName() {
		return voiceTitle;
	}

	public String getVoiceSize() {
		return voiceSize;
	}

	public String getVoiceTitle() {
		if (voiceTitle.length() - 4 > 15) {
			return voiceTitle.substring(0, 12) + "..."; // return audio file
														// title in 12 length
														// string
		} else
			return voiceTitle.substring(0, voiceTitle.length() - 4); // return
																		// audio
																		// file
																		// title
	}

	public void setVoiceDate(String voiceDate) {
		this.voiceDate = voiceDate;
	}

	public void setVoiceDuration(String voiceDuration) {
		this.voiceDuration = voiceDuration;
	}

	public void setVoiceSize(String voiceSize) {
		this.voiceSize = voiceSize;
	}

	public void setVoiceTitle(String voiceTitle) {
		this.voiceTitle = voiceTitle;
	}
}
