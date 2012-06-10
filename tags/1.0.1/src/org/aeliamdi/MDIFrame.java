/*
AceMDI - Easy, yet powerful MDI at your fingertips.
Copyright (C) 2004 Pritam G. Barhate.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information, contact pritam_g_barhate@rediffmail.com
*/
 
/*
 * Created on Sep 15, 2004
 *
 * Pritam G. Barhate 
 */
package org.aeliamdi;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.Vector;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * This class extends a <code>import javax.swing.JFrame</code> to
 * provide a Frame that is "MDI aware". <code>MDIView</code>s can be added by using 
 * <code>mdiFrameObject.addIntenInternalView(MDIView aView)</code>
 * <p>
 * <code>MDIFrame</code>uses <code>MDITabbedPane</code> (which extends 
 * <code>javax.swing.JTabbedPane</code>) and <code>javax.swing.JDesktopPane</code> 
 * to manage the views internally. When one of the views is in maximized
 * state all the viwes are represented as "Tabs" in a tabbed pane, otherwise 
 * the viwes are managed by internal frames.  
 * 
 * @author Pritam G. Barhate
 * 
 */
public class MDIFrame extends JFrame {
	/** 
	 * The <code>MDITabbedPane</code> that displays the views as tabs 
	 * when one of the view is in maximized state.
	 */
	protected MDITabbedPane tabbedPane;
	/**
	 * The <code>JDesktopPane</code> that displays the views as JInternalFrames
	 * when one of the view is minimized or restored state.
	 */
	protected JDesktopPane desktopPane;
	
	/**
	 * The custom desktop manager taht is used to manage the internal frames.
	 */
	protected MDIDesktopManager desktopManager;
	/** 
	 * This Vector holds all the views those will be added
	 * to the MDIFrame.
	 */ 
	protected Vector views = new Vector();
	
	/** This panel will contain the JTabbedPane and JDEsktopPane */
	protected JPanel viewContainer;
	
	/** Desktop pane view */
	public static final String DESKTOP = "desktop";
	/** Tabbed pane view */
	public static final String TABS = "tabs";
	/** 
	 * This variable keeps the track of the current view of the <code>MDIFrame</code>.
	 * It will be always one of <code>MDIFrame.TABS</code> or <code>MDIFrame.DESKTOP</code>. 
	 */
	private String currentViewPane;
	
	/**
	 * This variable is used for internal purpose only. It is used to
	 * fire MDIView activiated and deactiviated events.
	 */
	private MDIView lastSelectedView = null;
	
	/**
	 * This variable is used for internal purpose only. It is used to
	 * fire MDIView activiated and deactiviated events correctly when changing 
	 * from desktop pane to tabbed pane.
	 */
	private boolean paneChangedInternally = false;
	
	/** 
	 * This panel contains minimize, maximize, and close buttons.
	 * This panel is added to menubar when the <code>currentView</code>
	 * is MDIFrame.TABS. 
	 */
	protected JPanel windowButtons = new JPanel();
	
	/** The iconify button */
	protected MDIFrameButton iconifyButton;
	/** The restore button */
	protected MDIFrameButton restoreButton;
	/** The close button */
	protected MDIFrameButton closeButton;
	
	/** 
	 * This variable is used for internal purpose only. It is used to
     * allow disallow the JPanel windowButtons and could be used only
     * with constructor. Provided a getter being isButtonsEnabled().
     * Default value is true using standard MDIFrame constructor.
     */
    protected boolean buttonsEnabled = true;
    
	/** 
	 * This counter is used to facilitate the naming of new views
	 * such as Untitled1, Untitled2,...
	 * @see <code>MDIFrame.addNewInternalView()</code>
	 */ 
	private int windowCounter = 0;
	
	/**
	 * x-coordinate of last internal frame created.
	 * @see <code>MDIFrame.newWindowLocation()</code>
	 */
	private int windowX = 0;
	/**
	 * y-coordinate of last internal frame created.
	 * @see <code>MDIFrame.newWindowLocation()</code>
	 */
	private int windowY = 0; 
	
	/**
	 * A vector holding <code>MDIFrameListener</code>s that are 
	 * added to the <code>MDIFrame</code> object.
	 */
	private Vector mdiFrameListeners = new Vector();
	
	/** 
	 * This variable helps to fire MDIView activiated and deactiviated events
	 * correctly
	 * @see MDIView#closeView()
	 */
	private boolean suppressActiviationEvents;
	
	/** 
	 * This variable helps to fire MDIView restored
	 *events correctly without fireing intermidiate iconified
	 *events
	 * @see MDIFrameButton
	 */
	private boolean suppressIconifiedEvent;
	
	/**
	 * The Button Order will determine in which order the buttons will
	 * be displayed in menubar.
	 */
	private String buttonOrder = "mrc";
	
	/**
	 * This text will be set to the system menu for restore
	 * function.
	 */
	private String restoreMenuText = "Restore";

	/**
	 * This text will be set to the system menu for minimize
	 * function.
	 */
	private String minimizeMenuText = "Minimize";
	
	/**
	 * This text will be set to the system menu for close
	 * function.
	 */
	private String closeMenuText = "Close";
	
	/**
	 * This mnemonic will be set to the system menu for restore
	 * function.
	 */
	private int restoreMenuMnemonic = KeyEvent.VK_R;
	
	/**
	 * This mnemonic will be set to the system menu for minimize
	 * function.
	 */
	private int minimizeMenuMnemonic = KeyEvent.VK_N;
	
