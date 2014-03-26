package ai.ku.component;

public class AITextField extends javax.swing.JTextField{

	private static final long serialVersionUID = 1L;

	public AITextField()
	{
		this.setForeground( java.awt.Color.black );
		this.setBackground( ai.ku.util.Palette.gray );
		// this.setFont( new Font( "Calibri", Font.PLAIN, 14 ) );
		this.setFont( new java.awt.Font("Calibri", java.awt.Font.PLAIN, 14) );
		this.setEditable( true );
	}	
}
