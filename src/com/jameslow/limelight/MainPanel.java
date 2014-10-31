package com.jameslow.limelight;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import com.jameslow.*;
import com.jameslow.gui.SearchText;

public class MainPanel extends JPanel implements MouseListener,ChangeListener {
	private LimelightSettings settings;
	private LimelightWindow limelight;
	
	private JScrollPane folderscroll, servicescroll;
	private SearchText filter;
	private Folder folder;
	private OrderOfService service;
	private JTabbedPane folderstab, servicetab;
	private JPanel left, right;
	private JSplitPane pane;
	private Navigator nav;

	public MainPanel (LimelightWindow limelight) {
		super();
		this.limelight = limelight;
		settings = limelight.getSettings();
		setLayout(new BorderLayout());
		nav = new Navigator2();
		
		folderstab = new JTabbedPane();
			refreshFolders();
			folderstab.addChangeListener(this);
			folderstab.addKeyListener(limelight.getControls());
			//folderstab.setMinimumSize(new Dimension(limelight.getWindowSettings().getWidth()/2,limelight.getWindowSettings().getHeight()));						
		servicetab = new JTabbedPane();
			service = new OrderOfService(limelight,nav);
				service.getComponent().addKeyListener(limelight.getControls());
				service.getComponent().addMouseListener(this);
				servicescroll= new JScrollPane(service.getComponent(),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			servicetab.addKeyListener(limelight.getControls());
			servicetab.addTab("Playlist", servicescroll);
			//servicetab.setMinimumSize(new Dimension(limelight.getWindowSettings().getWidth()/2 - 3,limelight.getWindowSettings().getHeight()));
		pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, folderstab, servicetab);
			pane.setDividerSize(3);
			pane.setContinuousLayout(true);
			pane.setResizeWeight(0.5);
			left = new JPanel();
				left.setLayout(new BorderLayout());
				folder = new Folder(limelight);
					folder.setPath(settings.Folders.get(folderstab.getComponentAt(folderstab.getSelectedIndex()).getName()));
					folder.getComponent().addKeyListener(limelight.getControls());
					folder.getComponent().addMouseListener(this);
					folderscroll = new JScrollPane(folder.getComponent(),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				left.add(folderscroll, BorderLayout.CENTER);
				filter = new SearchText(true,folder);
			left.add(filter, BorderLayout.PAGE_START);
			((JComponent)folderstab.getComponentAt(0)).add(left);
		add(pane, BorderLayout.CENTER);
	}
	public void setDividerLocation(double proportion) {
		pane.setDividerLocation(proportion);
	}
	
	//Public functions
	public void refreshFolders() {
		folderstab.removeAll();
		for(String key: settings.Folders.keySet()) {
			JPanel temppanel = new JPanel();
			temppanel.setName(key);
			temppanel.setLayout(new BorderLayout());
			folderstab.add(temppanel);
		}	
	}
	
	public void addItem(String filepath) {
		addItem(filepath,service.getSelectedIndex());
	}
	public void addItem(String filepath, int index) {
		service.addFilepath(filepath,index);
	}
	public String[] getItems() {
		return service.getFilepaths();
	}
	public void addItems(String[] filepaths) {
		service.addFilepaths(filepaths);
	}
	public void removeItems() {
		service.removeAllFilepaths();
	}
	public Navigator getNavigator() {
		return nav;
	}
	public String getFilepath(int index) {
		nav.setNotOnItem(false);
		nav.setCurrentItem(index);
		service.setSelectedIndex(index);
		return service.getFilepath(index);
	}
	public void stateChanged(ChangeEvent e) {
		filter.setText("");
		if (folderstab.getSelectedIndex() >= 0) {
			folder.setPath(settings.Folders.get(folderstab.getComponentAt(folderstab.getSelectedIndex()).getName()));
			((JComponent)folderstab.getComponentAt(folderstab.getSelectedIndex())).add(left);
		}
	}
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			if (e.getSource() == service.getComponent() && service.getSelectedIndex() >= 0) {
				limelight.loadFile(service.getSelectedIndex());
			} else if (e.getSource() == folder.getComponent() && folder.getSelectedIndex() >= 0) {
				limelight.loadFile(folder.getSelectedFilePath());
			} else if (limelight.search != null && e.getSource() == limelight.search.getFileList().getComponent() && limelight.search.getFileList().getSelectedIndex() >= 0) {
				limelight.loadFile(limelight.search.getFileList().getSelectedFilePath());
			}
		}
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	private class Navigator2 extends Navigator {
		public Navigator2() {
			super(0);
		}
		public int getItemCount() {
			return service.getSize();
		}
	}
}
