package com.scdeco.embdesign;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.imgscalr.Scalr;

import com.google.common.base.CharMatcher;
import com.google.common.primitives.Ints;


public class EMBDesignControl 
{
	public enum FunctionCode { STOP, STITCH, JUMP, BORERIN, END, CHANGECOLOR};
	
	final class StitchPoint 
	{
		public int stepIndex;
		public FunctionCode funcCode;
		public int xCoord;
		public int yCoord;
		public int xCurrent;
		public int yCurrent;
		public int xChange;
		public int yChange;	
		
		public StitchPoint(){
		}
		
		public StitchPoint(int stepIndex,FunctionCode funcCode,int xCoord,int yCoord,int xCurrent,int yCurrent,int xChange,int yChange){
			this.stepIndex=stepIndex;
			this.funcCode=funcCode;
			this.xCoord=xCoord;
			this.yCoord=yCoord;
			this.xCurrent=xCurrent;
			this.yCurrent=yCurrent;
			this.xChange=xChange;
			this.yChange=yChange;
		}
		
		//copy constructor
		public StitchPoint(StitchPoint anotherStitch){
			this.stepIndex=anotherStitch.stepIndex;
			this.funcCode=anotherStitch.funcCode;
			this.xCoord=anotherStitch.xCoord;
			this.yCoord=anotherStitch.yCoord;
			this.xCurrent=anotherStitch.xCurrent;
			this.yCurrent=anotherStitch.yCurrent;
			this.xChange=anotherStitch.xChange;
			this.yChange=anotherStitch.yChange;			
		}
		
	}
	
	final class RunningStep
	{
		public int threadIndex;
		public int stitches;
		public int length;
		public int firstStitchIndex;
		public int lastStitchIndex;
	
	}
	
	public static final int Max_Thread_Number = 15;
	
	public EMBDesignControl()
	{
		this.threadList = new EmbroideryThread[Max_Thread_Number+2];
		this.stepList=new ArrayList<RunningStep>();
		
		this.ptTopLeft = new Point(0,0);
		this.ptBottomRight=new Point(0,0);
		this.zoomFactor = 1.0f;
		this.rotateAngle = 0.0f;
	}
	
	//format S1001,S1005,S1011...
	private String threads;
	public String getThreads(){
		return this.threads;
	}
	
	public void setThreads(String threads){
		this.threads = CharMatcher.is(',').trimFrom(threads.trim()).toUpperCase();
		setThreadList();
		redrawFlag=true;
	}
	
	private int threadCount;
	public int getThreadCount(){
		return this.threadCount;
	}
	
	private EmbroideryThread[] threadList;
	private void setThreadList(){
		if (this.threads.isEmpty())
			loadDefaultThreadList();
		
		else{
			threadList[0]= new EmbroideryThread("",this.bgThreadColor);
			threadList[16]=new EmbroideryThread("",this.cursorThreadColor);
					
			String[] threads = this.threads.split(",");
			int threadIndex=0;
			for( String code : threads)
				if (code != "")
					threadList[++threadIndex]= EMBThreadChart.getEmbroideryThread(code);
				else
					break;
			threadCount=threadIndex;
		}
	}
	
	private String runningSteps = "";
	public String getRunningSteps(){
		return this.runningSteps;
	}
	public void setRunningSteps(String runningSteps)
	{
		this.runningSteps = CharMatcher.is('-').trimFrom(runningSteps.trim());
		SetRunningStepList();
		redrawFlag = true;
	}

	//generate in createStitchList()
	private ArrayList<RunningStep> stepList;
	public ArrayList<RunningStep> getStepList()
	{
		return this.stepList;
	}
	
	private void SetRunningStepList() {
		
		if (this.runningSteps.isEmpty()) 
			loadDefaultStepList();
		
		else{
			String[] steps = this.runningSteps.split("-");
			if(steps.length<=stepList.size())
				for(int i=0;i<steps.length;i++)
					stepList.get(i).threadIndex=Integer.parseInt(steps[i]);
		}
	}
	
	private void loadDefaultStepList(){
		for(RunningStep step:stepList)
			step.threadIndex= stepList.indexOf(step) %(this.threadList.length - 1) +1;
	}
	
