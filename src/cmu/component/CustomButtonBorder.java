package cmu.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class CustomButtonBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 2687031700986069369L;
	
	private Insets insets = new Insets( 3, 3, 3, 3 );
	private Border line = BorderFactory.createLineBorder( Color.black, 1 );
	private Border spacer = BorderFactory.createEmptyBorder( 2, 4, 2, 4 );
	private Border raisedBevel = BorderFactory.createBevelBorder( BevelBorder.RAISED );
	private Border loweredBevel = BorderFactory.createBevelBorder( BevelBorder.LOWERED );
	private Border raisedBorder = BorderFactory.createCompoundBorder( BorderFactory.createCompoundBorder( line, raisedBevel ), spacer );
	private Border loweredBorder = BorderFactory.createCompoundBorder( BorderFactory.createCompoundBorder( line, loweredBevel ), spacer );
	//	private Border raisedBorder = javax.swing.BorderFactory.createCompoundBorder( raisedBevel, spacer );
	//	private Border loweredBorder = javax.swing.BorderFactory.createCompoundBorder( loweredBevel, spacer );

	public void paintBorder( Component c, Graphics g, int x, int y, int w, int h ) {
		JButton button = (JButton) c;
		ButtonModel model = button.getModel();

		if ( model.isEnabled() ) {
			if ( model.isPressed() && model.isArmed() ) 
			{ loweredBorder.paintBorder( button, g, x, y, w, h ); } 
			else 
			{ raisedBorder.paintBorder( button, g, x, y, w, h ); }
		} 
		else 
		{ raisedBorder.paintBorder( button, g, x, y, w, h ); }
	}

	public Insets getBorderInsets( Component c ) { 
		return insets; 
	}
	
}