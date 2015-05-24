package com.scdeco.embdesign;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;



public class EMBThreadChart 
{
	static Map<String,Color> threads = new HashMap<String,Color>();
	EMBThreadChart()
	{
		threads.put("S0561",Color.red);
		threads.put("S1001",Color.white);
		threads.put("S1005", Color.black);
		threads.put("S1011", Color.gray);
		threads.put("S1043",Color.blue);
	}
	public static Color getColor(String code)
	{
		return threads.get(code);
	}
	
	public static EmbroideryThread getEmbroideryThread(String code)
	{
		EmbroideryThread thread=new EmbroideryThread();
		thread.code = code;
		thread.color = getColor(code);
		return thread;
	}
}
