package com.scdeco.embdesign;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.google.common.base.CharMatcher;
import org.jdesktop.dataset.DataTable;

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
		/*public StitchPoint()
		{
			this.indexStep = 0;
			this.funcCode = FunctionCode.STOP;
			this.xCoord = 0;
			this.xChange = 0;
			this.xCurrent = 0;
			this.yCoord = 0;
			this.yChange = 0;
			this.yCurrent = 0;
		}*/
	}
	
	final class EmbroideryThread
	{
		public String name;
		public String code; 
		public Color color;
		/*
		 public EmbroideryThread()
		 {
		 	this.name = "";
		 	this.code = ""'
		 	this.color = new Color(0,0,0,1);
		 }
		 */
		
	}
	final class RunningStep
	{
		public int threadIndex;
		public int stitches;
		public int length;
		public int firstStitchIndex;
		public int lastStitchIndex;
		/*public RunningStep()
		{
			this.threadIndex = 0;
			this.stitches = 0;
			this.length = 0;
			this.firstStitchIndex = 0;
			this.lastStitchIndex = 0; 
		}*/
	}
	
	public static final int maxThreads = 15; 
	
	private String highlightSteps = "";
	public String getHighlightSteps()
	{
		return this.highlightSteps;
	}
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
	public ArrayList<String> getRunningStepList()
	{
		return this.runningStepList;
	}
	public void setRunningStepList(ArrayList<String> runningStepList)
	{
		this.runningStepList = runningStepList;
	}
	private ArrayList<String> highLightStepList = new ArrayList<String>();
	private int colors;
	public int getColors()
	{
		int i = 1;
		if (this.threadList != null)
		{
			while (i < this.threadList.length)
			{
				if ((this.threadList[i].code == null) || this.threadList[i].code.isEmpty() 
						|| this.threadList[i].code.trim().isEmpty()) {break;}
				i++;
			}
			this.colors = --i;
		}		
		else {this.colors = 0;}
		return this.colors;
	}
	public void setColors(int colors)
	{
		this.colors = colors;
	}
	private String threads;
	public String getThreads()
	{
		return this.threads;
	}
	public void setThread(String threads)
	{
		this.threads =threads;
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
	private ArrayList<String> stitchPointList;
	public ArrayList<String> getStitchPointList()
	{
		return this.stitchPointList;
	}
	public void setStitchPointList(ArrayList<String> stitchPointList)
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
	
	public int getStitches()
	{
		return this.stitchPointList.size();
	}
	public int getSteps()
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
	private Image image;
	private BufferedImage designBufferedImage;
	private float zoomFactor;
	private float rotateAngle;


	
	
	public EMBDesignControl()
	{
		this.stitchPointList = new ArrayList<String>();
		this.runningStepList = new ArrayList<String>();
		this.threadList = new EmbroideryThread[maxThreads+2];
		this.ptTopLeft = new Point(0,0);
		this.zoomFactor = 1.0f;
		this.rotateAngle = 0.0f;
	}
	private void getDSTToken() throws IOException
	{
		byte x = 0;
		byte y = 0;
		byte z = 0;
		int t = 0;
		short xChange = 0;
		short yChange = 0;
		while ( x == 0 && y == 0 && z == 0)
		{
			t = this.inFS.readByte(); if (t >= 0)x = (byte)t; else break;
			t = this.inFS.readByte(); if (t >= 0)y = (byte)t; else break;
			t = this.inFS.readByte(); if (t >= 0)z = (byte)t; else break;
		}
		if (t < 0 || z == 0XF3)
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
		this.currPoint.xCoord = 0;
		this.currPoint.yCoord = 0;
		this.currPoint.xChange = 0;
		this.currPoint.yChange = 0;
		this.currPoint.xCurrent = 0;
		this.currPoint.yCurrent = 0;
		this.ptTopLeft.x = 0;
		this.ptTopLeft.y = 0;
		this.ptBottomRight.x = 0;
		this.ptBottomRight.y = 0;
		if (designBufferedImage != null)
		{
			designBufferedImage = null;
		}
		this.image = null;
	}
	
	public void getStitchPoints() throws IOException
	{
		clearDesign();
		this.regetStitchesFlag = false;
		this.redrawFlag = true;
		if (this.dstFile.trim() == "") {return;}
		StitchPoint[] st;
		int stitchesCount = 0;
		try
		{
			this.inFS = new RandomAccessFile(dstFile,"r");
			this.inFS.seek(512);
			st = new StitchPoint[(int)((this.inFS.length()-512-1)/3)+1];
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
			this.stepList[pindex].firstStitchIndex = 0;
			for (int i = 0; i < stitchesCount; i++)
			{
				st[i].xCurrent = st[i].xCoord - this.ptTopLeft.x;
				st[i].yCurrent = this.ptTopLeft.y - st[i].yCoord;
				this.stitchPointList.add(st[i].toString());
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
		catch(Exception e)
		{
			
		}
		finally
		{
			if (this.inFS != null) {this.inFS.close();}
		}
	}
	
	public void drawDesign()
	{
		if (this.stitchPointList.size() == 0) {return;}
		redrawFlag = false;
		SetRunningStepList();
		SetEmbThreadList();
		StitchPoint currStitch;
		StitchPoint prevStitch;
		designBufferedImage = new BufferedImage(this.ptBottomRight.x - this.ptTopLeft.x +1
				,this.ptTopLeft.y-this.ptBottomRight.y+1,BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = designBufferedImage.createGraphics();
		int iThread = 0;
		
	}
	public void showDesign( Boolean redraw, Boolean regetStitches, float angle) throws IOException
	{
		this.rotateAngle = angle;
		showDesign(redraw, regetStitches);
	}
	public void showDesign(Boolean redraw, Boolean regetSitches) throws IOException
	{
		if (this.regetStitchesFlag) {getStitchPoints();}
		if (this.redrawFlag|redraw) {drawDesign();}
		this.sizeMode = 
	}
	private void rotateImage(Image img, float angle)
	{
		Image oldImage = null;
		if (this.image != null) {oldImage = this.image;}
		if (angle < 5) {this. image = img != null? img:null;}
		else 
		{
			if(img != null)
			{
				AffineTransform tx = new AffineTransform();
			    tx.rotate(angle, ((BufferedImage)img).getWidth() / 2,((BufferedImage) img).getHeight() / 2);
	
			    AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
			    this.image = op.filter((BufferedImage) img, null);
			}
			else {this.image = null;}
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
		this.highLightStepList.clear();
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
				this.highLightStepList.add(s.substring(0,i));
				s = s.substring(i+1);
				j++;
			}
			for (i = j; i < this.indexStep; i++)
			{
				this.highLightStepList.add(Integer.toString(0));
			}
		}
	}
	
	public void setThreads(String sColorWay, DataTable dtThread)
	{
		String[] sColorCodes = sColorWay.split(",");
		String sThread = "";
		Boolean hasErrorInColorWay = false;
		for( String sCode : sColorCodes)
		{
			if (sCode != "")
			{
				EmbroideryThread thread = getEmbroideryThread(sCode, dtThread);
			}
		}
	}
	
	private void SetEmbThreadList() {
		// TODO Auto-generated method stub
		
	}
}
