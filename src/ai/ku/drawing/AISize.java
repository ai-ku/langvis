package ai.ku.drawing;

public class AISize {

	public double width, height;

	public AISize(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	public String toString() {
		return String.format("[w: %.2f, h: %.2f]", this.width, this.height);
	}
	
}
