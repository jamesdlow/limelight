package com.jameslow.limelight;

public class Navigator {
	private int currentitem=0;
	private int itemcount=0;
	private boolean notonitem = true;

	public Navigator(int itemcount) {	
		newNav(itemcount);
	}
	public void newNav(int itemcount) {
		setItemCount(itemcount);
		setCurrentItem(-1);
		setNotOnItem(true);	
	}
	public int getItemCount() {
		return itemcount;
	}
	public void setItemCount(int value) {
		itemcount = value;
	}
	public boolean atEnd() {
		return getCurrentItem() >= getItemCount() - 1;
	}
	public boolean atBeginning() {
		return getCurrentItem() <= 0;
	}
	public int getCurrentItem() {
		return currentitem;
	}
	public void setCurrentItem(int index) {
		currentitem = index;
	}
	public boolean prevItem() {
		if (atBeginning()) {
			return false;
		} else {
			if(!getNotOnItem()) {
				setCurrentItem(getCurrentItem() - 1);
			}
			return true;
		}
	}
	public boolean nextItem() {
		if (atEnd()) {
			return false;
		} else {
			if(!getNotOnItem()) {
				setCurrentItem(getCurrentItem() + 1);
			}
			if(getCurrentItem() < 0) {
				setCurrentItem(0);
			}
			return true;
		}
	}
	public boolean getNotOnItem() {
		return notonitem;
	}
	public void setNotOnItem(boolean value) {
		notonitem = value;
	}
}