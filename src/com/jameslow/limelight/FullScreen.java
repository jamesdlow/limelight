package com.jameslow.limelight;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.jameslow.*;
import com.jameslow.limelight.handlers.ItemSlide;
import com.jameslow.limelight.handlers.ItemViewer;
import com.jameslow.limelight.handlers.TextDisplay;

public class FullScreen extends JFrame implements MouseListener {
	private boolean isFullScreen = false;
	private int screen = -1;
	private int numberScreens = -1;
	private GraphicsDevice gd;
	private LimelightSettings settings;
	private boolean blankCursor = false;
	private ItemViewer itemviewer;
	private LimelightWindow limelight;

	public FullScreen(LimelightWindow limelight) {
		super();
		setUndecorated(true);
		this.limelight = limelight;
		settings = (LimelightSettings) Main.Settings();
		setSize(320,240);
		setBackground(Color.BLACK);
		addMouseListener(this);
		addKeyListener(limelight.getControls());
		
		setLayout(new BorderLayout());
	}
	public void setItemViewer(ItemViewer viewer) {
		if (itemviewer != null) {
			itemviewer.getItemPanel().setVisible(false);
		} else {
			viewer.getItemPanel().setVisible(false);
		}
		itemviewer = viewer;
		JPanel panel = itemviewer.getItemPanel();
		panel.addKeyListener(limelight.getControls());
		//panel.addMouseListener(this);
		add(panel,BorderLayout.CENTER);
		panel.setVisible(true);
	}
	public void toggleCursor() {
		/*
		if (blankCursor) {
			setCursor(null);
		} else {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Cursor invisCursor = tk.createCustomCursor(tk.createImage(""),new Point(),null);
			setCursor(invisCursor);
		}
		*/
		blankCursor = !blankCursor;
	}
	public void setFullScreen(boolean fs) {
		if (fs) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] gs = ge.getScreenDevices();
			numberScreens = gs.length;
			//0 is auto mode, using the last monitor
			if (settings.DisplayScreen > numberScreens || settings.DisplayScreen == 0) {
				screen = numberScreens - 1;
			} else {
				screen = settings.DisplayScreen - 1;
			}
			gd = gs[screen];
			try {
				setBounds(gd.getDefaultConfiguration().getBounds());
				if (numberScreens == 1) {
					gd.setFullScreenWindow(this);
				}
				setVisible(true);
				isFullScreen = true;
				limelight.onEnterFullScreen();
			} catch(Exception e) {
				e.printStackTrace();
				Main.Logger().warning(e.getMessage());
			}
		} else {
			try {
				//if (gd.getFullScreenWindow().equals(this)) {
					gd.setFullScreenWindow(null);
				//}
				gd = null;
				isFullScreen = false;
				limelight.onExitFullScreen();
				setVisible(false);
			} catch(Exception e) {
				Main.Logger().warning(e.getMessage());
			}
		}
	}
	public void toggleFullScreen() {
		if (isFullScreen) {
			setFullScreen(false);
		} else {
			setFullScreen(true);
		}
	}
	public boolean overOtherWindows() {
		//TODO: could improve this to actually determine if it is on the same screen as other windows
		return isFullScreen && screen == 0 && numberScreens == 1;
	}
	public void setTextIfNullViewer() {
		if (itemviewer == null) {
			setItemViewer(limelight.getTextHandler().getFullscreenItemViewer());
		}
	}
	public void blackSlide() {
		setTextIfNullViewer();
		itemviewer.blackSlide();
	}
	public void blankSlide() {
		setTextIfNullViewer();
		itemviewer.blankSlide();
	}
	public void setSlide(ItemSlide slide, int index) {
		if (itemviewer != limelight.slides.fullscreenViewer) {
			setItemViewer(limelight.slides.fullscreenViewer);
		}
		itemviewer.displayObject(slide,index);
		itemviewer.getItemPanel().repaint();
	}
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == e.BUTTON1 && e.getClickCount() == 2) {
			toggleFullScreen();
		}/* else if (e.getClickCount() == 1) {
			//toggleCursor();
			if (e.getButton() == e.BUTTON1) {
				limelight.nextSlide();
			} else if (e.getButton() == e.BUTTON3) {
				limelight.prevSlide();
			}
		}*/
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
