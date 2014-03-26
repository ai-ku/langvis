package ai.ku.component;

public class AIButton extends javax.swing.JButton{

	private static final long serialVersionUID = 1L;

	public AIButton( String name ){
		super( name );
		// this.setBorderPainted(false);
		// this.setFocusPainted(false);
		this.setContentAreaFilled(false);
		this.setBackground( ai.ku.util.Palette.gray );
		this.setFont( new java.awt.Font("Calibri", java.awt.Font.BOLD, 14) );
		// this.setFont( new Font( "Calibri", Font.BOLD, 16 ) );
	}
}
