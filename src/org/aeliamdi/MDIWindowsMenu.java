package org.aeliamdi;

import ca.guydavis.swing.desktop.WindowPositioner;
import org.aeliamdi.util.SmallNumberIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.*;
import java.util.List;

@SuppressWarnings( "unchecked" )
public class MDIWindowsMenu extends JMenu implements ContainerListener {

    /**
     * The possible static menu items that can be added to the Windows menu
     * above the dynamic listing of open windows.
     */
    public enum MenuItem {
        /** Cascade windows from top left down */
        CASCADE,
        /** Checkerboard style tile of windows */
        TILE,
        /** Tile from top down */
        TILE_HORIZ,
        /** Tile from left to right */
        TILE_VERT,
        /** Restore the currently selected window to original size */
        RESTORE,
        /** Restore all windows to their original size */
        RESTORE_ALL,
        /** Minimize the currently selected window to original size */
        MINIMIZE,
        /** Minimize all windows to their original size */
        MINIMIZE_ALL,
        /** Maximize the currently selected window to original size */
        MAXIMIZE,
        /** Maximize all windows to their original size */
        MAXIMIZE_ALL,
        /** Indicates a menu separator should be placed in the menu */
        SEPARATOR,
	    /** Close current window */
	    CLOSE,
	    /** Close all windows */
	    CLOSE_ALL
    };

    /** The desktop whose windows are being monitored */
    private JDesktopPane desktop;

	private MDIFrame mdiFrame;

    /** Used to retrieve the menu item corresponding to a given frame */
    private Map<MDIView, JCheckBoxMenuItem> menusForViews;

    /** Used for sorting the frames in alphabetical order by title */
    private Comparator<JInternalFrame> frameComparator;

	private Comparator<MDIView> viewComparator;

    /** The static menus for each chosen MenuItem type */
    private Map<MenuItem, JMenuItem> staticMenus;

    /** An optional helper class which governs the position of new windows */
    private WindowPositioner windowPositioner;

    /**
     * Create the "Windows" menu for a MDI view using default title and menu
     * choices.
     *
     * @param mdiFrame
     *            The desktop to monitor.
     */
    public MDIWindowsMenu(MDIFrame mdiFrame) {
        this("Windows", mdiFrame);
    }

    /**
     * Create the "Windows" menu for a MDI view using the given title and
     * default menu choices.
     *
     * @param windowTitle
     *            The title of the window to display.
     * @param mdiFrame
     *            The desktop to monitor.
     */
    public MDIWindowsMenu(String windowTitle, MDIFrame mdiFrame) {
        this(windowTitle, mdiFrame, MenuItem.CLOSE, MenuItem.CLOSE_ALL, 
		        MenuItem.SEPARATOR, MenuItem.CASCADE, MenuItem.TILE,
                MenuItem.TILE_HORIZ, MenuItem.TILE_VERT, MenuItem.SEPARATOR,
                MenuItem.RESTORE, MenuItem.MINIMIZE, MenuItem.MAXIMIZE,
                MenuItem.SEPARATOR, MenuItem.RESTORE_ALL,
                MenuItem.MINIMIZE_ALL, MenuItem.MAXIMIZE_ALL);
    }

