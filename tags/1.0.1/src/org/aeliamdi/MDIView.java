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

import java.awt.Component;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * This class extends <code>javax.swing.JPanel</code> class and acts 
 * as the "MDI aware" container. When in maximized state it will be
 * displayed as a "tab" in a tabbed pane in its <code>MDIFrame</code> 
 * and in other states it will be represented as the "content pane" of
 * a <code>javax.swing.JInternalFrame</code>
 * A possible example is : 
 * <blockquote><pre>JTextPane textPane= new JTextPane();
JScrollPane scrollPane = new JScrollPane(textPane);
MDIIcon icon = new MDIIcon("new.gif");
MDIView text = new MDIView(editorFrame, textPane, null, icon);
text.addMDIViewListener(new TheMDIViewListener());
text.setLayout(new BorderLayout());
text.add(scrollPane, BorderLayout.CENTER);
mdiFrameObject.addInternalView(text);
</pre></blockquote>
 *	<p>
 * If you are interested in receiving the <code>MDIViewEvent</code>s such as 
 * activiated, deactiviated, closing, closed etc. use <code>mdiViewObject.addMDIViewListener()</code>
 * method.	
 * @author Pritam G. Barhate
 */

public class MDIView extends JPanel {
	/** The title for the MDIView */
	private String title;
	/** The icon for the MDIView */
	private MDIIcon icon;
	/** The MDIFrame to which the MDIView will be added to */
	private MDIFrame mdiFrame;
	/** This is the compontent that gets the focus when the view is selected */
	private Component defaultComponent;
	/** 
	 * This variable keeps track whether the view was iconified 
	 * when represented as a MDIInternalFrame in an MDIFrame.
	 */
	private boolean wasIconified;
	/** Do nothing default close operation */
	public static final int DO_NOTHING_ON_CLOSE = 1;
	/** Dispose default close operation */
	public static final int DISPOSE_ON_CLOSE = 2;
	
	/** represents ICONIFIED state */
	public static final int ICONIFIED = 10;
	/** represents RESTORED  state */
	public static final int RESTORED = 20;
	/** represents MAXIMIZED state */
	public static final int MAXIMIZED = 30;
	
	/** Action tobe taken when user closes the view */
	private int defaultCloseOperation = DISPOSE_ON_CLOSE;
	
	/** 
	 * State of view window one of <code>MDIView.ICONIFIED, MDIView.RESTORED</code> 
	 * or <code>MDIView.MAXIMIZED</code>
	 * */
	private int state;
	
	/**
	 * Bounds of the restored internal frame that contains view when
	 * the <code>MDIFrame</code> is in <code>MDIFrame.DESKTOP</code>
	 * mode.
	 */
	private Rectangle frameBounds;
	
	/**
	 * a vector holding <code>MDIViewListerer</code>s that are added to the <code>MDIView</code> object.
	 */
	private Vector mdiViewListeners = new Vector();
	
	/**
	 * Constructs a <code>MDIView</code> with specified parent
	 * <code>MDIFrame</code>.
	 * @param parentFrame
	 */
	public MDIView(MDIFrame parentFrame){
		this(parentFrame, null, null, null);
	}
	
	/**
	 * Constructs a <code>MDIView</code> with specified parent
	 * <code>MDIFrame</code>, defaultcomponent(the component that will get focus when the view is activiated), title and icon.
	 * 
	 */
	public MDIView(MDIFrame parentFrame, Component defaultComponent, String title, MDIIcon icon){
		super();
		mdiFrame = parentFrame;
		this.defaultComponent = defaultComponent;
		this.title = title;
		this.icon = icon;		
	}
	/**
	 * Returns the icon of this view. This method will return null if the 
	 * icon is not assigned.
	 * @return icon if it is assigned or null.
	 */
	public MDIIcon getIcon() {
		return icon;
	}
	
	
	/**
	 * Gives the <code>MDIViewListener</code>s added to the <code>MDIView</code> Object. 
	 * @return Returns the mdiViewListeners.
	 */
	public Vector getMDIViewListeners() {
		return mdiViewListeners;
	}
	/**
	 * For internal purpose only
	 * @return Returns the wasIconified.
	 */
	boolean wasIconified() {
		return wasIconified;
	}
	/**
	 * For internal purpose only
	 * @param wasIconified The wasIconified to set.
	 */
	void setWasIconified(boolean wasIconified) {
		this.wasIconified = wasIconified;
	}
	
