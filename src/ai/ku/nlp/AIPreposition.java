package ai.ku.nlp;

public class AIPreposition {

	public enum Preposition { UNKNOWN, IN_FRONT_OF, BEHIND, NEXT_TO, NEAR, ON, IN, BETWEEN, ABOVE, BELOW };
	
	private AIPOS source;
	private AIPOS target;
	private Preposition relation;
	
	public AIPreposition(AIPOS source, AIPOS target,String relation) {
		this.setSource(source);
		this.setTarget(target);
		this.setRelation(getRelation(relation));
	}

	private Preposition getRelation(String rltn) {
		
		if( rltn.equals("in_front_of") )
			return Preposition.IN_FRONT_OF;
		else if( rltn.equals("behind") )
			return Preposition.BEHIND;
		else if( rltn.equals("next_to") || rltn.equals("by") || rltn.equals("beside") )
			return Preposition.NEXT_TO;
		else if( rltn.equals("near") )
			return Preposition.NEAR;
		else if( rltn.equals("on") )
			return Preposition.ON;
		else if( rltn.equals("in") )
			return Preposition.IN;
		else if( rltn.equals("between") )
			return Preposition.BETWEEN;
		else if( rltn.equals("above") )
			return Preposition.ABOVE;
		else if( rltn.equals("below") )
			return Preposition.BELOW;
			
		return Preposition.UNKNOWN;
	}

	public AIPOS getSource() 
	{ return source; }

	public void setSource(AIPOS source) 
	{ this.source = source; }

	public AIPOS getTarget() 
	{ return target; }

	public void setTarget(AIPOS target) 
	{ this.target = target; }

	public Preposition getRelation() 
	{ return relation; }

	public void setRelation(Preposition relation) 
	{ this.relation = relation; }
	
	public String toString() {
		return ""+source.value()+"-"+relation+"-"+target.value();
	}
}