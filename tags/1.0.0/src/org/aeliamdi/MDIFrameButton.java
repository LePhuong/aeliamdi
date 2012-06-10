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
 * Created on Sep 18, 2004
 *
 * Pritam G. Barhate
 */
package org.aeliamdi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * This class represents the buttons those are shown on menubar of a 
 * <code>MDIFrame</code> when one of its views is in maximized state.
 * <p>
 * As of now it supports only Metal and Windows look and feel. If you
 * want to provide custom painting for any other look and feel, extend 
 * a class from this class. If you want to see how to do this correctly
 * please see the source code of CustomMDIButton.java file in the 
 * TestProject folder.
 * Then use <code>mdiFrameObject.setXXXButton(yourButtonObject);</code>
 * for all three buttons.
 * @author Pritam G. Barhate
 */
public class MDIFrameButton extends JButton{
	/** Constant indicating close button type */
	public static final int CLOSE_BUTTON = 1000;
	/** Constant indicating restore button type */
	public static final int RESTORE_BUTTON = 2000;
	/** Constant indicating iconify button type */
	public static final int ICONIFY_BUTTON = 3000;
	
	/** 
	 * Indicates the current type of button will be always one
	 * of <code>MDIFrameButton.CLOSE_BUTTON</code> , <code>MDIFrameButton.RESTORE_BUTTON</code> or 
	 * <code>MDIFrameButton.ICONIFY_BUTTON</code>. Note it is a readonly property. Once set in
	 * constructor it should not be changed.
	 */
	protected int buttonType;
	
	/** The MDIFrame in which the button is going to be used. */
	private	MDIFrame mdiFrame;	
	
	/**
	 * This variable is used to create the Mac Aqua Icons those 
	 * are precatched at the time of creation.
	 */
	private Icon aquaIcon = null;
	
	/**
	 * This variable is used to create the Mac Aqua Icons those 
	 * are precatched at the time of creation.
	 */
	private Icon aquaMouseOverIcon = null;
	
	/**
	 * This variable is used to save current ui id in paint method
	 * then later on it is compared to current ui id. If the current
	 * and pervious ui id is different, according to the ui new 
	 * icons and other painting properties for the button are set up. 
	 */
	private String previousUIID = null;
	
	/**
	 * This icon is used when there is no mouse foucs on the button.
	 */
	protected Icon buttonIcon = null;
	
	/**
	 * This icon is used when there is mouse foucs on the button when <code>mouseOverEnabled</code> is set to <code>true</code>.
	 */
	protected Icon mouseOverIcon = null;
	
	/**
	 * This property is used to toggle the rollover icon feature for the button.
	 */
	private boolean mouseOverIconEnabled = false;
	
	/**
	 * This is the instance of MouseOverListener that is
	 * used for the roll over icon effect. Note that the
	 * default rollOverIcon feature provided by the basic JButton
	 * is not sufficient for MDIFrameButton because of focus 
	 * problems presented by the fact that as soon as the 
	 * iconify or restore button is clicked the buttons
	 * are removed and then they don't get the subsequent 
	 * mouse out event.
	 */
	private MouseOverListener mouseOverListener = new MouseOverListener();
	
	;
	/**
	 * Constructs a MDIFramebutton with specified type for the specified MDIfarme  
	 * @param type The type of button, one of <code>MDIFrameButton.CLOSE_BUTTON</code> , <code>MDIFrameButton.RESTORE_BUTTON</code> or 
	 * <code>MDIFrameButton.ICONIFY_BUTTON</code>. 
	 * @param frame The MDIFrame for which the button is being created.
	 */
	public MDIFrameButton(int type, MDIFrame frame){
		super();
		this.buttonType = type;
		mdiFrame = frame;
		this.addActionListener(new TheActionListener());
		this.setFocusable(false);
		
		//precatch AquaIcons
		switch(this.buttonType){
		case MDIFrameButton.ICONIFY_BUTTON:
 			URL imageURL= this.getClass().getResource( "/res/images/aqua-orange.gif" );
			aquaIcon = new ImageIcon(imageURL);	
			URL imageURL1= this.getClass().getResource( "/res/images/aqua-orange-mouseover.gif" );
			aquaMouseOverIcon = new ImageIcon(imageURL1);	
			break;
		case MDIFrameButton.RESTORE_BUTTON:
			URL imageURL2= this.getClass().getResource( "/res/images/aqua-green.gif" );
			aquaIcon = new ImageIcon(imageURL2);		
			URL imageURL3= this.getClass().getResource( "/res/images/aqua-green-mouseover.gif" );
			aquaMouseOverIcon = new ImageIcon(imageURL3);
			break;				
		case MDIFrameButton.CLOSE_BUTTON:
			URL imageURL4= this.getClass().getResource( "/res/images/aqua-red.gif" );
			aquaIcon = new ImageIcon(imageURL4);	
			URL imageURL5= this.getClass().getResource( "/res/images/aqua-red-mouseover.gif" );
			aquaMouseOverIcon = new ImageIcon(imageURL5);
			break;	
		}
		
		this.addMouseListener(mouseOverListener);
	}
	
