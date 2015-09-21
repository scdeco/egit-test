package com.scdeco.emdesigntest;

import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.scdeco.embdesign.Colorway;
import com.scdeco.embdesign.EMBDesign;
import com.scdeco.embdesign.EMBDesignUtils;
import com.scdeco.embdesign.EMBThreadChart;

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
		EMBDesign designControl=new EMBDesign("c:\\Users\\Wenhao Zhang\\Desktop\\test\\W231930A.DST");
		EMBThreadChart.initThreadsFromXmlFile("c:\\Users\\Wenhao Zhang\\Desktop\\test\\DictEMBThread.xml");

		int count=designControl.getStitchCount();
		assertTrue(count>0);
		
		count=designControl.getStepCount();
		assertTrue(count>0);

		String threads = "S1169,S0643,S1159";
		String runningSteps = "1-2-3";
		
		Colorway colorway = new Colorway(threads,runningSteps);
		
		BufferedImage image1=designControl.getEMBDesignImage(colorway);
		BufferedImage image2=EMBDesignUtils.getDesignThumbnail(image1, 120);	
		
		try{
			File outputfile1 = new File("c:\\Users\\Wenhao Zhang\\Desktop\\test\\W231930A-1.png");
		    ImageIO.write(image1, "png", outputfile1);
		    outputfile1 = new File("c:\\Users\\Wenhao Zhang\\Desktop\\test\\W231930A-1-resized.png");
		    ImageIO.write(image2, "png", outputfile1);
		    
		}
		catch (IOException e) {
			
		}
	
	}
	
	@Test
	public void testReadFile(){
		
	}

}
