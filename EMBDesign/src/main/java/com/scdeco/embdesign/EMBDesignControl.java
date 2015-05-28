package com.scdeco.embdesign;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;


public class EMBDesignControl 
{
	public enum FunctionCode { STOP, STITCH, JUMP, BORERIN, END, CHANGECOLOR};
	
	final class Stitch 
	{
		public int stepIndex;
		public FunctionCode funcCode;
		public int xCoord;
		public int yCoord;
		public int xImage;
		public int yImage;
		public int xChange;
		public int yChange;	
		
		public Stitch(){
		}
		
		public Stitch(int stepIndex,FunctionCode funcCode,int xCoord,int yCoord,int xImage,int yImage,int xChange,int yChange){
			this.stepIndex=stepIndex;
			this.funcCode=funcCode;
			this.xCoord=xCoord;
			this.yCoord=yCoord;
			this.xImage=xImage;
			this.yImage=yImage;
			this.xChange=xChange;
			this.yChange=yChange;
		}
		
		//copy constructor
		public Stitch(Stitch anotherStitch){
			this.stepIndex=anotherStitch.stepIndex;
			this.funcCode=anotherStitch.funcCode;
			this.xCoord=anotherStitch.xCoord;
			this.yCoord=anotherStitch.yCoord;
			this.xImage=anotherStitch.xImage;
			this.yImage=anotherStitch.yImage;
			this.xChange=anotherStitch.xChange;
			this.yChange=anotherStitch.yChange;			
		}
		
	}
	
	final class Step{
		
		public int stepIndex;
		public int threadIndex;
		public int stitchCount;
		public int length;
		public int firstStitchIndex;
		public int lastStitchIndex;
	}
	
	
	public EMBDesignControl(){
		
		this.stepList=new ArrayList<Step>();

		this.ptTopLeft = new Point(0,0);
		this.ptBottomRight=new Point(0,0);
	}

	//generate in createStitchList()
	private ArrayList<Step> stepList;
	public ArrayList<Step> getStepList()	{
		return this.stepList;
	}

	private boolean trim=true;
	
