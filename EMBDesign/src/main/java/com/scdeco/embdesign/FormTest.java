package com.scdeco.embdesign;


import java.util.ArrayList;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;



public class FormTest {

	
	RGB c = new RGB(255,255,255);
	
	public FormTest(final Display display){
		final int width = 400;
		final int height = 200;
		final int buttonWidth = 120;
		final int buttonHeight = 30;
		final int textWidth = 230;
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
		
		Button okBtn = new Button(shell, SWT.PUSH);
		okBtn.setText("Ok");

		FormData okData = new FormData(buttonWidth, buttonHeight);
		okData.right = new FormAttachment(0,width - buttonX0);
		okData.top = new FormAttachment(0,buttonY0);
		okBtn.setLayoutData(okData);
		
		
		
		
		final Text valueText = new Text(shell, SWT.SINGLE);
		FormData valueData = new FormData(textWidth,textHeight);
		valueData.right = new FormAttachment(okBtn,-textX0);
		valueData.top = new FormAttachment(0, textY0);
		valueText.setLayoutData(valueData);
		
		final Label threadText = new Label(shell, SWT.SINGLE);
		FormData textData = new FormData(360,80);
		textData.left = new FormAttachment(0,textX0);
		textData.top = new FormAttachment(0, 6*textY0);
		threadText.setLayoutData(textData);
		threadText.setBackground(new Color(display,255,255,255));
		
		okBtn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				String code = "S"+valueText.getText();
				java.awt.Color awtColor = EMBThreadChart.getColor(code);
				Color color = new Color(display,awtColor.getRed(),awtColor.getGreen(),awtColor.getBlue());
				double weight[] = {0.33,0.33,0.33};
				threadText.setBackground(color);
				threadText.setText(code+" "+"Red: "+awtColor.getRed()+" "+"Green: "+awtColor.getGreen()+" "
				+"Blue: "+awtColor.getBlue()+ " Distance: " + EMBThreadChart.getDistanceOfHSL(awtColor,new java.awt.Color(246,213,97),weight));
				valueText.setText("");
			}
		});
		
		shell.setText("Color");
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
		 FormTest ex = new FormTest(display);
		 display.dispose();
	}
}

