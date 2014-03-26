package ai.ku.drawing;

import java.awt.Point;

public class AIPoint {

	public double x, y;

	public AIPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public AIPoint translate(double dx, double dy) {
		return new AIPoint( x + dx, y + dy );
	}
	
	public Point toPoint() {
		return new Point( (int)x, (int)y );
	}
	
	public String toString() {
		return String.format("[x: %.2f, y: %.2f]", this.x, this.y);
	}
	
}
