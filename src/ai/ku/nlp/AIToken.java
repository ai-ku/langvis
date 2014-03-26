package ai.ku.nlp;

public class AIToken {

	private String phrase;
	private String tag;
	
	public AIToken(String phrase, String tag){
		this.setPhrase(phrase);
		this.setTag(tag);
	}

	public void setPhrase(String phrase) 
	{ this.phrase = phrase; }

	public String getPhrase() 
	{ return phrase; }

	public void setTag(String tag) 
	{ this.tag = tag; }

	public String getTag() 
	{ return tag; }
	
}
