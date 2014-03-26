package ai.ku.nlp;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import ai.ku.main.Config;
import ai.ku.resource.SynsetRepository;
import ai.ku.util.Logger;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.item.Synset;
import edu.mit.jwi.item.SynsetID;
import edu.mit.jwi.morph.WordnetStemmer;

public class WordNetFinder {
	
	private IDictionary dict = null;
	private WordnetStemmer stemmer = null;
	private static WordNetFinder wnfinder = null;
	// private Hashtable<String,IIndexWord> nouns = null;
	private HashSet<String> nouns = null;

	// WordNet 3.0 Path
	// public static final String WORDNET_PATH = "/Users/emre/Desktop/WordNet-3.0/dict/";
	
	// Singleton
	public static WordNetFinder getInstance(){
		if( wnfinder == null )
			wnfinder = new WordNetFinder();
		return wnfinder;
	}

	public static void init(){
		WordNetFinder.getInstance();
	}

	public IDictionary getDictionary() 
	{ return dict; }

	private WordNetFinder(){
		System.setProperty("wordnet.database.dir", Config.WORDNET_PATH);
		URL url = null;
		try 
		{ url = new URL( "file", null, System.getProperty("wordnet.database.dir") ); } 
		catch (MalformedURLException e) 
		{ e.printStackTrace(); } 

		dict = new Dictionary(url); 
		try { dict.open(); } 
		catch (IOException e) { e.printStackTrace(); }

		stemmer = new WordnetStemmer(dict);

		// nouns = new Hashtable<String,IIndexWord>();
		nouns = new HashSet<String>();
		Iterator<IIndexWord> list = dict.getIndexWordIterator( POS.NOUN );

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("nouns.txt")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int count = 1;
		for( Iterator<?> iter = list; iter.hasNext(); )
		{
			IIndexWord iword = (IIndexWord) iter.next();

			HashSet<String> sids = getAvailableSynsetIDs(iword);
			ArrayList<String> availables = SynsetRepository.getInstance().getAvailableModels(sids);

			String result = count+" "+iword.getLemma()+" "+sids.size()+" "+availables.size();
			try { writer.append( result+"\n" ); } 
			catch (IOException e) { e.printStackTrace(); }

			// Logger.log(result);
			String word = iword.getLemma();
			count++;

			if( word.contains("_") ) {
				word = word.replace("_", " ");
				nouns.add(word);
			}
		}
		Logger.log("Noun size: "+nouns.size());

		try { writer.close(); } 
		catch (IOException e) { e.printStackTrace(); }

		/*
		for ( String word : nouns ) {
			HashSet<String> sids = getAvailableSynsetIDs(word,"NN");
			Logger.log(count+"- "+word+" : "+sids.size());

			if( count > 100 )
				break;
		}
		 */

	}

	private SynsetID toSynsetID(String id) {
		return SynsetID.parseSynsetID(id);
	}
	
	public boolean hasNounPhrase(String phrase){
		return nouns.contains(phrase);
	}

	public ArrayList<String> getNounPhrases(String sentence){
		ArrayList<String> list = new ArrayList<String>();
		for( Iterator<String> iter = nouns.iterator(); iter.hasNext(); )
		{
			String phrase = iter.next();
			if( sentence.contains( phrase ) )
				list.add( phrase );
		}
		return list;
	}

	// Print relations
	// find hyponyms
	public String printRelations( String searchWord, String tag, Pointer relationType ){

		POS pos = this.getWNPOS(tag);
		IIndexWord idxWord = dict.getIndexWord( searchWord, pos ); // get the synset
		int senseCount = idxWord.getWordIDs().size();
		for( int i = 0; i < senseCount; i++ )
		{
			IWordID wordID = idxWord.getWordIDs().get(i); // 1st meaning 
			IWord word = dict.getWord(wordID);
			ISynset synset = word.getSynset();
			// get the hypernyms
			List<ISynsetID> hypernyms = synset.getRelatedSynsets( relationType );
			// print out each hypernyms id and synonyms
			List<IWord> words; 

			for( ISynsetID sid : hypernyms ){
				words = dict.getSynset(sid).getWords(); 
				System.out.print(sid + " {");
				for( Iterator<IWord> iter = words.iterator(); iter.hasNext(); ){
					System.out.print( iter.next().getLemma() ); 
					if( iter.hasNext() )
						System.out.print(", "); 
				}
				System.out.println("}");
			}
			System.out.println("---------------");
		}
		return null;
	}

