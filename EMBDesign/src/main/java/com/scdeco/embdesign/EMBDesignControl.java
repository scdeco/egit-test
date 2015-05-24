package com.scdeco.embdesign;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
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
		public int indexStep;
		public FunctionCode funcCode;
		public int xCoord;
		public int yCoord;
		public int xCurrent;
		public int yCurrent;
		public int xChange;
		public int yChange;	
	}
	
	final class RunningStep
	{
		public int threadIndex;
		public int stitches;
		public int length;
		public int firstStitchIndex;
		public int lastStitchIndex;
	}
	
	public static final int maxThreads = 15; 
	
	private String highlightSteps = "";
	public void setHighlightSteps(String highlightSteps)
	{
		this.highlightSteps = highlightSteps;
		this.redrawFlag = true;
	}
	
	private String runningSteps = "";
	public String getRunningSteps()
	{
		return this.runningSteps;
	}
	public void setRunningSteps(String runningSteps)
	{
		this.runningSteps = runningSteps;
		redrawFlag = true;
		SetRunningStepList();
	}
	
	private ArrayList<String> runningStepList;
	private ArrayList<String> highlightStepList = new ArrayList<String>();
	
	public int getColorCount()
	{
		int colors=0;
		int i = 1;
		if (this.threadList != null)
		{
			while (i < this.threadList.length)
			{
				if ((this.threadList[i].code == null) || this.threadList[i].code.isEmpty() 
						|| this.threadList[i].code.trim().isEmpty()) {break;}
				i++;
			}
			colors = --i;
		}		
		return colors;
	}

	private String threads;
	public String getThreads()
	{
		return this.threads;
	}
	public void setThreads(String sColorWay)
	{
		String[] sColorCodes = sColorWay.split(",");
		String sThread = "";
		Boolean hasErrorInColorWay = false;
		for( String sCode : sColorCodes)
		{
			if (sCode != "")
			{
				EmbroideryThread thread = EMBThreadChart.getEmbroideryThread(sCode);
				sThread += thread.code + ":" + thread.name + ":" + thread.color.toString()+",";
			}
			else
			{
				hasErrorInColorWay = true;
				break;
			}
		}
		if (hasErrorInColorWay){sThread = "";}
		if (sThread != "") {sThread.substring(0, sThread.length() - 1);}
		this.threads = sThread;
	}
	
	private EmbroideryThread[] threadList;
	public EmbroideryThread[] getThreadList()
	{
		return this.threadList;
	}
	public void setThreadList(EmbroideryThread[] threadList)
	{
		this.threadList = threadList;
	}
	
	private ArrayList<StitchPoint> stitchPointList;
	public ArrayList<StitchPoint> getStitchPointList()
	{
		return this.stitchPointList;
	}
	public void setStitchPointList(ArrayList<StitchPoint> stitchPointList)
	{
		this.stitchPointList = stitchPointList;
	}
	
	private RunningStep[] stepList;
	public RunningStep[] getStepList()
	{
		return this.stepList;
	}
	public void setStepList(RunningStep[] stepList)
	{
		this.stepList = stepList;
	}
	
	private String dstFile = "";
	public String getDstFile()
	{
		return this.dstFile;
		
	}
	public void setDstFile(String dstFile)
	{	if(this.dstFile != dstFile) clearDesign();
		this.dstFile = dstFile;
		regetStitchesFlag = true;
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
	
	public int getStitcheCount()
	{
		return this.stitchPointList.size();
	}
	
	public int getStepCount()
	{
		return this.stepList == null?0:stepList.length;
	}
	
	public double getDesignHeight()
	{
		return stitchPointList.size() == 0 ? 0 : Math.abs((double)(ptBottomRight.y -ptTopLeft.y))/10.0d;
	}
	public double getDeignWidth()
	{
		return stitchPointList.size() == 0 ? 0 : Math.abs((double)(ptBottomRight.x - ptTopLeft.x))/10.0d;
	}
	public double getStartX()
	{
		return stitchPointList.size() == 0 ? 0 : ((double)(StitchPoint.class.cast(stitchPointList.get(0)).xCurrent) 
				- Math.abs((double)(ptBottomRight.x - ptTopLeft.x))/2.0d)/10.0d;
	}
	public double getStartY()
	{
		return stitchPointList.size() == 0 ? 0 : ((double)(StitchPoint.class.cast(stitchPointList.get(0)).yCurrent) 
				- Math.abs((double)(ptBottomRight.y - ptTopLeft.y))/2.0d)/10.0d;
	}
	public double getDesignLeft()
	{
		return stitchPointList.size() == 0 ? 0 : Math.abs((double)ptTopLeft.x)/10.0d;
	}
	public double getDesignUp()
	{
		return stitchPointList.size() == 0 ? 0 : Math.abs((double)ptTopLeft.y)/10.0d;
	}
	public double getDesignRight()
	{
		return stitchPointList.size() == 0 ? 0 : Math.abs((double)ptBottomRight.x)/10.0d;
	}
	public double getDesignDown()
	{
		return stitchPointList.size() == 0 ? 0 : Math.abs((double)ptBottomRight.y)/10.0d;
	}
	
	
	private int indexStep;
	private StitchPoint currPoint;
	private Point ptTopLeft;
	private Point ptBottomRight;
	private RandomAccessFile inFS;
	private Boolean redrawFlag = false;
	private Boolean regetStitchesFlag = false;
	private BufferedImage designBufferedImage;

	private Boolean trim = true;
	public void setTrim(Boolean trim) {
		this.trim = trim;
	}
	
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
	
	
	public EMBDesignControl()
	{
		this.stitchPointList = new ArrayList<StitchPoint>();
		this.runningStepList = new ArrayList<String>();
		this.threadList = new EmbroideryThread[maxThreads+2];
		this.ptTopLeft = new Point(0,0);
		this.ptBottomRight=new Point(0,0);
		this.zoomFactor = 1.0f;
		this.rotateAngle = 0.0f;
	}
	
	private void getDSTToken() 
	{
		this.currPoint=new StitchPoint(); 
		byte x = 0;
		byte y = 0;
		byte z = 0;

		short xChange = 0;
		short yChange = 0;
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
		if (z == (byte)0XF3)
		{
			this.currPoint.funcCode = FunctionCode.END;
			this.runningStepList.add("0");
			this.indexStep++;
			return;
		}
		xChange = (short)(-9*((x>>3)&1)+9*((x>>2)&1)-((x>>1)&1)+(x&1)
				-27*((y>>3)&1)+(27*(y>>2)&1)-3*(((y>>1)&1)+3*(y&1)-81*((z>>3)&1)+81*((z>>2)&1)));
		yChange = (short)(-9*((x>>4)&1)+9*((x>>5)&1)-((x>>6)&1)+((x>>7)&1)
				-27*((y>>4)&1)+(27*(y>>5)&1)-3*(((y>>6)&1)+3*((y>>7)&1)-81*((z>>4)&1)+81*((z>>5)&1)));
		this.currPoint.xChange = xChange;
		this.currPoint.yChange = yChange;
		this.currPoint.xCoord += xChange;
		this.currPoint.yCoord += yChange;
		switch(z&0XC3)
		{
		case 0X03:
			this.currPoint.indexStep = indexStep;
			this.currPoint.funcCode = FunctionCode.STITCH;
			break;
		case 0X83:
			this.currPoint.indexStep = indexStep;
			this.currPoint.funcCode = FunctionCode.JUMP;
			break;
		case 0XC3:
			this.currPoint.indexStep =++indexStep;
			this.currPoint.funcCode = FunctionCode.STOP;
			this.runningStepList.add("0");
			break;
		case 0X43:
			this.currPoint.indexStep = indexStep;
			this.currPoint.funcCode = FunctionCode.BORERIN;
			break;
		default:
			break;
		}
	}
	public void clearDesign()
	{
		this.runningStepList.clear();
		this.stepList = null;
		this.indexStep = 0;
		this.stitchPointList.clear();
		this.currPoint=null;
		this.ptTopLeft.x = 0;
		this.ptTopLeft.y = 0;
		this.ptBottomRight.x = 0;
		this.ptBottomRight.y = 0;
		
		this.designBufferedImage = null;
	}
	
	public void getStitchPoints(){
		if (this.dstFile.trim() == "") {return;}
		clearDesign();
		this.regetStitchesFlag = false;
		this.redrawFlag = true;
		StitchPoint[] st;
		int stitchesCount = 0;
		try
		{
			this.inFS = new RandomAccessFile(dstFile,"r");
			st = new StitchPoint[(int)((this.inFS.length()-512-1)/3)+1];
			this.inFS.seek(512);
			for (getDSTToken(); currPoint.funcCode != FunctionCode.END; getDSTToken())
			{
				if (this.currPoint.xCoord < this.ptTopLeft.x) {this.ptTopLeft.x = this.currPoint.xCoord;}
				if (this.currPoint.yCoord > this.ptTopLeft.y) {this.ptTopLeft.y = this.currPoint.yCoord;}
				if (this.currPoint.xCoord > this.ptBottomRight.x) {this.ptBottomRight.x = this.currPoint.xCoord;}
				if (this.currPoint.yCoord < this.ptBottomRight.y) {this.ptTopLeft.y = this.currPoint.yCoord;}
				st[stitchesCount++] = currPoint;
			}
			int pindex = 0;
			this.stepList = new RunningStep[this.runningStepList.size()];
			for(int i=0;i<stepList.length;i++){
				stepList[i]=new RunningStep();
			}
				
			this.stepList[pindex].firstStitchIndex = 0;
			for (int i = 0; i < stitchesCount; i++)
			{
				st[i].xCurrent = st[i].xCoord - this.ptTopLeft.x;
				st[i].yCurrent = this.ptTopLeft.y - st[i].yCoord;
				this.stitchPointList.add(st[i]);
				if(st[i].indexStep != pindex)
				{
					this.stepList[pindex].lastStitchIndex = i -1;
					this.stepList[pindex].stitches = this.stepList[pindex].lastStitchIndex - 
							this.stepList[pindex].firstStitchIndex+1;
					pindex = st[i].indexStep;
					this.stepList[pindex].firstStitchIndex = 1;
				}
			}
			this.stepList[pindex].lastStitchIndex = stitchesCount - 1;
			this.stepList[pindex].stitches = this.stepList[pindex].lastStitchIndex - this.stepList[pindex].firstStitchIndex;
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
	
	public void drawDesign()
	{
		if (this.stitchPointList.size() == 0) {return;}
		redrawFlag = false;
		SetRunningStepList();
		setEmbThreadList();
		StitchPoint currStitch;
		StitchPoint prevStitch;
		int length = this.ptBottomRight.x - this.ptTopLeft.x +1;
		int width = this.ptTopLeft.y-this.ptBottomRight.y+1;
		designBufferedImage = new BufferedImage(length,width,BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = designBufferedImage.createGraphics();
		Integer iThread = 0;
		graphics.setBackground(new Color(255,255,255,255));
		graphics.clearRect(0, 0, length, width);
		prevStitch = (StitchPoint)this.stitchPointList.get(0);
		ArrayList<String> runningStepList = this.highlightStepList.size() > 0 ? this.highlightStepList: this.runningStepList;
		for (int i =1 ; i < this.stitchPointList.size(); i++)
		{
			currStitch = (StitchPoint)this.stitchPointList.get(i);
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
	public void getDesignImage( Boolean redraw, Boolean regetStitches, float angle) throws IOException
	{
		this.rotateAngle = angle;
		getDesignImage(redraw, regetStitches);
	}
	public void getDesignImage(Boolean redraw, Boolean regetSitches) throws IOException
	{
		if (this.regetStitchesFlag) {getStitchPoints();}
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
	private void loadDefaultStepList()
	{
		this.runningStepList.clear();
		for(int i = 0; i < this.indexStep; i++)
		{
			this.runningStepList.add(Integer.toString((i%(this.threadList.length - 1) +1)));
		}
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
	
	private void SetRunningStepList() 
	{
		this.runningStepList.clear();
		this.highlightStepList.clear();
		this.runningSteps = this.runningSteps.trim();
		this.runningSteps = CharMatcher.is('-').trimFrom(runningSteps);
		if (this.runningSteps == "") {loadDefaultStepList();}
		else
		{
			int i, j = 0;
			String s = this.runningSteps + "-";
			while (s.contains("-"))
			{
				i = s.indexOf('-');
				this.runningStepList.add(s.substring(0,i));
				s = s.substring(i+1);
				j++;
			}
			for ( i = j; i < this.indexStep; i++)
			{
				this.runningStepList.add(Integer.toString(0));
			}
		}
		this.highlightSteps = this.highlightSteps.trim();
		this.highlightSteps = CharMatcher.is('-').trimFrom(highlightSteps);
		
		if (this.highlightSteps != "")
		{
			int i , j = 0;
			String s = this.highlightSteps + "-";
			while (s.contains("-"))
			{
				i = s.indexOf("-");
				this.highlightStepList.add(s.substring(0,i));
				s = s.substring(i+1);
				j++;
			}
			for (i = j; i < this.indexStep; i++)
			{
				this.highlightStepList.add(Integer.toString(0));
			}
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
	
	private void setEmbThreadList() 
	{
		this.threads = threads.trim();
		this.threads = CharMatcher.is(',').trimFrom(threads).toUpperCase();
		if (this.threads == ""){loadDefaultThreadList();}
		else
		{
			this.threadList[0].color = this.bgThreadColor;
			this.threadList[16].color = this.cursorThreadColor;
			EmbroideryThread t = new EmbroideryThread();
			t.code = "";
			t.name = "";
			t.color = this.bgThreadColor;
			for ( int k =1; k <= maxThreads; k++) {this.threadList[k] = t;}
			String s = this.threads+ ",";
			int i = 1;
			int j = 1;
			int m = 1;
			while(s.contains(","))
			{
				i = s.indexOf(',');
				String ss = s.substring(0,i);
				m = ss.indexOf(':');
				if ( m >= 0)
				{
					t.name = ss.substring(0, m);
					ss = ss.substring(m+1);
				}
				int l = Integer.parseInt(ss);
				t.color = new Color(l>>16&255,l>>8&255,l&255);
				this.threadList[j++]=t;
				s=s.substring(i+1);
			}
		}
	}
	public EmbroideryThread[] getEmbroideryThreadlist()
	{
		EmbroideryThread[] threads = (EmbroideryThread[])(this.threadList.clone());
		return threads;
	}
	public ArrayList<String> GetRunningStepList()
	{
		@SuppressWarnings("unchecked")
		ArrayList<String> steps = (ArrayList<String>) this.runningStepList.clone();
		return steps;
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
