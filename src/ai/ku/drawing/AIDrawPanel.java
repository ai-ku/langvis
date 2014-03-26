package ai.ku.drawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class AIDrawPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Drawable>drawables;
	
	public AIDrawPanel() {
		this.drawables = new ArrayList<Drawable>();
	}
	
	public void paintComponent( Graphics gg ){

		Graphics2D g = (Graphics2D) gg; 
		
		g.setBackground(Color.gray);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		// g.scale(1.5, 1.5);
		
		for ( Drawable drawable : drawables ) {
			if( drawable instanceof AIRect ) {
				AIRect r = (AIRect)drawable;
				r.draw(g);
			}
		}
	}

	public void refresh()
	{ this.repaint(); }
	
	public void addDrawable(Drawable d) {
		this.drawables.add(d);
	}
}
