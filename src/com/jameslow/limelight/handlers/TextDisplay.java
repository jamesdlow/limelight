package com.jameslow.limelight.handlers;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import com.jameslow.*;
import com.jameslow.limelight.*;

public class TextDisplay extends LimelightImagePane implements ComponentListener {
	private JLabel MainText, Title, Copyright, Author, Slide, Message;
	private Map<String,JLabel> Labels = new HashMap<String,JLabel>();
	private LimelightWindow limelight;
	private LimelightSettings settings;

	public TextDisplay(final LimelightWindow limelight) {
		super();
		this.limelight = limelight;
		this.settings = limelight.getSettings();
		try {
			setImage(settings.DisplayBackground);
		} catch (IOException e) {
			Main.Logger().warning("Cannot load background: " + settings.DisplayBackground);
		}
		addComponentListener(this);
		setLayout(null);

		MainText = createJLabel(this, settings.MainText);
		Title = createJLabel(this, settings.Title);
		Copyright = createJLabel(this, settings.Copyright);
		Author = createJLabel(this, settings.Author);
		Slide = createJLabel(this, settings.Slide);
		Message = createJLabel(this, settings.Message);
		//TODO: Do this as a setting
		//setDrawMode(ImagePane.DRAW_MODE_ASPECT);
		setBlack(false);
	}
	public void setSlide(String title, String main, String author, String copyright, String slide, int index) {
		setBlack(false);
		setTextItem(MainText,settings.MainText,main,index);
		setTextItem(Title,settings.Title,title,index);
		setTextItem(Copyright,settings.Copyright,copyright,index);
		setTextItem(Author,settings.Author,author,index);
		setTextItem(Slide,settings.Slide,slide,index);
	}
	public void setMessage(String message) {
		//isBalck = false; //?
	}
	public void blankSlide() {
		setBlack(false);
		setVisibleAll(false);
	}
	public void blackSlide() {
		setBlack(true);
		setVisibleAll(false);
	}
	private void setVisibleAll(boolean value) {
		for(Map.Entry<String, JLabel> entry : Labels.entrySet()) {
			entry.getValue().setVisible(value);
		}
	}
	private void clearAll() {
		for(Map.Entry<String, JLabel> entry : Labels.entrySet()) {
			entry.getValue().setText("");
		}
	}
	private JLabel createJLabel(JComponent parent, LimelightSettings.TextItem item) {
		JLabel label = new JLabel("");
		label.setForeground(item.FontColor);
		label.setFont(item.font);
		label.setName(item.Name);
		label.setVisible(item.Show);
		setPosition(label,this,item);
		parent.add(label);
		Labels.put(item.Name,label);
		return label;
	}
	private void setTextItem(JLabel label, LimelightSettings.TextItem item, String text, int index) {
		String align = "<p";
		if (item.FontAlign == SwingConstants.LEFT) {
			align = align + " align=left>";
		} else if (item.FontAlign == SwingConstants.RIGHT) {
			align = align + " align=right>";
		} else if (item.FontAlign == SwingConstants.CENTER) {
			align = align + " align=center>";
		} else {
		 	align = align + " align=justify>";
		}
		label.setVisible(item.Show && ((index == 0 && item.ShowFirst) || !item.ShowFirst));
		label.setText("<html>" + align + text.replaceAll(Main.OS().lineSeparator(),"<br>") + "</html>");
		setPosition(label,this,item);
	}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {
		for(Map.Entry<String, JLabel> entry : Labels.entrySet()) {
			JLabel label = entry.getValue();
			LimelightSettings.TextItem item = settings.TextItems.get(entry.getKey()); 
			label.setFont(label.getFont().deriveFont(item.FontSize * getHeight()));			
			setPosition(label,this,item);
		}
	}
	public void componentShown(ComponentEvent e) {}
	
	private void setPosition(JComponent child, JComponent parent, int left, int top, int width, int height) {
		Insets insets = parent.getInsets();
		child.setBounds(left + insets.left, top + insets.top, width, height);
	}
	private void setPosition(JComponent child, JComponent parent, int left, int top) {
		Dimension size = child.getPreferredSize();
		setPosition(child, parent, left, top, size.width, size.height);
	}
	private void setPosition(JComponent child, JComponent parent, LimelightSettings.TextItem item) {
		Dimension size = child.getPreferredSize();
		int x, y;
		if (item.Horizontal == SwingConstants.LEFT) {
			x = 0;
		} else if (item.Horizontal == SwingConstants.RIGHT) {
			x = - size.width;
		} else {
			x = - size.width / 2;
		}
		if (item.Vertical == SwingConstants.TOP) {
			y = 0;
		} else if (item.Vertical == SwingConstants.BOTTOM) {
			y = - size.height;
		} else {
			y = - size.height / 2;
		}
		setPosition(child,parent,(int)(item.Left * parent.getWidth()) + x, (int)(item.Top * parent.getHeight()) + y, size.width, size.height);
	}
}