	/** Regular Paint method. Provides the painting code according to the button type. */
	public void paint(Graphics g){		
		String uiID = UIManager.getLookAndFeel().getID();
		if(!uiID.equals(previousUIID)){
			setupLookAndFeel(uiID);			
			previousUIID = uiID;
		}		
		super.paint(g);		
	}	
	
	/**
	 * This method sets all the look and feel specific proerties
	 * for the buttons. Note that the programmers using AceMDI who
	 * wish to provide support for custom look and feel must 
	 * override this method in a specific way. For example on how 
	 * do this, please look at the source code in CustomMDIButton.java
	 * in the test folder. 
	 * @param uiID The uiID for the current look and feel
	 */
	public void setupLookAndFeel(String uiID){
		if(uiID.equals("Metal")){
			switch(this.buttonType){
			case MDIFrameButton.ICONIFY_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
				break;
			case MDIFrameButton.RESTORE_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
				break;
			case MDIFrameButton.CLOSE_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.closeIcon");
			}		
			this.setIcon(buttonIcon);	
			this.setFocusPainted(false);
			// Provide Metal specific painting
			Dimension size = new Dimension(20, 20);
			this.setPreferredSize(size);
			this.setBorderPainted(false);
			this.setOpaque(false);			
			//The following sentence just sets an transperent color 
			//to the button to support ocean theme.
			this.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
			this.setFocusPainted(false);		
			this.setMouseOverIconEnabled(false);
		}else if(uiID.equals("Windows")){
			switch(this.buttonType){
			case MDIFrameButton.ICONIFY_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
				break;
			case MDIFrameButton.RESTORE_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
				break;
			case MDIFrameButton.CLOSE_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.closeIcon");
			}		
			this.setIcon(buttonIcon);	
			this.setFocusPainted(false);
			this.setBorderPainted(true);	
			this.setPreferredSize(new Dimension(buttonIcon.getIconWidth(), buttonIcon.getIconHeight()));
			this.setMouseOverIconEnabled(false);
			this.setBackground(UIManager.getColor("Button.background"));
			this.setOpaque(true);
		}else if(uiID.equals("Aqua")){
			switch(this.buttonType){
			case MDIFrameButton.ICONIFY_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
				break;
			case MDIFrameButton.RESTORE_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
				break;
			case MDIFrameButton.CLOSE_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.closeIcon");
			}		
			this.setIcon(buttonIcon);	
			this.setFocusPainted(false);
			this.setMargin(new Insets(0,0,0,0));
			this.setIcon(aquaIcon);
			this.setBorderPainted(false);
			this.setPreferredSize(new Dimension(20, 20));
			this.mouseOverIcon = aquaMouseOverIcon;
			this.setMouseOverIconEnabled(true);
			this.setBackground(UIManager.getColor("Button.background"));
		}else {
			switch(this.buttonType){
			case MDIFrameButton.ICONIFY_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
				break;
			case MDIFrameButton.RESTORE_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
				break;
			case MDIFrameButton.CLOSE_BUTTON:
				buttonIcon = UIManager.getIcon("InternalFrame.closeIcon");
			}		
			this.setIcon(buttonIcon);	
			this.setFocusPainted(false);
			//this.setPreferredSize(new Dimension(20, 20));
			this.setPreferredSize(new Dimension(buttonIcon.getIconWidth() + 1 , buttonIcon.getIconHeight() + 1));
			this.setMouseOverIconEnabled(false);
			this.setOpaque(true);
			this.setBackground(UIManager.getColor("Button.background"));
		}
		
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	
	/**
	 * Gives the icon used for the button.
	 * @return Returns the buttonIcon.
	 */
	public Icon getButtonIcon() {
		return buttonIcon;
	}
	
	/**
	 * Sets the icon for the button. Note that this method
	 * uses <code>JButton.setIcon()</code> to set the icon. 
	 * So you don't have to call it again.
	 * @param buttonIcon The buttonIcon to set.
	 */
	public void setButtonIcon(Icon buttonIcon) {
		this.buttonIcon = buttonIcon;
		this.setIcon(buttonIcon);
	}
	
	/**
	 * Gives the roll over icon for the button.
	 * @return Returns the mouseOverIcon.
	 */
	public Icon getMouseOverIcon() {
		return mouseOverIcon;
	}
	
	/**
	 * Sets the roll over icon for the button. Note that
	 * <code>MDIFrameButton</code> has its own imlementation of roll over icon
	 * feature seperate from basic roll over icon functionality
	 * provided by the <code>JButton</code> class. 
	 * <P>
	 * To enable roll over icon feature you must call
	 * <code>MDIFrameButtonInstance.set setMouseOverIconEnabled(true)</code>
	 * @param mouseOverIcon The mouseOverIcon to set.
	 * @see MDIFrameButton#setMouseOverIconEnabled(boolean)
	 */
	public void setMouseOverIcon(Icon mouseOverIcon) {
		this.mouseOverIcon = mouseOverIcon;
	}
	
	/**
	 * Gives the previous look and feel ID
	 * @return Returns the previousUIID.
	 */
	protected String getPreviousUIID() {
		return previousUIID;
	}	
	
	/**
	 * This method is used to toggle the roll over icon feature
	 * for the button. Note that the
	 * default rollOverIcon feature provided by the basic JButton
	 * is not sufficient for MDIFrameButton because of focus 
	 * problems presented by the fact that as soon as the 
	 * iconify or restore button is clicked, the buttons
	 * are removed and then they don't get the subsequent 
	 * mouse out event.
	 *
	 * @param mouseOverIconEnabled The mouseOverIconEnabled to set.
	 */
	public void setMouseOverIconEnabled(boolean mouseOverIconEnabled) {
		this.mouseOverIconEnabled = mouseOverIconEnabled;
	}
	
	/**
	 * gives the type of the button.
	 * @return Returns the button Type, always one
	 * of <code>MDIFrameButton.CLOSE_BUTTON</code> , <code>MDIFrameButton.RESTORE_BUTTON</code> or 
	 * <code>MDIFrameButton.ICONIFY_BUTTON</code>.
	 */
	public int getButtonType() {
		return buttonType;
	}
	
	/**
	 * The MouseListener implemetation that provides the 
	 * roll over icon feature for the MDIFrameButton
	 * @author Pritam G. Barhate
	 */
	class MouseOverListener extends MouseAdapter{
		public void mouseEntered(MouseEvent e) {
			if(mouseOverIconEnabled){
				MDIFrameButton.this.setIcon(mouseOverIcon);				
			}
			mdiFrame.windowButtons.repaint();
		}
		
		public void mouseExited(MouseEvent e) {
			if(mouseOverIconEnabled){
				MDIFrameButton.this.setIcon(buttonIcon);				
			}
			mdiFrame.windowButtons.repaint();
		}
		
		/**
		 * Note that the default rollOverIcon feature provided by the basic JButton
		 * is not sufficient for MDIFrameButton because of focus 
		 * problems presented by the fact that as soon as the 
		 * iconify or restore button is clicked, the buttons
		 * are removed and then they don't get the subsequent 
		 * mouse out event. To overcome this problem the
		 * mouse released event is used to set the normal 
		 * button Icon for the button.
		 */
		public void mouseReleased(MouseEvent e) {
			if(mouseOverIconEnabled){
				MDIFrameButton.this.setIcon(buttonIcon);				
			}
			mdiFrame.windowButtons.repaint();
		}
	}
	
	/**
	 * This class will define the behaviour of the <code>MDIFrameButton</code>
	 * acording to its type.
	 * @author Pritam G. Barhate
	 */
	class TheActionListener implements ActionListener{
		/**
		 * The action performed method.
		 * @see ActionListener.actionPerformed(ActionEvent e).
		 */
		public void actionPerformed(ActionEvent e){
			MDIView view = mdiFrame.getActiveView();
			switch(MDIFrameButton.this.buttonType){				
				case MDIFrameButton.ICONIFY_BUTTON:
					MDIView selectedView = mdiFrame.getActiveView();
					selectedView.setWasIconified(false);
					mdiFrame.changeView();
					MDIInternalFrame frame = (MDIInternalFrame)mdiFrame.getDektopPane().getSelectedFrame();
					try{						
						frame.setIcon(true);						
					}catch(PropertyVetoException pve){}
				 	break;
				case MDIFrameButton.RESTORE_BUTTON:					
					if(view.wasIconified()){
						mdiFrame.setSuppressIconifiedEvent(true);
						mdiFrame.changeView();						
					}else{
						mdiFrame.changeView();
						view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_RESTORED);
					}						
					break;
				case MDIFrameButton.CLOSE_BUTTON:
//					view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSING);
//					if(view.getDefaultCloseOperation() == MDIView.DISPOSE_ON_CLOSE){
//						mdiFrame.removeView(view);
//						JTabbedPane tabbedPane = mdiFrame.getTabbedPane();
//						int tabCount = tabbedPane.getTabCount();
//						int index = tabbedPane.indexOfComponent(view);
//						if(index == (tabCount - 1)){
//							//System.out.println("Index: " + index);
//							mdiFrame.setLastSelectedView(view);
//						}
//						tabbedPane.removeTabAt(index);
//						view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSED);
//						if(index != (tabCount - 1)){
//							view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_DEACTIVIATED);
//							MDIView nextView = mdiFrame.getActiveView();
//							if(nextView != null){
//								//System.out.println(nextView + "aaaa");
//								nextView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_ACTIVIATED);
//								mdiFrame.setLastSelectedView(nextView);
//							}						
//						}					
//						if(mdiFrame.getViews().size() == 0){
//							mdiFrame.removeWindowButtons();
//						}
//					}			
					view.closeView();
					break;				
			}
		}
	}		
}
