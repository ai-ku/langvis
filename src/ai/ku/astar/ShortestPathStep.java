package ai.ku.astar;

import java.awt.Point;

public class ShortestPathStep {

	private int gScore, hScore;
	private Point pos;
	private ShortestPathStep parent;	
	
	public ShortestPathStep(Point pos) { 
		this.setPosition(pos); 
	}
	
	public boolean equals(Object other) {
		if( other instanceof ShortestPathStep  ) {
			ShortestPathStep otherStep = (ShortestPathStep)other;
			return otherStep.getPosition().equals(this.getPosition());
		}
		return false;
	}
	
	public String toString() {
		return String.format("pos=[%d,%d] g=%d h=%d f=%d", this.getPosition().x,this.getPosition().y, this.getGScore(), this.getHScore(), this.getFScore());
	}
	
	public int getFScore() { return this.gScore + this.hScore; }
	public int getGScore() { return gScore; }
	public int getHScore() { return hScore; }
	
	public void setGScore(int g) { this.gScore = g; }
	public void setHScore(int h) { this.hScore = h; }
	
	public ShortestPathStep getParent() { return parent; }
	public void setParent(ShortestPathStep parent) { this.parent = parent; }

	public Point getPosition() { return pos; }
	public void setPosition(Point pos) { this.pos = pos; }
	
}
