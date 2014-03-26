package ai.ku.reasoning;

import java.util.ArrayList;

import ai.ku.math.AIBox;
import ai.ku.math.AILine;
import ai.ku.math.AIVector;
import ai.ku.model.AIModel;
import ai.ku.util.Logger;
import edu.cmu.cs.stage3.alice.core.Model;

public class Oracle {
	
	public static boolean canSee(Model A,Model B, ArrayList<AIModel> models) {
		
		if( !B.isInFrontOf(A) )
			return false;
		
		AIVector centerA = new AIVector(A.getBoundingBox(A.getWorld()).getCenter());
		AIVector centerB = new AIVector(B.getBoundingBox(B.getWorld()).getCenter());
		AILine line = new AILine(centerA,centerB);
		
		for( AIModel m : models )
		{ 
			Model other = m.getModel();
			if( other.equals(A) || other.equals(B) )
				continue;
			
			if( isInside(other,A) || isInside(other,B) )
				continue;
			
			/*
			if( isInside(A,other) && !isInside(B,other) )
				return false;
			
			if( isInside(B,other) && !isInside(A,other) )
				return false;
			*/	
			
			AIBox box = new AIBox(other.getBoundingBox(other.getWorld()));
			if( box.intersects(line) )
				return false;
			
			Logger.log("testing model : "+other.getRepr());
		}
			
		return true;
	}
	
	public static boolean isInside(Model A, Model B) {	
		AIBox boxA = new AIBox(A.getBoundingBox(A.getWorld()));
		AIBox boxB = new AIBox(B.getBoundingBox(B.getWorld()));
		return boxA.isInside(boxB);
	}
	
	public static boolean intersects(Model A, Model B) {	
		AIBox boxA = new AIBox(A.getBoundingBox(A.getWorld()));
		AIBox boxB = new AIBox(B.getBoundingBox(B.getWorld()));
		return boxA.intersects(boxB);
	}
}