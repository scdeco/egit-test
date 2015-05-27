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

import com.scdeco.embdesign.EMBDesignControl;

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
	public void test() {
		EMBDesignControl designControl=new EMBDesignControl();
		designControl.setDstFile("/home/baku/DST/W434046B.DST");
		designControl.setThreads("S1024,S0561,S1043");
		designControl.setRunningSteps("1-2-3-1");
		
		int count=designControl.getStitchCount();
		assertTrue(count>0);
		
		count=designControl.getThreadCount();
		assertTrue(count==3);
		
		count=designControl.getStepCount();
		assertTrue(count==4);
		
		BufferedImage image=designControl.getDesignImage();
		assertTrue(image!=null);
		try{
			File outputfile = new File("/home/baku/DST/W434046B.png");
		    ImageIO.write(image, "png", outputfile);
		}
		catch (IOException e) {
			
		}
		
	}
	
	@Test
	public void testReadFile(){
		
	}

}
