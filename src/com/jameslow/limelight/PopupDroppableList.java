package com.jameslow.limelight;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.jameslow.*;

public class PopupDroppableList extends DroppableList implements ActionListener {
	private LimelightWindow limelight;
	private JMenuItem add,remove,removeall,display,edit,show,export,exportall,exportppt,exportiphoto;
	
	public PopupDroppableList(LimelightWindow limelight) {
		this(limelight,false);
	}
	public PopupDroppableList(LimelightWindow limelight, boolean allowadd) {
		this.limelight = limelight;
		setAllowAdd(allowadd);
		createMenu();
	}
	protected boolean readOnly() {
		return !getAllowAdd();
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add) {
			if (getSelectedIndex() >= 0) {
				for(int i : getSelectedIndices()) {
					limelight.addItem(getFilepath(i));
				}
			}
		} else if (e.getSource() == remove) {
			if (getSelectedIndex() >= 0) {
				removeSelectedFilepath();
			}
		} else if (e.getSource() == removeall) {
			removeAllFilepaths();
		} else if (e.getSource() == display) {
			if (getSelectedIndex() >= 0) {
				limelight.loadFile(getSelectedFilePath());
			}
		} else if (e.getSource() == show) {
			if (getSelectedIndex() >= 0) {
				Main.OS().showFile(getSelectedFilePath());
			}
		} else if (e.getSource() == edit) {
			if (getSelectedIndex() >= 0) {
				Main.OS().openFile(getSelectedFilePath());
			}
		} else if (e.getSource() == export) {
			if (getSelectedIndex() >= 0) {
				int[] indices = getSelectedIndices();
				String[] files = new String[indices.length];
				for (int i = 0; i<indices.length; i++) {
					files[i] = getFilepath(indices[i]);
				}
				limelight.exportSlides(files);
			}
		} else if (e.getSource() == exportall) {
			limelight.exportAllSlides();
		} else if (e.getSource() == exportppt) {
			limelight.exportPowerPoint();
		} else if (e.getSource() == exportiphoto) {
			limelight.exportiPhoto();
		}
	}
	private void createMenu() {
		List<JMenuItem> list = new ArrayList<JMenuItem>();
		if (readOnly()) {
			add = new JMenuItem("Add");
			add.addActionListener(this);
			list.add(add);
		} else {
			remove = new JMenuItem("Remove");
			remove.addActionListener(this);
			list.add(remove);
			
			removeall = new JMenuItem("Remove All");
			removeall.addActionListener(this);
			list.add(removeall);
		}
		display = new JMenuItem("Display");
		display.addActionListener(this);
		list.add(display);
		edit = new JMenuItem("Edit File");
		edit.addActionListener(this);
		list.add(edit);
		show = new JMenuItem("Show File");
		show.addActionListener(this);
		list.add(show);
		
		if (!readOnly()) {
			exportall = new JMenuItem("Export All As Images");
			exportall.addActionListener(this);
			list.add(exportall);
			exportppt = new JMenuItem("Export All As PowerPoint");
			exportppt.addActionListener(this);
			list.add(exportppt);
			if (Main.OS().isOSX()) {
				exportiphoto = new JMenuItem("Export All To iPhoto");
				exportiphoto.addActionListener(this);
				list.add(exportiphoto);
			}
		}
		export = new JMenuItem("Export Selected As Images");
		export.addActionListener(this);
		list.add(export);
		
		GUIUtils.addPopupListener(this.getComponent(), list);
	}
}
