package com.jameslow.limelight;

import java.awt.Color;

import com.jameslow.Main;
import com.jameslow.gui.ImagePane;

public class LimelightImagePane extends ImagePane {
	public LimelightImagePane() {
		super();
	}
	public void setBlack(boolean value) {
		if (value) {
			setBackground(Color.BLACK);
			setShowImage(false);
		} else {
			LimelightSettings settings = (LimelightSettings) Main.Settings();
			if ("".compareTo(settings.DisplayBackground) != 0) {
				setBackground(new Color(0f,0f,0f,1f));
			} else {
				setBackground(settings.DisplayColor);			
			}
			setShowImage(true);			
		}
	}
}
