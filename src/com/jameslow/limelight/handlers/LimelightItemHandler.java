package com.jameslow.limelight.handlers;

import com.jameslow.*;
import com.jameslow.limelight.*;

public abstract class LimelightItemHandler implements ItemHandler {
	protected String[] extensions;
	protected LimelightSettings settings = (LimelightSettings)Main.Settings();
	protected LimelightWindow window;
	protected ItemViewer preview, fullscreen;
	
	public LimelightItemHandler(LimelightWindow window, String extproperty) {
		this.window = window;
		extensions = settings.getPropertyList(extproperty);
	}
	public String[] getExtensions() {
		return extensions;
	}
	public boolean handlesDirectories() {
		return false;
	}
	public ItemViewer getPreviewItemViewer() {
		if (preview == null) {
			preview = getNewItemViewer();
		}
		return preview;
	}
	public ItemViewer getFullscreenItemViewer() {
		if (fullscreen == null) {
			fullscreen = getNewItemViewer();
		}
		return fullscreen;
	}
	protected abstract ItemViewer getNewItemViewer();
}
