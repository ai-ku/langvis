package ai.ku.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTagger {

	@SuppressWarnings("rawtypes")
	private HashMap tagset = new HashMap();
	
	private MaxentTagger tagger;
	private String trainedModelPath = "taggers/left3words-wsj-0-18.tagger";
	private ArrayList<TaggedWord> tagged;
	
	
	public POSTagger(){
		
		// Initialize Tagger
		try 
		{ tagger = new MaxentTagger( trainedModelPath ); } 
		catch (IOException e) 
		{ e.printStackTrace(); } 
		catch (ClassNotFoundException e) 
		{ e.printStackTrace(); }
		
		// Initialize TagSet
		initTagSet();
	}
	
	public String tagString( String sample ){
		String clean = this.removePunctuation( sample );
		
		ArrayList<String> words = this.converToWordList( clean );
		tagged = tagger.apply( ( Sentence.toWordList(words) ) );
		String result = this.convertToDisplableFormat( tagged );
		return result;
	}
	
	public ArrayList<TaggedWord> getTagged(){
		return tagged;
	}
	
	private String convertToDisplableFormat( ArrayList<TaggedWord> tagged ){
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(" Number of Words: "+tagged.size()+"\n");
		
		for( int i = 0; i < tagged.size(); i++ )
		{
			TaggedWord tw = tagged.get(i);
			String line = String.format(" %s - %s - %s\n", tw.word(), tw.tag(), tagset.get(tw.tag()) );
			buffer.append( line );
		}
		
		return buffer.toString();
	}
	
	private ArrayList<String> converToWordList(String sentence){
		
		StringTokenizer tokenizer = new StringTokenizer( sentence );
		ArrayList<String> words = new ArrayList<String>();
		while( tokenizer.hasMoreTokens() )
			words.add( tokenizer.nextToken() );
		
		/*
		ArrayList<TaggedWord> tagged = tagger.apply((Sentence.toWordList(words)));

		System.out.println("Number of Words: "+tagged.size());
		for( int i = 0; i < tagged.size(); i++ )
		{
			TaggedWord tw = tagged.get(i);
			System.out.printf("%s - %s - %s\n", tw.word(), tw.tag(), tagset.get(tw.tag()) );
		}
		*/
		
		return words;
	}
	
	public void removeDeterminers(ArrayList<TaggedWord> tagged){
		ArrayList<TaggedWord> removeList = new ArrayList<TaggedWord>();
		for( int i = 0; i < tagged.size(); i++ )
			if( tagged.get(i).tag().contains("DT") )
				removeList.add( tagged.get(i) );
		
		for( int i = 0; i < removeList.size(); i++ )
			tagged.remove( removeList.get(i) );		
	}
	
	private String removePunctuation( String sample ){
		String clean = null;
		clean = sample.replace( ".", "" );
		clean = clean.replace( ",", "" );
		clean = clean.replace( ";", "" );
		clean = clean.replace( ":", "" );
		return clean;
	}
	
	@SuppressWarnings("unchecked")
	private void initTagSet(){
		tagset.put( "CC", "Coordinating conjunction" );
		tagset.put( "CD", "Cardinal Number" );
		tagset.put( "DT", "Determiner" );
		tagset.put( "IN", "Preposition or subordinating conjunction" );
		tagset.put( "JJ", "Adjective" );
		tagset.put( "JJR", "Adjective, comparative" );
		tagset.put( "JJR", "Adjective, superlative" );
		tagset.put( "NN", "Noun, singular or mass" );
		tagset.put( "NNP", "Proper Noun, singular" );
		tagset.put( "NNPS", "Proper Noun, plural" );
		tagset.put( "NNS", "Noun, plural" );
		tagset.put( "PRP", "Personal pronoun" );
		tagset.put( "PRP$", "Possessive Pronoun" );
		tagset.put( "RB", "Adverb" );
		tagset.put( "VB", "Verb, base form" );
		tagset.put( "VBD", "Verb, past tense" );
		tagset.put( "VBG", "Verb, gerund or present participle" );
		tagset.put( "VBN", "Verb, past participle");
		tagset.put( "VBP", "Verb, non-3rd person singular present");
		tagset.put( "VBZ", "Verb, 3rd person singular present");
	}
	
}
