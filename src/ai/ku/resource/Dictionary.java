package ai.ku.resource;




import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

public class Dictionary {

	// modfiy this line to address the dictionary path
	private static final String dictionaryPath = "/Users/emre/Desktop/research/marks_story/Dictionary/";
	
	private Hashtable<String, String> nouns;
	private Hashtable<String, String> verbs;
	
	private static Dictionary dictionary = null;
	
	public static Dictionary getInstance(){
		if( dictionary == null )
			dictionary = new Dictionary();
		return dictionary;
	}
	
	private Dictionary(){
		
		String fileName = dictionaryPath + "Dictionary.xml";
		File file = new File( fileName );
		
		DictionaryReader reader = new DictionaryReader( file );
		nouns = reader.getElements("Noun");
		verbs = reader.getElements("Verbs");
		
		printNouns();
		printVerbs();
		
		/*
		while( nouns.keys().hasMoreElements() )
		{
			String key = nouns.keys().nextElement();
			String value = nouns.get(key);
			System.out.println("Key: "+key+", Value: "+value);
		}
		*/
	}
	
	public String getNounFileName( String noun ){
		String fileName = dictionaryPath + nouns.get( noun );
		return fileName;
	}
	
	private void printNouns() { 
		System.out.println("Printing nouns...");
        @SuppressWarnings("rawtypes")
		Enumeration enm = nouns.keys (); 
        while( enm.hasMoreElements () ) { 
            String key = (String) enm.nextElement (); 
            String value = (String) nouns.get( key );
            System.out.println ("{ " + key + ", " + value + " }"); 
        } 
        System.out.println("Done...");
    } 
	
	private void printVerbs() { 
		System.out.println("Printing verbs...");
        @SuppressWarnings("rawtypes")
		Enumeration enm = verbs.keys (); 
        while( enm.hasMoreElements () ) { 
            String key = (String) enm.nextElement (); 
            String value = (String) verbs.get( key );
            System.out.println ("{ " + key + ", " + value + " }"); 
        } 
    	System.out.println("Done...");
    }
}
