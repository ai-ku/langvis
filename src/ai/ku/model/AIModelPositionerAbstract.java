package ai.ku.model;
import ai.ku.nlp.AIPreposition.Preposition;

public abstract class AIModelPositionerAbstract {
	protected static AIModelPositionerAbstract mp = null;
	
	protected AIModelPositionerAbstract(){	}
	
	public void placeOn( AIModel a, AIModel b ) { }
	
	public void placeIn( AIModel a, AIModel b ) { }
	
	public void placeInFrontOf( AIModel a, AIModel b ) { }
	
	public void placeBehind( AIModel a, AIModel b ) { }
	
	public void placeNextTo( AIModel a, AIModel b ) { }
	
	public void placeNear( AIModel a, AIModel b ) { }
	
	public void placeOnGround(AIModel model) { }
	
	public void place( AIModel A, AIModel B, Preposition p ) {	}

	public void placeBelow(AIModel a, AIModel b) {}

	public void placeAbove(AIModel a, AIModel b) { }

}
