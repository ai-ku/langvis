package ai.ku.model;

import java.util.ArrayList;

public class AIModelGroup {

	private ArrayList<AIModel> aimodels = null; 
	
	public AIModelGroup() {
		new ArrayList<AIModel>();
	}

	public void addModel(AIModel amodel) 
	{ aimodels.add(amodel); }
	
}
