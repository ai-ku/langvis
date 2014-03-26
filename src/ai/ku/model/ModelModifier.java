package ai.ku.model;

import ai.ku.util.Scale;
import edu.cmu.cs.stage3.alice.core.Model;

public class ModelModifier {

	private static ModelModifier mm = null;

	// Singleton
	public static ModelModifier getInstance(){
		if( mm == null )
			mm = new ModelModifier();
		return mm;
	}
	
	private ModelModifier(){
		
	}
	
	public void modify(Model model,Scale scale) {
		model.resizeRightNow(scale.x,scale.y,scale.z);
	}
	
	public void modify(Model model,String function) {
		if( function.equals("makeBig") )
		{
			makeBig(model);
		}
		else if( function.equals("makeTall") )
		{
			makeTall(model);
		}
	}
	
	private void makeBig(Model model) {	
		model.resizeRightNow(2);
	}
	
	private void makeTall(Model model) {
		model.resizeRightNow(1, 1.2, 1);
		// model.moveRightNow(Direction.UP, model.getHeight()/2);
	}
}
