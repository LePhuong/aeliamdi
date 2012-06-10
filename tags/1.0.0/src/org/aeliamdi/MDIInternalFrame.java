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

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * Sole purpose to extend this class from JInternalFrame is to 
 * fire the <code>MDIViewEvent</code>s correctly. For most of the 
 * common uses of the MDIFramework this class is not required to be dalt
 * with directly. Almost all of the functionality of the framework is 
 * provided through the <code>MDIFrame</code> and <code>MDIView</code>
 * classes.
 * @author Pritam G. Barhate
 */
public class MDIInternalFrame extends JInternalFrame{
	private boolean disposedInternally;
	private boolean activatedInternally;
	private boolean deactivatedInternally = false;
		
	/** This is set as ContentPane for the frame. */
	private MDIView view;
	
	/** The parent frame for this internal frame. */
	private MDIFrame parentFrame;
	/**
	 * Constructs the <code>MDIIntenalFrame</code> with specified parent
	 * <code>MDIFrame</code> and the specified <code>MDIView</code> is set as
	 * its content pane. 
	 * @param parent The parent <code>MDIFrame</code> for this frame.
	 * @param aNotNullView The view for the internal frame note that this view
	 * should never be null.
	 */	
	public MDIInternalFrame(MDIFrame parent, MDIView aNotNullView){
		super(aNotNullView.getTitle(), true, true, true, true);
		parentFrame = parent;
		view = aNotNullView;
		//Note that the view must be set as the content pane of the frame.
		//A lot of code logic depends on it.
		this.setContentPane(view);
		this.setFrameIcon(view.getIcon());
		// Give the focus to the defaultComoponent whenever the Frame is
		// activiated.
		this.addInternalFrameListener(new InternalFrameAdapter(){
				public void internalFrameActivated(InternalFrameEvent e) {
					if(view.getDefaultComponent() != null){
						view.getDefaultComponent().requestFocusInWindow();
					}
					if(!activatedInternally){
						view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_ACTIVIATED);
					}else{
						activatedInternally = false;
					}
				}
				
				public void internalFrameDeactivated(InternalFrameEvent e){
					if(!deactivatedInternally){
						view.fireMDIViewEvent(MDIViewEvent.MDIVIEW_DEACTIVIATED);
						parentFrame.setLastSelectedView(view);
					}
					else{
						deactivatedInternally = false;
						parentFrame.setLastSelectedView(view);
					}
					
				}
			});
	}			
	
	/**
	 * 
	 * @return <code>true</code> if the internal frame was closed using <code>disposeInternally()</code>
	 * method, otherwise <code>false</code>.
	 */
	boolean getDisposedInternally(){
		return disposedInternally;
	}
	
	/**
	 * This method should be used whenever you want to close the internal
	 * frame programatically.
	 */
	void disposeInternally(){
		disposedInternally = true;
		super.dispose();
	}	
	/**
	 * This method should be used whenever you want to activiate the internal
	 * frame programatically but not want to generate a 
	 * <code>MDIViewEvent.MDIVIEW_ACTIVIATED</code> event. 
	 */
	void activateInternally(){
		activatedInternally = true;
		try{super.setSelected(true);}catch(PropertyVetoException pve){}
	}
	
	/**
	 * Gives the <code>MDIFrame</code> to which this internal frame 
	 * is added.
	 * @return Returns the parentFrame.
	 */
	public MDIFrame getParentFrame() {
		return parentFrame;
	}
	
	/**
	 * Gives the view for this internal frame.
	 * @return Returns the view.
	 */
	public MDIView getView() {
		return view;
	}
	/**
	 * Gives the value of deactivatedInternally.
	 * <p>
	 * Note that this method is for internal purpose only. 
	 * @return Returns the deactivatedInternally.
	 */
	boolean isDeactivatedInternally() {
		return deactivatedInternally;
	}
	
	/**
	 * sets the deactivatedInternally to specified value.
	 * <p>
	 * Note that this method is for internal purpose only. 
	 * @param deactivatedInternally The deactivatedInternally to set.
	 */
	void setDeactivatedInternally(boolean deactivatedInternally) {
		this.deactivatedInternally = deactivatedInternally;
	}
	
}
