package com.jameslow.limelight.handlers;

import java.awt.event.ComponentListener;
import java.io.File;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class DirectoryItemHandler implements ItemHandler {

	public ComponentListener getComponentListener() {
		return null;
	}

	public String[] getExtensions() {
		return new String[0];
	}

	public ListCellRenderer getListCellRenderer() {
		return null;
	}

	public boolean handlesDirectories() {
		return true;
	}

	public boolean isSinglePage(String filepath) {
		return false;
	}

	public ItemSlide[] getItemData(File file) {
		return null;
	}
	
	public ItemViewer getPreviewItemViewer() {
		return null;
	}

	public ItemViewer getFullscreenItemViewer() {
		return null;
	}

}
