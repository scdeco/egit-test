package com.scdeco.embdesign;

import java.awt.Color;

public final class EmbroideryThread {
	public String name;
	public String code; 
	public Color color;
	public EmbroideryThread(){
	}
	public EmbroideryThread(String code,Color color){
		this.code=code;
		this.color=color;
	}

}