	/**
	 * Gives the bounds of the restored internal frame that contains view when
	 * the <code>MDIFrame</code> is in <code>MDIFrame.DESKTOP</code>
	 * mode.
	 * @return Returns the frameBounds.
	 */
	Rectangle getFrameBounds() {
		return frameBounds;
	}
	
	/**
	 * Sets the bounds of the restored internal frame that contains view when
	 * the <code>MDIFrame</code> is in <code>MDIFrame.DESKTOP</code>
	 * mode.
	 * @param frameBounds The frameBounds to set.
	 */
	void setFrameBounds(Rectangle frameBounds) {
		this.frameBounds = frameBounds;
	}
	
	/**
	 * For internal purpose only.
	 * Sets the <code>MDIView.state</code> of the view to <code>state</code>
	 * @param state The state to set, should be one of <code>MDIView.ICONIFIED, MDIView.RESTORED</code>
	 * 			or <code>MDIView.MAXIMIZED</code>
	 * @throws IllegalArgumentException if <code>state</code> is not one of 
	 * 			<code>MDIView.ICONIFIED, MDIView.RESTORED</code> or <code>MDIView.MAXIMIZED</code>
	 */
	void changeState(int state) throws IllegalArgumentException{
		switch(state){
		case MDIView.MAXIMIZED:
			this.state = MDIView.MAXIMIZED;
			break;
		case MDIView.RESTORED:
			this.state = MDIView.RESTORED;
			break;
		case MDIView.ICONIFIED:
			this.state = MDIView.ICONIFIED;
			break;
		default:
			throw new IllegalArgumentException("state should be one of MDIView.ICONIFIED, MDIView.RESTORED or MDIView.MAXIMIZED");
		}
	}
	