	public int getWordCountInSynset(String id){
		SynsetID sid = SynsetID.parseSynsetID(id);
		Synset synset = (Synset) dict.getSynset(sid);
		List<IWord> words = synset.getWords();
		return words.size();
	}

	public ArrayList<String> getWordsForSynset(String id){
		SynsetID sid = SynsetID.parseSynsetID(id);
		Synset synset = (Synset) dict.getSynset(sid);
		List<IWord> iwords = synset.getWords();
		ArrayList<String> words = new ArrayList<String>();
		for( int i = 0; i < iwords.size(); i++ )
		{
			String word = iwords.get(i).getLemma();
			words.add( word );
			// System.out.println( word );
			System.out.println( id+" = "+word+": "+synset.getGloss() );
		}
		return words;
	}

	public List<ISynsetID> getHypernyms(ISynsetID synsetID){
		Synset synset = (Synset) dict.getSynset( synsetID );
		List<ISynsetID> hypernyms = synset.getRelatedSynsets( Pointer.HYPERNYM );
		return hypernyms;
	}

	public List<ISynsetID> getHyponyms(ISynsetID synsetID){
		Synset synset = (Synset) dict.getSynset( synsetID );
		List<ISynsetID> hyponyms = synset.getRelatedSynsets( Pointer.HYPONYM );
		return hyponyms;
	}

	private void addHyponyms( ISynsetID synsetID, HashSet<ISynsetID> sids ){
		Synset synset = (Synset) dict.getSynset( synsetID );
		List<ISynsetID> hyponyms = synset.getRelatedSynsets( Pointer.HYPONYM );
		for( int i = 0; i < hyponyms.size(); i++ )
		{
			ISynsetID currentSID = hyponyms.get(i);
			sids.add( currentSID );
			addHyponyms( currentSID, sids );
		}
	}

	public HashSet<String> getAvailableSynsetIDs(IIndexWord iword){
		HashSet<ISynsetID> asids = new HashSet<ISynsetID>();
		List<IWordID> list = iword.getWordIDs();
		for( int i = 0; i < list.size(); i++ )
		{
			IWordID wordID = iword.getWordIDs().get(i);
			IWord word = dict.getWord( wordID );
			ISynset synset = word.getSynset();
			SynsetID sid = (SynsetID) synset.getID();
			asids.add(sid);
			addHyponyms(sid,asids);
		}

		HashSet<String> sids = new HashSet<String>();
		for( Iterator<ISynsetID> iter = asids.iterator(); iter.hasNext(); )
		{
			String currentSID = iter.next().toString();
			sids.add( currentSID ); 
		}

		return sids;
	}

	public String stem(String word, String tag){
		POS pos = this.getWNPOS(tag);
		word = word.toLowerCase();
		String stem = word;
		if( pos != null ) {
			List<String> stems = stemmer.findStems(word, pos);
			stem = ( stems.size() > 0 ) ? stems.get(0) : word;
		}
		else {
			Logger.log("POS null! Word:"+word+" Tag:"+tag);
		}
		return stem;
	}

	public boolean includes( String searchWord, String tag ){
		String stem = this.stem(searchWord, tag);
		IIndexWord idxWord = dict.getIndexWord( stem, this.getWNPOS(tag) );
		return (idxWord != null);
	}

