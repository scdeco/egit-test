package com.scdeco.embdesign;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EMBThreadChart 
{
	static Map<String,Color> threads = new HashMap<String,Color>();
	static{
		threads.put("S0561",Color.red);
		threads.put("S1001",Color.white);
		threads.put("S1005", Color.black);
		threads.put("S1011", Color.gray);
		threads.put("S1043",Color.blue);
		threads.put("S1024",Color.yellow);
	}
	
	EMBThreadChart(){
	}
	
	public static Color getColor(String code){
		
		return threads.get(code);
	}
	
	public static EmbroideryThread getEmbroideryThread(String threadCode){
		
		EmbroideryThread thread=new EmbroideryThread();
		thread.code = threadCode;
		thread.color = getColor(threadCode);
		return thread;
	}
	
	public static List<EmbroideryThread> getEmbroideryThreadList(String threadCodes){
		
		List<EmbroideryThread> threadList = new ArrayList<EmbroideryThread>();
		String[] threadCodeList = threadCodes.split(Colorway.runningStepSeperator);
		if(threadCodeList.length>0){
			threadList.add(new EmbroideryThread("",Color.white)); //backgroundcolor
			for(String code:threadCodeList){
				code=code.trim();
				if (!code.isEmpty()){
					EmbroideryThread thread = EMBThreadChart.getEmbroideryThread(code);
					threadList.add(thread);
				}
				else 
					break;
			}
		}
		return threadList;
	}	
}
