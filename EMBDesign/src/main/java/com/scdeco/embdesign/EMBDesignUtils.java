package com.scdeco.embdesign;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.imgscalr.Scalr;

public class EMBDesignUtils{
	
	public static BufferedImage resize(BufferedImage srcBufferedImage,int targetWidth, int targetHeight){
		return (srcBufferedImage == null)?null:
				Scalr.resize(srcBufferedImage,Scalr.Method.AUTOMATIC,Scalr.Mode.AUTOMATIC,targetWidth,targetHeight );
	}

	public static BufferedImage getDesignThumbnail(BufferedImage srcBufferedImage,int thumbnailSize){
		return resize(srcBufferedImage,thumbnailSize,thumbnailSize);
	}
	
	public static ImageIcon getDesignIcon(BufferedImage srcBufferedImage){
		return new ImageIcon(srcBufferedImage);
	}
}
