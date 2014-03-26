package ai.ku.nlp;

import java.util.ArrayList;

public class AINoun extends AIPOS {

	private ArrayList<AIAdjective> modifiers;
	private String countString;
	private String stem;
	private String determiner;
	
	public AINoun(String value) {
		super(value);		
	}
	
	public AINoun(String value,String tag,int index) {
		super(value,tag,index);
	}
	
	public ArrayList<AIAdjective> getModifiers() {
		if( modifiers == null ) modifiers = new ArrayList<AIAdjective>();
		return modifiers;
	}
	
	public void setCount(String count){
		countString = count.toLowerCase();
	}
	
	public String getCount() {
		return countString;
	}
	
	public void setStem(String stem) {
		this.stem = stem;
	}
	
	public String getStem() {
		return stem;
	}
	
	public void addModifier(AIAdjective adj) 
	{ this.getModifiers().add(adj); }
	
	public void removeModifier(AIAdjective adj) 
	{ this.getModifiers().remove(adj); }
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		
		buff.append("- "+this.value()+"\n");
		buff.append("-- index: "+this.index()+"\n");
		buff.append("-- stem: "+this.getStem()+"\n");
		buff.append("-- det: "+this.getDeterminer()+"\n");
		buff.append("-- count: "+this.getCount()+"\n");
		buff.append("-- adjs: ");
		for( AIAdjective adj : this.getModifiers() )
			buff.append( adj.value()+", " );
			
		return buff.toString();
	}

	public String getDeterminer() {
		return determiner;
	}

	public void setDeterminer(String determiner) {
		this.determiner = determiner;
	}
}
