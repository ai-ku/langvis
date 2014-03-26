package cmu.component;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ComponentImageFactory extends javax.swing.JPanel {

	private static final long serialVersionUID = 6844687358220062214L;

	public ComponentImageFactory() {
		setPreferredSize( new Dimension( 0, 0 ) );
	}

	public java.awt.image.BufferedImage manufactureImage( javax.swing.JComponent component ) {
		boolean isShowing = component.isShowing();
		java.awt.Container parent = component.getParent();
		
		if( !isShowing ) 
		{
			if( parent != null ) 
			{ parent.remove( component ); }
			add( component );
		}
		doLayout();
		Dimension d = component.getPreferredSize();
		BufferedImage image = new java.awt.image.BufferedImage( d.width, d.height, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = image.createGraphics();
		component.paintAll( g );
 
		if( !isShowing )  
		{
			remove( component );
			if( parent != null ) 
			{ parent.add( component ); }
		}
		return image;
	}
}