package com.scdeco.emdesigntest;

import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.scdeco.embdesign.Colorway;
import com.scdeco.embdesign.EMBDesign;
import com.scdeco.embdesign.EMBDesignUtils;

public class TestDesign {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDefaultColorway(){
		/*
		Colorway colorway = new Colorway(5);
		System.out.println(colorway.getRunningSteps());
		
		colorway = new Colorway(15);
		System.out.println(colorway.getRunningSteps());

		colorway = new Colorway(16);
		System.out.println(colorway.getRunningSteps());
		
		colorway = new Colorway(60);
		System.out.println(colorway.getRunningSteps());
*/
	}
	
	@Test
	public void test() {
		EMBDesign designControl=new EMBDesign("/home/baku/DST/W432162A.DST");

		
		int count=designControl.getStitchCount();
		assertTrue(count>0);
		
		count=designControl.getStepCount();
		assertTrue(count>0);
		
		
		BufferedImage image1=designControl.getEMBDesignImage();
		//"S1061,S1005,S1040,S1011,S1147,S1159,S1001,S1043","1-2-3-4-1-5-2-6-3-4-7-2-8-7"
		BufferedImage image2=EMBDesignUtils.getDesignThumbnail(image1, 120);
		try{
			File outputfile1 = new File("/home/baku/DST/W432162A-1.png");
		    ImageIO.write(image1, "png", outputfile1);
		    outputfile1 = new File("/home/baku/DST/W432162A-1-resized.png");
		    ImageIO.write(image2, "png", outputfile1);
		    
		}
		catch (IOException e) {
			
		}
		
	}
	
	@Test
	public void testReadFile(){
		
	}

}
