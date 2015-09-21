package com.example.shengyingpan.voiceapp;

public class SoundClip {
	private String name;
	private String path;
	private int length;

	public SoundClip(String name, String path, int length) {
		this.name = name;
		this.path = path;
		this.length = length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return this.length;
	}

	public String getPath() {
		return this.path;
	}
}