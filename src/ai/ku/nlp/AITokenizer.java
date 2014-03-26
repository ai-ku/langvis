package ai.ku.nlp;

import java.util.ArrayList;

public class AITokenizer{

	private ArrayList<String> tokens;

	public AITokenizer(String sentence){

		sentence = removePunctuation( sentence.toLowerCase() );
	
		ArrayList<String> multipleWords = WordNetFinder.getInstance().getNounPhrases( sentence );
		for( int i = 0; i < multipleWords.size(); i++ )
		{
			String phrase = multipleWords.get(i);
			String replacement = "/-replacement"+i+"-/";
			sentence = sentence.replace( phrase, replacement );
		}

		String[] result = sentence.split("\\s");
		tokens = new ArrayList<String>();
		int repcount = 0;
		for( int i = 0; i < result.length; i++ )
		{
			if( result[i].equals( "/-replacement"+repcount+"-/" ) )
				result[i] = multipleWords.get( repcount++ );
			tokens.add( result[i] );
		}

		// Logger.log(tokens);
	}

	public ArrayList<String> getTokens()
	{ return tokens; }
	
	private String removePunctuation( String sample ){
		// ! " # $ % & ' ( ) * + , - . / : ; < = > ? @ [ \ ] ^ _ ` { | } ~ 
		char punctuation[] = { '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':',';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~' };
		for( char c : punctuation )
			sample = sample.replace( String.valueOf(c), "" );
		return sample;
	}

	public static void main(String[] args){
		String sampleText = "A young lady, looked at the wise man near the sport car.";	
		new AITokenizer(sampleText);	
	}

}
