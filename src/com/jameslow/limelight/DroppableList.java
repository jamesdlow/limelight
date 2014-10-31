package com.jameslow.limelight;

import javax.swing.*;
import com.jameslow.*;
import com.jameslow.gui.*;

public class DroppableList extends Droppable {
	protected DefaultListModel model = new DefaultListModel();
	protected DroppableListener listener = new DroppableListener() {
		public void addFilePath(String filepath) {
			model.addElement(FileUtils.getFilename(filepath));
		}
		public void addFilePath(int index, String filepath) {
			model.add(index, FileUtils.getFilename(filepath));
		}
		public void rearrangeFilePath(int index, int to) {

		}
		public void removeFilePath(int index) {
			model.remove(index);
		}
	};
	public DroppableList() {
		init(new JList(),listener);
		list.setModel(model);
	}	
}