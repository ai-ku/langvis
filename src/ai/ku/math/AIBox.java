package ai.ku.math;

import javax.vecmath.Vector3d;

import ai.ku.util.Logger;

import edu.cmu.cs.stage3.math.Box;

public class AIBox extends Box{

	private AIRectangle back;
	private AIRectangle front;
	private AIRectangle left;
	private AIRectangle right;
	private AIRectangle top;
	private AIRectangle bottom;

	public AIBox(Box b) {
		super(b.getMinimum(),b.getMaximum());
		this.initSides( getVectors(b.getCorners()) );
	}

	private void initSides(AIVector[] ps) {
		/*
		front  = new AIRectangle( ps[2], ps[6], ps[4], ps[0] );
		back   = new AIRectangle( ps[3], ps[7], ps[5], ps[1] );
		right  = new AIRectangle( ps[7], ps[5], ps[4], ps[6] );
		left   = new AIRectangle( ps[3], ps[1], ps[0], ps[2] );
		top    = new AIRectangle( ps[3], ps[7], ps[6], ps[2] );
		bottom = new AIRectangle( ps[1], ps[5], ps[4], ps[0] );
		*/
		front  = new AIRectangle( ps[3], ps[7], ps[5], ps[1] );
		back   = new AIRectangle( ps[2], ps[6], ps[4], ps[0] );
		right  = new AIRectangle( ps[3], ps[1], ps[0], ps[2] );
		left   = new AIRectangle( ps[7], ps[5], ps[4], ps[6] );
		top    = new AIRectangle( ps[3], ps[7], ps[6], ps[2] );
		bottom = new AIRectangle( ps[1], ps[5], ps[4], ps[0] );
	}

	private AIVector[] getVectors(Vector3d[] os) {
		AIVector[] vs = new AIVector[os.length];
		for (int i = 0; i < os.length; i++)
			vs[i] = new AIVector(os[i]); // Logger.log(""+vs[i].toString());
		return vs;
	}

	public boolean intersects(AILine line)
	{		
		boolean intersectsFront = front.intersects(line); if(intersectsFront) Logger.log("intersecting front");
		boolean intersectsBack = back.intersects(line); if(intersectsBack) Logger.log("intersecting back");
		boolean intersectsRight = right.intersects(line); if(intersectsRight) Logger.log("intersecting right");
		boolean intersectsLeft = left.intersects(line); if(intersectsLeft) Logger.log("intersecting left");
		boolean intersectsTop = top.intersects(line); if(intersectsTop) Logger.log("intersecting top"); 
		boolean intersectsBottom = bottom.intersects(line); if(intersectsBottom) Logger.log("intersecting bottom");

		return ( intersectsFront || intersectsBack || intersectsRight || intersectsLeft || intersectsTop || intersectsBottom );
	}

	public boolean intersects(AIBox other) 
	{
		Vector3d min = this.getMinimum();
		Vector3d max = this.getMaximum();
		Vector3d otherMin = other.getMinimum();
		Vector3d otherMax = other.getMaximum();

		return 
				(min.x <= otherMax.x) && (max.x >= otherMin.x) &&
				(min.y <= otherMax.y) && (max.y >= otherMin.y) &&
				(min.z <= otherMax.z) && (max.z >= otherMin.z);
		/*
	    	(min.x < other.max.x) && (max.x > other.min.x) &&
	        (min.y < other.max.y) && (max.y > other.min.y) &&
	        (min.z < other.max.z) && (max.z > other.min.z);
		 */
	}

	public boolean isInside(AIBox other) {
		Vector3d min = this.getMinimum();
		Vector3d max = this.getMaximum();
		Vector3d otherMin = other.getMinimum();
		Vector3d otherMax = other.getMaximum();
		return 
				(max.x <= otherMax.x) && (min.x >= otherMin.x) &&
				(max.y-this.getHeight()/100 <= otherMax.y) && ((min.y+this.getHeight()/100) >= otherMin.y) &&
				(max.z <= otherMax.z) && (min.z >= otherMin.z);
	}

	public double getVolume() {
		return ( this.getWidth() * this.getHeight() * this.getDepth() );
	}

	public static AIBox max(AIBox a, AIBox b) {	
		return ( a.getVolume() > b.getVolume() ) ? a : b;
	}

	public static AIBox min(AIBox a, AIBox b) {	
		return ( a.getVolume() > b.getVolume() ) ? b : a;
	}

	public AIRectangle getSurfaceFront() { return front; }
	public AIRectangle getSurfaceBack() { return back; }
	public AIRectangle getSurfaceLeft() { return left; }
	public AIRectangle getSurfaceRight() { return right; }
	public AIRectangle getSurfaceTop() { return top; }
	public AIRectangle getSurfaceBottom() { return bottom; }

}
