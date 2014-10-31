package com.jameslow.limelight;

import java.awt.event.*;

public class Controls implements KeyListener, MouseListener {
	private LimelightWindow limelight;
	
	public Controls(LimelightWindow limelight) {
		this.limelight = limelight;
	}
	public void keyTyped(KeyEvent e) {
		checkConsume(e);
	}
	public void keyPressed(KeyEvent e) {
		checkConsume(e);
	}
	private void checkConsume(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE
			|| e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT
			|| e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
			|| e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			e.consume();
		}
	}
	public void keyReleased(KeyEvent e) {
		checkConsume(e);
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			limelight.setFullScreen(false);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			limelight.prevSlide();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			limelight.nextSlide();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			limelight.prevSlide();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			limelight.nextSlide();
		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
			limelight.prevItem(true);
		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			limelight.nextItem(true);
		}
	}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
