package com.jameslow.limelight;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import com.jameslow.*;

public class LimelightPref extends PrefPanel implements ActionListener {
	private LimelightSettings settings;
	private JComboBox display;
	private JTextField background;
	private JButton backgroundbrowse;
	private JTextField folder;
	private JButton folderbrowse;
	
	public LimelightPref() {
		super();
		setLayout(new GridLayout(3,2));
		Dimension size = new Dimension(300,80); 
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		
		settings = (LimelightSettings) Main.Settings();
		
		JLabel displaylabel = new JLabel("Display:");
		add(displaylabel);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		String[] devices = new String[gs.length+1];
		devices[0] = "Auto";
		for (int i=0; i < gs.length; i++) {
			devices[i+1] = "Screen" + (i+1);
		}
		display = new JComboBox(devices);
		add(display);
		
		final PrefPanel parent = this;
		backgroundbrowse = new JButton("Background");
			backgroundbrowse.addActionListener(this);
		add(backgroundbrowse);

		background = new JTextField();
			background.setText(settings.DisplayBackground);
			background.setEditable(false);
		add(background);
		
		folderbrowse = new JButton("Folder");
			folderbrowse.addActionListener(this);
		add(folderbrowse);
		
		folder = new JTextField();
			folder.setText(settings.Folders.get(settings.Folders.keySet().toArray()[0]));
			folder.setEditable(false);
		add(folder);
		
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backgroundbrowse) {
			File file = Main.OS().openFileDialog(this.getParentFrame());
			if (file != null) {
				background.setText(file.getPath());
			}
		} else if (e.getSource() == folderbrowse) {
			File file = Main.OS().openFileDialog(this.getParentFrame(), true);
			if (file != null) {
				folder.setText(file.getPath());   
            }
		}
	}
	public void savePreferences() {
		settings.DisplayBackground = background.getText();
		String key = (String) settings.Folders.keySet().toArray()[0];
		settings.Folders.remove(key);
		settings.Folders.put(key, folder.getText());
		settings.DisplayScreen = display.getSelectedIndex();
		settings.saveSettings();
	}
}