	public HashSet<String> getAvailableSynsetIDs(String searchWord, String tag){
		POS pos = this.getWNPOS(tag);
		String stem = this.stem(searchWord, tag);
		// List<String> stems = stemmer.findStems(searchWord, pos);
		// String stem = ( stems.size() > 0 ) ? stems.get(0) : searchWord;
		HashSet<ISynsetID> asids = new HashSet<ISynsetID>();
		IIndexWord idxWord = dict.getIndexWord( stem, pos );
		List<IWordID> list = idxWord.getWordIDs();
		int wordSensesCount = list.size();
		for( int i = 0; i < wordSensesCount; i++ )
		{
			IWordID wordID = idxWord.getWordIDs().get(i);
			IWord word = dict.getWord( wordID );
			ISynset synset = word.getSynset();
			SynsetID sid = (SynsetID) synset.getID();
			asids.add(sid);
			addHyponyms(sid,asids);
		}
		/*
		// ArrayList<ISynsetID> asids = new ArrayList<ISynsetID>();
		for( int i = 0; i < asids.size(); i++ )
		{
			ISynsetID currentSID = asids.get(i);
			// System.out.println( currentSID.toString() );
			addHyponyms( currentSID, asids );
		}
		 */
		HashSet<String> sids = new HashSet<String>();
		for( Iterator<ISynsetID> iter = asids.iterator(); iter.hasNext(); )
		{
			String currentSID = iter.next().toString();
			sids.add( currentSID );
			// System.out.println( iter.next() ); 
		}
		/*
		for( int i = 0; i < asids.size(); i++ )
		{
			String currentSID = asids.get(i).toString();
			sids.add( currentSID );
		}
		 */
		return sids;
	}

	// Added by emre
	// 23.Agu.2012
	// WordNet Search with options
	public HashSet<String> getAvailableSynsetIDs(String searchWord, String tag, String option)
	{
		POS pos = this.getWNPOS(tag);
		String stem = this.stem(searchWord, tag);

		HashSet<ISynsetID> asids = new HashSet<ISynsetID>();
		IIndexWord idxWord = dict.getIndexWord( stem, pos );
		List<IWordID> list = idxWord.getWordIDs();
		int wordSensesCount = list.size();
		for( int i = 0; i < wordSensesCount; i++ )
		{
			IWordID wordID = idxWord.getWordIDs().get(i);
			IWord word = dict.getWord( wordID );
			ISynset synset = word.getSynset();
			SynsetID sid = (SynsetID) synset.getID();
			List<ISynsetID> hypernyms = this.getHypernyms(sid);
			for( ISynsetID hypernymID : hypernyms )
			{
				asids.add(hypernymID);
				addHyponyms(hypernymID,asids);
			}
		}
		HashSet<String> sids = new HashSet<String>();
		for( Iterator<ISynsetID> iter = asids.iterator(); iter.hasNext(); )
		{
			String currentSID = iter.next().toString();
			sids.add( currentSID );
		}
		return sids;
	}

	public HashSet<String> getCousins(List<String> ids, String option)
	{
		HashSet<ISynsetID> asids = new HashSet<ISynsetID>();
		for( String id : ids ) {
			List<ISynsetID> hypernyms = getHypernyms(toSynsetID(id));
			for( ISynsetID hypernymID : hypernyms )
			{
				asids.add(hypernymID);
				addHyponyms(hypernymID,asids);
			}
		}
		HashSet<String> sids = new HashSet<String>();
		for( Iterator<ISynsetID> iter = asids.iterator(); iter.hasNext(); )
		{
			String currentSID = iter.next().toString();
			sids.add( currentSID );
		}
		return sids;
	}

	public String getSynsetID( String searchWord, String tag ){
		POS pos = this.getWNPOS(tag);
		String stem = this.stem(searchWord, tag);
		IIndexWord idxWord = dict.getIndexWord( stem, pos );
		IWordID wordID = idxWord.getWordIDs().get(0);
		return wordID.getSynsetID().toString();
	}

	public ArrayList<String> getSynsetIDs( String searchWord, String tag ){

		ArrayList<String> sids = new ArrayList<String>();

		POS pos = this.getWNPOS(tag);
		List<String> stems = stemmer.findStems(searchWord, pos);
		String stem = null;
		if( stems.size() > 0 )
			stem = stems.get(0); 
		else
			stem = searchWord;

		IIndexWord idxWord = dict.getIndexWord( stem, pos );
		List<IWordID> list = idxWord.getWordIDs();
		int wordSensesCount = list.size();
		System.out.printf("The word \"%s\" has %d sense(s).\n",stem,wordSensesCount);

		for( int i = 0; i < wordSensesCount; i++ )
		{
			IWordID wordID = idxWord.getWordIDs().get(i);
			IWord word = dict.getWord( wordID );
			ISynset synset = word.getSynset();
			SynsetID sid = (SynsetID) synset.getID();
			String id = sid.toString();
			// int synsetSize = this.getWordCountInSynset(id);
			// System.out.printf("The synset ( %s ) has %d word(s).\n", id, synsetSize);
			// System.out.printf("Gloss: %s.\n",synset.getGloss());
			this.getWordsForSynset(id);
			sids.add(id);
		}

		return sids;
	}

