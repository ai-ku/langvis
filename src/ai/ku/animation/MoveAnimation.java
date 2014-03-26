package ai.ku.animation;

import java.awt.Point;
import java.util.ArrayList;

import javax.vecmath.Vector3d;

import ai.ku.astar.ShortestPathStep;
import ai.ku.drawing.AIPoint;
import ai.ku.drawing.AISize;
import ai.ku.model.AIModel;
import ai.ku.model.AIModel2D;
import ai.ku.util.Logger;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.response.MoveVectorAnimation;
import edu.cmu.cs.stage3.math.Vector3;

public class MoveAnimation implements Runnable {

	private ArrayList<ShortestPathStep> path;
	private AIModel model;
	private AISize tileSize;
	private AuthoringTool tool;
	
	public MoveAnimation(AIModel model,ArrayList<ShortestPathStep>path,AISize tileSize, AuthoringTool tool) {
		this.model = model;
		this.path = path;
		this.tileSize = tileSize;
		this.tool = tool;
	}
	
	public void run() {
		
		AIModel2D model2D = new AIModel2D(model);
		AIPoint s = model2D.tileRectForSize(tileSize).origin;
		Point prev = s.toPoint();
		
		for ( ShortestPathStep step : path ) {
			
			Point pos = step.getPosition();
			Point diff = new Point( pos.x - prev.x, pos.y - prev.y );
			Logger.log("Diff: "+diff.toString());
			
			AIModel ground = model.getGround();
			
			// AIModel2D ground2D = new AIModel2D(model.getGround());
			// AIPoint o = ground2D.topLeft();
			
			// Should calculate ground position in 3D space
			// AIPoint tp = new AIPoint( o.x + (pos.x * tileSize.width), o.y + (pos.y * tileSize.height ) );
			// AIPoint tp = new AIPoint( ground.position().x - ground.width()/2 + (pos.x * tileSize.width), ground.position().z - ground.depth()/2 + (pos.y * tileSize.height ) );
			// Vector3 v = new Vector3(tp.x, model.position().y ,tp.y);
			// modelA.setPosition(v);
			
			double amount = tileSize.width;
			Direction direction = Direction.LEFT;
			
			/*
			double amountY = 0;
			double amountX = 0;
			
			if( diff.y == -1 ) {
				direction = Direction.BACKWARD;
				amountY = amount;
			}
			
			if( diff.y == 1 ) {
				direction = Direction.FORWARD;
				amountY = -amount;
			}
			
			if( diff.x == -1 ) {
				direction = Direction.RIGHT;
				amountX = -amount;
			}
				
			if( diff.x == 1 ) {
				direction = Direction.LEFT;
				amountX = amount;
			}
			*/
			// model.getModel().moveRightNow( direction, amount );
			
			/*
			MoveTowardAnimation moveTo = new MoveTowardAnimation();
			moveTo.duration.set(10.0);
			moveTo.amount.set( modelA.distance(modelB) );
			moveTo.subject.set( modelA.getModel() );
			moveTo.target.set( modelB.getModel() );
			tool.performOneShot(moveTo, moveTo, modelA.getModel().getProperties());
			*/
			
			edu.cmu.cs.stage3.alice.core.response.MoveAnimation move = new edu.cmu.cs.stage3.alice.core.response.MoveAnimation();
			move.direction.set(direction);
			move.amount.set(amount);
			move.subject.set(model.getModel());
			move.isScaledBySize.set(false);
			move.duration.set(1);
			// Vector3d c = model.getPosition();
			// Vector3 v = new Vector3( c.x + amountX, c.y, c.z + amountY );
			// MoveVectorAnimation animation = new MoveVectorAnimation();
			// animation.subject.set(model.getModel());
			// animation.vector.set(v);
			tool.performOneShot(move, move, model.getModel().getProperties());
			
			
			prev = pos;
		}
		Logger.log("ended run");
	}

}