	private void loadDefaultThreadList()
	{
		this.threadList[0].color = this.bgThreadColor;
		this.threadList[1].color = new Color(0,255,0);
		this.threadList[2].color = new Color(0,0,255);
		this.threadList[3].color = new Color(255,0,0);
		this.threadList[4].color = new Color(255,255,0);
		this.threadList[5].color = new Color(0,255,255);
		this.threadList[6].color = new Color(255,0,255);
		this.threadList[7].color = new Color(0,153,0);
		this.threadList[8].color = new Color(0,0,153);
		this.threadList[9].color = new Color(153,0,0);
		this.threadList[10].color = new Color(255,153,51);
		this.threadList[11].color = new Color(153,0,204);
		this.threadList[12].color = new Color(153,102,51);
		this.threadList[13].color = new Color(255,255,255);
		this.threadList[14].color = new Color(0,0,0);
		this.threadList[15].color = new Color(255,126,204);
		this.threadList[16].color = this.cursorThreadColor;
		for (int i = 0; i <this.threadList.length-1;i++)
		{
			this.threadList[i].code = "";
			this.threadList[i].name = "";
		}
	}

	
	private StitchPoint[] stitchList;
	public StitchPoint[] getStitchList(){
		return this.stitchList;
	}
	
	private String dstFile = "";
	public String getDstFile(){
		return this.dstFile;
	}
	public void setDstFile(String dstFile){
		if(this.dstFile != dstFile){
			clearDesign();
			this.dstFile = dstFile;
			createStitchList();
		}
	}
	
	private Color bgThreadColor = new Color(50,50,50);
	public void setBgThreadColor(Color bgThreadColor)
	{
		this.bgThreadColor = bgThreadColor;
	}
	
	private Color cursorThreadColor = Color.green;
	public void setCursorThreadColor( Color cursorThreadColor)
	{
		this.cursorThreadColor = cursorThreadColor;
	}
	
	public int getStitchCount(){
		return this.stitchList==null?0:this.stitchList.length;
	}
	
	public int getStepCount()
	{
		return this.stepList == null?0:stepList.size();
	}
	
	public double getDesignHeight()	{
		return getStitchCount()==0? 0 : Math.abs((double)(ptBottomRight.y -ptTopLeft.y))/10.0d;
	}
	
	public double getDeignWidth(){
		return getStitchCount()==0 ? 0 : Math.abs((double)(ptBottomRight.x - ptTopLeft.x))/10.0d;
	}
	
	public double getStartX(){
		return getStitchCount() == 0 ? 0 : ((double)(stitchList[0].xCurrent) 
				- Math.abs((double)(ptBottomRight.x - ptTopLeft.x))/2.0d)/10.0d;
	}
	
	public double getStartY(){
		return getStitchCount() == 0 ? 0 : ((double)(stitchList[0].yCurrent) 
				- Math.abs((double)(ptBottomRight.y - ptTopLeft.y))/2.0d)/10.0d;
	}
	
	public double getDesignLeft(){
		return getStitchCount() == 0 ? 0 : Math.abs((double)ptTopLeft.x)/10.0d;
	}
	
	public double getDesignUp()	{
		return getStitchCount() == 0 ? 0 : Math.abs((double)ptTopLeft.y)/10.0d;
	}
	
	public double getDesignRight(){
		return getStitchCount() == 0 ? 0 : Math.abs((double)ptBottomRight.x)/10.0d;
	}
	
	public double getDesignDown(){
		return getStitchCount() == 0 ? 0 : Math.abs((double)ptBottomRight.y)/10.0d;
	}
	
	
	private StitchPoint currPoint;
	private Point ptTopLeft;
	private Point ptBottomRight;
	private RandomAccessFile inFS;
	private Boolean redrawFlag = false;
	private BufferedImage designBufferedImage;

