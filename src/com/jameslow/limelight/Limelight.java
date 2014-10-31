package com.jameslow.limelight;

import java.util.*;

import com.jameslow.*;

public class Limelight extends Main {
	public Limelight(String args[]) {
		super(args,null,null,LimelightSettings.class.getName(),LimelightWindow.class.getName(),null,null,LimelightPref.class.getName());
		/*
		Installer installer = null;
		 InstallManager imanager = new InstallManager();
		 Map installers = imanager.getInstallers();
		 Iterator iter = installers.entrySet().iterator();
		 String name = null;
		 while (iter.hasNext()) {
			 Map.Entry mapEntry = (Map.Entry) iter.next();
			 name = (String) mapEntry.getKey();
			 System.out.println(name);
		 	//installer = (Installer) mapEntry.getValue();
		 }
		 installer = imanager.getInstaller("CrossWire");
		 try {
			      installer.reloadBookList();
		 } catch (InstallException e) {
           e.printStackTrace();
         }
		 
			List books = Books.installed().getBooks();
			//List books = installer.getBooks();

			for (int i=0; i<books.size(); i++) {
				System.out.println(((Book) books.get(i)).getInitials());
			}
 
			         Book book = (Book) installer.getBook("ESV");

			 try {

				                  if (Books.installed().getBook("ESV") != null) { //$NON-NLS-1$
				                      Books.installed().removeBook(book);
				                      book.getDriver().delete(book);
				                  }
				              } catch (BookException e1) {
				                  e1.printStackTrace();
				              }
				  
				              try {
				                  // Now install it. Note this is a background task.
				                  installer.install(book);
				              } catch (InstallException e) {
				                  e.printStackTrace();
				              }
		 */
	}
	public static void main(String args[]) {
		instance = new Limelight(args);
	}
	protected boolean onQuit() {
		if (window != null) {
			((LimelightWindow)window).onQuit();
		}
		return true;
	}
	public static void refreshFolders() {
		if (window != null) {
			((LimelightWindow)window).refreshFolders();
		}
	}
}
