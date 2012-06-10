package org.aeliamdi.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Jennifer Gohlke
 * Date: Dec 21, 2010
 * Time: 8:27:28 PM
 */
public class SmallNumberIcon extends ImageIcon
{
	protected SmallNumberIcon()
	{
		super();
	}

	public SmallNumberIcon( int i )
	{
		this( i, UIManager.getFont("Label.font"), UIManager.getColor("Label.foreground") );
	}

	public SmallNumberIcon( int i, Font font, Color clr )
	{
		super();

		String text = String.valueOf( i );

		BufferedImage img = new BufferedImage( 16, 16, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = (Graphics2D)img.getGraphics();

		g.addRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON ) );
		g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

		//g.setColor( Color.white );
		//g.fillRect( 0, 0, 16, 16 );

		FontMetrics textMetrics = g.getFontMetrics( font );
		g.setFont( font );
		g.setColor( clr );

		int centeredX = ( img.getWidth() / 2 ) - ( textMetrics.stringWidth( text ) / 2 );
		//int centeredY = ( img.getHeight() / 2 ) + ( textMetrics.getHeight() / 2 );
		int centeredY = img.getHeight() - textMetrics.getMaxDescent();

		g.drawString( text, centeredX, centeredY );

		this.setImage( img );
	}
}