    /**
     * Create the "Windows" menu for a MDI view using the given title and menu
     * items.
     *
     * @param windowTitle
     *            The title of the window to display.
     * @param mdiFrame
     *            The desktop to monitor.
     * @param items
     *            A variable length argument indicating which menu items to
     *            display in the menu.
     */
    public MDIWindowsMenu(String windowTitle, MDIFrame mdiFrame,
            MenuItem... items) {

	    this.mdiFrame = mdiFrame;
	    this.mdiFrame.addMDIFrameListener( new CustomMDIFrameListener() );
        this.mdiFrame.getTabbedPane().addContainerListener(this);

        this.desktop = mdiFrame.getDektopPane();
        this.staticMenus = new HashMap<MenuItem, JMenuItem>();
        setText(windowTitle);

        for (MenuItem item : items) {
            addMenuItem(item);
        }

        // Add a final separator if the user forgot to include it
        if (items[items.length - 1] != MenuItem.SEPARATOR) {
            addMenuItem(MenuItem.SEPARATOR);
        }

        // Sort frames by title alphabetically
        this.frameComparator = new Comparator<JInternalFrame>() {
            public int compare(JInternalFrame o1, JInternalFrame o2) {
                int ret = 0;
                if (o1 != null && o2 != null) {
                    String t1 = o1.getTitle();
                    String t2 = o2.getTitle();

                    if (t1 != null && t2 != null) {
                        ret = t1.compareTo(t2);
                    } else if (t1 == null && t2 != null) {
                        ret = -1;
                    } else if (t1 != null && t2 == null) {
                        ret = 1;
                    } else {
                        ret = 0;
                    }
                }
                return (ret);
            }
        };
        this.viewComparator = new Comparator<MDIView>() {
            public int compare(MDIView o1, MDIView o2) {
                int ret = 0;
                if (o1 != null && o2 != null) {
                    String t1 = o1.getTitle();
                    String t2 = o2.getTitle();

                    if (t1 != null && t2 != null) {
                        ret = t1.compareTo(t2);
                    } else if (t1 == null && t2 != null) {
                        ret = -1;
                    } else if (t1 != null && t2 == null) {
                        ret = 1;
                    } else {
                        ret = 0;
                    }
                }
                return (ret);
            }
        };

        this.menusForViews = new HashMap<MDIView, JCheckBoxMenuItem>();
        //this.desktop.addContainerListener(this);
        //this.desktop.setDesktopManager(new CustomDesktopManager());
        updateWindowsList(); // Setup list for any existing windows
	    updateStaticMenuItems();
    }

