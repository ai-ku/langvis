package ai.ku.model;

import javax.vecmath.Vector3d;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Vector3;
import ai.ku.nlp.AIPreposition.Preposition;
import ai.ku.util.Logger;
import ai.ku.util.Randomizer;

public class AIModelSimplePositioner extends AIModelPositionerAbstract{
	
	public static AIModelSimplePositioner getInstance(){
		if( mp == null )
			mp = new AIModelPositionerWithSolver();
		return (AIModelSimplePositioner)mp;
	}


	public void placeOn( AIModel a, AIModel b ) {
		Model A = a.getModel();
		Model B = b.getModel();
		Logger.log("placing on A: "+A.getRepr()+", B: "+B.getRepr());
		A.setOrientationRightNow(B.getOrientationAsForwardAndUpGuide(A.getWorld()),A.getWorld());
		A.placeOnRightNow(B);
		a.setGround(b);
		a.addChild(b);
	}
	
	public void placeIn( AIModel a, AIModel b ) {
		
		Model A = a.getModel();
		Model B = b.getModel();
		Logger.log("placing in A: "+A.getRepr()+", B: "+B.getRepr());
		
		// Vector3 apos = A.getPosition(A.getWorld());
		// Vector3d bpos = B.getBoundingBox(B.getWorld()).getCenter();
		// A.setPositionRightNow(new Vector3d( bpos.x, apos.y, bpos.z ), A.getWorld());
		// A.setOrientationRightNow(B.getOrientationAsForwardAndUpGuide(B.getWorld()), A.getWorld());
		
		// Vector3d apos = a.getPosition();
		// Vector3d bpos = b.getPosition();
		// a.setPosition(new Vector3d( bpos.x, bpos.y+apos.y, bpos.z ));
		// a.setOrientation(b);
		
		// Vector3d ap = a.getCenter();
		Vector3d bp = b.getBottom();
		a.setPosition(new Vector3d( bp.x, bp.y, bp.z ));
		a.setOrientation(b);
		
		// AIVector origin = new AIVector( bpos );
		// AIVector target = new AIVector( bpos.x, apos.y, bpos.z );
		// AIVector diff = target.subtract( origin );
		// A.moveRightNow( diff.x, diff.y, diff.z, A.getWorld() );
	}
	
	public void placeInFrontOf( AIModel a, AIModel b ) {
		Model A = a.getModel();
		Model B = b.getModel();
		
		Matrix44 m1 = A.getLocalTransformation(); // Logger.log(m1);
		Logger.log("placing infront A: "+A.getRepr()+", B: "+B.getRepr());
		Vector3 apos = A.getPosition(A.getWorld());
		Vector3d bpos = b.getBoundingBox().getCenter();
		A.setPositionRightNow( new Vector3d( bpos.x, apos.y, bpos.z ) );
		// A.setOrientationRightNow(B);
		A.setOrientationRightNow(B.getOrientationAsForwardAndUpGuide());
		double amount = (2.0*B.getDepth()+A.getDepth()) / 2;
		A.moveRightNow(Direction.FORWARD, amount);
		
		// Logger.log("A Parent: "+A.getParent());
		// Logger.log("B Parent: "+B.getParent());
		
		Matrix44 m1p = A.getLocalTransformation(); // Logger.log(m1p);
		Matrix44 T = Matrix44.multiply(m1p, Matrix44.invert(m1)); // Logger.log(T);
		for( int i = 0; i < a.getChildCount(); i++ ) {
			Model child = a.getChild(i).getModel();
			Matrix44 m2 = child.getLocalTransformation();
			child.setLocalTransformationRightNow(Matrix44.multiply(T,m2));
		}
		
		a.addChild(b);
	}
	
	public void placeBehind( AIModel a, AIModel b ) {
		Model A = a.getModel();
		Model B = b.getModel();
		
		Matrix44 m1 = A.getLocalTransformation(); // Logger.log(m1);
		Logger.log("placing behind A: "+A.getRepr()+", B: "+B.getRepr());
		Vector3 apos = A.getPosition();
		Vector3 bpos = B.getPosition();
		
		A.setPositionRightNow( new Vector3d( bpos.x, apos.y, bpos.z ) );
		A.setOrientationRightNow(B.getOrientationAsForwardAndUpGuide());
		double amount = (1.5*B.getDepth()+A.getDepth()) / 2;
		A.moveRightNow(Direction.BACKWARD, amount);
		
		Matrix44 m1p = A.getLocalTransformation(); // Logger.log(m1p);
		Matrix44 T = Matrix44.multiply(m1p, Matrix44.invert(m1)); // Logger.log(T);
		for( int i = 0; i < a.getChildCount(); i++ ) {
			Model child = a.getChild(i).getModel();
			Matrix44 m2 = child.getLocalTransformation();
			child.setLocalTransformationRightNow(Matrix44.multiply(T,m2));
		}
		a.addChild(b);
	}
	
