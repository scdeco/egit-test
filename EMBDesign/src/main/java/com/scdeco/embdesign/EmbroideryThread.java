package com.scdeco.embdesign;

import java.awt.Color;

public final class EmbroideryThread {
	private String name;
	private String code; 
	private Color color;
	
	public EmbroideryThread(){
	}
	
	public EmbroideryThread(String threadCode,Color threadColor){
		this.setCode(threadCode);
		this.setColor(threadColor);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
