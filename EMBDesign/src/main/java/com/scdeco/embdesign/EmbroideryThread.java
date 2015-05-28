package com.scdeco.embdesign;

import java.awt.Color;

public final class EmbroideryThread {
	public String name;
	public String code; 
	public Color color;
	
	public EmbroideryThread(){
	}
	
	public EmbroideryThread(String threadCode,Color threadColor){
		this.code=threadCode;
		this.color=threadColor;
	}

}