	/**
	 * This mnemonic will be set to the system menu for close
	 * function.
	 */
	private int closeMenuMnemonic = KeyEvent.VK_C;

	/**
	 * Whether to add a close button to the right of the main MDITabbedPane
	 * @see MDITabbedPane
	 * @see MDITabTitle
	 */
	private boolean tabCloseButtonEnabled;


	/**
	 * Constructs a MDIFrame with no title and uses the minimize, maximize and close
	 * buttons on the menubar.
	 *
	 */
	public MDIFrame(){
		this(null);
	}
	
	/**
	 * Constructs a MDIFrame with specified title and uses the minimize, maximize and close
	 * buttons on the menubar.
	 * 
	 * @param title Title for the MDIFrame
	 */
	public MDIFrame(String title){
		this(title, true);
	}
	
	/**
	 * Constructs a MDIFrame with specified title. You can set the <code>buttonsEnabled</code>
	 * parameter to false so the minimize, maximize and close buttons on the menubar when one of the viws is in maximized state
	 * will not be shown. This can be desirable to the programmers who want to use AceMDI with 
	 * look and feels that are not properly supported such as Aqua look and feel on Mac OS X. 
	 * @param title Title for the MDIFrame
	 * @param buttonsEnabled if this parameter is set to false, then
	 * 			the buttons that are shown in the right side of the menubar,
	 * 			when one of the view is in maximized satae, will not be used. 
	 */
	public MDIFrame(String title, boolean buttonsEnabled){
		super(title);
		tabbedPane = new MDITabbedPane(this);
		this.tabCloseButtonEnabled = true;
		this.buttonsEnabled = buttonsEnabled;
		//Give the focus to the defaultComoponent whenever the tab selection
		//changes.
		tabbedPane.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){					
				MDIView view = (MDIView)tabbedPane.getSelectedComponent();
				//System.out.println("currentViewPane: "+ currentViewPane + " paneChangedInternally: " + paneChangedInternally);
				//fire MDIView activated and Deactivated events 
				//if(currentViewPane.equals(MDIFrame.TABS) && paneChangedInternally == false){
				//System.out.println("currentViewPane: "+ currentViewPane);
				
				if(currentViewPane.equals(MDIFrame.TABS)){
					if(lastSelectedView == null){
						if(view != null)
							view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_ACTIVIATED);
						lastSelectedView = view;
					}
					
					if(view != lastSelectedView && suppressActiviationEvents == false){
						if(view != null)
							view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_ACTIVIATED);
						if(lastSelectedView != null)
							lastSelectedView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_DEACTIVIATED);
						//System.out.println("This executed");
						lastSelectedView = view;						
					}
					
					suppressActiviationEvents = false;
				}else{
					paneChangedInternally = false;
				}
				
				if(view != null){
					Component comp = view.getDefaultComponent();
					if(comp != null)
						comp.requestFocusInWindow();
				}
			}
		});

		//System menu functionality
		tabbedPane.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				int x = e.getX();
				int y = e.getY();
				final int index = tabbedPane.indexAtLocation(x,y);
				if(index != -1){
					MDIIcon icon = (MDIIcon)tabbedPane.getIconAt(index);
					Rectangle iconArea = new Rectangle(icon.getLastX(), icon.getLastY(), icon.getIconWidth(), icon.getIconWidth());
					if(iconArea.contains(x, y)){
						JPopupMenu systemMenu = new JPopupMenu();
						JMenuItem newItem = new JMenuItem(MDIFrame.this.restoreMenuText, MDIFrame.this.restoreMenuMnemonic);
						newItem.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								((MDIView)views.get(index)).setRestored();
							}
						});
						systemMenu.add(newItem);
						
						newItem = new JMenuItem(MDIFrame.this.minimizeMenuText, MDIFrame.this.minimizeMenuMnemonic);
						newItem.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								((MDIView)views.get(index)).setIconified();
							}
						});
						systemMenu.add(newItem);
						
						newItem = new JMenuItem(MDIFrame.this.closeMenuText, MDIFrame.this.closeMenuMnemonic);
						newItem.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								((MDIView)views.get(index)).closeView();
							}
						});
						systemMenu.add(newItem);
						
						systemMenu.show(e.getComponent(), x, y);
					}
				}
			}
		});
		
		tabbedPane.setFocusable(false);
		desktopPane = new JDesktopPane();
		desktopManager = new MDIDesktopManager();
		desktopPane.setDesktopManager(desktopManager);
		viewContainer = new JPanel(new CardLayout());
		viewContainer.add(tabbedPane, TABS);
		viewContainer.add(desktopPane, DESKTOP);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(viewContainer, BorderLayout.CENTER);
		windowButtons = createWindowButtons();
		//Following line is safeguard against a NullPoniterException 
		//which is thrown when user of MDIFrame class calls
		//addNewInternalView() method before setting a JMenuBar for
		//the JFrame.
		setJMenuBar( new JMenuBar() );
		this.setCurrentViewPane(MDIFrame.TABS);
	}
	
	/** 
	 * Creates the windowButtons Panel. Here note that the buttons
	 * are added in order specified by <code>buttonOrder</code>
	 * variable. Note that here it is assumed that the order provided 
	 * is valid. (For details on specifing order please see documentation)
	 * for <code>MDIFrame.setButtonOrder()</code> 
	 * @see <code>MDIFrame.windowButtons</code>
	 * @see <code>MDIFrame.setButtonOrder</code>
	 */
	protected JPanel createWindowButtons(){
		JPanel windowButtons = new JPanel();
		windowButtons.setOpaque(false);
		iconifyButton = new MDIFrameButton(MDIFrameButton.ICONIFY_BUTTON, this);
		restoreButton = new MDIFrameButton(MDIFrameButton.RESTORE_BUTTON, this);
		closeButton = new MDIFrameButton(MDIFrameButton.CLOSE_BUTTON, this);
		
		windowButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 2));
		
		char firstButton = buttonOrder.charAt(0);
		char secondButton = buttonOrder.charAt(1);
		char thirdButton = buttonOrder.charAt(2);
		
		if(firstButton == 'm')
			windowButtons.add(iconifyButton);
		else if(firstButton == 'r')
			windowButtons.add(restoreButton);
		else
			windowButtons.add(closeButton);
		
		if(secondButton == 'm')
			windowButtons.add(iconifyButton);
		else if(secondButton == 'r')
			windowButtons.add(restoreButton);
		else
			windowButtons.add(closeButton);
		
		if(thirdButton == 'm')
			windowButtons.add(iconifyButton);
		else if(thirdButton == 'r')
			windowButtons.add(restoreButton);
		else
			windowButtons.add(closeButton);
		
		return windowButtons;
	}
	
	/**
	 * Gives the last selected view. 
	 * <p><b>Note: This Method is for internal purpose olny</b>
	 * @return Returns the lastSelectedView.
	 */
	MDIView getLastSelectedView() {
		return lastSelectedView;
	}
	
	/**
	 * sets the last selected view to specified value.
	 * <p><b>Note: This Method is for internal purpose olny</b>
	 * @param lastSelectedView The lastSelectedView to set.
	 */
	void setLastSelectedView(MDIView lastSelectedView) {
		this.lastSelectedView = lastSelectedView;
	}
	
	
	/**
	 * sets the <code>currentViewPane</code> to specified value. And shows appropriate 
	 * pane in the <code>MDIFrame</code>
	 * @param currentViewPane The <code>currentViewPane</code> to set, must be one of <code>MDIFrame.TABS</code> or <code>MDIFrame.DESKTOP</code>
	 * @throws <code>IllegalArgumentException</code> if <code>currentViewPane</code> is not one 
	 * of <code>MDIFrame.TABS</code> or <code>MDIFrame.DESKTOP</code> 
	 */
	public void setCurrentViewPane(String currentViewPane) throws IllegalArgumentException {
		if(currentViewPane.equals(MDIFrame.TABS) || currentViewPane.equals(MDIFrame.DESKTOP)){
			this.currentViewPane = currentViewPane;
			((CardLayout)viewContainer.getLayout()).show(viewContainer, currentViewPane);
		}else{
			throw new IllegalArgumentException("currentViewPane must be one of MDIFrame.TABS or MDIFrame.DESKTOP");
		}
	}
	/**
	 * Gives the <code>viewContainer</code>, the JPanel that contains
	 * the JTabbedPane and JDesktopPane.
	 * @return Returns the <code>viewContainer</code>.
	 */
	public JPanel getViewContainer() {
		return viewContainer;
	}
	/**
	 * Provides the location where the new internal frame should be
	 * placed so as to avoide overlapping.
	 * @return the location of new Internal Frame to be created
	 * 
	 * @see <code>MDIFrame.addNewInternalView()</code>
	 */
	protected Point newWindowLocation(){
		/*This works well in JDL 1.4.2 but not in 1.4.1
		int desktopWidth = desktopPane.getWidth();
		int desktopHeight = desktopPane.getHeight();
		*/
		int desktopWidth = viewContainer.getWidth();
		int desktopHeight = viewContainer.getHeight();
		windowX += 35;
		windowY += 35;
		if(windowX > desktopWidth - 35 || windowY > desktopHeight - 35){
			windowX = 35;
			windowY = 35;
		}
		
		return new Point(windowX, windowY);
	}
	
	/**
	 * Adds the <code>view</code> to the <code>MDIFrame</code> and displayes it.
	 * @param view The <code>MDIView</code> tobe added 
	 */
	public void addInternalView(MDIView view){
		windowCounter++;
		if(view.getTitle() == null){
			view.setInitTitle("Untitled" + windowCounter);
		}
		views.add(view);
		
		if(currentViewPane.equals(TABS)){
			if(views.size() == 1){
				addWindowButtons();
			}
			tabbedPane.addTab(view.getTitle(), view.getIcon(), view);
			view.changeState(MDIView.MAXIMIZED);
			tabbedPane.setSelectedComponent(view);
			//System.out.println("view is max " + view.isMaximized());			
		}else{
			MDIInternalFrame frame = new MDIInternalFrame(this, view);
			Point location = newWindowLocation();
			frame.setLocation(location);
			int desktopWidth = desktopPane.getWidth();
			int desktopHeight = desktopPane.getHeight();
			int width = desktopWidth-100 > 100 ? desktopWidth-100 : 100;
			int height = desktopHeight-50 > 50 ? desktopHeight-50 : 50;
			frame.setSize(width, height);
					
			view.changeState(MDIView.RESTORED);
			view.setFrameBounds(frame.getBounds());
			frame.addInternalFrameListener(new FrameListener());
			desktopPane.add(frame);
			frame.setVisible(true);
			
		}
		view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_OPENED);
	}
	
	/**
	 * Adds the <code>view</code> to the <code>MDIFrame</code> and displayes it.
	 <p>convenience method for MDIFrame.addInternalview(). This method internally
	 calls MDIFrame.addInternalview().
	 * @param view The <code>MDIView</code> tobe added 
	 */
	public void add(MDIView view){
		this.addInternalView(view);
	}
	
	/**
	 * Toggles the view pane between <code>MDIFrame.TABS</code> and 
	 * <code>MDIFrame.DESKTOP</code>. This will not fire any MDIView
	 * events. It will only fire <code>MDIFrameEvent</code>s.
	 */
	public void changeView(){
		if(currentViewPane == MDIFrame.TABS){
			if(views.size() == 0){
				this.setCurrentViewPane(MDIFrame.DESKTOP);
			}else{
				currentViewPane = MDIFrame.DESKTOP;
				MDIView activeView = (MDIView)tabbedPane.getSelectedComponent();
				MDIInternalFrame frameToActivate = null;
				tabbedPane.removeAll();
				for(int i=0; i<views.size(); i++){
					MDIView view = (MDIView)views.get(i);
					MDIInternalFrame frame = new MDIInternalFrame(this, view);
					Rectangle rect = view.getFrameBounds();
					if(rect == null){
						Point location = newWindowLocation();
						frame.setLocation(location);
						/* This works in 1.4.2 but not in 1.4.1
						int desktopWidth = desktopPane.getWidth();
						int desktopHeight = desktopPane.getHeight();
						System.out.println("desktopWidth " + desktopWidth + " desktopHeight " + desktopHeight);
						int width = desktopWidth-100 > 100 ? desktopWidth-100 : 100;
						int height = desktopHeight-50 > 50 ? desktopHeight-50 : 50;
						*/
						
						int containerWidth  = viewContainer.getWidth();
						int containerHeight = viewContainer.getHeight();
						int width = containerWidth - 100 > 100 ? containerWidth - 100 : 100;
						int height = containerHeight - 50 > 50 ? containerHeight - 50 : 50;
						
						frame.setSize(width, height);
						view.setFrameBounds(rect);
					}else{
						frame.setBounds(rect);
					}
					desktopPane.add(frame);
					frame.addInternalFrameListener(new FrameListener());
					frame.show();
					
					if(view.wasIconified()){
						view.changeState(MDIView.ICONIFIED);
						suppressIconifiedEvent = true;
						try{frame.setIcon(true);}catch(PropertyVetoException pve){}
					}else{
						view.changeState(MDIView.RESTORED);						
					}
					
					if(view == activeView){
						frameToActivate = frame;
					}					
				}
				((CardLayout)viewContainer.getLayout()).show(viewContainer, MDIFrame.DESKTOP);;
				//try{frameToActivate.setSelected(true);}catch(PropertyVetoException pve){}
				frameToActivate.activateInternally();
				//if the active vew was in Iconified view restore it.
				if (frameToActivate.isIcon()){
					try{frameToActivate.setIcon(false);}catch(PropertyVetoException pve){}
					activeView.setWasIconified(false);
				}
				
				getJMenuBar().remove(windowButtons);
				getJMenuBar().revalidate();
				getJMenuBar().repaint();	//This is required for some custom themes for windows XP
			}
			MDIFrameEvent event = new MDIFrameEvent(this, MDIFrame.TABS, MDIFrame.DESKTOP);
			this.fireMDIFrameEvent(event);
		} else{
			if(views.size() == 0){
				this.setCurrentViewPane(MDIFrame.TABS);
			}else{
				JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
				MDIView activeView = null;
				if( selectedFrame == null){
					activeView = (MDIView)views.get(0);					
				}else{
					activeView = (MDIView)selectedFrame.getContentPane();
					((MDIInternalFrame)desktopPane.getSelectedFrame()).setDeactivatedInternally(true);	//Force to skip a unwanted Mdiview deactivated event
				}				
				JInternalFrame[] jinternalframes = desktopPane.getAllFrames();
				MDIInternalFrame[] frames = new MDIInternalFrame[jinternalframes.length];
				for(int d = 0; d<jinternalframes.length; d++){
					frames[d] = (MDIInternalFrame)jinternalframes[d];
					if(frames[d].isIcon()){
						((MDIView)frames[d].getContentPane()).setWasIconified(true);
					}else{
						((MDIView)frames[d].getContentPane()).setFrameBounds(frames[d].getBounds());
					}
					frames[d].disposeInternally();
				}
				for(int i=0; i<views.size(); i++){
					MDIView view = (MDIView)views.get(i);
					tabbedPane.addTab(view.getTitle(), view.getIcon(), view);
				}
				tabbedPane.setSelectedComponent(activeView);
				paneChangedInternally = true;
				//System.out.println("Just executed");
				this.setCurrentViewPane(MDIFrame.TABS);
									
				addWindowButtons();
				windowButtons.repaint();
			}
			MDIFrameEvent event = new MDIFrameEvent(this, MDIFrame.DESKTOP, MDIFrame.TABS);
			this.fireMDIFrameEvent(event);
		}
	}
	
	/** For internal purpose only */
	void addWindowButtons(){
		if(buttonsEnabled){
			getJMenuBar().add(windowButtons);
			getJMenuBar().revalidate();
		}
	}
		
	/** For internal purpose only */
	void removeWindowButtons(){
		if(buttonsEnabled){
			getJMenuBar().remove(windowButtons);
			getJMenuBar().revalidate();
			getJMenuBar().repaint();
		}
	}
	/**
	 * Changes the background color of the desktop that contains 
	 * the <code>MDIView</code>s.
	 * @param c color to set.
	 */
	public void setDesktopBackground(Color c){
		desktopPane.setBackground(c);
		desktopPane.repaint();
		tabbedPane.repaint();		
	}
	
	
	/**
	 * Gives the background of the desktop that contains 
	 * the <code>MDIView</code>s.
	 * @return the background color
	 */
	public Color getDesktopBackground(){
		return desktopPane.getBackground();
	}
	/**
	 * Gives the close button that is shown in the menubar when
	 * one of the views is in maximizd state.
	 * @return Returns the closeButton.
	 */
	public MDIFrameButton getCloseButton() {
		return closeButton;
	}
	/**
	 * Sets the close button that is shown in menubar when one of the
	 * views is in maximized state. This method will do nothing if 
	 * <code>buttonsEnabled</code> flag is set to <code>false</code>
	 * while creating the <code>MDIFrame</code>. 
	 * @param closeButton The closeButton to set.
	 * @throws IllegalArgumentException if closeButton.getButtonType() != MDIFrameButton.CLOSE_BUTTON
	 */
	public void setCloseButton(MDIFrameButton closeButton) throws IllegalArgumentException{
		if(closeButton.getButtonType() != MDIFrameButton.CLOSE_BUTTON)
			throw new IllegalArgumentException("The buttonType of closeButton must be MDIFrameButton.CLOSE_BUTTON");
		
		if(!buttonsEnabled){
			return;
		}
		
		windowButtons.remove(this.closeButton);
		windowButtons.add(closeButton);
		windowButtons.revalidate();		
		this.closeButton = closeButton;
	}
	
	/**
	 * Gives the restore button that is shown in menubar when 
	 * one of the view is in maximized state.
	 * @return Returns the restoreButton.
	 */
	public MDIFrameButton getRestoreButton() {
		return restoreButton;
	}
	/**
	 * Sets the restore button that is shown in menubar when 
	 * one of the viwes is in maximized state.This method will do nothing if 
	 * <code>buttonsEnabled</code> flag is set to <code>false</code>
	 * while creating the <code>MDIFrame</code>. 
	 * @param restoreButton The restoreButton to set.
	 * @throws IllegalArgumentException if restoreButton.getButtonType() != MDIFrameButton.RESTORE_BUTTON 
	 */
	public void setRestoreButton(MDIFrameButton restoreButton) throws IllegalArgumentException{
		if(restoreButton.getButtonType() != MDIFrameButton.RESTORE_BUTTON)
			throw new IllegalArgumentException("The buttonType of restoreButton must be MDIFrameButton.RESTORE_BUTTON");
		
		if(!buttonsEnabled){
			return;
		}
		
		windowButtons.remove(this.closeButton);
		windowButtons.remove(this.restoreButton);
		windowButtons.add(restoreButton);
		windowButtons.add(this.closeButton);
		windowButtons.revalidate();
		this.restoreButton = restoreButton;
	}
	/**
	 * Gives the close button that is shown in the menubar when
	 * one of the views is in maximized state. 
	 * @return Returns the iconifyButton.
	 */
	public MDIFrameButton getIconifyButton() {
		return iconifyButton;
	}
	/**
	 * Sets the iconify button that is shown in the menubar 
	 * when one of the views is in maximized state. This method will do nothing if 
	 * <code>buttonsEnabled</code> flag is set to <code>false</code>
	 * while creating the <code>MDIFrame</code>.
	 * @param iconifyButton The iconifyButton to set.
	 * @throws IllegalArgumentException if iconifyButton.getButtonType() != MDIFrameButton.ICONIFY_BUTTON
	 */
	public void setIconifyButton(MDIFrameButton iconifyButton) throws IllegalArgumentException{
		if(iconifyButton.getButtonType() != MDIFrameButton.ICONIFY_BUTTON)
			throw new IllegalArgumentException("The buttonType of iconifyButton must be MDIFrameButton.RESTORE_BUTTON");
		
		if(!buttonsEnabled){
			return;
		}
		
		windowButtons.remove(this.closeButton);
		windowButtons.remove(this.restoreButton);
		windowButtons.remove(this.iconifyButton);
		windowButtons.add(iconifyButton);
		windowButtons.add(this.restoreButton);
		windowButtons.add(this.closeButton);
		windowButtons.revalidate();
		this.iconifyButton = iconifyButton;
	}
	
	/**
	 * Gives is the minimize, maximize and close buttons on the menubar
	 * will be shown or not.
	 * @return Returns the buttonsEnabled.
	 */
	public boolean isButtonsEnabled() {
		return buttonsEnabled;
	}
	
	/**
	 * This method can be used to toggle the iconify, restore
	 * and close buttons that are shown on the menubar. If 
	 * <code>buttonsEnabled</code> is <code>true</code> then the buttons will
	 * be visible else the buttons will be invisible.
	 * @param buttonsEnabled The buttonsEnabled to set.
	 */
	public void setButtonsEnabled(boolean buttonsEnabled) {
		if(buttonsEnabled){
			if(!this.buttonsEnabled){
				this.buttonsEnabled = buttonsEnabled;
				if(views.size() >= 1 && currentViewPane == MDIFrame.TABS){
					addWindowButtons();
				}				
			}
		}else{
			if(this.buttonsEnabled){
				if(views.size() >= 1 && currentViewPane == MDIFrame.TABS){
					removeWindowButtons();
				}
				this.buttonsEnabled = buttonsEnabled;
			}
		}
	}
	
	/**
	 *<b>Note: For internal purpose only </b> 
	 */
	boolean isSuppressActiviationEvents() {
		return suppressActiviationEvents;
	}
	
	/**
	 * <b>Note: For internal purpose only </b>  
	 */
	void setSuppressActiviationEvents(boolean suppressActiviationEvents) {
		this.suppressActiviationEvents = suppressActiviationEvents;
	}
	
	/**
	 * <b>Note: For internal purpose only </b>  
	 */
	boolean isSuppressIconifiedEvent(){
		return suppressIconifiedEvent;
	}
	
	/**
	 * <b>Note: For internal purpose only </b>  
	 */
	void setSuppressIconifiedEvent(boolean suppressIconifiedEvent){
		this.suppressIconifiedEvent = suppressIconifiedEvent;
	}
	
	/**
	 * Gives the window buttons panel.
	 * @return the windowButtons panel.
	 */
	public JPanel getWindowButtons() {
		return windowButtons;
	}
	/**
	 * Gives the type of currentViewPane of the <code>MDIFrame</code>.
	 * One of <code>MDIFrame.TABS</code> or <code>MDIFrame.DESKTOP</code>.
	 * @return Returns the currentViewPane of this <code>MDIFrame</code>.
	 */
	public String getCurrentViewPane() {
		return currentViewPane;
	}
	/**
	 * Gives the tabbed pane that is used to show vies as tabs
	 * when one of the views is in maximized state.
	 * @return Returns the tabbedPane.
	 */
	public MDITabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
	/**
	 * gives the <code>JDesktopPane</code> that is used to show
	 * <code>JInternalFrame</code>s when on of the views is in 
	 * resotred or minimized state. 
	 * @return Returns the dektopPane.
	 */
	public JDesktopPane getDektopPane() {
		return desktopPane;
	}
	
	/**
	 * Gives the custom desktop manager that is used to manages
	 * the internal frames.
	 * @return the desktop manager.
	 */
	public MDIDesktopManager getDesktopManager() {
		return desktopManager;
	}
	
	/**
	 * Returns a vector containing all the views those are contained by this <code>MDIFrame</code>. 
	 * @return Returns the views.
	 */
	public Vector getViews() {
		return views;
	}
	
	/**
	 * Gives the currently active view. Note that this may return null 
	 * if all windows are minimised and not a single one is selected.
	 *  
	 * @return currently active view or <code>null</code>.
	 */
	public MDIView getActiveView(){
		if(currentViewPane == TABS){
			return (MDIView)tabbedPane.getSelectedComponent();
		}else{
			//this may return null if all windows are minimised and 
			//not a single one is selected.
			JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
			if(selectedFrame != null)
				return (MDIView)selectedFrame.getContentPane();
			else
				return null;
		}
	}
	
	/**
	 * Sets <code>view</code> as currently active view if it is not 
	 * currently active and sends MDIView selected and deselected 
	 * events to corrosponding views. Note that no events are generated
	 * if <code>view</code> is already active.
	 * 
	 * @param view <code>view</code> to set active.
	 * @throws IllegalArgumentException if <code>view </code> equals to <code>null</code>
	 * or the <code>MDIFrame</code> does not contain the <code>view</code>.
	 */
	public void setActiveView(MDIView view) throws IllegalArgumentException{
		if(view == null){
			throw new IllegalArgumentException("view should not be null");
		}			
			
		if(views.contains(view) == false){
			throw new IllegalArgumentException("The MDIFrame does not contain the specified view");
		}
		
		if(currentViewPane == MDIFrame.TABS){
			tabbedPane.setSelectedComponent(view);
		}else{
			JInternalFrame[] frames = desktopPane.getAllFrames();
			for(int i=0; i<frames.length; i++){
				if(frames[i].getContentPane() == view){
					try{frames[i].setSelected(true);}catch(PropertyVetoException pve){}
					break;
				}					
			}
		}		
	}
	

	
	/**
	 * Gives the view which should get the focus after <code>aView</code>.
	 * <p>The next focusable view returneed by this method is the next element
	 * after <code>aView</code> in the <code>views</code> 
	 * (can be obtained by <code>mdiFrameObj.getViews()</code>) vector. Note 
	 * that this is inconsistant with default <code>JDesktopPane</code> implementation
	 * where always the topmost available internal frame would get the focus. 
	 * 
	 * @param aView the specified <code>MDIView</code> 
	 * @return the next view which should get focus.
	 * @throws IllegalArgumentException if <code>aView</code> is null or if 
	 * the <code>MDIFrame</code> does not contain <code>aView</code>.
	 * @see javax.swing.JDesktopPane
	 * @see javax.swing.JLayeredPane
	 * @see MDIFrame#getViews()
	 */
	public MDIView nextFocusableView(MDIView aView) throws IllegalArgumentException{
		if(aView == null){
			throw new IllegalArgumentException("aView should not be null");
		}else{
			int index = views.indexOf(aView);
			if(index == -1){
				throw new IllegalArgumentException("The MDIFrame doesnot contain the View");
			}
			if(index < views.size() - 1){
				return (MDIView)views.get(index + 1);
			}else{
				return (MDIView)views.get(0);
			}
		}
	}
	
	/**
	 * This Method is intended for internal purpose only and 
	 * should not be called by outside/user code. If you want 
	 * to remove a view use one of <code>MDIView.closeView()</code>or 
	 * <code>MDIView.disposeView</code> instade.
	 * 
	 * @see MDIView#closeView()
	 * @see MDIView#disposeView()
	 *
	 * @param component
	 */
	protected void removeView(Component component){
		views.remove(component);
		/*
		if(views.size() <= 1){
			//nextViewAction.setEnabled(false);
			//previousViewAction.setEnabled(false);
		}*/
	}
	
	/**
	 * Gives a vector containing the <code>MDIFrameListerer</code>s that are 
	 * added to <code>the MDIFrame</code> object.
	 * @return a <code>Vector</code> containg <code>MDIFrameListerer</code>s
	 */
	public Vector getMDIFrameListeners() {
		return mdiFrameListeners;
	}	
	
	/**
	 * Gives text that is set to the system menu for close
	 * function.
	 * @return Returns the closeMenuText.
	 */
	public String getCloseMenuText() {
		return closeMenuText;
	}
	
	/**
	 * Sets text that is set to the system menu for close
	 * function.
	 * @param closeMenuText The closeMenuText to set.
	 */
	public void setCloseMenuText(String closeMenuText) {
		this.closeMenuText = closeMenuText;
	}
	
	/**
	 * Gives text that is set to the system menu for minimize
	 * function.
	 * @return Returns the minimizeMenuText.
	 */
	public String getMinimizeMenuText() {
		return minimizeMenuText;
	}
	
	/**
	 * Sets text that is set to the system menu for minimize
	 * function.
	 * @param minimizeMenuText The minimizeMenuText to set.
	 */
	public void setMinimizeMenuText(String minimizeMenuText) {
		this.minimizeMenuText = minimizeMenuText;
	}
	
	/**
	 * Gives text that is set to the system menu for restore
	 * function.
	 * @return Returns the restoreMenuText.
	 */
	public String getRestoreMenuText() {
		return restoreMenuText;
	}
	
	/**
	 * Sets text that is set to the system menu for restore
	 * function.
	 * @param restoreMenuText The restoreMenuText to set.
	 */
	public void setRestoreMenuText(String restoreMenuText) {
		this.restoreMenuText = restoreMenuText;
	}
	
	
	/**
	 * Gives the mnemonic that is set to the system menu for close
	 * function.
	 * @return Returns the closeMenuMnemonic.
	 */
	public int getCloseMenuMnemonic() {
		return closeMenuMnemonic;
	}
	
	/**
	 * Sets the mnemonic that is set to the system menu for close
	 * function.
	 * @param closeMenuMnemonic The closeMenuMnemonic to set.
	 */
	public void setCloseMenuMnemonic(int closeMenuMnemonic) {
		this.closeMenuMnemonic = closeMenuMnemonic;
	}
	
	/**
	 * Gives the mnemonic that is set to the system menu for minimize
	 * function.
	 * @return Returns the minimizeMenuMnemonic.
	 */
	public int getMinimizeMenuMnemonic() {
		return minimizeMenuMnemonic;
	}
	
	/**
	 * Sets the mnemonic that is set to the system menu for minimize
	 * function.
	 * @param minimizeMenuMnemonic The minimizeMenuMnemonic to set.
	 */
	public void setMinimizeMenuMnemonic(int minimizeMenuMnemonic) {
		this.minimizeMenuMnemonic = minimizeMenuMnemonic;
	}
	
	/**
	 * Gives the mnemonic that is set to the system menu for restore
	 * function.
	 * @return Returns the restoreMenuMnemonic.
	 */
	public int getRestoreMenuMnemonic() {
		return restoreMenuMnemonic;
	}
	
	/**
	 * Sets the mnemonic that is set to the system menu for restore
	 * function.
	 * @param restoreMenuMnemonic The restoreMenuMnemonic to set.
	 */
	public void setRestoreMenuMnemonic(int restoreMenuMnemonic) {
		this.restoreMenuMnemonic = restoreMenuMnemonic;
	}
	/**
	 * Adds the <code>lisener</code> to the <code>MDIFrame</code>
	 * object so that it can receive <code>MDIFrameEvent</code>s
	 * @param listener the <code>MDIFrameListener</code> to add
	 */
	public void addMDIFrameListener(MDIFrameListener listener){
		mdiFrameListeners.add(listener);
	}
	
	/**
	 * Removes the <code>listener</code> from the <code>MDIFrame</code>
	 * object so that it no longer receives the <code>MDIFrameEvent</code>s.
	 *  
	 * @param listener the <code>MDIFrameListener</code> to remove.
	 */
	public void removeMDIFrameListener(MDIFrameListener listener){
		mdiFrameListeners.remove(listener);
	}
	
	/**
	 * Fires <code>MDIFrameEvent.</code>
	 * <p>
	 * Note that this method is for internal purpose only. 
	 * Invoke it only when you are sure about what are you doing.
	 */
	public void fireMDIFrameEvent(MDIFrameEvent event){
		for(int i=0; i<mdiFrameListeners.size(); i++){
			((MDIFrameListener)mdiFrameListeners.get(i)).viewPaneChanged(event);
		}
	}

	/**
	 * @return true if this MDIFrame will add a close button to its tabbed panes
	 */
	public boolean isTabCloseButtonEnabled()
	{
		//TODO: Remove the button from existing tab components...
		return tabCloseButtonEnabled;
	}


	/**
	 * Set if this MDIFrame will add a close button to its tabbed panes
	 */
	public void setTabCloseButtonEnabled( boolean tabCloseButtonEnabled )
	{
		this.tabCloseButtonEnabled = tabCloseButtonEnabled;
	}

	/**
	 * The main purpose of this class is to change the behaviour of
	 * <code>DefaultDesktopManager</code> according to the needs of 
	 * <code>MDIFrame</code> class. Its respective methods will also
	 * fire appropriate <code>MDIView</code> events.
	 * @author Pritam G. Barhate
	 */
	public class MDIDesktopManager extends DefaultDesktopManager{
		/** This variable is used to supress the unwanted 
		 * MDIView restored event that is thrown during 
		 * maximizeFrame() method.
		 */
		private boolean supressRestoreEvent = false;
		/**
		 * Resizes the Frame so that the MDIFrame.DESKTOP view changes 
		 * to MDIFrame.TABS view and the frame fills the entire 
		 * area of the container. This method fires a MdIView maximized
		 * event.
		 * 
		 * @see javax.swing.DesktopManager.maximizeFrame() 
		 */		
		public void maximizeFrame(JInternalFrame f) {
			if(f.isIcon()){
				try{ f.setSelected(true);} catch(PropertyVetoException pve){}
				//super.deiconifyFrame(f);
				supressRestoreEvent = true;
				try{f.setIcon(false);}catch(PropertyVetoException pve){}
				MDIView activeView = (MDIView)desktopPane.getSelectedFrame().getContentPane();
				activeView.setWasIconified(false);
				activeView.setFrameBounds(f.getBounds());
				activeView.changeState(MDIView.MAXIMIZED);
				activeView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_MAXIMIZED);
				//System.out.println(activeView.getTitle());
			}else{	
				if(f.isSelected() == false){
					try{ f.setSelected(true);} catch(PropertyVetoException pve){}
				}					
				MDIView activeView = (MDIView)desktopPane.getSelectedFrame().getContentPane();
				activeView.changeState(MDIView.MAXIMIZED);
				activeView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_MAXIMIZED);
			}
			changeView();
		}
		
		/**
		 * Restores the Frame and fires a MDIView restored event.
		 * 
		 * @see javax.swing.DesktopManager.deiconifyFrame()
		 */
		public void deiconifyFrame(JInternalFrame f) {
			//calling this before any code ensures that 
			//frame to restore will be active frame
			//so restored event is fired correctly.
			super.deiconifyFrame(f);
			MDIView activeView = (MDIView)desktopPane.getSelectedFrame().getContentPane();
			activeView.setWasIconified(false);
			activeView.changeState(MDIView.RESTORED);
			if(supressRestoreEvent){
				supressRestoreEvent = false;
			}else{
				activeView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_RESTORED);
			}
			/*MDIView activeView = (MDIView)f.getContentPane();
			activeView.setWasIconified(false);
			activeView.changeState(MDIView.RESTORED);
			if(supressRestoreEvent){
				supressRestoreEvent = false;
			}else{
				activeView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_RESTORED);
			}*/
			
		}
		
		/**
		 * Iconifies the Frame and fires a MDIView iconified event.
		 */
		public void iconifyFrame(JInternalFrame f){
			//Using following commented section will send 
			//a iconified event to only active frame even 
			//if the frame which was iconified was not the 
			//active frame.
			/* 
			JInternalFrame frame = desktopPane.getSelectedFrame();
			if(frame != null){
				MDIView activeView = (MDIView)frame.getContentPane();
				activeView.setWasIconified(true);
				activeView.setFrameBounds(frame.getBounds());
				activeView.changeState(MDIView.ICONIFIED);
				activeView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_ICONIFIED);
			}
			*/
			if(suppressIconifiedEvent == false){
				MDIView activeView = (MDIView)f.getContentPane();
				activeView.setWasIconified(true);
				activeView.setFrameBounds(f.getBounds());
				activeView.changeState(MDIView.ICONIFIED);
				activeView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_ICONIFIED);
			}else{
				suppressIconifiedEvent = false;
			}
			super.iconifyFrame(f);
		}
		
		/**
		 * Closes the Frame and fires a MDIView closed event.
		 */
		public void closeFrame(JInternalFrame f) {
			MDIInternalFrame mdiif = (MDIInternalFrame)f;
			if(!mdiif.getDisposedInternally()){
				MDIView view = (MDIView)mdiif.getContentPane();
				MDIView nextView = MDIFrame.this.nextFocusableView(view);
				removeView(view);
				view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSED);
				if(mdiif.isSelected()){
					JInternalFrame[] frames = desktopPane.getAllFrames();
					for(int i=0; i<frames.length; i++){
						if(frames[i].getContentPane() == nextView){
							try {frames[i].setSelected(true); }catch(PropertyVetoException pve){}
							break;
						}
					}
				}
			}else{
				super.closeFrame(mdiif);
			}			
		}	
		/**
		 * gives the value of supressRestoreEvent.
		 * @return value of supressRestoreEvent
		 */
		protected boolean isSupressRestoreEvent() {
			return supressRestoreEvent;
		}
		
		/**
		 * Sets the supressRestoreEvent to specified value.
		 * @param supressRestoreEvent value to set.
		 */
		protected void setSupressRestoreEvent(boolean supressRestoreEvent) {
			this.supressRestoreEvent = supressRestoreEvent;
		}
	}
	
	/**
	 * The main purpose of this class to provide a mechanism to 
	 * fire the MDIView closing event at appropriate occasions.
	 * @author Pritam G. Barhate
	 */
	class FrameListener extends InternalFrameAdapter{
		/**
	     * Invoked when an internal frame is in the process of being closed.
	     * This method is provided so as to fire a MDiView closing event.
	     */
		public void internalFrameClosing(InternalFrameEvent e) {
			MDIInternalFrame frame = (MDIInternalFrame)e.getSource();
			MDIView theView = frame.getView();
			theView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSING);
			if(theView.getDefaultCloseOperation() == MDIView.DO_NOTHING_ON_CLOSE){
				frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
			}else{
				frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
			}
		}
	}
	
	
}
