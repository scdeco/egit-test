package com.scdeco.embdesign;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import org.imgscalr.Scalr;

public class EMBDesignUtils{
	
	public EMBDesignUtils(BufferedImage bufferedImage){
		this.designBufferedImage=bufferedImage; 
	}
	
	BufferedImage designBufferedImage;
	public BufferedImage getBufferedImage(){
		return designBufferedImage;
	}
	
	public BufferedImage GetDesignThumbnail(int thumbnailSize){
		BufferedImage thumbnail = null;
		if (this.designBufferedImage != null)
		{
			double ratio = designBufferedImage.getHeight()/designBufferedImage.getWidth();
			int width = (int)( ratio > 1 ? (thumbnailSize/ratio) : thumbnailSize);
			int height = (int)(ratio > 1 ? thumbnailSize : (int)(thumbnailSize * ratio));
			thumbnail = Scalr.resize(this.designBufferedImage,width,height,Scalr.OP_ANTIALIAS);
		}
		return thumbnail;
	}
}
