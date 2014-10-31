package com.jameslow.limelight;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.usermodel.SlideShow;

import com.jameslow.*;
import com.jameslow.FileUtils.Filefilter;
import com.jameslow.limelight.handlers.*;

public class LimelightWindow extends MainWindow {
	protected Action newAction, openAction, saveAction,
					 undoAction, cutAction, copyAction, pasteAction, clearAction,
					 selectAllAction, fullScreenAction, previewAction, slidesAction,
					 nextSlideAction, prevSlideAction, blankSlideAction, blackSlideAction,
					 mainWindowAction, compactWindowAction, largeWindowAction, searchAction;
	protected FullScreen fs;
	protected Preview prev;
	protected Slides slides;
	protected MainPanel mainpanel;
	protected Search search;
	protected LimelightSettings settings;
	protected Controls controls;
	protected OSSpecific os;
	protected List<ItemHandler> handlers = new ArrayList<ItemHandler>();
	protected Map<String,ItemHandler> extmap = new HashMap<String,ItemHandler>();
	protected ItemHandler directoryhandler;
	protected boolean remote = true;
	protected ItemHandler texthandler;
	protected boolean mlast, slast, plast, hidwindows;
	protected static final int SPACE = 3;
	protected AirPlay airplay;
	
	public LimelightWindow() {
		super();
		settings = (LimelightSettings) Main.Settings();
		controls = new Controls(this);
		os = Limelight.OS();
		getContentPane().setLayout(new BorderLayout());
		mainpanel = new MainPanel(this);
			add(mainpanel, BorderLayout.CENTER);
		fs = new FullScreen(this);
		if (!os.addQuit()) {
			slides = new Slides(this, createMenu());
			prev = new Preview(this, createMenu());
		} else {
			slides = new Slides(this);
			prev = new Preview(this);
		}
		setUpHandlers();
		loadPlaylist();
		setUpRemote();
	}
	public void postLoad() {
		setDividerLocation(0.5);
		prev.blackSlide();
		fs.blackSlide();
	};
	public void setDividerLocation(double proportion) {
		mainpanel.setDividerLocation(proportion);
	}
	public boolean stopAirPlay() {
		return stopAirPlay(false);
	}
	public boolean stopAirPlay(boolean showMessage) {
		if (airplay != null) {
			airplay.stop();
			airplay = null;
			if (showMessage) {
				JOptionPane.showMessageDialog(this,"AirPlay has been stoped. Click again to start.","AirPlay stopped",JOptionPane.PLAIN_MESSAGE);
			}
			return true;
		}
		return false;
	}
	public void toggleAirPlay() {
		if (!stopAirPlay(true) && airplay == null) {
			try {
				airplay = AirPlay.searchDialog(this);
				if (airplay != null) {
					sendAirPlay();
				}
			} catch (IOException e) {
				Main.showLogError("Error", "Error starting AirPlay: "+e.getMessage(), e, this);
			}
			
		}
	}
	public void sendAirPlay() {
		if (airplay != null) {
			try {
				BufferedImage image = exportSlideBlocking();
				if (image != null) {
					airplay.photo(image);
				}
			} catch (IOException e) {
				Main.showLogError("Error", "Error showing AirPlay: "+e.getMessage(), e, this);
			}
		}
	}
	public ItemHandler getTextHandler() {
		return texthandler;
	}
	private void setUpHandlers() {
		texthandler = new TextItemHandler(this);
		handlers.add(new DirectoryItemHandler());
		handlers.add(texthandler);
		handlers.add(new PDFItemHandler(this));
		handlers.add(new ImageHandler(this));
		handlers.add(new PPTItemHandler(this));
		//TODO: Get list from jars in plugins directory and add to handlers
		//TODO: Add setting for overriding priority
		for (ItemHandler handler : handlers) {
			if (handler.handlesDirectories()) {
				directoryhandler = handler;
			}
			String[] exts = handler.getExtensions();
			if (exts != null) {
				for (String ext : exts) {
					extmap.put(ext.toLowerCase(), handler);
				}
			}
		}
	}
	public boolean onQuit() {
		savePlaylist();
		remote = false;
		os.killProcess(os.getNativeLibFile(settings.REMOTE_LIB));
		return true;
	}
	private void setUpRemote() {
		remote = true;
		if (os.isOSX()) {
			final String iremoted = os.getNativeLibFile(settings.REMOTE_LIB);
			final LimelightWindow limelight = this;
			os.killProcess(iremoted);
			if (os.writeNativeLib(settings.REMOTE_LIB)) {
				Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						final String MENU = "14";
						final String PLAY_PAUSE = "15";					
						final String FORWARD = "16";
						final String BACK = "17";
						final String VOLUME_UP = "1d";
						final String VOLUME_DOWN = "1e";
						
						String[] args = new String[2];
						args[0] = iremoted;
						args[1] = "-x";
						Process p = Runtime.getRuntime().exec(args);
						InputStream i = p.getInputStream();
						BufferedReader in = new BufferedReader(new InputStreamReader(i));
						String line;
						while (remote && (line = in.readLine()) != null) {
							if (line.indexOf("depressed") < 0) {
								if (line.indexOf(MENU) >= 0) {
									limelight.toggleFullScreen();
								} else if (line.indexOf(PLAY_PAUSE) >= 0) {
									//TODO: Play pause video
								} else if (line.indexOf(FORWARD) >= 0) {
									limelight.nextSlide();
								} else if (line.indexOf(BACK) >= 0) {
									limelight.prevSlide();
								} else if (line.indexOf(VOLUME_UP) >= 0) {
									limelight.nextSlide();
								} else if (line.indexOf(VOLUME_DOWN) >= 0) {
									limelight.prevSlide();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				});
				thread.start();
			}
		}
	}
	
	public WindowSettings getDefaultWindowSettings() {
		return new WindowSettings(640,240,0,0,true,JFrame.NORMAL);
	}
	public void createOtherActions() {
		int shortcutKeyMask = Main.OS().shortCutKey();

		//Create actions that can be used by menus, buttons, toolbars, etc.
		newAction = new newActionClass(this,"New",KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutKeyMask));
		openAction = new openActionClass(this,"Open",KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutKeyMask));
		saveAction = new saveActionClass(this,"Save Playlist",KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutKeyMask));

		undoAction = new undoActionClass("Undo",KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutKeyMask));
		cutAction = new cutActionClass("Cut",KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutKeyMask));
		copyAction = new copyActionClass("Copy",KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutKeyMask));
		pasteAction = new pasteActionClass("Paste",KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcutKeyMask));
		clearAction = new clearActionClass("Clear");
		selectAllAction = new selectAllActionClass("Select All",KeyStroke.getKeyStroke(KeyEvent.VK_A, shortcutKeyMask));

		nextSlideAction = new nextSlideActionClass("Next Slide",KeyStroke.getKeyStroke(KeyEvent.VK_T, shortcutKeyMask));
		prevSlideAction = new prevSlideActionClass("Prev Slide",KeyStroke.getKeyStroke(KeyEvent.VK_E, shortcutKeyMask));
		blankSlideAction = new blankSlideActionClass("Blank Slide",KeyStroke.getKeyStroke(KeyEvent.VK_B, shortcutKeyMask));
		blackSlideAction = new blackSlideActionClass("Black Slide",KeyStroke.getKeyStroke(KeyEvent.VK_K, shortcutKeyMask));
		fullScreenAction = new fullScreenActionClass("Fullscreen",KeyStroke.getKeyStroke(KeyEvent.VK_F, shortcutKeyMask));
		previewAction = new previewActionClass("Preview",KeyStroke.getKeyStroke(KeyEvent.VK_P, shortcutKeyMask));
		slidesAction = new slidesActionClass("Slides",KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcutKeyMask));
		mainWindowAction = new mainWindowActionClass("Main",KeyStroke.getKeyStroke(KeyEvent.VK_M, shortcutKeyMask),this);
		compactWindowAction = new compactWindowActionClass("Compact",KeyStroke.getKeyStroke(KeyEvent.VK_M, shortcutKeyMask),this);
		largeWindowAction = new largeWindowActionClass("Large",KeyStroke.getKeyStroke(KeyEvent.VK_M, shortcutKeyMask),this);
		searchAction = new searchActionClass("Search",KeyStroke.getKeyStroke(KeyEvent.VK_H, shortcutKeyMask),this);
	}
	public void createFileMenu(JMenu fileMenu) {
		fileMenu.add(new JMenuItem(newAction));
		fileMenu.add(new JMenuItem(openAction));
		fileMenu.add(new JMenuItem(saveAction));
	}
	public void createEditMenu(JMenu editMenu) {
		//editMenu.add(new JMenuItem(undoAction));
		//editMenu.addSeparator();
		//editMenu.add(new JMenuItem(cutAction));
		//editMenu.add(new JMenuItem(copyAction));
		//editMenu.add(new JMenuItem(pasteAction));
		editMenu.add(new JMenuItem(clearAction));
		editMenu.add(new JMenuItem(searchAction));
		editMenu.addSeparator();
		editMenu.add(new JMenuItem(selectAllAction));
	}
	public void createViewMenu(JMenu viewMenu) {
		viewMenu.add(new JMenuItem(nextSlideAction));
		viewMenu.add(new JMenuItem(prevSlideAction));
		viewMenu.add(new JMenuItem(blankSlideAction));
		viewMenu.add(new JMenuItem(blackSlideAction));
		viewMenu.addSeparator();
		viewMenu.add(new JMenuItem(fullScreenAction));
		viewMenu.add(new JMenuItem(mainWindowAction));
		viewMenu.add(new JMenuItem(previewAction));
		viewMenu.add(new JMenuItem(slidesAction));
	}
	public void createHelpMenu(JMenu helpMenu) {}
	public void createOtherMenus(JMenuBar mainMenuBar) {
		JMenu windowMenu;		
		windowMenu = new JMenu("Window");
		windowMenu.add(new JMenuItem(compactWindowAction));
		windowMenu.add(new JMenuItem(largeWindowAction));
		mainMenuBar.add(windowMenu);
	}
	public class newActionClass extends AbstractAction {
		private LimelightWindow limelight;
		public newActionClass(LimelightWindow limelight, String text, KeyStroke shortcut) {
			super(text);
			this.limelight = limelight;
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			limelight.clearPlaylist();
		}
	}
	public class openActionClass extends AbstractAction {
		private LimelightWindow limelight;
		public openActionClass(LimelightWindow limelight, String text, KeyStroke shortcut) {
			super(text);
			this.limelight = limelight;
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			Filefilter[] filters = new Filefilter[1];
			List<String> extlist = new ArrayList<String>();
			String[] plsext = Main.Settings().getPropertyList(LimelightSettings.EXT_PLAYLIST);
			for (String ext : plsext) {
				extlist.add(ext);
			}
			for (ItemHandler h : handlers) {
				for(String ext : h.getExtensions()) {
					if (!extlist.contains(ext)) {
						extlist.add(ext);
					}
				}
			}
			String[] extarray = new String[extlist.size()];
			for (int i = 0; i<extlist.size(); i++) {
				extarray[i] = extlist.get(i);
			}
			filters[0] = new Filefilter(extarray,"Limelight Files");
			File file = Main.OS().openFileDialog(limelight, false, null, null, filters);
			if (file != null) {
				String filepath = file.getAbsolutePath();
				String thisext = FileUtils.getExt(filepath);
				boolean handled = false;
				boolean opened = false;
				int i = 0;
				while (i < plsext.length && !handled) {
					if (plsext[i].compareTo(thisext) == 0) {
						handled = true;
						opened = limelight.loadPlaylist(filepath);
					}
					i++;
				}
				
				if (!opened) {
					i = 0;
					handled = false;
					while (i < handlers.size() && !handled) {
						ItemHandler h = handlers.get(i);
						int j = 0;
						String[] exts = h.getExtensions();
						while (j < exts.length && !handled) {
							if (exts[j].compareTo(thisext) == 0) {
								handled = true;
								addItem(filepath);
							}
							j++;
						}
						i++;
					}
				}
	        }
		}
	}
	public class saveActionClass extends AbstractAction {
		private LimelightWindow limelight;
		public saveActionClass(LimelightWindow limelight, String text, KeyStroke shortcut) {
			super(text);
			this.limelight = limelight;
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			Filefilter[] filters = new Filefilter[1];
			filters[0] = new Filefilter(Main.Settings().getPropertyList(LimelightSettings.EXT_PLAYLIST),"Limelight Playlists");
			File file = Main.OS().saveFileDialog(limelight, null, null, filters);
			if (file != null) {
				if (FileUtils.getExt(file.getName()).compareTo("") == 0) {
					file = new File(file.getAbsoluteFile() + ".xml");
				}
	        	boolean cont = true;
	        	if (file.exists()) {
	        		int n = JOptionPane.showConfirmDialog(limelight,
	        				"A file of the same name already exists, are you sure you want to overwrite",
	        				"", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	        		cont = n == 0;
	        	}
	        	if (cont) {
	        		limelight.savePlaylist(file.getAbsolutePath());
	        	}
	        }
		}
	}
	public class undoActionClass extends AbstractAction {
		public undoActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println("Undo...");
		}
	}
	public class cutActionClass extends AbstractAction {
		public cutActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println("Cut...");
		}
	}
	public class copyActionClass extends AbstractAction {
		public copyActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println("Copy...");
		}
	}
	public class pasteActionClass extends AbstractAction {
		public pasteActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println("Paste...");
		}
	}
	public class clearActionClass extends AbstractAction {
		public clearActionClass(String text) {
			super(text);
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println("Clear...");
		}
	}
	public class selectAllActionClass extends AbstractAction {
		public selectAllActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println("Select All...");
		}
	}
	public class nextSlideActionClass extends AbstractAction {
		public nextSlideActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			nextSlide();
		}
	}
	public class prevSlideActionClass extends AbstractAction {
		public prevSlideActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			prevSlide();
		}
	}
	public class blankSlideActionClass extends AbstractAction {
		public blankSlideActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			blankSlide();
		}
	}
	public class blackSlideActionClass extends AbstractAction {
		public blackSlideActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			blackSlide();
		}
	}
	public class fullScreenActionClass extends AbstractAction {
		public fullScreenActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			toggleFullScreen();
		}
	}
	public class previewActionClass extends AbstractAction {
		public previewActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			if (prev.isVisible()) {
				//if (prev == null) {
				//	prev = new Preview(settings, createMenu());
				prev.setVisible(false);
			} else {
				prev.setVisible(true);
			//	prev = null;
			}
		}
	}
	public class slidesActionClass extends AbstractAction {
		public slidesActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			if (slides.isVisible()) {
				slides.setVisible(false);
			} else {
				slides.setVisible(true);
			}
		}
	}
	public class mainWindowActionClass extends AbstractAction {
		LimelightWindow main;
		public mainWindowActionClass(String text, KeyStroke shortcut, LimelightWindow main) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
			this.main = main;
		}
		public void actionPerformed(ActionEvent e) {
			if (main.isVisible()) {
				main.setVisible(false);
			} else {
				main.setVisible(true);
			}
		}
	}
	public class compactWindowActionClass extends AbstractAction {
		LimelightWindow main;
		public compactWindowActionClass(String text, KeyStroke shortcut, LimelightWindow main) {
			super(text);
			this.main = main;
		}
		public void actionPerformed(ActionEvent e) {
			main.prev.setInnerSize(new Dimension(320,240));
			int pwidth = main.prev.getWidth();
			int pheight = main.prev.getHeight();
			int menubar = Main.OS().getMenuBar();
			WindowSettings mains = new WindowSettings(pwidth*2,240,0,menubar,true,JFrame.NORMAL);
			WindowSettings prevs = new WindowSettings(pwidth,pheight,0,240+menubar+SPACE,true,JFrame.NORMAL);
			WindowSettings slides = new WindowSettings(pwidth,pheight,pwidth,240+menubar+SPACE,true,JFrame.NORMAL);
			main.arrangeWindows(mains,slides,prevs);
			//main.arrangeWindows(main.getDefaultWindowSettings(), main.slides.getDefaultWindowSettings(), main.prev.getDefaultWindowSettings());
		}
	}
	public class largeWindowActionClass extends AbstractAction {
		LimelightWindow main;
		public largeWindowActionClass(String text, KeyStroke shortcut, LimelightWindow main) {
			super(text);
			this.main = main;
		}
		public void actionPerformed(ActionEvent e) {
			main.prev.setInnerSize(new Dimension(640,480));
			int pwidth = main.prev.getWidth();
			int pheight = main.prev.getHeight();
			int menubar = Main.OS().getMenuBar();
			WindowSettings mains = new WindowSettings(pwidth,240,0,menubar,true,JFrame.NORMAL);
			WindowSettings prevs = new WindowSettings(pwidth,pheight,0,240+menubar+SPACE,true,JFrame.NORMAL);
			WindowSettings slides = new WindowSettings(320,pheight+240+SPACE,pwidth+SPACE,menubar,true,JFrame.NORMAL);
			main.arrangeWindows(mains,slides,prevs);
			//main.arrangeWindows(main.getDefaultWindowSettings(),  new WindowSettings(320,745,643,0,true,JFrame.NORMAL), new WindowSettings(640,502,0,265,true,JFrame.NORMAL));
		}
	}
	public class searchActionClass extends AbstractAction {
		LimelightWindow main;
		public searchActionClass(String text, KeyStroke shortcut, LimelightWindow main) {
			super(text);
			this.main = main;
		}
		public void actionPerformed(ActionEvent e) {
			if (search == null) {
				search = new Search(main,createMenu());
			}
			if (!search.isVisible()) {
				search.setLocation(main.getX() + (main.getWidth() - search.getWidth())/2, main.getY()+200);
				search.setVisible(true);
			}
		}
	}
	public Controls getControls() {
		return controls;
	}
	public LimelightSettings getSettings() {
		return settings;
	}
	public void toggleFullScreen() {
		fs.toggleFullScreen();
	}
	public void setFullScreen(boolean value) {
		fs.setFullScreen(value);
	}
	public void addItem(String filepath) {
		mainpanel.addItem(filepath);
	}
	public void arrangeWindows(WindowSettings mainsettings, WindowSettings slidesettings, WindowSettings previewsettings) {
		setBounds(mainsettings);
		slides.setBounds(slidesettings);
		prev.setBounds(previewsettings);
	}
	
	public void jumpItem(int index, boolean andJump) {
		Navigator nav = mainpanel.getNavigator();
		if (index < nav.getItemCount()) {
			nav.setCurrentItem(index);
			loadFile(nav.getCurrentItem());
			if (andJump) {
				jumpSlide(0);
			}
		}
	}
	public boolean nextItem(boolean andJump) {
		Navigator nav = mainpanel.getNavigator();
		boolean start = nav.getNotOnItem() && nav.atBeginning() && (nav.getItemCount() > 0);
		if (nav.nextItem() || start) {
			if (start) {
				nav.setCurrentItem(0);
			}
			loadFile(nav.getCurrentItem());
			if (andJump) {
				jumpSlide(0);
			}
			return true;
		} else {
			return false;
		}
	}
	public boolean prevItem(boolean andJump) {
		Navigator nav = mainpanel.getNavigator();
		if (nav.prevItem()) {
			loadFile(nav.getCurrentItem());
			if (andJump) {
				jumpSlide(0);
			}
			return true;
		} else {
			return false;
		}	
	}
	public boolean nextSlide() {
		Navigator nav = slides.getNavigator();
		if (nav.nextItem()) {
			jumpSlide(nav.getCurrentItem());
			return true;
		} else {
			if (nextItem(false)) {
				jumpSlide(0);
				return true;
			} else {
				return false;
			}
		}
	}
	public boolean prevSlide() {
		Navigator nav = slides.getNavigator();
		if (nav.prevItem()) {
			jumpSlide(nav.getCurrentItem());
			return true;
		} else {
			if (prevItem(false)) {
				jumpSlide(nav.getItemCount()-1);
				return false;
			} else {
				return false;
			}
		}
	}
	public void blankSlide() {
		//Change this to blank the current fullscreen/preview viewers
		Navigator nav = slides.getNavigator();
		nav.setNotOnItem(true);
		fs.blankSlide();
		prev.blankSlide();
	}
	public void blackSlide() {
		Navigator nav = slides.getNavigator();
		nav.setNotOnItem(true);
		fs.blackSlide();
		prev.blackSlide();
	}
	public void jumpSlide(int index) {
		//This is current done inside slides.getSlide()
		//Navigator nav = slides.getNavigator();
		//nav.setCurrentItem(index);
		//nav.setNotOnItem(false);
		ItemSlide slide = slides.getSlide(index);
		fs.setSlide(slide, index);
		prev.setSlide(slide, index);
	}
	public void loadFile(int index) {
		//This is current done inside main.getFilepath()
		//Navigator nav = main.getNavigator();
		//nav.setCurrentItem(index);
		//nav.setNotOnItem(false);
		slides.loadFile(mainpanel.getFilepath(index));
	}
	public void loadFile(String filename) {
		Navigator nav = mainpanel.getNavigator();
		nav.setNotOnItem(true);
		slides.loadFile(filename);
	}
	public void savePlaylist() {
		//TODO: Figure out what's going on here
		Playlist playlist = new Playlist(Main.Settings().getXMLHelper());
		playlist.replaceItems(mainpanel.getItems());
		Main.Settings().saveSettings();
	}
	public void savePlaylist(String filename) {
		Playlist playlist = new Playlist();
		playlist.replaceItems(mainpanel.getItems());
		playlist.save(filename);
	}
	public void loadPlaylist() {
		Playlist playlist = new Playlist(Main.Settings().getXMLHelper());
		mainpanel.addItems(playlist.getItems());
	}
	public boolean loadPlaylist(String filename) {
		try {
			Playlist playlist = new Playlist(filename);
			if (playlist.isPlaylist()) {
				mainpanel.addItems(playlist.getItems());
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Main.Logger().warning("Failed to open playlist: "+e.getMessage());
			return false;
		}
	}
	public void clearPlaylist() {
		mainpanel.removeItems();
	}
	public void refreshFolders() {
		mainpanel.refreshFolders();
	}
	//Exporting functions
	public BufferedImage exportSlideBlocking() {
		ItemViewer viewer = prev.getItemViewer();
		if (viewer != null) {
			final JComponent comp = viewer.getItemPanel();
			try {
				return GUIUtils.componentToImage(comp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	private void exportSlide(final String dir, final String prefix) {
		try {
			ItemViewer viewer = prev.getItemViewer();
			if (viewer != null) {
				final JComponent comp = viewer.getItemPanel();
				//GUIUtils.componentToImage(comp,dir+Main.OS().fileSeparator()+prefix+slides.getItemTitle()+(slides.getNavigator().getCurrentItem()+1)+".jpg",640,480);
				Runnable run = new Runnable() {
					public void run() {
						try {
							GUIUtils.componentToImage(comp, dir+Main.OS().fileSeparator()+prefix+slides.getItemTitle()+"-"+(slides.getNavigator().getCurrentItem()+1)+".jpg");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				SwingUtilities.invokeAndWait(run);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void exportSlide(String dir, int index) {
		exportSlide(dir,createLimelightPrefix()+"-"+(""+(10000+index)).substring(1)+"-");		
	}
	public void exportSlides(String filepath) {
		stopAirPlay();
		loadFile(filepath);
		jumpSlide(0);
		final File file = Main.OS().openFileDialog(this, true);
		Thread exportThread = new Thread() {
			public void run() {
				if (file != null) {
					String dir = createLimelightExportDir(file).getAbsolutePath();
					Navigator nav = slides.getNavigator();
					if (nav.getItemCount() > 0) {
						int i = 1;
						do {
							jumpSlide(nav.getCurrentItem());
							exportSlide(dir,i);
							i++;
						} while (nav.nextItem());
					}
				}
			}
		};
		exportThread.start();
	}
	public void exportSlides(final String[] filepaths) {
		stopAirPlay();
		jumpItem(0,true);
		final File file = Main.OS().openFileDialog(this, true);
		Thread exportThread = new Thread() {
			public void run() {
				if (file != null) {
					String dir = createLimelightExportDir(file).getAbsolutePath();
					int i = 1;
					for (String filepath : filepaths) {
						loadFile(filepath);
						jumpSlide(0);
						Navigator nav = slides.getNavigator();
						if (nav.getItemCount() > 0) {
							do {
								jumpSlide(nav.getCurrentItem());
								exportSlide(dir,i);
								i++;
							} while (nav.nextItem());
						}
					}
				}
			}
		};
		exportThread.start();
	}

	public void exportPowerPoint() {
		stopAirPlay();
		jumpItem(0,true);
		final File file = Main.OS().openFileDialog(this, true);
		Thread exportThread = new Thread() {
			public void run() {
				if (file != null) {
					final String dir = file.getAbsolutePath();
					final SlideShow ppt = new SlideShow();
					int i = 1;
					do {
						try {
							ItemViewer viewer = prev.getItemViewer();
							if (viewer != null) {
								final JComponent comp = viewer.getItemPanel();
								final Runnable run = new Runnable() {
									public void run() {
										try {
											BufferedImage image = GUIUtils.componentToImage(comp);
											ByteArrayOutputStream baos = new ByteArrayOutputStream();
									        try {
									            ImageIO.write(image, "JPEG", baos);
									        } catch (IOException ex) {
									        	Main.Logger().warning("Could not create image: "+ex.getMessage());
									        }

											Slide slide = ppt.createSlide();
											int idx = ppt.addPicture(baos.toByteArray(), Picture.JPEG);
											Picture pict = new Picture(idx);
											pict.setAnchor(new java.awt.Rectangle(0, 0, ppt.getPageSize().width, ppt.getPageSize().height));
											slide.addShape(pict);
										} catch (Exception e) {
											Main.Logger().warning("Could not create slide: "+e.getMessage());
										}
									}
								};
								SwingUtilities.invokeAndWait(run);
							}
						} catch (Exception e) {
							Main.Logger().warning("Could not process viewer: "+e.getMessage());
						}
					} while (nextSlide());
					FileOutputStream out;
					try {
						out = new FileOutputStream(dir+Main.OS().fileSeparator()+createLimelightPrefix()+".ppt");
						ppt.write(out);
					    out.close();
					} catch (Exception e) {
						Main.Logger().severe("Could not write powerpoint: "+e.getMessage());
					}
				}
			}
		};
		exportThread.start();
	}
	public void exportAllSlides() {
		stopAirPlay();
		jumpItem(0,true);
		final File file = Main.OS().openFileDialog(this, true);
		Thread exportThread = new Thread() {
			public void run() {
				if (file != null) {
					String dir = createLimelightExportDir(file).getAbsolutePath();
					int i = 1;
					do {
						exportSlide(dir,i);
						i++;
					} while (nextSlide());
				}
			}
		};
		exportThread.start();
	}
	public void exportiPhoto() {
		stopAirPlay();
		jumpItem(0,true);
		final File file = Main.OS().openFileDialog(this, true);
		Thread exportThread = new Thread() {
			public void run() {
				if (file != null) {
					String dir = createLimelightExportDir(file).getAbsolutePath();
					int i = 1;
					do {
						exportSlide(dir,i);
						i++;
					} while (nextSlide());
					String[] args = new String[4];
					args[0] = "open";
					args[1] = "-a";
					args[2] = "iPhoto";
					args[3] = dir;
					Main.OS().executeProcess(args);
				}
			}
		};
		exportThread.start();
	}
	private String createLimelightPrefix() {
		return "Limelight-"+(new SimpleDateFormat("yyyyMMdd")).format(new Date());
	}
	private File createLimelightExportDir(File initial) {
		File dir = new File(initial.getAbsolutePath() + Main.OS().fileSeparator() + createLimelightPrefix());
		dir.mkdirs();
		return dir;
		/*
		TODO: Could add some error handling
		if (dir.mkdirs()) {
			return dir;
		}
		throw new IOException("Could not make directory " + dir.getAbsolutePath());
		*/
	}
	public int getSlideViewportWidth() {
		return slides.getViewportWidth();
	}
	public void onExitFullScreen() {
		if (hidwindows) {
			hidwindows = false;
			setVisible(mlast);
			slides.setVisible(slast);
			prev.setVisible(plast);
		}
	}
	public void onEnterFullScreen() {
		if (fs.overOtherWindows()) {
			hidwindows = true;
			mlast = isVisible();
			slast = slides.isVisible();
			plast = prev.isVisible();
			setVisible(false);
			slides.setVisible(false);
			prev.setVisible(false);
		}
	}
}