	private float zoomFactor;
		public float getZoomFactor() {
		return zoomFactor;
	}
	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
	}
	
	private float rotateAngle;
	public float getRotateAngle() {
		return rotateAngle;
	}
	public void setRotateAngle(float rotateAngle) {
		this.rotateAngle = rotateAngle;
	}
	
	
	
	private void getDSTToken() 
	{
		byte x = 0,y = 0,z = 0;
		while ( x == 0 && y == 0 && z == 0)
		{
			try {
				x = this.inFS.readByte();
				y = this.inFS.readByte();
				z = this.inFS.readByte();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				z = (byte) 0XF3;
			} 
		}
		
		if (z == (byte)0XF3){
			
			this.currPoint.funcCode = FunctionCode.END;
			this.stepList.add(new RunningStep());
			return;
		}
		
		int xChange = -9*((x>>3)&1)+9*((x>>2)&1)-((x>>1)&1)+(x&1)
				-27*((y>>3)&1)+27*((y>>2)&1)-3*((y>>1)&1)+3*(y&1)-81*((z>>3)&1)+81*((z>>2)&1);
		int yChange = -9*((x>>4)&1)+9*((x>>5)&1)-((x>>6)&1)+((x>>7)&1)
				-27*((y>>4)&1)+27*((y>>5)&1)-3*((y>>6)&1)+3*((y>>7)&1)-81*((z>>4)&1)+81*((z>>5)&1);
		
		this.currPoint.xChange = xChange;
		this.currPoint.yChange = yChange;
		this.currPoint.xCoord += xChange;
		this.currPoint.yCoord += yChange;
		
		this.currPoint.stepIndex = stepList.size();
		
		switch(z&0XC3){
			case 0X03:
				this.currPoint.funcCode = FunctionCode.STITCH;
				break;
			case 0X83:
				this.currPoint.funcCode = FunctionCode.JUMP;
				break;
			case 0XC3:
				this.currPoint.funcCode = FunctionCode.STOP;
				this.stepList.add(new RunningStep());
				break;
			case 0X43:
				this.currPoint.funcCode = FunctionCode.BORERIN;
				break;
			default:
				break;
		}
	}
	
	public void clearDesign()
	{
		this.stitchList=null;
		this.stepList.clear();

		this.ptTopLeft.x = 0;
		this.ptTopLeft.y = 0;
		this.ptBottomRight.x = 0;
		this.ptBottomRight.y = 0;
		
		this.designBufferedImage = null;
	}
	
	public void createStitchList(){
		if (this.dstFile.trim() == "") {return;}
		clearDesign();
		this.redrawFlag = true;
		int stitchesCount = 0;
		try
		{
			this.inFS = new RandomAccessFile(dstFile,"r");
			stitchList = new StitchPoint[(int)((this.inFS.length()-512-1)/3)+1];
			this.currPoint=new StitchPoint(); 
			this.inFS.seek(512);			
			for (getDSTToken(); currPoint.funcCode != FunctionCode.END; getDSTToken()){
				
				if (this.currPoint.xCoord < this.ptTopLeft.x) this.ptTopLeft.x = this.currPoint.xCoord;
				if (this.currPoint.yCoord > this.ptTopLeft.y) this.ptTopLeft.y = this.currPoint.yCoord;
				if (this.currPoint.xCoord > this.ptBottomRight.x) this.ptBottomRight.x = this.currPoint.xCoord;
				if (this.currPoint.yCoord < this.ptBottomRight.y) this.ptBottomRight.y = this.currPoint.yCoord;
				stitchList[stitchesCount++] = new StitchPoint(currPoint);
			}
			
			int pindex = 0;
			stepList.get(pindex).firstStitchIndex = 0;
			for (int i = 0; i < stitchesCount; i++)
			{
				StitchPoint st=stitchList[i];
				st.xCurrent = st.xCoord - this.ptTopLeft.x;
				st.yCurrent = this.ptTopLeft.y - st.yCoord;
				if(st.stepIndex != pindex)
				{
					RunningStep rs=stepList.get(pindex);
					rs.lastStitchIndex = i -1;
					rs.stitches = rs.lastStitchIndex -rs.firstStitchIndex+1;
					
					pindex = st.stepIndex;
					stepList.get(pindex).firstStitchIndex = i;
				}
			}
			
			stepList.get(pindex).lastStitchIndex = stitchesCount - 1;
			stepList.get(pindex).stitches = stepList.get(pindex).lastStitchIndex - stepList.get(pindex).firstStitchIndex;
		}
		catch(IOException e)
		{
			System.out.println("Error file reading.");
			e.printStackTrace();
		}
		finally
		{
			if (this.inFS != null)
				try {
					this.inFS.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	public void drawStep(RunningStep step,Graphics2D g2d){
		Color threadColor=threadList[step.threadIndex].color;
		g2d.setColor(threadColor);
		
		StitchPoint currStitch;
		StitchPoint prevStitch=stitchList[0];

		for(int i=step.firstStitchIndex+1;i<=step.lastStitchIndex;i++){
			currStitch=stitchList[i];
			g2d.drawLine(prevStitch.xCurrent,prevStitch.yCurrent,currStitch.xCurrent,currStitch.yCurrent);
			prevStitch=currStitch;
		}
	
	}
	
	public void drawDesign()
	{
		if (this.stitchList.length == 0) return;
		redrawFlag = false;

		int width = this.ptBottomRight.x - this.ptTopLeft.x +1;
		int height = this.ptTopLeft.y-this.ptBottomRight.y+1;
		designBufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = designBufferedImage.createGraphics();

		g2d.setBackground(new Color(255,255,255,255));
		g2d.clearRect(0, 0, width, height);
		
		for(RunningStep step:stepList)
			drawStep(step,g2d);
	}
		
/*		
		for (int i =1 ; i < this.stitchPointList.length; i++)
		{
			currStitch = (StitchPoint)this.stitchPointList[i];
			iThread = Ints.tryParse(runningStepList.get(prevStitch.indexStep).toString());
			if ( iThread != null )
			{
				iThread = iThread < this.threadList.length ? iThread : 0;
				graphics.setColor(this.threadList[iThread].color);
				if (currStitch.funcCode == FunctionCode.JUMP||prevStitch.funcCode == FunctionCode.JUMP 
						|| prevStitch.funcCode == FunctionCode.STOP)
				{
					if (!trim)
					{
						float[] dash1 = {2f,0f,2f};
						BasicStroke bs1 = new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1.0f,dash1,2f);
						graphics.setStroke(bs1);
						graphics.drawLine(prevStitch.xCurrent,prevStitch.yCurrent,currStitch.xCurrent,currStitch.yCurrent);
					}
					else
					{
						BasicStroke bs2 =  new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
						graphics.setStroke(bs2);
						graphics.drawLine(prevStitch.xCurrent,prevStitch.yCurrent,currStitch.xCurrent,currStitch.yCurrent);
					}
				}
			}
		}
		
	}
*/
	public BufferedImage getDesignImage(){
		if (this.redrawFlag)
			drawDesign();
		return this.designBufferedImage;
	}
	
	public void getDesignImage( Boolean redraw, float angle) throws IOException
	{
		this.rotateAngle = angle;
		getDesignImage(redraw);
	}
	public void getDesignImage(Boolean redraw) throws IOException
	{
		if (this.redrawFlag|redraw) {drawDesign();}
		rotateImage(this.designBufferedImage, this.rotateAngle); 
	}
	private void rotateImage(BufferedImage img, float angle)
	{
		if (angle < 5) {this.designBufferedImage = img != null? img:null;}
		else 
		{
			if(img != null)
			{
				AffineTransform tx = new AffineTransform();
			    tx.rotate(angle, (img.getWidth() / 2), (img.getHeight() / 2));
	
			    AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
			    this.designBufferedImage = op.filter((BufferedImage) img, null);
			}
			else {this.designBufferedImage = null;}
		}
	}
	

	

	public static List<EmbroideryThread> getEMBThreadListFromColorway(String sColorway)
	{
		List<EmbroideryThread> threadList = new ArrayList<EmbroideryThread>();
		String[] sColorCodes = sColorway.split(",");
		for(String sCode:sColorCodes)
		{
			if (sCode != "")
			{
				EmbroideryThread thread = EMBThreadChart.getEmbroideryThread(sCode);
				threadList.add(thread);
			}
			else {break;}
		}
		return threadList;
	}
	
	public BufferedImage GetDesignThumbnail(int thumbnailSize)
	{
		BufferedImage thumbnail = null;
		if (this.designBufferedImage != null)
		{
			double designHeight = this.getDesignHeight();
			double desighWidth = this.getDeignWidth();
			double ratio = designHeight/desighWidth;
			double length = (double)thumbnailSize;
			int width = (int)( ratio > 1 ? (length/ratio) : length);
			int height = (int)(ratio > 1 ? length : (length * ratio));
			thumbnail = Scalr.resize(this.designBufferedImage,width,height,Scalr.OP_ANTIALIAS);
		}
		return thumbnail;
	}
	

	public static String getNormalizedThreadCode(String sCode)
	{
		sCode=sCode.trim().toUpperCase();
		if ((sCode != null) && !sCode.isEmpty() && !sCode.trim().isEmpty())
		{
			Integer n = Ints.tryParse(sCode);
			if (("MS6".contains(sCode.substring(0, 1)) & sCode.length() == 4) &&  n != null)
			{
				sCode = "S" + sCode;
			}	
		}
		return sCode;
	}
	public static EmbroideryThread getEmbroideryThread(String code)
	{
		EmbroideryThread thread = new EmbroideryThread();
		thread.code = "";
		thread.name = "";
		thread.color = null;
		code = getNormalizedThreadCode(code);
		if ((code != null) && !code.isEmpty() && !code.trim().isEmpty())
		{
			thread.code = code;
			
		}
		return thread;
	}
	private static String runningStepsSeperator = "[\\-+*/.:;,]";
	/*public static String getNormalizedRunningStep()
	{
		string runningStep = 
		return normalizeRunningStept(runningStep);
	}*/
	public static String normalizeRunningStep(String runningStep)
	{
		if( runningStep != "")
		{
			String[] steps = runningStep.split(runningStepsSeperator);
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
	public String getColorWay()
	{
		String sColorway = "";
		for(EmbroideryThread thread : this.threadList)
		{
			if((thread.code == null) || thread.code.isEmpty() || thread.code.trim().isEmpty())
			{
				sColorway += thread.code+",";
			}
		}
		if (sColorway != "")
		{
			sColorway = sColorway.substring(0, sColorway.length()-1);
		}
		return sColorway;
	}
	/*public static String getColorway()
	{
		String colorway = "";
		String sThreadCodes = "";
		for (Integer i =1; i <= 15; i++)
		{
			String col = "sColor"+i.toString();
			String s = 
		}
		return colorway;
	}*/
}
