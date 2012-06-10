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
 * Created on Oct 7, 2004
 *
 * Pritam G. Barhate
 */
package org.aeliamdi;

import java.awt.AWTEvent;

/**
 * Event class representing MDIFrame events.
 * <p>
 * Currently, there is only one event for MDIFrame,
 * which is viewPaneChanged.
 * @see MDIFrameListener
 * @author Pritam G. Barhate
 *
 */
public class MDIFrameEvent extends AWTEvent {
	/** View pane before the viewPaneChanged event */
	private String oldViewPane;
	/** View pane after the viewPaneChanged event */
	private String newViewPane;
	
	public MDIFrameEvent(MDIFrame source, String oldViewPane, String newViewPane){
		super(source, -1);
		this.oldViewPane = oldViewPane;
		this.newViewPane = newViewPane;
	}
	
	/**
	 * Gives View pane before the viewPaneChanged event
	 * @return View pane after the viewPaneChanged event
	 */
	public String getNewViewPane() {
		return newViewPane;
	}
	
	/**
	 * Gives View pane after the viewPaneChanged event
	 * @return View pane after the viewPaneChanged event
	 */
	public String getOldViewPane() {
		return oldViewPane;
	}
}
