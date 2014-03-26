package ai.ku.drawing;

import java.awt.Color;
import java.awt.Graphics2D;

public class AIRect implements Drawable{
	
	public AIPoint origin;
	public AISize size;
	private double rotation;
	private Color color;
	
	public AIRect(AIPoint origin, AISize size) {
		this.size = size;
		this.origin = origin;
		this.setRotation(0.0f);
		this.setColor(Color.red);
	}
	
	public AIRect(double x, double y, double width, double height) {
		this(new AIPoint(x,y),new AISize(width,height));
	}

	public AIRect(AISize size) {
		this(new AIPoint(0,0),size);
	}
	
	public AIRect(double width, double height) {
		this(new AISize(width,height));
	}

	public String toString() {
		AIPoint o = origin;
		AISize s = size;
		return "x: " + o.x + ", y: " + o.y + ", w: " + s.width + ", h: " + s.height + "\n"; 
	}
	
	public void draw(Graphics2D g) {
		AIPoint o = origin;
		AISize s = size;
		g.setColor(this.getColor());
		g.fillRect( (int)o.x, (int)o.y, (int)s.width, (int)s.height );
	}
	
	// public AIPoint getOrigin() { return origin; }
	// public void setOrigin(AIPoint origin) { this.origin = origin; };
	
	public AIPoint getCenter() { return new AIPoint( origin.x + size.width/2, origin.y + size.height/2 ); }
	public void setCenter(AIPoint center) { this.origin = new AIPoint( center.x - size.width/2, center.y - size.height/2 ); }
	
	// public AISize getSize() { return size; }
	// public void setSize(AISize size) { this.size = size; };
	
	public double getWidth() { return size.width; }
	public double getHeight() { return size.height; }

	public double getRotation() { return rotation; }
	public void setRotation(double rotation) { this.rotation = rotation; }

	public Color getColor() { return color; }
	public void setColor(Color color) { this.color = color; }
	
}
