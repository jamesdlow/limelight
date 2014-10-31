package com.jameslow.limelight.handlers;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import javax.swing.*;
import com.jameslow.*;
import com.jameslow.limelight.*;
import com.sun.pdfview.*;

public class PDFItemHandler extends LimelightItemHandler {
	
	public PDFItemHandler(LimelightWindow window) {
		super(window, LimelightSettings.EXT_PDF);
	}

	public ItemSlide[] getItemData(File file)  {
        try {
        	RandomAccessFile raf = new RandomAccessFile(file, "r");
        	FileChannel channel = raf.getChannel();
        	ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY,0, channel.size());
        	PDFFile pdffile = new PDFFile(buf);
        	ItemSlide[] slides = new ItemSlide[pdffile.getNumPages()];
        	for (int i=0; i<slides.length; i++) {
        		slides[i] = new ItemSlide(file.getAbsolutePath(),i,pdffile.getPage(i));
        	}
        	return slides;
        } catch (Exception e) {
        	Main.Logger().warning("Could not load PDF file "+file.getAbsolutePath()+": "+e.getMessage());
        	return null;
        }
	}

	protected ItemViewer getNewItemViewer() {
		return new PDFViewer();
	}

	
	public ComponentListener getComponentListener() {
		return null;
	}

	public ListCellRenderer getListCellRenderer() {
		return new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				ItemSlide slide = (ItemSlide)value;
				PDFPage page = (PDFPage)slide.getContent();
				PagePanel panel = new PagePanel();
				panel.showPage(page);
				/*
				ListCellRenderer rend = new DefaultListCellRenderer();
				Component comp = rend.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (comp instanceof JComponent) {
					panel.setBorder(((JComponent) comp).getBorder());
				}
				*/
				return panel;
			}
		};
	}

	public boolean isSinglePage(String filepath) {
		return false;
	}

}
