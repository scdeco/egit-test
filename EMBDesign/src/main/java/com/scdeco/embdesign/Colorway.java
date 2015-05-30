package com.scdeco.embdesign;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Ints;

public class Colorway {
	
	public static String runningStepSeperator = "[+\\-*/.:;,]";	//+-*/.:;,
	public static final int Max_Thread_Number = 15;
	public static Color backgroundColor=Color.WHITE;
	
	public Colorway(String threads,String runningSteps){
		setThreads(threads);
		setRunningSteps(runningSteps);
	}
	
	public Colorway(int stepCount){
		if (stepCount>0){
			setThreads(""); 
			setRunningSteps(getDefaultRunningSteps(stepCount));
		}

	}
	private String getDefaultRunningSteps(int stepCount){
		String runningSteps = "";
		for(int step=0;step<stepCount;step++)
			runningSteps+= "-"+(step%Max_Thread_Number+1);
		runningSteps=runningSteps.substring(1);
		return runningSteps;
	}
	
	String runningSteps;
	//value of runningStepList is colorIndex of threadList
	int[] runningStepList;
	
	String threads;
	List<EmbroideryThread> threadList;
	
	public String getRunningSteps(){
		return this.runningSteps;
	}

	public void setRunningSteps(String runningSteps){
		this.runningSteps=normalizeRunningStep(runningSteps);
		setRunningStepList();
	}

	private void setRunningStepList() {
		if (!runningSteps.isEmpty()){
			String[] stepList = this.runningSteps.split(runningStepSeperator);
			runningStepList=new int[stepList.length];
			for(int i=0;i<stepList.length;i++){
				runningStepList[i]=Integer.parseInt(stepList[i]);
			}
		}
		else
			runningStepList=null;
	}
	
	public int getRunningStepCount(){
		return runningStepList==null?0:runningStepList.length;
	}

	public String getThreads(){
		return this.threads;
	}
	
	public void setThreads(String threads){
		this.threads=normalizThreads(threads);
		setThreadList();
	}
	
	private void setThreadList(){
		
		threadList=threads.isEmpty()?defaultThreadList:EMBThreadChart.getEmbroideryThreadList(threads);
	}
	
	public int getThreadCount(){
		return threadList==null?0:threadList.size();
	}
	
	//get thread color of the designated step
	public Color getStepColor(int stepIndex){
		return getThreadColor(runningStepList[stepIndex]);
	}
	
	//get thread color from threadList
	public Color getThreadColor(int colorIndex){
		return threadList.get(colorIndex).color;
	}
	public static final List<EmbroideryThread> defaultThreadList=new ArrayList<EmbroideryThread>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = -3668026125195896498L;

		{
			new EmbroideryThread("", backgroundColor);
			new EmbroideryThread("", new Color(0,255,0));
			new EmbroideryThread("", new Color(0,0,255));
			new EmbroideryThread("", new Color(0,255,0));
			new EmbroideryThread("", new Color(0,0,255));
			new EmbroideryThread("", new Color(255,0,0));
			new EmbroideryThread("", new Color(255,255,0));
			new EmbroideryThread("", new Color(0,255,255));
			new EmbroideryThread("", new Color(255,0,255));
			new EmbroideryThread("", new Color(0,153,0));
			new EmbroideryThread("", new Color(0,0,153));
			new EmbroideryThread("", new Color(153,0,0));
			new EmbroideryThread("", new Color(255,153,51));
			new EmbroideryThread("", new Color(153,0,204));
			new EmbroideryThread("", new Color(153,102,51));
			new EmbroideryThread("", new Color(255,255,255));
			new EmbroideryThread("", new Color(0,0,0));
			new EmbroideryThread("", new Color(255,126,204));
		}
	};
	
	public static String normalizThreads(String threads)
	{
		threads=threads.trim().toUpperCase();
		if ((threads != null) && !threads.isEmpty() && !threads.trim().isEmpty())
		{
			Integer n = Ints.tryParse(threads);
			if (("MS6".contains(threads.substring(0, 1)) & threads.length() == 4) &&  n != null)
			{
				threads = "S" + threads;
			}	
		}
		return threads;
	}
	
	public static String normalizeRunningStep(String runningStep)
	{
		if( runningStep != "")
		{
			String[] steps = runningStep.split(runningStepSeperator);
			runningStep = "";
			boolean isZeros =  true;
			for(String step : steps)
			{
				String s = step.trim();
				if (s != "")
				{
					Integer i = Ints.tryParse(s);
					runningStep += "-" + i.toString();
					if ( i>0) {isZeros = false;}
					
				}
			}
			runningStep = isZeros? "":runningStep.substring(1);
		}
		return runningStep;
	}
	
	public static Colorway getDefaultColorway(){
		return new Colorway("",""); 
	}
	
}
