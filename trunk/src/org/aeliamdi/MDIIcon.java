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
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;



/**
 * Sole purpose of this class is to provide a way to provide
 * system menu functionality for TAB view of the MDIFrame.
 * @author Pritam G. Barhate 
 * @author Yvon
 */
public class MDIIcon extends ImageIcon {
	
	/** x-coordinate of icon location where the icon was painted last time */
	private int lastX;
	/** y-coordinate of icon location where the icon was painted last time */
	private int lastY;	
	
	/**
	 * Creates a MDIIcon from specified file.
	 * @param fileName a String specifying a filename or path
	 */
	public MDIIcon(String fileName){
		super(fileName);
	}
	
	/**
     * Creates a MDIIcon from specified url.
     * <p>
     * author Yvon
     * @param url an URL specifying a file url.
     * author Yvon
     */
    public MDIIcon(URL url){
        super(url);
    }
        
	/**
	 * This method saves the <code>x, y</code> paameters into <code>lastX</code>
	 * and <code>lastY</code> member variables. Then using these member variables 
	 * the icon rectangle can be calculated.
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		lastX = x;
		lastY = y;
		super.paintIcon(c,g,x,y);
	}
	
	/**
	 * Returns x-coordinate of icon location where the icon was painted last time 
	 * @return <code>lastX</code>
	 */
	public int getLastX() {
		return lastX;
	}
	/**
	 * y-coordinate of icon location where the icon was painted last time
	 * @return <code>lastY<code> 
	 */
	public int getLastY() {
		return lastY;
	}

}
