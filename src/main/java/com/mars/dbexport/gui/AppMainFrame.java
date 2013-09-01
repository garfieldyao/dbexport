package com.mars.dbexport.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.utils.ClientUtils;

public class AppMainFrame extends JFrame {
	private static final long serialVersionUID = -3503038684028459652L;
	
	
	public AppMainFrame() {
		// TODO Auto-generated constructor stub
		initGUI();
	}

	private void initGUI() {
		// TODO Auto-generated method stub
		Dimension screenSize = ClientUtils.getScreenSize();
		double sheight = screenSize.height * 2 / 3;
		double swidth = sheight * 1.5;
		this.setSize((int) swidth, (int) sheight);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Demo");
		this.setIconImage(AppContext.getLogoIcon());
		ClientUtils.centerWindow(this);
	}
}