	public void placeNextTo( AIModel a, AIModel b ) {
		
		Model A = a.getModel();
		Model B = b.getModel();
		
		Matrix44 m1 = A.getLocalTransformation(); // Logger.log(m1);
		Logger.log("placing next to A: "+A.getRepr()+", B: "+B.getRepr());
		Vector3 apos = A.getPosition();
		Vector3 bpos = B.getPosition();
		A.setPositionRightNow( new Vector3d( bpos.x, apos.y, bpos.z ) );
		// A.setOrientationRightNow( B );
		A.setOrientationRightNow(B.getOrientationAsForwardAndUpGuide());
		double amount = (B.getWidth()+A.getWidth()) / 2;
		if( Randomizer.randomBoolean() )
			A.moveRightNow(Direction.LEFT, amount);
		else
			A.moveRightNow(Direction.RIGHT, amount);
		
		Matrix44 m1p = A.getLocalTransformation(); // Logger.log(m1p);
		Matrix44 T = Matrix44.multiply(m1p, Matrix44.invert(m1)); // Logger.log(T);
		for( int i = 0; i < a.getChildCount(); i++ ) {
			Model child = a.getChild(i).getModel();
			Matrix44 m2 = child.getLocalTransformation();
			child.setLocalTransformationRightNow(Matrix44.multiply(T,m2));
		}
		
		a.addChild(b);
		//A.setPositionRightNow( new Vector3d( bpos.x, apos.y, bpos.z-(B.getDepth()/2+A.getDepth()/2) ) );
	}
	
	public void placeNear( AIModel a, AIModel b ) {
		Model A = a.getModel();
		Model B = b.getModel();
		Logger.log("placing near A: "+A.getRepr()+", B: "+B.getRepr());
		Vector3 apos = A.getPosition(A.getWorld());
		Vector3 bpos = B.getPosition(B.getWorld());
		// double offset = b.getGround().getModel().getPosition(B.getWorld()).y;
		A.setPositionRightNow( new Vector3d( bpos.x, bpos.y + apos.y, bpos.z ), A.getWorld() );
		// A.setOrientationRightNow( B );
		A.setOrientationRightNow(B.getOrientationAsForwardAndUpGuide(A.getWorld()),A.getWorld());
		double amount = (1.5*B.getWidth()+A.getWidth()) / 2;
		if( Randomizer.randomBoolean() )
			A.moveRightNow(Direction.LEFT, amount);
		else
			A.moveRightNow(Direction.RIGHT, amount);
		a.addChild(b);
		//A.setPositionRightNow( new Vector3d( bpos.x, apos.y, bpos.z-(B.getDepth()/2+A.getDepth()/2) ) );
	}
	
	public void placeOnGround(AIModel model) {
		Model ground = (Model)model.getModel().getWorld().getChildAt(5);
		model.getModel().placeOnRightNow(ground);
	}
	
	public void place( AIModel A, AIModel B, Preposition p ) {
		
		Logger.log("placing models");
		if( p == Preposition.IN_FRONT_OF )
			placeInFrontOf(A,B);
		else if( p == Preposition.BEHIND )
			placeBehind(A,B);
		else if( p == Preposition.NEXT_TO )
			placeNextTo(A,B);
		else if( p == Preposition.NEAR )
			placeNear(A,B);
		else if( p == Preposition.ON )
			placeOn(A,B);
		else if( p == Preposition.IN )
			placeIn(A,B);
		else if( p == Preposition.ABOVE )
			placeAbove(A,B);
		else if( p == Preposition.BELOW )
			placeBelow(A,B);
		
	}

	public void placeBelow(AIModel a, AIModel b) {
		Model A = a.getModel();
		Model B = b.getModel();
		Logger.log("placing below A: "+A.getRepr()+", B: "+B.getRepr());
		Vector3 apos = A.getPosition(A.getWorld());
		Vector3 bpos = B.getPosition(B.getWorld());
		A.setPositionRightNow( new Vector3d( bpos.x, apos.y, bpos.z ), A.getWorld() );
		A.setOrientationRightNow(B.getOrientationAsForwardAndUpGuide(A.getWorld()),A.getWorld());
		double amount = (1.5*B.getHeight()+A.getHeight()) / 2;
		A.moveRightNow(Direction.DOWN, amount, A.getWorld());
		a.addChild(b);
	}

	public void placeAbove(AIModel a, AIModel b) {
		
		Model A = a.getModel();
		Model B = b.getModel();
		World w = A.getWorld(); 
		Logger.log("placing above A: "+A.getRepr()+", B: "+B.getRepr());
		Vector3 apos = A.getPosition(w);
		Vector3 bpos = B.getPosition(w);
		A.setPositionRightNow( new Vector3d( bpos.x, apos.y, bpos.z ), w );
		A.setOrientationRightNow(B.getOrientationAsForwardAndUpGuide(w),w);
		double amount = (1.5*B.getHeight()+A.getHeight()) / 2;
		A.moveRightNow(Direction.UP, amount, w);
		a.addChild(b);
	}

}
