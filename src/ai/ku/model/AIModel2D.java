package ai.ku.model;

import ai.ku.drawing.AIPoint;
import ai.ku.drawing.AIRect;
import ai.ku.drawing.AISize;
import ai.ku.util.Logger;

public class AIModel2D {

	private AIModel model;
	private AISize size;
	private AIPoint position;
	
	public AIModel2D(AIModel model) {
		this.model = model;
		
		// This class assumes position of model won't change during usage of this class
		this.position = new AIPoint( this.model.position().x, this.model.position().z );
		
		// This class assumes size of model won't change during usage of this class
		// Treats model as a square for simplicity
		double d = Math.max(this.model.width(), this.model.depth());
		this.size = new AISize(d,d);
	}
	
	public void setOrigin(AIPoint origin) { this.setPosition(this.position().translate( -origin.x, -origin.y)); }
	public void setPosition(AIPoint pos) { position = pos; }
	public AIPoint position() { return position; }
	public AIPoint topLeft() { return this.position().translate( -this.width()/2, -this.height()/2 ); }
	public AIPoint topRight() { return this.position().translate( this.width()/2, -this.height()/2 ); }
	public AIPoint bottomRight() { return this.position().translate( this.width()/2, this.height()/2 ); }
	public AIPoint bottomLeft() { return this.position().translate( -this.width()/2, this.height()/2 ); }
	
	public double width() { return size.width; }
	public double height() { return size.height; }
	public AISize size() { return size; }
	
	// Unused for now, setOrigin is used instead
	public AIPoint positionOnGround(AIModel2D ground) {
		AIPoint o = ground.topLeft();
		return this.position().translate(-o.x, -o.y); 
	}
	
	public AIRect tileRectForSize(AISize tile) {
		
		double tox = this.position().x / tile.width; // center in tiles
		double toy = this.position().y / tile.height; // center in tiles
		double tw = this.width() / tile.width; // width in tiles
		double th = this.height() / tile.height; // height in tiles
		
		int x = (int) Math.round( tox - tw/2 );
		int y = (int) Math.round( toy - th/2 );
		int w = (int) Math.round(tw);
		int h = (int) Math.round(th);
		
		return new AIRect( x, y, w, h );
	}
	
	public void printInfo() {
		Logger.log("name: "+this.model.name());
		Logger.log("size: "+this.size());
		Logger.log("center: "+this.position());
		Logger.log("top-left: "+topLeft());
		Logger.log("top-right: "+topRight());
		Logger.log("bottom-right: "+bottomRight());
		Logger.log("bottom-left: "+bottomLeft());
		Logger.log("---name: "+this.model.name());
	}
}
