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


import javax.swing.*;
import java.awt.*;

/**
 * Sole purpose to extend this class from <code>JTabbedPane</code> is to 
 * to facilitate the correct inner workings of the MDIFramework. For most of the 
 * common uses of the MDIFramework, this class is not required to be dalt with
 * directly. Almost all of the functionality of the framework is 
 * provided through the <code>MDIFrame</code> and <code>MDIView</code>
 * classes.
 * @author Pritam G. Barhate
 */
public class MDITabbedPane extends JTabbedPane{
	private MDIFrame parentFrame;
	
	/**
	 * Constructs the tabbed pane with specified parent.
	 * @param parent The <code>MDIFrame</code> to which this tabbed pane
	 * 		  will be added to.
	 */
	public MDITabbedPane(MDIFrame parent){
		super();	
		parentFrame = parent;
	}

	@Override
	public void insertTab( String title, Icon icon, Component component, String tip, int index )
	{
		super.insertTab( title, icon, component, tip, index );

		if( !parentFrame.isTabCloseButtonEnabled() )
			return;

		MDITabTitle newComponent = new MDITabTitle( parentFrame, this, title, icon, component );
		setTabComponentAt( indexOfComponent( component ), newComponent );

		/*SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				Component tabComponent = getTabComponentAt( getTabCount() - 1 );

				System.out.println( tabComponent );
			}
		} );*/
	}

	/**
	 * Regular paint method from <code>Component</code> class.
	 */
	public void paint(Graphics g){
		if(getTabCount() == 0){ 
			// if there are no tabs in tabbed pane, draw a fillRect for
			// entire area of tabbed pane so as to reflect the background
			// that of the desktop pane which is used along with this tabbed
			// pane in MDIFrame class.
			g.setColor(parentFrame.getDektopPane().getBackground());
			g.fillRect(0,0, getWidth(), getHeight());			
		}else{
			super.paint(g);
		}		
	}	
	/**
	 * Gives the parent <code>MDIframe</code> of this tabbed pane.
	 * @return Returns the parentFrame.
	 */
	public MDIFrame getParentFrame() {
		return parentFrame;
	}
}