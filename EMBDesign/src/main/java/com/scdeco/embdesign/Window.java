package com.scdeco.embdesign;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;



public class Window {

	
	RGB c = new RGB(255,255,255);
	
	public Window(final Display display){
		final int width = 400;
		final int height = 650;
		final int buttonWidth = 120;
		final int buttonHeight = 30;
		final int textWidth = 90;
		final int textHeight = 30;
		final int textX0 = 10;
		final int textY0 = 10;
		final int textXOffset = 120;
		final int textYOffset = 60;
		final int buttonX0 = 25;
		final int buttonY0 = 10;
		final int arraySize = 10;
		
		EMBThreadChart.initThreadsFromXmlFile("c:\\Users\\Wenhao Zhang\\Desktop\\test\\DictEMBThread.xml");
		
		final Shell shell = new Shell(display, SWT.SHELL_TRIM | SWT.CENTER);
		
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		
		Button btnPicker = new Button(shell, SWT.PUSH);
		btnPicker.setText("Color Picker");
	
		Button btnRGB = new Button(shell, SWT.PUSH);
		btnRGB.setText("RGB Color");

		Button btnHSL = new Button(shell, SWT.PUSH);
		btnHSL.setText("HSB Color");

		Button btnYUV = new Button(shell, SWT.PUSH);
		btnYUV.setText("YUV Color");

		Button btnCIE = new Button(shell, SWT.PUSH);
		btnCIE.setText("CIE Color");

		FormData pickerData = new FormData(buttonWidth, buttonHeight);
		pickerData.right = new FormAttachment(0,width - buttonX0);
		pickerData.top = new FormAttachment(0,buttonY0);
		btnPicker.setLayoutData(pickerData);
		
		FormData rgbData = new FormData(buttonWidth, buttonHeight);
		rgbData.right = new FormAttachment(0,width - buttonX0);
		rgbData.top = new FormAttachment(btnPicker, buttonY0);
		btnRGB.setLayoutData(rgbData);

		FormData hsbData = new FormData(buttonWidth, buttonHeight);
		hsbData.right = new FormAttachment(0,width - buttonX0);
		hsbData.top = new FormAttachment(btnRGB,buttonY0);
		btnHSL.setLayoutData(hsbData);
		
		FormData yuvData = new FormData(buttonWidth, buttonHeight);
		yuvData.right = new FormAttachment(0,width - buttonX0);
		yuvData.top = new FormAttachment(btnHSL,buttonY0);
		btnYUV.setLayoutData(yuvData);
		
		FormData cieData = new FormData(buttonWidth, buttonHeight);
		cieData.right = new FormAttachment(0,width - buttonX0);
		cieData.top = new FormAttachment(btnYUV,buttonY0);
		btnCIE.setLayoutData(cieData);
		
		
		//final Text t = new Text(shell, SWT.BORDER | SWT.MULTI);
		
		final Text[] threadText = new Text[arraySize];
		final Text[] colorText = new Text[arraySize];

		 for(int i = 0; i<arraySize; i++){
			 	threadText[i] = new Text(shell, SWT.SINGLE);
				FormData textData = new FormData(textWidth,textHeight);
				textData.left = new FormAttachment(0,textX0);
				textData.top = new FormAttachment(0, textY0+ i*textYOffset);
				threadText[i].setLayoutData(textData);
		}
		 
		for(int i = 0; i<arraySize; i++){
			 	colorText[i] = new Text(shell, SWT.SINGLE);
				FormData textData = new FormData(textWidth,textHeight);
				textData.left = new FormAttachment(0,textX0+textXOffset);
				textData.top = new FormAttachment(0, textY0+ i*textYOffset);
				colorText[i].setLayoutData(textData);
		}
		
		btnPicker.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				ColorDialog cd = new ColorDialog(shell);
		        cd.setText("Color Select");
		        cd.setRGB(new RGB(255, 255, 255));
		        c = cd.open();
		        if (c == null){
		        	return;
		        }
		        Color color = new Color(display, c);
		        for (int i = 0; i < arraySize; i++){
		        	threadText[i].setBackground(color);
		        }

			}
		});
		btnRGB.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){

		        Color color = new Color(display, c);
				double[] weight = {0,0,0};
		        EmbroideryThread[] rgbClosest = EMBThreadChart.getClosestThreadList(new java.awt.Color(color.getRed(),color.getGreen(),color.getBlue()),arraySize,"RGB",weight);
		       for (int i = 0; i < arraySize; i++){
		        	Color swtColor = new Color(display,rgbClosest[i].getColor().getRed(),
		        			rgbClosest[i].getColor().getGreen(),rgbClosest[i].getColor().getBlue());
		        	colorText[i].setBackground(swtColor);
		        	colorText[i].setText(rgbClosest[i].getCode());
		        }
			}
		});
		btnHSL.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
	
		        Color color = new Color(display, c);
				double[] weight = {0.5,0.1,0.4};
		        EmbroideryThread[] hslClosest = EMBThreadChart.getClosestThreadList(new java.awt.Color(color.getRed(),color.getGreen(),color.getBlue()),arraySize,"HSB",weight);
		       for (int i = 0; i < arraySize; i++){
		        	Color swtColor = new Color(display,hslClosest[i].getColor().getRed(),
		        			hslClosest[i].getColor().getGreen(),hslClosest[i].getColor().getBlue());
		        	colorText[i].setBackground(swtColor);
		        	colorText[i].setText(hslClosest[i].getCode());
		        }
			}
		});
		btnYUV.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){

		        Color color = new Color(display, c);
				double[] weight = {0.4,0.2,0.2};
		        EmbroideryThread[] yuvClosest = EMBThreadChart.getClosestThreadList(new java.awt.Color(color.getRed(),color.getGreen(),color.getBlue()),arraySize,"YUV",weight);
		       for (int i = 0; i < arraySize; i++){
		        	Color swtColor = new Color(display,yuvClosest[i].getColor().getRed(),
		        			yuvClosest[i].getColor().getGreen(),yuvClosest[i].getColor().getBlue());
		        	colorText[i].setBackground(swtColor);
		        	colorText[i].setText(yuvClosest[i].getCode());
		        }
			}
		});
		btnCIE.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){

		        Color color = new Color(display, c);
				double[] weight = {0,0,0};
		        EmbroideryThread[] cieClosest = EMBThreadChart.getClosestThreadList(new java.awt.Color(color.getRed(),color.getGreen(),color.getBlue()),arraySize,"CIE",weight);
		       for (int i = 0; i < arraySize; i++){
		        	Color swtColor = new Color(display,cieClosest[i].getColor().getRed(),
		        			cieClosest[i].getColor().getGreen(),cieClosest[i].getColor().getBlue());
		        	colorText[i].setBackground(swtColor);
		        	colorText[i].setText(cieClosest[i].getCode());
		        }
			}
		});
		shell.setText("Closet Color");
		shell.setSize(width,height);
		
		centerWindow(shell);
		shell.open();
		
		while (!shell.isDisposed()){
			if (!display.readAndDispatch()){
				display.sleep();
			}
		}
		 display.dispose();
	}
	private void centerWindow(Shell shell){
		Rectangle bds = shell.getDisplay().getBounds();
		Point p = shell.getSize();

        int nLeft = (bds.width - p.x) / 2;
        int nTop = (bds.height - p.y) / 2;

        shell.setBounds(nLeft, nTop, p.x, p.y);
	}
	
	
	public static void main(String[] args){
		 Display display = new Display();
		 Window ex = new Window(display);
		 display.dispose();
	}
}
