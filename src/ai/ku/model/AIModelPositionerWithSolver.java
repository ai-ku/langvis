package ai.ku.model;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import EDU.Washington.grad.gjb.cassowary.ClVariable;
import EDU.Washington.grad.gjb.cassowary.ExCLError;
import ai.ku.constraint.AIConstraintRectangle;
import ai.ku.constraint.AIConstraintRules;
import ai.ku.constraint.AIConstraintSolver;
import ai.ku.nlp.AIPreposition.Preposition;
import ai.ku.util.Logger;
import ai.ku.util.Randomizer;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Vector3;

public class AIModelPositionerWithSolver extends AIModelPositionerAbstract {

	protected static AIConstraintSolver cs = null;
	
	public static AIModelPositionerWithSolver getInstance(){
		if( mp == null )
		{
			mp = new AIModelPositionerWithSolver();
			cs = new AIConstraintSolver();
		}
		return (AIModelPositionerWithSolver)mp;
	}
	
	public void placeOn( AIModel a, AIModel b ) {
		Model A = a.getModel();
		Model B = b.getModel();
		Logger.log("placing on A: "+A.getRepr()+", B: "+B.getRepr());
		a.setOrientation(b);
		A.placeOnRightNow(B);
		a.setGround(b);
		
		AIConstraintRules rulesOfA = a.getRules();
		
		rulesOfA.ignoreX = false;
		rulesOfA.ignoreY = false;
		rulesOfA.isXDynamic = true;
		rulesOfA.isYDynamic = true;
		rulesOfA.isDepthDynamic = false;
		rulesOfA.isWidthDynamic = false;
		
		AIConstraintRules rulesOfB = b.getRules();
		
		rulesOfB.ignoreX = false;
		rulesOfB.ignoreY = false;
		rulesOfB.isXDynamic = true;
		rulesOfB.isYDynamic = true;
		rulesOfB.isDepthDynamic = true;
		rulesOfB.isWidthDynamic = true;
		
		cs.addObjectOnORInToAnotherOne(a, b, Preposition.ON, false);
	}
	
	public void placeIn( AIModel a, AIModel b ) {
		Vector3d ap = a.getBottom();
		Vector3d bp = b.getBottom();
		a.move(new Vector3d( bp.x - ap.x, bp.y - ap.y, bp.z - ap.z ));
		a.setOrientation(b);
		a.setGround(b.getGround());
		
		AIConstraintRules rulesOfA = a.getRules();
		
		rulesOfA.ignoreX = false;
		rulesOfA.ignoreY = false;
		rulesOfA.isXDynamic = true;
		rulesOfA.isYDynamic = true;
		rulesOfA.isDepthDynamic = true;
		rulesOfA.isWidthDynamic = true;
		
		AIConstraintRules rulesOfB = b.getRules();
		
		rulesOfB.ignoreX = false;
		rulesOfB.ignoreY = false;
		rulesOfB.isXDynamic = true;
		rulesOfB.isYDynamic = true;
		rulesOfB.isDepthDynamic = true;
		rulesOfB.isWidthDynamic = true;
		
		cs.addObjectOnORInToAnotherOne(a, b, Preposition.IN, false);

	}
	
	public void placeInFrontOf( AIModel a, AIModel b ) {
		AIModel ground = b.getGround();
		a.getModel().placeOnRightNow(ground.getModel());
		a.setOrientation(b);
		a.setGround(ground);
		Vector3d center = a.getBottom();
		Box box = a.getBoundingBox();
		a.initializeRectangle(center.x, center.z, box.getWidth(), box.getDepth());
		
		AIConstraintRules rulesOfA = a.getRules();
		
		rulesOfA.ignoreX = false;
		rulesOfA.ignoreY = false;
		rulesOfA.isXDynamic = true;
		rulesOfA.isYDynamic = true;
		rulesOfA.isDepthDynamic = true;
		rulesOfA.isWidthDynamic = true;
		
		AIConstraintRules rulesOfB = b.getRules();
		
		rulesOfB.ignoreX = true;
		rulesOfB.ignoreY = false;
		rulesOfB.isXDynamic = true;
		rulesOfB.isYDynamic = true;
		rulesOfB.isDepthDynamic = true;
		rulesOfB.isWidthDynamic = true;
		
		cs.addObjectInFrontOfAnotherOne(a, b);
	}
	
