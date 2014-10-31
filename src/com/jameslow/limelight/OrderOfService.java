package com.jameslow.limelight;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import com.jameslow.*;
import com.jameslow.gui.LimegreenString;

public class OrderOfService extends PopupDroppableList {
	protected Navigator nav;
	protected DroppableListener listener = new DroppableListener() {
		public void addFilePath(String filepath) {
		}
		public void addFilePath(int index, String filepath) {
			if (index <= nav.getCurrentItem()) {
				nav.setCurrentItem(nav.getCurrentItem()+1);
			}
		}
		public void rearrangeFilePath(int index, int to) {
			if (index == nav.getCurrentItem() && to > index) {
				nav.setCurrentItem(to);
			} else if (index == nav.getCurrentItem() && to < index) {
				nav.setCurrentItem(to+1);
			} else if (to <= nav.getCurrentItem() && index  > nav.getCurrentItem()) {
				nav.setCurrentItem(nav.getCurrentItem()+1);
			} else if (to >= nav.getCurrentItem() && index  < nav.getCurrentItem()) {
				nav.setCurrentItem(nav.getCurrentItem()-1);
			}
		}
		public void removeFilePath(int index) {
			if (index == nav.getCurrentItem()) {
				nav.setNotOnItem(true);
			}
			if (index <= nav.getCurrentItem()) {
				nav.setCurrentItem(nav.getCurrentItem()-1);
			}
		}
	};
	
	public OrderOfService(final LimelightWindow limelight, Navigator nav) {
		super(limelight,true);
		this.nav = nav;
		addListener(listener);
		setAllowRearrage(true);
		setAllowRejectedRemove(true);
	}
}
