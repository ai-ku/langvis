package ai.ku.component;

public class AITextArea extends javax.swing.JTextArea{

	private static final long serialVersionUID = 1L;

	public AITextArea()
	{
		this.setForeground( java.awt.Color.black );
		this.setBackground( ai.ku.util.Palette.gray );
		this.setFont( new java.awt.Font( "Calibri", java.awt.Font.PLAIN, 16 ) );
		this.setEditable( false );
	}
}
