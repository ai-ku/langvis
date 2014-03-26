package ai.ku.constraint;

import ai.ku.model.AIModel;
import ai.ku.nlp.AIPreposition.Preposition;

public class AIRelation {
	private Preposition type;
	private AIModel source;
	private AIModel target;
	
	public AIRelation(AIModel source, AIModel target,Preposition p)
	{
		this.type = p;
		this.source = source;
		this.target = target;
	}
	
	public Preposition getType() {
		return type;
	}
	public void setType(Preposition type) {
		this.type = type;
	}
	
	public AIModel getSource() {
		return source;
	}
	public void setSource(AIModel source) {
		this.source = source;
	}
	public AIModel getTarget() {
		return target;
	}
	public void setTarget(AIModel target) {
		this.target = target;
	}

}