	private Stitch[] stitchList;
	public Stitch[] getStitchList(){
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
			drawDesign();
		}
	}
	
	private Stitch currPoint;
	private Point ptTopLeft;
	private Point ptBottomRight;
	private RandomAccessFile inFS;
	private BufferedImage designBufferedImage;
	
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
			this.stepList.add(new Step());
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
				this.stepList.add(new Step());
				break;
			case 0X43:
				this.currPoint.funcCode = FunctionCode.BORERIN;
				break;
			default:
				break;
		}
	}
	

	public void createStitchList(){
		if (this.dstFile.trim() == "") {return;}
		clearDesign();
		int stitchCount = 0;
		try
		{
			this.inFS = new RandomAccessFile(dstFile,"r");
			stitchList = new Stitch[(int)((this.inFS.length()-512-1)/3)+1];
			this.currPoint=new Stitch(); 
			this.inFS.seek(512);			
			for (getDSTToken(); currPoint.funcCode != FunctionCode.END; getDSTToken()){
				
				if (this.currPoint.xCoord < this.ptTopLeft.x) this.ptTopLeft.x = this.currPoint.xCoord;
				if (this.currPoint.yCoord > this.ptTopLeft.y) this.ptTopLeft.y = this.currPoint.yCoord;
				if (this.currPoint.xCoord > this.ptBottomRight.x) this.ptBottomRight.x = this.currPoint.xCoord;
				if (this.currPoint.yCoord < this.ptBottomRight.y) this.ptBottomRight.y = this.currPoint.yCoord;
				stitchList[stitchCount++] = new Stitch(currPoint);
			}
			
			int stepIndex = 0;
			stepList.get(stepIndex).firstStitchIndex = 0;
			for (int i = 0; i < stitchCount; i++)
			{
				Stitch st=stitchList[i];
				st.xImage = st.xCoord - this.ptTopLeft.x;
				st.yImage = this.ptTopLeft.y - st.yCoord;
				if(st.stepIndex != stepIndex)
				{
					Step rs=stepList.get(stepIndex);
					rs.lastStitchIndex = i -1;
					rs.stitchCount = rs.lastStitchIndex -rs.firstStitchIndex+1;
					rs.stepIndex=stepIndex;
					
					stepIndex = st.stepIndex;
					stepList.get(stepIndex).firstStitchIndex = i;
				}
			}
			stepList.get(stepIndex).stepIndex=stepIndex;
			stepList.get(stepIndex).lastStitchIndex = stitchCount - 1;
			stepList.get(stepIndex).stitchCount = stepList.get(stepIndex).lastStitchIndex - stepList.get(stepIndex).firstStitchIndex;
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
	
	private IndexColorModel createIndexColorModel(Colorway colorway){

		int size=getStepCount()+1;

		byte[] r=new byte[size];
		byte[] g=new byte[size];
		byte[] b=new byte[size];
		
		r[0]=(byte)255;
		g[0]=(byte)255;
		b[0]=(byte)255;

		for (byte i=1;i<size;i++){
			
			Color color=colorway.getStepColor(i-1);
			
			r[i]=(byte)color.getRed();
			g[i]=(byte)color.getGreen();
			b[i]=(byte)color.getBlue();
		}
		
		return new IndexColorModel(7,size,r,g,b);
	}
	
	public void drawStep(Step step,Graphics2D g2d){
		int stepIndex=step.stepIndex+1;
		Color threadColor=new Color(stepIndex,stepIndex,stepIndex);
		g2d.setColor(threadColor);
		
		Stitch currStitch;
		Stitch prevStitch=stitchList[0];

		for(int i=step.firstStitchIndex+1;i<=step.lastStitchIndex;i++){
			currStitch=stitchList[i];

			if (currStitch.funcCode == FunctionCode.JUMP||prevStitch.funcCode == FunctionCode.JUMP 
					|| prevStitch.funcCode == FunctionCode.STOP){
				if (!trim){
					float[] dash1 = {2f,0f,2f};
					BasicStroke bs1 = new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1.0f,dash1,2f);
					g2d.setStroke(bs1);
					g2d.drawLine(prevStitch.xImage,prevStitch.yImage,currStitch.xImage,currStitch.yImage);
				}
			}
			else{
				BasicStroke bs2 =  new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
				g2d.setStroke(bs2);
				g2d.drawLine(prevStitch.xImage,prevStitch.yImage,currStitch.xImage,currStitch.yImage);
			}
			prevStitch=currStitch;
		}
	
	}
	
	public void drawDesign()
	{
		if (getStitchCount() == 0) return;

		int width = getDesignWidthInPixel();
		int height = getDesignHeightInPixel();
		int size=getStepCount()+1;
		
		byte[] r=new byte[size];
		byte[] g=new byte[size];
		byte[] b=new byte[size];
		
		for (byte i=0;i<size;i++){
			r[i]=i;g[i]=i;b[i]=i;
		}
			
		IndexColorModel indexColorModel=new IndexColorModel(7,size,r,g,b);
		
		designBufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_INDEXED,indexColorModel);
		Graphics2D g2d = designBufferedImage.createGraphics();

		g2d.setBackground(new Color(0,0,0));
		g2d.clearRect(0, 0, width, height);
		
		for(Step step:stepList)
			drawStep(step,g2d);
	}
		
	public BufferedImage getDesignImage(Colorway colorway){
		IndexColorModel indexColorModel=createIndexColorModel(colorway);
		return new BufferedImage(indexColorModel,designBufferedImage.getRaster(),false,null); 
		
	}
	
	public BufferedImage getDesignImage(){
		return this.designBufferedImage;
	}

	

	
	public int getStitchCount(){
		return this.stitchList==null?0:this.stitchList.length;
	}
	
	public int getStepCount(){
		return this.stepList == null?0:stepList.size();
	}

	
	public int getDesignHeightInPixel(){
		return getStitchCount()==0? 0 : ptTopLeft.y-ptBottomRight.y +1;
	}
	
	public int getDesignWidthInPixel(){
		return getStitchCount()==0 ? 0 : ptBottomRight.x-ptTopLeft.x+1;
	}
	
	
	public double getDesignHeight(){
		return getStitchCount()==0? 0 : (double)getDesignHeightInPixel()/10.0d;
	}
	
	public double getDesignWidth(){
		return getStitchCount()==0 ? 0 : (double)getDesignWidthInPixel()/10.0d;
	}
	
	public double getStartX(){
		return getStitchCount() == 0 ? 0 : ((double)(stitchList[0].xImage) 
				- Math.abs((double)(ptBottomRight.x - ptTopLeft.x))/2.0d)/10.0d;
	}
	
	public double getStartY(){
		return getStitchCount() == 0 ? 0 : ((double)(stitchList[0].yImage) 
				- Math.abs((double)(ptBottomRight.y - ptTopLeft.y))/2.0d)/10.0d;
	}
	
	public double getDesignLeft(){
		return getStitchCount() == 0 ? 0 : Math.abs((double)ptTopLeft.x)/10.0d;
	}
	
	public double getDesignTop()	{
		return getStitchCount() == 0 ? 0 : Math.abs((double)ptTopLeft.y)/10.0d;
	}
	
	public double getDesignRight(){
		return getStitchCount() == 0 ? 0 : Math.abs((double)ptBottomRight.x)/10.0d;
	}
	
	public double getDesignBottom(){
		return getStitchCount() == 0 ? 0 : Math.abs((double)ptBottomRight.y)/10.0d;
	}
	
	
}
