package com.jameslow.limelight.handlers;

public class ItemSlide {
	protected String filepath;
	protected int page;
	protected Object content;
	
	public ItemSlide(String filepath, int page, Object content) {
		this.filepath = filepath;
		this.page = page;
		this.content = content;
	}
	public int getPage() {
		return page;
	}
	public Object getContent() {
		return content;
	}
	public String getFilepath() {
		return filepath;
	}
}
