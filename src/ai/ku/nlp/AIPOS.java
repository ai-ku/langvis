package ai.ku.nlp;

public class AIPOS {

	private String tag = "none";
	private String value = "none";
	private int index = -1;

	public AIPOS(String value) 
	{ this.setValue(value); }
	
	public AIPOS(String value,String tag,int index) {
		this.setValue(value);
		this.setTag(tag);
		this.setIndex(index);
	}
	
	public String value() 
	{ return value; }

	public void setValue(String value)
	{ this.value = value; }

	public int index() 
	{ return index; }

	public void setIndex(int index) 
	{ this.index = index; }

	public String tag() 
	{ return tag; }

	public void setTag(String tag) 
	{ this.tag = tag; }

}
