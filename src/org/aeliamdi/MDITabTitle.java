package org.aeliamdi;

import org.aeliamdi.util.LinesBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 6/8/12</p>
 * <p>Time: 7:40 PM</p>
 *
 * @author Jennifer Gohlke
 */
public class MDITabTitle extends JPanel implements PropertyChangeListener, MouseListener
{
	private MDITabbedPane mdiTabbedPane;
	private Component component;
	private JLabel myLabel;
	private JLabel closeLabel;
	private Icon hoverIcon;
	private Icon closeIcon;
	private MDIFrame parentFrame;

	public MDITabTitle( final MDIFrame parentFrame, final MDITabbedPane mdiTabbedPane, String title, Icon icon, final Component component )
	{
		super( new BorderLayout( 0, 1 ) );
		this.parentFrame = parentFrame;
		this.mdiTabbedPane = mdiTabbedPane;
		this.component = component;

		URL closeURL = this.getClass().getResource( "/res/images/tab-close.gif" );
		URL mouseoverURL = this.getClass().getResource( "/res/images/tab-close-mouseover.gif" );
		closeIcon = new ImageIcon( closeURL );
		hoverIcon = new ImageIcon( mouseoverURL );

		mdiTabbedPane.addPropertyChangeListener( this );

		setOpaque( false );

		myLabel = new JLabel( title, icon, SwingConstants.TRAILING );
		myLabel.setLabelFor( component );
		myLabel.setOpaque( false );
		JPanel container = new JPanel( new FlowLayout( FlowLayout.CENTER, 5, 1 ) );
		container.setOpaque( false );
		container.add( myLabel );
		JPanel separator = new JPanel();
		separator.setOpaque( false );
		container.add( separator );
		add( container, BorderLayout.WEST );

		closeLabel = new JLabel( closeIcon, SwingConstants.RIGHT );
		closeLabel.setOpaque( false );
		closeLabel.setVerticalAlignment( SwingConstants.CENTER );
		closeLabel.setIconTextGap( 0 );
		closeLabel.setBorder( new LinesBorder( null, new Insets( 0, 0, 0, 2 ) ) );
		closeLabel.addMouseListener( this );

		add( closeLabel );
	}

	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		if( evt.getPropertyName().equals( "indexForTitle" ) )
		{
			String title = mdiTabbedPane.getTitleAt( Integer.parseInt( String.valueOf( evt.getNewValue() ) ) );
			myLabel.setText( title );
		}
	}

	@Override
	public void mouseClicked( MouseEvent e )
	{

	}

	@Override
	public void mousePressed( MouseEvent e )
	{

	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		for( Object obj : parentFrame.getViews() )
		{
			MDIView view = (MDIView)obj;
			if( view.equals( component ) )
			{
				view.closeView();
				break;
			}
		}
	}

	@Override
	public void mouseEntered( MouseEvent e )
	{
		if( closeIcon.equals( hoverIcon ) )
			return;

		closeLabel.setIcon( hoverIcon );
		closeLabel.revalidate();
	}

	@Override
	public void mouseExited( MouseEvent e )
	{
		if( closeIcon.equals( hoverIcon ) )
			return;

		closeLabel.setIcon( closeIcon );
		closeLabel.revalidate();
	}
}
