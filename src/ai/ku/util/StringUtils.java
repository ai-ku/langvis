package ai.ku.util;

public class StringUtils {

	public String toUpperCaseFirstLetter(String word) {
		char first = Character.toUpperCase(word.charAt(0));
		return ""+first+""+word.substring(1);
	}
	
	public String reverse(String word) {
		
		if( word == null || word.equals("") )
			return word;
		
		String s = "";
		for( char c : word.toCharArray() )
			s = c + s;
			
		return s;
	}
	
}
