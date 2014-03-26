package ai.ku.drawing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class AIDrawFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int width = 512;
	private static final int height = 512;

	private Toolkit t = Toolkit.getDefaultToolkit();
		
	private AIDrawPanel drawPanel;
	
	public AIDrawFrame(){	
		
		Dimension ss = t.getScreenSize();

		this.setLocation( (int)( ss.getWidth() - width )/2, (int)( ss.getHeight() - height )/2 );
		this.setTitle( "Bird's Eye" );
		this.setSize( width, height );
		this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		this.setResizable( false );	
		
		this.drawPanel = new AIDrawPanel();
		this.add( drawPanel, BorderLayout.CENTER );
	}

	public AIDrawPanel getDrawPanel() {
		return this.drawPanel;
	}
	
	public void addDrawable(Drawable d) {
		this.drawPanel.addDrawable(d);
	}
}
