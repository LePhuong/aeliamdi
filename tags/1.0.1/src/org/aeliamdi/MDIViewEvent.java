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
 * Created on Sep 23, 2004
 *
 * Pritam G. Barhate
 */
package org.aeliamdi;

import java.awt.AWTEvent;

/**
 * Event class representing MDIView events.
 * @author Pritam G. Barhate
 */
public class MDIViewEvent extends AWTEvent {
	/**
     * The first number in the range of IDs used for <code>MDIView</code> events.
     */
	public static final int MDIVIEW_FIRST = 20143;
	
	/**
	 * The <code>MDIView</code> "activiated" event type.
	 */
	public static final int MDIVIEW_ACTIVIATED = MDIVIEW_FIRST;
	
	/**
	 * The <code>MDIView</code> "deactiviated" event type.
	 */
	public static final int MDIVIEW_DEACTIVIATED = MDIVIEW_ACTIVIATED + 1;
	
	/**
	 * The "MDIView opened" Event type. This event is raised only once that is
	 * when the <code>MDIView</code> is added to MDIFrame.
	 * 
	 * @see MDIFrame#addInternalView(MDIView)
	 * @see MDIFrame#add(MDIView)
	 */
	public static final int MDIVIEW_OPENED = MDIVIEW_DEACTIVIATED + 1;
	
	/**
	 * The <code>MDIView</code> "closed" event type. 
	 */
	public static final int MDIVIEW_CLOSED = MDIVIEW_OPENED  + 1;
	
	/**
	 *The <code>MDIView</code> "iconified" event type.
	 */
	public static final int MDIVIEW_ICONIFIED = MDIVIEW_CLOSED + 1;
	
	/**
	 *The <code>MDIView</code> "restored" event type.
	 */
	public static final int MDIVIEW_RESTORED = MDIVIEW_ICONIFIED + 1;
	
	/**
	 *The <code>MDIView</code> "maximized" event type.
	 */
	public static final int MDIVIEW_MAXIMIZED = MDIVIEW_RESTORED + 1;
	
	/**
	 *The <code>MDIView</code> "closing" event type.
	 */
	public static final int MDIVIEW_CLOSING = MDIVIEW_MAXIMIZED + 1;
	
	/**
     * The last number in the range of IDs used for <code>MDIView</code> events.
     */
    public static final int INTERNAL_FRAME_LAST = MDIVIEW_CLOSING;
	
	/**
	 * Constructs a <code>MDIViewEvent</code> with given <code>source</code>
	 * and <code>id</code>
	 */	
	public MDIViewEvent(MDIView source, int id){
		super(source, id);
	}
	
	/**
     * Returns a string identifying this event.
     * This method should be useful for event logging and for debugging.
     *
     * @return a string identifying the event type.
     */
	public String paramString() {
        String typeString;
        switch(id) {
        	case MDIViewEvent.MDIVIEW_ACTIVIATED:
        		typeString = "MDIVIEW_ACTIVIATED";
        		break;
        	
        	case MDIViewEvent.MDIVIEW_DEACTIVIATED:
        		typeString = "MDIVIEW_DEACTIVIATED";
        		break;
        		
        	case MDIViewEvent.MDIVIEW_OPENED:
        		typeString = "MDIVIEW_OPENED";
        		break;
        		
        	case MDIViewEvent.MDIVIEW_CLOSED:
        		typeString = "MDIVIEW_CLOSED";
        		break;
        		
        	case MDIViewEvent.MDIVIEW_ICONIFIED:
        		typeString = "MDIVIEW_ICONIFIED";
        		break;
        	
        	case MDIViewEvent.MDIVIEW_RESTORED:
        		typeString = "MDIVIEW_RESTORED";
        		break;
        		
        	case MDIViewEvent.MDIVIEW_MAXIMIZED:
        		typeString = "MDIVIEW_MAXIMIZED";
        		break;
        		
        	case MDIViewEvent.MDIVIEW_CLOSING:
        		typeString = "MDIVIEW_CLOSING";
        		break;
        		
        	default:
        		typeString = "unknown type";        
        }
        return typeString;
	}
}
