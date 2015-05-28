package com.scdeco.embdesign;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;

public class EMBDesignImage{
	
	public EMBDesignImage(){
	}
	
	
	BufferedImage designBufferedImage;
	/*
	public BufferedImage GetDesignThumbnail(int thumbnailSize)
	{
		BufferedImage thumbnail = null;
		if (this.designBufferedImage != null)
		{
			double designHeight = this.getDesignHeightInPixel();
			double desighWidth = this.getDeignWidthInPixel();
			double ratio = designHeight/desighWidth;
			double length = (double)thumbnailSize;
			int width = (int)( ratio > 1 ? (length/ratio) : length);
			int height = (int)(ratio > 1 ? length : (length * ratio));
			thumbnail = Scalr.resize(this.designBufferedImage,width,height,Scalr.OP_ANTIALIAS);
		}
		return thumbnail;
	}
	*/	
}
