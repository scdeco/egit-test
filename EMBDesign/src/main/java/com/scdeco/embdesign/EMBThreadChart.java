package com.scdeco.embdesign;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EMBThreadChart 
{
    private static final class LABWrapper{
    	private double cieL;
 
    	
    	public double getCieL() {
			return cieL;
		}
    	
    	private double cieA;
		public double getCieA() {
			return cieA;
		}
		
    	private double cieB;
		public double getCieB() {
			return cieB;
		}

		
    	
    	public LABWrapper(double cieL, double cieA, double cieB){
    		this.cieL = cieL;
    		this.cieA = cieA;
    		this.cieB = cieB;
    	}
    }
	
	
	
	
	static Map<String,Color> threads = new LinkedHashMap<String,Color>();
    public static void initThreadsFromXmlFile(String xmlFile){
		parseXmlFile(xmlFile);
		parseDocument();
		
	}
	public static void initThreadsFromDataBase(){
		
		Connection conn = null;
		PreparedStatement  pst = null;
		ResultSet rs = null;
		
		String baseURL = "jdbc:mysql://localhost:3306/";
		String dbName = "world";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "zwh940428";
		
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(baseURL+dbName,userName,password);
			pst = conn.prepareStatement("Select sCode,nRed,nGreen,nBlue from dictembthread");
			rs = pst.executeQuery();
			
			while(rs.next()){
				threads.put(rs.getString("sCode"), new Color(rs.getInt("nRed"),rs.getInt("nGreen"),rs.getInt("nBlue")));
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
		finally{
			if (conn != null) {  
                try {  
                    conn.close();  
                } catch (SQLException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (pst != null) {  
                try {  
                    pst.close();  
                } catch (SQLException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (rs != null) {  
                try {  
                    rs.close();  
                } catch (SQLException e) {  
                    e.printStackTrace();  
                }  
            }	
		}
	}
	public static Color getColor(String code){
		
		return threads.get(code.toUpperCase());
	}
	
	public static EmbroideryThread getEmbroideryThread(String threadCode){
		
		EmbroideryThread thread=new EmbroideryThread();
		thread.setCode(threadCode);
		thread.setColor(getColor(threadCode));
		return thread;
	}
	
	public static List<EmbroideryThread> getEmbroideryThreadList(String threadCodes){
		
		List<EmbroideryThread> threadList = new ArrayList<EmbroideryThread>();
		String[] threadCodeList = threadCodes.split(Colorway.runningStepSeperator);
		if(threadCodeList.length>0){
			threadList.add(new EmbroideryThread("",Color.white)); //background color
			for(String code:threadCodeList){
				code=code.trim();
				if (!code.isEmpty()){
					EmbroideryThread thread = EMBThreadChart.getEmbroideryThread(code);
					threadList.add(thread);
				}
				else 
					break;
			}
		}
		return threadList;
	}	
	
	private static Document dom;
	private static void parseXmlFile(String xmlFile){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse(xmlFile);
			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	private static void parseDocument(){
		Element docEle = dom.getDocumentElement();
		
		String sCode;
		int nRed;
		int nGreen;
		int nBlue;
		
		NodeList nlsCode = docEle.getElementsByTagName("sCode");
		NodeList nlnRed = docEle.getElementsByTagName("nRed");
		NodeList nlnGreen = docEle.getElementsByTagName("nGreen");
		NodeList nlnBlue = docEle.getElementsByTagName("nBlue");
		
		if ((nlsCode != null)&&(nlsCode.getLength() > 0)&&(nlnRed != null)&&(nlnRed.getLength() > 0)
				&&(nlnGreen != null)&&(nlnGreen.getLength() > 0)&&(nlnBlue != null)&&(nlnBlue.getLength() > 0)){
			for (int i = 0; i< nlsCode.getLength();i++){
				
				Element elesCode = (Element)nlsCode.item(i);
				Element elenRed = (Element)nlnRed.item(i);
				Element elenGreen = (Element)nlnGreen.item(i);
				Element elenBlue = (Element)nlnBlue.item(i);
				if(elenRed != null && elenGreen != null && elenBlue != null){
					sCode = getTextValue(elesCode);
					nRed = getIntValue(elenRed);
					nGreen = getIntValue(elenGreen);
					nBlue = getIntValue(elenBlue);
					Color x = new Color(nRed, nGreen, nBlue);	
					threads.put(sCode,x);
				}
				else;
				
				
				
			}
			
		}
	}
	private static String getTextValue(Element ele) {
		String textVal = null;
		textVal = ele.getFirstChild().getNodeValue();

		return textVal;
	}
	private static int getIntValue(Element ele) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele));
	}
	
	public static EmbroideryThread[] getClosestThreadList(Color color, int count, String algorithm, double[] weight)
    {
        if (count < 1)  count = 1;
        EmbroideryThread[] listClosestColor = new EmbroideryThread[count];
        double[] listDist = new double[count];
        
        for (int i = 0; i < count; i++)
            listDist[i] = Double.MAX_VALUE;
        for(String key: threads.keySet())
        {
        	Color c = threads.get(key);
            if( c != null){
                double curDistance = 0.0d;
                switch (algorithm)
                {
                    case "RGB":
                        curDistance = getDistanceOfRGB(color, c);
                        break;
                    case "HSB":
                        curDistance = getDistanceOfHSL(color, c, weight);
                        break;
                    case "YUV":
                        curDistance = getDistanceOfYUV(color, c, weight);
                        break;
                    case "CIE":
                        curDistance = getDistanceOfCIE(color, c);
                        break;
                }
                if (curDistance < listDist[count - 1])
                {
                    int n = count - 1;
                    while (n >= 0 && curDistance < listDist[n])
                        n--;
                    n++;
                    for (int i = count - 1; i > n; i--)
                    {
                        listDist[i] = listDist[i - 1];
                        listClosestColor[i]=listClosestColor[i-1];
                    }
                    listDist[n] = curDistance;
                    EmbroideryThread element = new EmbroideryThread();
                    element.setColor(c);
                    element.setCode(key);
                    listClosestColor[n]=element;
                }
            }
        }
        return listClosestColor; 
    }

    public static EmbroideryThread GetClosestThread(Color color, String algorithm, double[] weight)
    {
        // adjust these values to place more or less importance on
        // the differences between HSV components of the colors
        EmbroideryThread thread = new EmbroideryThread();
        EmbroideryThread[] minThreadlist= getClosestThreadList(color, 1, algorithm, weight);

        thread.setCode(minThreadlist[0].getCode());
  //      thread.name = minDr[0]["sName"].ToString();
        thread.setColor(minThreadlist[0].getColor());
        return thread;
    }
	
	
	public static double getDistanceOfYUV(Color color1,Color color2, double[] weight){
		double dR = color1.getRed() - color2.getRed();
        double dG = color1.getGreen() - color2.getGreen();
        double dB = color1.getBlue() - color2.getBlue();

        double dY = 0.299d * dR + 0.587d * dG + 0.114d * dB;
        double dU = -0.14713d * dR - 0.28886d * dG + 0.436d * dB;
        double dV = 0.615d * dR - 0.51499d * dG - 0.10001d * dB;

        return Math.sqrt(dY * dY * weight[0] + dU * dU * weight[1] + dV * dV * weight[2]);
	}
	
	public static double getDistanceOfRGB(Color color1, Color color2){
        int dR = color1.getRed() - color2.getRed();
        int dG = color1.getGreen() - color2.getGreen();
        int dB = color1.getBlue() - color2.getBlue();
        return Math.sqrt(dR * dR + dG * dG + dB * dB);
    }

    public static double getDistanceOfHSL(Color color1, Color color2, double[] weight){
    	
    	float[] hsbValuesColor1 = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
    	float[] hsbValuesColor2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
    	
    	float hueColor1 = hsbValuesColor1[0];
    	float saturationColor1 = hsbValuesColor1[1];	
    	float brightnessColor1 = hsbValuesColor1[2];
    	
    	float hueColor2 = hsbValuesColor2[0];
    	float saturationColor2 = hsbValuesColor2[1];
    	float brightnessColor2 = hsbValuesColor2[2];
    	
        float dH = Math.abs(hueColor1 - hueColor2);// / 360.0f;
        if (dH > 0.5) 
            dH = 1.0f - dH;
        float dS = saturationColor1 - saturationColor2;
        float dL = brightnessColor1 - brightnessColor2;
        return Math.sqrt(dH * dH * weight[0] + dS * dS * weight[1] + dL * dL * weight[2]);
    }

    public static double getDistanceOfCIE(Color color1, Color color2){
    	
        LABWrapper LAB1 = RGBtoLAB(color1);
        LABWrapper LAB2 = RGBtoLAB(color2);

        double dL = LAB1.getCieL() - LAB2.getCieL();
        double dA = LAB1.getCieA() - LAB2.getCieA();
        double dB = LAB1.getCieB() - LAB2.getCieB();

        return Math.sqrt(dL * dL + dA * dA + dB * dB);
    }

    public static LABWrapper RGBtoLAB(Color color){
    	
    	double cieL;
    	double cieA;
    	double cieB;
        //copy from http://smlync.tumblr.com/post/20531889265/comparing-colors-using-delta-e-in-c

        double R = ((double)(color.getRed())) / 255.0;       //R from 0 to 255
        double G = ((double)(color.getGreen())) / 255.0;       //G from 0 to 255
        double B = ((double)(color.getBlue())) / 255.0;       //B from 0 to 255

        R = (R > 0.04045 ? Math.pow(((R + 0.055) / 1.055), 2.4) : R / 12.92) * 100;
        G = (G > 0.04045 ? Math.pow(((G + 0.055) / 1.055), 2.4) : G / 12.92) * 100;
        B = (B > 0.04045 ? Math.pow(((B + 0.055) / 1.055), 2.4) : B / 12.92) * 100;

        //Observer. = 2�, Illuminant = D65
        double X = R * 0.4124 + G * 0.3576 + B * 0.1805;
        double Y = R * 0.2126 + G * 0.7152 + B * 0.0722;
        double Z = R * 0.0193 + G * 0.1192 + B * 0.9505;

        // based upon the XYZ - CIE-L*ab formula at easyrgb.com (http://www.easyrgb.com/index.php?X=MATH&H=07#text7)

        double var_X = X / 95.047;         // Observer= 2�, Illuminant= D65
        double var_Y = Y / 100.000;
        double var_Z = Z / 108.883;

        var_X = var_X > 0.008856 ? Math.pow(var_X, (1 / 3.0)) : (7.787 * var_X) + (16 / 116.0);
        var_Y = var_Y > 0.008856 ? Math.pow(var_Y, (1 / 3.0)) : (7.787 * var_Y) + (16 / 116.0);
        var_Z = var_Z > 0.008856 ? Math.pow(var_Z, (1 / 3.0)) : (7.787 * var_Z) + (16 / 116.0);

        cieL = (116 * var_Y) - 16;
        cieA = 500 * (var_X - var_Y);
        cieB = 200 * (var_Y - var_Z);
        
        return  new LABWrapper(cieL,cieA,cieB);
       
    }

    

}
