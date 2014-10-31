package com.jameslow.limelight; 

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import com.explodingpixels.macwidgets.*;
import com.jameslow.*;
import com.jameslow.gui.*;
import com.jameslow.limelight.handlers.*;

public class Slides extends AbstractWindow implements ActionListener, MouseListener, ListSelectionListener {
	private LimelightSettings settings;
	private JList slides;
	private JScrollPane slidescroll;
	private JPanel top;
	private Object topmac;
	private JButton black, blank, next, prev, airplay;
	private TextDisplay display;
	private LimelightWindow limelight;
	private Navigator nav;
	private String filepath, title;
	private ItemSlide[] slidesdata;
	protected ItemViewer previewViewer, fullscreenViewer;

	public Slides(LimelightWindow limelight) {
		this(limelight,null);
	}
	public Slides(LimelightWindow limelight, JMenuBar menu) {
		super();
		this.limelight = limelight;
		settings = limelight.getSettings();
		title = "";
		
		nav = new Navigator(0);
		addKeyListener(limelight.getControls());
		if(menu != null) {
			setJMenuBar(menu);
		}
		setLayout(new BorderLayout());
		if (Main.OS().isOSX()) {
			MacUtils.makeWindowLeopardStyle(getRootPane());
			topmac = new UnifiedToolBar();
			add(((UnifiedToolBar) topmac).getComponent(),BorderLayout.PAGE_START);
		} else {
			top = new JPanel();
			add(top, BorderLayout.PAGE_START);	
		}
		black = createJButton("Black",this,limelight.getControls(),Main.OS().isOSX());
		blank = createJButton("Blank",this,limelight.getControls(),Main.OS().isOSX());
		prev = createJButton("Prev",this,limelight.getControls(),Main.OS().isOSX());
		next = createJButton("Next",this,limelight.getControls(),Main.OS().isOSX());
		airplay = createJButton("AirPlay",this,limelight.getControls(),Main.OS().isOSX());
		slides = new JList();
			slides.addMouseListener(this);
			slides.addKeyListener(limelight.getControls());
		slidescroll= new JScrollPane(slides,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(slidescroll, BorderLayout.CENTER);
		slides.addListSelectionListener(this);
		setVisible(getWindowSettings().getVisible());
	}
	public void postLoad() {}
	public void valueChanged(ListSelectionEvent arg0) {
		slides.ensureIndexIsVisible(slides.getSelectedIndex());
	}
	private JButton createCommonJButton(String name, ActionListener actionlistener, KeyListener keylistener) {
		JButton button = new JButton(name);
		button.addActionListener(actionlistener);
		button.addKeyListener(keylistener);
		return button;
	}
	private JButton createJButton(String name, ActionListener actionlistener, KeyListener keylistener, boolean mac) {
		JButton button = createCommonJButton(name,actionlistener,keylistener);
		if (mac) {
			button.putClientProperty("JButton.buttonType", "textured");
			((UnifiedToolBar) topmac).addComponentToLeft(button);
		} else { 
			top.add(button);
		}
		return button;
	}
	public void loadFile(String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
			if (file.isDirectory()) {
				//Use directory handler
			} else {
				String thisext = FileUtils.getExt(file.getName()).toLowerCase();
				if ("".compareTo(thisext) != 0 && limelight.extmap.containsKey(thisext)) {
					this.filepath = filepath;
					ItemHandler handler = limelight.extmap.get(thisext);
					slides.setCellRenderer(handler.getListCellRenderer());
					previewViewer = handler.getPreviewItemViewer();
					fullscreenViewer = handler.getFullscreenItemViewer(); 
					slidesdata = handler.getItemData(file);
					slides.setListData(slidesdata);
					title = FileUtils.stripExt(filepath);
					nav.newNav(slidesdata.length);
				} else {
					//Unknown file ext
					((DefaultListModel)slides.getModel()).clear();
					filepath = "";
					title = "";
				}
			}
		}
		if ("".compareTo(title) == 0 ) {
			setTitle(getDefaultTitle());
		} else {
			setTitle(getDefaultTitle() + " - " + title);
		}
	}
	public ItemSlide getSlide(int index) {
		nav.setNotOnItem(false);
		nav.setCurrentItem(index);
		slides.setSelectedIndex(index);
		return slidesdata[index];
	}
	public Navigator getNavigator() {
		return nav;
	}
	public void mouseClicked(MouseEvent e) {
		if (slides.getSelectedIndex() != -1) {
			limelight.jumpSlide(slides.getSelectedIndex());
		}
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == next) {
			limelight.nextSlide();
		} else if (e.getSource() == prev) {
			limelight.prevSlide();
		} else if (e.getSource() == blank) {
			limelight.blankSlide();
		} else if (e.getSource() == black) {
			limelight.blackSlide();	
		} else if (e.getSource() == airplay) {
			limelight.toggleAirPlay();
		}
	}
	public String getDefaultTitle() {
		return "Slides";
	}
	public String getItemTitle() {
		return title;
	}
	public WindowSettings getDefaultWindowSettings() {
		//return new WindowSettings(320,745,643,0,true,JFrame.NORMAL);
		return new WindowSettings(320,262,320,240+Main.OS().getMenuBar()+LimelightWindow.SPACE,true,JFrame.NORMAL);
	}
	public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4) {
		// TODO Auto-generated method stub
		return null;
	}
	public int getViewportWidth() {
		return slidescroll.getWidth();
	}
}