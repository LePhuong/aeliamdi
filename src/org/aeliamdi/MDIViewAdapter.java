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
 * Created on Sep 25, 2004
 *
 * Pritam G. Barhate
 */
package org.aeliamdi;

/**
 * An abstract adapter class for receiving <code>MDIView</code> events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * @author Pritam G. Barhate
 */
public abstract class MDIViewAdapter implements MDIViewListener {

	/**
	 * Invoked when the <code>MDIView</code> is activiated.
	 */
	public void MDIViewActivated( MDIViewEvent e ) {}

	/**
	 * Invoked when the <code>MDIView</code> is deactiviaed.
	 */
	public void MDIViewDeactivated( MDIViewEvent e ) {}

	/**
	 * Invoked when the <code>MDIView</code> is opened.
	 */
	public void MDIViewOpened(MDIViewEvent e) {}

	/**
	 * Invoked when the <code>MDIView</code> is closed.
	 */	
	public void MDIViewClosed(MDIViewEvent e) {}

	/**
	 * Invoked when the <code>MDIView</code> is iconified.
	 */
	public void MDIViewIconified(MDIViewEvent e) {}

	/**
	 * Invoked when the <code>MDIView</code> is restored.
	 */
	public void MDIViewRestored(MDIViewEvent e) {}

	/**
	 * Invoked when the <code>MDIView</code> is maximized.
	 */
	public void MDIViewMaximized(MDIViewEvent e) {}

	/**
	 * Invoked when the <code>MDIView</code> is about to be closed.
	 */
	public void MDIViewClosing(MDIViewEvent e) {}

}