	public String getSynsetProperty(String id) {
		SynsetID sid = SynsetID.parseSynsetID(id);
		Synset synset = (Synset) dict.getSynset(sid);
		// IWord word = synset.getWords().get(0);
		// ystem.out.println(word.getAdjectiveMarker().name());
		// System.out.println(word.getLemma());
		// ISenseEntry ise = dict.getSenseEntry(synset.getWords().get(0).getSenseKey());
		List<ISynsetID> attributes = synset.getRelatedSynsets( Pointer.ATTRIBUTE );
		for (ISynsetID synid : attributes) {
			getWordsForSynset( synid.toString() );
		}
		// String result = synset.getWords().get(0).
		// System.out.println(result);
		// System.out.println(ise.toString());

		String result = ( attributes.size() > 0 ) ? attributes.get(0).toString() : null;
		return result;
	}

	public String printWordSenses( String searchWord, POS pos ) {

		StringBuffer buffer = new StringBuffer();
		String stem = null;
		List<String> stems = null;

		stems = stemmer.findStems( searchWord, pos );
		stem = stems.get(0);

		String line = String.format(" Lemma = %s\n", searchWord);
		buffer.append(line);
		line = String.format(" \"%s\" has %d stem(s).\n", searchWord, stems.size());
		buffer.append(line);
		line = String.format(" Stem = %s\n", stem);
		buffer.append(line);

		// System.out.printf("\nLemma = %s\n", searchWord);
		// System.out.printf("\"%s\" has %d stem(s).\n", searchWord, stems.size() );
		// System.out.printf("Stem = %s\n", stem);

		/*System.out.printf("POS = %s\n", getPOSString(pos));*/

		IIndexWord idxWord = dict.getIndexWord( stem, pos ); 

		List<IWordID> list = idxWord.getWordIDs();
		int wordSensesCount = list.size();

		line = String.format(" The word \"%s\" has %d sense(s).\n",stem,wordSensesCount);
		buffer.append(line);
		// System.out.printf("The word \"%s\" has %d sense(s).\n",stem,wordSensesCount);

		for( int i = 0; i < wordSensesCount; i++ )
		{
			// look up first sense of the word "dog"
			IWordID wordID = idxWord.getWordIDs().get(i);
			IWord word = dict.getWord( wordID );

			ISynset synset = word.getSynset();
			// get the hypernyms
			List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
			List<IWord> words; 

			if( hypernyms.size() > 0  )
				buffer.append(" Hypernyms: ");
			for(ISynsetID sid : hypernyms)
			{
				words = dict.getSynset(sid).getWords(); 
				// System.out.print(sid + " {");
				// buffer.append("{ ");
				for(Iterator<IWord> j = words.iterator(); j.hasNext();){
					// System.out.print(j.next().getLemma()); 
					buffer.append( j.next().getLemma() );
					if( j.hasNext() )
						buffer.append(" ");
					// System.out.print(", "); 
					// System.out.println("}");
				}
				buffer.append(" ");
			}
			if( hypernyms.size() > 0  )
				buffer.append("\n");

			// System.out.println("Lemma = " + word.getLemma()); 
			// System.out.println("Id = " + wordID); 
			// System.out.printf("Gloss(%d) = %s\n",(i+1),word.getSynset().getGloss());
			line = String.format(" Gloss(%d) (%s) = %s\n",(i+1), word.getSynset().getID().toString(),word.getSynset().getGloss());
			buffer.append(line);
		}

		buffer.append("\n");

		return buffer.toString();
	}

	private POS getWNPOS(String pos){
		if( pos.contains("JJ") )
			return POS.ADJECTIVE;

		else if( pos.contains("RB") )
			return POS.ADVERB;

		else if( pos.contains("NN") )
			return POS.NOUN;

		else if( pos.contains("VB") )
			return POS.VERB;

		return null;
	}
}