	/**
	 * Gives the <code>state</code> of the window
	 * @return Returns the state one of <code>MDIView.ICONIFIED, MDIView.RESTORED</code>
	 * 			or <code>MDIView.MAXIMIZED</code>
	 */
	public int getState() {
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS)
			return MDIView.MAXIMIZED; //In this stage all windows are maximized.
		else
			return state;
	}
	
	/**
	 * Returns the defaultCloseOperation. It is one of the 
	 * <ul>
	 * <li>DO_NOTHING_ON_CLOSE</li>
	 * <li>DISPOSE_ON_CLOSE</li>
	 * </ul>
	 * @return the defaultCloseOperation.
	 */
	public int getDefaultCloseOperation() {
		return defaultCloseOperation;
	}
	
	/**
	 * Sets the default close operation for this view.
	 * @param defaultCloseOperation The defaultCloseOperation to set. It should be 
	 * one of <code>MDIView.DO_NOTHING_ON_CLOSE</code> or <code>MDIView.DISPOSE_ON_CLOSE</code> or
	 * a <code>IllegalArgumentException</code> will be thrown.
	 * @throws IllegalArgumentException
	 */
	public void setDefaultCloseOperation(int defaultCloseOperation) throws IllegalArgumentException{
		if(defaultCloseOperation == DO_NOTHING_ON_CLOSE || defaultCloseOperation == DISPOSE_ON_CLOSE)
			this.defaultCloseOperation = defaultCloseOperation;
		else
			throw new IllegalArgumentException("defaultCloseOperation should be one of MDIView.DO_NOTHING_ON_CLOSE or MDIView.DISPOSE_ON_CLOSE");
	}
	/**
	 * Returns the compontent that gets the focus when the view is selected 
	 * @return defaultComponent.
	 */
	public Component getDefaultComponent() {
		return defaultComponent;
	}
	
	/**
	 * Sets the compontent that gets the focus when the view is selected 
	 * @param defaultComponent The defaultComponent to set.
	 * @throws <code>IllegalArgumentException</code> if the defaultComponent argument is not contained by the MDIView
	 */
	public void setDefaultComponent(Component defaultComponent) throws IllegalArgumentException{
		Component [] components = this.getComponents();
		boolean found = false;
		// check whether the defaultComponent argument is within
		// the view. If no throw a IllegalArgumentException.
		for(int i=0; i<components.length; i++){
			if(components[i] == defaultComponent)	
				found = true;				
		}
		if(found)
			this.defaultComponent = defaultComponent;
		else
			throw new IllegalArgumentException("The MDIView does not contain the specified component");
	}
	
	/**
	 * Sets the icon for the view and reflects the necessary changes in
	 * the <code>MDIFrame</code> that contains the view. 
	 * @param icon The icon to set.
	 */
	public void setIcon(MDIIcon icon) {
		this.icon = icon;
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS){
			MDITabbedPane tabpane = mdiFrame.getTabbedPane();
			int index = tabpane.indexOfComponent(this);
			if(index != -1){
				tabpane.setIconAt(index, icon);
			}
		}else{
			JInternalFrame frames[] = mdiFrame.getDektopPane().getAllFrames();
			for(int i=0; i<frames.length; i++){
				if(frames[i].getContentPane() == this){
					frames[i].setFrameIcon(icon);			
					break;
				}
			}	
		}
	}
	
	/**
	 * Returns the title of this view. It can be null if the title 
	 * is not set.
	 * @return title.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * For internal purpose only. 
	 * @see MDIFrame.addInternalView();
	 * @param title the title to set
	 */
	void setInitTitle(String title){
		this.title = title;
	}
	
	/**
	 * Sets the title for the view and reflects the title change
	 * in the <code>MDIFrame</code> that contians this view.
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS){
			MDITabbedPane tabpane = mdiFrame.getTabbedPane();
			int index = tabpane.indexOfComponent(this);
			if(index != -1){
				tabpane.setTitleAt(index, title);
			}			
		}else{
			JInternalFrame frames[] = mdiFrame.getDektopPane().getAllFrames();
			for(int i=0; i<frames.length; i++){
				if(frames[i].getContentPane() == this){
					frames[i].setTitle(title);			
					break;
				}
			}	
		}
	}
	
	
	
	/**
	 * Returns the <code>MDIFrame</code> that contains this view.
	 * @return the mdiFrame.
	 */
	public MDIFrame getMdiFrame() {
		return mdiFrame;
	}
	
	/**
	 * Returns whether the <code>MDIView</code> is maximized.
	 * @return <code>true</code> if the frame is maximized, <code>false</code> otherwise.
	 */
	public boolean isMaximized(){
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS)
			return true; //In this stage all windows are maximized.		
		else 
			return false;
	}
	
	/**
	 * Set the view to maximized state and sets it selected. 
	 * Also this method will fire appropriate events according to action taken.
	 * Note that if active view pane is tabbed pane the view will
	 * be only set selected. Also in this case only <code>MDIView</code> 
	 * activiated event will be fired.
	 */
	public void setMaximized(){
		if(this.isMaximized() == false){
			mdiFrame.setActiveView(this);
			mdiFrame.changeView();
			this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_MAXIMIZED);
		}else{
			mdiFrame.setActiveView(this);
		}
	}
	
	/**
	 * Returns whether the <code>MDIView</code> is in iconified state.
	 * @return 	<code>true</code> if the frame is in iconified state, 
	 * 			<code>false</code> otherwise.
	 */
	public boolean isIconified(){
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS){
			return false;
		}else{
			if(state == MDIView.ICONIFIED)
				return true;
			else 
				return false;
		}
	}
	
	/**
	 * Sets the view to iconified state and fires appropriate <code>MDIView</code> events. 
	 */
	public void setIconified(){
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS){
			mdiFrame.changeView();
		}
		JInternalFrame frames[] = mdiFrame.getDektopPane().getAllFrames();
		for(int i=0; i<frames.length; i++){
			if(frames[i].getContentPane() == this){
				try{
					frames[i].setIcon(true);
				}catch(PropertyVetoException pve){
					
				}				
				break;
			}
		}	
	}
	
	/**
	 * Returns whether the <code>MDIView</code> is restored.
	 * @return true if the <code>MDIView</code> is restored, <code>false</code> otherwise.
	 */
	public boolean isRestored(){
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS){
			return false;
		}else{
			if(state == MDIView.RESTORED)
				return true;
			else 
				return false;
		}
	}
	
	/**
	 * Sets the view to restored state and activates it.<p>
	 * Also this method will fire appropriate events according to action taken. 
	 * For Example, if the view is in maximized state and is not selected. then
	 * calling this method will fire <code>MDIView</code> restored
	 * and <code>MDIView</code> activiated events.
	 * Note that that if the view is already in restored state
	 * then it is only activiated. Also in this case only <code>MDIView</code> 
	 * activiated event will be fired.
	 */
	public void setRestored(){
		//if the view is already in restored state just set it 
		//active.
		if(this.isRestored()){
			JInternalFrame frames[] = mdiFrame.getDektopPane().getAllFrames();
			for(int i=0; i<frames.length; i++){
				if(frames[i].getContentPane() == this){
					try{
						frames[i].setSelected(true);
					}catch(PropertyVetoException pve){
						
					}				
					break;
				}
			}	
			return;
		}
		
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS){
			if(this.wasIconified()){
				mdiFrame.setSuppressIconifiedEvent(true);						
				mdiFrame.changeView();						
			}else{
				mdiFrame.changeView();
				this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_RESTORED);
			}
		}
		JInternalFrame frames[] = mdiFrame.getDektopPane().getAllFrames();
		for(int i=0; i<frames.length; i++){
			if(frames[i].getContentPane() == this){
				try{
					frames[i].setIcon(false);
					frames[i].setSelected(true);
				}catch(PropertyVetoException pve){
					
				}				
				break;
			}
		}		
	}
	
	/**
	 * Returns whether the <code>MDIView</code> is "selected" or active.
	 * @return <code>true</code> if the <code>MDIView</code> is "selected" or active.
	 */
	public boolean isSelected(){
		return mdiFrame.getActiveView() == this ? true : false; 
	}
	
	/**
	 * Sets the <code>MDIView</code> "selected" or "deselected" depending upon
	 * the value of <code>selected</code>.
	 * <p>
	 * If <code>selected</code> is <code>true</code> then the <code>MDIView</code> is selected
	 * and the <code>MDIView</code> receives a activiated event. 
	 * Note that if the <code>MDIView</code> is already selected nothing happens.
	 * <p>
	 * If <code>selected</code> is <code>false</code> and  the <code>MDIView</code> is already "selected" 
	 * then the <code>MDIView</code> is "deselected" and the <code>MDIView</code> receives a 
	 * deactiviated event, else nothing happens. Note that if the <code>MDIView</code> is the 
	 * only view in its <code>MDIFrame</code> and <code>selected</code> is <code>false</code> 
	 * then also nothing happens. 
	 * @param 	selected <code>true</code> if the view is to be selected and 
	 * 			<code>false</code> if the view is to be deselected.
	 */
	public void setSelected(boolean selected){
		if(selected){
			mdiFrame.setActiveView(this);
		}else{
			if( this == mdiFrame.getActiveView()){
				mdiFrame.setActiveView(mdiFrame.nextFocusableView(this));
			}
		}
	}
	
	/**
	 * This method closes the view and removes it from its 
	 * <code>MDIFrame</code>. Note that this method has same effect
	 * as that of clicking the close button of the view.
	 * <p>
	 * It will fire a <code>MDIVIEW_CLOSING</code> event before closing closing the view
	 * and <code>MDIVIEW_CLOSED</code> event after closing it.
	 * If you don't want <code>MDIVIEW_CLOSING</code> event to be fired
	 * then use MDIView.disposeView() otherwise both methods are same.
	 * <p>
	 * <b>
	 * Caution: Please note that it is not safe to use this method
	 * when the current view is <code>MDIFrame.TABS</code> due to bugs (Bug ID Nos. 4253819, 4230160 and numerous others)
	 * in JTabbedPane while removing tabs. This method should be
	 * called sequentially on all views, when you want to close all the views just before the <code>MDIFrame
	 * </code> is closed. It is safe in that case, even if the current view is 
	 * <code>MDIFrame.TABS</code>. The problem with this method is that at certain special situations
	 * the activiation events are not properly fired when a tab is removed
	 * from the tabbed pane due to above specified bugs. I have tried to
	 * cover almost all of the inconsistant behaviors and tried to fire correct
	 * events. But every time I think it is perfect, a new inconsistant behavior is
	 * found. So, for now, try and avoid calling this method except while closing
	 * the <code>MDIFrame</code>. Note that it is perfectly
	 * safe when views are closed with close button or through system menu.
	 * 
	 * </b>
	 * @see MDIView#disposeView()
	 */
	public void closeView(){
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS){
			this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSING);
			if(this.getDefaultCloseOperation() == MDIView.DISPOSE_ON_CLOSE){
				this.removeTabbedView();
			}		
		}else{
			this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSING);
			JInternalFrame frames[] = mdiFrame.getDektopPane().getAllFrames();
			for(int i=0; i<frames.length; i++){
				if(frames[i].getContentPane() == this){
					MDIView nextView = mdiFrame.nextFocusableView(this);					
					if(frames[i].isSelected()){
						JInternalFrame[] frames1 = mdiFrame.getDektopPane().getAllFrames();
						for(int j=0; j<frames1.length; j++){
							if(frames1[j].getContentPane() == nextView){
								try {frames1[j].setSelected(true); }catch(PropertyVetoException pve){}
								break;
							}
						}
					}
					mdiFrame.removeView(this);
					
					((MDIInternalFrame)frames[i]).disposeInternally();
					this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSED);
					break;
				}
			}	
		}
	}
	
	/**
	 * This method closes the view and removes it from its 
	 * <code>MDIFrame</code>. This method will fire <code>MDIVIEW_CLOSED</code>
	 * event but it will not fire <code>MDIVIEW_CLOSING</code> event.
	 * If you want <code>MDIVIEW_CLOSING</code> event to be fired used
	 * <code>MDIFrame.closeView</code> instade; otherwise both methods
	 * are same. 
	 * 
	 * <p>
	 * <b>
	 * Caution: Please note that it is not safe to use this method
	 * when the current view is <code>MDIFrame.TABS</code> due to bugs (Bug ID Nos. 4253819, 
	 * 4230160 and numerous others)in JTabbedPane while removing tabs. This method should be
	 * called sequentially on all views, when you want to close all the views just before the <code>MDIFrame
	 * </code> is closed. It is safe in that case, even if the current view is 
	 * <code>MDIFrame.TABS</code>. The problem with this method is that at certain special situations
	 * the activiation events are not properly fired when a tab is removed
	 * from the tabbed pane due to above specified bugs. I have tried to
	 * cover almost all of the inconsistant behaviors and tried to fire correct
	 * events. But every time I think it is perfect, a new inconsistant behavior is
	 * found. So, for now, try and avoid calling this method except while closing
	 * the <code>MDIFrame</code>. Note that it is perfectly
	 * safe when views are closed with close button or through system menu.
	 * 
	 * </b>
	 */
	public void disposeView(){				
		if(mdiFrame.getCurrentViewPane() == MDIFrame.TABS){
			//this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSING);
			if(this.getDefaultCloseOperation() == MDIView.DISPOSE_ON_CLOSE){
				this.removeTabbedView();
			}		
		}else{
			//this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSING);
			JInternalFrame frames[] = mdiFrame.getDektopPane().getAllFrames();
			for(int i=0; i<frames.length; i++){
				if(frames[i].getContentPane() == this){
					MDIView nextView = mdiFrame.nextFocusableView(this);					
					if(frames[i].isSelected()){
						JInternalFrame[] frames1 = mdiFrame.getDektopPane().getAllFrames();
						for(int j=0; j<frames1.length; j++){
							if(frames1[j].getContentPane() == nextView){
								try {frames1[j].setSelected(true); }catch(PropertyVetoException pve){}
								break;
							}
						}
					}
					mdiFrame.removeView(this);
					((MDIInternalFrame)frames[i]).disposeInternally();
					this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSED);
					break;
				}
			}	
		}		
	}
	
	/**
	 * Note: This method is created to put the common code 
	 * from closeView() and disposeView() methods.
	 *
	 */
	private void removeTabbedView(){
		JTabbedPane tabbedPane = mdiFrame.getTabbedPane();
		int tabCount = tabbedPane.getTabCount();
		MDIView lastActiveView = mdiFrame.getActiveView();
		int index = tabbedPane.indexOfComponent(this);
		int selectedIndex = mdiFrame.getTabbedPane().getSelectedIndex();
		
		if(index < selectedIndex){			
			mdiFrame.setSuppressActiviationEvents(true);
		}
		
		if(index == selectedIndex && index == (tabCount - 1)){
			mdiFrame.setLastSelectedView(this);
		}
		
		tabbedPane.removeTabAt(index);
		mdiFrame.removeView(this);
		
		if(index < selectedIndex){
			tabbedPane.setSelectedIndex(selectedIndex - 1);
		}
		
		this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_CLOSED);
		
		if(lastActiveView == this){
			if(index != (tabCount - 1)){
				this.fireMDIViewEvent(MDIViewEvent.MDIVIEW_DEACTIVIATED);
				MDIView nextView = mdiFrame.getActiveView();
				if(nextView != null){
					nextView.fireMDIViewEvent(MDIViewEvent.MDIVIEW_ACTIVIATED);
					mdiFrame.setLastSelectedView(nextView);
				}						
			}
		}
		
		if(mdiFrame.getViews().size() == 0){
			mdiFrame.removeWindowButtons();
		}
	}
	
	/**
	 * Adds the specified <code>MDIViewListener</code> to the <code>MDIView</code> object
	 * so that it can recive the <code>MDIViewEvents</code> fired by the <code>MDIView</code>
	 * @param listener the MDIViewListener to add
	 */
	public void addMDIViewListener(MDIViewListener listener){
		mdiViewListeners.add(listener);		
	}
	
	/**
	 * Removes the specified <code>MDIViewListener</code> from the <code>MDIView</code>
	 * so that it no longer recives the <code>MDIViewEvents</code> fired by the <code>MDIView</code>
	 * @param listener the MDIViewListener to to remove
	 */
	public void removeMDIViewListener(MDIViewListener listener){
		mdiViewListeners.remove(listener);
	}	
	
	
	/**
	 * Returns a string representation of this view i.e. its <code>title</code>.
	 * The returned string may be empty but may not be null. 
	 * @return title of the view.
	 * @see java.lang.Object.toString()
	 */
	public String toString(){
		return title == null ? "" : title;
	}
	
	/**
	 * Fires a </code>MDIViewEvent.</code>
	 * @param eventId id of the event tobe fired. Must be one of the following:
	 * <ul>
	 * <li>MDIViewEvent.MDIVIEW_ACTIVIATED</li>
	 * <li>MDIViewEvent.MDIVIEW_DEACTIVIATED</li>
	 * <li>MDIViewEvent.MDIVIEW_OPENED</li>
	 * <li>MDIViewEvent.MDIVIEW_CLOSED</li>
	 * <li>MDIViewEvent.MDIVIEW_ICONIFIED</li>
	 * <li>MDIViewEvent.MDIVIEW_RESTORED</li>
	 * <li>MDIViewEvent.MDIVIEW_MAXIMIZED</li>
	 * <li>MDIViewEvent.MDIVIEW_CLOSING</li>
	 * </ul>
	 * If the event id is not one of the above nothing happens.
	 */
	public void fireMDIViewEvent(int eventId){
		//Trigger the viewPaneChanged Event on our parent!
		this.getMdiFrame().fireMDIFrameEvent( new MDIFrameEvent( this.getMdiFrame(), null, null ) );

		MDIViewEvent event = new MDIViewEvent(this, eventId);
		for(int i=0; i<mdiViewListeners.size(); i++){
			switch(eventId){
				case MDIViewEvent.MDIVIEW_ACTIVIATED:
					((MDIViewListener)mdiViewListeners.get(i)).MDIViewActivated( event );
					break;
				
				case MDIViewEvent.MDIVIEW_DEACTIVIATED:
					((MDIViewListener)mdiViewListeners.get(i)).MDIViewDeactivated( event );
					break;
				
				case MDIViewEvent.MDIVIEW_OPENED:
					((MDIViewListener)mdiViewListeners.get(i)).MDIViewOpened(event);			
					break;
					
				case MDIViewEvent.MDIVIEW_CLOSED:
					((MDIViewListener)mdiViewListeners.get(i)).MDIViewClosed(event);
					break;
					
				case MDIViewEvent.MDIVIEW_ICONIFIED:
					((MDIViewListener)mdiViewListeners.get(i)).MDIViewIconified(event);
					break;
					
				case MDIViewEvent.MDIVIEW_RESTORED:
					((MDIViewListener)mdiViewListeners.get(i)).MDIViewRestored(event);
					break;
					
				case MDIViewEvent.MDIVIEW_MAXIMIZED:
					((MDIViewListener)mdiViewListeners.get(i)).MDIViewMaximized(event);
					break;
					
				case MDIViewEvent.MDIVIEW_CLOSING:
					((MDIViewListener)mdiViewListeners.get(i)).MDIViewClosing(event);
					break;					
			}
		}
	}	
}