	public void placeBehind( AIModel a, AIModel b ) {
		Model ground = (Model)b.getModel().getWorld().getChildAt(5);
		a.getModel().placeOnRightNow(ground);
		a.setOrientation(b);
		a.setGround(b.getGround());
		Vector3d center = a.getBottom();
		Box box = a.getBoundingBox();
		a.initializeRectangle(center.x, center.z, box.getWidth(), box.getDepth());
		
		AIConstraintRules rulesOfA = a.getRules();
		
		rulesOfA.ignoreX = false;
		rulesOfA.ignoreY = false;
		rulesOfA.isXDynamic = true;
		rulesOfA.isYDynamic = true;
		rulesOfA.isDepthDynamic = true;
		rulesOfA.isWidthDynamic = true;
		
		AIConstraintRules rulesOfB = b.getRules();
		
		rulesOfB.ignoreX = true;
		rulesOfB.ignoreY = false;
		rulesOfB.isXDynamic = true;
		rulesOfB.isYDynamic = true;
		rulesOfB.isDepthDynamic = true;
		rulesOfB.isWidthDynamic = true;
		
		cs.addObjectBehindAnotherOne(a, b);
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
		// A.setPositionRightNow( new Vector3d( bpos.x, apos.y, bpos.z-(B.getDepth()/2+A.getDepth()/2) ) );
	}
	
	public void placeNear( AIModel a, AIModel b ) {
		
		Model ground = (Model)b.getModel().getWorld().getChildAt(5);
		a.getModel().placeOnRightNow(ground);
		a.setOrientation(b);
		a.setGround(b.getGround());
		
		Vector3d center = a.getBottom();
		Box box = a.getBoundingBox();
		a.initializeRectangle(center.x, center.z, box.getWidth(), box.getDepth());
		
		AIConstraintRules rulesOfA = a.getRules();
		
		rulesOfA.ignoreX = false;
		rulesOfA.ignoreY = false;
		rulesOfA.isXDynamic = true;
		rulesOfA.isYDynamic = true;
		rulesOfA.isDepthDynamic = false;
		rulesOfA.isWidthDynamic = false;
		
		AIConstraintRules rulesOfB = b.getRules();
		
		rulesOfB.ignoreX = false;
		rulesOfB.ignoreY = false;
		rulesOfB.isXDynamic = true;
		rulesOfB.isYDynamic = true;
		rulesOfB.isDepthDynamic = false;
		rulesOfB.isWidthDynamic = false;
		
		cs.addObjectNearAnotherOne(a, b);
	}
	
	public void placeOnGround(AIModel model) {
		Model ground = (Model)model.getModel().getWorld().getChildAt(5);
		model.getModel().placeOnRightNow(ground);
		model.setGround(new AIModel(ground));
		Vector3d center = model.getBottom();
		Box box = model.getBoundingBox();
		model.initializeRectangle(center.x, center.z, box.getWidth(), box.getDepth());
		
		cs.addObject(model);
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
	
	public void solveConstraintAndUpdatePosition(ArrayList<AIModel> models)
	{
		ArrayList<ClVariable> variables = new ArrayList<ClVariable>();
		ArrayList<Double> values = new ArrayList<Double>();
		
		for(AIModel m : models)
		{
			AIConstraintRectangle rect = m.getRectangle();
			variables.add(rect.centerX());
			variables.add(rect.centerY());
			values.add(rect.getXDoubleForm());
			values.add(rect.getYDoubleForm());
		}
		
		try {
			System.out.println("Before solving....\n");
			this.displayForDebug(models);
			cs.resetSolver();
			cs.addAllStay();
			cs.addAllConstraint();
			cs.solveForNewValues(variables, values);
		} catch (ExCLError e) {
			e.printStackTrace();
		}
		
		for(AIModel m : models)
		{
			AIConstraintRectangle rect = m.getRectangle();
			Box box = m.getBoundingBox();
			double scaleX = rect.getWidthDoubleForm() / box.getWidth();
			m.getModel().resizeRightNow(scaleX);
		}
		
		for(AIModel m : models)
		{
			m.getModel().placeOnRightNow(m.getGround().getModel());	
			AIConstraintRectangle rect = m.getRectangle();
			Vector3d bottomCenter = m.getBottom();
			double x = rect.getXDoubleForm() - bottomCenter.x;
			double z = rect.getYDoubleForm() - bottomCenter.z;
			m.move(new Vector3d(x, 0, z));
		}
		
		System.out.println("After solving....\n");
		this.displayForDebug(models);
		
	}
	
	public void displayForDebug(ArrayList<AIModel> models)
	{
		for(AIModel m : models)
		{
			System.out.println(m.getModel().getRepr());
			System.out.println("Center(model): " + m.getCenter().toString());
			System.out.println("Bottom(model): " + m.getBottom().toString());
			System.out.println("Center(ground of model): " + m.getGround().getCenter().toString());
			System.out.println("Bottom(ground of model): " + m.getGround().getBottom().toString());
			AIConstraintRectangle rect = m.getRectangle();
			System.out.println("Center : " + rect.getCenter().toString());
			System.out.println("Width : " + rect.width().toString());
			System.out.println("Depth : " + rect.depth().toString());
			System.out.println("Rate : " + Double.toString(rect.getDepthDoubleForm() / rect.getWidthDoubleForm()));
			System.out.println();
		}
	}
	
	public void resetPositioner()
	{
		mp = null;
		cs = null;
	}
}