    /**
     * Creates a static menu item with mnemonic and action listener.
     *
     * @param item
     *            The type of menu item to add.
     */
    private void addMenuItem(MenuItem item) {
        String name = null;
        Integer mnemonic = null;
        ActionListener listener = null;

        switch (item) {
        case CASCADE:
            name = "Cascade";
            mnemonic = KeyEvent.VK_C;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
	                setTabbed( false );
                    cascade();
                }
            };
            break;
        case TILE:
            name = "Tile";
            mnemonic = KeyEvent.VK_T;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
	                setTabbed( false );
                    tile();
                }
            };
            break;
        case TILE_HORIZ:
            name = "Tile Horizontally";
            mnemonic = KeyEvent.VK_H;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
	                setTabbed( false );
                    tileHorizontally();
                }
            };
            break;
        case TILE_VERT:
            name = "Tile Vertically";
            mnemonic = KeyEvent.VK_V;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
	                setTabbed( false );
                    tileVertically();
                }
            };
            break;
        case RESTORE:
            name = "Restore";
            mnemonic = KeyEvent.VK_R;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
	                if( !isTabbed() )
	                {
						try {
							if (desktop.getSelectedFrame().isIcon()) {
								desktop.getSelectedFrame().setIcon(false);
							} else if (desktop.getSelectedFrame().isMaximum()) {
								desktop.getSelectedFrame().setMaximum(false);
							}
						} catch (PropertyVetoException ex) {
							throw new RuntimeException(ex);
						}
	                }
	                else
	                {
						mdiFrame.getActiveView().setRestored();
	                }
                }
            };
            break;
        case RESTORE_ALL:
            name = "Restore All";
            mnemonic = KeyEvent.VK_E;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
	                if( !isTabbed() )
	                {
						for (JInternalFrame frame : desktop.getAllFrames()) {
							try {
								if (frame.isIcon()) {
									frame.setIcon(false);
								} else if (frame.isMaximum()) {
									frame.setMaximum(false);
								}
							} catch (PropertyVetoException ex) {
								throw new RuntimeException(ex);
							}
						}
	                }
	                else
	                {
						for ( MDIView view : (List<MDIView>)mdiFrame.getViews() ) {
							if( view.isIconified() )
								view.setRestored();
							else if( view.isMaximized() )
								view.setRestored();
						}
	                }
                }
            };
            break;
        case MINIMIZE:
            name = "Minimize";
            mnemonic = KeyEvent.VK_M;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
	                mdiFrame.getActiveView().setIconified();
                }
            };
            break;
        case MINIMIZE_ALL:
            name = "Minimize All";
            mnemonic = KeyEvent.VK_I;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
                    for ( MDIView view : (List<MDIView>)mdiFrame.getViews() ) {
						view.setIconified();
                    }
                }
            };
            break;
        case MAXIMIZE:
            name = "Maximize";
            mnemonic = KeyEvent.VK_A;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
	                mdiFrame.getActiveView().setMaximized();
                }
            };
            break;
        case MAXIMIZE_ALL:
            name = "Maximize All";
            mnemonic = KeyEvent.VK_X;
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
                    for ( MDIView view : (List<MDIView>)mdiFrame.getViews() ) {
						view.setMaximized();
                    }
                }
            };
            break;
        case CLOSE:
            name = "Close";
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
                     mdiFrame.getActiveView().closeView();
                }
            };
            break;
        case CLOSE_ALL:
            name = "Close All";
            listener = new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused")
                ActionEvent e) {
                    for ( MDIView view : (List<MDIView>)mdiFrame.getViews() ) {
						view.closeView();
                    }
                }
            };
            break;
        case SEPARATOR:
            addSeparator(); // Create a menu separator
        }

        // Now create a menu item with the given name, mnemonic and listener
        if (name != null) {
            JMenuItem menuItem = new JMenuItem(name);
	        if( mnemonic != null )
				menuItem.setMnemonic(mnemonic);
            menuItem.addActionListener(listener);
            add(menuItem); // Add to the main menu
            staticMenus.put(item, menuItem);
        }
    }

	private void setTabbed( boolean b )
	{
		if( !b && ( mdiFrame.getCurrentViewPane().equals( MDIFrame.TABS ) ) )
			mdiFrame.changeView();
		else if( b && ( mdiFrame.getCurrentViewPane().equals( MDIFrame.DESKTOP ) ) )
			mdiFrame.changeView();
	}

	private boolean isTabbed()
	{
		return ( mdiFrame.getCurrentViewPane().equals( MDIFrame.TABS ) );
	}

	/**
     * @return A list of frames on the desktop which are not iconified and are
     *         visible.
     */
    private List<JInternalFrame> getAllVisibleFrames() {
        List<JInternalFrame> frames = new ArrayList<JInternalFrame>();
        for (JInternalFrame frame : this.desktop.getAllFrames()) {
            if (frame.isVisible() && !frame.isClosed() && !frame.isIcon()) {
                frames.add(frame);
            }
        }
        Collections.sort(frames, this.frameComparator);
        return frames;
    }

    /**
     * Change the bounds of visible windows to tile them vertically on the
     * desktop.
     */
    protected void tileVertically() {
        List<JInternalFrame> frames = getAllVisibleFrames();
	    if( frames.size() == 0 )
	        return;
        int newWidth = this.desktop.getWidth() / frames.size();
        int newHeight = this.desktop.getHeight();

        int x = 0;
        for (JInternalFrame frame : frames) {
            if (frame.isMaximum()) {
                try {
                    frame.setMaximum(false); // Restore if maximized first
                } catch (PropertyVetoException ex) {
                    throw new RuntimeException(ex);
                }
            }
            frame.reshape(x, 0, newWidth, newHeight);
            x += newWidth;
        }
    }

    /**
     * Change the bounds of visible windows to tile them horizontally on the
     * desktop.
     */
    protected void tileHorizontally() {
        List<JInternalFrame> frames = getAllVisibleFrames();
	    if( frames.size() == 0 )
	        return;
        int newWidth = this.desktop.getWidth();
        int newHeight = this.desktop.getHeight() / frames.size();

        int y = 0;
        for (JInternalFrame frame : frames) {
            if (frame.isMaximum()) {
                try {
                    frame.setMaximum(false); // Restore if maximized first
                } catch (PropertyVetoException ex) {
                    throw new RuntimeException(ex);
                }
            }
            frame.reshape(0, y, newWidth, newHeight);
            y += newHeight;
        }
    }

    /**
     * Change the bounds of visible windows to tile them checkerboard-style on
     * the desktop.
     */
    protected void tile() {
        List<JInternalFrame> frames = getAllVisibleFrames();
        if (frames.size() == 0) {
            return;
        }

        double sqrt = Math.sqrt(frames.size());
        int numCols = (int) Math.floor(sqrt);
        int numRows = numCols;
        if ((numCols * numRows) < frames.size()) {
            numCols++;
            if ((numCols * numRows) < frames.size()) {
                numRows++;
            }
        }

        int newWidth = this.desktop.getWidth() / numCols;
        int newHeight = this.desktop.getHeight() / numRows;

        int y = 0;
        int x = 0;
        int frameIdx = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (frameIdx < frames.size()) {
                    JInternalFrame frame = frames.get(frameIdx++);
                    if (frame.isMaximum()) {
                        try {
                            frame.setMaximum(false);
                        } catch (PropertyVetoException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    frame.reshape(x, y, newWidth, newHeight);
                    x += newWidth;
                }
            }
            x = 0;
            y += newHeight;
        }
    }

    /**
     * Change the bounds of visible windows to cascade them down from the top
     * left of the desktop.
     */
    protected void cascade() {
        List<JInternalFrame> frames = getAllVisibleFrames();
        if (frames.size() == 0) {
            return;
        }

        int newWidth = (int) (this.desktop.getWidth() * 0.6);
        int newHeight = (int) (this.desktop.getHeight() * 0.6);
        int x = 0;
        int y = 0;
        for (JInternalFrame frame : frames) {
            if (frame.isMaximum()) {
                try {
                    frame.setMaximum(false);
                } catch (PropertyVetoException ex) {
                    throw new RuntimeException(ex);
                }
            }
            frame.reshape(x, y, newWidth, newHeight);
            x += 25;
            y += 25;

            if ((x + newWidth) > this.desktop.getWidth()) {
                x = 0;
            }

            if ((y + newHeight) > this.desktop.getHeight()) {
                y = 0;
            }
        }
    }

    /**
     * Records the addition of a window to the desktop.
     *
     * @see java.awt.event.ContainerListener#componentAdded(java.awt.event.ContainerEvent)
     */
    public void componentAdded(ContainerEvent e) {
        if ( (this.windowPositioner != null) && (e.getChild() instanceof JInternalFrame) )
        {
            JInternalFrame frame = (JInternalFrame) e.getChild();
            Point position = this.windowPositioner.getPosition( frame, getAllVisibleFrames() );
            frame.setLocation(position);
        }

        updateWindowsList();
	    updateStaticMenuItems();
    }

    /**
     * Records the removal of a window from the desktop.
     *
     * @see java.awt.event.ContainerListener#componentRemoved(java.awt.event.ContainerEvent)
     */
    public void componentRemoved( @SuppressWarnings("unused") ContainerEvent e ) {
        updateWindowsList();
	    updateStaticMenuItems();
    }

    /**
     * Invoked to regenerate the dynamic window listing menu items at the bottom
     * of the menu.
     */
    private void updateWindowsList() {

        List<MDIView> views = new ArrayList<MDIView>();
        for( MDIView view : (List<MDIView>)mdiFrame.getViews() )
        {
            views.add(view);
        }
        Collections.sort(views, this.viewComparator);

        for (Component menu : this.getMenuComponents()) {
            if (menu instanceof JCheckBoxMenuItem) {
                this.remove(menu);
            }
        }

        this.menusForViews.clear();

		int i = 1;
		ButtonGroup group = new ButtonGroup();
		for (final MDIView view : views) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem( view.getTitle(), new SmallNumberIcon( i, UIManager.getFont("Label.font"), UIManager.getColor("Label.foreground") ) );

			if (view.isIconified()) {
				item.setSelected(false);
			}

			if (view.isSelected()) {
				item.setState(true);
			}
			group.add(item);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(@SuppressWarnings("unused")
				ActionEvent e) {
					if( isTabbed() )
					{
						view.setMaximized();
					}
					else
					{
						if (view.isIconified()) {
							view.setRestored();
						}

						if (!view.isSelected()) {
							view.setSelected(true);
							view.grabFocus();
						}
					}
				}
			});
			this.menusForViews.put(view, item);
			add(item);
			i++;
		}
		if( i == 1 )
		{
			JCheckBoxMenuItem item = new JCheckBoxMenuItem( "No Windows" );
			item.setEnabled( false );
			add(item);
		}
    }

    /**
     * Toggle the enabled state of the static menu items depending on the
     * selected frame.
     */
    private void updateStaticMenuItems() {
        //JInternalFrame selectedView = this.desktop.getSelectedFrame();
	    MDIView selectedView = this.mdiFrame.getActiveView();
	    JMenuItem closeItem = this.staticMenus.get( MenuItem.CLOSE );
	    JMenuItem closeAllItem = this.staticMenus.get( MenuItem.CLOSE_ALL );
        JMenuItem minimizeItem = this.staticMenus.get(MenuItem.MINIMIZE);
	    JMenuItem minimizeAllItem = this.staticMenus.get( MenuItem.MINIMIZE_ALL );
        JMenuItem maximizeItem = this.staticMenus.get(MenuItem.MAXIMIZE);
	    JMenuItem maximizeAllItem = this.staticMenus.get( MenuItem.MAXIMIZE_ALL );
        JMenuItem restoreItem = this.staticMenus.get(MenuItem.RESTORE);
	    JMenuItem restoreAllItem = this.staticMenus.get( MenuItem.RESTORE_ALL );
	    JMenuItem cascadeItem = this.staticMenus.get( MenuItem.CASCADE );
	    JMenuItem tileItem = this.staticMenus.get( MenuItem.TILE );
	    JMenuItem tileVertItem = this.staticMenus.get( MenuItem.TILE_HORIZ );
	    JMenuItem tileHorizItem = this.staticMenus.get( MenuItem.TILE_VERT );

        for (JCheckBoxMenuItem item : menusForViews.values()) {
            item.setSelected(false);
        }

	    boolean hasViews = this.mdiFrame.getViews().size() > 0;
	    if( closeAllItem != null )
	        closeAllItem.setEnabled( hasViews );
	    if( minimizeAllItem != null )
	        minimizeAllItem.setEnabled( hasViews );
	    if( maximizeAllItem != null )
	        maximizeAllItem.setEnabled( hasViews );
	    if( restoreAllItem != null )
	        restoreAllItem.setEnabled( hasViews );
	    if( cascadeItem != null )
	        cascadeItem.setEnabled( hasViews );
	    if( tileItem != null )
	        tileItem.setEnabled( hasViews );
	    if( tileVertItem != null )
	        tileVertItem.setEnabled( hasViews );
	    if( tileHorizItem != null )
	        tileHorizItem.setEnabled( hasViews );

        if ( selectedView == null) {
	        if( closeItem != null )
	            closeItem.setEnabled( false );
            restoreItem.setEnabled(false);
            maximizeItem.setEnabled(false);
            minimizeItem.setEnabled(false);
        } else if ( selectedView.isIconified()) {
            restoreItem.setEnabled(true);
            maximizeItem.setEnabled(true);
            //maximizeItem.setEnabled(selectedView.isMaximizable());
            minimizeItem.setEnabled(false);
            menusForViews.get( selectedView ).setSelected(true);
        } else if ( selectedView.isMaximized()) {
            restoreItem.setEnabled(true);
            maximizeItem.setEnabled(false);
            minimizeItem.setEnabled(true);
            //minimizeItem.setEnabled(selectedView.isIconifiable());
            menusForViews.get( selectedView ).setSelected(true);
        } else { // Window in regular position
            restoreItem.setEnabled(false);
            maximizeItem.setEnabled(true);
            minimizeItem.setEnabled(true);
            //maximizeItem.setEnabled(selectedView.isMaximizable());
            //minimizeItem.setEnabled(selectedView.isIconifiable());
            menusForViews.get( selectedView ).setSelected(true);
        }
    }

	private class CustomMDIFrameListener implements MDIFrameListener
	{
		@Override
		public void viewPaneChanged( MDIFrameEvent e )
		{
			updateWindowsList();
			updateStaticMenuItems();
		}
	}

    /**
     * Use this window positioner to position (<code>setLocation()</code>)
     * of new windows added to the desktop.
     *
     * @param windowPositioner
     */
    public void setWindowPositioner(WindowPositioner windowPositioner) {
        this.windowPositioner = windowPositioner;
    }
}